package ro.alex.licenta.database;

import com.azure.cosmos.models.SqlParameter;
import com.azure.cosmos.models.SqlQuerySpec;
import org.json.simple.JSONObject;
import ro.alex.licenta.models.JSON;
import ro.alex.licenta.models.Player;
import ro.alex.licenta.models.Quest;
import ro.alex.licenta.util.Config;

import java.util.*;

public class QuestDB implements QuestDBI{
    private final String container = Config.questsContainer;
    private final String playersContainer = Config.playersContainer;


    @Override
    public boolean createQuest(Quest quest) throws Exception {
        if(quest.getTokens() < 0) throw new Exception("You can't create a task with negative tokens");


        String sqlQuery = "SELECT * FROM c WHERE c.name = @name";
        var paramList = new ArrayList<SqlParameter>();
        paramList.add(new SqlParameter("@name", quest.getName()));
        var querySpec = new SqlQuerySpec(sqlQuery, paramList);
        JSONObject resName = DataAccess.findObject(container, querySpec);

        if(resName != null) throw new Exception("A task with this name has already been created");


        var responsePlayer = DataAccess.findObject(playersContainer, queryPlayerById(quest.getCreatedBy()));

        if(responsePlayer == null) throw new Exception("The player doesn't exist");

        var playerCreated = JSON.deserializeJSONPlayer(responsePlayer);

        if(playerCreated.getTokens() < quest.getTokens()) throw new Exception("You do not have enough tokens to create this task");

        playerCreated.updateTokens(-quest.getTokens());


        JSONObject json = quest.createJSON();
        DataAccess.createObject(container, json);


        playerCreated.addQuestsCreatedId(quest.getId());
        DataAccess.replaceObject(playersContainer, playerCreated.createJSON());

        return true;
    }


    @Override
    public List<JSONObject> getIncompleteQuests(String playerId, int amount) throws Exception {
        String sqlQuery = "SELECT * FROM c WHERE c.createdBy_id != @playerId AND c.takenBy_id = @takenId";
        var paramList = new ArrayList<SqlParameter>();
        paramList.add(new SqlParameter("@playerId", playerId));
        paramList.add(new SqlParameter("@takenId", null));
        var querySpec = new SqlQuerySpec(sqlQuery, paramList);
        var response = DataAccess.findListObjects(container, querySpec);

        if(response == null) return null;

        int size = response.size();


        if(size <= amount){
            return response;
        }


        Set<Integer> randomSet = new HashSet<>();
        Random random = new Random();
        while(randomSet.size() < amount || randomSet.size() == size)
            randomSet.add(random.nextInt(size));

        List<JSONObject> listDisplay = new ArrayList<>();
        for(var pos : randomSet)
            listDisplay.add(response.get(pos));

        return listDisplay;
    }


    @Override
    public List<JSONObject> getCreatedQuests(String playerId) throws Exception {
        String sqlQuery = "SELECT * FROM c WHERE c.createdBy_id = @playerId";
        var paramList = new ArrayList<SqlParameter>();
        paramList.add(new SqlParameter("@playerId", playerId));
        var querySpec = new SqlQuerySpec(sqlQuery, paramList);

        return DataAccess.findListObjects(container, querySpec);
    }


    @Override
    public List<JSONObject> getAcceptedQuests(String playerId) throws Exception {
        String sqlQuery = "SELECT * FROM c WHERE c.takenBy_id = @playerId";
        var paramList = new ArrayList<SqlParameter>();
        paramList.add(new SqlParameter("@playerId", playerId));
        var querySpec = new SqlQuerySpec(sqlQuery, paramList);

        return DataAccess.findListObjects(container, querySpec);
    }


    @Override
    public boolean deleteQuest(String questId) throws Exception {
        var resultQuest = DataAccess.findObject(container, queryQuestById(questId));
        if(resultQuest == null) throw new Exception("Task is not in the db");
        var playerIdCreated = (String) resultQuest.get("createdBy_id");
        var playerIdTaken = (String) resultQuest.get("takenBy_id");


        var resultPlayerCreated = DataAccess.findObject(playersContainer, queryPlayerById(playerIdCreated));
        var resultPlayerTaken = DataAccess.findObject(playersContainer, queryPlayerById(playerIdTaken));

        if(resultPlayerCreated != null) {
            var playerCreated = JSON.deserializeJSONPlayer(resultPlayerCreated);
            playerCreated.removeQuestsCreated(questId);
            var updateres1=  DataAccess.replaceObject(playersContainer, playerCreated.createJSON());
            if(updateres1 >= 300) throw new Exception("There was a problem with the db");
        }
        if(resultPlayerTaken != null) {
            var playerTaken = JSON.deserializeJSONPlayer(resultPlayerTaken);

            playerTaken.removeQuestsAccepted(questId);

            var updateres2 = DataAccess.replaceObject(playersContainer, playerTaken.createJSON());
        }


        int res = DataAccess.deleteObject(container, questId);
        if(res >= 300) throw new Exception("Db error, Status code: " + res);

        return true;
    }


    @Override
    public boolean takeQuest(String playerId, String questId) throws Exception {
        var responseQuest = DataAccess.findObject(container, queryQuestById(questId));

        if(responseQuest == null) throw new Exception("The task doesn't exist");
        if(responseQuest.get("takenBy_id") != null) throw new Exception("This task is already taken");


        var quest = JSON.deserializeJSONQuest(responseQuest);
        quest.setTakenBy_id(playerId);
        var status = DataAccess.replaceObject(container, quest.createJSON());


        var responsePlayer = DataAccess.findObject(playersContainer, queryPlayerById(playerId));

        if(responsePlayer == null) throw new Exception("The player doesn't exist");

        var player = JSON.deserializeJSONPlayer(responsePlayer);
        player.addQuestsAcceptedId(questId);
        DataAccess.replaceObject(playersContainer, player.createJSON());

        if(status >= 300) throw new Exception("Something went wrong in the db");
        return true;
    }


    @Override
    public boolean completeQuest(String playerId, String questId) throws Exception {
        var resultQuest = DataAccess.findObject(container, queryQuestById(questId));

        if(resultQuest == null) throw new Exception("Task is not in the db");
        var quest = JSON.deserializeJSONQuest(resultQuest);


        if(quest.getCreatedBy_id().equals(playerId))
            quest.setAcceptedCreator(true);


        if(quest.getTakenBy_id().equals(playerId))
            quest.setCompletedTaker(true);

        var resultUpdate = DataAccess.replaceObject(container, quest.createJSON());

        if(resultUpdate >= 300) throw new Exception("There was a problem with the db");


        if(quest.isCompletedTaker() && quest.isAcceptedCreator()){
            updatePlayers(quest);


            deleteQuest(quest.getId());
        }

        return true;
    }


    @Override
    public boolean cancelQuest(String playerId, String questId) throws Exception {
        var resultQuest = DataAccess.findObject(container, queryQuestById(questId));

        if(resultQuest == null) throw new Exception("The task is not in the db");

        var quest = JSON.deserializeJSONQuest(resultQuest);


        if(quest.getTakenBy_id() != null) throw new Exception("This task is already taken by someone. You can't cancel it anymore.");


        if(!quest.getCreatedBy_id().equals(playerId)) throw new Exception("You are not the creator of the task");


        var resultCreator = DataAccess.findObject(playersContainer, queryPlayerById(playerId));

        if(resultCreator == null) throw new Exception("Player not found in the db");

        var creator = JSON.deserializeJSONPlayer(resultCreator);
        creator.updateTokens(quest.getTokens());

        DataAccess.replaceObject(playersContainer, creator.createJSON());

        return deleteQuest(questId);
    }

    private SqlQuerySpec queryQuestById(String questId){
        String sqlQuery = "SELECT * FROM c WHERE c.id = @questId";
        var paramList = new ArrayList<SqlParameter>();
        paramList.add(new SqlParameter("@questId", questId));
        return new SqlQuerySpec(sqlQuery, paramList);
    }

    private SqlQuerySpec queryPlayerById(String playerId){
        String sqlQuery = "SELECT * FROM c WHERE c.id = @playerId";
        var paramList = new ArrayList<SqlParameter>();
        paramList.add(new SqlParameter("@playerId", playerId));
        return new SqlQuerySpec(sqlQuery, paramList);
    }

    private void updatePlayers(Quest quest) throws Exception {


        var resultTaker = DataAccess.findObject(playersContainer, queryPlayerById(quest.getTakenBy_id()));
        var resultCreator = DataAccess.findObject(playersContainer, queryPlayerById(quest.getCreatedBy()));

        if(resultTaker == null) throw new Exception("Taker of the task not found in the db");
        if(resultCreator == null) throw new Exception("Creator of the task not found in the db");

        var taker = JSON.deserializeJSONPlayer(resultTaker);
        var creator = JSON.deserializeJSONPlayer(resultCreator);


        taker.updateTokens(quest.getTokens());
        taker.updateRank(quest.getTokens() * 10);
        taker.addQuestsTakenCompleted();

        creator.updateRank(quest.getTokens() * 5);
        creator.addQuestsCreatedCompleted();

        updateBadgesCreator(creator);
        updateBadgesTaker(taker);


        var updateTaker = DataAccess.replaceObject(playersContainer, taker.createJSON());
        var updateCreator = DataAccess.replaceObject(playersContainer, creator.createJSON());

        if(updateTaker >= 300) throw new Exception("Something went wrong with the player db");
        if(updateCreator >= 300) throw new Exception("Something went wrong with the player db");
    }

    private Player updateBadgesCreator(Player creator) {
        String fileName = "badge_questCreatedCompleted_";
        if(creator.getQuestsCreatedCompleted() == 1) creator.addBadge(fileName + "1.png");
        if(creator.getQuestsCreatedCompleted() == 5) creator.addBadge(fileName + "2.png");
        if(creator.getQuestsCreatedCompleted() == 10) creator.addBadge(fileName + "3.png");
        if(creator.getQuestsCreatedCompleted() == 15) creator.addBadge(fileName + "4.png");
        if(creator.getQuestsCreatedCompleted() == 20) creator.addBadge(fileName + "5.png");

        return creator;
    }

    private Player updateBadgesTaker(Player taker) {
        String fileName = "badge_questsTakenCompleted_";
        if (taker.getQuestsTakenCompleted() == 1) taker.addBadge(fileName + "1.png");
        if (taker.getQuestsTakenCompleted() == 5) taker.addBadge(fileName + "2.png");
        if (taker.getQuestsTakenCompleted() == 10) taker.addBadge(fileName + "3.png");
        if (taker.getQuestsTakenCompleted() == 15) taker.addBadge(fileName + "4.png");
        if (taker.getQuestsTakenCompleted() == 20) taker.addBadge(fileName + "5.png");

        return taker;
    }
}

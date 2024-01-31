package ro.alex.licenta.database;

import org.json.simple.JSONObject;
import ro.alex.licenta.models.Quest;

import java.util.List;

public interface QuestDBI {

    boolean createQuest(Quest quest) throws Exception;


    List<JSONObject> getIncompleteQuests(String name, int amount) throws Exception;


    List<JSONObject> getCreatedQuests(String playerId) throws Exception;


    List<JSONObject> getAcceptedQuests(String playerId) throws Exception;


    boolean deleteQuest(String questId) throws Exception;


    boolean takeQuest(String playerId, String questId) throws Exception;


    boolean completeQuest(String playerId, String questId) throws Exception;


    boolean cancelQuest(String playerId, String questId) throws Exception;
}

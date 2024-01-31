package ro.alex.licenta.models;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Player {
    private String username;
    private final String email;
    private String password;
    private Integer tokens;
    private List<String> badges_id;
    private Integer rank;
    private final String id;
    private final List<String> questsCreated_id;
    private final List<String> questsAccepted_id;
    private Integer questsTakenCompleted;
    private Integer questsCreatedCompleted;
    private Integer xp;

    public Player(String username, String email, String password) {
        this.id = UUID.randomUUID().toString();

        this.username = username;
        this.email = email;
        this.password = password;

        this.tokens = 200;
        this.rank = 1;
        this.badges_id = new ArrayList<>();

        this.questsCreated_id = new ArrayList<>();
        this.questsAccepted_id = new ArrayList<>();

        this.questsTakenCompleted = 0;
        this.questsCreatedCompleted = 0;

        this.xp = 0;
    }

    public Player(String username, String email, String password, Integer tokens, List<String> badges_id, Integer rank, String id, List<String> questsCreated_id, List<String> questsAccepted_id, Integer questsTakenCompleted, Integer questsCreatedCompleted, Integer xp) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.tokens = tokens;
        this.badges_id = badges_id;
        this.rank = rank;
        this.id = id;
        this.questsCreated_id = questsCreated_id;
        this.questsAccepted_id = questsAccepted_id;
        this.questsTakenCompleted = questsTakenCompleted;
        this.questsCreatedCompleted = questsCreatedCompleted;
        this.xp = xp;
    }


    public JSONObject createJSON() {
        JSONObject json = new JSONObject();

        json.put("id", id);

        json.put("email", email);
        json.put("username", username);
        json.put("password", password);

        json.put("tokens", tokens);
        json.put("rank", rank);
        JSONArray badgesJSON = new JSONArray();
        if(badges_id != null) badgesJSON.addAll(badges_id);
        json.put("badges_id", badgesJSON);

        JSONArray questsCreatedJSON = new JSONArray();
        if(questsCreated_id != null) questsCreatedJSON.addAll(questsCreated_id);
        json.put("questsCreated_id", questsCreatedJSON);
        JSONArray questsAcceptedJSON = new JSONArray();
        if(questsAccepted_id != null) questsAcceptedJSON.addAll(questsAccepted_id);
        json.put("questsAccepted_id", questsAcceptedJSON);

        json.put("questsTakenCompleted", questsTakenCompleted);
        json.put("questsCreatedCompleted", questsCreatedCompleted);

        json.put("xp", xp);

        return json;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getId() {
        return id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void addQuestsAcceptedId(String questId) {
        this.questsAccepted_id.add(questId);
    }

    public void addQuestsCreatedId(String questId) {
        this.questsCreated_id.add(questId);
    }

    public List<String> getQuestsCreated_id() {
        return questsCreated_id;
    }

    public List<String> getQuestsAccepted_id() {
        return questsAccepted_id;
    }

    public boolean removeQuestsAccepted(String questId) {
        return questsAccepted_id.removeIf(quest -> quest.equals(questId));
    }

    public boolean removeQuestsCreated(String questId) {
        return questsCreated_id.removeIf(quest -> quest.equals(questId));
    }

    public void updateTokens(int tokens){
        this.tokens += tokens;
    }

    public Integer getTokens() {
        return tokens;
    }

    public Integer getQuestsTakenCompleted() {
        return questsTakenCompleted;
    }

    public Integer getQuestsCreatedCompleted() {
        return questsCreatedCompleted;
    }

    public void addQuestsTakenCompleted() {
        this.questsTakenCompleted++;
    }

    public void addQuestsCreatedCompleted() {
        this.questsCreatedCompleted++;
    }

    public void updateRank(int xp){
        this.xp += xp;

        while(this.xp >= 50){
            this.rank++;
            this.xp -= 50;
        }
    }

    public int getRank() {
        return this.rank;
    }

    public void addBadge(String badge_id) {
        if(this.badges_id == null) this.badges_id = new ArrayList<>();
        this.badges_id.add(badge_id);
    }

    public List<String> getBadges(){
        return this.badges_id;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

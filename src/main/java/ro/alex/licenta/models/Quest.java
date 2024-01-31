package ro.alex.licenta.models;

import org.json.simple.JSONObject;

import java.util.UUID;

public class Quest {
    private final String name;
    private final String description;
    private final String createdBy_id;
    private String takenBy_id;
    private boolean completedTaker;
    private boolean acceptedCreator;
    private final String id;
    private final int tokens;

    public Quest(String name, String description, String createdBy_id, int tokens){
        this.id = UUID.randomUUID().toString();

        this.name = name;
        this.description = description;
        this.createdBy_id = createdBy_id;
        this.tokens = tokens;

        this.takenBy_id = null;

        this.acceptedCreator = false;
        this.completedTaker = false;
    }

    public Quest(String name, String description, String createdBy_id, String takenBy_id, boolean completedTaker, boolean acceptedCreator, String id, int tokens) {
        this.name = name;
        this.description = description;
        this.createdBy_id = createdBy_id;
        this.takenBy_id = takenBy_id;
        this.completedTaker = completedTaker;
        this.acceptedCreator = acceptedCreator;
        this.id = id;
        this.tokens = tokens;
    }

    public JSONObject createJSON() {
        JSONObject json = new JSONObject();

        json.put("id", id);

        json.put("name", name);
        json.put("description", description);
        json.put("createdBy_id", createdBy_id);
        json.put("tokens", tokens);

        json.put("takenBy_id", takenBy_id);

        json.put("acceptedCreator", acceptedCreator);
        json.put("completedTaker", completedTaker);

        return json;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCreatedBy() {
        return createdBy_id;
    }

    public String getId() {
        return id;
    }

    public void setTakenBy_id(String takenBy_id){
        this.takenBy_id = takenBy_id;
    }

    public String getCreatedBy_id() {
        return createdBy_id;
    }

    public String getTakenBy_id() {
        return takenBy_id;
    }

    public boolean isCompletedTaker() {
        return completedTaker;
    }

    public boolean isAcceptedCreator() {
        return acceptedCreator;
    }

    public void setCompletedTaker(boolean completedTaker) {
        this.completedTaker = completedTaker;
    }

    public void setAcceptedCreator(boolean acceptedCreator) {
        this.acceptedCreator = acceptedCreator;
    }

    public int getTokens() {
        return tokens;
    }
}

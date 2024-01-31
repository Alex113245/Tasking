package ro.alex.licenta.models;

import org.json.simple.JSONObject;

import java.util.List;

public class JSON {
    public static Quest deserializeJSONQuest(JSONObject questJSON) {
        return new Quest((String) questJSON.get("name"),
                (String) questJSON.get("description"),
                (String) questJSON.get("createdBy_id"),
                (String) questJSON.get("takenBy_id"),
                (boolean) questJSON.get("completedTaker"),
                (boolean) questJSON.get("acceptedCreator"),
                (String) questJSON.get("id"),
                (int) questJSON.get("tokens"));
    }

    public static Player deserializeJSONPlayer(JSONObject playerJSON) {
        return new Player((String) playerJSON.get("username"),
                          (String) playerJSON.get("email"),
                          (String) playerJSON.get("password"),
                          (int) playerJSON.get("tokens"),
                          (List<String>) playerJSON.get("badges_id"),
                          (int) playerJSON.get("rank"),
                          (String) playerJSON.get("id"),
                          (List<String>) playerJSON.get("questsCreated_id"),
                          (List<String>) playerJSON.get("questsAccepted_id"),
                          (int) playerJSON.get("questsTakenCompleted"),
                          (int) playerJSON.get("questsCreatedCompleted"),
                          (int) playerJSON.get("xp"));
    }
}

package ro.alex.licenta.database;

import org.json.simple.JSONObject;
import ro.alex.licenta.models.Player;

import java.util.List;

public interface PlayerDBI {

    boolean register(Player player) throws Exception;


    boolean login(String email, String password) throws Exception;


    JSONObject findPlayerById(String id) throws Exception;


    boolean deleteById(String id) throws Exception;


    boolean replace(Player player) throws Exception;



    List<JSONObject> findTop(int amount) throws Exception;
}

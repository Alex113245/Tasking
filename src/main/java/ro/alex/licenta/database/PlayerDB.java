package ro.alex.licenta.database;

import com.azure.cosmos.models.SqlParameter;
import com.azure.cosmos.models.SqlQuerySpec;
import org.json.simple.JSONObject;
import ro.alex.licenta.App;
import ro.alex.licenta.util.Config;
import ro.alex.licenta.util.Security;
import ro.alex.licenta.models.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PlayerDB implements PlayerDBI {
    private final String container = Config.playersContainer;


    @Override
    public boolean register(Player player) throws Exception{
        if(!validEmail(player.getEmail())) throw new Exception("Invalid email");
        if(!validPassword(player.getPassword())) throw new Exception("Invalid password 4 to 8 character both lower " +
                "and upper case and number required.");


        JSONObject resEmail = DataAccess.findObject(container, queryOnEmail(player.getEmail()));
        if(resEmail != null) throw new Exception("Email already in use");


        JSONObject resUsername = DataAccess.findObject(container, queryOnUsername(player.getUsername()));
        if(resUsername != null) throw new Exception("Username already in use");


        var hashedPassword = Security.generateStorngPasswordHash(player.getPassword());
        player.setPassword(hashedPassword);

        JSONObject json = player.createJSON();
        DataAccess.createObject(container, json);


        if(App.getInstance() != null) App.getInstance().setMyPlayer(json);
        return true;
    }


    @Override
    public boolean login(String email, String password) throws Exception {
        JSONObject res = DataAccess.findObject(container, queryOnEmail(email));


        if(res == null) throw new Exception("Email not found in the db");


        if(!Security.validatePassword(password, (String) res.get("password"))) throw new Exception("Incorrect credentials");


        if(App.getInstance() != null) App.getInstance().setMyPlayer(res);
        return true;
    }


    @Override
    public JSONObject findPlayerById(String id) throws Exception {
        JSONObject res = DataAccess.findObject(container, queryOnId(id));

        if(res == null) throw new Exception("Id not found in the db");

        return res;
    }


    @Override
    public boolean deleteById(String id) throws Exception {
        int res = DataAccess.deleteObject(container, id);
        if(res >= 300) throw new Exception("Db error, Status code: " + res);
        return true;
    }


    @Override
    public boolean replace(Player player) throws Exception {
        int res = DataAccess.replaceObject(container, player.createJSON());
        if(res >= 300) throw new Exception("Db error, Status code: " + res);
        return true;
    }


    @Override
    public List<JSONObject> findTop(int amount) throws Exception {
        String sqlQuery = "SELECT TOP @amount * FROM c ORDER BY c.rank DESC";
        var paramList = new ArrayList<SqlParameter>();
        paramList.add(new SqlParameter("@amount", amount));
        SqlQuerySpec sqlQuerySpec = new SqlQuerySpec(sqlQuery, paramList);

        var response = DataAccess.findListObjects(container, sqlQuerySpec);

        if(response == null) throw new Exception("Something went wrong with the db");

        return response;
    }


    private SqlQuerySpec queryOnEmail(String email){
        String sqlQuery = "SELECT * FROM c WHERE c.email = @email";
        var paramList = new ArrayList<SqlParameter>();
        paramList.add(new SqlParameter("@email", email));
        return new SqlQuerySpec(sqlQuery, paramList);
    }


    private SqlQuerySpec queryOnUsername(String username) {
        String sqlQuery = "SELECT * FROM c WHERE c.username = @username";
        var paramList = new ArrayList<SqlParameter>();
        paramList.add(new SqlParameter("@username", username));
        return new SqlQuerySpec(sqlQuery, paramList);
    }


    private SqlQuerySpec queryOnId(String id) {
        String sqlQuery = "SELECT * FROM c WHERE c.id = @id";
        var paramList = new ArrayList<SqlParameter>();
        paramList.add(new SqlParameter("@id", id));
        return new SqlQuerySpec(sqlQuery, paramList);
    }


    private boolean validEmail(String email){
        String regexPattern = "^[a-zA-Z\\d_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z\\d-]+\\.)+[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(regexPattern);

        return pattern.matcher(email).matches();
    }


    private boolean validPassword(String password){
        String regexPattern = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{4,8}$";
        Pattern pattern = Pattern.compile(regexPattern);

        return pattern.matcher(password).matches();
    }
}

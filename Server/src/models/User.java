
package models;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class User {
    private int id;
    private String username;
    private String password;

    // Constructor que inicializa los atributos
    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    // Getter para 'id'
    public int getId() {
        return id;
    }

    // Setter para 'id'
    public void setId(int id) {
        this.id = id;
    }

    // Getter para 'username'
    public String getUsername() {
        return username;
    }

    // Setter para 'username'
    public void setUsername(String username) {
        this.username = username;
    }

    // Getter para 'password'
    public String getPassword() {
        return password;
    }

    // Setter para 'password'
    public void setPassword(String password) {
        this.password = password;
    }
    
    public static List<User> readUsersFile() throws ParseException {
        List<User> userList = new ArrayList<>();
        JSONParser parser = new JSONParser();

        String filePath = "user_data.json";

        String projectDir = System.getProperty("user.dir");
        File file = new File(projectDir, filePath);

        if (!file.exists()) {
            System.out.println("El archivo no existe, se devolverá una lista vacía.");
            return userList;
        }

        try (FileReader reader = new FileReader(file)) {
            JSONArray jsonArray = (JSONArray) parser.parse(reader);

            for (Object obj : jsonArray) {
                JSONObject jsonObject = (JSONObject) obj;
                int id = ((Long) jsonObject.get("id")).intValue();
                String username = (String) jsonObject.get("username");
                String password = (String) jsonObject.get("password");

                User user = new User(id, username, password);
                userList.add(user);
            }

        } catch (IOException | ParseException e) {

        }

        return userList;
    }
    
     @Override
        public String toString() {
            return "User{id=" + id + ", username='" + username + "', password='" + password + "'}";
        }
}

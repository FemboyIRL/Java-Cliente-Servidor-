package models;

import java.util.ArrayList;
import java.util.List;

public class User {

    private int id;
    private String username;
    private String password;
    private List<Integer> blockedUsersID;

    // Constructor que inicializa los atributos
    public User(int id, String username, String password, List<Integer> blockedUsersID) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.blockedUsersID = blockedUsersID;
    }

    User(String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
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

    public void setBlockedUsersList(List<Integer> blockedUsersID) {
        this.blockedUsersID = blockedUsersID;
    }

    public List<Integer> getBlockedUsersList() {
        return blockedUsersID;
    }

    public boolean blockUser(User user) {
        if (blockedUsersID == null) {
            blockedUsersID = new ArrayList<>();
        }

        if (!blockedUsersID.contains(user.getId())) {
            blockedUsersID.add(user.getId());
            System.out.println(user.getUsername() + " ha sido bloqueado.");
            return true;
        } else {
            System.out.println(user.getUsername() + " ya está bloqueado.");
            return false;
        }
    }

    public boolean unblockUser(User user) {
        if (blockedUsersID == null) {
            blockedUsersID = new ArrayList<>();
        }

        if (blockedUsersID.contains(user.getId())) {
            blockedUsersID.remove(Integer.valueOf(user.getId()));
            System.out.println(user.getUsername() + " ha sido desbloqueado.");
            return true;
        } else {
            System.out.println(user.getUsername() + " no está bloqueado.");
            return false;
        }
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', password='" + password + "'}";
    }
}

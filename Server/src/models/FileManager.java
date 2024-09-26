package models;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class FileManager {

    private static List<JSONObject> connectToFile(String url) {
        String filePath = url;
        File file = new File(filePath);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Error al crear el archivo: " + e.getMessage());
                return null;
            }
        }
        JSONArray objArray = new JSONArray();
        if (file.length() != 0) {
            try (FileReader reader = new FileReader(file)) {
                JSONParser parser = new JSONParser();
                objArray = (JSONArray) parser.parse(reader);
            } catch (IOException | ParseException e) {
                System.out.println("Error al leer el archivo existente: " + e.getMessage());
            }
        }

        List<JSONObject> jsonObjectList = new ArrayList<>();
        for (Object obj : objArray) {
            jsonObjectList.add((JSONObject) obj);
        }

        return jsonObjectList;
    }

    public static void saveSharedFileToServer(SharedFiles archivoCompartido) {
        String filePath = "archivos_compartidos.json";
        File file = new File(filePath);

        List<JSONObject> jsonArray = connectToFile(filePath);
        JSONArray archivosArray = new JSONArray();
        archivosArray.addAll(jsonArray);

        boolean archivoExiste = false;
        for (Object obj : archivosArray) {
            JSONObject jsonObj = (JSONObject) obj;
            int archivoId = ((Long) jsonObj.get("id")).intValue();
            if (archivoId == archivoCompartido.getId()) {
                archivoExiste = true;
                break;
            }
        }

        if (!archivoExiste) {
            JSONObject archivoObj = new JSONObject();
            archivoObj.put("id", archivoCompartido.getId());
            archivoObj.put("usuarioID", archivoCompartido.getUsuarioID());
            archivoObj.put("nombre", archivoCompartido.getNombre());
            archivoObj.put("descargas", archivoCompartido.getDescargas());

            archivosArray.add(archivoObj);
        } else {
            System.out.println("El archivo con ID " + archivoCompartido.getId() + " ya existe en el archivo.");
        }

        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(archivosArray.toJSONString());
            System.out.println("Archivo agregado o actualizado en el servidor: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }

    public static List<SharedFiles> readSharedFilesFromServer() {
        String filePath = "archivos_compartidos.json";
        File file = new File(filePath);
        List<SharedFiles> archivosCompartidosList = new ArrayList<>();

        if (!file.exists()) {
            System.out.println("El archivo no existe: " + file.getAbsolutePath());
            return archivosCompartidosList;
        }

        try (FileReader reader = new FileReader(file)) {
            JSONParser jsonParser = new JSONParser();
            Object obj = jsonParser.parse(reader);
            JSONArray archivosArray = (JSONArray) obj;

            for (Object archivoObj : archivosArray) {
                JSONObject archivoJSON = (JSONObject) archivoObj;

                int id = ((Long) archivoJSON.get("id")).intValue();
                int usuarioID = ((Long) archivoJSON.get("usuarioID")).intValue();
                String nombre = (String) archivoJSON.get("nombre");
                int descargas = ((Long) archivoJSON.get("descargas")).intValue();

                SharedFiles archivoCompartido = new SharedFiles();
                archivoCompartido.setId(id);
                archivoCompartido.setUsuarioID(usuarioID);
                archivoCompartido.setNombre(nombre);
                archivoCompartido.setDescargas(descargas);

                archivosCompartidosList.add(archivoCompartido);
            }

        } catch (IOException | ParseException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }

        return archivosCompartidosList;
    }

    public static void saveUserToFile(User user) {
        String filePath = "user_data.json";
        File file = new File(filePath);

        List<JSONObject> jsonArray = connectToFile(filePath);
        JSONArray userArray = new JSONArray();
        userArray.addAll(jsonArray);

        boolean userExists = false;
        for (Object obj : userArray) {
            JSONObject jsonObj = (JSONObject) obj;
            int userId = ((Long) jsonObj.get("id")).intValue();
            if (userId == user.getId()) {
                userExists = true;
                break;
            }
        }

        if (!userExists) {
            JSONObject userObj = new JSONObject();
            userObj.put("id", user.getId());
            userObj.put("username", user.getUsername());
            userObj.put("password", user.getPassword());

            JSONArray blockedUsersJSONArray = new JSONArray();
            for (Integer blockedUserId : user.getBlockedUsersList()) {
                blockedUsersJSONArray.add(blockedUserId);
            }
            userObj.put("blockedUsersID", blockedUsersJSONArray);

            userArray.add(userObj);
        } else {
            System.out.println("El usuario con ID " + user.getId() + " ya existe en el archivo.");
        }

        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(userArray.toJSONString());
            System.out.println("Usuario agregado o actualizado en el archivo: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error al escribir en el archivo: " + e.getMessage());
        }
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
                JSONArray blockedUsersJSONArray = (JSONArray) jsonObject.get("blockedUsersID");
                List<Integer> blockedUsersList = new ArrayList<>();

                for (Object blockedUserObj : blockedUsersJSONArray) {
                    int blockedUserId = ((Long) blockedUserObj).intValue();
                    blockedUsersList.add(blockedUserId);
                }
                User user = new User(id, username, password, blockedUsersList);
                userList.add(user);
            }

        } catch (IOException | ParseException e) {

        }

        return userList;
    }

    public static void updateUserInFile(User updatedUser) {
        String filePath = "user_data.json";
        File file = new File(filePath);
        List<JSONObject> jsonArray = connectToFile(filePath);
        JSONArray userArray = new JSONArray();
        userArray.addAll(jsonArray);
        boolean userFound = false;
        for (int i = 0; i < userArray.size(); i++) {
            JSONObject jsonObj = (JSONObject) userArray.get(i);
            int userId = ((Long) jsonObj.get("id")).intValue();
            if (userId == updatedUser.getId()) {
                userFound = true;
                jsonObj.put("username", updatedUser.getUsername());
                jsonObj.put("password", updatedUser.getPassword());
                JSONArray blockedUsersJSONArray = new JSONArray();
                for (Integer blockedUserId : updatedUser.getBlockedUsersList()) {
                    blockedUsersJSONArray.add(blockedUserId);
                }
                jsonObj.put("blockedUsersID", blockedUsersJSONArray);
                userArray.set(i, jsonObj);
                break;
            }
        }
        if (!userFound) {
            System.out.println("Usuario con ID " + updatedUser.getId() + " no encontrado.");
            return;
        }

        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(userArray.toJSONString());
            System.out.println("Usuario actualizado en el archivo: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }

    public static void writeMessageToFile(Recado recado) {
        String filePath = "recados_data.json";
        File file = new File(filePath);
        List<JSONObject> jsonArray = connectToFile(filePath);
        JSONArray recadoArray = new JSONArray();
        recadoArray.addAll(jsonArray);

        JSONObject recadoObj = new JSONObject();
        recadoObj.put("id", recado.getId());
        recadoObj.put("usuarioDe", recado.getUsuarioDe());
        recadoObj.put("usuarioPara", recado.getUsuarioPara());
        recadoObj.put("contenido", recado.getContenidoRecado());
        recadoArray.add(recadoObj);

        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(recadoArray.toJSONString());
            System.out.println("Recado agregado de " + recado.getUsuarioDe() + " para " + recado.getUsuarioPara() + "al archivo en: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }

    public static List<Recado> readMessagesFile() throws ParseException {
        List<Recado> recadoList = new ArrayList<>();
        JSONParser parser = new JSONParser();

        String filePath = "recados_data.json";

        String projectDir = System.getProperty("user.dir");
        File file = new File(projectDir, filePath);

        if (!file.exists()) {
            System.out.println("El archivo no existe, se devolverá una lista vacía.");
            return recadoList;
        }

        try (FileReader reader = new FileReader(file)) {
            JSONArray jsonArray = (JSONArray) parser.parse(reader);

            for (Object obj : jsonArray) {
                JSONObject jsonObject = (JSONObject) obj;
                int id = ((Long) jsonObject.get("id")).intValue();
                String usuarioDe = (String) jsonObject.get("usuarioDe");
                String usuarioPara = (String) jsonObject.get("usuarioPara");
                String contenido = (String) jsonObject.get("contenido");

                Recado recado = new Recado(id, usuarioDe, usuarioPara, contenido);
                recadoList.add(recado);
            }

        } catch (IOException | ParseException e) {
            System.out.println("eee e e ");
        }

        return recadoList;
    }

    public static void deleteMessagesFromFile(List<Recado> messagesToDelete) {
        String filePath = "recados_data.json";
        File file = new File(filePath);
        List<JSONObject> jsonArray = connectToFile(filePath);
        JSONArray recadoArray = new JSONArray();
        recadoArray.addAll(jsonArray);

        JSONArray updatedRecadoArray = new JSONArray();

        List<Integer> idsToDelete = new ArrayList<>();
        for (Recado recado : messagesToDelete) {
            idsToDelete.add(recado.getId());
        }

        for (Object obj : recadoArray) {
            JSONObject jsonObj = (JSONObject) obj;
            int recadoId = ((Long) jsonObj.get("id")).intValue();
            if (!idsToDelete.contains(recadoId)) {
                updatedRecadoArray.add(jsonObj);
            }
        }

        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(updatedRecadoArray.toJSONString());
            System.out.println("Recados actualizados en el archivo: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }

}

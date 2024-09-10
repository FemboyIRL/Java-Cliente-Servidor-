
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import models.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Servidor {

    public static void main(String[] args) throws ParseException {
        try (
                ServerSocket servidor = new ServerSocket(8080);
                Socket cliente = servidor.accept();
                PrintWriter escritor = new PrintWriter(cliente.getOutputStream(), true);
                BufferedReader lector = new BufferedReader(new InputStreamReader(cliente.getInputStream()))) {
            String password;
            String username;
            int passwordCounter = 0;
            boolean reverseMode = false;
            boolean session = false;
            List<User> users = null;

            System.out.println("Servidor en línea");

            while (true) {
                username = lector.readLine();
                boolean userExists = false;
                User activeUser = null;
                users = User.readUsersFile();
                for (User user : users) {
                    System.out.println(user.getId());
                    System.out.println(user.getUsername());
                    System.out.println(user.getPassword());
                    if (username.equals(user.getUsername())) {
                        activeUser = user;
                        userExists = true;
                        break;
                    }
                }
                if (userExists) {
                    escritor.println("Ingrese su contrasenia");

                    while (passwordCounter < 3) {
                        password = lector.readLine();
                        if (password == null) {
                            System.out.println("Cliente desconectado durante el ingreso de contraseña.");
                            break;
                        }

                        if (password.equals(activeUser.getPassword())) {
                            escritor.println("Felicidades te has logeado");
                            session = true;
                            manejarComandos(lector, escritor, reverseMode, session);
                            break;
                        } else {
                            escritor.println("Contraseña incorrecta. Intente nuevamente:");
                            passwordCounter++;
                            if (passwordCounter >= 3) {
                                escritor.println("Demasiados intentos fallidos, cerrando conexión.");
                                break;
                            }
                        }
                    }
                } else {
                    escritor.println("Ingrese la contrasenia para el nuevo usuario " + username);
                    password = lector.readLine();
                    int id = users.size() + 1;
                    User newUser = new User(id, username, password);
                    saveUserToFile(newUser);
                    session = true;
                    escritor.println("Felicidades te has logeado");
                    manejarComandos(lector, escritor, reverseMode, session);
                }
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }

        System.out.println("Servidor cerrado.");
    }

    private static void manejarComandos(BufferedReader lector, PrintWriter escritor, boolean reverseMode, boolean session) throws IOException {
        String entrada;
        String palabraDeletrear = null;
        int indexLetra = 0;

        while (session) {
            entrada = lector.readLine();

            System.out.println("Entrada recibida: " + entrada);

            if (entrada == null) {
                escritor.println("Entrada nula, por favor intente nuevamente.");
                escritor.flush();
                continue;
            }

            if (palabraDeletrear != null) {
                if(reverseMode){
                  String reversa = new StringBuilder(palabraDeletrear).reverse().toString();
                  palabraDeletrear = reversa;
                }
                if (indexLetra < palabraDeletrear.length()) {
                    escritor.println(palabraDeletrear.charAt(indexLetra));
                    escritor.flush();
                    indexLetra++;
                } else {
                    palabraDeletrear = null;
                    indexLetra = 0;
                    escritor.println("Deletreo completado. Puede ingresar otro comando.");
                    escritor.flush();
                }
                continue;
            }

            String[] partes = entrada.split(" ", 2);
            String comando = partes[0].trim();
            String argumento = partes.length > 1 ? partes[1].trim() : "";

            if (entrada.equals("reverse on")) {
                comando = "reverse on";
            }

            if (entrada.equals("reverse off")) {
                comando = "reverse off";
            }

            System.out.println("Comando: " + comando + ", Argumento: " + argumento);

            switch (comando) {
                case "deletrear":
                    if (!argumento.isEmpty()) {
                        palabraDeletrear = argumento;
                        indexLetra = 0;
                        escritor.println("Iniciando deletreo de: " + argumento);
                        escritor.flush();
                    } else {
                        escritor.println("No se ha proporcionado una palabra para deletrear.");
                        escritor.flush();
                    }
                    break;
                case "reverse off":
                    escritor.println("Saliendo del modo reversa");
                    reverseMode = false;
                    break;
                case "reverse on":
                    escritor.println("Entrando en modo reversa");
                    reverseMode = true;
                    break;
                case "pt":
                    escritor.println("Ingrese su nombre de usuario");
                    escritor.flush();
                    session = false;
                    break;
                default:
                    if (reverseMode) {
                        String reversa = new StringBuilder(entrada).reverse().toString();
                        escritor.println(reversa);
                        escritor.flush();
                    } else {
                        escritor.println(entrada.toUpperCase() + " Luis");
                        escritor.flush();
                    }
                    break;
            }
        }
    }


    private static void saveUserToFile(User user) {
        String filePath = "user_data.json";
        File file = new File(filePath);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.out.println("Error al crear el archivo: " + e.getMessage());
                return;
            }
        }

        JSONArray userArray = new JSONArray();
        if (file.length() != 0) {
            try (FileReader reader = new FileReader(file)) {
                JSONParser parser = new JSONParser();
                userArray = (JSONArray) parser.parse(reader);
            } catch (IOException | ParseException e) {
                System.out.println("Error al leer el archivo existente: " + e.getMessage());
            }
        }

        JSONObject userObj = new JSONObject();
        userObj.put("id", user.getId());
        userObj.put("username", user.getUsername());
        userObj.put("password", user.getPassword());
        userArray.add(userObj);

        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(userArray.toJSONString());
            System.out.println("Usuario agregado al archivo en: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }
}

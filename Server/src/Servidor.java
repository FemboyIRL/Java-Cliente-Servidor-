
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.FileManager;
import models.Recado;
import models.SharedFiles;
import models.User;
import org.json.simple.parser.ParseException;

public class Servidor {

    public static void main(String[] args) throws ParseException, java.text.ParseException {
        try (
                ServerSocket servidor = new ServerSocket(8080);
                Socket cliente = servidor.accept();
                PrintWriter escritor = new PrintWriter(cliente.getOutputStream(), true);
                BufferedReader lector = new BufferedReader(new InputStreamReader(cliente.getInputStream()))) {
            String password;
            String username;
            int passwordCounter = 0;
            boolean reverseMode = false;
            List<User> users;

            System.out.println("Servidor en línea");

            deleteExpiredFiles();

            while (true) {
                username = lector.readLine();
                boolean userExists = false;
                User activeUser = null;
                users = FileManager.readUsersFile();
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
                            _manageMessages(activeUser, lector, escritor);
                            escritor.println("Felicidades te has logeado");
                            manejarComandos(lector, escritor, reverseMode, activeUser, passwordCounter, servidor, cliente);
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
                    List<Integer> blockedUsers = new ArrayList();
                    User newUser = new User(id, username, password, blockedUsers);
                    FileManager.saveUserToFile(newUser);
                    activeUser = newUser;
                    escritor.println("Felicidades te has logeado");
                    manejarComandos(lector, escritor, reverseMode, activeUser, passwordCounter, servidor, cliente);

                }
            }
        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }

        System.out.println(
                "Servidor cerrado.");
    }

    private static void deleteExpiredFiles() throws java.text.ParseException {
        String sharedFilesPath = "compartidos/";
        List<SharedFiles> archivosCompartidos = FileManager.readSharedFilesFromServer();
        List<SharedFiles> expiredFiles = new ArrayList<>();

        for (SharedFiles archivo : archivosCompartidos) {
            if (archivo.haExpirado()) {
                String fileToDeletePath = sharedFilesPath + archivo.getNombre();
                File fileToDelete = new File(fileToDeletePath);
                expiredFiles.add(archivo);

                if (fileToDelete.exists()) {
                    if (fileToDelete.delete()) {
                        System.out.println("Archivo eliminado: " + fileToDelete.getAbsolutePath());
                    } else {
                        System.out.println("No se pudo eliminar el archivo: " + fileToDelete.getAbsolutePath());
                    }
                }
            }
        }
        if (!expiredFiles.isEmpty()) {
            System.out.println("Archivos expirados eliminados. Actualizando el registro de archivos compartidos...");
            FileManager.deleteSharedFilesFromFile(expiredFiles);
        } else {
            System.out.println("No se encontraron archivos expirados.");
        }

    }

    private static void _manageMessages(User activeUser, BufferedReader lector, PrintWriter escritor) throws ParseException, IOException {
        List<Recado> allMessages;
        allMessages = FileManager.readMessagesFile();
        List<Recado> userMessages = new ArrayList<>();
        for (Recado message : allMessages) {
            if (message.getUsuarioPara().equals(activeUser.getUsername())) {
                userMessages.add(message);
            }
        }
        if (!userMessages.isEmpty()) {
            escritor.println(userMessages);
            FileManager.deleteMessagesFromFile(userMessages);
            lector.readLine();
        }
    }

    private static void downloadFile(File fileToDownload, Socket clientSocket, PrintWriter escritor) {
        try (FileInputStream fileInputStream = new FileInputStream(fileToDownload);
                OutputStream outputStream = clientSocket.getOutputStream()) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();

            outputStream.write("FIN".getBytes());

            System.out.println("Archivo enviado correctamente: " + fileToDownload.getName());
        } catch (IOException e) {
            escritor.println("Error al descargar el archivo: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error al cerrar el socket del cliente: " + e.getMessage());
            }
        }
    }

    private static void manejarComandos(BufferedReader lector, PrintWriter escritor, boolean reverseMode, User activeUser, int passwordCounter, ServerSocket servidor, Socket cliente) throws IOException, java.text.ParseException {
        String entrada;
        String palabraDeletrear = null;
        int indexLetra = 0;

        while (activeUser != null) {
            try {
                entrada = lector.readLine();

                System.out.println("Entrada recibida: " + entrada);

                if (entrada == null) {
                    escritor.println("Entrada nula, por favor intente nuevamente.");
                    escritor.flush();
                    continue;
                }

                if (palabraDeletrear != null) {
                    if (reverseMode) {
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
                        escritor.flush();
                        reverseMode = false;
                        break;
                    case "reverse on":
                        escritor.println("Entrando en modo reversa");
                        reverseMode = true;
                        escritor.flush();
                        break;
                    case "pt":
                        escritor.println("Ingrese su nombre de usuario");
                        escritor.flush();
                        passwordCounter = 0;
                        activeUser = null;
                        break;
                    case "usuarios":
                        List<User> users;
                        try {
                            users = FileManager.readUsersFile();
                            escritor.println(users);
                        } catch (ParseException ex) {
                            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        break;
                    case "recado":
                        users = FileManager.readUsersFile();
                        List<Recado> allMessages = FileManager.readMessagesFile();
                        User userExists = null;
                        for (User user : users) {
                            System.out.println(argumento);
                            if (argumento.equals(user.getUsername())) {
                                System.out.println(user.getUsername());
                                System.out.println(user.getId());
                                userExists = user;
                            }
                        }
                        if (userExists != null) {
                            if (userExists.getBlockedUsersList().contains(activeUser.getId())) {
                                escritor.println("No puedes enviar un recado a " + userExists.getUsername() + " porque te ha bloqueado.");
                            } else {
                                escritor.println("Inserte el contenido del recado");
                                String contenido = lector.readLine();
                                Recado recado = new Recado(allMessages.size() + 1, activeUser.getUsername(), userExists.getUsername(), contenido);
                                FileManager.writeMessageToFile(recado);
                                escritor.println("Recado enviado de " + activeUser.getUsername() + " para " + userExists.getUsername());
                            }
                        } else {
                            escritor.println("Usuario destinatario no encontrado.");
                        }
                        break;
                    case "bloquear":
                        users = FileManager.readUsersFile();
                        userExists = null;
                        for (User user : users) {
                            System.out.println(argumento);
                            if (argumento.equals(user.getUsername())) {
                                System.out.println(user.getUsername());
                                System.out.println(user.getId());
                                userExists = user;
                            }
                        }
                        if (userExists != null) {
                            escritor.println("Seguro que deseas bloquear a " + argumento + "(y/n)");
                            String ans = lector.readLine();
                            switch (ans) {
                                case "y":
                                    boolean operation = activeUser.blockUser(userExists);
                                    if (operation) {
                                        FileManager.updateUserInFile(activeUser);
                                        escritor.println("Usuario " + argumento + " bloqueado exitosamente");
                                    } else {
                                        escritor.println("Usuario " + argumento + " ya está bloqueado.");
                                    }
                                    break;
                                case "n":
                                    escritor.println("Usuario " + argumento + " no ha sido bloqueado");
                                    break;
                                default:
                                    escritor.println("Responda con y o n ");
                                    break;
                            }
                        } else {
                            escritor.println("Usuario destinatario no encontrado.");
                        }
                        break;
                    case "desbloquear":
                        users = FileManager.readUsersFile();
                        userExists = null;
                        for (User user : users) {
                            System.out.println(argumento);
                            if (argumento.equals(user.getUsername())) {
                                System.out.println(user.getUsername());
                                System.out.println(user.getId());
                                userExists = user;
                            }
                        }
                        if (userExists != null) {
                            escritor.println("Seguro que deseas desbloquear a " + argumento + "(y/n)");
                            String ans = lector.readLine();
                            switch (ans) {
                                case "y":
                                    boolean operation = activeUser.unblockUser(userExists);
                                    if (operation) {
                                        FileManager.updateUserInFile(activeUser);
                                        escritor.println("Usuario " + argumento + " desbloqueado exitosamente");
                                    } else {
                                        escritor.println("Usuario " + argumento + " no está bloqueado.");
                                    }
                                    break;
                                case "n":
                                    escritor.println("Usuario " + argumento + " no ha sido desbloqueado");
                                    break;
                                default:
                                    escritor.println("Responda con y/n ");
                                    break;
                            }
                        } else {
                            escritor.println("Usuario destinatario no encontrado.");
                        }
                        break;
                    case "listarArchivos":
                        List<SharedFiles> archivosCompartidos = FileManager.readSharedFilesFromServer();
                        if (!archivosCompartidos.isEmpty()) {
                            escritor.println(archivosCompartidos);
                            escritor.flush();
                        }
                        break;
                    case "subirArchivos":
                        if (argumento.isEmpty()) {
                            escritor.println("Ingresa la ruta del archivo a subir.");
                            break;
                        }

                        File sourceFile = new File(argumento);

                        if (!sourceFile.isAbsolute()) {
                            escritor.println("Por favor, ingresa la ruta absoluta del archivo.");
                            break;
                        }

                        if (!sourceFile.exists()) {
                            escritor.println("El archivo especificado no se encontró en la ruta: " + argumento);
                            break;
                        }

                        escritor.println("Ingrese la contraseña para el archivo(deje en blanco para dejar sin contraseña)");
                        String password = lector.readLine();

                        escritor.println("Ingrese la fecha de expiración del archivo (dd/MM/yyyy HH:mm):");
                        String fechaExpiracionString = lector.readLine();

                        String regex = "\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}";
                        if (!fechaExpiracionString.matches(regex)) {
                            escritor.println("Error: La fecha ingresada no tiene el formato correcto. Use el formato dd/MM/yyyy HH:mm.");
                            break;
                        }

                        SimpleDateFormat formatoFechaHora = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                        formatoFechaHora.setLenient(false);
                        Date fechaExpiracion;

                        fechaExpiracion = formatoFechaHora.parse(fechaExpiracionString);

                        archivosCompartidos = FileManager.readSharedFilesFromServer();

                        String destinationPath = "compartidos/" + sourceFile.getName();
                        File destinationFile = new File(destinationPath);

                        try {
                            if (!destinationFile.getParentFile().exists() && !destinationFile.getParentFile().mkdirs()) {
                                throw new IOException("No se pudo crear el directorio de destino.");
                            }

                            Files.copy(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            System.out.println("Archivo subido correctamente a: " + destinationFile.getAbsolutePath());

                            int newId = archivosCompartidos.size() + 1;
                            SharedFiles archivoCompartido = new SharedFiles(newId, activeUser.getId(), sourceFile.getName(), 0, password, fechaExpiracion);
                            FileManager.saveSharedFileToServer(archivoCompartido);

                            escritor.println("El archivo ha sido subido y registrado correctamente.");
                        } catch (IOException e) {
                            escritor.println("Error al subir el archivo: " + e.getMessage());
                            e.printStackTrace();
                        }
                        break;

                    case "bajarArchivos":
                        if (argumento.isEmpty()) {
                            escritor.println("Ingresa el nombre del archivo a descargar.");
                            break;
                        }
                        String filePath = "compartidos/" + argumento;

                        File fileToDownload = new File(filePath);
                        
                        System.out.println("Tamaño del archivo: " + fileToDownload.length() + " bytes");

                        System.out.println("Buscando el archivo para descargar en: " + fileToDownload.getAbsolutePath());

                        if (!fileToDownload.exists()) {
                            escritor.println("El archivo especificado no se encontró: " + fileToDownload.getAbsolutePath());
                            break;
                        }

                        // Aceptar la conexión del cliente
                        Socket descargador = servidor.accept();

                        try (FileInputStream fileInputStream = new FileInputStream(fileToDownload);
                                OutputStream outputStream = descargador.getOutputStream()) {

                            byte[] buffer = new byte[1024];
                            int bytesRead;

                            // Leer el archivo y enviarlo al cliente
                            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                            }

                            outputStream.flush(); // Asegúrate de que todos los datos se envían

                            // Enviar un mensaje final para indicar que la transferencia ha terminado
                            outputStream.write("FIN".getBytes()); // Mensaje final

                        } catch (IOException e) {
                            escritor.println("Error al descargar el archivo: " + e.getMessage());
                            e.printStackTrace(); // Para obtener más detalles sobre el error
                        } finally {
                            try {
                                descargador.close(); // Cerrar el socket del cliente
                            } catch (IOException e) {
                                System.err.println("Error al cerrar el socket del cliente: " + e.getMessage());
                            }
                        }
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
            } catch (ParseException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}

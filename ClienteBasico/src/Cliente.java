
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente {

    public static void main(String[] args) {
        try (
                Socket salida = new Socket("localhost", 8080);
                PrintWriter escritor = new PrintWriter(salida.getOutputStream(), true);
                BufferedReader lector = new BufferedReader(new InputStreamReader(salida.getInputStream()));
                BufferedReader passwordReader = new BufferedReader(new InputStreamReader(System.in));
                BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in))) {

            String entrada;
            String lectura;
            String passwordResponse;
            String password;
            System.out.println("Ingrese su nombre de usuario");

            while ((password = passwordReader.readLine()) != null) {
                escritor.println(password);
                passwordResponse = lector.readLine();
                System.out.println(passwordResponse);

                if ("Felicidades te has logeado".equals(passwordResponse)) {
                    System.out.println("Ingrese un comando:");

                    while ((entrada = teclado.readLine()) != null) {
                        escritor.println(entrada);

                        if (entrada.startsWith("bajarArchivos")) {
                            String[] partes = entrada.split(" ", 2);
                            if (partes.length > 1) {
                                String nombreArchivo = partes[1];

                                String archivoResponse = lector.readLine();
                                System.out.println(archivoResponse); 

                                // Si el servidor indica que el archivo tiene contraseña
                                if (archivoResponse.equals("Ingrese la contraseña del archivo")) {
                                    String passwordArchivo = teclado.readLine();
                                    escritor.println(passwordArchivo); // Enviar contraseña al servidor

                                    // Verificar la respuesta del servidor respecto a la contraseña
                                    String passwordCheckResponse = lector.readLine();
                                    if (passwordCheckResponse.equals("Contraseña correcta. Iniciando descarga...")) {
                                        // Aquí puedes implementar la lógica para recibir el archivo
                                        try (Socket fileSocket = new Socket("localhost", 8080);
                                                InputStream inputStream = fileSocket.getInputStream()) {
                                            recibirArchivo(inputStream, nombreArchivo);
                                        } catch (IOException e) {
                                            System.err.println("Error al recibir el archivo: " + e.getMessage());
                                        }
                                    } else {
                                        System.out.println("Contraseña incorrecta. No se puede descargar el archivo.");
                                    }
                                } else if (archivoResponse.equals("El archivo se descargará sin contraseña.")) {
                                    // Si el archivo no tiene contraseña, simplemente lo descargamos
                                    try (Socket fileSocket = new Socket("localhost", 8080);
                                            InputStream inputStream = fileSocket.getInputStream()) {
                                        recibirArchivo(inputStream, nombreArchivo);
                                    } catch (IOException e) {
                                        System.err.println("Error al recibir el archivo: " + e.getMessage());
                                    }
                                }
                            } else {
                                System.out.println("Debe proporcionar el nombre del archivo.");
                            }
                        } else {
                            lectura = lector.readLine();
                            System.out.println(lectura);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }

    private static void recibirArchivo(InputStream inputStream, String nombreArchivo) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(nombreArchivo)) {
            byte[] buffer = new byte[1024];
            int bytesRead;

            System.out.println("Descargando archivo: " + nombreArchivo);

            // Leer el archivo y guardarlo
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
                System.out.println("Bytes leídos: " + bytesRead); // Para depuración
            }

            System.out.println("Archivo recibido y guardado como: " + nombreArchivo);
        } catch (IOException e) {
            System.err.println("Error al recibir el archivo: " + e.getMessage());
        }
    }

}

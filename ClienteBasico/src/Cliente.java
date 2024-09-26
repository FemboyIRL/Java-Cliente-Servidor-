
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
                                recibirArchivo(salida, nombreArchivo);
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

    private static void recibirArchivo(Socket socket, String nombreArchivo) {
        try (InputStream inputStream = socket.getInputStream();
                FileOutputStream fileOutputStream = new FileOutputStream(nombreArchivo)) {

            byte[] buffer = new byte[1024];  
            int bytesRead;

            System.out.println("Descargando archivo: " + nombreArchivo);

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }

            System.out.println("Archivo recibido y guardado como: " + nombreArchivo);

            BufferedReader lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String mensajeFinal = lector.readLine(); 
            System.out.println(mensajeFinal);

        } catch (IOException e) {
            System.err.println("Error al recibir el archivo: " + e.getMessage());
        }
    }

}

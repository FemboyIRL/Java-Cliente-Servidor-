
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente {

    public static void main(String[] args) {
        try (
                // Creación del socket y los flujos de entrada/salida
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
                        lectura = lector.readLine();
                        System.out.println(lectura);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }
}


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente {

    public static void main(String[] args) throws IOException {
        Socket salida = new Socket("localhost", 8080);
        PrintWriter escritor = new PrintWriter(salida.getOutputStream(), true);
        BufferedReader lector = new BufferedReader(new InputStreamReader(salida.getInputStream()));
        BufferedReader passwordReader = new BufferedReader(new InputStreamReader(System.in));
        BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));

        String entrada;
        String lectura;
        String passwordResponse;
        String password;

        System.out.println("Ingrese la contrasenia");

        try {
            while ((password = passwordReader.readLine()) != null) {
                escritor.println(password);
                passwordResponse = lector.readLine();
                System.out.println(passwordResponse);
                if (passwordResponse.equals("Felicidades te has logeado")) {
                    while ((entrada = teclado.readLine()) != null) {
                        escritor.println(entrada);
                        lectura = lector.readLine();
                        System.out.println(lectura);
                    }
                }
            }
        } catch (NullPointerException e) {
            System.out.println("Demasiados intentos cerrando conexion");
        }

    }
}

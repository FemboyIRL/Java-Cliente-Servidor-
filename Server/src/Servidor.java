
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    public static void main(String[] args) {
        try (
                ServerSocket servidor = new ServerSocket(8080);
                Socket cliente = servidor.accept();
                PrintWriter escritor = new PrintWriter(cliente.getOutputStream(), true);
                BufferedReader lector = new BufferedReader(new InputStreamReader(cliente.getInputStream()))) {
            String realPassword = "caillou";
            String password;
            int passwordCounter = 0;
            boolean reverseMode = false;

            System.out.println("Servidor en línea");

            while (passwordCounter < 3) {
                password = lector.readLine();
                if (password == null) {
                    System.out.println("Cliente desconectado durante el ingreso de contraseña.");
                    break;
                }

                if (password.equals(realPassword)) {
                    escritor.println("Felicidades te has logeado");
                    manejarComandos(lector, escritor, reverseMode, password);
                    break;
                } else {
                    escritor.println("Contraseña incorrecta. Intente nuevamente:");
                    passwordCounter++;
                }
            }

            if (passwordCounter >= 3) {
                escritor.println("Demasiados intentos fallidos, cerrando conexión.");
            }

        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }

        System.out.println("Servidor cerrado.");
    }

    private static void manejarComandos(BufferedReader lector, PrintWriter escritor, boolean reverseMode, String password) throws IOException {
        String entrada;
        while ((entrada = lector.readLine()) != null) {
            switch (entrada) {
                case "reverse off":
                    if (reverseMode == false) {
                        escritor.println("El modo reversa ya esta apagado");
                    } else {
                        escritor.println("Saliendo del modo reversa");
                        reverseMode = false;
                    }
                    break;
                case "reverse on":
                    if (reverseMode) {
                        escritor.println("El modo reversa ya esta activado");
                    } else {
                        escritor.println("Entrando en modo reversa");
                        reverseMode = true;
                    }
                    break;
                case "":
                    escritor.println("Ingrese algo");
                    break;
                case "pt":
                    escritor.println("Cerrando conexión...");
                    password = null;
                    break;
                default:
                    if (reverseMode) {
                        String reversa = new StringBuilder(entrada).reverse().toString();
                        escritor.println(reversa);
                    } else {
                        escritor.println(entrada.toUpperCase() + "Luis");
                    }
                    break;
            }
        }
    }
}

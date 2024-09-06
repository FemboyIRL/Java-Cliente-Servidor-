
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
            String realPassword = "1234";
            String password;
            int passwordCounter = 0;
            boolean reverseMode = false;
            boolean session = false;

            System.out.println("Servidor en línea");

            while (passwordCounter < 3) {
                password = lector.readLine();
                if (password == null) {
                    System.out.println("Cliente desconectado durante el ingreso de contraseña.");
                    break;
                }

                if (password.equals(realPassword)) {
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
                    escritor.flush();
                    reverseMode = true;
                    break;
                case "pt":
                    escritor.println("Cerrando conexión...");
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

}


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

    public static void main(String[] nyaOwO) throws IOException {
        ServerSocket servidor = new ServerSocket(8080);
        Socket cliente = servidor.accept();

        PrintWriter escritor = new PrintWriter(cliente.getOutputStream(), true);
        BufferedReader lector = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
        String realPassword = "caillou";
        String password;
        int passwordCounter = 0;
        String entrada;
        
        System.out.println("Servidor en linea");

        while ((password = lector.readLine()) != null) {
            if(passwordCounter >= 3){
                escritor.println("i");
                break;
            }
            if (password.equals(realPassword)) {
                escritor.println("Felicidades te has logeado");
                while ((entrada = lector.readLine()) != null) {
                    switch (entrada) {
                        case "":
                            escritor.println("Ingrese algo");
                            break;
                        case "pt":
                            break;
                        default:
                            escritor.println(entrada.toUpperCase() + "Luis");
                            break;
                    }
                }
            } else {
                escritor.println("Contrasenia incorrecta");
                passwordCounter++;
            }
        }

    }
}

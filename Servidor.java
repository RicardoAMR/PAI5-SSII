import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

public class Servidor extends Conexion // Se hereda de conexión para hacer uso de los sockets y demás
{
    public Servidor() throws IOException {
        super("servidor");
    } // Se usa el constructor para servidor de Conexion

    public Boolean verificacionFirma(String firma, String clavePublica, String mensaje) 
                                        throws SignatureException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException{
        Signature sg = Signature.getInstance("SHA256withRSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(clavePublica.getBytes());
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey pubKey = keyFactory.generatePublic(keySpec);
        sg.initVerify(pubKey);
        sg.update(mensaje.getBytes());
        // Verification de firma
        return sg.verify(firma.getBytes());
    }

    public void startServer()// Método para iniciar el servidor
    {
        try {
            System.out.println("Esperando..."); // Esperando conexión

            cs = ss.accept(); // Accept comienza el socket y espera una conexión desde un cliente

            System.out.println("Cliente en línea");

            // Se obtiene el flujo de salida del cliente para enviarle mensajes
            salidaCliente = new DataOutputStream(cs.getOutputStream());

            // Se le envía un mensaje al cliente usando su flujo de salida
            salidaCliente.writeUTF("Petición recibida y aceptada");

            // Se obtiene el flujo entrante desde el cliente
            BufferedReader entrada = new BufferedReader(new InputStreamReader(cs.getInputStream()));
            
            while ((mensajeServidor = entrada.readLine()) != null) // Mientras haya mensajes desde el cliente
            {
                // Se verifica que la firma sea correcta
                Boolean sn = verificacionFirma(mensajeServidor.split("|")[2], mensajeServidor.split("|")[3], mensajeServidor.split("|")[0]);
                PrintWriter fichero = null;
                try
                {
                    // si append == true  escribe al final del fichero
                    // si append == false sobrescribe el fichero
                    boolean append = true;
                    fichero = new PrintWriter(new FileWriter("log.txt", append));
                    if (sn) {
                        fichero.println("ACIERTO - No hay error de firma");
                    } else {
                        fichero.println("FALLO - Hay error de firma");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (null != fichero) fichero.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            System.out.println("Fin de la conexión");

            BufferedReader fichero = null;
            List<String> aciertos = new ArrayList<>();
            List<String> total = new ArrayList<>();
            try{
                fichero = new BufferedReader(new FileReader("log.txt"));
                String linea = fichero.readLine();
                while (linea != null) {
                    total.add(linea);
                    if (linea.contains("ACIERTO")){
                        aciertos.add(linea);
                    }
                    linea = fichero.readLine();
                }
            } catch(Exception e){
                System.out.println("Error"+e.toString());
            } finally {
                try {
                    if (fichero != null) fichero.close();
                } catch(Exception e){
                    System.out.println("Error"+e.toString());
                }
            }
            // salida por pantalla
            System.out.println("Hay una proporción de aciertos de " + aciertos.size()/total.size());
            ss.close();// Se finaliza la conexión con el cliente
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
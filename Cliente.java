import java.io.DataOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.Signature;

public class Cliente extends Conexion {
    public Cliente() throws IOException {
        super("cliente");
    } // Se usa el constructor para cliente de Conexion

    public void startClient() // Método para iniciar el cliente
    {
        try {
            // Flujo de datos hacia el servidor
            salidaServidor = new DataOutputStream(cs.getOutputStream());
            KeyPairGenerator kgen = KeyPairGenerator.getInstance("RSA");
            kgen.initialize(2048);
            KeyPair keys = kgen.generateKeyPair();
            PublicKey clavePublica = keys.getPublic();
            String prueba = "fadsfdasfasfdsadsada";
            Signature sg = Signature.getInstance("SHA256withRSA");
            sg.initSign(keys.getPrivate());
            sg.update(prueba.getBytes());
            // Firma
            byte[] firma = sg.sign();
            // Se enviará la firma, clave pública
            salidaServidor.writeBytes(prueba + "|" + "IDempleado" + "|" + firma + "|" + clavePublica.toString());

            cs.close();// Fin de la conexión

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
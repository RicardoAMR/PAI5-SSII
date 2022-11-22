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
import java.util.Calendar;
import java.util.GregorianCalendar;
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
            
            List<Integer> proporciones = new ArrayList<>();
            while ((mensajeServidor = entrada.readLine()) != null) // Mientras haya mensajes desde el cliente
            {
                // Se verifica que la firma sea correcta
                String[] separar = mensajeServidor.split("|"); 
                Boolean sn = verificacionFirma(separar[2], separar[3], separar[0]);
                Calendar fecha = new GregorianCalendar();
                Integer mes = fecha.get(Calendar.MONTH);
                List<String> aciertos = new ArrayList<>();
                List<String> total = new ArrayList<>();
                PrintWriter fichero = null;
                // Se almacenan los errores
                try
                {
                    // si append == true  escribe al final del fichero
                    // si append == false sobrescribe el fichero
                    boolean append = true;
                    fichero = new PrintWriter(new FileWriter("log" + mes + ".txt", append));
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
                
                Thread.sleep(0);

                Calendar nuevaFecha = new GregorianCalendar();
                Integer nuevoMes = nuevaFecha.get(Calendar.MONTH);
                if(mes != nuevoMes){
                    // Se calcula la proporción de error al cambiar de mes
                    BufferedReader calculo = null;
                    try{
                        calculo = new BufferedReader(new FileReader("log" + nuevoMes + ".txt"));
                        String linea = calculo.readLine();
                        while (linea != null) {
                            total.add(linea);
                            if (linea.contains("ACIERTO")){
                                aciertos.add(linea);
                            }
                            linea = calculo.readLine();
                        }
                    } catch(Exception e){
                        System.out.println("Error"+e.toString());
                    } finally {
                        try {
                            if (calculo != null) calculo.close();
                        } catch(Exception e){
                            System.out.println("Error"+e.toString());
                        }
                    }
                    
                    // Se escribe al final del fichero la proporción resultante
                    PrintWriter registro = null;
                    try
                    {
                        // si append == true  escribe al final del fichero
                        // si append == false sobrescribe el fichero
                        boolean append = true;
                        registro = new PrintWriter(new FileWriter("log" + nuevoMes + ".txt", append));
                        registro.println("Hay una proporción de aciertos de " + (aciertos.size()/total.size()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (null != registro) registro.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    // salida por pantalla
                    System.out.println("Hay una proporción de aciertos de " + (aciertos.size()/total.size()));
                }
            }

            if(proporciones.size()>2){
                for (int i=0;i<(proporciones.size()-2);i++){
                    Integer p1 = proporciones.get(i);
                    Integer p2 = proporciones.get(i+1);
                    Integer p3 = proporciones.get(i+2);
                    if ((p3 > p1 && p3 > p2) || (p3 < p1 && p3 == p2) || (p3 == p1 && p3 > p2)){
                        System.out.println("TENDENCIA POSITIVA");
                    } else if(p3 < p1 || p3 < p2) {
                        System.out.println("TENDENCIA NEGATIVA");
                    } else if(p3 == p1 && p3 == p2) {
                        System.out.println("TENDENCIA NULA");
                    }
                }
            }

            System.out.println("Fin de la conexión");

            ss.close();// Se finaliza la conexión con el cliente
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
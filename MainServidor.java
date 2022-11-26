import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.net.ServerSocketFactory;

public class MainServidor {

    private ServerSocket serverSocket;


    // Constructor del Servidor
    public MainServidor() throws Exception {
        ServerSocketFactory socketFactory = (ServerSocketFactory) ServerSocketFactory.getDefault();
        serverSocket = (ServerSocket) socketFactory.createServerSocket(7071);
    }

    public Boolean verificacionFirma(String firma, byte[] clavePublica, String mensaje) 
            throws SignatureException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException{                                 
        Signature sg = Signature.getInstance("SHA256withRSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(clavePublica);                    
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");                
        RSAPublicKey pubKey = (RSAPublicKey) keyFactory.generatePublic(keySpec);              
        sg.initVerify(pubKey);
        //Se modifica artificialmente el mensaje
        double n = 6;
        if((int)(Math.random()*10+1) < n){
        mensaje = "Vega ha modificado el mensaje (VEGA ESO NO SE HACE   D:<   )";
        }
        sg.update(mensaje.getBytes());
        // Verification de firma
        boolean t = sg.verify(Base64.getDecoder().decode(firma));
        System.out.println(t);
        return t;
}

    // Ejecución del servidor para escuchar peticiones de los clientes
    private boolean runServer() {
        List<Double> proporciones = new ArrayList<>();
        while (true) {
            try {
                System.err.println("Esperando conexiones de clientes...");
                Socket socket = (Socket) serverSocket.accept();
                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //PrintWriter output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                String p = input.readLine();
                boolean c=true;
                while (c) {  
                    String[] separar = p.split("_");
                    //String m = separar[3].split("=")[1];
                    //String k = m.split(",")[0];
                    //System.out.println(k);
                    System.out.println(separar[0]);
                    System.out.println(separar[2]);
                    byte[] publicBytes = Base64.getMimeDecoder().decode(separar[3].getBytes());
                    Boolean sn = verificacionFirma(separar[2], publicBytes, separar[0]+"_"+separar[1]);
                    Calendar fecha = new GregorianCalendar();
                    Integer mes = fecha.get(Calendar.MONTH);
                    List<String> aciertos = new ArrayList<>();
                    List<String> total = new ArrayList<>();

                    // Se almacenan los errores
                    PrintWriter fichero = null;
                    try
                    {
                        // si append == true  escribe al final del fichero
                        // si append == false sobrescribe el fichero
                        boolean append = true;
                        fichero = new PrintWriter(new FileWriter("./log/log" + mes + ".txt", append));
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
                    
                    Thread.sleep(10000);
    
                    Calendar nuevaFecha = new GregorianCalendar();
                    Integer nuevoMes = nuevaFecha.get(Calendar.MONTH);
                    if(mes != nuevoMes){
                        System.out.println("ha cambiado el mes");
                        c=false;
                        // Se calcula la proporci贸n de error al cambiar de mes
                        BufferedReader calculo = null;
                        try{
                            calculo = new BufferedReader(new FileReader("./log/log" + mes + ".txt"));
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
                        
                        

                        // Se escribe al final del fichero la proporci贸n resultante
                        PrintWriter registro = null;
                        double resultado = (double)aciertos.size()/total.size();
                        try
                        {
                            // si append == true  escribe al final del fichero
                            // si append == false sobrescribe el fichero
                            boolean append = true;
                            registro = new PrintWriter(new FileWriter("./log/log" + mes + ".txt", append));
                            registro.println("Hay una proporci贸n de aciertos de " + resultado);
                            proporciones.add(resultado);
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
                        System.out.println("Hay una proporci贸n de aciertos de " + resultado);
                    }
                }
    
                if(proporciones.size()>2){
                    for (int i=0;i<(proporciones.size()-2);i++){
                        Double p1 = proporciones.get(i);
                        Double p2 = proporciones.get(i+1);
                        Double p3 = proporciones.get(i+2);
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
    
                socket.close();// Se finaliza la conexión con el cliente
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void main(String args[]) throws Exception {
        MainServidor server = new MainServidor();
        server.runServer();
    }
}
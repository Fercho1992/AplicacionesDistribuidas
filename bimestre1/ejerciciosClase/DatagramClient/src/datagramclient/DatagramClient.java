package datagramclient;

/**
 * un EchoClient que se lee datos del usuario, lo envia al servidor. El servidor
 * recibe los datagramas y regresa el mismo datagrama y el cliente lo imprime.
 * Se conecta solo al localhost.
 * 
*/
import java.net.*;
import java.io.*;

public class DatagramClient {

    String hostname;

    //Construactor
    public DatagramClient() {
    }

    //Contructor que devuelve un String
    public DatagramClient(String hostname) {
        this.hostname = hostname;
    }

    //Metodo Get
    public String getHostname() {
        return hostname;
    }

    //Metodo Set
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    //Main
    public static void main(String[] args) {

        DatagramClient direccionCliente = new DatagramClient();
        //Direccion IP del cliente y tambien de servidor
        
        //Al comentar esta linea de codigo la direccion IP que va a escoger va
        //ser del localhost 127.0.0.1
        direccionCliente.setHostname("192.168.1.5");
        
        //Puertos
        int port = 2018;
        int len = 1024;
        
        DatagramPacket sPacket, rPacket;

        try {
            InetAddress ia = InetAddress.getByName(direccionCliente.getHostname());
            DatagramSocket datasocket = new DatagramSocket();
            BufferedReader stdinp = new BufferedReader(
                    new InputStreamReader(System.in));

            while (true) {
                try {
                    System.out.println("Ingrese un mensaje:");
                    String echoline = stdinp.readLine();

                    //Este método compara esta cadena para el objeto especificado. 
                    //El resultado es verdadero si y sólo si el argumento no es 
                    //nulo y es un objeto String que representa la misma secuencia 
                    //de caracteres que este objeto .
                    //Si el mensaje es "Salir" se termina la coneccion con el servidor
                    if (echoline.equals("Salir")) {
                        break;
                    }

                    byte[] buffer = new byte[echoline.length()];
                    buffer = echoline.getBytes();
                    sPacket = new DatagramPacket(buffer, buffer.length, ia, port);
                    System.out.println("Enviando mensaje al servidor..." + direccionCliente.getHostname());
                    datasocket.send(sPacket);

                    byte[] rbuffer = new byte[len];
                    rPacket = new DatagramPacket(rbuffer, rbuffer.length);
                    System.out.println("Esperando respuesta del servidor...");
                    datasocket.receive(rPacket);

                    String retstring = new String(rPacket.getData(), 0, rPacket.getLength());
                    System.out.println("El servidor devolvió el siguiente mensaje:");
                    System.out.println(retstring);
                } catch (IOException e) {
                    System.err.println(e);
                }
            }  //  while
        } catch (UnknownHostException e) {
            System.err.println(e);
        } catch (SocketException se) {
            System.err.println(se);
        }
    }//end main
}

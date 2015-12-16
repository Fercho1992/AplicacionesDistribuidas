/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nameserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 *
 * @author PC-12
 */
public class Conexion extends Thread {

    private static NameTable table = new NameTable();

    private Socket Cliente;

    public Conexion(Socket client) {

        this.Cliente = client;

    }

    public void run() {
        try {
            BufferedReader din = new BufferedReader(new InputStreamReader(Cliente.getInputStream()));
            PrintWriter pout = new PrintWriter(Cliente.getOutputStream());
            String getline = din.readLine();
            System.out.println(getline);

            StringTokenizer st = new StringTokenizer(getline);

            String tag = st.nextToken();   
            if (tag.equals("Busqueda: ")) {
                int index = table.search(st.nextToken());
                System.out.println(index);
                if (index == -1) 
                {
                    pout.println(-1 + " " + "Host nulo");
                } else {
                    pout.println(table.getPort(index) + " " + table.getHostName(index));
                }
            } else if (tag.equals("Insercion: ")) {
                String name = st.nextToken();
                String hostName = st.nextToken();
                int port = Integer.parseInt(st.nextToken());
                int retValue = table.insert(name, hostName, port);
                System.out.println(retValue);
                pout.println(retValue);
            }
            pout.flush();
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            try {
                Cliente.close();
            } catch (Exception e) {
            }
        }

    }

}

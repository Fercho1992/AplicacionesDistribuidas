/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nameclient;

import java.lang.*;
import java.util.*;
import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Name {

  BufferedReader din;
  PrintStream pout;

  public void getSocket() throws IOException {
    Socket server = new Socket(Symbols.nameServer, Symbols.serverPort);
    din = new BufferedReader(new InputStreamReader(server.getInputStream()));
    pout = new PrintStream(server.getOutputStream());

  }

  public int insertName(String name, String hname, int portnum) throws IOException {
    getSocket();
    pout.println("Insertar " + name + " " + hname + " " + portnum);
    pout.flush();
    return Integer.parseInt(din.readLine());
  }

  public PortAddr searchName(String name) throws IOException {
    getSocket();
    pout.println("Buscar " + name);
    pout.flush();
    String result = din.readLine();
    StringTokenizer st = new StringTokenizer(result);
    int portnum = Integer.parseInt(st.nextToken());
    String hname = st.nextToken();
    return new PortAddr(hname, portnum);
  }

  public static void main(String[] args) {

    Name myclient = new Name();
    try {
//        myclient.insertName("hello1", "oak.ece.utexas.edu", 1000);
//        PortAddr pa = myclient.searchName("hello1");
        myclient.insertName("google", "google.com", 2000);
        PortAddr pa = myclient.searchName("google");
        System.out.println(pa.getHostName() + ":" + pa.getPort());

    } catch (Exception e) {
//      System.err.println("Server aborted  :" + e.toString());
      e.printStackTrace();
    }
  }
}

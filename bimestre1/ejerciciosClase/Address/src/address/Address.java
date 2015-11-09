/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package address;

import java.net.*;

/**
 *
 * @author 22B
 */
public class Address {

    public static void main(String[] args) {

        try {

            InetAddress address1;

            address1 = InetAddress.getLocalHost();

            System.out.println("Direccion IP actual  es: ");
            System.out.println(address1.getHostName());
            System.out.println(address1.getHostAddress());
            
            InetAddress compu;
            compu= InetAddress.getByName ("www.epn.edu.ec");
            
            System.out.println("EPN "+compu.getHostAddress());
            
            
        } catch (UnknownHostException e) {

        }
    }

}

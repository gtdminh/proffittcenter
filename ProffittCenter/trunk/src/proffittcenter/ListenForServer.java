/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package proffittcenter;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dave
 */
public class ListenForServer extends Thread  {
    private ServerSocket server;
    private Socket sock;
    private DataInputStream in;
    String inputLine ;
    
    @Override
    public void run(){
        try {
            server = new ServerSocket(6754);
            sock = server.accept();
            BufferedReader inBuf = new BufferedReader(
                                   new InputStreamReader(sock.getInputStream()));
            while(true){
                inputLine = inBuf.readLine();
                System.out.println(inputLine);
            }
        } catch (IOException ex) {
            Logger.getLogger(ListenForServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}

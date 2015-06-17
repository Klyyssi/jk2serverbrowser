
package udp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 *
 * @author Markus Mulkahainen
 */
public class Sender {
    

    /**
     * Send message to given address
     * @param message byte array to send
     * @param serverIp address to send
     * @return received message
     * @throws IOException if failed to send or timeout
     */
    public String sendServer(byte[] message, InetSocketAddress serverIp) throws IOException {
        try (DatagramSocket server = new DatagramSocket(); BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            send(server, message, serverIp.getAddress(), serverIp.getPort());
            server.setSoTimeout(2000);
            return receiveAsString(server);
        }
    }
    
    private void send(DatagramSocket server, byte[] dataOut, InetAddress ip, int port) throws IOException {
        DatagramPacket packetOut = new DatagramPacket(dataOut, dataOut.length, ip, port);
        server.send(packetOut);
    }
    
    private byte[] receive(DatagramSocket server) throws IOException {
        byte[] dataIn = new byte[2048];
        DatagramPacket packetIn = new DatagramPacket(dataIn, dataIn.length); 
        server.receive(packetIn);
        return dataIn;
    }
    
    private String receiveAsString(DatagramSocket server) throws IOException {   
        return new String(receive(server), "UTF-8");
    }    
}

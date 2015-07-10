
package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Arrays;

/**
 *
 * @author Markus Mulkahainen
 */
public class Connection {
    

    /**
     * Send message to given address
     * @param message byte array to send
     * @param serverIp address to send
     * @return received message as string
     * @throws IOException if failed to send or timeout
     */
    public String sendServer(byte[] message, InetSocketAddress serverIp) throws IOException {
        try (DatagramSocket server = new DatagramSocket()) {
            send(server, message, serverIp.getAddress(), serverIp.getPort());
            server.setSoTimeout(2000);
            return receiveAsString(server);
        }
    }
    
    public byte[] sendAndReceiveAsBytes(byte[] message, InetSocketAddress serverIp) throws IOException {
        try (DatagramSocket server = new DatagramSocket()) {
            send(server, message, serverIp.getAddress(), serverIp.getPort());
            server.setSoTimeout(5000);
            return receive(server);
        }
    }
    
    public void send(DatagramSocket server, byte[] dataOut, InetAddress ip, int port) throws IOException {
        DatagramPacket packetOut = new DatagramPacket(dataOut, dataOut.length, ip, port);
        server.send(packetOut);
    }
    
    public byte[] receive(DatagramSocket server) throws IOException {
        byte[] dataIn = new byte[2048];
        DatagramPacket packetIn = new DatagramPacket(dataIn, dataIn.length); 
        server.receive(packetIn);
        return dataIn;
    }
    
    /**
     * Removes empty zero's from end of the file
     * @param bytes
     * @return 
     */
    public static byte[] trim(byte[] bytes) {
        int lastNonZero = 0;
        
        for (int i = bytes.length - 1; i > 0; i--) {
            if (bytes[i] != 0x00) {
                lastNonZero = i + 1;
                break;
            }          
        }
        
        return Arrays.copyOf(bytes, lastNonZero);
    }
    
    private String receiveAsString(DatagramSocket server) throws IOException {   
        return new String(receive(server), "UTF-8");
    }    
}

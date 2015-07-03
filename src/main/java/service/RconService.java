
package service;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import jk2serverbrowser.ByteOperations;
import jk2serverbrowser.Message;
import jk2serverbrowser.Tuple;
import rx.Observable;
import udp.Connection;

/**
 *
 * @author Markus Mulkahainen
 */
public class RconService implements IRconService {

    private final Connection connection = new Connection();
    
    @Override
    public Observable<byte[]> send(byte[] command, byte[] password, Tuple<String, Integer> ip) {
        return Observable.create(subscriber -> {
            new Thread(() -> {
                try (DatagramSocket server = new DatagramSocket()) {
                    connection.send(server, ByteOperations.concat(ByteOperations.concat(Message.RCON_PREFIX, password), command), InetAddress.getByName(ip.x), ip.y);
                    byte[] response = filter(receive(server));
                    
                    while (response.length != 0) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(Connection.trim(response));
                            response = filter(receive(server));
                        }
                    }
                } catch (SocketException ex ) {
                    subscriber.onNext(Message.CONN_FAILURE);
                } catch (IOException ex) {
                    subscriber.onNext(Message.CONN_FAILURE);
                }              
                
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }
            }).start();
        });
    }
    
    private byte[] filter(byte[] response) {
        int LF = findFirst(response, (byte) 0x0a);
        return Arrays.copyOfRange(response, (LF != -1) ? LF + 1 : 0, response.length + 1);
    }
    
    /**
     * Returns the index of the first occurrence of the specified byte in bytearray.
     * @returns index if found, -1 otherwise
     */
    private static int findFirst(byte[] bytes, byte toFind) {        
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == toFind) return i;
        }
        
        return -1;
    }
    
    private byte[] receive(DatagramSocket server) throws IOException {
        return connection.receive(server);
    }
}

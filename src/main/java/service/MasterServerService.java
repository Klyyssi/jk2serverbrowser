
package service;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import jk2serverbrowser.END_OF_FILE;
import jk2serverbrowser.IMasterServerService;
import jk2serverbrowser.MasterServerParser;
import jk2serverbrowser.Message;
import jk2serverbrowser.Tuple;
import rx.Observable;
import udp.Connection;

/**
 *
 * @author Markus Mulkahainen
 */
public class MasterServerService implements IMasterServerService {
    
    private final Connection connection = new Connection();
    
    @Override
    public Observable<List<Tuple<String, Integer>>> getServers(Tuple<String, Integer> ip, String protocol, boolean originalLike) {
        try (DatagramSocket server = new DatagramSocket()) {
            server.setSoTimeout(5000);
            connection.send(server, Message.protocolToMessage(protocol), InetAddress.getByName(ip.x), ip.y);
            List<Tuple<String, Integer>> servers = getServers(originalLike, server);
            System.out.println(" - Masterserver " +ip.x + ":" +ip.y + " listed " +servers.size() + " ip addresses");
            return Observable.just(servers);
        } catch (IOException ex) {
            System.out.println(" - No response from masterserver " +ip.x +":" +ip.y);
        }       
        
        return Observable.just(new ArrayList<>());
    }
    
    private List<Tuple<String, Integer>> getServers(boolean originalLike, DatagramSocket server) throws IOException {       
        Tuple<List<Tuple<String, Integer>>, END_OF_FILE> list = MasterServerParser.parseResponse(connection.receive(server));
        
        if (originalLike && list.y == END_OF_FILE.EOT) {
            list.x.addAll(getServers(originalLike, server));
        }
        
        return list.x;
    }
}

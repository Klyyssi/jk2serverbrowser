
package service;

import java.io.IOException;
import java.net.InetSocketAddress;
import jk2serverbrowser.IGameServerService;
import jk2serverbrowser.Message;
import jk2serverbrowser.ServerStatusParser;
import jk2serverbrowser.Tuple;
import rx.Observable;
import udp.Connection;

/**
 *
 * @author Markus Mulkahainen
 */
public class GameServerService implements IGameServerService {
    
    private final Connection connection = new Connection();

    @Override
    public Observable<Tuple<String[], Long>> getServerInfo(Tuple<String, Integer> ip) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Observable<Tuple<String[], Long>> getServerStatus(Tuple<String, Integer> ip) {
        try {            
            Long latency = System.currentTimeMillis();
            String[] serverStatus = connection.sendServer(Message.GET_STATUS, new InetSocketAddress(ip.x, ip.y)).split("\\r?\\n");
            latency = System.currentTimeMillis() - latency;
            return Observable.just(new Tuple<>(serverStatus, latency));
        } catch (IOException ex) {
            System.out.println(" - Couldn't reach server " +ip.x +":" +ip.y);
        }
        
        return Observable.just(new Tuple<>(ServerStatusParser.emptyServerStatus(ip), 999L));    
    }
    
}

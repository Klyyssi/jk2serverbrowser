
package jk2serverbrowser;

import rx.Observable;

/**
 *
 * @author Markus Mulkahainen
 */
public interface IGameServerService {
    
    Observable<Tuple<String[], Long>> getServerInfo(Tuple<String, Integer> ip);
    
    /**
     * Get server status of JK2/JKA gameserver.
     * 
     * @param ip gameservers ip address
     * @return tuple of serverstatus and latency
     */
    Observable<Tuple<String[], Long>> getServerStatus(Tuple<String, Integer> ip);
    
}


package jk2serverbrowser;

import rx.Observable;

/**
 *
 * @author Markus Mulkahainen
 */
public interface IGameServerService {
    
    Observable<String[]> getServerInfo(Tuple<String, Integer> ip);
    
    Observable<String[]> getServerStatus(Tuple<String, Integer> ip);
    
}

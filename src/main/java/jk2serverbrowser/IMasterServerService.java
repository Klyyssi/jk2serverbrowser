
package jk2serverbrowser;

import java.util.List;
import rx.Observable;

/**
 *
 * @author Markus Mulkahainen
 */
public interface IMasterServerService {
    
    Observable<List<Tuple<String, Integer>>> getServers(MasterServer masterServer);
}

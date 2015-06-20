
package jk2serverbrowser.fixtures;

import java.util.Arrays;
import java.util.List;
import jk2serverbrowser.IMasterServerService;
import jk2serverbrowser.MasterServer;
import jk2serverbrowser.Tuple;
import rx.Observable;

/**
 *
 * @author Markus Mulkahainen
 */
public class MasterServerServiceFixtures implements IMasterServerService {

    @Override
    public Observable<List<Tuple<String, Integer>>> getServers(MasterServer masterServer, boolean originalLike) {
        return Observable.just(Arrays.asList(new Tuple("178.162.194.152", 28070), new Tuple("123.123.123.123", 28090)));
    }
    
}

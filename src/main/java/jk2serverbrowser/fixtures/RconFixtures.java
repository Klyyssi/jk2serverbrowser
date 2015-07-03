
package jk2serverbrowser.fixtures;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import jk2serverbrowser.ByteOperations;
import jk2serverbrowser.Tuple;
import rx.Observable;
import service.IRconService;

/**
 *
 * @author Markus Mulkahainen
 */
public class RconFixtures implements IRconService {

    private static final byte[] PASSWORD = "dog123".getBytes(Charset.forName("UTF-8"));
    
    private static final byte[] PREFIX = ByteOperations.concat("print".getBytes(Charset.forName("UTF-8")), new byte[] { 0x0a });
    
    private static final byte[] BAD_RCONPASSWORD = "Bad rconpassword.\n".getBytes(Charset.forName("UTF-8"));

    @Override
    public Observable<byte[]> send(byte[] command, byte[] password, Tuple<String, Integer> ip) {        

        return Arrays.equals(password, PASSWORD) ? 
                Observable.just(ByteOperations.concat(PREFIX, requestToResponse(new String(command)).getBytes(Charset.forName("UTF-8")))) : 
                Observable.just(ByteOperations.concat(PREFIX, BAD_RCONPASSWORD));
    }
    
    private static String requestToResponse(String request) {
        Map<String, String> response = new HashMap<>();       
        response.put("status", "desann");
        return response.get(request);
    }
}


package jk2serverbrowser;

import java.nio.charset.Charset;
import rx.Observable;
import rx.subjects.PublishSubject;
import service.IRconService;

/**
 *
 * @author Markus Mulkahainen
 */
public class RemoteConsoleController {
    
    private final PublishSubject<String> response = PublishSubject.create();
    private final IRconService rconService;
    
    public RemoteConsoleController(IRconService rconService) {
        this.rconService = rconService;
    }
    
    public void send(String command, String password, String ip, int port) {
        rconService.send(toBytes(command), toBytes(password + " "), new Tuple<>(ip, port)).subscribe(x -> {
            response.onNext(toString(x));
        });
    }
    
    public Observable<String> response() {
        return response;
    }
    
    private static String toString(byte[] bytes) {
        return new String(bytes, Charset.forName("UTF-8"));
    }
    
    private static byte[] toBytes(String string) {
        return string.getBytes(Charset.forName("UTF-8"));
    }
}

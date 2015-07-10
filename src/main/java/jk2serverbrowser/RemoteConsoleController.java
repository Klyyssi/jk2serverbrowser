
package jk2serverbrowser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import rx.Observable;
import rx.subjects.PublishSubject;
import service.IRconService;

/**
 *
 * @author Markus Mulkahainen
 */
public final class RemoteConsoleController {
    
    private final Map<String, String> rconPasswords = new HashMap<>();
    private final PublishSubject<String> response = PublishSubject.create();
    private final PublishSubject<Optional<String>> containsPassword = PublishSubject.create();
    private final IRconService rconService;
    
    public RemoteConsoleController(IRconService rconService) {
        this.rconService = rconService;
        tryReadRconPasswords();
    }
        
    public void trySaveRconPasswords() {
        try (PrintWriter writer = new PrintWriter("rcon.txt", "UTF-8")) {
            rconPasswords.entrySet().forEach(e -> { 
                writer.println(e.getKey() +"|" +e.getValue());
            });
            
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            System.err.println(" - Couldn't save rcon passwords");
        }
    }
    
    private void tryReadRconPasswords() {
        try {
            List<String> rcon = Files.readAllLines(new File("rcon.txt").toPath(), Charset.forName("UTF-8"));
            for (String s : rcon) {                
                String[] pieces = s.split("\\|");
                rconPasswords.put(pieces[0], pieces[1]);
            }
        } catch (IOException ex) {
            System.err.println(" - rcon.txt not found");
        }
    }
    
    public void getPassword(String forIp) {     
        containsPassword.onNext(Optional.ofNullable(rconPasswords.get(forIp)));   
    }
    
    public void setPassword(String password, String forIp) {
        rconPasswords.put(forIp, password);
    }
    
    public void removePassword(String forIp) {
        rconPasswords.remove(forIp);
    }
    
    public void send(String command, String password, String ip, int port) {
        rconService.send(toBytes(command), toBytes(password), new Tuple<>(ip, port)).subscribe(x -> {
            response.onNext(toString(x));
        });
    }
    
    public Observable<Optional<String>> containsPassword() {
        return containsPassword;
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

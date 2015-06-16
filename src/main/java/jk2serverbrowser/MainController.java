
package jk2serverbrowser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JOptionPane;
import rx.Observable;
import settings.Setting;
import settings.SettingsManager;

/**
 *
 * @author Markus Mulkahainen
 */
public class MainController {
    
    private final String SETTINGS_FILE = "settings.ini";   
    private MasterServer masterServer = MasterServer.JK2_104_ORIGINAL;    
    private final List<GameServer> favourites = new ArrayList<>();  
    private final List<GameServer> servers = Collections.synchronizedList(new ArrayList<>());   
    private final IGameServerService gameService;
    private final IMasterServerService masterService;
    
    private SettingsManager settingsManager;
    
    public MainController(IMasterServerService masterServerService, IGameServerService gameServerService) {
        masterService = masterServerService;
        gameService = gameServerService;
    }
    
    public void loadSettings() {
        readFavourites();
        settingsManager = new SettingsManager();
        settingsManager.loadSettings(SETTINGS_FILE);
    }
    
    public void trySaveFavourites() {
        try {
            try (PrintWriter writer = new PrintWriter("favourites.txt", "UTF-8")) {
                for (GameServer s : favourites) {
                    writer.println(s.getIp().getAddress().getHostAddress() +":" +s.getIp().getPort());
                }
            }
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            System.err.println(" - Couldn't save favourites");
        }
    }
    
    private void readFavourites() {
        try {
            List<String> favs = Files.readAllLines(new File("favourites.txt").toPath(), Charset.forName("UTF-8"));
            
            for (String s : favs) {                
                String[] pieces = s.split(":");
                InetSocketAddress isa = new InetSocketAddress(pieces[0], Integer.parseInt(pieces[1]));
                GameServer g = new GameServer(isa);   
                favourites.add(g);
            }
        } catch (IOException ex) {
            System.err.println(" - favourites.txt not found");
        }
    }
    
    public SettingsManager getSettings() {
        return settingsManager;
    }
    
    public List<GameServer> getFavourites() {
        return favourites;
    }
    
    public List<GameServer> getServers() {
        return servers;
    }
    
    public void setMasterServer(MasterServer masterServer) {
        this.masterServer = masterServer;
    }
    
    public Observable<String[]> getServerStatus(GameServer server) {       
        return gameService.getServerStatus(new Tuple(server.getIp().getAddress().toString(), server.getIp().getPort()));
    }
    
    public void refreshFavourites() {
        favourites.forEach(server -> {
            gameService.getServerStatus(new Tuple(server.getIp().getAddress().toString(), server.getIp().getPort())).subscribe(s -> {
               server.setServerStatus((String[]) s);
            });
        });
    }
    
    public void joinServer(GameServer server, boolean jk2) throws IOException {
        String path = jk2 ? settingsManager.getSetting(Setting.JK2PATH) : settingsManager.getSetting(Setting.JKAPATH);                
        String strIp = server.getIp().getAddress().toString();
        ProcessBuilder builder = new ProcessBuilder( path, "+connect", strIp.substring(strIp.indexOf("/") + 1, strIp.length()) +":" +server.getIp().getPort());                
        builder.directory( new File(path.substring(0, path.lastIndexOf("/"))) );
        builder.redirectErrorStream(true);
        Process process =  builder.start();        
    }
    
    public Observable<GameServer> getNewServerList() {
        servers.clear();
        
        return Observable.create(subscriber -> {
            new Thread(() -> {
                masterService.getServers(masterServer).subscribe(list -> {
                    list.parallelStream().forEach(ipTuple -> {
                        if (!subscriber.isUnsubscribed()) {
                            gameService.getServerStatus(ipTuple).subscribe(serverStatus -> {
                                GameServer server = statusToServer(serverStatus, ipTuple);
                                servers.add(server);
                                subscriber.onNext(server);
                           });                      
                        }
                   });
                });
                
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }
            }).start();
        });
    }
    
    private static GameServer statusToServer(String[] serverStatus, Tuple<String, Integer> ip) {
        GameServer server = new GameServer(new InetSocketAddress(ip.x, ip.y));
        server.setServerStatus(serverStatus);
        return server;
    }
    
}

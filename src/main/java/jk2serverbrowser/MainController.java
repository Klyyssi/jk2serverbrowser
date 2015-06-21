
package jk2serverbrowser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import rx.Observable;
import settings.Setting;
import settings.SettingsManager;

/**
 *
 * @author Markus Mulkahainen
 */
public class MainController {
    
    private final String SETTINGS_FILE = "settings.ini";   
    private final List<GameServer> favourites = new ArrayList<>();
    private final List<GameServer> servers = Collections.synchronizedList(new ArrayList<>());   
    private final IGameServerService gameService;
    private final IMasterServerService masterService;  
    private SettingsManager settingsManager;
    private MasterServer masterServer = MasterServer.JK2_104_ORIGINAL;    

    
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
                    writer.println(s.getIp() +":" +s.getPort());
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
                Tuple<String, Integer> ip = new Tuple<>(pieces[0], Integer.parseInt(pieces[1]));
                favourites.add(ServerStatusParser.statusToServer(ServerStatusParser.emptyServerStatus(ip), ip, 999L));
            }
        } catch (IOException ex) {
            System.err.println(" - favourites.txt not found");
        }
    }
    
    public SettingsManager getSettings() {
        return settingsManager;
    }
    
    public List<GameServer> getServers() {
        return servers;
    }
    
    public void setMasterServer(MasterServer masterServer) {
        this.masterServer = masterServer;
    }
    
    public void addToFavourites(GameServer server) {
        if (!favourites.contains(server)) favourites.add(server);
    }
    
    public void removeFromFavourites(GameServer server) {
        favourites.remove(server);
    }
    
    public List<GameServer> getFavourites() {
        return favourites;
    }
    
    public Observable<GameServer> refresh(GameServer server) {      
        return gameService.getServerStatus(new Tuple<>(server.getIp(), server.getPort())).map(status -> {
            return ServerStatusParser.statusToServer(status.x, new Tuple(server.getIp(), server.getPort()), status.y);
        });
    }
    
    public Observable<GameServer> refreshFavourites() {
        return Observable.create(subscriber -> {
            new Thread(() -> {
                favourites.forEach(x -> {
                    gameService.getServerStatus(new Tuple<>(x.getIp(), x.getPort())).subscribe(s -> {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(ServerStatusParser.statusToServer(s.x, new Tuple<>(x.getIp(), x.getPort()), s.y));
                        }
                    });
                });
                
                if(!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }
            }).start();
        });
    }
    
    public void joinServer(GameServer server) throws IOException {
        String path = isJk2MasterServer(masterServer) ? settingsManager.getSetting(Setting.JK2PATH) : settingsManager.getSetting(Setting.JKAPATH);                
        String strIp = server.getIp();
        ProcessBuilder builder = new ProcessBuilder( path, "+connect", strIp.substring(strIp.indexOf("/") + 1, strIp.length()) +":" +server.getPort());                
        builder.directory( new File(path.substring(0, path.lastIndexOf("/"))) );
        builder.redirectErrorStream(true);
        builder.start();        
    }
    
    public Observable<GameServer> getNewServerList() {
        servers.clear();
                
        return Observable.create(subscriber -> {
            new Thread(() -> {
                masterService.getServers(masterServerToTuple(masterServer), masterServer.protocol, isOriginalLike(masterServer)).subscribe(list -> {
                    list.parallelStream().forEach(ipTuple -> {
                        gameService.getServerStatus(ipTuple).subscribe(serverStatus -> {
                            if (!subscriber.isUnsubscribed()) {                               
                                GameServer server = ServerStatusParser.statusToServer(serverStatus.x.length < 3 ? ServerStatusParser.emptyServerStatus(ipTuple) : serverStatus.x, ipTuple, serverStatus.y);
                                servers.add(server);
                                subscriber.onNext(server);
                            }
                        });                                             
                    });
                });
                
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }
            }).start();
        });
    }  
    
    private Tuple<String, Integer> masterServerToTuple(MasterServer masterServer) {
        return new Tuple<>(masterServer.ip.equals("N/A") ? settingsManager.getSetting(Setting.CUSTOM_MASTERSERVER_IP) : masterServer.ip, 
                masterServer.port.equals("N/A") ? Integer.parseInt(
                        isJk2MasterServer(masterServer) ? 
                                settingsManager.getSetting(Setting.CUSTOM_MASTERSV_JK2_PORT) : settingsManager.getSetting(Setting.CUSTOM_MASTERSV_JKA_PORT)) 
                        : Integer.parseInt(masterServer.port));
    }
    
    private boolean isJk2MasterServer(MasterServer masterServer) {
        return masterServer.toString().startsWith("JK2");
    }
    
    private boolean isOriginalLike(MasterServer masterServer) {
        List<MasterServer> originalLikeServers = new ArrayList<>();
        originalLikeServers.add(MasterServer.JA_100_ORIGINAL);
        originalLikeServers.add(MasterServer.JA_101_ORIGINAL);
        originalLikeServers.add(MasterServer.JK2_102_ORIGINAL);
        originalLikeServers.add(MasterServer.JK2_104_ORIGINAL);

        return originalLikeServers.contains(masterServer);
    }
}

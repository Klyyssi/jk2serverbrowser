
package jk2serverbrowser;

import java.net.InetSocketAddress;
import java.util.Objects;

/**
 *
 * @author Markus Mulkahainen
 */
public class GameServer {
    
    private InetSocketAddress ip;
    private String mod = "", force_disable = "", weapon_disable = "", gametype = "", maxclients = "0", clients = "0", mapname = "", hostname = "", ping = "", players = "0";    
    
    public GameServer(InetSocketAddress ip) {
        this.ip = ip;
        hostname = ip.toString();
    }
    
    public void setServerStatus(String[] array) {
        String[] serverStatus = array[1].split("\\\\");
        for (int i = 0; i < serverStatus.length; i++) {
            switch (serverStatus[i]) {
                case "gamename": 
                    mod = serverStatus[i+1].replaceAll("\\^[0-9]", "");
                    break;
                case "g_forcePowerDisable": 
                    force_disable = serverStatus[i+1];
                    break;
                case "g_weapondisable": 
                    weapon_disable = serverStatus[i+1];
                    break;
                case "g_gametype": 
                    switch (serverStatus[i+1]) {
                        case ("0"):
                            gametype = "FFA";
                            break;
                        case ("1"):
                            gametype = "Holocron";
                            break;
                        case ("2"):
                            gametype = "Jedi master";
                            break;
                        case ("3"):
                            gametype = "Duel";
                            break;
                        case ("5"):
                            gametype = "TFFA";
                            break;
                        case ("7"):
                            gametype = "CTF";
                            break;
                        case ("8"):
                            gametype = "CTY";
                            break;
                    }     
                break;
                case "sv_maxclients":
                    maxclients = serverStatus[i+1];
                    break;                
                case "mapname":
                    mapname = serverStatus[i+1];
                    break;
                case "sv_hostname":
                    hostname = serverStatus[i+1];
                    hostname = hostname.replaceAll("\\^[8-9]", "").replaceAll("[^a-zA-Z0-9?=@><#_'!&\\]\\[\\(\\)\\-\\.`~\\*\\^ ]", "");   
                    //hostname = hostname.replaceAll("\\\\", "");
                    hostname = addHTMLColorTags(hostname);
            }
            clients = Integer.toString(array.length - 3);
            players = countPlayers(array);
        }
    }
    
    private String addHTMLColorTags(String s) {
        return ColorTagger.htmlize(s);
    }
    
    private String countPlayers(String[] array) {
        int c = 0;
        for (String s : array) {           
            if (s.matches("-?[0-9]+ ([1-9]|[0-9]{2,}) \".*\"")) c++;        
        }
        return Integer.toString(c);
    }
    
    @Override
    public String toString() {
        return "Hostname: " +hostname + "\nIP: " +ip.getAddress() +":" +ip.getPort() +"\nPlayers: " +clients +"/" +maxclients +"\n";
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof GameServer)) return false;
        GameServer gs = (GameServer) o;
        return this.hashCode() == gs.hashCode();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.ip);
        return hash;
    }
    
    public String getHostname() {
        return hostname;
    }
    
    public String getHostnameNoHTML() {
        return hostname.replaceAll("\\<[^>]*>","");
    }

    public InetSocketAddress getIp() {
        return ip;
    }

    public String getMod() {
        return mod;
    }

    public String getForce_disable() {
        return force_disable;
    }

    public String getWeapon_disable() {
        return weapon_disable;
    }

    public String getGametype() {
        return gametype;
    }

    public String getMaxclients() {
        return maxclients;
    }

    public String getClients() {
        return clients;
    }

    public String getMapname() {
        return mapname;
    }  
    
    public String getPlayerCount() {
        return players;
    }
    
    public void setPing(long latency) {
        ping = Long.toString(latency);
    }
    
    public String getPing() {
        return ping;
    }
}

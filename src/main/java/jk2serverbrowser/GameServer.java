
package jk2serverbrowser;

import java.util.Objects;

/**
 *
 * @author Markus Mulkahainen
 */
public class GameServer {
    
    private final String 
            ip,
            mod, 
            force_disable, 
            weapon_disable, 
            gametype, 
            maxclients, 
            clients, 
            mapname, 
            hostname, 
            ping, 
            players;
    
    private final int port;
    
    public GameServer(String ip, int port, String mod, String force_disable, String weapon_disable, String gametype, String maxclients, String clients, String mapname, String hostname, String ping, String players) {
        this.ip = ip;
        this.port = port;
        this.mod = mod;
        this.force_disable = force_disable;
        this.weapon_disable = weapon_disable;
        this.gametype = gametype;
        this.maxclients = maxclients;
        this.clients = clients;
        this.mapname = mapname;
        this.hostname = hostname;
        this.ping = ping;
        this.players = players;
    }
    
    @Override
    public String toString() {
        return "Hostname: " +hostname + "\nIP: " +ip +":" +port +"\nPlayers: " +clients +"/" +maxclients +"\n";
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
        hash = 89 * hash + Objects.hashCode(this.ip) + this.port;
        return hash;
    }
    
    public String getHostname() {
        return hostname;
    }
    
    public String getHostnameNoHTML() {
        return hostname.replaceAll("\\<[^>]*>","");
    }

    public String getIp() {
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
    
    public String getPing() {
        return ping;
    }
    
    public int getPort() {
        return port;
    }
}

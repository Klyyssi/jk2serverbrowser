
package jk2serverbrowser;

/**
 *
 * @author Markus Mulkahainen
 */
public class ServerFilter {
    
    private final String key;
    private final boolean hideBotAndEmptyServers;
    private final boolean hideEmptyServers;
    
    public ServerFilter(String key, boolean hideBotAndEmptyServers, boolean hideEmptyServers) {
        this.key = key;
        this.hideBotAndEmptyServers = hideBotAndEmptyServers;
        this.hideEmptyServers = hideEmptyServers;
    }   
    
    public boolean filter(GameServer server) {
        return ((hideEmptyServers && Integer.parseInt(server.getClients()) > 0) || !hideEmptyServers) &&
                ((hideBotAndEmptyServers && Integer.parseInt(server.getPlayerCount()) > 0 && Integer.parseInt(server.getClients()) > 0) || !hideBotAndEmptyServers) &&
                (ColorTagger.ignoreColours(server.getHostname()).toLowerCase().contains(key) ||
                server.getIp().contains(key) ||
                server.getForce_disable().contains(key) ||
                server.getWeapon_disable().contains(key) ||
                server.getGametype().toLowerCase().contains(key) ||
                server.getMapname().toLowerCase().contains(key) ||
                server.getPlayers().stream().anyMatch(p -> ColorTagger.ignoreColours(p.getName()).toLowerCase().contains(key)));
    }
    
    public static ServerFilter emptyFilter() {
        return new ServerFilter("", false, false);
    }
}

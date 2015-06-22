
package jk2serverbrowser;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Markus Mulkahainen
 */
public class ServerStatusParser {
    
    //String ip, int port, String mod, String force_disable, String weapon_disable, String gametype, String maxclients, String clients, String mapname, String hostname, String ping, String players

    
    public static GameServer statusToServer(String[] serverStatus, Tuple<String, Integer> ip, Long ping) {
        String[] parsedStatus = parseStatus(serverStatus);       
        return new GameServer(ip.x, ip.y, parsedStatus[0], parsedStatus[1], parsedStatus[2], parsedStatus[3], parsedStatus[4], parsedStatus[5], parsedStatus[6], parsedStatus[7], Long.toString(ping), parsePlayers(serverStatus));
    }   
    
    public static String[] emptyServerStatus(Tuple<String, Integer> forIp) {
        return new String[] { "getServerStatusResponse", "\\version\\JK2MP: v1.04mv linux-amd64 May 29 2015\\sv_maxclients\\0\\g_maxGameClients\\0\\g_weapondisable\\0\\g_forcePowerDisable\\0\\sv_floodProtect\\0\\sv_maxPing\\0\\sv_minPing\\0\\sv_maxRate\\100000\\sv_hostname\\"+forIp.x +"\\g_gametype\\0\\g_duelWeaponDisable\\0\\timelimit\\0\\g_needpass\\0\\mapname\\NA\\sv_privateClients\\0\\bot_minplayers\\0\\gamename\\NA", "" };
    }
    
    private static List<Player> parsePlayers(String[] serverStatus) {
        List<Player> players = new ArrayList<>();
        
        for (String s : serverStatus) {
            if (s.matches("-?[0-9]+ [0-9]+ \".*\"")) players.add(playerFromStatus(s));
        }
        
        return players;
    }
    
    private static Player playerFromStatus(String lineToParse) {
        String[] pieces = lineToParse.split(" ");
        return new Player(lineToParse.substring(lineToParse.indexOf("\""), lineToParse.lastIndexOf("\"")).replaceAll("\"", "").replaceAll("\\^[a-z]", ""), pieces[0], pieces[1]);
    }
        
    private static String[] parseStatus(String[] array) {       
        String[] parsedStatus = new String[10];               
        String[] serverStatus = array[1].split("\\\\");
        for (int i = 0; i < serverStatus.length; i++) {
            switch (serverStatus[i]) {
                case "gamename": 
                    parsedStatus[0] = serverStatus[i+1].replaceAll("\\^[0-9]", "");
                    break;
                case "g_forcePowerDisable": 
                    parsedStatus[1] = serverStatus[i+1];
                    break;
                case "g_weapondisable": 
                    parsedStatus[2] = serverStatus[i+1];
                    break;
                case "g_gametype": 
                    switch (serverStatus[i+1]) {
                        case ("0"):
                            parsedStatus[3] = "FFA";
                            break;
                        case ("1"):
                            parsedStatus[3] = "Holocron";
                            break;
                        case ("2"):
                            parsedStatus[3] = "Jedi master";
                            break;
                        case ("3"):
                            parsedStatus[3] = "Duel";
                            break;
                        case ("5"):
                            parsedStatus[3] = "TFFA";
                            break;
                        case ("7"):
                            parsedStatus[3] = "CTF";
                            break;
                        case ("8"):
                            parsedStatus[3] = "CTY";
                            break;
                    }     
                break;
                case "sv_maxclients":
                    parsedStatus[4] = serverStatus[i+1];
                    break;                
                case "mapname":
                    parsedStatus[6] = serverStatus[i+1];
                    break;
                case "sv_hostname":
                    parsedStatus[7] = serverStatus[i+1].replaceAll("\\^[8-9]", "").replaceAll("[^a-zA-Z0-9?=@><#_'!&\\]\\[\\(\\)\\-\\.`~\\*\\^ ]", "");   
                    //hostname = hostname.replaceAll("\\\\", "");
                    //hostname = addHTMLColorTags(hostname);
            }
            parsedStatus[5] = Integer.toString(array.length - 3);            
        }
        
        return parsedStatus;

    }   
}

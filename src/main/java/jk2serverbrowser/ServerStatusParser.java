
package jk2serverbrowser;

/**
 *
 * @author Markus Mulkahainen
 */
public class ServerStatusParser {
    
    //String ip, int port, String mod, String force_disable, String weapon_disable, String gametype, String maxclients, String clients, String mapname, String hostname, String ping, String players

    
    public static GameServer statusToServer(String[] serverStatus, Tuple<String, Integer> ip) {
        String[] parsedStatus = parseStatus(serverStatus);       
        return new GameServer(ip.x, ip.y, parsedStatus[0], parsedStatus[1], parsedStatus[2], parsedStatus[3], parsedStatus[4], parsedStatus[5], parsedStatus[6], parsedStatus[7], parsedStatus[8], parsedStatus[9]);
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
                    parsedStatus[7] = addHTMLColorTags(serverStatus[i+1].replaceAll("\\^[8-9]", "").replaceAll("[^a-zA-Z0-9?=@><#_'!&\\]\\[\\(\\)\\-\\.`~\\*\\^ ]", ""));   
                    //hostname = hostname.replaceAll("\\\\", "");
                    //hostname = addHTMLColorTags(hostname);
                    break;
                case "ping":
                    parsedStatus[8] = serverStatus[i+1];
            }
            parsedStatus[5] = Integer.toString(array.length - 3);
            parsedStatus[9] = countPlayers(array);
            
        }
        
        return parsedStatus;

    }
    
    private static String addHTMLColorTags(String s) {
        return ColorTagger.htmlize(s);
    }
    
    private static String countPlayers(String[] array) {
        int c = 0;
        for (String s : array) {           
            if (s.matches("-?[0-9]+ ([1-9]|[0-9]{2,}) \".*\"")) c++;        
        }
        return Integer.toString(c);
    }
}

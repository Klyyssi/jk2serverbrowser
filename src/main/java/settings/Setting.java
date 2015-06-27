
package settings;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains single setting with default value.
 * 
 * @author Incubo
 * @author Markus Mulkahainen
 */
public enum Setting { 
    
    JK2PATH ("C:\\Games\\Star Wars JK II Jedi Outcast\\GameData\\jk2mp.exe"), 
    JKAPATH ("C:\\Games\\Star Wars Jedi Academy\\GameData\\jkamp.exe"),         
    SELECTED_MASTERSERVER ("0"),
    SELECTED_VERSION ("0"),
    SELECTED_FILTER ("0"),         
    CUSTOM_MASTERSERVER_IP ("master.jkhub.org"), 
    CUSTOM_MASTERSV_JK2_PORT ("28060"), 
    CUSTOM_MASTERSV_JKA_PORT ("29060");
    
    public final String defaultValue;
    
    Setting(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public static Map<String, String> masterServers() {
        Map<String, String> masterServers = new HashMap<>();
        
        masterServers.put("0", "Original");
        masterServers.put("1", "Ouned");
        masterServers.put("2", "jkhub");
        masterServers.put("3", "Custom");
        
        return masterServers;
    }
    
    public static Map<String, String> gameversions() {
        Map<String, String> gameversions = new HashMap<>();
        
        gameversions.put("0", "JK2 1.04");
        gameversions.put("1", "JK2 1.02");
        gameversions.put("2", "JA 1.00");
        gameversions.put("3", "JA 1.01");
        
        return gameversions;
    }
    
    public static Map<String, String> filters() {
        Map<String, String> filters = new HashMap<>();
        
        filters.put("0", "No filters");
        filters.put("1", "Empty");
        filters.put("2", "Empty & Bots");
        
        return filters;
    }
}

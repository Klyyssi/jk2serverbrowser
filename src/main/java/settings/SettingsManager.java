package settings;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Server browser uses these settings while querying the servers. 
 * These settings are first set by settings.ini, 
 * after that they can be set by the GUI.
 * 
 * @author Incubo
 * @author Markus Mulkahainen
 */
public class SettingsManager {
        
	private static final String COMMENT_STR = "#";
        
        /**
         * Map a setting to its value
         */
	private HashMap<Setting, String> settingsMap = new HashMap<Setting, String>();	
        
	public void loadSettings(String file) {
		File cfgFile = new File(file);
                                
		try {
			List<String> lines = Files.readAllLines(cfgFile.toPath(), Charset.forName("UTF-8"));
                        
                        for (Setting setting : Setting.values()) {
                            
                            //This is not the most efficient way, but there are only a few options
                            for (String line : lines) {
                                
                                // Skip comments or empty lines
				if (line.startsWith(COMMENT_STR) || line.isEmpty()) continue; 
                                
                                // in case user has used \ instead of /, we need to fix it since java can't open the file
				String[] tokens = line.replaceAll("\\\\", "/").split(" *= *");
                                
                                // if setting was found in settings file
                                if (setting.toString().equals(tokens[0])) {
                                    settingsMap.put(setting, tokens[1]);
                                }
                            }
                            
                            //if the setting was not set, we use the default value for it
                            if (!settingsMap.containsKey(setting)) {
                                settingsMap.put(setting, setting.defaultValue);
                            }
                            
                        }
		} catch (IOException e) {
                    System.out.println(" - " +file +" not found or a problem while loading settings - using default settings");
                    
                    SetDefaultSettings();
		}
	}
        
        private void SetDefaultSettings() {
            for (Setting setting : Setting.values()) {
                settingsMap.put(setting, setting.defaultValue);
            }
        }
	
	public String getSetting(Setting key) {
		return settingsMap.get(key);
	}
        
        /**
         * Set a specific setting.
         * Needed in GUI.
         * It is also needed in case we create view for settings.
         * 
         * @param key settings key
         * @param value the setting value for the key
         * @return true if setting was found and set, false otherwise
         */
        public boolean setSetting(Setting key, String value) {
            if (settingsMap.containsKey(key)) {
                settingsMap.put(key, value);
            }
            
            return false;
        }

	public void printSettings() {
		System.out.println("-- Settings: ");
		for(Entry<Setting, String> entry : settingsMap.entrySet()) {
			System.out.println(entry.getKey() + " = " + entry.getValue());
		}
	}
        
        public Map<Setting, String> getSettings() {
            return settingsMap;
        }
}


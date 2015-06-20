
package settings;

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
    SELECTED_BOTFILTER ("0"),         
    CUSTOM_MASTERSERVER_IP ("master.jkhub.org"), 
    CUSTOM_MASTERSV_JK2_PORT ("28060"), 
    CUSTOM_MASTERSV_JKA_PORT ("29060");
    
    public final String defaultValue;
    
    Setting(String defaultValue) {
        this.defaultValue = defaultValue;
    }

}

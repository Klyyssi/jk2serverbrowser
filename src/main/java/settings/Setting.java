
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
    DEFAULT_SELECTED_MASTERSERVER ("1"),
    DEFAULT_SELECTED_VERSION ("0"),
    DEFAULT_SELECTED_BOTFILTER ("0"),         
    CUSTOM_MASTERSERVER_IP ("93.188.162.182"), 
    CUSTOM_MASTERSV_JK2_PORT ("28060"), 
    CUSTOM_MASTERSV_JKA_PORT ("29060"),
    
    //actual settings that are used by serverinfogetter
    MASTERSERVER ("masterjk2.ravensoft.com"),
    PORT ("28060"), 
    PROTOCOL ("16");
    
    public final String defaultValue;
    
    Setting(String defaultValue) {
        this.defaultValue = defaultValue;
    }

}

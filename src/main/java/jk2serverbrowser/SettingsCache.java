
package jk2serverbrowser;

import java.util.HashMap;
import java.util.Map;
import rx.Observable;
import rx.subjects.PublishSubject;
import settings.Setting;
import settings.SettingsManager;

/**
 *  
 * @author Markus Mulkahainen
 */
public class SettingsCache {
    
    private final PublishSubject<Boolean> hasChanged = PublishSubject.create();
    private final SettingsManager settingsManager;
    private Map<Setting, String> settingsCache;
    
    public SettingsCache(SettingsManager settings) {
        settingsCache = new HashMap<>(settings.getSettings());
        settingsManager = settings;
    }
    
    public void apply() {
        settingsCache.forEach((x,y) -> {
            settingsManager.setSetting(x, y);
        });
        
        hasChanged.onNext(!settingsManager.getSettings().equals(settingsCache));
    }
    
    public void set(Setting setting, String value) {
        settingsCache.put(setting, value);
                
        hasChanged.onNext(!settingsCache.equals(settingsManager.getSettings()));
    }
    
    public void discard() {
        settingsCache = new HashMap<>(settingsManager.getSettings());
    }
    
    public Observable<Boolean> hasChanged() {
        return hasChanged;
    }
}

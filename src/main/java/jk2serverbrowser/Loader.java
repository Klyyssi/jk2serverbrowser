
package jk2serverbrowser;

import javax.swing.SwingUtilities;
import static jk2serverbrowser.Gui.createGUI;
import service.GameServerService;
import service.MasterServerService;

/**
 *
 * @author Markus Mulkahainen
 */
public class Loader {

    public static void main(String[] args) {
        
        MainController controller = new MainController(new MasterServerService(), new GameServerServiceFixtures());
        controller.loadSettings();
        
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createGUI(controller);
            }
        });
    }
    
}

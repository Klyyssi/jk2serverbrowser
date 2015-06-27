
package jk2serverbrowser;

import javax.swing.SwingUtilities;
import static jk2serverbrowser.Gui.createGUI;
import jk2serverbrowser.fixtures.GameServerServiceFixtures;
import jk2serverbrowser.fixtures.MasterServerServiceFixtures;
import service.GameServerService;
import service.MasterServerService;

/**
 *
 * @author Markus Mulkahainen
 */
public class Loader {

    public static void main(String[] args) {
        IMasterServerService masterService;
        IGameServerService gameService;
        
        if (args.length > 0 && args[0].equals("offline")) {
            masterService = new MasterServerServiceFixtures();
            gameService = new GameServerServiceFixtures();
        } else {
            masterService = new MasterServerService();
            gameService = new GameServerService();
        }
        
        MainController controller = new MainController(masterService, gameService);
        controller.loadSettings();
        
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createGUI(controller);
            }
        });
    }
    
}

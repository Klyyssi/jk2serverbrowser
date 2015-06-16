
package jk2serverbrowser;

import javax.swing.SwingUtilities;
import static jk2serverbrowser.Gui.createGUI;
import jk2serverbrowser.fixtures.GameServerServiceFixtures;
import jk2serverbrowser.fixtures.MasterServerServiceFixtures;

/**
 *
 * @author Markus Mulkahainen
 */
public class Loader {

    public static void main(String[] args) {
        
        MainController controller = new MainController(new MasterServerServiceFixtures(), new GameServerServiceFixtures());
        controller.loadSettings();
        
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createGUI(controller);
            }
        });
    }
    
}

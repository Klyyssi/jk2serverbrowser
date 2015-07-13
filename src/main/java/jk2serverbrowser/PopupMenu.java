
package jk2serverbrowser;

import java.io.IOException;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import service.RconService;

/**
 * Context menu for mouse2-click on server table
 * @author Markus Mulkahainen
 */
public class PopupMenu extends JPopupMenu {
    
    private final JMenuItem rcon;
    private final JMenuItem join;
    private final JMenuItem guard;
    private final JMenuItem favourite;
    private final JMenuItem deleteFavourite;
    private final Gui maingui;
    private final MainController controller;
    
    public PopupMenu(Gui gui, MainController controller) {
        this.controller = controller;
        maingui = gui;
        join = new JMenuItem("Join server");
        rcon = new JMenuItem("Remote console");
        guard = new JMenuItem("Add server guard");
        favourite = new JMenuItem("Add to favourites");
        deleteFavourite = new JMenuItem("Delete favourite");
        deleteFavourite.setEnabled(false);
        
        rcon.addActionListener(x -> {
            //RemoteConsole console = new RemoteConsole(gui, controller.getRconController(), gui.getSelectedServer().getIp(), Integer.toString(gui.getSelectedServer().getPort()));
        });
        join.addActionListener(x -> {
            if (maingui.getSelectedServer() != null) {               
                try {
                    maingui.destroyServerGuards();
                    controller.joinServer(maingui.getSelectedServer()); 
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Couldn't find path to game folder. Please, check your settings.ini.");
                }
            }
        });
        guard.addActionListener(x -> {
            newServerGuard();
        });
        favourite.addActionListener(x -> {
            controller.addToFavourites(maingui.getSelectedServer());
        });
        deleteFavourite.addActionListener(x -> {
            controller.removeFromFavourites(maingui.getSelectedServer());
            maingui.refreshFavourites();
        });
        
        add(join);
        add(favourite);
        add(rcon);
        add(guard);
        add(deleteFavourite);
    }
    
    public void setDeleteFavourite(boolean enabled) {
        deleteFavourite.setEnabled(enabled);
    }
    
    private void newServerGuard() {
        GameServer s = maingui.getSelectedServer();
        if (s != null) 
            maingui.addServerGuard(new ServerGuard(s, controller, maingui));
    }
}


package jk2serverbrowser;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

/**
 * Context menu for mouse2-click on server table
 * @author Markus Mulkahainen
 */
public class PopupMenu extends JPopupMenu implements MouseListener {
    private JMenuItem join;
    private JMenuItem guard;
    private JMenuItem favourite;
    private JMenuItem deleteFavourite;
    private Gui maingui;
    private MainController controller;
    
    public PopupMenu(Gui gui, MainController controller) {
        this.controller = controller;
        maingui = gui;
        join = new JMenuItem("Join server");
        guard = new JMenuItem("Add server guard");
        favourite = new JMenuItem("Add to favourites");
        deleteFavourite = new JMenuItem("Delete favourite");
        deleteFavourite.setEnabled(false);
        join.addMouseListener(this);
        guard.addMouseListener(this);   
        favourite.addMouseListener(this);
        deleteFavourite.addMouseListener(this);
        add(join);
        add(favourite);
        add(guard);
        add(deleteFavourite);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e == null || !(e.getSource() instanceof JMenuItem)) return;
        JMenuItem clicked = (JMenuItem) e.getSource();
        if (clicked == guard) {
            newServerGuard();
        } else if (clicked == join) {
            if (maingui.getSelectedServer() != null) {               
                try {
                    maingui.destroyServerGuards();
                    controller.joinServer(maingui.getSelectedServer(), true); 
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Couldn't find path to game folder. Please, check your settings.ini.");
                }
            }
        } else if (clicked == favourite) {
            if (!controller.getFavourites().contains(maingui.getSelectedServer())) {
                controller.getFavourites().add(maingui.getSelectedServer());
            }
        } else if (clicked == deleteFavourite) {
            controller.getFavourites().remove(maingui.getSelectedServer());
            maingui.clearTable();
            //maingui.showServerlist();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
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

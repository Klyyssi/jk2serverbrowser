
package jk2serverbrowser;

import java.awt.EventQueue;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.WindowConstants;

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
    private ServerBrowser browser;
    private List<GameServer> favourites;
    
    public PopupMenu(Gui gui, ServerBrowser browser, List<GameServer> favourites) {
        this.browser = browser;
        this.favourites = favourites;
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
                maingui.joinServer(maingui.getSelectedServer());     
            }
        } else if (clicked == favourite) {
            if (!favourites.contains(maingui.getSelectedServer())) {
                favourites.add(maingui.getSelectedServer());
            }
        } else if (clicked == deleteFavourite) {
            favourites.remove(maingui.getSelectedServer());
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
            maingui.addServerGuard(new ServerGuard(s, browser, maingui));
    }
}

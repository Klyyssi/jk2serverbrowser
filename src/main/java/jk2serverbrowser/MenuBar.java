
package jk2serverbrowser;

import java.awt.event.KeyEvent;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 *
 * @author Markus Mulkahainen
 */
public class MenuBar extends JMenuBar {
    
    private final JFrame parent;
    private final MainController controller;
    
    public MenuBar(JFrame frame, MainController controller) {
        super();        
        
        parent = frame;
        this.controller = controller;
        
        add(createFileMenu());
        add(createViewMenu());
        add(createToolsMenu());
        add(createHelpMenu());
    }
    
    private JMenu createFileMenu() {
        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);
        
        //direct ip connect
        JMenuItem connect = new JMenuItem("Connect to ip", KeyEvent.VK_C);
        connect.addActionListener(x -> { 
            String input = (String) JOptionPane.showInputDialog(parent, 
                    "eg. 123.123.123.123:28090", "Enter IP and port [" + (controller.getSelectedMasterServer().toString().startsWith("JK2") ? "JK2]" : "JKA]"), 
                    JOptionPane.PLAIN_MESSAGE, null, null, "");
            
            if (input != null && input.length() > 0) {
                String error = tryJoinServer(input);

                if (error != null) {
                    JOptionPane.showMessageDialog(parent, "Error: " + error);
                }
            }
        });
        file.add(connect);
        //
        
        //exit
        JMenuItem exit = new JMenuItem("Exit", KeyEvent.VK_E);
        exit.addActionListener(x ->  System.exit(0));
        file.add(exit);
        //
     
        return file;
    }
    
    private JMenu createViewMenu() {
        JMenu view = new JMenu("View");
        view.setMnemonic(KeyEvent.VK_V);
        
        //log
        JMenuItem log = new JMenuItem("Log", KeyEvent.VK_L);
        log.addActionListener(x -> { 
            //TODO 
        });
        view.add(log);
        //
        
        return view;
    }
    
    private JMenu createToolsMenu() {
        JMenu tools = new JMenu("Tools");
        tools.setMnemonic(KeyEvent.VK_T);
        
        // options
        JMenuItem options = new JMenuItem("Options", KeyEvent.VK_O);
        options.addActionListener(x -> {
            //TODO
        });
        tools.add(options);
        //
        
        return tools;
    }
    
    private JMenu createHelpMenu() {
        JMenu help = new JMenu("Help");
        help.setMnemonic(KeyEvent.VK_H);
        
        //about
        JMenuItem about = new JMenuItem("About", KeyEvent.VK_A);
        about.addActionListener(x -> { 
            JOptionPane.showMessageDialog(parent, "This Jedi Knight 2 / Jedi Academy server browser was created by Grenixal. "
                    + "\nThanks also to Incubo, who has contributed to this project. \n \n"
                    + "In case you get new ideas or you would like to report bugs, please contact me at \ngrennybear@hotmail.com",
                    "About", JOptionPane.NO_OPTION);
        });
        help.add(about);
        //
        
        return help;
    }
    
    /**
     * Returns null if all was ok.
     * Returns error message otherwise.
     * @param ip
     * @return 
     */
    private String tryJoinServer(String ip) {
        try {
            String[] pieces = ip.split(":");
            if (pieces.length != 2 || pieces[0].split(".").length != 4) return "IP address was not in correct format";
            Tuple<String, Integer> ipTuple = new Tuple<>(pieces[0], Integer.parseInt(pieces[1]));
            controller.joinServer(ServerStatusParser.statusToServer(ServerStatusParser.emptyServerStatus(ipTuple), ipTuple, 999L));
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(parent, "Couldn't find path to game folder. Please, check your settings.ini.");
        } catch (NumberFormatException nfe) {
            return "Unable to read port";
        }
        
        return null;
    }
}

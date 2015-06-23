
package jk2serverbrowser;

import java.awt.event.KeyEvent;
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
    
    public MenuBar(JFrame frame) {
        super();        
        
        parent = frame;
        
        add(createFileMenu());
        add(createViewMenu());
        add(createToolsMenu());
        add(createHelpMenu());
    }
    
    private JMenu createFileMenu() {
        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);
        
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
            JOptionPane.showMessageDialog(parent, "This Jedi Knight 2 / Jedi Academy server browser was created by Grenixal. \nThanks also to Incubo, who has contributed to this project. \n \nIn case you get new ideas or want to report bugs, contact me at \ngrennybear@hotmail.com",
                    "About", JOptionPane.NO_OPTION);
        });
        help.add(about);
        //
        
        return help;
    }
}

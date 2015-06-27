
package jk2serverbrowser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import settings.Setting;
import settings.SettingsManager;

/**
 *
 * @author Markus Mulkahainen
 */
public final class OptionsView extends JDialog {
    
    private final SettingsManager settings;
    private final SettingsCache cache;
    
    public OptionsView(JFrame parent, SettingsManager settings) {
        super(parent, "Options", true);
        
        this.settings = settings;
        cache = new SettingsCache(settings);
        
        getContentPane().setLayout(new BorderLayout());
        
        getContentPane().add(createTabPanel(), BorderLayout.CENTER);
        getContentPane().add(createButtonPanel(), BorderLayout.SOUTH);
        
        this.setLocation(parent.getLocation().x + 300, parent.getLocation().y + 200);
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setVisible(true);
    }
    
    public JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new BorderLayout());
        
        JPanel buttons = new JPanel();
        JButton ok = new JButton("Ok");
        JButton apply = new JButton("Apply");
        JButton cancel = new JButton("Cancel");
        
        apply.setEnabled(false);
        
        buttons.add(ok);
        buttons.add(apply);
        buttons.add(cancel);
        
        cache.hasChanged().subscribe(hasChanged -> { 
            apply.setEnabled(hasChanged);
        });
        
        cancel.addActionListener(x -> { 
            dispose();
        });
        ok.addActionListener(x -> {
            cache.apply();
            dispose();
        });
        apply.addActionListener(x -> {
            cache.apply();
        });
        
        buttonPanel.add(buttons, BorderLayout.EAST);
        
        return buttonPanel;
    }
    
    public JPanel createTabPanel() {
        JPanel rootPanel = new JPanel();
        JTabbedPane tabPanel = new JTabbedPane();
        
        tabPanel.setPreferredSize(new Dimension(400, 200));
                
        tabPanel.addTab("General", createGeneralTab());
        tabPanel.addTab("Startup", createStartupTab());
        tabPanel.addTab("Custom masterserver", createCustomMasterserverTab());
        
        rootPanel.add(tabPanel);
        return rootPanel;
    }
    
    public JComponent createGeneralTab() {
        JPanel general = new JPanel();
        general.setLayout(new BoxLayout(general, BoxLayout.Y_AXIS));
        
        JPanel jk2 = new JPanel();
        Border border1 = BorderFactory.createTitledBorder("Path to jk2mp.exe");
        JTextField jk2path = new JTextField(25);
        jk2path.setText(settings.getSetting(Setting.JK2PATH));
        JButton browsejk2 = new JButton("Browse");
        jk2.add(jk2path);
        jk2.add(browsejk2);
        jk2.setBorder(border1);
        //there seems not to be other way, its horrible
        jk2path.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }
            
            private void update() {
                cache.set(Setting.JK2PATH, jk2path.getText());
            }
        });
        
        browsejk2.addActionListener(x -> { 
            jk2path.setText(openFileBrowser(jk2path.getText()));
        });
        
        JPanel jka = new JPanel();
        Border border2 = BorderFactory.createTitledBorder("Path to jkamp.exe");
        JTextField jkapath = new JTextField(25);
        jkapath.setText(settings.getSetting(Setting.JKAPATH));
        JButton browsejka = new JButton("Browse");
        jka.add(jkapath);
        jka.add(browsejka);
        jka.setBorder(border2);
        jkapath.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }
            
            private void update() {
                cache.set(Setting.JKAPATH, jkapath.getText());
            }
        });
        browsejka.addActionListener(x -> {
            jkapath.setText(openFileBrowser(jkapath.getText()));
        });
        
        general.add(jk2);
        general.add(jka);
        return general;
    }
    
    public JPanel createStartupTab() {        
        JPanel startup = new JPanel();
        startup.setLayout(new BoxLayout(startup, BoxLayout.PAGE_AXIS));
        
        JPanel gamePanel = new JPanel();
        JLabel label1 = new JLabel("Default selected gameversion");
        JComboBox games = new JComboBox(Setting.gameversions().values().toArray());
        gamePanel.add(label1);
        gamePanel.add(games);
        games.setSelectedIndex(Integer.parseInt(settings.getSetting(Setting.SELECTED_VERSION)));
        games.addActionListener(x -> {
            cache.set(Setting.SELECTED_VERSION, Setting.gameversions().entrySet().stream()
                                                     .filter(m -> m.getValue().equals((String) games.getSelectedItem()))
                                                     .findFirst()
                                                     .get()
                                                     .getKey());
        });
        
        JPanel masterPanel = new JPanel();
        JLabel label2 = new JLabel("Default selected masterserver");
        JComboBox servers = new JComboBox(Setting.masterServers().values().toArray());
        masterPanel.add(label2);
        masterPanel.add(servers);
        servers.setSelectedIndex(Integer.parseInt(settings.getSetting(Setting.SELECTED_MASTERSERVER)));
        servers.addActionListener(x -> {
            cache.set(Setting.SELECTED_MASTERSERVER, Setting.masterServers().entrySet().stream()
                                                     .filter(m -> m.getValue().equals((String) servers.getSelectedItem()))
                                                     .findFirst()
                                                     .get()
                                                     .getKey());
        });
        
        JPanel filterPanel = new JPanel();
        JLabel label3 = new JLabel("Default selected filter");
        JComboBox filters = new JComboBox(Setting.filters().values().toArray());
        filterPanel.add(label3);
        filterPanel.add(filters);
        filters.setSelectedIndex(Integer.parseInt(settings.getSetting(Setting.SELECTED_FILTER)));
        filters.addActionListener(x -> {
            cache.set(Setting.SELECTED_FILTER, Setting.filters().entrySet().stream()
                                                .filter(m -> m.getValue().equals((String) filters.getSelectedItem()))
                                                .findFirst()
                                                .get()
                                                .getKey());
        });
        
        startup.add(gamePanel);
        startup.add(masterPanel);
        startup.add(filterPanel);
        
        return startup;
    }
    
    public JPanel createCustomMasterserverTab() {
        JPanel custom = new JPanel();
        custom.setLayout(new BoxLayout(custom, BoxLayout.Y_AXIS));
        
        JPanel ipPanel = new JPanel();
        JLabel label1 = new JLabel("Custom masterserver IP");
        JTextField ip = new JTextField(15);
        ipPanel.add(label1);
        ipPanel.add(ip);
        ip.setText(settings.getSetting(Setting.CUSTOM_MASTERSERVER_IP));
        ip.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) { udpate();            }

            @Override
            public void removeUpdate(DocumentEvent e) { udpate();            }

            @Override
            public void changedUpdate(DocumentEvent e) { udpate();            }
            
            public void udpate() {
                cache.set(Setting.CUSTOM_MASTERSERVER_IP, ip.getText());
            }
            
        });
        
        JPanel jk2panel = new JPanel();
        JLabel label2 = new JLabel("Custom masterserver JK2 port");
        JTextField jk2 = new JTextField(6);
        jk2panel.add(label2);
        jk2panel.add(jk2);
        jk2.setText(settings.getSetting(Setting.CUSTOM_MASTERSV_JK2_PORT));
        jk2.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { udpate();            }

            @Override
            public void removeUpdate(DocumentEvent e) { udpate();            }

            @Override
            public void changedUpdate(DocumentEvent e) { udpate();            }
            
            public void udpate() {
                cache.set(Setting.CUSTOM_MASTERSV_JK2_PORT, jk2.getText());
            }
            
        });
        
        JPanel jkapanel = new JPanel();
        JLabel label3 = new JLabel("Custom masterserver JKA port");
        JTextField jka = new JTextField(6);
        jkapanel.add(label3);
        jkapanel.add(jka);
        jka.setText(settings.getSetting(Setting.CUSTOM_MASTERSV_JKA_PORT));
        jka.getDocument().addDocumentListener(new DocumentListener() { 
            @Override
            public void insertUpdate(DocumentEvent e) { udpate();            }

            @Override
            public void removeUpdate(DocumentEvent e) { udpate();            }

            @Override
            public void changedUpdate(DocumentEvent e) { udpate();            }
            
            public void udpate() {
                cache.set(Setting.CUSTOM_MASTERSV_JKA_PORT, jka.getText());
            }
        });
        
        custom.add(ipPanel);
        custom.add(jk2panel);
        custom.add(jkapanel);
        return custom;
    }
    
    private String openFileBrowser(String defaultValue) {
        final JFileChooser browser = new JFileChooser();       
        return browser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION ? 
                browser.getSelectedFile().getAbsolutePath() : 
                defaultValue;
    }
}

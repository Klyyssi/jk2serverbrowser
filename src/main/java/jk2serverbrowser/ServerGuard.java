
package jk2serverbrowser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

/**
 * Server guard -dialog. 
 * ServerGuard is used to keep track of the servers' players, and make a sound alarm when someone joins the guarded server.
 * @author Markus Mulkahainen
 */
public class ServerGuard extends JFrame implements ActionListener {
    
    private final GameServer server;
    private final MainController controller;
    private final Gui maingui;
    private final JPanel mainpanel = new JPanel(new BorderLayout());
    private final JPanel bottompanel = new JPanel(new BorderLayout());
    private final JRadioButton rbtnJoin = new JRadioButton("Join server"), 
            rbtnNotify = new JRadioButton("Sound notification");
    private final ButtonGroup btnGroup = new ButtonGroup();
    private JTable playertable = new JTable();
    private boolean firstTime = true;
    private int playerCount = 0, oldPlayerCount = 0;
    private Timer timer;
    
    /**
     * Constructor.
     * @param server The server that will be guarded
     * @param controller This ServerBrowser will be used to contact the server above, and refresh its' information
     * @param gui Main gui is needed in order to join the server
     */
    public ServerGuard(GameServer server, MainController controller, Gui gui) {       
        this.maingui = gui;
        this.controller = controller;
        this.server = server;
        setTitle("Server guard - " +server.getHostname());
        setPreferredSize(new Dimension(640,480));
        createComponents();
        add(mainpanel);
        refreshServer();
        timer = new Timer(20000, this);
        timer.start();
        super.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                timer.stop();
                setVisible(false);
            }
        });
        pack();
        setVisible(true);
    }
    
    /**
     * Create all GUI components.
     */
    private void createComponents() {      
        createPlayerTable();
        JScrollPane sp = new JScrollPane(playertable);       
        JPanel rbtnpanel = new JPanel();
        btnGroup.add(rbtnJoin);
        btnGroup.add(rbtnNotify);
        rbtnNotify.setSelected(true);
        Border border = BorderFactory.createTitledBorder("When new player joins - do");

        rbtnpanel.add(rbtnNotify, BorderLayout.WEST);
        rbtnpanel.add(rbtnJoin, BorderLayout.EAST);
        bottompanel.add(rbtnpanel, BorderLayout.WEST);
        rbtnpanel.setBorder(border);
        mainpanel.add(sp, BorderLayout.CENTER);
        mainpanel.add(bottompanel, BorderLayout.SOUTH);
    }
    
    private void createPlayerTable() {
        String col[] = {"Player", "Score", "Ping"};
        DefaultTableModel tableModel = new DefaultTableModel(col, 0);
        
        playertable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
			{
                            Component c = super.prepareRenderer(renderer, row, column);
                            if (!isRowSelected(row))
                            {                            
                                c.setBackground(getBackground());
                                int modelRow = convertRowIndexToModel(row);
                                String type = (String)getModel().getValueAt(modelRow, 0);
                                String ping = (String) getModel().getValueAt(modelRow, 2);
                                if (!ping.equals("0")) { 
                                    c.setBackground(Color.green);
                                }
                                if (ColorTagger.deHtmlize(type.toLowerCase()).contains("grenixal")) c.setBackground(Color.yellow);    
                            }

                            return c;
			}
            @Override
            public boolean isCellEditable(int nRow, int nCol) {
                return false;
            }
        };
        
        TableRowSorter trs = new TableRowSorter(tableModel);        
        trs.setComparator(1, new IntComparator());
        trs.setComparator(2, new IntComparator());
        playertable.setRowSorter(trs);
        
        //playertable.setPreferredScrollableViewportSize(new Dimension(220, 300));
        playertable.setFillsViewportHeight(true);
        playertable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        playertable.getColumnModel().getColumn(0).setPreferredWidth(140);    
        playertable.getColumnModel().getColumn(1).setPreferredWidth(40);  
        playertable.getColumnModel().getColumn(2).setPreferredWidth(40);   
    }    
    
    public void destroy() {
        timer.stop();
        this.dispose();
    }
    
    /**
     * Used for sorting rows in the player table
     */
    class IntComparator implements Comparator {
            @Override
            public int compare(Object o1, Object o2) {
                String int1 = (String)o1;
                String int2 = (String)o2;
                return Integer.parseInt(int2) - Integer.parseInt(int1);
            }
        }
    
    private void refreshServer() {
        controller.refresh(server).subscribe(x -> {           
            //Clear the table
            DefaultTableModel tableModel = (DefaultTableModel) playertable.getModel();
            tableModel.setRowCount(0);
            //

            setupPlayerTableData(x.getPlayers()); 
            oldPlayerCount = playerCount;
            playerCount = playertable.getRowCount();
            if (!firstTime && (playerCount - oldPlayerCount > 0))
                alarm();

            firstTime = false;
        });
    }
    
    /**
     * When a player joins the server, this method is called
     */
    private void alarm() {
        if (rbtnNotify.isSelected()) {           
            try {
                Clip clip = AudioSystem.getClip();
                AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File("plop.wav"));
                clip.open(inputStream);
                clip.start();
            } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
                JOptionPane.showMessageDialog(this, "Someone joined to the server - failed to play sound!");
            }
        } 
        else if (rbtnJoin.isSelected()) {
            try {              
                controller.joinServer(server);
                maingui.destroyServerGuards();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Couldn't find path to the game folder. Please, check your settings.ini.");
            }
        }
    }    
    
    /**
     * Fill the playertable with players.
     * @param list list of players
     */
    public void setupPlayerTableData(List<Player> list) {
        if (list == null) return;
        DefaultTableModel tableModel = (DefaultTableModel) playertable.getModel();
        for (Player p : list) {
            String[] data = new String[3];
            data[0] = p.getName();
            data[1] = p.getScore();
            data[2] = p.getPing();
            tableModel.addRow(data);
        }
        
        playertable.setModel(tableModel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        refreshServer();
    }
}

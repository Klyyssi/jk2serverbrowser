
package jk2serverbrowser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import rx.Subscription;

/**
 *
 * @author Markus Mulkahainen
 */
public final class Gui extends JPanel implements ActionListener, ListSelectionListener, WindowListener {

    public static void createGUI(MainController controller) {
        JFrame frame = new JFrame("JK2/JKA server browser");
        frame.setPreferredSize(new Dimension(1280,720));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Gui gui = new Gui(controller);
        gui.setOpaque(true);
        frame.addWindowListener(gui);
        frame.setContentPane(gui);
        frame.pack();
        frame.setVisible(true);
    }  
    
    private JTable table, playerTable;
    private final JButton btnGetServers, btnJoin;
    private GameServer selectedServer;
    private JRadioButton botfilter, emptyfilter;
    private List<ServerGuard> serverGuards = new ArrayList<>();
    private final PopupMenu popupMenu;
   
    private Subscription refreshList;
    
    private MainController controller;

    @Override
    public void windowOpened(WindowEvent e) {
        setSettings();
        changeMasterserver();
        getNewServerList();              
    }
    
    private void setSettings() {
        controller.getSettings().getSettings().forEach((x,y) -> {
            switch (x) {
                case SELECTED_VERSION:
                    switch (y) {
                        case "0":
                            //jk2 1.04
                            rbtns[0].setSelected(true);
                            break;
                        case "1":
                            //jk2 1.02
                            rbtns[1].setSelected(true);
                            break;
                        case "2":
                            //jka 1.00
                            rbtns[3].setSelected(true);
                            break;
                        case "3":
                            //jka 1.01
                            rbtns[2].setSelected(true);
                            break;      
                    }
                    break;
                case SELECTED_MASTERSERVER:
                    switch (y) {
                        case "1":
                            ounedMaster.setSelected(true);
                            break;
                        case "2":
                            jkhubMaster.setSelected(true);
                            break;
                        case "3":
                            customMaster.setSelected(true);
                            break;
                    }
                    break;
                case SELECTED_BOTFILTER:
                    switch (y) {
                        case "1":                                
                            botfilter.setSelected(true);          
                            break;
                        case "2":
                            emptyfilter.setSelected(true);  
                            break;
                    }
                    changeFilter();
                    break;
            }
        });
    }

    @Override
    public void windowClosing(WindowEvent e) { 
        controller.trySaveFavourites();
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
    
    /**
     * Used in servertable (sorting by Player count), and playertable (sorting by ping and score). It is messy...
     */
    class IntComparator implements Comparator {
            @Override
            public int compare(Object o1, Object o2) {
                String int1 = (String)o1;
                String int2 = (String)o2;
                int index1 = int1.indexOf("[");
                int index2 = int1.indexOf("]");
                int index3 = int2.indexOf("[");
                int index4 = int2.indexOf("]");
                if (index1 == -1 || index3 == -1) {
                    index2 = int1.length();
                    index4 = int2.length();
                }
                int n1 = Integer.parseInt(int1.substring(index1 + 1, index2));
                int n2 = Integer.parseInt(int2.substring(index3 + 1, index4));
                if (index1 != -1 && index3 != -1) {
                    if (n1 == n2) {
                        int a = Integer.parseInt(int1.substring(0,int1.lastIndexOf("/")));
                        int b = Integer.parseInt(int2.substring(0, int2.lastIndexOf("/")));
                        return a-b;
                    }                       
                }
                return n1 - n2;
            }
        }
    
    private TableRowSorter sorter;
    
    public Gui(MainController controller) {
        super(new BorderLayout());
        
        this.controller = controller;
        
        String col[] = {"Server name", "Players", "Map", "Gametype", "Mod", "Ping"};
        DefaultTableModel tableModel = new DefaultTableModel(col, 0);        
        
        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
			{          
                            Component c;
                            try {
                                c = super.prepareRenderer(renderer, row, column);      
                            } catch (NullPointerException npe) {
                                return null;
                            }
                            c.setForeground(Color.black);
                            if (column == 0) 
                                c.setFont(new Font("Sans Serif", Font.BOLD, 14));

                            //c.setBackground(new java.awt.Color(50,50,50,50));
                            if (!isRowSelected(row))
                                    c.setBackground(row % 2 == 0 ? new java.awt.Color(210,210,210) : new java.awt.Color(170,170,170));                              
                            lblServerCount.setText("Servers: " +table.getRowCount());
                            return c;
			}
            @Override
            public boolean isCellEditable(int nRow, int nCol) {
                return false;
            }            
        };
        
        sorter = new TableRowSorter(tableModel);        
        sorter.setComparator(1, new IntComparator());      
        sorter.setComparator(5, new IntComparator());
        table.setRowSorter(sorter);
        popupMenu = new PopupMenu(this, controller);
        table.setComponentPopupMenu(popupMenu);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point p = e.getPoint();
                int rowNumber = table.rowAtPoint(p);
                ListSelectionModel model = table.getSelectionModel();
                model.setSelectionInterval(rowNumber, rowNumber);
            }
        });
        
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(this);
        table.setFont(new Font("Sans Serif", Font.PLAIN, 14));
        table.setRowHeight(20);       
        
        TableColumn column;
        for (int i = 0; i < 5; i++) {
            column = table.getColumnModel().getColumn(i);
            if (i == 0) {
                column.setPreferredWidth(500);
            } else {
                column.setPreferredWidth(100);
            }
        }
        btnGetServers = new JButton("Get servers");
        btnJoin = new JButton("Join");
        btnJoin.setEnabled(false);
        
        btnGetServers.addActionListener(x -> {
                if (internet.isSelected()) {
                    clearTable();
                    getNewServerList();
                } else {
                    refreshFavourites();
                }
        });
        
        btnJoin.addActionListener(x -> {
            destroyServerGuards();
            try {
                controller.joinServer(selectedServer); 
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Couldn't find path to game folder. Please, check your settings.ini.");
            }
        });
        JPanel rightPanel = new JPanel(new BorderLayout());              
        
        createPlayerTable();
        JScrollPane sp = new JScrollPane(playerTable); 
        JPanel rightHackPanel = new JPanel(new BorderLayout());
        rightHackPanel.add(sp, BorderLayout.CENTER);
        rightPanel.add(rightHackPanel, BorderLayout.CENTER);
        
        JPanel centerpanel = new JPanel(new BorderLayout());
        JScrollPane infoPane = new JScrollPane(createInfoPanel());
        centerpanel.add(infoPane, BorderLayout.CENTER);
        centerpanel.add(createRadioButtonPanel(), BorderLayout.SOUTH); 
        centerpanel.setPreferredSize(new Dimension(100, 200));
        rightHackPanel.add(centerpanel, BorderLayout.SOUTH);
        
        rightPanel.add(rightHackPanel, BorderLayout.CENTER);
        
        
        //add(rightPanel, BorderLayout.EAST);
        JPanel buttonPanel = new JPanel(new BorderLayout()); 
        JPanel buttonPanelhack = new JPanel();
        buttonPanel.add(buttonPanelhack, BorderLayout.SOUTH);
        buttonPanelhack.add(btnGetServers);
        buttonPanelhack.add(btnJoin);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        JPanel leftpanel = new JPanel(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(table);
        leftpanel.add(scrollPane, BorderLayout.CENTER);
        leftpanel.add(createLowerPanel(), BorderLayout.SOUTH);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftpanel, rightPanel);
        splitPane.setDividerLocation(1050);
        add(splitPane);
    }
    
    private JLabel lblServerCount;
    private NoneSelectedButtonGroup filtergroup;
    private JRadioButton ounedMaster, jk2Master, jkhubMaster, customMaster, favourite, internet;   
    
    private JPanel createLowerPanel() {       
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 1));
        
        JPanel filterpanel = new JPanel();
        JPanel masterserverPanel = new JPanel();
        ButtonGroup masterservers = new ButtonGroup();
        ounedMaster = new JRadioButton("Ouned's");
        ounedMaster.setToolTipText("Enable to use master.ouned.de");
        jk2Master = new JRadioButton("Original");
        jk2Master.setToolTipText("Enable to use original masterjk2/3.ravensoft.com masterserver.");
        jkhubMaster = new JRadioButton("jkhub");
        jkhubMaster.setToolTipText("Enable to use master.jkhub.org");
        jkhubMaster.addActionListener(this);
        customMaster = new JRadioButton("Custom");
        customMaster.setToolTipText("Enable to use custom masterserver options defined in settings.ini");
        customMaster.addActionListener(this);
        
        masterservers.add(ounedMaster);
        masterservers.add(jk2Master);
        masterservers.add(jkhubMaster);
        masterservers.add(customMaster);
        masterserverPanel.add(jk2Master);
        masterserverPanel.add(ounedMaster);
        masterserverPanel.add(jkhubMaster);
        masterserverPanel.add(customMaster);
        jk2Master.setSelected(true);
        filtergroup = new NoneSelectedButtonGroup();
        Border border1 = BorderFactory.createTitledBorder("Server filters - Hide:");
        Border border2 = BorderFactory.createTitledBorder("Masterserver: ");
        Border border3 = BorderFactory.createTitledBorder("Serverlist:");
        
        JPanel serverlistSelectionPanel = new JPanel();
        ButtonGroup serverlists = new ButtonGroup();
        favourite = new JRadioButton("Favourites");
        internet = new JRadioButton("Internet");
        serverlists.add(internet);
        serverlists.add(favourite);
        serverlistSelectionPanel.add(internet);
        serverlistSelectionPanel.add(favourite);
        
        internet.addActionListener(x -> {
            btnGetServers.setText("Get servers");
            popupMenu.setDeleteFavourite(false);
            clearTable();
            getNewServerList();
        });
        
        favourite.addActionListener(x -> {
            btnGetServers.setText("Refresh");
            popupMenu.setDeleteFavourite(true);
            clearTable();
            controller.refreshFavourites().subscribe(server -> {
               this.addServerToTable(server);
            });
        });
        
        serverlistSelectionPanel.setBorder(border3);
        internet.setSelected(true);
        
        masterserverPanel.setBorder(border2);
        botfilter = new JRadioButton("Empty & bot-only servers");
        emptyfilter = new JRadioButton("Empty servers");
        filtergroup.add(botfilter);
        filtergroup.add(emptyfilter);
        jk2Master.addActionListener(this);
        ounedMaster.addActionListener(this);
        botfilter.addActionListener(this);
        emptyfilter.addActionListener(this);
        lblServerCount = new JLabel("");      
        lblServerCount.setBorder(new EmptyBorder(0,0,0,20));
        filterpanel.add(botfilter);
        filterpanel.add(emptyfilter);
        filterpanel.setBorder(border1);       
        panel.add(filterpanel);
        panel.add(masterserverPanel);
        panel.add(serverlistSelectionPanel);
        panel.add(lblServerCount);
        return panel;
    }
    
    private void addActionListeners(JButton btn) {
        btn.addActionListener(this);
    }
    
    private JLabel hostname, mod, ip, forcepowerdisable, weapondisable;
    private JRadioButton rbtns[] = new JRadioButton[4];
    private ButtonGroup version;
    
    private JPanel createInfoPanel() {
        JPanel infopanel = new JPanel();       
        infopanel.setLayout(new BoxLayout(infopanel, BoxLayout.Y_AXIS));
        hostname = new JLabel("Hostname: ");
        mod = new JLabel("Mod: ");
        ip = new JLabel("IP: ");
        forcepowerdisable = new JLabel("Forcepower disable: ");
        weapondisable = new JLabel("Weapon disable: ");
        infopanel.add(hostname);
        infopanel.add(mod);
        infopanel.add(ip);
        infopanel.add(forcepowerdisable);
        infopanel.add(weapondisable);    
        return infopanel;
    }
    
    private JPanel createRadioButtonPanel() {
        rbtns[0] = new JRadioButton("1.04");
        rbtns[1] = new JRadioButton("1.02");
        rbtns[2] = new JRadioButton("1.01");
        rbtns[3] = new JRadioButton("1.00");
        
        rbtns[0].setToolTipText("Find servers for Jedi Knight 2 version 1.04");
        rbtns[1].setToolTipText("Find servers for Jedi Knight 2 version 1.02");
        rbtns[2].setToolTipText("Find servers for Jedi Knight 3 Jedi Academy version 1.01");
        rbtns[3].setToolTipText("Find servers for Jedi Knight 3 Jedi Academy version 1.00");
        
        JPanel rdbtnPanel = new JPanel(new GridLayout(0,1));
        JPanel jk2panel = new JPanel(new GridLayout(0,2));
        JPanel jkapanel = new JPanel(new GridLayout(0,2));     
        
        Border border1 = BorderFactory.createTitledBorder("Jedi Knight 2");
        Border border2 = BorderFactory.createTitledBorder("Jedi Academy");
        jk2panel.setBorder(border1);
        jkapanel.setBorder(border2);
        
        version = new ButtonGroup();
        version.add(rbtns[0]);
        version.add(rbtns[1]);
        version.add(rbtns[3]);
        version.add(rbtns[2]);        
        jk2panel.add(rbtns[0]);
        rbtns[0].setSelected(true);       
        jk2panel.add(rbtns[1]);
        jkapanel.add(rbtns[2]);
        jkapanel.add(rbtns[3]);
        rbtns[0].addActionListener(this);
        rbtns[1].addActionListener(this);
        rbtns[3].addActionListener(this);
        rbtns[2].addActionListener(this);
        
        rbtns[0].setActionCommand("JK2_104");
        rbtns[1].setActionCommand("JK2_102");
        rbtns[3].setActionCommand("JA_101");
        rbtns[2].setActionCommand("JA_100");
        
        rdbtnPanel.add(jk2panel);
        rdbtnPanel.add(jkapanel);
        return rdbtnPanel;
    }
    
    private void createPlayerTable() {
        String col[] = {"Player", "Score", "Ping"};
        DefaultTableModel tableModel = new DefaultTableModel(col, 0);
        
        playerTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
			{
				Component c = super.prepareRenderer(renderer, row, column);
				if (!isRowSelected(row))
				{
					int modelRow = convertRowIndexToModel(row);
					String type = (String)getModel().getValueAt(modelRow, 0);
                                        String ping = (String) getModel().getValueAt(modelRow, 2);
                                        c.setBackground(new java.awt.Color(180,180,180));
                                        if (!ping.equals("0")) c.setBackground(new java.awt.Color(118, 192, 16));
					if (type.toLowerCase().contains("grenixal")) c.setBackground(new java.awt.Color(219, 219, 0));                                                                         
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
        playerTable.setRowSorter(trs);
        
        playerTable.setPreferredScrollableViewportSize(new Dimension(220, 350));
        playerTable.setFillsViewportHeight(true);
        playerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        playerTable.getColumnModel().getColumn(0).setPreferredWidth(140);    
        playerTable.getColumnModel().getColumn(1).setPreferredWidth(40);  
        playerTable.getColumnModel().getColumn(2).setPreferredWidth(40);   
    }
    
    public void clearTable() {
        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        tableModel.setRowCount(0);
    }
    
    public void refreshFavourites() {
        clearTable();
        controller.refreshFavourites().subscribe(server -> {
           addServerToTable(server); 
        });
    }
    
    private void getNewServerList() {
        clearTable();
        
        if (refreshList != null) {
            refreshList.unsubscribe();
        }

        refreshList = controller.getNewServerList().subscribe(x -> {
            addServerToTable(x);
        });

    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        
        if (o instanceof JRadioButton) {
            JRadioButton btn = (JRadioButton) o;  
            
            if (btn.getText().startsWith("Empty")) {
                changeFilter();           
            } else {
                changeMasterserver();
            }         
        }
    }
   
    private void changeFilter() {
        if (botfilter.isSelected()) {
            RowFilter<DefaultTableModel, Object> rf = RowFilter.regexFilter("\\[([1-9]|[0-9]{2,})\\]", 1);
            sorter.setRowFilter(rf);
        }  
        else if (emptyfilter.isSelected()) {
            RowFilter<DefaultTableModel, Object> rf = RowFilter.regexFilter("([1-9]|[0-9]{2,})/", 1);
            sorter.setRowFilter(rf);
        }            
        else 
            sorter.setRowFilter(null);        
    }
    
    private MasterServer selectionToMasterServer(Tuple<JRadioButton, JRadioButton> selections) {
        Map<Tuple<JRadioButton, JRadioButton>, MasterServer> map = new HashMap<>();
        map.put(new Tuple(jk2Master, rbtns[0]), MasterServer.JK2_104_ORIGINAL);
        map.put(new Tuple(jk2Master, rbtns[1]), MasterServer.JK2_102_ORIGINAL);
        map.put(new Tuple(jk2Master, rbtns[2]), MasterServer.JA_101_ORIGINAL);
        map.put(new Tuple(jk2Master, rbtns[3]), MasterServer.JA_100_ORIGINAL);
        map.put(new Tuple(ounedMaster, rbtns[0]), MasterServer.JK2_104_OUNED);
        map.put(new Tuple(ounedMaster, rbtns[1]), MasterServer.JK2_102_OUNED);
        map.put(new Tuple(ounedMaster, rbtns[2]), MasterServer.JA_101_OUNED);
        map.put(new Tuple(ounedMaster, rbtns[3]), MasterServer.JA_100_OUNED);
        map.put(new Tuple(jkhubMaster, rbtns[0]), MasterServer.JK2_104_JKHUB);
        map.put(new Tuple(jkhubMaster, rbtns[1]), MasterServer.JK2_102_JKHUB);
        map.put(new Tuple(jkhubMaster, rbtns[2]), MasterServer.JA_101_JKHUB);
        map.put(new Tuple(jkhubMaster, rbtns[3]), MasterServer.JA_100_JKHUB);
        map.put(new Tuple(customMaster, rbtns[0]), MasterServer.JK2_104_CUSTOM);
        map.put(new Tuple(customMaster, rbtns[1]), MasterServer.JK2_102_CUSTOM);
        map.put(new Tuple(customMaster, rbtns[2]), MasterServer.JA_101_CUSTOM);
        map.put(new Tuple(customMaster, rbtns[3]), MasterServer.JA_100_CUSTOM);
                
        return map.get(selections);
    }
    
    private JRadioButton getSelectedMasterServerButton() {
        if (jk2Master.isSelected()) return jk2Master;
        else if (ounedMaster.isSelected()) return ounedMaster;
        else if (jkhubMaster.isSelected()) return jkhubMaster;
        return customMaster;
    }
    
    private JRadioButton getSelectedVersionButton() {
        for (JRadioButton btn : rbtns) {
            if (btn.isSelected()) return btn;
        }
        
        return rbtns[0];
    }
    
    private void changeMasterserver() {
        controller.setMasterServer(selectionToMasterServer(new Tuple(getSelectedMasterServerButton(), getSelectedVersionButton())));
    }
    
    public synchronized void addServerToTable(GameServer s) {
        if (s == null) return;

        DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
        
        Object[] data = new Object[6];
        data[0] = s.getHostname();
        data[1] = s.getClients() +"/" +s.getMaxclients() + " [" +s.getPlayerCount() + "]";
        data[2] = s.getMapname();
        data[3] = s.getGametype();
        data[4] = s.getMod();
        data[5] = s.getPing();
        tableModel.addRow(data);
        
        table.setModel(tableModel);
    }   

    public void setupPlayerTable(List<Player> players) {
        if (players == null) return;
        
        DefaultTableModel tableModel = (DefaultTableModel) playerTable.getModel();
        tableModel.setRowCount(0);
        
        for (Player p : players) {
            String[] data = new String[3];
            data[0] = p.getName();
            data[1] = p.getScore();
            data[2] = p.getPing();
            tableModel.addRow(data);
        }
        
        playerTable.setModel(tableModel);
    }
        
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) return;
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) { btnJoin.setEnabled(false); return; }
        btnJoin.setEnabled(true);
        if (internet.isSelected())
            selectedServer = controller.getServers().get(table.convertRowIndexToModel(selectedRow));
        else
            selectedServer = controller.getFavourites().get(table.convertRowIndexToModel(selectedRow));
        //System.out.println(selectedServer);
        
        displayServer(selectedServer);
    }
    
    private void displayServer(GameServer server) {
        mod.setText("Mod: " + server.getMod());
        hostname.setText("Hostname: " +server.getHostnameNoHTML());
        ip.setText("Ip: " +server.getIp() + ":" +server.getPort());
        forcepowerdisable.setText("Forcepower disable: " + server.getForce_disable() + (server.getForce_disable().equals("163837") ? " (No force)" : ""));
        weapondisable.setText("Weapon disable: " + server.getWeapon_disable() + (server.getWeapon_disable().equals("65531") ? " (Saber only)" : ""));
        setupPlayerTable(server.getPlayers());
    }
    
    public void destroyServerGuards() {
        for (ServerGuard s : serverGuards) {
            s.destroy();
        }
        serverGuards.clear();
    }
    
    public void addServerGuard(ServerGuard serverGuard) {
        serverGuards.add(serverGuard);
    }
    
    public class NoneSelectedButtonGroup extends ButtonGroup {
        @Override
        public void setSelected(ButtonModel model, boolean selected) {
          if (selected) {
            super.setSelected(model, selected);
          } else {
            clearSelection();
          }
        }
    }
    
    public GameServer getSelectedServer() {
        return selectedServer;
    }
}

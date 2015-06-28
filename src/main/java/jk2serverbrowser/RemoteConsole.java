
package jk2serverbrowser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import udp.Connection;

/**
 *
 * @author Markus Mulkahainen
 */
public final class RemoteConsole extends JDialog {
    
    private final RemoteConsoleController controller;
    
    private JPasswordField password;
    private JTextField ip;
    private JTextField port;
    private JTextArea area;
    
    public RemoteConsole(JFrame parent, RemoteConsoleController controller, String withIp, String withPort) {
        super(parent, "Remote Console", true);
        
        this.controller = controller;
        
        getContentPane().setLayout(new BorderLayout());
                
        setPreferredSize(new Dimension(800,600));
        setLocation(parent.getLocation().x + 300, parent.getLocation().y + 200);
        
        getContentPane().add(createTopBar(withIp, withPort), BorderLayout.NORTH);
        getContentPane().add(createTextArea(), BorderLayout.CENTER);
        getContentPane().add(createBottomPanel(), BorderLayout.SOUTH);
        
        controller.response().subscribe(response -> { 
            if (response.isEmpty()) return;
            area.append(response);
        });
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setVisible(true);
    }
    
    public JPanel createBottomPanel() {
        JPanel bottom = new JPanel(new BorderLayout());
        
        JLabel prefix = new JLabel(" > ");
        JTextField command = new JTextField();
        command.addActionListener(x -> { 
            area.append("> "+command.getText() + "\n");
            controller.send(command.getText(), new String(password.getPassword()), ip.getText(), Integer.parseInt(port.getText()));
            command.setText("");
        });
        
        bottom.add(prefix, BorderLayout.WEST);
        bottom.add(command, BorderLayout.CENTER);
        return bottom;
    }
    
    public JPanel createTextArea() {
        JPanel center = new JPanel(new BorderLayout());
        
        area = new JTextArea("Console\n-------\n\n");       
        JScrollPane scrollPane = new JScrollPane(area);
        area.setEditable(false);
        area.setFont(new Font("monospaced", Font.PLAIN, 12));
        area.setBackground(Color.black);
        area.setForeground(Color.yellow);
        
        center.add(scrollPane, BorderLayout.CENTER);
        return center;
    }
    
    public JPanel createTopBar(String withIp, String withPort) {
        JPanel top = new JPanel(new BorderLayout());
        
        JPanel panel = new JPanel();
        JLabel label1 = new JLabel("IP:");
        ip = new JTextField(15);
        ip.setText(withIp == null ? "" : withIp);
        JLabel label2 = new JLabel("  Port:");
        port = new JTextField(5);
        port.setText(withPort == null ? "" : withPort);
        JLabel label3 = new JLabel("  Password:");
        password = new JPasswordField(10);
        JCheckBox remember = new JCheckBox("Remember password");
        
        panel.add(label1);
        panel.add(ip);
        panel.add(label2);
        panel.add(port);
        panel.add(label3);
        panel.add(password);
        panel.add(remember);
        
        top.add(panel, BorderLayout.WEST);
        return top;
    }
}

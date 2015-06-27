
package jk2serverbrowser;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import udp.Connection;

/**
 *
 * @author Markus Mulkahainen
 */
public class RemoteConsole extends JDialog {
    
    private final Connection connection = new Connection();
    
    public RemoteConsole(JFrame parent) {
        super(parent, "Remote Console", true);
        
        getContentPane().add(createContent());
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setVisible(true);
    }
    
    public JPanel createContent() {
        JPanel panel = new JPanel();
        
        JButton button = new JButton("Send");
        button.addActionListener(x -> {
            try (DatagramSocket server = new DatagramSocket()) {
                String str = "rcon suomi21 status";
                byte[] msg = new byte[] { (byte) 0xff, (byte)0xff, (byte)0xff, (byte)0xff };
                
                connection.send(server, concat(msg, str.getBytes()), InetAddress.getByName("130.234.134.132"), 28070);
                
                while (true) {
                    System.out.println(new String(connection.receive(server)));
                }
                
            } catch (IOException ex) {
                Logger.getLogger(RemoteConsole.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        
        panel.add(button);
        return panel;
    }
    
    
    public byte[] concat(byte[] a, byte[] b) {
        int aLen = a.length;
        int bLen = b.length;
        byte[] c= new byte[aLen+bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
     }
}

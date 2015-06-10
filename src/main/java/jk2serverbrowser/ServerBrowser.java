
package jk2serverbrowser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rx.Observable;

/**
 *
 * @author Markus Mulkahainen
 */
public class ServerBrowser {
    
    private InetSocketAddress masterserver;
    private ArrayList<GameServer> servers = new ArrayList<>();
    private String protocol = "16";
    private boolean original = true;

    public boolean isOriginal() {
        return original;
    }

    public void setOriginal(boolean original) {
        this.original = original;
    }
    
    public ServerBrowser(String ip, int port) {
        masterserver = new InetSocketAddress(ip, port);
    }
    
    public ServerBrowser() {
        masterserver = new InetSocketAddress("masterjk2.ravensoft.com", 28060);
    }
    
    public void setMasterserver(String host, int port) {       
        masterserver = new InetSocketAddress(host, port);
    }
    
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    
        public Observable<GameServer> getNewServerListAsObservable() {
        servers.clear();
        try (DatagramSocket server = new DatagramSocket(); BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {   
            server.setSoTimeout(5000);            
            byte[] protocolAsArray = protocol.getBytes();            
            //message = getservers 16 (or other protocol)
            byte message[] = { (byte) 0xff, (byte)0xff,(byte)0xff,(byte)0xff,0x67,0x65,0x74,0x73,0x65,0x72,0x76,0x65,0x72,0x73,0x20,protocolAsArray[0],protocolAsArray[1] };
            send(server, message, masterserver.getAddress(), masterserver.getPort());           
            byte[] array = receive(server);
            List<InetSocketAddress> ipList = parseIpAddresses(array, server, new ArrayList<InetSocketAddress>());
            System.out.println("Masterserver (" +masterserver.getAddress() +") listed " +ipList.size() + " ip addresses"); 
            parseServers(ipList);                      
            servers.removeAll(Collections.singleton(null));
            return Observable.from(servers);
        }  catch (SocketException ex) {
            System.err.println(" - Disconnected");
        } catch (IOException io) {
            System.err.println(" - No response from " +masterserver.getAddress());
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerBrowser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException npe) {
            System.err.println(" - Couldn't send anything. Check your internet connection.");
        }
        return null;
    } 
    
    public List<GameServer> getNewServerList() {
        servers.clear();
        try (DatagramSocket server = new DatagramSocket(); BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {   
            server.setSoTimeout(5000);            
            byte[] protocolAsArray = protocol.getBytes();            
            //message = getservers 16 (or other protocol)
            byte message[] = { (byte) 0xff, (byte)0xff,(byte)0xff,(byte)0xff,0x67,0x65,0x74,0x73,0x65,0x72,0x76,0x65,0x72,0x73,0x20,protocolAsArray[0],protocolAsArray[1] };
            send(server, message, masterserver.getAddress(), masterserver.getPort());           
            byte[] array = receive(server);
            List<InetSocketAddress> ipList = parseIpAddresses(array, server, new ArrayList<InetSocketAddress>());
            System.out.println("Masterserver (" +masterserver.getAddress() +") listed " +ipList.size() + " ip addresses"); 
            parseServers(ipList);                      
            servers.removeAll(Collections.singleton(null));
            return servers;
        }  catch (SocketException ex) {
            System.err.println(" - Disconnected");
        } catch (IOException io) {
            System.err.println(" - No response from " +masterserver.getAddress());
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerBrowser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException npe) {
            System.err.println(" - Couldn't send anything. Check your internet connection.");
        }
        return null;
    }      
    
    private void parseServers(List<InetSocketAddress> ipList) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();
        List<GameServer> servList = Collections.synchronizedList(new ArrayList()); //thread-safe
        
        for (InetSocketAddress ip : ipList) {
            ServerInfoGetter sig = new ServerInfoGetter(ip, servList);
            Thread t = new Thread(sig);
            threads.add(t);
            t.start();           
        }
        
        for (Thread t : threads) {
            t.join();           
        }
        
        servers.addAll(servList);
    }
    
    private List<InetSocketAddress> parseIpAddresses(byte[] array, DatagramSocket server, List<InetSocketAddress> ipList) throws IOException {
        for (int i = firstMark(array); i < array.length; i+=7) {
                String ip = (array[i+1] & 0xff) +"." +(array[i+2] & 0xff) +"." +(array[i+3]& 0xff) +"." +(array[i+4]& 0xff);
                if (ip.contains("69.79.84.0")) { //EOT
                    if (original) {
                        return parseIpAddresses(receive(server), server, ipList); 
                    }
                    return ipList;
                } 
                if (ip.contains("69.79.70.0")) return ipList; //EOF
                int port = ((array[i+5] & 0xff) << 8) + (array[i+6] & 0xff);
                ipList.add(new InetSocketAddress(ip, port));
        }
        return ipList;
    }
    
    /**
     * Search for the first index of 0x5c in masterservers response. This is needed because the custom masterservers are not using exactly same protocol...
     * @param array masterservers response
     * @return the index of the first 0x5c
     */
    private int firstMark(byte[] array) {
        for (int i = 0; i < array.length; i++) {
            if ((array[i] & 0xff) == 92) {
                return i;
            }
        }
        return 0;
    }
    
    private void send(DatagramSocket server, byte[] dataOut, InetAddress ip, int port) throws IOException {
        DatagramPacket packetOut = new DatagramPacket(dataOut, dataOut.length, ip, port);
        server.send(packetOut);
    }
    
    /**
     * Used for getting response from masterserver
     * @param server masterserver
     * @return int array of masterservers response (list of ip addresses)
     * @throws IOException when fails to recieve from master server
     */
    private byte[] receive(DatagramSocket server) throws IOException {
        byte[] dataIn = new byte[2048];
        DatagramPacket packetIn = new DatagramPacket(dataIn, dataIn.length); 
        server.receive(packetIn);
        return dataIn;
    }
    
    private String receiveAsString(DatagramSocket server) throws IOException {
        byte[] dataIn = new byte[2048];
        DatagramPacket packetIn = new DatagramPacket(dataIn, dataIn.length);
        server.receive(packetIn);     
        return new String(packetIn.getData(), "UTF-8");
    }    
    
    public List<GameServer> getServerList() {
        return servers;
    }   
    
    private String[] getServerInfo(InetSocketAddress serverIp) throws IOException {
        //message = (ff ff ff ff) getinfo xxx
        return sendServer( new byte[] { (byte) 0xff, (byte)0xff,(byte)0xff,(byte)0xff, 0x67,0x65, 0x74, 0x69, 0x6e, 0x66, 0x6f, 0x20, 0x78, 0x78, 0x78 }, serverIp).split("\\\\");
    }
    
    private String sendServer(byte[] message, InetSocketAddress serverIp) throws IOException {
        try (DatagramSocket server = new DatagramSocket(); BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            send(server, message, serverIp.getAddress(), serverIp.getPort());
            server.setSoTimeout(2000);
            return receiveAsString(server);
        }
    }
    
    public void refresh(GameServer server) throws IOException {
        long latency = System.currentTimeMillis();                              
        //message = (ff ff ff ff) getstatus
        String[] received = sendServer(new byte[] {(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x67, 0x65, 0x74, 0x73, 0x74, 0x61, 0x74, 0x75, 0x73 }, server.getIp()).split("\\r?\\n");
        latency = System.currentTimeMillis() - latency;
        server.setPing(latency);
        server.setServerStatus(received);
        //return received;
    }
    
    public String[] getServerStatus(InetSocketAddress ip) throws IOException {
        //message = (ff ff ff ff) getstatus
        return sendServer(new byte[] {(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x67, 0x65, 0x74, 0x73, 0x74, 0x61, 0x74, 0x75, 0x73 }, ip).split("\\r?\\n");
    }

    public InetSocketAddress getMasterserver() {
        return masterserver;
    }       
    
    class ServerInfoGetter implements Runnable {

        private InetSocketAddress ip;
        private List<GameServer> list;
        
        public ServerInfoGetter(InetSocketAddress ip, List<GameServer> list) {
            this.ip = ip;
            this.list = list;
        }
        
        @Override
        public void run()  {
            try {
                long latency = System.currentTimeMillis();
                String[] serverInfo = getServerStatus(ip);
                latency = System.currentTimeMillis() - latency;
                GameServer s = new GameServer(ip); 
                s.setServerStatus(serverInfo);
                s.setPing(latency);
                list.add(s);
            } catch (IOException ex) {
                System.err.println(" - Couldn't reach server " +ip);
            }
        }         
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jk2serverbrowser;

import java.util.HashMap;

/**
 *
 * @author Markus Mulkahainen
 */
public class Message {
    
    /**
     * (ff ff ff ff) getstatus
     */
    public static final byte[] GET_STATUS = new byte[] { (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x67, 0x65, 0x74, 0x73, 0x74, 0x61, 0x74, 0x75, 0x73 };
    
    /**
     * (ff ff ff ff) getinfo xxx
     */
    public static final byte[] GET_INFO = new byte[] { (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x67, 0x65, 0x74, 0x69, 0x6e, 0x66, 0x6f, 0x20, 0x78, 0x78, 0x78 };
    
    /**
     * (ff ff ff ff) getservers 15
     */
    public static final byte[] GET_SERVERS_15 = new byte[] {  (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x67, 0x65, 0x74, 0x73, 0x65, 0x72, 0x76, 0x65, 0x72, 0x73, 0x20, 0x31, 0x35 };
    
    /**
     * (ff ff ff ff) getservers 16
     */
    public static final byte[] GET_SERVERS_16 = new byte[] {  (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x67, 0x65, 0x74, 0x73, 0x65, 0x72, 0x76, 0x65, 0x72, 0x73, 0x20, 0x31, 0x36 };
    
    /**
     * (ff ff ff ff) getservers 25
     */
    public static final byte[] GET_SERVERS_25 = new byte[] {  (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x67, 0x65, 0x74, 0x73, 0x65, 0x72, 0x76, 0x65, 0x72, 0x73, 0x20, 0x32, 0x35 };
    
    /**
     * (ff ff ff ff) getservers 26
     */
    public static final byte[] GET_SERVERS_26 = new byte[] {  (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x67, 0x65, 0x74, 0x73, 0x65, 0x72, 0x76, 0x65, 0x72, 0x73, 0x20, 0x32, 0x36 };
    
    public static byte[] protocolToMessage(String protocol) {
        HashMap<String, byte[]> protocols = new HashMap<>();
        
        protocols.put("15", GET_SERVERS_15);
        protocols.put("16", GET_SERVERS_16);
        protocols.put("25", GET_SERVERS_25);
        protocols.put("26", GET_SERVERS_26);
        
        return protocols.get(protocol);
    }
}

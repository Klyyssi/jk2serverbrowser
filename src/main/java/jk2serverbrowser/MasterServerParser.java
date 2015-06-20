
package jk2serverbrowser;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Markus Mulkahainen
 */
public class MasterServerParser {

    /**
     * Returns list of server ip addresses.
     * 
     * @param response masterserver response to parse
     * @return 
     */
    public static Tuple<List<Tuple<String, Integer>>, END_OF_FILE> parseResponse(byte[] response) {
        List<Tuple<String, Integer>> ipList = new ArrayList<>();
        
        for (int i = firstMark(response) + 1; i < response.length; i+=7) {
            END_OF_FILE eof = isEndOfFile(new byte[] { response[i], response[i+1], response[i+2] });
            
            if (eof != null) {
                return new Tuple(ipList, eof);
            }
            
            ipList.add(getIpAddress(new byte[] { response[i], response[i+1], response[i+2], response[i+3], response[i+4], response[i+5] }));
        }
        
        return new Tuple(ipList, END_OF_FILE.EOF);
    }
    
    /**
     * Convert bytes to END_OF_FILE.
     * Returns null if conversion fails.
     * @param bytes
     * @return 
     */
    private static END_OF_FILE isEndOfFile(byte[] bytes) {
        return END_OF_FILE.bytesToEndOfFile(bytes);
    }
    
    private static Tuple<String, Integer> getIpAddress(byte[] bytes) {
        return new Tuple((bytes[0] & 0xff) +"." +(bytes[1] & 0xff) +"." +(bytes[2]& 0xff) +"." +(bytes[3]& 0xff), ((bytes[4] & 0xff) << 8) + (bytes[5] & 0xff));
    }
    
    /**
     * Search for the first index of 0x5c in masterservers response. 
     * This is needed because the custom masterservers are not using exactly same protocol as original.
     * @param array masterservers response
     * @return the index of the first 0x5c
     */
    private static int firstMark(byte[] array) {
        for (int i = 0; i < array.length; i++) {
            if ((array[i] & 0xff) == 92) {
                return i;
            }
        }
        return 0;
    }
    
}

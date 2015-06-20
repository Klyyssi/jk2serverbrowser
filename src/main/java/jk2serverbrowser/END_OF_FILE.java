
package jk2serverbrowser;

import java.util.Arrays;

/**
 *
 * @author Markus Mulkahainen
 */
public enum END_OF_FILE {
    
    EOT (new byte[] { 0x45, 0x4f, 0x54 }),
    EOF (new byte[] { 0x45, 0x4f, 0x46 });
    
    public final byte[] bytes;
    
    public static END_OF_FILE bytesToEndOfFile(byte[] bytes) {
        for (END_OF_FILE value : END_OF_FILE.values()) {
            if (Arrays.equals(value.bytes, bytes)) return value;
        }
        
        return null;
    }
    
    END_OF_FILE(byte[] toBytes) {
        bytes = toBytes;
    }
}

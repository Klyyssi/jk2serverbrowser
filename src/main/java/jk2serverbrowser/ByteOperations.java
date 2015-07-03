
package jk2serverbrowser;

/**
 *
 * @author Markus Mulkahainen
 */
public class ByteOperations {
    
    public static byte[] concat(byte[] a, byte[] b) {
        int aLen = a.length;
        int bLen = b.length;
        byte[] c= new byte[aLen+bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
     }
}


package service;

import jk2serverbrowser.Tuple;
import rx.Observable;

/**
 *
 * @author Markus Mulkahainen
 */
public interface IRconService {
    
    Observable<byte[]> send(byte[] command, byte[] password, Tuple<String, Integer> ip); 
}

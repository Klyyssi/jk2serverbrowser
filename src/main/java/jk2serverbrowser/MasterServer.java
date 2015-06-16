
package jk2serverbrowser;

/**
 *
 * @author Markus Mulkahainen
 */
public enum MasterServer {
    
    JK2_104_ORIGINAL ("masterjk2.ravensoft.com", "28060", "16"),
    JK2_102_ORIGINAL ("masterjk2.ravensoft.com", "28060", "15"),
    JK2_104_OUNED ("master.ouned.de", "28060", "16"),
    JK2_102_OUNED ("master.ouned.de", "28060", "15"),
    JK2_104_JKHUB ("master.jkhub.org", "28060", "16"),
    JK2_102_JKHUB ("master.jkhub.org", "28060", "15"),
    JK2_104_CUSTOM ("N/A", "N/A", "16"),
    JK2_102_CUSTOM ("N/A", "N/A", "15"),
    
    JA_101_ORIGINAL ("masterjk3.ravensoft.com", "29060", "26"),
    JA_100_ORIGINAL ("masterjk3.ravensoft.com", "29060", "25"),
    JA_101_OUNED ("master.ouned.de", "29060", "26"),
    JA_100_OUNED ("master.ouned.de", "29060", "25"),
    JA_101_JKHUB ("master.jkhub.org", "29060", "26"),
    JA_100_JKHUB ("master.jkhub.org", "29060", "25"),
    JA_101_CUSTOM ("N/A", "N/A", "26"),
    JA_100_CUSTOM ("N/A", "N/A", "25");
    
    public final String ip;
    public final String port;
    public final String protocol;
    
    MasterServer(String ip, String port, String protocol) {
        this.ip = ip;
        this.port = port;
        this.protocol = protocol;
    }
}

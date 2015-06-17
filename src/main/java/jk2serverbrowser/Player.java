
package jk2serverbrowser;

/**
 *
 * @author Markus Mulkahainen
 */
public class Player {
    
    private final String name;
    private final String score;
    private final String ping;
    
    public Player(String name, String score, String ping) {
        this.name = name;
        this.score = score;
        this.ping = ping;
    }
    
    public String getName() {
        return name;
    }

    public String getScore() {
        return score;
    }

    public String getPing() {
        return ping;
    } 
}

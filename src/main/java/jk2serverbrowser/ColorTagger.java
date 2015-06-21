
package jk2serverbrowser;

/**
 *
 * @author Markus Mulkahainen
 */
public class ColorTagger {
    
    private static final String[][] colorTags = {{"\\^1", "<span style=\"color:red;\">"}, 
                                                {"\\^2", "<span style=\"color:lime;\">"},
                                                {"\\^3", "<span style=\"color:yellow;\">"},
                                                {"\\^4", "<span style=\"color:blue;\">"},
                                                {"\\^5", "<span style=\"color:aqua;\">"},
                                                {"\\^6", "<span style=\"color:pink;\">"},
                                                {"\\^7", "<span style=\"color:white;\">"},
                                                {"\\^0", "<span style=\"color:black;\">"}};
    
    public static String htmlize(String s) {
        s = "<html>" +s;
        for (String[] colorTag : ColorTagger.colorTags) {
            s = s.replaceAll(colorTag[0], colorTag[1]);
        }
        //we are not adding </span> tags, but it seems to work fine without them... I don't bother adding them :D
        //plus we should be working with stringbuilder!
        s = s +"</html>";
        return s;
    }
    
    public static String deHtmlize(String s) {
        return s.replaceAll("\\<[^>]*>","");
    }
}

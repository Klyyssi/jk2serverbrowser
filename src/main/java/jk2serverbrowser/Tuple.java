package jk2serverbrowser;

import java.util.Objects;

/**
 *
 * @author Markus Mulkahainen
 */
public class Tuple<X, Y> { 
    public final X x; 
    public final Y y; 
    
    public Tuple(X x, Y y) { 
        this.x = x; 
        this.y = y; 
    }
        
    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof Tuple)) return false;
        if (o == this) return true;
        
        Tuple t = (Tuple) o;
        return t.x.equals(this.x) && t.y.equals(this.y);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.x);
        hash = 67 * hash + Objects.hashCode(this.y);
        return hash;
    }
} 
 

package couch.cushion.actor.message;

import java.io.Serializable;

public class SetFrameRate implements Serializable {
    
    private static final long serialVersionUID = -3268950497555304637L;
    
    private int numerator;
    private int denominator;
    
    protected SetFrameRate() {
        this(0, 0);
    }
    
    public SetFrameRate(final int numerator, final int denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }
    
    public int getDenominator() {
        return denominator;
    }
    
    public int getNumerator() {
        return numerator;
    }
}

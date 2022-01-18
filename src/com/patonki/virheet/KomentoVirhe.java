package com.patonki.virheet;

public class KomentoVirhe extends Exception{
    //Teksti mikä laitetaan komennon paikalle, jos komennon suoritus menee pieleen
    private final String korvaavaTeksti;
    private boolean fatal = false;

    public KomentoVirhe(String message, String korvaavaTeksti) {
        super(message);
        this.korvaavaTeksti = korvaavaTeksti;
    }
    public KomentoVirhe(String message, String korvaavaTeksti,boolean fatal) {
        this(message,korvaavaTeksti);
        this.fatal = fatal;
    }

    public boolean isFatal() {
        return fatal;
    }

    public String getKorvaavaTeksti() {
        return korvaavaTeksti;
    }
}

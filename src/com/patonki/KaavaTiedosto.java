package com.patonki;

/**
 * Luokka, jonka ainut tehtävä on tallentaa tietoa kaavaan liittyen. Tallentaa:
 * Käyttäjän kirjoittaman koodin, käyttäjän määrittelemät muuttujat ja tiedoston nimen.
 */
public class KaavaTiedosto {
    private final String koodi;
    private final String muuttujat;

    public KaavaTiedosto(String koodi, String muuttujat) {
        this.koodi = koodi;
        this.muuttujat = muuttujat;
    }

    public String getKoodi() {
        return koodi;
    }

    public String getMuuttujat() {
        return muuttujat;
    }
    public String[] getMuuttujatArray() {
        if (muuttujat.trim().length()==0) return new String[0];
        else return muuttujat.split(",");
    }
}

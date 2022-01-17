package com.patonki;

/**
 * Luokka, jonka ainut tehtävä on tallentaa tietoa kaavaan liittyen. Tallentaa:
 * Käyttäjän kirjoittaman koodin, käyttäjän määrittelemät muuttujat ja tiedoston nimen.
 */
public class KaavaTiedosto {
    private final String koodi;
    private final String muuttujat;
    private final String nimi;

    public KaavaTiedosto(String koodi, String muuttujat, String nimi) {
        this.koodi = koodi;
        this.muuttujat = muuttujat;
        this.nimi = nimi;
    }

    public String getKoodi() {
        return koodi;
    }

    public String getMuuttujat() {
        return muuttujat;
    }

    public String getNimi() {
        return nimi;
    }
}

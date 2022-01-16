package com.patonki;

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

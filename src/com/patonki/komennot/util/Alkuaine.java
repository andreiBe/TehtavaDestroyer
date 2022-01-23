package com.patonki.komennot.util;

import java.util.Objects;

public class Alkuaine {
    private String nimi;
    private int maara;

    public Alkuaine(String nimi, int maara) {
        this.nimi = nimi;
        this.maara = maara;
    }

    public void setMaara(int maara) {
        this.maara = maara;
    }

    public String getNimi() {
        return nimi;
    }

    @Override
    public String toString() {
        return "{"+nimi+" "+maara+"}";
    }

    public int getMaara() {
        return maara;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Alkuaine alkuaine = (Alkuaine) o;
        return nimi.equals(alkuaine.nimi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nimi);
    }
}

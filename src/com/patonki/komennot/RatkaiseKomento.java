package com.patonki.komennot;

import com.patonki.Komento;
import com.patonki.KoodiParser;
import com.patonki.komennot.util.JavaGiacJNI;
import com.patonki.virheet.KomentoVirhe;

public class RatkaiseKomento implements Komento {
    @Override
    public String run(String[] parameters, KoodiParser parser) throws KomentoVirhe {
        String yhtalo = parameters[0];
        String[] ratkaisut = JavaGiacJNI.ratkaise(yhtalo);
        StringBuilder ratkaisu = new StringBuilder();
        for (int i = 0; i < ratkaisut.length; i++) {
            ratkaisu.append(ratkaisut[i]);
            if (i != ratkaisut.length-1) {
                ratkaisu.append("\\vee ");
            }
        }
        return ratkaisu.toString();
    }
}

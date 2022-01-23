package com.patonki.komennot;

import com.patonki.Komento;
import com.patonki.KoodiParser;
import com.patonki.komennot.util.KemiaArvot;
import com.patonki.virheet.KomentoVirhe;

public class Moolimassa implements Komento {
    private static final KemiaArvot kemiaArvot = new KemiaArvot();
    @Override
    public String run(String[] parameters, KoodiParser parser) throws KomentoVirhe {
        if (parameters.length < 1) throw new KomentoVirhe("Liian vähän parametreja!","",true);
        return kemiaArvot.yhdisteenMolekyyliPaino(parameters[0])+"";
    }
}

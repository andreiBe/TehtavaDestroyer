package com.patonki.komennot;

import com.patonki.Komento;
import com.patonki.KoodiParser;
import com.patonki.komennot.util.Alkuaine;
import com.patonki.komennot.util.KemiaArvot;
import com.patonki.virheet.KomentoVirhe;

import java.util.List;

public class LaskeMoolimassa implements Komento {
    private static final KemiaArvot kemiaArvot = new KemiaArvot();
    @Override
    public String run(String[] parameters, KoodiParser parser) throws KomentoVirhe {
        if (parameters.length < 1) throw new KomentoVirhe("Liian vähän parametreja. Parametriksi pitäisi antaa yhdiste","",true);
        String yhdiste = parameters[0];
        StringBuilder lasku = new StringBuilder();
        List<Alkuaine> alkuaineet = kemiaArvot.keraaAlkuAineet(kemiaArvot.simppeliMuoto(yhdiste));
        for (Alkuaine alkuaine : alkuaineet) {
            lasku.append(alkuaine.getMaara() > 1 ? alkuaine.getMaara()+"\\cdot " : "").append("M(").append(alkuaine.getNimi()).append(")+");
        }
        lasku.setLength(lasku.length()-1); lasku.append("=(");
        for (Alkuaine alkuaine : alkuaineet) {
            double paino = kemiaArvot.getMolekyylipaino(alkuaine.getNimi());
            lasku.append(alkuaine.getMaara() > 1 ? alkuaine.getMaara()+"*" : "").append(paino).append("+");
        }
        lasku.setLength(lasku.length()-1);
        lasku.append(") \\frac{g}{mol}");
        lasku.append("="); lasku.append(kemiaArvot.yhdisteenMolekyyliPaino(yhdiste));
        lasku.append("\\frac{g}{mol}");
        return lasku.toString();
    }

}

package com.patonki.komennot;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import com.patonki.Komento;
import com.patonki.KoodiParser;
import com.patonki.LatexToMath;
import com.patonki.virheet.KomentoVirhe;

/**
 * Laskee laskun vain, jos vastaukseksi tulee kokonaisluku. Lisää myös alkuun yhtäsuuruusmerkin.
 */
public class CountJosKokonaisLukuKomento implements Komento {
    @Override
    public String run(String[] params, KoodiParser parser) throws KomentoVirhe {
        //Luokka, jolla voi laskea merkkijono laskuja
        DoubleEvaluator evaluator = new DoubleEvaluator();
        String lasku = params[0];
        //lasku on ehkä kirjoitettu latexilla
        lasku = LatexToMath.parseExpression(lasku);
        try {
            double d= evaluator.evaluate(lasku);
            if (Math.abs(d-(int)d) < 0.000000004) { //tarpeeks lähellä
                //desimaali luvuissa tapahtuu joskus virheitä tietokoneella
                if (params.length < 2) {
                    return "="+(int)d;
                }
                else {
                    return String.valueOf((int)d);
                }
            }
            if (d == Double.POSITIVE_INFINITY || d == Double.NEGATIVE_INFINITY
            || Double.isNaN(d)) {
                throw new KomentoVirhe("Nollalla jakaminen tai neliöjuuri negatiivisesta luvusta",
                        "Ei\\ voi\\ laskea");
            }
            if (params.length < 2) {
                return ""; // ei kirjoiteta mitään
            }
            return params[1];
        }
        catch (RuntimeException e) {
            e.printStackTrace();
            throw new KomentoVirhe("Laskemisessa ongelma: "+lasku,"",true);
        }
    }
}

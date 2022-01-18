package com.patonki.komennot;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import com.patonki.Komento;
import com.patonki.KoodiParser;
import com.patonki.virheet.KomentoVirhe;

/**
 * Laskee laskun vain, jos vastaukseksi tulee kokonaisluku. Lisää myös alkuun yhtäsuuruusmerkin.
 */
public class CountJosKokonaisLukuKomento implements Komento {
    @Override
    public String run(String[] params, KoodiParser parser) throws KomentoVirhe {
        //Luokka, jolla voi laskea merkkijono laskuja
        DoubleEvaluator evaluator = new DoubleEvaluator();
        //Korvataan suomalaisten suosimat pilkut pisteillä
        String lasku = params[0].replace(",",".");
        //poistetaan latex sulkeet: \left( ja \right( --> ( ja )
        lasku = lasku.replace("\\left","");
        lasku = lasku.replace("\\right","");
        lasku = lasku.replace("\\cdot","*");
        try {
            double d= evaluator.evaluate(lasku);
            if (Math.abs(d-(int)d) < 0.000000004) {
                //desimaali luvuissa tapahtuu joskus virheitä tietokoneella
                if (params.length < 2) {
                    return "="+(int)d;
                } else {
                    return String.valueOf((int)d);
                }
            }
            if (params.length < 2) {
                return ""; // ei kirjoiteta mitään
            }
            return params[1];
        } catch (NumberFormatException e) {
            if (e.getMessage().equals("Infinite or NaN"))
                throw new KomentoVirhe("Nollalla jakaminen tai neliöjuuri negatiivisesta luvusta",
                        "Ei\\ voi\\ laskea");
            throw new KomentoVirhe("Laskemisessa ongelma: "+lasku,"",true);
        }
        catch (RuntimeException e) {
            e.printStackTrace();
            throw new KomentoVirhe("Laskemisessa ongelma: "+lasku,"",true);
        }
    }
}

package com.patonki.komennot;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import com.patonki.Komento;
import com.patonki.KoodiParser;

/**
 * Laskee laskun vain, jos vastaukseksi tulee kokonaisluku. Lisää myös alkuun yhtäsuuruusmerkin.
 */
public class CountJosKokonaisLukuKomento implements Komento {
    @Override
    public String run(String[] params, KoodiParser parser) {
        //Luokka, jolla voi laskea merkkijono laskuja
        DoubleEvaluator evaluator = new DoubleEvaluator();
        //Korvataan suomalaisten suosimat pilkut pisteillä
        String lasku = params[0].replace(",",".");
        //poistetaan latex sulkeet: \left( ja \right( --> ( ja )
        lasku = lasku.replace("\\left","");
        lasku = lasku.replace("\\right","");

        double d = evaluator.evaluate(lasku);
        if (Math.abs(d-(int)d) < 0.000000004) {
            //desimaali luvuissa tapahtuu joskus virheitä tietokoneella
            return "="+(int)d;
        }
        return ""; // ei kirjoiteta mitään
    }
}

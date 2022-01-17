package com.patonki.komennot;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import com.patonki.Komento;
import com.patonki.KoodiParser;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * #count[] komento
 */
public class CountKomento implements Komento {
    @Override
    public String run(String[] params, KoodiParser parser) {
        //Luokka, jolla voi laskea merkkijono laskuja
        DoubleEvaluator evaluator = new DoubleEvaluator();
        //Korvataan suomalaisten suosimat pilkut pisteillä
        String lasku = params[0].replace(",",".");
        //poistetaan latex sulkeet: \left( ja \right( --> ( ja )
        lasku = lasku.replace("\\left","");
        lasku = lasku.replace("\\right","");

        int merkitsevat;
        if (params.length > 1) {
            String tarkkuus = params[1];
            if (tarkkuus.startsWith("+")) {  //+x merkitsevää numeroa verrattuna vastaustarkkuuteen
                merkitsevat = parser.getMerkitsevatNumerot() + Integer.parseInt(tarkkuus.substring(1));
            }
            else if (tarkkuus.startsWith("-")) { //-x merkitsevää numeroa verrattuna vastaustarkkuuteen
                merkitsevat = parser.getMerkitsevatNumerot() - Integer.parseInt(tarkkuus.substring(1));
            }
            else if (tarkkuus.equals("int")) { //palauttaa laskun tuloksen pyöristettynä kokonaislukujen tarkkuuteen
                double tulos = evaluator.evaluate(lasku);
                return String.valueOf(Math.round(tulos));
            }
            //Huom. tarkkuus 0 tarkoittaa ei pyöristystä ollenkaan
            else { //juuri niin monta merkitsevää numeroa kuin käyttäjä haluaa
                merkitsevat = Integer.parseInt(tarkkuus);
            }
            //Jos parametria ei ole käytetään vastaustarkkuutta
        } else merkitsevat = parser.getMerkitsevatNumerot();

        double tulos = evaluator.evaluate(lasku);

        BigDecimal pyoristetty = new BigDecimal(tulos).round(new MathContext(merkitsevat));
        return pyoristetty.toPlainString();
    }
}

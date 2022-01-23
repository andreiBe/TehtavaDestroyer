package com.patonki.komennot;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import com.patonki.Komento;
import com.patonki.KoodiParser;
import com.patonki.LatexToMath;
import com.patonki.virheet.KomentoVirhe;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * #count[] komento
 */
public class CountKomento implements Komento {
    @Override
    public String run(String[] params, KoodiParser parser) throws KomentoVirhe {
        if (params.length == 0) throw new KomentoVirhe("Liian vähän parametreja:0","null",true);
        //Luokka, jolla voi laskea merkkijono laskuja
        DoubleEvaluator evaluator = new DoubleEvaluator();
        String lasku = params[0];
        //lasku on ehkä kirjoitettu latex kielellä
        lasku = LatexToMath.parseExpression(lasku);
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
            else if (tarkkuus.equals("0")) { //sama kuin epätarkin lähtöarvo
                merkitsevat = parser.getMerkitsevatNumerot();
            }
            else { //juuri niin monta merkitsevää numeroa kuin käyttäjä haluaa
                try {
                    merkitsevat = Integer.parseInt(tarkkuus);
                } catch (NumberFormatException e) {
                    throw new KomentoVirhe("Ei sovi tarkkuudeksi: "+tarkkuus,"null",true);
                }
            }
            //Jos parametria ei ole käytetään vastaustarkkuutta
        } else merkitsevat = 0;
        try {
            double tulos = evaluator.evaluate(lasku);
            BigDecimal pyoristetty = new BigDecimal(tulos).round(new MathContext(merkitsevat));
            if (merkitsevat == 0) {
                return max5Decimaalia(pyoristetty);
            }
            return pyoristetty.toPlainString();
        } catch (NumberFormatException e) {
            if (e.getMessage().equals("Infinite or NaN"))
                throw new KomentoVirhe("Nollalla jakaminen tai neliöjuuri negatiivisesta luvusta",
                        "Ei\\ voi\\ laskea");
            throw new KomentoVirhe("Laskussa ongelma: "+lasku,"",true);
        }
        catch (RuntimeException e) {
            e.printStackTrace();
            throw new KomentoVirhe("Laskussa ongelma: "+lasku,"",true);
        }
    }
    private String max5Decimaalia(BigDecimal bigDecimal) {
        String[] split = bigDecimal.toPlainString().split("[,.]");
        if (split.length > 1 && split[1].length() > 5) {
            int i = Integer.parseInt(split[1].charAt(5)+"");
            String desimaalit = split[1].substring(0,4);
            int vika = Integer.parseInt(split[1].charAt(4)+"");
            desimaalit += i >= 5 ? ((vika+1)+"").charAt(0) : (vika+"").charAt(0);
            return split[0]+","+desimaalit;
        }
        return bigDecimal.toPlainString();
    }
}

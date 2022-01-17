package com.patonki;

import com.fathzer.soft.javaluator.DoubleEvaluator;
import com.patonki.komennot.CountJosKokonaisLukuKomento;
import com.patonki.komennot.CountKomento;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Muuttaa koodin listaksi ohjeita, jotka Kirjoittaja voi suorittaa.
 */
public class KoodiParser {
    //Sisältää komennot, joita käyttäjä voi käyttää koodissaan
    private final HashMap<String,Komento> komennot = new HashMap<>();

    public KoodiParser() {
        komennot.put("count", new CountKomento());
        komennot.put("ifInt", new CountJosKokonaisLukuKomento());
    }
    private int merkitsevatNumerot;
    //palauttaa pienemmän merkitsevien numeroiden määrän arvojen keskuudesta
    public int getMerkitsevatNumerot(String[] arvot) {
        int min = Integer.MAX_VALUE;
        for (String arvo : arvot) {
            arvo = arvo.replace(",",".");
            try {
                int tulos = 0;
                Double.parseDouble(arvo);
                //Kopioin netistä regex kaavan
                String[] osat = arvo.split("(^0+(\\.?)0*|(~\\.)0+$|\\.)");
                for (String s : osat) {
                    tulos += s.length();
                }
                min = Math.min(min,tulos);
            } catch (NumberFormatException ignored) {}
        }
        return min;
    }
    //palauttaa seuraavan merkin joko oikealta tai vasemmalta, mutta ei hyväksy välilyöntejä
    private char charAt(String koodi, int index, int dir) {
        while (index > 0 && index < koodi.length()) {
            if (koodi.charAt(index)==' ')index+=dir;
            else return koodi.charAt(index);
        }
        return 0;
    }
    private boolean onKirjainTaiNumero(char c) {
        if (c >= 'a' && c <= 'z') return true;
        if (c >= 'A' && c <= 'A') return true;
        if (c == 'ä' | c == 'ö' | c == 'å') return true;
        if (c == 'Ä' | c == 'Ö' | c== 'Å') return true;
        return c >= '0' && c <= '9';
    }
    public String korvaaMuuttujatArvoilla(String koodi, String[] muuttujat, String[] arvot) {
        for (int i = 0; i < muuttujat.length; i++) {
            String muuttuja = muuttujat[i];
            //Muuttuja ei ole osa sanaa \\b tarkoittaa sanan loppua tai alkua
            Matcher matcher = Pattern.compile("\\b"+muuttuja+"\\b").matcher(koodi);
            String korvaaja = arvot[i];
            while (matcher.find()) {
                int start = matcher.start(); //kohta, josta muuttuja alkaa
                //Miinus luvut korotettuna toiseen pitää laittaa sulkeiden sisään
                if (korvaaja.startsWith("-") && charAt(koodi,start+1,1)=='^') {
                    koodi = koodi.replaceFirst(
                            "\\b"+muuttuja+"\\b",
                            Matcher.quoteReplacement("\\left("+korvaaja+"\\right)"));
                } else {
                    koodi = koodi.replaceFirst("\\b"+muuttuja+"\\b",korvaaja);
                }
                //luodaan uusi matcher, koska merkkijono vaihtui
                matcher = Pattern.compile("\\b"+muuttuja+"\\b").matcher(koodi);
            }
        }
        return koodi;
    }

    public void setMerkitsevatNumerot(int merkitsevatNumerot) {
        this.merkitsevatNumerot = merkitsevatNumerot;
    }

    public String maaritaFunktioidenArvot(String koodi) {
        int i;
        //etsitään #-merkkejä, koska se tarkoittaa funktiota
        while ((i = koodi.lastIndexOf("#")) != -1) { //HUOM. käytetään lastIndexOf()
            int suljeAukeaa = koodi.indexOf("[",i);
            int suljeSulkeutuu = koodi.indexOf("]",suljeAukeaa);

            String komennonNimi = koodi.substring(i+1, suljeAukeaa);
            String p = koodi.substring(suljeAukeaa+1,suljeSulkeutuu); //sulkeiden sisällä oleva teksti
            String[] params = p.split(";");

            //Komento palauttaa arvon, joka kuuluu laittaa komennon tilalle
            String value = komennot.get(komennonNimi).run(params,this);

            //TODO tämä saattaa perjaatteessa olla ongelma,
            // jos tulevaisuudessa muuttujien arvot muuttuvat kesken suorituksen
            koodi = koodi.replace("#"+komennonNimi+"["+p+"]",value);
        }
        return koodi;
    }
    public List<Instruction> parse(String koodi, String[] muuttujat, String[] arvot) {
        ArrayList<Instruction> ohjeet = new ArrayList<>();

        koodi = korvaaMuuttujatArvoilla(koodi,muuttujat,arvot);
        this.merkitsevatNumerot = getMerkitsevatNumerot(arvot);
        koodi = maaritaFunktioidenArvot(koodi);
        // +- on sama kuin -
        koodi = koodi.replaceAll("\\+\\s*-","-");
        // -- on sama kuin +
        koodi = koodi.replaceAll("-\\s*-","+");
        // 1 * x on sama kuin x
        koodi = koodi.replaceAll("1\\s*\\*(?=\\s*[a-zäxöåA-ZXÄÖÅ])","");
        //8*x on sama kuin 8x
        koodi = koodi.replaceAll("\\*\\s*(?=[a-zäöåA-ZÄÖ])","");
        //Joskus -- korvaaminen aiheuttaa plussan, jonka ei pitäisi olla olemassa
        //Esim --b + 9 EI OLE: +b+9
        koodi = koodi.replaceAll("(?<![\\w)])\\+","");

        String[] lines = koodi.split("\n");
        for (String line : lines) {
            if (line.startsWith("<t>")) { //ei käytetä latex koodia
                ohjeet.add(new Instruction(line.substring(3), true));
            } else {
                //korvataan pilkut latexin pilkuilla
                line = line.replaceAll(",","{,}");
                ohjeet.add(new Instruction(line, false));
            }
        }
        return ohjeet;
    }

    public int getMerkitsevatNumerot() {
        return merkitsevatNumerot;
    }
}

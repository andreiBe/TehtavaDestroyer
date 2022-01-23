package com.patonki;

import com.patonki.komennot.*;
import com.patonki.util.StringUtil;
import com.patonki.virheet.KomentoVirhe;
import com.patonki.virheet.ParserException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Muuttaa koodin listaksi ohjeita, jotka Kirjoittaja voi suorittaa.
 */
public class KoodiParser {
    //Sisältää komennot, joita käyttäjä voi käyttää koodissaan
    private final HashMap<String, Komento> komennot = new HashMap<>();
    private final StringUtil stringUtil = new StringUtil();
    private final ArrayList<String> muuttujat = new ArrayList<>();
    private final ArrayList<String> arvot = new ArrayList<>();
    private int merkitsevatNumerot;
    public KoodiParser() {
        komennot.put("Laske", new CountKomento());
        komennot.put("JosInt", new CountJosKokonaisLukuKomento());
        komennot.put("MoolimassaLasku",new LaskeMoolimassa());
        komennot.put("Moolimassa", new Moolimassa());
        komennot.put("Ratkaise",new RatkaiseKomento());
    }
    public String suoritaKomento(String komennonNimi, String[] parameters) throws ParserException {
        Komento komento = komennot.get(komennonNimi);
        if (komento == null) throw new ParserException("Ei ole komento:" + komennonNimi);

        try {
            //Komento palauttaa arvon, joka kuuluu laittaa komennon tilalle
            return komento.run(parameters, this);
        } catch (KomentoVirhe e) {
            //Komennon suorituksessa virhe
            e.printStackTrace();
            //Ohjelman suoritus ei voi jatkua
            if (e.isFatal()) throw new ParserException(e.getMessage());
            return e.getKorvaavaTeksti(); //komennot voivat silti laittaa jonkin arvon
        }
    }
    public List<Instruction> parse(String koodi, String[] muuttujat, String[] arvot) throws ParserException {
        ArrayList<Instruction> ohjeet = new ArrayList<>();
        this.muuttujat.addAll(Arrays.asList(muuttujat));
        this.arvot.addAll(Arrays.asList(arvot));
        koodi = koodi.replaceAll("\\\\(?=#)","");
        //Etsitään koodissa määritellyt muuttujat esim: #var = 9
        koodi = stringUtil.loydaKoodissaMaaritellytMuuttujat(koodi,this.muuttujat,this.arvot);
        koodi = stringUtil.korvaaTextRuudut(koodi,this.muuttujat);
        koodi = koodi.replaceAll("\\\\(left|right)(?=[\\[\\]])","");
        //Korvataan sekä koodissa määritellyt että käyttäjän määrittelemät muuttujat arvoillaan
        koodi = stringUtil.korvaaMuuttujatArvoilla(koodi, this.muuttujat, this.arvot);
        //Selvitetään merkitsevien numeroiden määrä komentoja varten
        this.merkitsevatNumerot = stringUtil.getMerkitsevatNumerot(arvot);
        koodi = stringUtil.ifLausekkeet(koodi);

        //Suoritetaan komennot
        koodi = stringUtil.maaritaFunktioidenArvot(koodi,this);
        //Korvataan esim -- merkinnät + merkillä ja +- merkinnät - merkillä
        koodi = stringUtil.siistiMatematiikkaa(koodi);
        //Luodaan ohjeet
        String[] lines = koodi.split("\n");
        for (String rivi : lines) {
            if (rivi.startsWith("<t>")) { //ei käytetä latex koodia
                ohjeet.add(new Instruction(rivi.substring(3), true));
            } else if (!rivi.startsWith("<ignore>")) {
                rivi = stringUtil.valmisteleLatexKoodiRivi(rivi);
                ohjeet.add(new Instruction(rivi, false));
            }
        }
        return ohjeet;
    }

    public int getMerkitsevatNumerot() {
        return merkitsevatNumerot;
    }

    //Käytetään vain testaamiseen
    public void setMerkitsevatNumerot(int merkitsevatNumerot) {
        this.merkitsevatNumerot = merkitsevatNumerot;
    }
}

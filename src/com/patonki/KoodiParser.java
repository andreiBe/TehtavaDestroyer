package com.patonki;

import com.patonki.komennot.CountJosKokonaisLukuKomento;
import com.patonki.komennot.CountKomento;
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
        komennot.put("count", new CountKomento());
        komennot.put("ifInt", new CountJosKokonaisLukuKomento());
    }

    public String korvaaMuuttujatArvoilla(String koodi, List<String> muuttujat, List<String> arvot) {
        //Huom aloitetaan lopusta, koska siellä ovat koodissa määritellyt muuttujat
        for (int i = muuttujat.size() - 1; i >= 0; --i) {
            String muuttuja = muuttujat.get(i);
            String muuttujanArvo = arvot.get(i);
            //Muuttuja ei ole osa sanaa \\b tarkoittaa sanan loppua tai alkua
            Matcher matcher = Pattern.compile("\\b" + muuttuja + "\\b").matcher(koodi);
            while (matcher.find()) {
                int start = matcher.start(); //kohta, josta muuttuja alkaa
                //Miinus luvut korotettuna potenssiin pitää laittaa sulkeiden sisään
                if (muuttujanArvo.startsWith("-") && stringUtil.seuraavaMerkki(koodi, start + 1) == '^') {
                    koodi = stringUtil.korvaaMuuttujaSulkeidenKera(koodi, muuttuja, muuttujanArvo);
                }
                //* -8 --> * (-8)
                else if (muuttujanArvo.startsWith("-") && stringUtil.edellinenMerkki(koodi, start - 1) == '*') {
                    koodi = stringUtil.korvaaMuuttujaSulkeidenKera(koodi, muuttuja, muuttujanArvo);
                } else {
                    koodi = koodi.replaceFirst("\\b" + muuttuja + "\\b", muuttujanArvo);
                }
                //luodaan uusi matcher, koska merkkijono vaihtui
                matcher = Pattern.compile("\\b" + muuttuja + "\\b").matcher(koodi);
            }
        }
        return koodi;
    }

    public String maaritaFunktioidenArvot(String koodi) throws ParserException {
        String[] rivit = koodi.split("\n");
        for (int j = 0; j < rivit.length; j++) {
            String rivi = rivit[j];
            int i;
            //etsitään #-merkkejä, koska se tarkoittaa funktiota
            //Huom aloitetaan lopusta, koska funktiot saattavat olla sisäkkäin
            while ((i = rivi.lastIndexOf("#")) != -1) {
                int suljeAukeaa = rivi.indexOf("[", i);
                int suljeSulkeutuu = rivi.indexOf("]", suljeAukeaa);
                if (suljeAukeaa == -1 || suljeSulkeutuu == -1) {
                    throw new ParserException("Komento kirjoitettu väärin oikea tapa: #nimi[p1;p2]"); //virhe
                }

                String komennonNimi = rivi.substring(i + 1, suljeAukeaa);
                String p = rivi.substring(suljeAukeaa + 1, suljeSulkeutuu); //sulkeiden sisällä oleva teksti
                String[] params = p.split(";");
                if (komennot.get(komennonNimi) == null) {
                    throw new ParserException("Ei ole komento:" + komennonNimi);
                }
                String value;
                try {
                    //Komento palauttaa arvon, joka kuuluu laittaa komennon tilalle
                    value = komennot.get(komennonNimi).run(params, this);
                } catch (KomentoVirhe e) {
                    //Komennon suorituksessa virhe
                    e.printStackTrace();
                    //Ohjelman suoritus ei voi jatkua
                    if (e.isFatal()) throw new ParserException(e.getMessage());
                    value = e.getKorvaavaTeksti(); //komennot voivat silti laittaa jonkin arvon
                }

                rivi = rivi.replace("#" + komennonNimi + "[" + p + "]", value);
                rivit[j] = rivi;
            }
        }
        return stringUtil.rivitYhdeksiMerkkijonoksi(rivit);
    }

    public List<Instruction> parse(String koodi, String[] muuttujat, String[] arvot) throws ParserException {
        ArrayList<Instruction> ohjeet = new ArrayList<>();
        this.muuttujat.addAll(Arrays.asList(muuttujat));
        this.arvot.addAll(Arrays.asList(arvot));
        //poistetaan välilyönnit, joita ennen ei ole \ merkkiä
        koodi = stringUtil.poistaValilyonnitLatexKielesta(koodi);
        //Etsitään koodissa määritellyt muuttujat esim: #var = 9
        koodi = loydaKoodissaMaaritellytMuuttujat(koodi);
        //Korvataan sekä koodissa määritellyt että käyttäjän määrittelemät muuttujat arvoillaan
        koodi = korvaaMuuttujatArvoilla(koodi, this.muuttujat, this.arvot);
        //Selvitetään merkitsevien numeroiden määrä komentoja varten
        this.merkitsevatNumerot = stringUtil.getMerkitsevatNumerot(arvot);
        //Suoritetaan komennot
        koodi = maaritaFunktioidenArvot(koodi);
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

    private String loydaKoodissaMaaritellytMuuttujat(String koodi) {
        String[] rivit = koodi.split("\n");
        Pattern setterPatern = Pattern.compile("#\\s*[^=]+\\s*="); //Esim: #aine = Na_3SO
        for (int i = 0; i < rivit.length; i++) {
            String line = rivit[i];
            Matcher matcher = setterPatern.matcher(line);
            if (matcher.find()) {
                //Ennen yhtäsuuruus merkkiä, mutta ei #-merkkiä
                String muuttujaName = line.substring(1, line.indexOf("=")).replaceAll(" ", "");
                muuttujat.add(muuttujaName);
                //Jälkeen yhtäsuuruus merkin
                String muuttujaArvo = line.substring(line.indexOf("=") + 1);
                arvot.add(muuttujaArvo);
                rivit[i] = "<ignore>"; //tämä rivi on jo käsitelty
            }
        }
        return stringUtil.rivitYhdeksiMerkkijonoksi(rivit);
    }

    public int getMerkitsevatNumerot() {
        return merkitsevatNumerot;
    }

    //Käytetään vain testaamiseen
    public void setMerkitsevatNumerot(int merkitsevatNumerot) {
        this.merkitsevatNumerot = merkitsevatNumerot;
    }
}

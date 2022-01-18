package com.patonki.util;

import java.util.regex.Matcher;

/**
 * Sisältää apumetodeja erilaisiin merkkijono operaatioihin
 */
public class StringUtil {
    /**
     * Palauttaa epätarkimman arvon merkitsevien numeroiden määrän.
     * Jos arvot taulukossa on arvoja, jotka eivät ole numeroita, niitä ei huomioida.
     * @param arvot Käyttäjän arvot
     * @return epätarkin merkitsevien numeroiden määrä
     */
    public int getMerkitsevatNumerot(String[] arvot) {
        int min = Integer.MAX_VALUE;
        for (String arvo : arvot) {
            arvo = arvo.replace(",",".");
            try {
                int tulos = 0;
                Double.parseDouble(arvo); //tuottaa virheen ehkä
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

    /**
     * Palauttaa seuraavan merkin tai 0, jos sellaista ei ole.
     * @param koodi Merkkijono, josta etsiä
     * @param index Seuraava indeksi
     * @return seuraava merkki
     */
    public char seuraavaMerkki(String koodi, int index) {
        if (index >= koodi.length()) return 0;
        else return koodi.charAt(index);
    }

    /**
     * Palauttaa edellisen merkin tai 0, jos sellaista ei ole.
     * @param koodi Merkkijono, josta etsiä
     * @param index edellinen indeksi
     * @return edellinen merkki
     */
    public char edellinenMerkki(String koodi, int index) {
        if (index < 0) return 0;
        else return koodi.charAt(index);
    }

    /**
     * Korvaa muuttujan arvolla, mutta laittaa muuttujan ympärille latex sulkeet.
     * Hyödyllistä esim. jos negatiivinen luku korotetaan toiseen potenssiin.
     * @param koodi Merkkijono, jossa korvaustehdään
     * @param muuttuja Korvattava muuttuja
     * @param korvaaja Muuttujan arvo
     * @return koodi muutettuna
     */
    public String korvaaMuuttujaSulkeidenKera(String koodi, String muuttuja, String korvaaja) {
        return koodi.replaceFirst(
                "\\b" + muuttuja + "\\b",
                //täytyy käyttää quoteReplacement, koska \\ merkit ei muuten toimisi
                Matcher.quoteReplacement("\\left(" + korvaaja + "\\right)"));
    }

    /**
     * Kokoaa merkkijono taulukon yhdeksi merkkijonoksi, jossa taulukon alkiot
     * on jaoteltu rivivaihdoilla
     * @param rivit Merkkijonon rivit taulukkona
     * @return Yhdistetty merkkijono
     */
    public String rivitYhdeksiMerkkijonoksi(String[] rivit) {
        StringBuilder tulos = new StringBuilder(); //Kootaan merkkijono taulukko yhdeksi merkkijonoksi
        for (String rivi : rivit) {
            tulos.append(rivi).append("\n");
        }
        if (tulos.length() > 0) tulos.setLength(tulos.length()-1);
        return tulos.toString();
    }

    /**
     * Poistaa välilyönnit, joita ennen ei ole \-merkkiä.
     * Ei myöskään poista välilyöntejä riveiltä, jotka alkavat &lt;t&gt; tagilla.
     * @param koodi Merkkijono, josta välilyönnit poistetaan
     * @return Merkkijono, josta ollaan poistettu välilyönnit
     */
    public String poistaValilyonnitLatexKielesta(String koodi) {
        String[] rivit = koodi.split("\n");
        for (int i = 0; i < rivit.length; i++) {
            String rivi = rivit[i];
            //Jos välilyöntiä edeltää \, se pidetään
            rivi = rivi.replaceAll("(?<!\\\\) ", "");
            if (!rivi.startsWith("<t>")) {
                rivit[i] = rivi;
            }
        }
        return rivitYhdeksiMerkkijonoksi(rivit);
    }

    /**
     * Funktio tekee nämä asiat
     * <ol>
     *     <li>Korvaa +- merkit pelkällä miinus merkillä</li>
     *     <li>Korvaa -- merkit pelkällä plus merkillä</li>
     *     <li>Poistaa tuntemattomien arvojen kertomisen yhdellä 1x == x</li>
     *     <li>Poistaa turhat kertomerkit 8*x == 8x</li>
     *     <li>Poistaa turhat plus merkit +8-3 == 8-3</li>
     * </ol>
     * @param koodi Siistittävä koodi
     * @return Koodi siistittynä
     */
    public String siistiMatematiikkaa(String koodi) {
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
        return koodi;
    }

    /**
     * Tätä funktiota kutsutaan vain, latex koodi riveille ei teksti riveille.
     * Funktio tekee seuraavat asiat:
     * <ol>
     *     <li>Muuttaa pilkut latex pilkuiksi {,}</li>
     *     <li>Muuttaa kertomerkit latex kertomerkeiksi \cdot</li>
     * </ol>
     * @param rivi Muokattava rivi
     * @return Muokattu rivi
     */
    public String valmisteleLatexKoodiRivi(String rivi) {
        //korvataan pilkut latexin pilkuilla
        rivi = rivi.replaceAll(",","{,}");
        //korvataan * merkit latexin \cdot merkillä
        rivi = rivi.replaceAll("\\*",Matcher.quoteReplacement("\\cdot"));
        return rivi;
    }
}

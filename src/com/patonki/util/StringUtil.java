package com.patonki.util;

import com.patonki.KoodiParser;
import com.patonki.virheet.KomentoVirhe;
import com.patonki.virheet.ParserException;

import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Sisältää apumetodeja erilaisiin merkkijono operaatioihin.
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
        if (index < 0) return 0;
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
        if (index >= koodi.length()) return 0;
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
        if (muuttuja.isEmpty()) return koodi;
        return koodi.replaceFirst(
                "\\b" + muuttuja + "\\b",
                //täytyy käyttää quoteReplacement, koska \\ merkit ei muuten toimisi
                Matcher.quoteReplacement("\\left(" + korvaaja + "\\right)"));
    }

    /**
     * Kokoaa merkkijono taulukon yhdeksi merkkijonoksi, jossa taulukon alkiot
     * on jaoteltu rivivaihdoilla. Ei lisää rivejä, joiden arvo on &lt;ignore&gt;
     * @param rivit Merkkijonon rivit taulukkona
     * @return Yhdistetty merkkijono
     */
    public String rivitYhdeksiMerkkijonoksi(String[] rivit) {
        StringBuilder tulos = new StringBuilder(); //Kootaan merkkijono taulukko yhdeksi merkkijonoksi
        for (String rivi : rivit) {
            if (rivi.equals("<ignore>")) continue;
            tulos.append(rivi).append("\n");
        }
        if (tulos.length() > 0) tulos.setLength(tulos.length()-1);
        return tulos.toString();
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

    /**
     * Koodissa pystyy määrittelemään muuttujia tällä tavalla:
     * <p>
     *      <code>
     *           #muuttujaNimi = arvo
     *      </code>
     * </p>
     * <p>
     *     Tämä funktio kerää nämä muuttujat ja muuttujien arvot parametrina annettuihin listoihin
     *     Poistaa myös rivit koodista joissa muuttujia määritellään merkitsemällä rivit &lt;ignore&gt;
     *     tagin avulla.
     * </p>
     * @param koodi KoodiEditorissa oleva koodi
     * @param muuttujat Lista johon muuttujat laitetaan
     * @param arvot Lista johon muuttujien arvot laitetaan
     * @return Koodi, josta määritysrivit ollaan poistettu
     */
    public String loydaKoodissaMaaritellytMuuttujat(String koodi, Collection<String> muuttujat, Collection<String> arvot) {
        String[] rivit = koodi.split("\n");
        Pattern setterPatern = Pattern.compile("#\\s*[^=()]+\\s*=(?!=)"); //Esim: #aine = Na_3SO
        for (int i = 0; i < rivit.length; i++) {
            String line = rivit[i];
            Matcher matcher = setterPatern.matcher(line);
            if (matcher.find()) {
                //Ennen yhtäsuuruus merkkiä, mutta ei #-merkkiä
                String muuttujaName = line.substring(1, line.indexOf("=")).replaceAll(" ", "");
                //Tarkistetaan koostuuko muuttujaNimi peruskirjaimista
                if (muuttujaName.replaceAll("\\w","").length() != 0) {
                    continue;
                }
                muuttujat.add(muuttujaName);
                //Jälkeen yhtäsuuruus merkin
                String muuttujaArvo = line.substring(line.indexOf("=") + 1);
                arvot.add(muuttujaArvo);
                rivit[i] = "<ignore>"; //poistetaan rivi
            }
        }
        return rivitYhdeksiMerkkijonoksi(rivit);
    }

    /**
     * Korvaa koodissa olevat muuttujat niiden arvoilla. Korvaa muuttujan vain silloin
     * jos se ei ole osa isompaa sanaa.
     * <p>
     *     Esim. korvattava: a, koodi = ab --&gt; Ei korvaa mitään, mutta
     *     korvattava: a, koodi = a + b --&gt; Korvaa a:n
     * </p>
     * <p>
     *     Jos muuttujan tilalle tulee negatiivinen luku koodi varmistaa ettei matemaattisen
     *     laskun merkitys muutu.
     * </p>
     * @param koodi Koodi, johon korvaukset tehdään
     * @param muuttujat Korvattavat muuttujat
     * @param arvot Muuttujien arvot. Huom: indeksissä i olevan muuttujan arvo on index i arvot listassa
     * @return Koodi, johon on tehty korvaukset
     * @throws ParserException Jos muuttujalla ja funktiolla on sama nimi
     */
    public String korvaaMuuttujatArvoilla(String koodi, List<String> muuttujat, List<String> arvot) throws ParserException {
        //Huom aloitetaan lopusta, koska siellä ovat koodissa määritellyt muuttujat
        for (int i = muuttujat.size() - 1; i >= 0; --i) {
            String muuttuja = muuttujat.get(i);
            String muuttujanArvo = arvot.get(i);
            //Muuttuja ei ole osa sanaa \\b tarkoittaa sanan loppua tai alkua
            Matcher matcher = Pattern.compile("\\b" + muuttuja + "\\b").matcher(koodi);
            int kertoja = 0;
            while (matcher.find()) {
                if (kertoja++ >= 1000) throw new ParserException("Muuttujalla ja funktiolla sama nimi");
                int start = matcher.start(); //kohta, josta muuttuja alkaa
                //Miinus luvut korotettuna potenssiin pitää laittaa sulkeiden sisään
                if (muuttujanArvo.startsWith("-") && seuraavaMerkki(koodi, start + 1) == '^') {
                    koodi = korvaaMuuttujaSulkeidenKera(koodi, muuttuja, muuttujanArvo);
                }
                //* -8 --> * (-8)
                else if (muuttujanArvo.startsWith("-") && edellinenMerkki(koodi, start - 1) == '*') {
                    koodi = korvaaMuuttujaSulkeidenKera(koodi, muuttuja, muuttujanArvo);
                } else {
                    koodi = koodi.replaceFirst("\\b" + muuttuja + "\\b", muuttujanArvo);
                }
                //luodaan uusi matcher, koska merkkijono vaihtui
                matcher = Pattern.compile("\\b" + muuttuja + "\\b").matcher(koodi);
            }
        }
        return koodi;
    }

    /**
     * Jos muuttujat on kirjoitettu latexin \text{} tagin sisään, tagi poistetaan ja
     * jäljelle jää pelkkä muuttuja.
     * <p>
     *     <code>\text{a} --&gt; a</code>
     * </p>
     * @param koodi Koodi, johon muutokset tehdään
     * @param muuttujat Muuttujat, joiden ympäriltä tagit poistetaan
     * @return Koodi, johon muutokset on tehty
     */
    public String korvaaTextRuudut(String koodi, Collection<String> muuttujat) {
        if (muuttujat.isEmpty()) return koodi;
        StringBuilder muuttujatBuilder = new StringBuilder();
        for (String muuttuja : muuttujat) {
            muuttujatBuilder.append(muuttuja).append("|");
        }
        if (muuttujatBuilder.length() > 0) muuttujatBuilder.setLength(muuttujatBuilder.length()-1);
        String m = muuttujatBuilder.toString();
        String regex = "\\\\text\\{(?=("+m+"))";
        koodi = koodi.replaceAll("(?<=\\\\text\\{("+m+"))}","");
        return koodi.replaceAll(regex,"");
    }

    /**
     * Käy läpi koodin rivi riviltä ja etsii if-lausekkeita.
     * Jos if-lauseke löytyy ja sen arvoksi muodostuu false, kaikki if-lausekkeen alle kuuluva koodi
     * poistetaan. If lausekkeen lopun määrittää funktio: {@link #viimeinenRiviIfLausekkeessa(String[], int)}
     * If lausekkeen arvon laksemisen hoitaa funktio: {@link #ifLausekeOnTotta(String)}
     * @param koodi Koodi, josta if-lausekkeita etsitään
     * @return Koodi, josta turhat rivit ollaan poistettu
     * @throws ParserException Jos if-lausekkeen arvon laskemisessa on ongelma
     */
    public String ifLausekkeet(String koodi) throws ParserException {
        String[] rivit = koodi.split("\n");
        for (int i = 0; i < rivit.length; i++) {
            String rivi = rivit[i];
            if (rivi.startsWith("#if")) {
                int vikaRivi =viimeinenRiviIfLausekkeessa(rivit,i);
                if (!ifLausekeOnTotta(rivi)) {
                    for (int index = i; index <= vikaRivi; index++) {
                        rivit[index]="<ignore>";
                    }
                }
                rivit[i] = "<ignore>";
                if (rivit[vikaRivi].startsWith("#end")) rivit[vikaRivi] = "<ignore>";
            }
        }
        return rivitYhdeksiMerkkijonoksi(rivit);
    }
    private static final Pattern FUNC_PATTERN = Pattern.compile("[A-ZÄÖÅ]\\w*\\(");
    /**
     * Suorittaa koodissa olevat funktiot. Esim:
     * <p>
     *     <code>arvo: #count(3+4) --&gt; arvo:7</code>
     * </p>
     * @param koodi Koodi, jossa funktiot korjataan funktioiden arvolla.
     * @param parser KoodiParser, jolla komennot suoritetaan
     * @return Koodi, jossa muutokset on tehty
     * @throws ParserException Komentojen etsimisessä jokin virhe
     */
    public String maaritaFunktioidenArvot(String koodi, KoodiParser parser) throws ParserException {
        String[] rivit = koodi.split("\n");
        for (int j = 0; j < rivit.length; j++) {
            String rivi = funktiotRivissa(rivit[j],parser);
            rivit[j] = rivi;
        }
        return rivitYhdeksiMerkkijonoksi(rivit);
    }

    /**
     * Apumetodi, jota {@link #maaritaFunktioidenArvot(String, KoodiParser)} käyttää
     * Suorittaa itseään recurssiivisesti löytääkseen funktiot funktioiden sisällä.
     * @param rivi Käsiteltävä merkkijono
     * @param parser KoodiParser, jolla komennot suoritetaan
     * @return Rivi käsiteltynä
     * @throws ParserException Komennon suorituksessa jokin ongelma
     */
    private String funktiotRivissa(String rivi, KoodiParser parser) throws ParserException {
        Matcher matcher = FUNC_PATTERN.matcher(rivi);
        int i=-1;
        //Etsitään funktioita
        while (matcher.find(i+1)) {
            i = matcher.start();
            int suljeAukeaa = rivi.indexOf("(", i);
            int suljeSulkeutuu = sulkevaSulje(rivi,suljeAukeaa);
            if (suljeAukeaa == -1 || suljeSulkeutuu == -1) {
                throw new ParserException("Komento kirjoitettu väärin oikea tapa: Nimi(p1;p2)\n"+rivi); //virhe
            }

            String komennonNimi = rivi.substring(i, suljeAukeaa);
            String p = rivi.substring(suljeAukeaa + 1, suljeSulkeutuu); //sulkeiden sisällä oleva teksti
            String[] params = p.split(";");
            for (int k = 0; k < params.length; k++) {
                //Ajetaan funktio recursiivisesti
                params[k] = funktiotRivissa(params[k].trim(),parser);
            }
            //saattaa epännistua
            try {
                String value = parser.suoritaKomento(komennonNimi,params);
                rivi = rivi.replace(komennonNimi + "(" + p + ")", value);
                matcher = FUNC_PATTERN.matcher(rivi); //merkkijono muuttui, eli tarvitaan uusi matcheri
            } catch (ParserException ignored) {
                System.out.println("Ei tunnettu komento "+komennonNimi);
            }
            if (i >= rivi.length()) break;
        }
        return rivi;
    }
    /**
     * Palauttaa indeksin, jossa suljetta vastaava sulkeva sulje on
     * <p>Esim: Laske(9+(4+2)) --&gt; palauttaa viimeisen indeksin</p>
     * @param rivi Merkkijono, josta sulkevaa sulkua etsitään
     * @param vasenSuljeIndeksi Avautuvan sulkeen indeksi
     * @return Sulkeutuvan sulkeen indeksi tai -1, jos ei löydy
     */
    public int sulkevaSulje(String rivi, int vasenSuljeIndeksi) {
        int deepness = 0;
        for (int i = vasenSuljeIndeksi; i < rivi.length(); i++) {
            char c = rivi.charAt(i);
            if (c == '(') deepness++;
            if (c == ')') deepness--;
            if (deepness == 0) return i;
        }
        return -1;
    }

    /**
     * Sama kuin {@link #sulkevaSulje(String, int)}, mutta eri suuntaan
     * @param rivi Merkkijono, josta etsiä
     * @param oikeaSuljeIndeksi Sulkevan sulkeen indeksi
     * @return Avautuvan sulkeen indeksi
     */
    public int avautuvaSulje(String rivi, int oikeaSuljeIndeksi) {
        int deepness = 0;
        for (int i = oikeaSuljeIndeksi; i >= 0; i--) {
            char c = rivi.charAt(i);
            if (c == ')') deepness++;
            if (c == '(') deepness--;
            if (deepness == 0) return i;
        }
        return -1;
    }

    /**
     * Ottaa parametrikseen if-lauseke rivin, eli rivin, josta if-lauseke löydettiin.
     * Palauttaa true tai false riippuen siitä onko if-lausekkeen arvo totta vai ei.
     * <p>Tuetut operaattorit:</p>
     * <ul>
     *     <li>== (sama)</li>
     *     <li>&lt;= pienempi tai yhtä suuri</li>
     *     <li>&gt;= suurempi tai yhtä suuri</li>
     *     <li>&lt; pienempi</li>
     *     <li>&gt; suurempi</li>
     *     <li> != (ei sama)</li>
     * </ul>
     * @param ifLauseke rivi, josta if-lauseke löydettiin Esim. if a == 5
     * @return if-lausekkeen arvo
     * @throws ParserException Jos vertailija operaattoria ei tunneta tai if-lauseke on muuten kirjoitettu
     * väärin
     */
    private boolean ifLausekeOnTotta(String ifLauseke) throws ParserException {
        ifLauseke = ifLauseke.replaceAll("(\\s|\\\\)","");
        Matcher matcher = Pattern.compile("(==|<=|>=|<|>|!=)").matcher(ifLauseke);
        if (matcher.find()) {
            String vertailija = matcher.group();
            String eka = ifLauseke.substring(3,matcher.start());
            String toka = ifLauseke.substring(matcher.end());
            switch (vertailija) {
                case "==":
                    return eka.equals(toka);
                case "<=":
                    return Double.parseDouble(eka) <= Double.parseDouble(toka);
                case ">=":
                    return Double.parseDouble(eka) >= Double.parseDouble(toka);
                case "<":
                    return Double.parseDouble(eka) < Double.parseDouble(toka);
                case ">":
                    return Double.parseDouble(eka) > Double.parseDouble(toka);
                case "!=":
                    return Double.parseDouble(eka) != Double.parseDouble(toka);
                default:
                    throw new ParserException("Ei ole vertailija: "+vertailija);
            }
        }
        throw new ParserException("If lauseke kirjoitettu väärin: "+ifLauseke);
    }

    /**
     * Etsii kohdan, jossa if-lauseke loppuu. If-lauseke loppuu joko toiseen
     * if-lausekkeeseen tai #end komentoon tai viimeiseen riviin
     * @param rivit Koodin rivit
     * @param aloitusIndeksi Indeksi, josta if-lauseke alkaa
     * @return Indeksi, jossa on viimeinen if-lausekkeeseen kuuluva rivi
     */
    private int viimeinenRiviIfLausekkeessa(String[] rivit, int aloitusIndeksi) {
        for (int i = aloitusIndeksi+1; i < rivit.length; i++) {
            String rivi = rivit[i];
            if (rivi.startsWith("#end")) {
                return i;
            }
            if (rivi.startsWith("#if")) {
                return i-1;
            }
        }
        return rivit.length-1;
    }
}

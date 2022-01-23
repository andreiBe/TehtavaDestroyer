package com.test;

import com.patonki.KoodiParser;
import com.patonki.util.StringUtil;
import com.patonki.virheet.ParserException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class StringUtilTest {
    private static StringUtil stringUtil;
    @BeforeAll
    static void setUp() {
        stringUtil = new StringUtil();
    }

    @Test
    void getMerkitsevatNumerot() {
        String[] arvot = {"0,0042","0,0000543"}; //edeltävät nollat
        int tulos = stringUtil.getMerkitsevatNumerot(arvot);
        assertEquals(2,tulos);

        arvot = new String[]{"6.32", "5.42", "8,4212"}; //normaalit desimaaliluvut
        tulos = stringUtil.getMerkitsevatNumerot(arvot);
        assertEquals(3,tulos);

        arvot = new String[] {"600","1000","500"}; //lopussa olevat nollat
        tulos = stringUtil.getMerkitsevatNumerot(arvot);
        assertEquals(3,tulos);

        arvot = new String[] {"7,00004"}; //välissä olevat nollat
        tulos = stringUtil.getMerkitsevatNumerot(arvot);
        assertEquals(6,tulos);

        arvot = new String[0];
        tulos = stringUtil.getMerkitsevatNumerot(arvot);
        assertEquals(Integer.MAX_VALUE,tulos);
    }

    @Test
    void seuraavaMerkki() {
        String koodi = "h()";
        char c = stringUtil.seuraavaMerkki(koodi,1);
        assertEquals('(',c);

        c = stringUtil.seuraavaMerkki(koodi,3);
        assertEquals(0,c);
    }

    @Test
    void edellinenMerkki() {
        String koodi = "h()";
        char c = stringUtil.edellinenMerkki(koodi,1);
        assertEquals('(',c);

        c = stringUtil.edellinenMerkki(koodi,3);
        assertEquals(0,c);
    }

    @Test
    void korvaaMuuttujaSulkeidenKera() {
        String koodi = "7*a";
        String tulos = stringUtil.korvaaMuuttujaSulkeidenKera(koodi,"a","-6");
        assertEquals("7*\\left(-6\\right)",tulos);

        koodi = "7*ab"; //ei korvaa
        tulos = stringUtil.korvaaMuuttujaSulkeidenKera(koodi,"a","-6");
        assertEquals(koodi,tulos);

        tulos = stringUtil.korvaaMuuttujaSulkeidenKera(koodi,"","7");
        assertEquals(koodi,tulos);

        tulos = stringUtil.korvaaMuuttujaSulkeidenKera(koodi,"ab","");
        assertEquals("7*\\left(\\right)",tulos);
    }

    @Test
    void rivitYhdeksiMerkkijonoksi() {
        String[] rivit = new String[] {"rivi1","rivi2","rivi3"};
        String tulos = stringUtil.rivitYhdeksiMerkkijonoksi(rivit);
        assertEquals("rivi1\nrivi2\nrivi3",tulos);

        rivit = new String[0];
        tulos = stringUtil.rivitYhdeksiMerkkijonoksi(rivit);
        assertEquals("",tulos);
    }

    @Test
    void siistiMatematiikkaa() {
        String koodi = "+9+-8--3*x+1*z";
        String tulos = stringUtil.siistiMatematiikkaa(koodi);
        assertEquals("9-8+3x+z",tulos);
    }

    @Test
    void valmisteleLatexKoodiRivi() {
        String rivi = "8,4*7";
        String tulos = stringUtil.valmisteleLatexKoodiRivi(rivi);
        assertEquals("8{,}4\\cdot7",tulos);
    }

    @Test
    void loydaKoodissaMaaritellytMuuttujat() {
        String koodi =
                        "#jou=9\n" +
                        "normaali rivi = 34\n" +
                        "#funktio(7=4)\n" +
                        "#Toinen=#func(65)";
        ArrayList<String> muuttujat = new ArrayList<>();
        ArrayList<String> arvot = new ArrayList<>();
        String tulos = stringUtil.loydaKoodissaMaaritellytMuuttujat(koodi,muuttujat,arvot);
        assertEquals(new ArrayList<>(Arrays.asList("jou", "Toinen")),muuttujat);
        assertEquals(new ArrayList<>(Arrays.asList("9", "#func(65)")),arvot);
        String expected = "normaali rivi = 34\n" +
                            "#funktio(7=4)";
        assertEquals(expected,tulos);
    }

    @Test
    void korvaaMuuttujatArvoilla() throws ParserException {
        String koodi = "a+blob=ab 9*a a^3";
        String tulos = stringUtil.korvaaMuuttujatArvoilla(
                koodi,Arrays.asList("a","blob"),Arrays.asList("-6","yep")
        );
        assertEquals("-6+yep=ab 9*\\left(-6\\right) \\left(-6\\right)^3",tulos);
    }

    @Test
    void korvaaTextRuudut() {
        String koodi = "dummy koodia aluksi \\text{ab} \\text{eiMuuttuja} jou";
        String tulos = stringUtil.korvaaTextRuudut(koodi, Collections.singletonList("ab"));
        assertEquals("dummy koodia aluksi ab \\text{eiMuuttuja} jou", tulos);
    }

    @Test
    void ifLausekkeet() throws ParserException {
        String koodi = "#if 8==8\n" +
                        "Ei poisteta\n" +
                        "#end\n"+
                        "#if 3<2\n" +
                        "poistetaan";
        String tulos = stringUtil.ifLausekkeet(koodi);
        String expected = "Ei poisteta";
        assertEquals(expected,tulos);
    }

    @Test
    void maaritaFunktioidenArvot() throws ParserException {
        KoodiParser parser = new KoodiParser();
        parser.setMerkitsevatNumerot(2);

        String koodi = "tulos: Laske(9.7+3.2;2)test";
        String tulos = stringUtil.maaritaFunktioidenArvot(koodi,parser);
        String expected = "tulos: 13test"; //huom pyöristys kahden merkitsevän numeron tarkkuudelle
        assertEquals(expected,tulos); //mahdollinen tulos: 12,899999999999...

        koodi = "tulos: Laske(9.7+3.2;+2)test";
        tulos = stringUtil.maaritaFunktioidenArvot(koodi,parser);
        expected = "tulos: 12.90test";
        assertEquals(expected,tulos);

        koodi = "tulos: Laske(9.7+3.2;-1)test";
        tulos = stringUtil.maaritaFunktioidenArvot(koodi,parser);
        expected = "tulos: 10test";
        assertEquals(expected,tulos);

        koodi = "tulos: Laske(9.7+3.2;5)test";
        tulos = stringUtil.maaritaFunktioidenArvot(koodi,parser);
        expected = "tulos: 12.900test";
        assertEquals(expected,tulos);

        koodi = "Laske((-5)^2-4*1*3)";
        tulos = stringUtil.maaritaFunktioidenArvot(koodi,parser);
        expected = "13";
        assertEquals(expected,tulos);
    }
}
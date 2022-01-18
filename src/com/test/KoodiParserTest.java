package com.test;

import com.patonki.Instruction;
import com.patonki.KoodiParser;
import com.patonki.virheet.ParserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class KoodiParserTest {
    private KoodiParser koodiParser;
    @BeforeEach
    void setUp() {
        koodiParser = new KoodiParser();
    }


    @Test
    void parse() throws ParserException {
        String[] muuttujat = {"a","b","c"};
        String[] arvot = {"lol","8","1,98743"};
        String koodi = "a+b*x / c\n<t>just text,";
        Instruction expected = new Instruction("lol+8x/1{,}98743",false);
        Instruction expected2 = new Instruction("just text,",true);
        List<Instruction> result = koodiParser.parse(koodi,muuttujat,arvot);
        assertEquals(expected.getMessage(), result.get(0).getMessage());
        assertEquals(expected2.getMessage(),result.get(1).getMessage());
        assertEquals(expected2.isJustText(),result.get(1).isJustText());
    }

    @Test
    void korvaaMuuttujatArvoilla() {
        String koodi = "dublin b aku cd-soitin a c";
        String[] muuttujat = {"a","b","c"};
        String[] arvot = {"8","0","2"};
        String tulos = koodiParser.korvaaMuuttujatArvoilla(koodi,
                new ArrayList<>(Arrays.asList(muuttujat)),
                new ArrayList<>(Arrays.asList(arvot)));
        String expected = "dublin 0 aku cd-soitin 8 2";
        assertEquals(expected,tulos);
    }

    @Test
    void maaritaFunktioidenArvot() throws ParserException {
        koodiParser.setMerkitsevatNumerot(2);

        String koodi = "tulos: #count[9.7+3.2;2]test";
        String tulos = koodiParser.maaritaFunktioidenArvot(koodi);
        String expected = "tulos: 13test"; //huom pyöristys kahden merkitsevän numeron tarkkuudelle
        assertEquals(expected,tulos); //mahdollinen tulos: 12,899999999999...

        koodi = "tulos: #count[9.7+3.2;+2]test";
        tulos = koodiParser.maaritaFunktioidenArvot(koodi);
        expected = "tulos: 12.90test";
        assertEquals(expected,tulos);

        koodi = "tulos: #count[9.7+3.2;-1]test";
        tulos = koodiParser.maaritaFunktioidenArvot(koodi);
        expected = "tulos: 10test";
        assertEquals(expected,tulos);

        koodi = "tulos: #count[9.7+3.2;5]test";
        tulos = koodiParser.maaritaFunktioidenArvot(koodi);
        expected = "tulos: 12.900test";
        assertEquals(expected,tulos);

        koodi = "#count[(-5)^2-4*1*3]";
        tulos = koodiParser.maaritaFunktioidenArvot(koodi);
        expected = "13";
        assertEquals(expected,tulos);
    }

}
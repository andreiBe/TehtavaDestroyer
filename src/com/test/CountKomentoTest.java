package com.test;

import com.patonki.Komento;
import com.patonki.KoodiParser;
import com.patonki.komennot.CountKomento;
import com.patonki.virheet.KomentoVirhe;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CountKomentoTest {

    @Test
    void run() throws KomentoVirhe {
        Komento komento = new CountKomento();
        KoodiParser koodiParser = new KoodiParser();
        koodiParser.setMerkitsevatNumerot(2);
        //HUOM: StringUtilsTest testaa Laske komentoa jo paljon
        String tulos = komento.run(new String[] {"9+2"},koodiParser);
        assertEquals("11",tulos);
        tulos = komento.run(new String[] {"1/3","int"},koodiParser);
        assertEquals("0",tulos);
        Assertions.assertThrows(KomentoVirhe.class, () -> {
            komento.run(new String[] {"9+3","lol"},koodiParser);
        });
        Assertions.assertThrows(KomentoVirhe.class, () ->komento.run(new String[] {"(-5)^0.5"},koodiParser));
        Assertions.assertThrows(KomentoVirhe.class, ()->komento.run(new String[] {"ab+32"},koodiParser));

        tulos = komento.run(new String[] {"1/3"},koodiParser);
        assertEquals("0,33333",tulos);
    }
}
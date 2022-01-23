package com.test;

import com.patonki.Komento;
import com.patonki.KoodiParser;
import com.patonki.komennot.LaskeMoolimassa;
import com.patonki.virheet.KomentoVirhe;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LaskeMoolimassaTest {

    @Test
    void run() throws KomentoVirhe {
        KoodiParser parser = new KoodiParser();
        Komento komento = new LaskeMoolimassa();

        String tulos = komento.run(new String[] {"NaCl"},parser);
        String oikeaVastaus = "M(Na)+M(Cl)=(22.9897+35.453) \\frac{g}{mol}=58.443\\frac{g}{mol}";
        assertEquals(oikeaVastaus,tulos);

        tulos = komento.run(new String[] {"C_12O_6H_12"},parser);
        oikeaVastaus = "12\\cdot M(C)+6\\cdot M(O)+12\\cdot M(H)=(12*12.0107+6*15.9994+12*1.0079) \\frac{g}{mol}=252.22\\frac{g}{mol}";
        assertEquals(oikeaVastaus,tulos);
    }
}
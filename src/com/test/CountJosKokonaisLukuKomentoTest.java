package com.test;

import com.patonki.KoodiParser;
import com.patonki.komennot.CountJosKokonaisLukuKomento;
import com.patonki.virheet.KomentoVirhe;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CountJosKokonaisLukuKomentoTest {
    private final KoodiParser koodiParser = new KoodiParser();
    private final CountJosKokonaisLukuKomento komento = new CountJosKokonaisLukuKomento();
    private String run(String[] things) {
        try {
            return komento.run(things,koodiParser);
        } catch (KomentoVirhe komentoVirhe) {
            return komentoVirhe.getKorvaavaTeksti();
        }
    }
    @Test
    void run() {
        String tulos = run(new String[] {"\\frac{5{,}0}{1{,}}"});
        assertEquals("=5",tulos);

        tulos = run(new String[] {"\\frac{6}{0}"});
        assertEquals("Ei\\ voi\\ laskea",tulos);

        tulos = run(new String[] {"8/3"});
        assertEquals("",tulos);

        tulos = run(new String[]{"\\sqrt{53}","jou"});
        assertEquals("jou",tulos);

        assertThrows(KomentoVirhe.class, () ->
                komento.run(new String[] {"a+5"},koodiParser));
    }
}
package com.test;

import com.patonki.komennot.util.Alkuaine;
import com.patonki.komennot.util.KemiaArvot;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class KemiaArvotTest {
    private static KemiaArvot kemiaArvot;
    @BeforeAll
    static void setUp() {
        kemiaArvot = new KemiaArvot();
    }

    @Test
    void simppeliMuoto() {
        String yhdiste = "(Na_2Cl)_3O_2";
        String tulos = kemiaArvot.simppeliMuoto(yhdiste);
        String oikeaVastaus = "NaNaClNaNaClNaNaClOO";
        assertEquals(oikeaVastaus,tulos);
        System.out.println(kemiaArvot.keraaAlkuAineet(oikeaVastaus));
    }

    @Test
    void keraaAlkuAineet() {
        String yhdiste = "(Na_2Cl_2S_4)_2S_3O_2";
        List<Alkuaine> tulos = kemiaArvot.keraaAlkuAineet(kemiaArvot.simppeliMuoto(yhdiste));
        System.out.println(tulos);
    }
}
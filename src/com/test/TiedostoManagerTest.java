package com.test;

import com.patonki.KaavaTiedosto;
import com.patonki.util.TiedostoManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class TiedostoManagerTest {
    private static final TiedostoManager tiedostoManager = new TiedostoManager();
    @BeforeAll
    static void setUp() {
        File file = new File("test/");
        assert file.exists() || file.mkdir();
        tiedostoManager.vaihdaPohjatKansio("test/");
    }

    @Test
    void kopioiTiedostoKansioon() {
        String tiedosto = "/defaultFiles/kemia.txt";
        String toinenTiedosto = "/defaultFiles/jou.lma";
        assert tiedostoManager.kopioiTiedostoKansioon(tiedosto);
        assert tiedostoManager.kopioiTiedostoKansioon(toinenTiedosto);
    }

    @Test
    void lueLMathTiedosto() throws IOException {
        tiedostoManager.kopioiTiedostoKansioon("/defaultFiles/jou.lma");
        KaavaTiedosto tiedosto = tiedostoManager.lueLMathTiedosto("test/jou.lma","jou");
        System.out.println("Lmath:"+tiedosto.getKoodi());
        System.out.println("Lmath:"+tiedosto.getMuuttujat());
    }

    @Test
    void tiedostotPohjatKansiossa() {
        tiedostoManager.tiedostotPohjatKansiossa();
    }

    @Test
    void saveFile() throws IOException {
        tiedostoManager.saveFile("hello.txt","jouuu","a,b");
        Assertions.assertThrows(IOException.class, ()->tiedostoManager.saveFile("hello.lma","crash","a,b"));
    }

    @Test
    void uniqueFile() {
        tiedostoManager.uniqueFile();
    }
    @Test
    void renameFile() {
        assert tiedostoManager.renameFile("hello.txt","jou");
    }
    @Test
    void readFile() throws IOException {
        KaavaTiedosto tiedosto = tiedostoManager.readFile("jou.txt");
        assertEquals("jouuu",tiedosto.getKoodi());
    }
    @Test
    void deleteFile() {
        assert tiedostoManager.deleteFile("jou.txt");
        File file = new File("test/");
        for (File f : Objects.requireNonNull(file.listFiles())) {
            assert f.delete();
        }
        assert file.delete();
    }
}
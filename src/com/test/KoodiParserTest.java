package com.test;

import com.patonki.Instruction;
import com.patonki.KaavaTiedosto;
import com.patonki.KoodiParser;
import com.patonki.util.StringUtil;
import com.patonki.util.TiedostoManager;
import com.patonki.virheet.ParserException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class KoodiParserTest {
    private KoodiParser koodiParser;
    private final String[][] inputs = new String[][] {
            new String[] {"Na_2SO_4","3,2","142,046","0,40"},
            new String[] {"-3","-2","1"},
            new String[] {},
            new String[] {"4","3","3","8"}
    };
    private final String[] EMPTY = new String[0];
    @BeforeEach
    void setUp() {
        koodiParser = new KoodiParser();
    }

    @ParameterizedTest
    @ValueSource(ints = {2})
    void errors(int number) throws IOException {
        KaavaTiedosto tiedosto = new TiedostoManager().readFile(number+".txt");
        Assertions.assertThrows(ParserException.class, () -> {
          koodiParser.parse(tiedosto.getKoodi(),tiedosto.getMuuttujatArray(),inputs[number]);
        });
    }
    @ParameterizedTest
    @ValueSource(ints = {0,1})
    void files(int number) throws IOException {
        KaavaTiedosto tiedosto = new TiedostoManager().readFile(number+".txt");
        List<Instruction> instructions = new ArrayList<>();
        Assertions.assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
            instructions.addAll(koodiParser.parse(tiedosto.getKoodi(),tiedosto.getMuuttujatArray(), inputs[number]));
        });
        Scanner oikeaVastaus = new Scanner(Paths.get("pohjat/result/"+number+".txt"));
        StringBuilder oikeaVastausMerkkijono = new StringBuilder();
        StringBuilder vastaus = new StringBuilder();
        int i = 0;
        while (oikeaVastaus.hasNextLine()) {
            String line = oikeaVastaus.nextLine();
            oikeaVastausMerkkijono.append(line).append("\n");
            if (i < instructions.size()) {
                vastaus.append(instructions.get(i).getMessage()).append("\n");
            }
            i++;
        }
        while (i < instructions.size()) {
            vastaus.append(instructions.get(i).getMessage()).append("\n");
            i++;
        }
        assertEquals(oikeaVastausMerkkijono.toString(), vastaus.toString());
    }
    @Test
    void parse() throws ParserException {
        String[] muuttujat = {"a","b","c"};
        String[] arvot = {"lol","8","1,98743"};
        String koodi = "a+b*x / c\n<t>just text,";
        Instruction expected = new Instruction("lol+8x / 1{,}98743",false);
        Instruction expected2 = new Instruction("just text,",true);
        List<Instruction> result = koodiParser.parse(koodi,muuttujat,arvot);
        assertEquals(expected.getMessage(), result.get(0).getMessage());
        assertEquals(expected2.getMessage(),result.get(1).getMessage());
        assertEquals(expected2.isJustText(),result.get(1).isJustText());
    }
    @Test
    void giac() throws ParserException {
        String koodi = "Ratkaise(x^2=9,x)";
        List<Instruction> result = koodiParser.parse(koodi,new String[0],new String[0]);
        assertEquals("-3.0\\vee 3.0",result.get(0).getMessage());

        koodi = "Ratkaise(a=b*c*d,c)";
        result = koodiParser.parse(koodi,EMPTY,EMPTY);
        assertEquals("a/b/d",result.get(0).getMessage());

        koodi = "Ratkaise(a/2=8,a)";
        result = koodiParser.parse(koodi,EMPTY,EMPTY);
        assertEquals("16.0",result.get(0).getMessage());
    }

}
package com.patonki;

import com.fathzer.soft.javaluator.DoubleEvaluator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

interface Komento {
    String run(String[] parameters);
}
public class KoodiParser {
    private final HashMap<String,Komento> komennot = new HashMap<>();

    public KoodiParser() {
        komennot.put("count", (params) -> {
            DoubleEvaluator evaluator = new DoubleEvaluator();
            String lasku = params[0].replace(",",".");
            lasku = lasku.replace("\\left","");
            lasku = lasku.replace("\\right","");
            int merkitsevat;
            if (params.length > 1) {
                String tarkkuus = params[1];
                if (tarkkuus.startsWith("+")) {
                    merkitsevat = this.merkitsevatNumerot + Integer.parseInt(tarkkuus.substring(1));
                }
                else if (tarkkuus.startsWith("-")) {
                    merkitsevat = this.merkitsevatNumerot - Integer.parseInt(tarkkuus.substring(1));
                }
                else if (tarkkuus.equals("int")) {
                    double tulos = evaluator.evaluate(lasku);
                    return String.valueOf(Math.round(tulos));
                }
                else {
                    merkitsevat = Integer.parseInt(tarkkuus);
                }
            } else merkitsevat = this.merkitsevatNumerot;
            double tulos = evaluator.evaluate(lasku);

            BigDecimal pyoristetty = new BigDecimal(tulos).round(new MathContext(merkitsevat));
            return pyoristetty.toPlainString();
        });
    }
    private int merkitsevatNumerot;
    public int getMerkitsevatNumerot(String[] arvot) {
        int min = Integer.MAX_VALUE;
        for (String arvo : arvot) {
            arvo = arvo.replace(",",".");
            try {
                int tulos = 0;
                Double.parseDouble(arvo);
                String[] osat = arvo.split("(^0+(\\.?)0*|(~\\.)0+$|\\.)");
                for (String s : osat) {
                    tulos += s.length();
                }
                min = Math.min(min,tulos);
            } catch (NumberFormatException ignored) {}
        }
        return min;
    }
    private char charAt(String koodi, int index, int dir) {
        while (index > 0 && index < koodi.length()) {
            if (koodi.charAt(index)==' ')index+=dir;
            else return koodi.charAt(index);
        }
        return 0;
    }
    private boolean onKirjainTaiNumero(char c) {
        if (c >= 'a' && c <= 'z') return true;
        if (c >= 'A' && c <= 'A') return true;
        if (c == 'ä' | c == 'ö' | c == 'å') return true;
        if (c == 'Ä' | c == 'Ö' | c== 'Å') return true;
        return c >= '0' && c <= '9';
    }
    public String korvaaMuuttujatArvoilla(String koodi, String[] muuttujat, String[] arvot) {
        for (int i = 0; i < muuttujat.length; i++) {
            String muuttuja = muuttujat[i];
            Matcher matcher = Pattern.compile("\\b"+muuttuja+"\\b").matcher(koodi);
            String korvaaja = arvot[i];
            while (matcher.find()) {
                int start = matcher.start();
                if (korvaaja.startsWith("-") && charAt(koodi,start+1,1)=='^') {
                    koodi = koodi.replaceFirst(
                            "\\b"+muuttuja+"\\b",
                            Matcher.quoteReplacement("\\left("+korvaaja+"\\right)"));
                } else {
                    koodi = koodi.replaceFirst("\\b"+muuttuja+"\\b",korvaaja);
                }
                matcher = Pattern.compile("\\b"+muuttuja+"\\b").matcher(koodi);
            }
            //koodi = koodi.replaceAll("\\b"+muuttuja+"\\b", korvaaja);
        }
        return koodi;
    }

    public void setMerkitsevatNumerot(int merkitsevatNumerot) {
        this.merkitsevatNumerot = merkitsevatNumerot;
    }

    public String maaritaFunktioidenArvot(String koodi) {
        int i;
        while ((i = koodi.lastIndexOf("#")) != -1) {
            int suljeAukeaa = koodi.indexOf("[",i);
            int suljeSulkeutuu = koodi.indexOf("]",suljeAukeaa);
            String command = koodi.substring(i+1, suljeAukeaa);
            String p = koodi.substring(suljeAukeaa+1,suljeSulkeutuu);
            String[] params = p.split(";");

            String value = komennot.get(command).run(params);

            koodi = koodi.replace("#"+command+"["+p+"]",value);
        }
        return koodi;
    }
    public List<Instruction> parse(String koodi, String[] muuttujat, String[] arvot) {
        ArrayList<Instruction> ohjeet = new ArrayList<>();

        koodi = korvaaMuuttujatArvoilla(koodi,muuttujat,arvot);
        this.merkitsevatNumerot = getMerkitsevatNumerot(arvot);
        koodi = maaritaFunktioidenArvot(koodi);

        koodi = koodi.replaceAll("\\+\\s*-","-");
        koodi = koodi.replaceAll("-\\s*-","+");
        koodi = koodi.replaceAll("1\\s*\\*(?=\\s*[a-zäxöåA-ZXÄÖÅ])","");
        koodi = koodi.replaceAll("\\*\\s*(?=[a-zäöåA-ZÄÖ])","");
        koodi = koodi.replaceAll("(?<!\\w)\\+","");
        String[] lines = koodi.split("\n");
        for (String line : lines) {
            if (line.startsWith("<t>")) {
                ohjeet.add(new Instruction(line.substring(3), true));
            } else {
                line = line.replaceAll(",","{,}");
                ohjeet.add(new Instruction(line, false));
            }
        }
        return ohjeet;
    }
 }

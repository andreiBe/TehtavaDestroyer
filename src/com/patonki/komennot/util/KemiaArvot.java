package com.patonki.komennot.util;

import com.patonki.virheet.KomentoVirhe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KemiaArvot {
    private static final HashMap<String, Double> moolimassat = new HashMap<>();

    public KemiaArvot() {
        if (moolimassat.size() == 0) {
            moolimassat.put("H", 1.0079);
            moolimassat.put("He", 4.0026);
            moolimassat.put("Li", 6.941);
            moolimassat.put("Be", 9.0122);
            moolimassat.put("B", 10.811);
            moolimassat.put("C", 12.0107);
            moolimassat.put("N", 14.0067);
            moolimassat.put("O", 15.9994);
            moolimassat.put("F", 18.9984);
            moolimassat.put("Ne", 20.1797);
            moolimassat.put("Na", 22.9897);
            moolimassat.put("Mg", 24.305);
            moolimassat.put("Al", 26.9815);
            moolimassat.put("Si", 28.0855);
            moolimassat.put("P", 30.9738);
            moolimassat.put("S", 32.065);
            moolimassat.put("Cl", 35.453);
            moolimassat.put("K", 39.0983);
            moolimassat.put("Ar", 39.948);
            moolimassat.put("Ca", 40.078);
            moolimassat.put("Sc", 44.9559);
            moolimassat.put("Ti", 47.867);
            moolimassat.put("V", 50.9415);
            moolimassat.put("Cr", 51.9961);
            moolimassat.put("Mn", 54.938);
            moolimassat.put("Fe", 55.845);
            moolimassat.put("Ni", 58.6934);
            moolimassat.put("Co", 58.9332);
            moolimassat.put("Cu", 63.546);
            moolimassat.put("Zn", 65.39);
            moolimassat.put("Ga", 69.723);
            moolimassat.put("Ge", 72.64);
            moolimassat.put("As", 74.9216);
            moolimassat.put("Se", 78.96);
            moolimassat.put("Br", 79.904);
            moolimassat.put("Kr", 83.8);
            moolimassat.put("Rb", 85.4678);
            moolimassat.put("Sr", 87.62);
            moolimassat.put("Y", 88.9059);
            moolimassat.put("Zr", 91.224);
            moolimassat.put("Nb", 92.9064);
            moolimassat.put("Mo", 95.94);
            moolimassat.put("Tc", 98.0);
            moolimassat.put("Ru", 101.07);
            moolimassat.put("Rh", 102.9055);
            moolimassat.put("Pd", 106.42);
            moolimassat.put("Ag", 107.8682);
            moolimassat.put("Cd", 112.411);
            moolimassat.put("In", 114.818);
            moolimassat.put("Sn", 118.71);
            moolimassat.put("Sb", 121.76);
            moolimassat.put("I", 126.9045);
            moolimassat.put("Te", 127.6);
            moolimassat.put("Xe", 131.293);
            moolimassat.put("Cs", 132.9055);
            moolimassat.put("Ba", 137.327);
            moolimassat.put("La", 138.9055);
            moolimassat.put("Ce", 140.116);
            moolimassat.put("Pr", 140.9077);
            moolimassat.put("Nd", 144.24);
            moolimassat.put("Pm", 145.0);
            moolimassat.put("Sm", 150.36);
            moolimassat.put("Eu", 151.964);
            moolimassat.put("Gd", 157.25);
            moolimassat.put("Tb", 158.9253);
            moolimassat.put("Dy", 162.5);
            moolimassat.put("Ho", 164.9303);
            moolimassat.put("Er", 167.259);
            moolimassat.put("Tm", 168.9342);
            moolimassat.put("Yb", 173.04);
            moolimassat.put("Lu", 174.967);
            moolimassat.put("Hf", 178.49);
            moolimassat.put("Ta", 180.9479);
            moolimassat.put("W", 183.84);
            moolimassat.put("Re", 186.207);
            moolimassat.put("Os", 190.23);
            moolimassat.put("Ir", 192.217);
            moolimassat.put("Pt", 195.078);
            moolimassat.put("Au", 196.9665);
            moolimassat.put("Hg", 200.59);
            moolimassat.put("Tl", 204.3833);
            moolimassat.put("Pb", 207.2);
            moolimassat.put("Bi", 208.9804);
            moolimassat.put("Po", 209.0);
            moolimassat.put("At", 210.0);
            moolimassat.put("Rn", 222.0);
            moolimassat.put("Fr", 223.0);
            moolimassat.put("Ra", 226.0);
            moolimassat.put("Ac", 227.0);
            moolimassat.put("Pa", 231.0359);
            moolimassat.put("Th", 232.0381);
            moolimassat.put("Np", 237.0);
            moolimassat.put("U", 238.0289);
            moolimassat.put("Am", 243.0);
            moolimassat.put("Pu", 244.0);
            moolimassat.put("Cm", 247.0);
            moolimassat.put("Bk", 247.0);
            moolimassat.put("Cf", 251.0);
            moolimassat.put("Es", 252.0);
            moolimassat.put("Fm", 257.0);
            moolimassat.put("Md", 258.0);
            moolimassat.put("No", 259.0);
            moolimassat.put("Rf", 261.0);
            moolimassat.put("Lr", 262.0);
            moolimassat.put("Db", 262.0);
            moolimassat.put("Bh", 264.0);
            moolimassat.put("Sg", 266.0);
            moolimassat.put("Mt", 268.0);
            moolimassat.put("Rg", 272.0);
            moolimassat.put("Hs", 277.0);
            moolimassat.put("Ds", 280.0);
        }
    }
    public List<Alkuaine> keraaAlkuAineet(String simppeliYhdiste) {
        StringBuilder alkuaine = new StringBuilder();
        ArrayList<Alkuaine> aineet = new ArrayList<>();
        for (int i = 0; i < simppeliYhdiste.length(); i++) {
            char c = simppeliYhdiste.charAt(i);
            if (Character.isUpperCase(c) && alkuaine.length() > 0) {
                String nimi = alkuaine.toString();
                if (aineet.contains(new Alkuaine(nimi,0))) {
                    Alkuaine aine = aineet.stream().filter(a -> a.getNimi().equals(nimi)).findFirst().get();
                    aine.setMaara(aine.getMaara()+1);
                } else aineet.add(new Alkuaine(nimi,1));
                alkuaine.setLength(0);
            }
            alkuaine.append(c);
        }
        String nimi = alkuaine.toString();
        if (aineet.contains(new Alkuaine(nimi,0))) {
            Alkuaine aine = aineet.stream().filter(a -> a.getNimi().equals(nimi)).findFirst().get();
            aine.setMaara(aine.getMaara()+1);
        } else aineet.add(new Alkuaine(nimi,1));
        return aineet;
    }
    public String right(String yhdiste, int index) {
        index++;
        StringBuilder right = new StringBuilder();
        char c;
        while (index < yhdiste.length() && Character.isDigit(c = yhdiste.charAt(index))) {
            right.append(c);
            index++;
        }
        return right.toString();
    }
    public double getMolekyylipaino(String alkuaine) {
        if (!moolimassat.containsKey(alkuaine))
            throw new IllegalArgumentException("Ei arvoa alkuaineelle:"+alkuaine);
        return moolimassat.get(alkuaine);
    }
    public String left(String yhdiste, int index) {
        index--;
        StringBuilder left = new StringBuilder();
        char c;
        while (index >= 0 && Character.isAlphabetic(c = yhdiste.charAt(index))) {
            left.append(c);
            if (Character.isUpperCase(c)) {
                break;
            }
            index--;
        }
        return left.reverse().toString();
    }

    public String simppeliMuoto(String yhdiste) {
        yhdiste = yhdiste.replaceAll("\\s", "");
        int index = -1;
        while ((index = yhdiste.indexOf("_", index + 1)) != -1) {
            if (yhdiste.charAt(index - 1) != ')') {
                String nimi = left(yhdiste, index);
                String maara = right(yhdiste, index);
                String kerrottu = new String(
                        new char[Integer.parseInt(maara)])
                        .replace("\0", nimi);
                yhdiste = yhdiste.replaceAll(nimi + "_" + maara, kerrottu);
            }
        }
        while ((index = yhdiste.lastIndexOf("(")) != -1) {
            int end = yhdiste.indexOf(")",index);
            String sulkeet = yhdiste.substring(index,end+1);
            String maara = right(yhdiste, end+1);
            String kerrottu = new String(
                    new char[Integer.parseInt(maara)]
            ).replace("\0", sulkeet.substring(1, sulkeet.length() - 1));
            yhdiste = yhdiste.replace(sulkeet + "_" + maara, kerrottu);
        }
        return yhdiste;
    }

    public double yhdisteenMolekyyliPaino(String yhdiste) {
        List<Alkuaine> aineet = keraaAlkuAineet(simppeliMuoto(yhdiste));
        double summa = 0;
        for (Alkuaine alkuaine : aineet) {
            summa += alkuaine.getMaara() * getMolekyylipaino(alkuaine.getNimi());
        }
        summa = Math.round(summa*1000)/1000d;
        return summa;
    }
}

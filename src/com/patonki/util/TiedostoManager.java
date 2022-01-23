package com.patonki.util;

import com.patonki.KaavaTiedosto;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Sisältää apumetodeja tiedostojen käsittelyyn.
 */
public class TiedostoManager {
    private static final String OLETUS_TIEDOSTOT_KANSIO = "/defaultFiles";
    private String POHJAT_KANSIO = "pohjat/";
    private final ArrayList<String> tiedostot = new ArrayList<>();

    //käytetään testaamiseen
    public void vaihdaPohjatKansio(String uusiArvo) {
        POHJAT_KANSIO = uusiArvo;
    }
    /**
     * Kerää oletustiedostot, eli valmiit scriptit, jotka ovat jo valmiina asennettuna ohjelmaan.
     * Palauttaa vain sellaiset tiedostot, jotka eivät ole jo valmiiksi /pohjat kansiossa.
     *
     * @return Merkkijono lista Esim: kemia.txt, test.lma, lasku.txt
     */
    private List<String> oletusTiedostot() throws FileNotFoundException {
        InputStream inputStream = TiedostoManager.class.getResourceAsStream("/defaultFiles.txt");
        //defaultFiles.txt sisältää tiedon siitä mitä tiedostoja pitää hakea
        if (inputStream == null) throw new FileNotFoundException("/defaultFiles.txt tiedostoa ei löydy");
        Scanner scanner = new Scanner(inputStream);
        ArrayList<String> oletusTiedostot = new ArrayList<>();
        while (scanner.hasNextLine()) {
            String tiedosto = scanner.nextLine();
            if (!this.tiedostot.contains(tiedosto)) {
                if (kopioiTiedostoKansioon(OLETUS_TIEDOSTOT_KANSIO + "/" + tiedosto)) { //kopioiminen onnistui
                    oletusTiedostot.add(tiedosto);
                }
            }
        }
        return oletusTiedostot;
    }

    /**
     * Kopio halutun tiedoston /pohjat kansioon. Sisäisesti käyttää funktiota {@link Files#copy(InputStream, Path, CopyOption...)}
     * Toimii kaikilla tiedostoilla
     *
     * @param sijainti Kopioitavan tiedoston sijainti
     * @return Onnistuiko kopioiminen
     */
    public boolean kopioiTiedostoKansioon(String sijainti) {
        String tiedostonimi = sijainti.substring(sijainti.lastIndexOf("/") + 1);
        try {
            Files.copy(TiedostoManager.class.getResourceAsStream(sijainti), Paths.get(POHJAT_KANSIO + tiedostonimi),StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Käytetään kun käyttäjä avaa tiedoston tiedoston valitsijalla ja haluaa avata sen tiedoston.
     *
     * @param path Käyttäjän valitseman tiedoston sijainti.
     * @return Onnistuiko avaaminen
     */
    public boolean avaaTiedosto(String path) {
        path = path.replace("\\", "/");
        return kopioiTiedostoKansioon(path);
    }

    /**
     * Lukee .lma tiedoston. LMath tallentaa tiedostoja jännällä tavalla siten, että ne ovat ikäänkuin
     * zip tiedostoja, joissa on monta eri osaa. Tiedosto, jossa latex koodi on, on kirjoitettu html kielellä.
     *
     * @param path LMath tiedoston sijainti
     * @param nimi LMath tiedoston nimi
     * @return LMath tiedoston sisältämä tieto
     * @throws IOException Jos tiedosto ei löydy
     */
    public KaavaTiedosto lueLMathTiedosto(String path, String nimi) throws IOException {
        ZipFile lmathTiedosto;
        try {
            lmathTiedosto = new ZipFile(path);
        } catch (ZipException e) {
            throw new IOException("LMath tiedosto on formatettu väärin!");
        }

        Enumeration<? extends ZipEntry> entries = lmathTiedosto.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if (entry.getName().startsWith("pages/")) { //Oikea tiedosto
                InputStream stream = lmathTiedosto.getInputStream(entry);
                KaavaTiedosto kaavaTiedosto =  lueLMathHTMLtiedosto(stream);
                lmathTiedosto.close();
                return kaavaTiedosto;
            }
        }
        lmathTiedosto.close();
        throw new IllegalArgumentException("Lmath tiedosto jotenkin viallinen");
    }

    /**
     * Lukee LMath html tiedoston ja palauttaa sen sisältämän tiedon
     *
     * @param inputStream sijainti
     * @return LMath tiedoston sisältämä tieto
     */
    private KaavaTiedosto lueLMathHTMLtiedosto(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream);
        StringBuilder koodi = new StringBuilder(); //tiedoston sisältö
        while (scanner.hasNextLine()) {
            koodi.append(scanner.nextLine());
        }
        //Rivit on jaettu diveillä ja koko teksti sisältö alkaa merkkijonolla content:
        String[] lines = koodi.toString().split("(<div>|\"content\":\")");
        koodi.setLength(0); //käytetään samaa StringBuilderia uudestaan
        String muuttujat = "";
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            //Poistetaan turhat \ merkit
            line = line.replaceAll("\\\\(?=[^\\w ])", "");
            //Viimeinen rivi saattaa sisältää rivin, jossa määritellään muuttujat
            if (i == lines.length - 1 && line.toLowerCase().contains("muuttujat")) {
                muuttujat = line.substring(line.indexOf(":") + 1, line.indexOf("<"));
                continue;
            }
            if (line.contains("alt=")) { //latex koodi on img tagin alt arvossa
                int index = line.indexOf("alt=");
                //Teksti on lainausmerkkien sisällä
                String text = line.substring(index + 5, line.indexOf('"', index + 5));
                koodi.append(text).append("\n");
            } else if (line.indexOf('<') != -1) {
                //Ei latex koodi vaan normaali kirjoitus
                String text = line.substring(0, line.indexOf('<'));
                koodi.append("<t>").append(text).append("\n");
            }
        }
        //Poistetaan tyhjä tila muuttujista
        muuttujat = muuttujat.replace(" ", "");
        if (koodi.length() > 0) koodi.setLength(koodi.length()-1);
        return new KaavaTiedosto(koodi.toString(), muuttujat);
    }

    /**
     * Palauttaa taulukon tiedostoista, jotka ovat pohjakansiossa tai
     * kuuluvat ohjelmaan oletuksella.
     *
     * @return Taulukko, joka sisältää tiedostojen nimet Esim. hello.txt tai jou.lma
     */
    public String[] tiedostotPohjatKansiossa() {
        File kansio = new File("pohjat");
        if (!kansio.exists()) {
            if (!kansio.mkdir()) {
                //luodaan kansio, jos sitä ei ole
                System.out.println("WARNING pohkakansiota ei voitu luoda");
            }
        }
        File[] files = kansio.listFiles();
        assert files != null;
        for (File f : files) { //Lisätään jokainen kansion sisällä oleva tiedosto listaan
            if (f.isDirectory()) continue;
            this.tiedostot.add(f.getName());
        }
        //Lisätään lisäksi ohjelman sisään rakennetut tiedostot, jos niitä ei olla ennen lisätty
        try {
            this.tiedostot.addAll(oletusTiedostot());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //Kopioidaan tulos taulukkoon, jotta kutsuja luokalla ei ole pääsyä oikeaan listaan
        String[] tuotos = new String[this.tiedostot.size()];
        for (int i = 0; i < this.tiedostot.size(); i++) {
            tuotos[i] = this.tiedostot.get(i);
        }
        return tuotos;
    }

    /**
     * Tallentaa normaaleja tekstitiedostoja. Ohjelma ei mahdollista ainakaan vielä
     * LMath tiedostojen muokkaamista.
     *
     * @param fileName  Tallennettavan tiedoston nimi
     * @param koodi     Tallennettava koodi
     * @param muuttujat Tallennettavat muuttujat
     * @throws IOException Jos kirjoittaminen ei onnistu
     */
    public void saveFile(String fileName, String koodi, String muuttujat) throws IOException {
        if (!fileName.endsWith(".txt")) {
            throw new IOException("Vain tekstitiedostoja voidaan tallentaa");
        }
        FileWriter writer = new FileWriter(POHJAT_KANSIO + fileName);
        muuttujat = muuttujat.replaceAll("\\s", ""); //välilyönnit ynnä muu tyhä tila pois
        String sisalto = "<start>\n" +
                koodi +
                "\n<end>\n" +
                muuttujat;
        writer.write(sisalto);
        writer.close();
    }

    /**
     * palauttaa nimeämättömän tiedoston, jolla on uniikki numero tunnus.
     * Tälläistä tiedostoa ei ole olemassa /pohjat kansiossa.
     * <p>Esim: unnamed (43).txt</p>
     *
     * @return tiedoston nimi: Esim. unnamed(43).txt
     */
    public String uniqueFile() {
        int number = 0;
        while (this.tiedostot.contains("unnamed (" + number + ")")) {
            number++;
        }
        return "unnamed (" + number + ").txt";
    }

    /**
     * Yrittää poistaa valitun tiedoston pohjat kansiosta
     *
     * @param valittu poistettavan tiedoston nimi
     * @return Onnistuiko poistaminen
     */
    public boolean deleteFile(String valittu) {
        File f = new File(POHJAT_KANSIO + valittu);
        return f.delete();
    }

    /**
     * Yrittää uudelleen nimetä halutun tiedoston pohja kansiossa.
     * Uuteen nimeen ei laiteta tiedostopäätettä vaan tiedostopääte on sama kuin vanhassa.
     *
     * @param oldName vanhan tiedoston nimi Esim. hei.txt
     * @param newName uusi nimi
     * @return onnistuiko nimeäminen
     */
    public boolean renameFile(String oldName, String newName) {
        File org = new File(POHJAT_KANSIO + oldName);
        String oldExtenssion = oldName.substring(oldName.lastIndexOf("."));
        return org.renameTo(new File(POHJAT_KANSIO + newName + oldExtenssion));
    }

    /**
     * Lukee sekä .txt että .lma tiedostoja. Käyttää lmath tiedostojen lukemiseen
     * {@link #lueLMathTiedosto(String, String)} funktiota. Tekstitiedostoissa täytyy olla tietty
     * syntaksi, jotta lukeminen onnistuu.
     *
     * @param nimi Luettavan tiedoston nimi. Tiedoston kuuluu löytyä pohjat kansiosta
     * @return Tiedoston sisältämä tieto
     * @throws IOException Tekstitiedosto on formatoitu väärin tai sitä ei ole olemassa
     */
    public KaavaTiedosto readFile(String nimi) throws IOException {
        if (nimi.endsWith(".lma")) {
            return lueLMathTiedosto(POHJAT_KANSIO + nimi, nimi);
        }
        Scanner scanner = new Scanner(Paths.get(POHJAT_KANSIO + nimi));
        if (!scanner.hasNextLine()) throw new IOException("Väärin formatoitu tiedosto");
        scanner.nextLine(); // <start>
        StringBuilder koodi = new StringBuilder();
        if (!scanner.hasNextLine()) throw new IOException("Väärin formatoitu tiedosto");
        String rivi = scanner.nextLine();
        while (!rivi.equals("<end>") && scanner.hasNextLine()) {
            koodi.append(rivi).append("\n");
            rivi = scanner.nextLine();
        }
        //Poistetaan viimeinen rivinvaihto
        if (koodi.length() > 0) koodi.setLength(koodi.length() - 1);
        String muuttujat = "";
        if (scanner.hasNextLine()) muuttujat = scanner.nextLine();
        return new KaavaTiedosto(koodi.toString(), muuttujat);
    }
}

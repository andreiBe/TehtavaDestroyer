package com.patonki.util;

import com.patonki.KaavaTiedosto;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Sisältää apumetodeja tiedostojen käsittelyyn.
 */
public class FileManager {
    private final ArrayList<String> files = new ArrayList<>();

    public String[] tiedostotPohjatKansiossa() {
        File kansio = new File("pohjat");
        if (!kansio.exists()) kansio.mkdir(); //luodaan kansio, jos sitä ei ole
        File[] files = kansio.listFiles();
        assert files != null;
        for (File f : files) { //Lisätään jokainen kansion sisällä oleva tiedosto listaan
            String eiPaatetta = f.getName().substring(0,f.getName().lastIndexOf('.'));
            this.files.add(eiPaatetta);
        }
        //Kopioidaan tulos taulukkoon, jotta kutsuja luokalla ei ole pääsyä oikeaan listaan
        String[] tuotos = new String[this.files.size()];
        for (int i = 0; i < this.files.size(); i++) {
            tuotos[i] = this.files.get(i);
        }
        return tuotos;
    }
    public void saveFile(String fileName, String koodi, String muuttujat) throws IOException {
        FileWriter writer = new FileWriter("pohjat/"+fileName+".txt");
        muuttujat = muuttujat.replaceAll("\\s","");
        String sisalto = "<start>\n" +
                koodi +
                "\n<end>\n" +
                muuttujat;
        writer.write(sisalto);
        writer.close();
    }
    public String uniqueFile() { //palauttaa nimeämättömän tiedoston, jolla on uniikki numero tunnus
        int number = 0;
        while (this.files.contains("unnamed ("+number+")")) {
            number++;
        }
        return "unnamed ("+number+")";
    }

    public void deleteFile(String valittu) {
        File f = new File("pohjat/"+valittu+".txt");
        f.delete();
    }
    public boolean renameFile(String oldName, String newName) {
        File org = new File("pohjat/"+oldName+".txt");
        return org.renameTo(new File("pohjat/"+newName+".txt"));
    }
    public KaavaTiedosto readFile(String nimi) throws IOException {
        Scanner scanner = new Scanner(Paths.get("pohjat/"+nimi+".txt"));
        scanner.nextLine(); // <start>
        StringBuilder koodi = new StringBuilder();
        String rivi = scanner.nextLine();
        while (!rivi.equals("<end>")) {
            koodi.append(rivi).append("\n");
            rivi = scanner.nextLine();
        }
        //Poistetaan viimeinen rivinvaihto
        if (koodi.length() > 0) koodi.setLength(koodi.length()-1);
        String muuttujat = "";
        if (scanner.hasNextLine()) muuttujat = scanner.nextLine();
        return new KaavaTiedosto(koodi.toString(),muuttujat,nimi);
    }
}

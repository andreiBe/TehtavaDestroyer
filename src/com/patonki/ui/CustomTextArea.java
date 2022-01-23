package com.patonki.ui;

import com.patonki.util.StringUtil;
import javafx.concurrent.Task;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Erityinen teksti alue, joka värittää funktiot ja muuttujat tekstistä.
 */
public class CustomTextArea extends CodeArea {
    private final ExecutorService executor; //ajaa syntaxin värityksen eri threadissa
    private final Controller controller; //käyttöliittymän toiminnallisuus luokka

    public CustomTextArea(Controller controller) {
        super();
        this.controller = controller;
        //Värittää metodit ja funktiot joka 500 millisekuntti muutosten jälkeen
        executor = Executors.newSingleThreadExecutor();
        Subscription subscription = multiPlainChanges()
                .successionEnds(Duration.ofMillis(500))
                .supplyTask(this::computeHighlightingAsync)
                .awaitLatest(multiPlainChanges())
                .filterMap(t -> {
                    if (t.isSuccess()) {
                        return Optional.of(t.get());
                    } else {
                        t.getFailure().printStackTrace();
                        return Optional.empty();
                    }
                })
                .subscribe(this::applyHighlighting);
    }
    public void updateHighlighting() {
        appendText("");
    }
    private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
        setStyleSpans(0, highlighting); //antaa teksti alueelle listan kohtia mitkä pitää värittää
    }
    //Kerää kaikki käyttäjän määrittelemät muuttujat listaan
    //muuttujan voi määritellä koodissa: #x = 98
    private List<String> collectMuuttujat(String text) {
        ArrayList<String> muuttujat = new ArrayList<>();
        new StringUtil().loydaKoodissaMaaritellytMuuttujat(text,muuttujat,new ArrayList<>());
        return  muuttujat;
    }
    private StyleSpans<Collection<String>> computeHighlighting(String text) {
        List<String> muuttujat = this.controller.getMuuttujatAsList();
        for (int i = 0; i < muuttujat.size(); i++) {
            muuttujat.set(i,muuttujat.get(i).trim());
        }
        muuttujat.addAll(collectMuuttujat(text));

        StringBuilder regex = new StringBuilder();
        //Kerää kaikki muuttujat regex arvoon, jotta ne kaikki voidaan tunnistaa
        for (String muuttuja : muuttujat) {
            regex.append("\\b").append(muuttuja).append("\\b|");
        }
        if (regex.length() > 0) {
            regex.setLength(regex.length()-1); //viimeinen | pois
        } else {
            /*
            Regex ei toimi, jos etsittävä on tyhjä merkkijono, joten laitetaan tilalle merkkijono
            , jota ei ikinä vahingossa kirjoiteta
            */
            regex.append("placeholderVariableNotSupposedToBeTyped");
        }
        //Muuttujat, funktiot ja tuplapisteet väritetään eri värillä
        Matcher matcher = Pattern.compile(
                "(?<VARIABLE>" + regex.toString() + ")"
                        + "|(?<FUNCTION>" + "[A-ZÄÖÅ]\\w*\\(|\\)|\\(" + ")"
                        + "|(?<DOUBLEDOT>" + ";" + ")"
        ).matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        //Etsitään matcheja ja lisätään niitä vastaava styleClass listaan
        //Huomaa esim: .variable css luokka löytyy css tiedostosta
        while (matcher.find()) {
            String styleClass = matcher.group("VARIABLE") != null ? "variable" :
                    matcher.group("FUNCTION") != null ? "function" :
                            matcher.group("DOUBLEDOT") != null ? "doubleDot" : null;
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
        String text = getText();
        Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
            @Override
            protected StyleSpans<Collection<String>> call() {
                return computeHighlighting(text);
            }
        };
        executor.execute(task);
        return task;
    }

    public void setText(String koodi) {
        clear();
        appendText(koodi);
    }
}

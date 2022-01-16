package com.patonki.ui;

import javafx.concurrent.Task;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomTextArea extends CodeArea {
    private final ExecutorService executor;
    private final Controller controller;

    public CustomTextArea(Controller controller) {
        super();
        this.controller = controller;
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
    private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
        setStyleSpans(0, highlighting);
    }
    private StyleSpans<Collection<String>> computeHighlighting(String text) {
        String[] muuttujat = this.controller.getMuuttujat();
        StringBuilder regex = new StringBuilder();
        for (String muuttuja : muuttujat) {
            regex.append("\\b").append(muuttuja).append("\\b|");
        }

        Matcher matcher = Pattern.compile(
                "(?<VARIABLE>"+regex.substring(0,regex.length()-1)+")"
                +"|(?<FUNCTION>"+"#\\w+\\[|\\]"+")"
                +"|(?<DOUBLEDOT>"+";"+")"
        ).matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =matcher.group("VARIABLE") != null ? "variable" :
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
            protected StyleSpans<Collection<String>> call() throws Exception {
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

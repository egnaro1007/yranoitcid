package com.yranoitcid.Backend.Api;

import java.io.IOException;
import java.io.InputStream;
import javazoom.jl.player.Player;
import javazoom.jl.decoder.JavaLayerException;

public class googleChanTTS extends api {

    String text = "Hello, World";

    public googleChanTTS() {
        super("https://translate.google.com/translate_tts");
        addPragma("ie", "UTF-8");
        addPragma("tl", "en");
        addPragma("client", "tw-ob");
        addPragma("q", text);
    }

    public void say(String inputTerm) {
        this.text = inputTerm;
        this.editPragma("q", this.text);
        this.connect();
        try {
            this.parse().play();
        } catch (IOException | JavaLayerException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Player parse() throws IOException, JavaLayerException {
        InputStream audio = connection.getInputStream();
        return new Player(audio);
    }

    public void setLanguage(String language) {
        this.editPragma("tl", language);
    }
}

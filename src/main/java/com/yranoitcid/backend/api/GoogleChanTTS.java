package com.yranoitcid.backend.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
public class GoogleChanTTS extends AbstractAPI {

    String text = "Hello, World";

    public GoogleChanTTS() {
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
            MediaPlayer mediaPlayer = new MediaPlayer(this.parse());
            mediaPlayer.setOnReady(mediaPlayer::play);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Media parse() throws IOException {
        InputStream audio = connection.getInputStream();
        String tempFileUri = Objects.requireNonNull(createTemporaryFileFromInputStream(audio)).toURI().toString();

        Media media = new Media(tempFileUri);
        return media;
    }

    private File createTemporaryFileFromInputStream(InputStream inputStream) {
        try {
            // Create a temporary file
            File tempFile = File.createTempFile("temp_audio", ".mp3");

            // Copy InputStream to the temporary file
            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setLanguage(String language) {
        this.editPragma("tl", language);
    }
}

package com.tuannm.scripts;

import com.tuannm.scripts.model.Translation;
import com.tuannm.scripts.model.LinkFileName;
import com.tuannm.scripts.model.Sentence;
import com.tuannm.scripts.model.SentenceWithAudioFilename;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CsvParseAndMerge {


    public List<Translation> createRawData() throws IOException {

        List<Sentence> sentences = this.readSentencesFromCSV();
        List<LinkFileName> linkFileNames = this.readLinksFileNameFromCSV(sentences);
        List<SentenceWithAudioFilename> sentencesWithAudioFilenames = this.readSentencesWithAudioFilenameFromCSV(sentences);

        var englishSentences = sentences.stream().filter(sentence -> Objects.equals(sentence.getLang(), "eng")).toList();
        var vietnameseSentences = sentences.stream().filter(sentence -> Objects.equals(sentence.getLang(), "vie")).toList();

        List<Translation> rawData = new ArrayList<>();
        for (Sentence englishSentence : englishSentences) {
            List<Long> collectTranslationId = linkFileNames
                    .stream()
                    .filter(linkFileName -> Objects.equals(englishSentence.getSentenceId(), linkFileName.getSentenceId()))
                    .map(LinkFileName::getTranslationId)
                    .toList();

            boolean sentenceAvailable = sentencesWithAudioFilenames
                    .stream()
                    .anyMatch(sentencesWithAudio ->
                            Objects.equals(englishSentence.getSentenceId(), sentencesWithAudio.getSentenceId())
                                    && (!Objects.equals(sentencesWithAudio.getLicense(), "\\N"))
                    );

            for (Sentence vietnameseSentence : vietnameseSentences) {
                if (collectTranslationId.contains(vietnameseSentence.getSentenceId())) {
                    for (var translationId : collectTranslationId) {
                        if (Objects.equals(translationId, vietnameseSentence.getSentenceId())) {
                            Translation entityTranslation = getTranslation(englishSentence, vietnameseSentence, sentenceAvailable);
                            rawData.add(entityTranslation);
                        }
                    }
                    break;
                }
            }

        }
        return rawData;


    }

    private static Translation getTranslation(Sentence englishSentence, Sentence vietnameseSentence, boolean sentenceAvailable) {
        Translation entityTranslation = new Translation();
        entityTranslation.setId(englishSentence.getSentenceId());
        entityTranslation.setText(englishSentence.getText());
        if (sentenceAvailable) {
            entityTranslation.setAudioUrl("https://audio.tatoeba.org/" + englishSentence.getLang() + "/" + englishSentence.getSentenceId() + ".mp3");
        } else {
            entityTranslation.setAudioUrl(null);
        }
        entityTranslation.setTranslateId(vietnameseSentence.getSentenceId());
        entityTranslation.setTranslateText(vietnameseSentence.getText());
        return entityTranslation;
    }


    private List<Sentence> readSentencesFromCSV() throws IOException {
        List<Sentence> sentences = new ArrayList<>();
        ClassPathResource classPathResource = new ClassPathResource("sentences1.csv");
        InputStream inputStream = classPathResource.getInputStream();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            sentences = br.lines().filter(line -> {
                String[] values = line.split("\\t");
                return Objects.equals(values[1], "vie") || Objects.equals(values[1], "eng");
            }).map(line -> {
                String[] values = line.split("\\t");
                Sentence sentence = new Sentence();
                sentence.setSentenceId(Long.parseLong(values[0]));
                sentence.setLang(values[1]);
                sentence.setText(values[2]);
                return sentence;
            }).toList();

        } catch (Exception e) {
            e.printStackTrace();
            // Handle exception
        }
        return sentences;
    }

    private List<LinkFileName> readLinksFileNameFromCSV(List<Sentence> sentences) throws IOException {
        List<LinkFileName> linkFileNames = new ArrayList<>();
        ClassPathResource classPathResource = new ClassPathResource("links1.csv");
        InputStream inputStream = classPathResource.getInputStream();

        List<Long> collectSentenceId = sentences.stream().map(Sentence::getSentenceId).toList();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));) {
            linkFileNames = br.lines()
                    .filter(line -> {
                        String[] values = line.split("\\t");
                        return collectSentenceId.contains(Long.parseLong(values[0]));
                    })
                    .map(line -> {
                        String[] values = line.split("\\t");
                        LinkFileName linkFileName = new LinkFileName();
                        linkFileName.setSentenceId(Long.parseLong(values[0]));
                        linkFileName.setTranslationId(Long.parseLong(values[1]));
                        return linkFileName;
                    }).toList();

        } catch (Exception e) {
            e.printStackTrace();
            // Handle exception
        }
        return linkFileNames;
    }

    private List<SentenceWithAudioFilename> readSentencesWithAudioFilenameFromCSV(List<Sentence> sentences) throws IOException {
        List<SentenceWithAudioFilename> sentencesWithAudioFilenames = new ArrayList<>();
        ClassPathResource classPathResource = new ClassPathResource("sentences_with_audio1.csv");
        InputStream inputStream = classPathResource.getInputStream();
        List<Long> collectSentenceId = sentences.stream().map(Sentence::getSentenceId).toList();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));) {

            sentencesWithAudioFilenames = br.lines().filter(line -> {
                String[] values = line.split("\\t");
                return collectSentenceId.contains(Long.parseLong(values[0]));
            }).map(line -> {
                String[] values = line.split("\\t");
                SentenceWithAudioFilename sentencesWithAudioFilename = new SentenceWithAudioFilename();
                sentencesWithAudioFilename.setSentenceId(Long.parseLong(values[0]));
                sentencesWithAudioFilename.setUserName(values[1]);
                sentencesWithAudioFilename.setLicense(values[2]);
                sentencesWithAudioFilename.setAttributionUrl(values[3]);
                return sentencesWithAudioFilename;
            }).toList();
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exception
        }
        return sentencesWithAudioFilenames;
    }
}

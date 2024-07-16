package com.tuannm.scripts.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SentenceWithAudioFilename {
    private long sentenceId;
    private String userName;
    private String license;
    private String attributionUrl;
}

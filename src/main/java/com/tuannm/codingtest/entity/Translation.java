package com.tuannm.codingtest.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Translation {
    @Id
    private Long id;
    private String text;
    private String audioUrl;
    private Long translateId;
    private String translateText;
}

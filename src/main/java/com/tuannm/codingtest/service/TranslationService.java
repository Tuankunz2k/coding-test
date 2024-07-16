package com.tuannm.codingtest.service;

import com.tuannm.codingtest.entity.Translation;
import org.springframework.data.domain.Page;
public interface TranslationService {
    Page<Translation> getTranslationPage(Integer pageNum, Integer pageSize);
}

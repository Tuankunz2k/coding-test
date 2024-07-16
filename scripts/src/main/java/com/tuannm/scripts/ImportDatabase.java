package com.tuannm.scripts;

import com.tuannm.scripts.model.Translation;
import com.tuannm.scripts.repository.Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ImportDatabase {
    private final CsvParseAndMerge csvParseAndMerge;
    private final Repository repository;

    @Transactional
    public void importData() {
        try {
            List<Translation> rawData = csvParseAndMerge.createRawData();
            if (!rawData.isEmpty()){
                repository.saveAll(rawData);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exception
        }
    }
}

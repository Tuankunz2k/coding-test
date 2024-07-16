package com.tuannm.scripts.repository;

import com.tuannm.scripts.model.Translation;
import org.springframework.data.jpa.repository.JpaRepository;

@org.springframework.stereotype.Repository
public interface Repository  extends JpaRepository<Translation, Long> {
}

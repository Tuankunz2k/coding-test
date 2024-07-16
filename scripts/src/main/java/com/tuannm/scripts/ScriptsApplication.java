package com.tuannm.scripts;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.IOException;
import java.util.TimeZone;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.tuannm.scripts.repository")
public class ScriptsApplication {
    @Autowired
    private final ImportDatabase importDatabase;

    public ScriptsApplication(ImportDatabase importDatabase) {
        this.importDatabase = importDatabase;
    }

    public static void main(String[] args) {
        SpringApplication.run(ScriptsApplication.class, args);
    }

    @PostConstruct
    public void init() throws IOException {

        importDatabase.importData();
    }
}

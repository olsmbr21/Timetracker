package fhtw.timetracker.util;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Stage 04.1: Repository-Grundgerüst.
 * Ab Stage 04.2 kommt das Schreiben, ab 04.3 das Lesen.
 */
public class CsvBookingRepository {

    private final Path file;

    public CsvBookingRepository(String fileName) {
        this.file = Paths.get(fileName);
    }

    public Path getFilePath() {
        return file;
    }
}
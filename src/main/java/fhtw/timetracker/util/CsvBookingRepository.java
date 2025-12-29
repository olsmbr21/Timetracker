package fhtw.timetracker.util;

import fhtw.timetracker.model.Booking;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Stage 04.1: Repository-Grundgerüst.
 * Ab Stage 04.2 kommt das Schreiben, ab 04.3 das Lesen.
 */
public class CsvBookingRepository {

    private final Path file;

    public CsvBookingRepository(String fileName) {
        this.file = Paths.get(fileName);
    }

    public synchronized void saveBooking(Booking booking) throws IOException {
        if (booking == null) return;

        if (file.getParent() != null) Files.createDirectories(file.getParent());

        try (BufferedWriter writer = Files.newBufferedWriter(
                file, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {

            writer.write(booking.toCsvLine());
            writer.newLine();
        }
    }
    public synchronized List<Booking> loadAll() throws IOException {
        List<Booking> list = new ArrayList<>();
        if (!Files.exists(file)) return list;

        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                Booking b = Booking.fromCsvLine(line);
                if (b != null) list.add(b);
            }
        }
        return list;
    }
}
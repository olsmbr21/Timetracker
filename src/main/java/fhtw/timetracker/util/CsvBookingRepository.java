package fhtw.timetracker.util;

import fhtw.timetracker.model.Booking;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV-Persistenz für Buchungen (Server-seitig).
 * synchronized verhindert Dateikonflikte bei parallelen Clients.
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

    private synchronized void overwriteAll(List<Booking> bookings) throws IOException {
        if (file.getParent() != null) Files.createDirectories(file.getParent());

        try (BufferedWriter writer = Files.newBufferedWriter(
                file, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

            for (Booking booking : bookings) {
                writer.write(booking.toCsvLine());
                writer.newLine();
            }
        }
    }

    public synchronized boolean cancelBooking(long bookingId, String userName) throws IOException {
        List<Booking> all = loadAll();
        boolean changed = false;

        for (Booking b : all) {
            if (b.getId() == bookingId && userName != null && userName.equals(b.getUserName())) {
                if (!Booking.STATUS_CANCELED.equals(b.getStatus())) {
                    b.setStatus(Booking.STATUS_CANCELED);
                    changed = true;
                }
            }
        }

        if (changed) overwriteAll(all);
        return changed;
    }
}
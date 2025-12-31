package fhtw.timetracker.client;

import fhtw.timetracker.model.Booking;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * TCP-Client-Service (Request/Response).
 */
public class TimeTrackerClientService {

    private final String host;
    private final int port;

    public TimeTrackerClientService(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void sendBooking(Booking booking) throws IOException {
        try (Socket socket = new Socket(host, port);
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {

            out.write("CREATE_BOOKING;" + booking.toCsvLine());
            out.newLine();
            out.flush();

            expectOk(in.readLine());
        }
    }

    public List<Booking> loadBookingsForUser(String userName) throws IOException {
        try (Socket socket = new Socket(host, port);
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {

            out.write("GET_BOOKINGS;" + userName);
            out.newLine();
            out.flush();

            return readBookings(in);
        }
    }

    // Stage 13: neu
    public void cancelBooking(long bookingId, String userName) throws IOException {
        try (Socket socket = new Socket(host, port);
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {

            out.write("CANCEL_BOOKING;" + bookingId + ";" + userName);
            out.newLine();
            out.flush();

            expectOk(in.readLine());
        }
    }

    private void expectOk(String firstLine) throws IOException {
        if ("OK".equals(firstLine)) return;
        throw new IOException(firstLine == null ? "No response" : firstLine);
    }

    private List<Booking> readBookings(BufferedReader in) throws IOException {
        expectOk(in.readLine());

        List<Booking> result = new ArrayList<>();
        String line;
        while ((line = in.readLine()) != null) {
            if ("END".equals(line)) break;
            Booking b = Booking.fromCsvLine(line);
            if (b != null) result.add(b);
        }
        return result;
    }
}


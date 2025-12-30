package fhtw.timetracker.client;

import fhtw.timetracker.model.Booking;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Stage 06.2: Client kann Buchung senden und Buchungen eines Users laden.
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

            String first = in.readLine();
            if (!"OK".equals(first)) throw new IOException(first == null ? "No response" : first);
        }
    }

    public List<Booking> loadBookingsForUser(String userName) throws IOException {
        try (Socket socket = new Socket(host, port);
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {

            out.write("GET_BOOKINGS;" + userName);
            out.newLine();
            out.flush();

            String first = in.readLine();
            if (!"OK".equals(first)) throw new IOException(first == null ? "No response" : first);

            List<Booking> list = new ArrayList<>();
            String line;
            while ((line = in.readLine()) != null) {
                if ("END".equals(line)) break;
                Booking b = Booking.fromCsvLine(line);
                if (b != null) list.add(b);
            }
            return list;
        }
    }
}


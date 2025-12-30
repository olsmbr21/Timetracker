package fhtw.timetracker.server;

import fhtw.timetracker.util.CsvBookingRepository;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Stage 05.2: Grundlegendes Command-Parsing.
 */
public class ClientHandler implements Runnable {

    private final Socket socket;
    private final CsvBookingRepository repository;

    public ClientHandler(Socket socket, CsvBookingRepository repository) {
        this.socket = socket;
        this.repository = repository;
    }

    @Override
    public void run() {
        try (Socket s = socket;
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream(), StandardCharsets.UTF_8));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = in.readLine()) != null) {
                if (line.isBlank()) continue;

                out.write(handleCommand(line));
                out.flush();

                if (line.startsWith("QUIT")) break;
            }

        } catch (IOException e) {
            System.err.println("ClientHandler error: " + e.getMessage());
        }
    }

    private String handleCommand(String line) {
        String[] parts = line.split(";", 2);
        String cmd = parts[0];

        if ("QUIT".equals(cmd)) return "OK\n";
        return "ERROR;Unknown command\n";
    }
}


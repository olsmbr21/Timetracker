package fhtw.timetracker.server;

import fhtw.timetracker.util.CsvBookingRepository;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Stage 05.1: Server nimmt Clients an und startet Threads.
 */
public class TimeTrackerServer {

    public static final int PORT = 5000;

    public static void main(String[] args) {
        CsvBookingRepository repository = new CsvBookingRepository("bookings.csv");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket client = serverSocket.accept();
                new Thread(new ClientHandler(client, repository), "server-client-handler").start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }
}

package api;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;

public class HttpServer {
    private static final int PORT = 8000;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            Socket socket = serverSocket.accept();

            new Thread(() -> {
                handleRequest(socket);
            }).start();
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
        }
    }

    private static void handleRequest(Socket socket) {
        try (InputStream input = socket.getInputStream();
             OutputStream output = socket.getOutputStream()) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line;
            StringBuilder requestBuilder = new StringBuilder();
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                requestBuilder.append(line).append("\n");
            }

            String request = requestBuilder.toString();
            String[] requestLines = request.split("\n");

            String response = "";

            final String startPattern = "=";
            final String endPattern = "HTTP/1.1";

            final int startIndex = requestLines[0].indexOf(startPattern);
            final int endIndex = requestLines[0].indexOf(endPattern);

            final String query = requestLines[0].substring(startIndex + 1, endIndex).trim();
            final String decodedQuery = URLDecoder.decode(query, "UTF-8");

            output.write(response.getBytes());
            output.flush();

        } catch (IOException ex) {
            System.out.println("Request handling exception: " + ex.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
                System.out.println("Socket closing exception: " + ex.getMessage());
            }
        }
    }
}

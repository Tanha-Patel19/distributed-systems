import java.io.*;
import java.net.*;

public class SimpleHttpClient {
    public static void main(String[] args) throws Exception {
        String host = "localhost";
        int port = 9090;
        String path = "/index.html";
        if (args.length > 0) {
            host = args[0];
        }
        if (args.length > 1) {
            port = Integer.parseInt(args[1]);
        }
        if (args.length > 2) {
            path = args[2];
        }

        Socket socket = new Socket(host, port);
        PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));

        // the HTTP request: request line, headers, empty line
        writer.print("GET " + path + " HTTP/1.1\r\n");
        writer.print("Host: " + host + "\r\n");
        writer.print("Connection: close\r\n");
        writer.print("\r\n");
        writer.flush();

        String responseLine;
        while ((responseLine = reader.readLine()) != null) {
            System.out.println(responseLine);
        }

        writer.close();
        reader.close();
        socket.close();
    }
}
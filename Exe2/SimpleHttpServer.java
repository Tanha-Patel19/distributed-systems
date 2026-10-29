import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.StringTokenizer;

public class SimpleHttpServer {
    public static void main(String[] args) throws Exception {
        int port = 9090;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("httpServer running on port " + serverSocket.getLocalPort());

            while (true) {
                Socket socket = serverSocket.accept();      // wait for a client
                System.out.println("New connection accepted "
                        + socket.getInetAddress() + ":" + socket.getPort());

                HttpRequestHandler request = new HttpRequestHandler(socket);
                // Create a new thread to process the request.
                Thread thread = new Thread(request);
                // Start the thread.
                thread.start();
            }
        }
    }
}

class HttpRequestHandler implements Runnable {
    private static final String CRLF = "\r\n";
    private static final Path ROOT = Paths.get("www").toAbsolutePath().normalize();
    private final Socket socket;

    public HttpRequestHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            socket.setSoTimeout(10000);   // give up on clients that send nothing
            processRequest();
        } catch (Exception e) {
            System.out.println("Error: " + e);
        } finally {
            try {
                socket.close();
            } catch (Exception e) {
                System.out.println("Error while closing: " + e);
            }
        }
    }

    private void processRequest() throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        OutputStream output = socket.getOutputStream();

        // 1. request line, e.g. "GET /index.html HTTP/1.1"
        String requestLine = br.readLine();
        if (requestLine == null) {
            return;                         // client sent nothing
        }
        System.out.println(Thread.currentThread().getName() + ": " + requestLine);

        // 2. header lines, until the empty line
        String headerLine = br.readLine();
        while (headerLine != null && !headerLine.isEmpty()) {
            headerLine = br.readLine();
        }

        // 3. split the request line into its three parts
        StringTokenizer s = new StringTokenizer(requestLine);
        if (s.countTokens() != 3) {
            sendError(output, "400 Bad Request");
            return;
        }
        String method = s.nextToken();      // GET
        String fileName = s.nextToken();    // /index.html
        // third token (HTTP/1.1) is not needed

        if (!method.equals("GET")) {
            sendError(output, "405 Method Not Allowed");
            return;
        }
        if (fileName.equals("/")) {
            fileName = "/index.html";
        }

        // 4. find the file inside the www folder
        Path file = ROOT.resolve(fileName.substring(1)).normalize();
        if (!file.startsWith(ROOT)) {       // do not leave the www folder
            sendError(output, "403 Forbidden");
            return;
        }

        // 5. send the file or a 404
        if (Files.isRegularFile(file)) {
            byte[] data = Files.readAllBytes(file);
            sendHeader(output, "200 OK", contentType(fileName), data.length);
            output.write(data);
        } else {
            sendError(output, "404 Not Found");
        }
        output.flush();
    }

    private void sendHeader(OutputStream output, String status, String type, long length)
            throws Exception {
        output.write(("HTTP/1.1 " + status + CRLF).getBytes());
        output.write(("Content-Type: " + type + CRLF).getBytes());
        output.write(("Content-Length: " + length + CRLF).getBytes());
        output.write(("Connection: close" + CRLF).getBytes());
        output.write(CRLF.getBytes());      // empty line = end of the header
    }

    private void sendError(OutputStream output, String status) throws Exception {
        String body = "<html><body><h1>" + status + "</h1></body></html>";
        sendHeader(output, status, "text/html", body.length());
        output.write(body.getBytes());
        output.flush();
    }

    private String contentType(String fileName) {
        if (fileName.endsWith(".html") || fileName.endsWith(".htm")) {
            return "text/html";
        }
        if (fileName.endsWith(".txt")) {
            return "text/plain";
        }
        return "application/octet-stream";
    }
}


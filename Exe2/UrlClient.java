import java.io.*;
import java.net.*;

public class UrlClient {
    public static void main(String[] args) throws Exception {
        String address = "http://localhost:9090/index.html";
        if (args.length > 0) {
            address = args[0];
        }
        URL url = URI.create(address).toURL();

        URLConnection connection;
        if (args.length > 2) {                       // 1c: explicit proxy
            Proxy proxy = new Proxy(Proxy.Type.HTTP,
                    new InetSocketAddress(args[1], Integer.parseInt(args[2])));
            connection = url.openConnection(proxy);
        } else {
            connection = url.openConnection();       // direct (or system proxy settings)
        }
        connection.setConnectTimeout(5000);          // do not wait forever
        connection.setReadTimeout(5000);

        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error: " + e);       // e.g. 404 or server not reachable
        }
    }
}
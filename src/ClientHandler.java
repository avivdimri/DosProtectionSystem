import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Queue;

public class ClientHandler implements HttpHandler {

    // handle function for each client request with protect logic from DOS
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        long timeRequest    = System.currentTimeMillis();
        int status          = 503; //default
        String message      = "Service Unavailable";
        int clientId;
        if (!"GET".equals(exchange.getRequestMethod())) {
            // handle only GET request
            return;
        }
        clientId = getClientId(exchange);
        // thread safeness
        synchronized (this) {
            Queue<Long> queue = DosProtectionServer.map.get(clientId); //clientId's queue
            if (queue == null) {
                // create first time queue
                queue = new LinkedList<>();
            }
            //Check if this specific client reached the max number of requests per time frame threshold
            if (queue.size() < 5) {
                queue.add(timeRequest);
                DosProtectionServer.map.put(clientId, queue);
                status  = 200;
                message = "200 OK";

            }
            //check if the top element isn't relevant any more (pass 5 seconds)
            else if (timeRequest - queue.peek() > 5000) {
                queue.remove();             // remove the irrelevant time request
                queue.add(timeRequest);     // add the current relevant time request
                status  = 200;
                message = "200 OK";
            }

            // otherwise the specific client reached the max number of requests per time frame threshold
        }

        OutputStream outputStream = exchange.getResponseBody();
        exchange.sendResponseHeaders(status, message.length());
        outputStream.write(message.getBytes());
        outputStream.flush();
        outputStream.close();
    }

    // extract the clientId parameter
    private int getClientId(HttpExchange httpExchange) {

        return Integer.parseInt(httpExchange.
                getRequestURI()
                .toString()
                .split("\\?")[1]
                .split("=")[1]);

    }
}

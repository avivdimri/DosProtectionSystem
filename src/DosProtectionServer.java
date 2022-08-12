import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DosProtectionServer {
    private ExecutorService executor;
    private HttpServer      server = null;
    public static HashMap<Integer, Queue<Long>> map = null; // key = clientId, value = queue of requests
    public static final int MaxTimeWait = 60;

    public DosProtectionServer() {
        try {
            server = HttpServer.create(new InetSocketAddress("localhost", 3000), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // The server  utilize all available cores
        executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        map = new HashMap<>();
    }

    // the function starts the server to listen for incoming HTTP requests
    public void start() {
        server.createContext("/", new ClientHandler()); // Handle each request in a separate thread
        server.setExecutor(executor);
        server.start();
        System.out.println("Server is listening on port 3000");
    }

    // the function stop the http server and  gracefully drain all the threads.
    public void finish() {
        server.stop(1);
        executor.shutdown();
        try {
            if (!executor.awaitTermination(MaxTimeWait, TimeUnit.SECONDS)) {
                System.err.println("Threads didn't finish in 60 seconds!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        DosProtectionServer mySystem = new DosProtectionServer();
        mySystem.start();
        System.in.read(); // only if user press key in console the main thread continue to finish function
        mySystem.finish();
        System.out.println("server down");

    }

}

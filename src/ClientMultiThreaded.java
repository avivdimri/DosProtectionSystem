import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ClientMultiThreaded {
    public static final int MaxTimeWait = 60;
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter number of clients");
        int nThreads = Integer.parseInt(scanner.nextLine());    // Read user input
        if (nThreads < 0) {
            System.out.println("Error, can't create negative numbers of clients");
            return;
        }
        // assume the user enter a reasonable number
        ClientWorker[] clients = new ClientWorker[nThreads];
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);
        Random r = new Random();

        for (int i = 0; i < nThreads; i++) {
            clients[i] = new ClientWorker(r.nextInt(nThreads));
            executor.execute(clients[i]);
        }

        System.in.read();    // only if user press key (+ enter of curse) in console the main thread continue to close all the threads
        executor.shutdown(); // no more new clients
        for (ClientWorker client : clients) {
            client.stop(); // gracefully drain all the threads.
        }
        try {
            // wait for all threads to be done
            if (!executor.awaitTermination(MaxTimeWait, TimeUnit.SECONDS)) {
                System.err.println("Threads didn't finish in 60 seconds!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("All clients are finished");
    }
}
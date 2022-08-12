//package com.journaldev.threadpool;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class ClientWorker implements Runnable {
    private int id;
    public final int MaxSleepTime = 2000;
    public volatile boolean stop  = false;

    public ClientWorker(int id) {
        this.id = id;
    }

    // the function send  request to server and wait some random time and then send another request until be stopped
    @Override
    public void run() {
        while (!stop) {
            try {
                sendHttpRequest(id);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            Random r = new Random();
            int randInt = r.nextInt(MaxSleepTime);
            try {
                Thread.sleep(randInt);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    // stop the client thread to send unlimited requests
    public void stop() {
        stop = true;
    }

    // the function Send HTTP request to a server with simulated HTTP client identifier as a query parameter
    // and print the response status and message
    private void sendHttpRequest(int id) throws Exception {
        String str = "http://localhost:3000/?clientId=" + id;
        URL url = new URL(str);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        int status = con.getResponseCode();
        String message = con.getResponseMessage();
        System.out.println(status + " " + message);
    }

}
package com.sgh.selvaganapathyhydraulics.helper;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class SelfPinger {

	private static final String PING_URL = "https://selvaganapathyhydraulics-1.onrender.com/ping";


    @Scheduled(fixedRate = 12 * 60 * 1000) // every 12 minutes
    public void pingSelf() {
        try {
            int responseCode = sendPing(PING_URL);

            if (responseCode == 200) {
                System.out.println("[✅ PING SUCCESS] Server is live at: " + PING_URL);
            } else {
                System.err.println("[⚠️ PING WARNING] Received status: " + responseCode);
            }

        } catch (IOException e) {
            System.err.println("[❌ PING ERROR] Could not reach server: " + e.getMessage());
        }
    }

    private int sendPing(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = null;
        int attempts = 0;
        int responseCode = -1;

        while (attempts < 3) {
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000); // 5 sec
                conn.setReadTimeout(5000);
                responseCode = conn.getResponseCode();

                if (responseCode == 200) break;

            } catch (IOException ex) {
                System.err.println("[Retry #" + (attempts + 1) + "] Failed to ping: " + ex.getMessage());
            } finally {
                if (conn != null) conn.disconnect();
                attempts++;
            }
        }

        return responseCode;
    }
}

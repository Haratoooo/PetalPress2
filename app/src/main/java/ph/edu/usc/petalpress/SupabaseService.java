package ph.edu.usc.petalpress;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SupabaseService {
    private static final String SUPABASE_URL = "https://etfmwhmqmnnsatkirrkx.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImV0Zm13aG1xbW5uc2F0a2lycmt4Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTE5NTAwODgsImV4cCI6MjA2NzUyNjA4OH0.TihBvneGpMnny29L9GU7i5Mn3I12jyc3HLv0I5qxPZQ";

    public static String signInAndGetToken(Context context, String email, String password) {
        try {
            URL url = new URL(SUPABASE_URL + "/auth/v1/token?grant_type=password");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("apikey", SUPABASE_API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String body = "{ \"email\": \"" + email + "\", \"password\": \"" + password + "\" }";

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes("utf-8"));
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();

            return sb.toString(); // JSON with access_token, etc.

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int signUp(String email, String password) {
        try {
            URL url = new URL(SUPABASE_URL + "/auth/v1/signup");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("apikey", SUPABASE_API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonBody = "{ \"email\": \"" + email + "\", \"password\": \"" + password + "\" }";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            return conn.getResponseCode();  // 200 or 201 = success

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static String fetchJournals(String userToken) {
        try {
            URL url = new URL(SUPABASE_URL + "/rest/v1/journals?select=*");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("apikey", SUPABASE_API_KEY);
            conn.setRequestProperty("Authorization", "Bearer " + userToken);
            conn.setRequestProperty("Accept", "application/json");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String fetchEntries(String userToken, String journalId) {
        try {
            URL url = new URL(SUPABASE_URL + "/rest/v1/entries?journal_id=eq." + journalId + "&select=*");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("apikey", SUPABASE_API_KEY);
            conn.setRequestProperty("Authorization", "Bearer " + userToken);
            conn.setRequestProperty("Accept", "application/json");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

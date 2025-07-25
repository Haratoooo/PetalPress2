package ph.edu.usc.petalpress;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

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

    public static int createJournal(String userToken, String title, String description, String coverImageUrl) {
        try {
            // Set the default image if no image is provided
            if (coverImageUrl == null || coverImageUrl.isEmpty()) {
                coverImageUrl = "journal_cover_test";  // Default image for journal cover
            }

            URL url = new URL(SUPABASE_URL + "/rest/v1/journals");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("apikey", SUPABASE_API_KEY);
            conn.setRequestProperty("Authorization", "Bearer " + userToken);
            conn.setRequestProperty("Content-Type", "application/json");

            // Create the JSON body for the new journal entry
            String jsonBody = String.format("{\"title\": \"%s\", \"description\": \"%s\", \"cover_image_url\": \"%s\"}",
                    title, description, coverImageUrl);

            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response code from the server
            int responseCode = conn.getResponseCode();
            return responseCode;  // 200 or 201 = success

        } catch (Exception e) {
            e.printStackTrace();
            return -1;  // Return error code if something goes wrong
        }
    }


    // Method for editing a journal (update functionality)
    public static int updateJournal(String userToken, String journalId, String title, String description, String coverImageUrl) {
        try {
            URL url = new URL(SUPABASE_URL + "/rest/v1/journals?id=eq." + journalId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("PATCH");
            conn.setRequestProperty("apikey", SUPABASE_API_KEY);
            conn.setRequestProperty("Authorization", "Bearer " + userToken);
            conn.setRequestProperty("Content-Type", "application/json");

            // Create the JSON body for the updated journal entry
            String jsonBody = String.format("{\"title\": \"%s\", \"description\": \"%s\", \"cover_image_url\": \"%s\"}",
                    title, description, coverImageUrl);

            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            return responseCode;  // 200 or 204 = success

        } catch (Exception e) {
            e.printStackTrace();
            return -1;  // Return error code if something goes wrong
        }
    }

    // Method for deleting a journal
    public static int deleteJournal(String userToken, String journalId) {
        try {
            URL url = new URL(SUPABASE_URL + "/rest/v1/journals?id=eq." + journalId);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("DELETE");
            conn.setRequestProperty("apikey", SUPABASE_API_KEY);
            conn.setRequestProperty("Authorization", "Bearer " + userToken);
            conn.setRequestProperty("Content-Type", "application/json");

            int responseCode = conn.getResponseCode();
            return responseCode;  // 200 or 204 = success

        } catch (Exception e) {
            e.printStackTrace();
            return -1;  // Return error code if something goes wrong
        }
    }

    // Sign-Out AsyncTask
    public static class SignOutTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... userToken) {
            try {
                // Perform sign-out API request
                URL url = new URL(SUPABASE_URL + "/auth/v1/logout");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setRequestProperty("apikey", SUPABASE_API_KEY);
                conn.setRequestProperty("Authorization", "Bearer " + userToken[0]);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                int responseCode = conn.getResponseCode();
                return responseCode; // Return the response code
            } catch (Exception e) {
                e.printStackTrace();
                return -1;  // Return error code if something goes wrong
            }
        }

        @Override
        protected void onPostExecute(Integer responseCode) {
            super.onPostExecute(responseCode);
            // Here, we will handle the response code and update the UI accordingly
            if (responseCode == 200 || responseCode == 204) {
                // Success: Notify user and handle UI updates
                Log.d("SupabaseService", "Signed out successfully");
                // Optionally, notify the user in UI (via Toast or similar)
            } else {
                // Error: Notify user
                Log.e("SupabaseService", "Error signing out: " + responseCode);
            }
        }
    }

    // Call this method to start the async sign-out task
    public static void signOutAsync(String userToken) {
        new SignOutTask().execute(userToken);  // Pass the user token for sign-out
    }
}



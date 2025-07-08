package ph.edu.usc.petalpress;



import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SupabaseService {
    private static final String SUPABASE_URL = "https://etfmwhmqmnnsatkirrkx.supabase.co";
    private static final String SUPABASE_API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImV0Zm13aG1xbW5uc2F0a2lycmt4Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTE5NTAwODgsImV4cCI6MjA2NzUyNjA4OH0.TihBvneGpMnny29L9GU7i5Mn3I12jyc3HLv0I5qxPZQ";

    public static int signIn(String email, String password) {
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

            return conn.getResponseCode(); // 200 = success

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
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
}

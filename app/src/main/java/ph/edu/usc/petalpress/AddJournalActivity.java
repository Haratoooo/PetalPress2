package ph.edu.usc.petalpress;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class AddJournalActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView imagePreview;
    private EditText editTitle, editDescription;
    private Button btnCreate, btnCancel;

    private String userToken;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journal);

        imagePreview = findViewById(R.id.imagePreview);
        editTitle = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);
        btnCreate = findViewById(R.id.btnCreate);
        btnCancel = findViewById(R.id.btnCancel);

        userToken = getIntent().getStringExtra("user_token");
        userId = getIntent().getStringExtra("user_id");

        imagePreview.setOnClickListener(v -> openImagePicker());
        btnCancel.setOnClickListener(v -> finish());

        btnCreate.setOnClickListener(v -> {
            String title = editTitle.getText().toString().trim();
            String description = editDescription.getText().toString().trim();

            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description)) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String id = UUID.randomUUID().toString();
            String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            String imagePath = null;

            if (imageUri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                    imagePath = saveImageToInternalStorage(bitmap, id + ".jpg");
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
                }
            }

            JournalRepository repo = new JournalRepository(this);
            repo.insertJournal(id, title, description, createdAt, imagePath);
            Intent intent = new Intent(AddJournalActivity.this, EntriesList.class);
            intent.putExtra("journal_id", id);
            intent.putExtra("journal_title", title);
            intent.putExtra("journal_description", description);
            intent.putExtra("journal_image_path", imagePath);
            startActivity(intent);
            finish(); // Optional: close AddJournalActivity


            Toast.makeText(this, "Journal saved locally 🎉", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

        private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Cover Photo"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imagePreview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageThenSaveJournal(Uri uri) {
        new Thread(() -> {
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                String fileName = UUID.randomUUID().toString() + ".jpg";
                URL url = new URL("https://etfmwhmqmnnsatkirrkx.supabase.co/storage/v1/object/images/" + fileName);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Authorization", "Bearer " + userToken);
                conn.setRequestProperty("apikey", SupabaseService.getApiKey());
                conn.setRequestProperty("Content-Type", "image/jpeg");
                conn.setDoOutput(true);

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                }

                int responseCode = conn.getResponseCode();
                if (responseCode == 200 || responseCode == 201) {
                    String imageUrl = "https://etfmwhmqmnnsatkirrkx.supabase.co/storage/v1/object/public/images/" + fileName;
                    saveJournalToSupabase(imageUrl);
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Image upload failed!", Toast.LENGTH_SHORT).show());
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error uploading image", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
    public String saveImageToInternalStorage(Bitmap bitmap, String filename) {
        try {
            File file = new File(getFilesDir(), filename);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveJournalToSupabase(String imageUrl) {
        String journalId = UUID.randomUUID().toString();
        String title = editTitle.getText().toString();
        String description = editDescription.getText().toString();


        new Thread(() -> {
            int response = SupabaseService.insertJournal(
                    userToken,
                    journalId,
                    userId,
                    title,
                    description,
                    imageUrl != null ? imageUrl : ""
            );

            runOnUiThread(() -> {
                if (response == 201 || response == 200) {
                    Toast.makeText(this, "Journal created successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Failed to create journal. Code: " + response, Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}

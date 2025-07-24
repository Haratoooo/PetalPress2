package ph.edu.usc.petalpress;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import io.supabase.gotrue.GotrueSession;
import io.supabase.supabase.SupabaseClient;
import io.supabase.supabase.SupabaseException;
import io.supabase.supabase.SupabaseTable;
import io.supabase.storage.StorageClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class AddJournalActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView imagePreview;
    private EditText editTitle, editDescription;
    private Button btnCreate, btnCancel;
    private SupabaseClient supabaseClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journal);

        // Initialize Supabase client with URL and API Key
        supabaseClient = new SupabaseClient.Builder()
                .url("https://supabase.com/dashboard/project/etfmwhmqmnnsatkirrkx") // Your Supabase URL
                .apikey("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImV0Zm13aG1xbW5uc2F0a2lycmt4Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTE5NTAwODgsImV4cCI6MjA2NzUyNjA4OH0.TihBvneGpMnny29L9GU7i5Mn3I12jyc3HLv0I5qxPZQ") // Your Supabase API Key
                .build();

        // Find views
        imagePreview = findViewById(R.id.imagePreview);
        editTitle = findViewById(R.id.editTitle);
        editDescription = findViewById(R.id.editDescription);
        btnCreate = findViewById(R.id.btnCreate);
        btnCancel = findViewById(R.id.btnCancel);

        // Image picker on cover photo click
        imagePreview.setOnClickListener(v -> openImagePicker());

        // Handle Cancel button click (finish activity)
        btnCancel.setOnClickListener(v -> finish());

        // Handle Create button click (validate and submit)
        btnCreate.setOnClickListener(v -> {
            String title = editTitle.getText().toString().trim();
            String description = editDescription.getText().toString().trim();

            // Validate input fields
            if (TextUtils.isEmpty(title)) {
                Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(description)) {
                Toast.makeText(this, "Please enter a description", Toast.LENGTH_SHORT).show();
                return;
            }

            if (imageUri != null) {
                // Upload the image and then save journal
                uploadImageToSupabase(imageUri);
            } else {
                // If no image selected, save without the image
                saveJournalToSupabase(null);
            }
        });
    }

    // Open image picker to choose a cover photo
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Cover Photo"), PICK_IMAGE_REQUEST);
    }

    // Handle image picker result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            // Set the image preview to selected image
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imagePreview.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Upload image to Supabase Storage
    private void uploadImageToSupabase(Uri imageUri) {
        // Get the Supabase Storage client
        StorageClient storageClient = supabaseClient.storage().from("images");  // Assuming 'images' is your bucket name

        // Convert the URI to an InputStream
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            String fileName = UUID.randomUUID().toString() + ".jpg"; // Generate a unique file name

            // Upload image to Supabase Storage
            storageClient.upload(fileName, inputStream)
                    .onSuccess(result -> {
                        // Get the public URL of the uploaded image
                        String imageUrl = result.publicUrl();
                        saveJournalToSupabase(imageUrl); // Save the journal with the image URL
                    })
                    .onFailure(exception -> {
                        // Handle failure
                        Toast.makeText(this, "Image upload failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error opening image", Toast.LENGTH_SHORT).show();
        }
    }

    // Save journal to Supabase
    private void saveJournalToSupabase(String imageUrl) {
        // Get Supabase session and user
        GotrueSession session = supabaseClient.auth.getSession();
        User currentUser = session.getUser();

        if (currentUser == null) {
            Toast.makeText(this, "User is not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate a unique journal ID
        String journalId = UUID.randomUUID().toString();

        // Prepare the data to insert into Supabase table
        SupabaseTable journalsTable = supabaseClient.from("journals");

        try {
            journalsTable.insert()
                    .values(
                            "id", journalId,
                            "title", editTitle.getText().toString(),
                            "description", editDescription.getText().toString(),
                            "user_id", currentUser.getId(),
                            "image_name", imageUrl, // Save the image URL
                            "entry_count", 0 // Set initial entry count to 0
                    )
                    .execute();

            Toast.makeText(this, "Journal created successfully", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity and return to the homepage

        } catch (SupabaseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to create journal", Toast.LENGTH_SHORT).show();
        }
    }
}

package ph.edu.usc.petalpress;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.supabase.gotrue.GotrueSession;
import com.supabase.supabase.SupabaseClient;
import com.supabase.supabase.SupabaseException;
import com.supabase.supabase.SupabaseTable;
import com.supabase.supabase.models.User;

import java.io.IOException;
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

        // Initialize Supabase client
        supabaseClient = new SupabaseClient();

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

            // Save journal to Supabase
            saveJournalToSupabase(title, description);
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

    // Save journal to Supabase
    private void saveJournalToSupabase(String title, String description) {
        // Prepare Supabase session to get current user ID
        GotrueSession session = supabaseClient.auth.getSession();
        User currentUser = session.getUser();

        if (currentUser == null) {
            Toast.makeText(this, "User is not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate unique journal ID
        String journalId = UUID.randomUUID().toString();

        // If image is selected, upload it to Supabase storage (this can be expanded to support image upload functionality)
        String imageUrl = null; // Placeholder for the image URL after upload

        // Call Supabase API to insert the journal record into the `journals` table
        SupabaseTable journalsTable = supabaseClient.from("journals");

        try {
            journalsTable.insert()
                    .values(
                            "id", journalId,
                            "title", title,
                            "description", description,
                            "user_id", currentUser.getId(),
                            "image_name", imageUrl,  // This can be the image URL after uploading
                            "entry_count", 0 // Set initial entry count to 0
                    )
                    .execute();

            // Notify user and close the activity
            Toast.makeText(this, "Journal created successfully", Toast.LENGTH_SHORT).show();
            finish();

        } catch (SupabaseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to create journal", Toast.LENGTH_SHORT).show();
        }
    }
}

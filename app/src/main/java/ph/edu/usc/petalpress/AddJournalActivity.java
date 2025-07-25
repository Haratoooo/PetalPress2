import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.provider.MediaStore;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class AddJournalActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView imagePreview;
    private EditText editTitle, editDescription;
    private Button btnCreate, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journal);

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

            // Save journal locally
            saveJournalLocally(title, description);
        });
    }

    // Open image picker to choose a cover photo
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");  // Set the type to image only
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

    // Save journal to local storage
    private void saveJournalLocally(String title, String description) {
        // Generate a unique journal ID
        String journalId = UUID.randomUUID().toString();

        // Get file name for the image (if image is selected)
        String imageFileName = null;
        if (imageUri != null) {
            imageFileName = saveImageToInternalStorage(imageUri);
        }

        // Save journal details to SharedPreferences or SQLite
        // Here, we use SharedPreferences as an example

        // Get SharedPreferences editor
        Context context = getApplicationContext();
        String fileName = "journals";
        SharedPreferences sharedPref = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        // Store journal details in SharedPreferences
        editor.putString("journal_id", journalId);
        editor.putString("title", title);
        editor.putString("description", description);
        editor.putString("image_file", imageFileName);
        editor.apply();  // Save the changes

        Toast.makeText(this, "Journal saved locally", Toast.LENGTH_SHORT).show();
        finish();  // Close the activity and return to the previous screen
    }

    // Save image to internal storage and return the file name
    private String saveImageToInternalStorage(Uri imageUri) {
        try {
            // Create a file name for the image
            String fileName = UUID.randomUUID().toString() + ".jpg";

            // Open the input stream
            InputStream inputStream = getContentResolver().openInputStream(imageUri);

            // Open an output stream to save the image in internal storage
            OutputStream outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);

            // Write the image data to internal storage
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            // Close the streams
            inputStream.close();
            outputStream.close();

            return fileName;  // Return the file name where the image is stored
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving image", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}

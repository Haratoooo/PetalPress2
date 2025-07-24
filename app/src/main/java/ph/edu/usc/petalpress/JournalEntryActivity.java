package ph.edu.usc.petalpress;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Stack;

public class JournalEntryActivity extends AppCompatActivity {

    private LinearLayout toolbarLayout, textParentWrapper;
    private ImageButton toggleButton;
    private EditText entryEditText;
    private ImageButton undoButton, redoButton;

    private final Stack<String> undoStack = new Stack<>();
    private final Stack<String> redoStack = new Stack<>();

    private boolean isUndoOrRedo = false;  // Flag to avoid triggering TextWatcher during undo/redo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_entry);

        // Initialize views
        toolbarLayout = findViewById(R.id.toolbar_toggle);
        textParentWrapper = findViewById(R.id.textparentwrapper);
        toggleButton = findViewById(R.id.toolbar_toggle_btn);
        entryEditText = findViewById(R.id.entryEditText);
        undoButton = findViewById(R.id.undobtn);
        redoButton = findViewById(R.id.redobtn);

        // Initially hide the toolbar
        toolbarLayout.setVisibility(View.GONE);

        // Save the initial text state
        undoStack.push(entryEditText.getText().toString());

        // Toolbar toggle logic
        toggleButton.setOnClickListener(v -> {
            if (toolbarLayout.getVisibility() == View.VISIBLE) {
                toolbarLayout.setVisibility(View.GONE);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) textParentWrapper.getLayoutParams();
                params.addRule(RelativeLayout.BELOW, R.id.header);
                textParentWrapper.setLayoutParams(params);
            } else {
                toolbarLayout.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) textParentWrapper.getLayoutParams();
                params.addRule(RelativeLayout.BELOW, R.id.toolbar_toggle);
                textParentWrapper.setLayoutParams(params);
            }
        });

        // Watch for user text input (not from undo/redo)
        entryEditText.addTextChangedListener(new TextWatcher() {
            private String previousText = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!isUndoOrRedo) {
                    previousText = s.toString();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isUndoOrRedo) {
                    if (!s.toString().equals(previousText)) {
                        undoStack.push(previousText);
                        redoStack.clear();
                    }
                }
            }
        });

        // Undo logic
        undoButton.setOnClickListener(v -> {
            if (!undoStack.isEmpty()) {
                isUndoOrRedo = true;
                redoStack.push(entryEditText.getText().toString());

                String previousText = undoStack.pop();
                entryEditText.setText(previousText);
                entryEditText.setSelection(previousText.length());

                isUndoOrRedo = false;
            }
        });

        // Redo logic
        redoButton.setOnClickListener(v -> {
            if (!redoStack.isEmpty()) {
                isUndoOrRedo = true;
                undoStack.push(entryEditText.getText().toString());

                String nextText = redoStack.pop();
                entryEditText.setText(nextText);
                entryEditText.setSelection(nextText.length());

                isUndoOrRedo = false;
            }
        });
    }
}

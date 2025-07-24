package ph.edu.usc.petalpress;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Stack;

public class JournalEntryActivity extends AppCompatActivity {

    private LinearLayout toolbarLayout, textParentWrapper;
    private ImageButton toggleButton;
    private EditText entryEditText;
    private ImageButton undoButton, redoButton;
    private View popupView;
    private PopupWindow formattingPopup;
    private final Stack<String> undoStack = new Stack<>();
    private final Stack<String> redoStack = new Stack<>();
    private boolean isUndoOrRedo = false;

    // New: Track selection range
    private int selectionStart = -1;
    private int selectionEnd = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_entry);

        toolbarLayout = findViewById(R.id.toolbar_toggle);
        textParentWrapper = findViewById(R.id.textparentwrapper);
        toggleButton = findViewById(R.id.toolbar_toggle_btn);
        entryEditText = findViewById(R.id.entryEditText);
        undoButton = findViewById(R.id.undobtn);
        redoButton = findViewById(R.id.redobtn);
        ImageButton textFormattingBtn = findViewById(R.id.textformattingbtn);

        toolbarLayout.setVisibility(View.GONE);
        undoStack.push(entryEditText.getText().toString());

        ImageView backButton = findViewById(R.id.ic_back_arrow);
        backButton.setOnClickListener(v -> {
            saveJournalEntry(); // Replace with your actual save logic if needed

            Intent intent = new Intent(JournalEntryActivity.this, Homepage.class); // or MainActivity.class, etc.
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });


        // Toggle toolbar
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

        // Save selection, then show popup
        textFormattingBtn.setOnClickListener(v -> {
            selectionStart = entryEditText.getSelectionStart();
            selectionEnd = entryEditText.getSelectionEnd();

            if (formattingPopup != null && formattingPopup.isShowing()) {
                formattingPopup.dismiss();
            } else {
                showTextFormattingPopup(v);
            }
        });

        entryEditText.addTextChangedListener(new TextWatcher() {
            private String previousText = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!isUndoOrRedo) previousText = s.toString();
            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (!isUndoOrRedo && !s.toString().equals(previousText)) {
                    undoStack.push(previousText);
                    redoStack.clear();
                }
            }
        });

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

    private void showTextFormattingPopup(View anchorView) {
        popupView = getLayoutInflater().inflate(R.layout.text_formatting_popup, null);

        int popupWidth = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 209, getResources().getDisplayMetrics());
        int popupHeight = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());

        formattingPopup = new PopupWindow(popupView, popupWidth, popupHeight, true);
        formattingPopup.setElevation(8f);
        formattingPopup.setOutsideTouchable(true);
        formattingPopup.setFocusable(true);
        formattingPopup.showAsDropDown(anchorView, -30, 10);

        TextView heading1 = popupView.findViewById(R.id.heading1);
        TextView heading2 = popupView.findViewById(R.id.heading2);
        TextView heading3 = popupView.findViewById(R.id.heading3);
        TextView subheading = popupView.findViewById(R.id.subheading);

        View.OnClickListener styleClickListener = v -> {
            int start = entryEditText.getSelectionStart();
            int end = entryEditText.getSelectionEnd();

            if (start < end) {
                SpannableStringBuilder spannable = new SpannableStringBuilder(entryEditText.getText());

                if (v.getId() == R.id.heading1) {
                    spannable.setSpan(new RelativeSizeSpan(1.8f), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannable.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else if (v.getId() == R.id.heading2) {
                    spannable.setSpan(new RelativeSizeSpan(1.5f), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannable.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else if (v.getId() == R.id.heading3) {
                    spannable.setSpan(new RelativeSizeSpan(1.3f), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannable.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else if (v.getId() == R.id.subheading) {
                    spannable.setSpan(new RelativeSizeSpan(1.2f), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannable.setSpan(new StyleSpan(Typeface.ITALIC), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                entryEditText.setText(spannable);
                entryEditText.setSelection(end); // maintain cursor
                formattingPopup.dismiss(); // optional: hide popup after applying
            }
        };

        heading1.setOnClickListener(styleClickListener);
        heading2.setOnClickListener(styleClickListener);
        heading3.setOnClickListener(styleClickListener);
        subheading.setOnClickListener(styleClickListener);


        // Set up format buttons
        ImageButton boldBtn = popupView.findViewById(R.id.bold_btn);
        ImageButton italicBtn = popupView.findViewById(R.id.italic_btn);
        ImageButton underlineBtn = popupView.findViewById(R.id.underline_btn);
        ImageButton strikethroughBtn = popupView.findViewById(R.id.strikethrough_btn);

        boldBtn.setOnClickListener(v -> applyStyleSpan(Typeface.BOLD));
        italicBtn.setOnClickListener(v -> applyStyleSpan(Typeface.ITALIC));
        underlineBtn.setOnClickListener(v -> applyUnderline());
        strikethroughBtn.setOnClickListener(v -> {
            int start = entryEditText.getSelectionStart();
            int end = entryEditText.getSelectionEnd();

            if (start < end) {
                SpannableStringBuilder spannable = new SpannableStringBuilder(entryEditText.getText());
                spannable.setSpan(new android.text.style.StrikethroughSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                entryEditText.setText(spannable);
                entryEditText.setSelection(end); // Preserve cursor
            }
        });

        // Optional: set up spinners (basic example)
        Spinner fontSpinner = popupView.findViewById(R.id.font_spinner);
        Spinner sizeSpinner = popupView.findViewById(R.id.size_spinner);

        String[] fonts = {"sans-serif", "serif", "monospace"};
        String[] sizes = {"12", "14", "16", "18", "20", "24"};

        ArrayAdapter<String> fontAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fonts);
        fontAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fontSpinner.setAdapter(fontAdapter);

        ArrayAdapter<String> sizeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sizes);
        sizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sizeSpinner.setAdapter(sizeAdapter);

        fontSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean isFirstSelection = true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (isFirstSelection) {
                    isFirstSelection = false;
                    return; // Skip default auto-select on popup open
                }
                if (selectionStart >= 0 && selectionEnd > selectionStart) {
                    SpannableStringBuilder spannable = new SpannableStringBuilder(entryEditText.getText());
                    android.text.style.TypefaceSpan typefaceSpan = new android.text.style.TypefaceSpan(fonts[pos]);
                    spannable.setSpan(typefaceSpan, selectionStart, selectionEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    entryEditText.setText(spannable);
                    entryEditText.setSelection(selectionEnd);
                }
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        sizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean isFirstSelection = true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (isFirstSelection) {
                    isFirstSelection = false;
                    return;
                }
                if (selectionStart >= 0 && selectionEnd > selectionStart) {
                    SpannableStringBuilder spannable = new SpannableStringBuilder(entryEditText.getText());
                    int sizeInPx = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP,
                            Float.parseFloat(sizes[pos]),
                            getResources().getDisplayMetrics()
                    );
                    android.text.style.AbsoluteSizeSpan sizeSpan = new android.text.style.AbsoluteSizeSpan(sizeInPx);
                    spannable.setSpan(sizeSpan, selectionStart, selectionEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    entryEditText.setText(spannable);
                    entryEditText.setSelection(selectionEnd);
                }
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

    }

    private void applyStyleSpan(int style) {
        if (selectionStart < selectionEnd) {
            SpannableStringBuilder spannable = new SpannableStringBuilder(entryEditText.getText());
            spannable.setSpan(new StyleSpan(style), selectionStart, selectionEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            entryEditText.setText(spannable);
            entryEditText.setSelection(selectionEnd);
        }
    }

    private void applyUnderline() {
        if (selectionStart < selectionEnd) {
            SpannableStringBuilder spannable = new SpannableStringBuilder(entryEditText.getText());
            spannable.setSpan(new UnderlineSpan(), selectionStart, selectionEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            entryEditText.setText(spannable);
            entryEditText.setSelection(selectionEnd);
        }
    }

    private void saveJournalEntry() {
        String text = entryEditText.getText().toString();
        // TODO: Save to database, SharedPreferences, file, etc.
        // For now you could just log or simulate saving
        Log.d("JournalEntry", "Saved entry: " + text);
    }

}

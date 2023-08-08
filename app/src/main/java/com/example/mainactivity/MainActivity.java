package com.example.mainactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private EditText editTxt;

    private EditText outputTxt;
    private Button translateBtn;

    private Spinner langSpinner;

    private String selectedLang, selectedLangCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTxt = findViewById(R.id.translateTxt);
        translateBtn = findViewById(R.id.translateBtn);
        langSpinner = findViewById(R.id.spinnerLang);
        outputTxt = findViewById(R.id.outputTxt);

        //Disables the output editText
        outputTxt.setInputType(InputType.TYPE_NULL);

        // Initialize spinner with supported languages
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.supported_lang, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        langSpinner.setAdapter(adapter);

        // Set listener for spinner item selection
        langSpinner.setOnItemSelectedListener(this);
        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Checks if the edit text is empty or not
                * if it's empty, a message will appear, stating it so*/
                if(TextUtils.isEmpty(editTxt.getText().toString())){
                    Toast.makeText(MainActivity.this, "Text is empty", Toast.LENGTH_SHORT).show();
                }else{
                    langSpinner.getSelectedItem().toString();

                    // Build TranslatorOptions with the target language "tl" (Tagalog) and the source language "en" (English)
                    TranslatorOptions options = new TranslatorOptions.Builder()
                            .setTargetLanguage(selectedLangCode)
                            .setSourceLanguage("en").build();

                    // Create a Translator instance based on the given options
                    Translator translator = Translation.getClient(options);

                    // Get the source language text from the edit text
                    String sourceLang = editTxt.getText().toString();

                    // Show a progress dialog while downloading the translation model
                    ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("Downloading translation model");
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    // Downloads model if needed and dismisses the progress dialog on success or failure
                    translator.downloadModelIfNeeded().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            progressDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                        }
                    });

                    // Translate the text using the Translator instance and listen for success or failure
                    Task<String> result = translator.translate(sourceLang).addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String s) {
                            // If translation is successful, display the translated text in the output text field
                            outputTxt.setText(s);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // If translation is successful, display the translated text using a toast
                            Toast.makeText(MainActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        // Retrieve selected language name and its corresponding code
        selectedLang = adapterView.getItemAtPosition(i).toString();
        selectedLangCode = getResources().getStringArray(R.array.supported_lang_codes)[i];
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        
    }
}
package com.shvmsaini.booklisting;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
    public TextView resultsPerPage;
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public static String KEY = "searchPerPage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        resultsPerPage = findViewById(R.id.results_config);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();
        resultsPerPage.setOnClickListener(v -> show());
        int results = sharedPreferences.getInt(KEY,10);
        resultsPerPage.setText(String.valueOf(results));

        sharedPreferences.registerOnSharedPreferenceChangeListener((sharedPreferences, s) ->{
                    BookActivity.maxResults= sharedPreferences.getInt(KEY,BookActivity.maxResults);
                    Log.d(SettingsActivity.class.toString(), "onSharedPreferenceChange: maxResults=" + BookActivity.maxResults);
                }
               );

    }

    public void show() {
        final Dialog dialog = new Dialog(SettingsActivity.this,R.style.Dialog);
        dialog.setTitle("Results per page");
        dialog.setContentView(R.layout.number_dialog);
        Button cancelButton = dialog.findViewById(R.id.cancelButton);
        Button setButton = dialog.findViewById(R.id.setButton);

        final NumberPicker np = dialog.findViewById(R.id.numberPicker);
        np.setMaxValue(40);
        np.setMinValue(10);

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        setButton.setOnClickListener(v -> {
            resultsPerPage.setText(String.valueOf(np.getValue()));

            editor = sharedPreferences.edit();

            editor.putInt("searchPerPage", np.getValue()).apply();

            dialog.dismiss();
        });
        dialog.show();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
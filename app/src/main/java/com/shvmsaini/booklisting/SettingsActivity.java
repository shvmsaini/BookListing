package com.shvmsaini.booklisting;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    public TextView resultsPerPage;
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        resultsPerPage = findViewById(R.id.results_config);

        sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        resultsPerPage.setOnClickListener(v -> show());
        String KEY = "searchPerPage";
        int results = sharedPreferences.getInt(KEY,12);
        resultsPerPage.setText(String.valueOf(results));


    }

    public void show() {

        final Dialog dialog = new Dialog(SettingsActivity.this,R.style.Dialog);
        dialog.setTitle("Results per page");
        dialog.setContentView(R.layout.number_dialog);
        Button cancelButton = dialog.findViewById(R.id.cancelButton);
        Button setButton = dialog.findViewById(R.id.setButton);

        final NumberPicker np = dialog.findViewById(R.id.numberPicker);
        np.setMaxValue(50);
        np.setMinValue(10);

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        setButton.setOnClickListener(v -> {
            resultsPerPage.setText(String.valueOf(np.getValue()));
            sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);

            editor = sharedPreferences.edit();

            editor.putInt("searchPerPage", np.getValue()).apply();
            dialog.dismiss();
        });
        dialog.show();


    }

}
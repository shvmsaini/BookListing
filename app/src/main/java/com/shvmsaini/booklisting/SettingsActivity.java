package com.shvmsaini.booklisting;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    private TextView resultsPerPage;
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    private final String key = "searchPerPage";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        resultsPerPage = findViewById(R.id.results_config);

        sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        resultsPerPage.setOnClickListener(v -> {
            show();
        });

//        resultsPerPage.setText(sharedPreferences.getInt(key,10));
//        editor.putInt(key,15).apply();


//        Log.d("INSIDE", String.valueOf(sharedPreferences.getInt("searchPerPage",10)));

    }
    public void show() {

        final Dialog dialog = new Dialog(SettingsActivity.this);
        dialog.setTitle("How many results do you want to see per page?");
        dialog.setContentView(R.layout.number_dialog);
        Button cancelButton = dialog.findViewById(R.id.cancelButton);
        Button setButton = dialog.findViewById(R.id.setButton);

        final NumberPicker np = dialog.findViewById(R.id.numberPicker);
        np.setMaxValue(50);
        np.setMinValue(10);
        np.setOnValueChangedListener((picker, oldVal, newVal) -> {
            editor.putInt(key,np.getValue()).apply();

            Log.d("INSIDE", String.valueOf(newVal));
        });
        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
        });
        setButton.setOnClickListener(v ->{
            resultsPerPage.setText(String.valueOf(np.getValue()));
            editor.putInt("searchPerPage",np.getValue()).apply();

            dialog.dismiss();
        });
        dialog.show();


    }

}
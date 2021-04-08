package com.shvmsaini.booklisting;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

public class BookActivity extends AppCompatActivity {
    public static BookAdapter bookAdapter;
    ListView bookView;
    public static String SAMPLE_URL = "https://www.googleapis.com/books/v1/volumes?q=Search+terms";
    public ArrayList<Book> books = new ArrayList<>();
    public BookViewModel bookViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitymain);
        bookView = findViewById(R.id.list);
        TextView emptyStateView = findViewById(R.id.empty_view);
        EditText searchQuery = findViewById(R.id.search_query);
        bookView.setEmptyView(emptyStateView);
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        bookAdapter = new BookAdapter(BookActivity.this,books);
        bookViewModel = new ViewModelProvider(this).get(BookViewModel.class);

        if (networkInfo != null && networkInfo.isConnected()) {
            bookViewModel.getBooks(SAMPLE_URL).observe(this, books -> {
                Log.d("INSIDE", "observe");
                bookAdapter = new BookAdapter(this,books);
                bookView.setAdapter(bookAdapter);
            });

        } else {
            emptyStateView.setText(R.string.no_internet_connection);
        }

        Button button = findViewById(R.id.search_button);
        button.setOnClickListener(v -> {
            String query = searchQuery.getText().toString().trim().replace(" ","+");
            bookViewModel.loadBooks("https://www.googleapis.com/books/v1/volumes?q=" + query);
            hideKeyboard(BookActivity.this);
        });
        searchQuery.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                button.performClick();
            }
            return false;
        });


    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}










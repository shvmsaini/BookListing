package com.shvmsaini.booklisting;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

public class BookActivity extends AppCompatActivity {
    public BookAdapter bookAdapter;
    public ListView bookView;
    public static String SAMPLE_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    public ArrayList<Book> books = new ArrayList<>();
    public BookViewModel bookViewModel;
    public TextView emptyStateView;
    public SearchView searchQuery;
    public ConnectivityManager connMgr;
    public NetworkInfo networkInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitymain);
        bookView = findViewById(R.id.list);
        emptyStateView = findViewById(R.id.empty_view);
        searchQuery = findViewById(R.id.search_query);
        bookView.setEmptyView(emptyStateView);
        bookAdapter = new BookAdapter(BookActivity.this, books);
        bookViewModel = new ViewModelProvider(this).get(BookViewModel.class);

        // For checking if network is connected
        connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            bookViewModel.getBooks(SAMPLE_URL).observe(this, books -> {
                bookAdapter = new BookAdapter(this, books);
                bookView.setAdapter(bookAdapter);
            });

        } else {
            emptyStateView.setText(R.string.no_internet_connection);
        }

        searchQuery.setSubmitButtonEnabled(true);
        searchQuery.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String temp = searchQuery.getQuery().toString();
                query = temp.trim().replace(" ", "+");
                if (temp.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Search bar is empty!", Toast.LENGTH_SHORT).show();
                    return false;
                }
                bookViewModel.loadBooks(SAMPLE_URL + query);
                hideKeyboard(BookActivity.this);
                searchQuery.clearFocus();
                emptyStateView.setText(R.string.loading);
                emptyStateView.setVisibility(View.VISIBLE);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Destroyed", Toast.LENGTH_SHORT).show();
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
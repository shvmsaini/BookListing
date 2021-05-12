package com.shvmsaini.booklisting;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

public class BookActivity extends AppCompatActivity {
    public BookAdapter bookAdapter;
    public ListView bookView;
    public static String SAMPLE_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    public ArrayList<Book> books = new ArrayList<>();
    public BookViewModel bookViewModel;
    public TextView emptyStateView;
    public ConnectivityManager connMgr;
    public NetworkInfo networkInfo;
    public FloatingActionButton nextButton;
    public FloatingActionButton previousButton;
    public Button settingsButton;
    public int pageNumber = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitymain);

        bookView = findViewById(R.id.list);
        emptyStateView = findViewById(R.id.empty_view);
        bookView.setEmptyView(emptyStateView);
        bookAdapter = new BookAdapter(BookActivity.this, books);
        bookViewModel = new ViewModelProvider(this).get(BookViewModel.class);

        // For checking if network is connected
        connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            bookViewModel.getBooks(SAMPLE_URL).observe(this, books -> {
                if (books.size() == 0) {
                    emptyStateView.setText(R.string.no_books_found);
                }
                bookAdapter = new BookAdapter(this, books);
                bookView.setAdapter(bookAdapter);
            });

        } else {
            emptyStateView.setText(R.string.no_internet_connection);

        }

    }

    @Override
    public void startActivity(Intent intent) {
        Log.d("INSIDE", "startActivity");
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            return;
        }

        super.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        MenuItem clearHistoryItem = menu.findItem(R.id.history);
        MenuItem settingsItem = menu.findItem(R.id.settings);
        settingsItem.setOnMenuItemClickListener(v -> {
            Intent intent= new Intent(BookActivity.this,SettingsActivity.class);
            BookActivity.this.startActivity(intent);
            return false;
        });
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);

        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);

        clearHistoryItem.setOnMenuItemClickListener(item -> {
            suggestions.clearHistory();
            return false;
        });

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
                String suggestion = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
                searchView.setQuery(suggestion, true);
                return false;
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                pageNumber = 1;
                String temp = searchView.getQuery().toString().trim();
                Objects.requireNonNull(getSupportActionBar()).setTitle("Find: " + temp);
                suggestions.saveRecentQuery(temp, null);
                query = temp.replace(" ", "+");
                bookViewModel.loadBooks(SAMPLE_URL + query);
                hideKeyboard(BookActivity.this);
                searchView.clearFocus();
                emptyStateView.setText(R.string.loading);
                emptyStateView.setVisibility(View.VISIBLE);
                bookView.setVisibility(View.GONE);
                searchItem.collapseActionView();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        nextButton = findViewById(R.id.page_next);
        previousButton = findViewById(R.id.page_previous);
        nextButton.setOnClickListener(v -> {
            pageNumber++;
            bookViewModel.loadBooks(SAMPLE_URL + searchView.getQuery().toString() + ",page=" + pageNumber);
            emptyStateView.setText(R.string.loading);
            emptyStateView.setVisibility(View.VISIBLE);
            bookView.setVisibility(View.GONE);
            Log.d("INSIDE", searchView.getQuery().toString() + ",page=" + pageNumber);

        });
        previousButton.setOnClickListener(v -> {
            if(pageNumber>1){
                pageNumber--;
                bookViewModel.loadBooks(SAMPLE_URL + searchView.getQuery().toString() + ",page=" + pageNumber);
                emptyStateView.setText(R.string.loading);
                emptyStateView.setVisibility(View.VISIBLE);
                bookView.setVisibility(View.GONE);
                Log.d("INSIDE", searchView.getQuery().toString() + ",page=" + pageNumber);
            }

        });
        return true;
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
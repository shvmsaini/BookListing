package com.shvmsaini.booklisting;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

public class BookActivity extends AppCompatActivity {
    public static BookAdapter bookAdapter;
    public ListView bookView;
    public static String SAMPLE_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    public ArrayList<Book> books = new ArrayList<>();
    public BookViewModel bookViewModel;
    public TextView emptyStateView;
    public ConnectivityManager connMgr;
    public NetworkInfo networkInfo;
    public FloatingActionButton nextButton;
    public FloatingActionButton previousButton;
    public int pageNumber = 1;
    public static int maxResults = 10;
    public SearchView searchView;
    public SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitymain);
        //setting up views
        nextButton = findViewById(R.id.page_next);
        previousButton = findViewById(R.id.page_previous);
        bookView = findViewById(R.id.list);
        emptyStateView = findViewById(R.id.empty_view);
        bookView.setEmptyView(emptyStateView);

        bookAdapter = new BookAdapter(BookActivity.this, books);
        bookViewModel = new ViewModelProvider(this).get(BookViewModel.class);
        toggleButtonVisibility(false);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        maxResults = sharedPreferences.getInt(SettingsActivity.KEY,maxResults);

        if (networkChecker()) {
            bookViewModel.getBooks(SAMPLE_URL).observe(this, books -> {
                if (books.size() == 0) {
                    emptyStateView.setText(R.string.no_books_found);
                    toggleButtonVisibility(false);
                }else{
                    bookAdapter = new BookAdapter(this, books);
                    bookView.setAdapter(bookAdapter);
                    toggleButtonVisibility(true);
                }

            });

        } else {
            emptyStateView.setText(R.string.no_internet_connection);
        }

        bookView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent= new Intent(BookActivity.this,BookDetailActivity.class);
            intent.putExtra("int", position);
            intent.putExtra("string",searchView.getQuery().toString());
            BookActivity.this.startActivity(intent);
        });
    }

    @Override
    public void startActivity(Intent intent) {
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
        searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);

        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this, MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);

        clearHistoryItem.setOnMenuItemClickListener(item -> {
            suggestions.clearHistory();
            return true;
        });
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                if(networkChecker()){
                    Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
                    String suggestion = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
                    searchView.setQuery(suggestion, true);
                    toggleButtonVisibility(false);
                    return true;
                }
                else{
                    bookAdapter.clear();
                    emptyStateView.setText(R.string.no_internet_connection);
                    return false;
                }

            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(networkChecker()){
                    pageNumber = 1;
                    String temp = searchView.getQuery().toString().trim();
                    Objects.requireNonNull(getSupportActionBar()).setTitle("Find: " + temp);
                    suggestions.saveRecentQuery(temp, null);
                    query = temp.replace(" ", "+");
                    bookViewModel.loadBooks(SAMPLE_URL + query + "&maxResults=" + sharedPreferences.getInt(SettingsActivity.KEY,10));

                    hideKeyboard(BookActivity.this);
                    searchView.clearFocus();
                    emptyStateView.setText(R.string.loading);
                    emptyStateView.setVisibility(View.VISIBLE);
                    bookView.setVisibility(View.GONE);
                    searchItem.collapseActionView();
                    return true;
                }
                else{
                    bookAdapter.clear();
                    emptyStateView.setText(R.string.no_internet_connection);
                    return false;
                }
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        nextButton.setOnClickListener(v -> pageChange(pageNumber++));
        previousButton.setOnClickListener(v -> {
            if(pageNumber>1) pageChange(pageNumber--);
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

    public void toggleButtonVisibility(boolean state){
        if(state){
            nextButton.setVisibility(View.VISIBLE);
            previousButton.setVisibility(View.VISIBLE);
        }else{
            nextButton.setVisibility(View.GONE);
            previousButton.setVisibility(View.GONE);
        }

    }

    public void pageChange(int pageNumber){
        bookViewModel.loadBooks(SAMPLE_URL + searchView.getQuery().toString() +
                "&maxResults=" +sharedPreferences.getInt(SettingsActivity.KEY,10)+
                "&startIndex=" + (pageNumber-1)*sharedPreferences.getInt(SettingsActivity.KEY,10));
        emptyStateView.setText(R.string.loading);
        emptyStateView.setVisibility(View.VISIBLE);
        bookView.setVisibility(View.GONE);
        toggleButtonVisibility(false);
    }

    public boolean networkChecker(){
        // For checking if network is connected
        connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
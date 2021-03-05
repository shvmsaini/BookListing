package com.shvmsaini.booklisting;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class BookActivity extends AppCompatActivity {
    public BookAdapter bookAdapter;
    ListView bookView;
    public final static String SAMPLE_URL = "https://www.googleapis.com/books/v1/volumes?q=search+terms";
    public ArrayList<Book> books = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitymain);
        bookView = findViewById(R.id.list);
        bookAdapter = new BookAdapter(BookActivity.this,books);
        thread.start();
        Button button = findViewById(R.id.search_button);
        button.setOnClickListener(v -> Log.d("INSIDE","button.setOn"));

    }

    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            URL url = QueryUtility.createURL(SAMPLE_URL);
            String JSONResponse;
            try{
                assert url != null;
                JSONResponse = QueryUtility.ReadFromStream(QueryUtility.MakeHTTPRequest(url));
                books = new ArrayList<>(QueryUtility.extractBooksFromJSON(JSONResponse));
            }
            catch (IOException | JSONException ioException){
                ioException.printStackTrace();

            }
            runOnUiThread(() -> {
                bookAdapter.addAll(books);
                bookView.setAdapter(bookAdapter);
            });
            bookAdapter.notifyDataSetChanged();
        }

    });


}










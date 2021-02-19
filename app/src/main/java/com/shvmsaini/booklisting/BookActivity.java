package com.shvmsaini.booklisting;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class BookActivity extends AppCompatActivity {
    BookAsyncTask bookAsyncTask = new BookAsyncTask();
    private BookAdapter bookAdapter;
    ListView bookView;
    private final static String SAMPLE_URL = "https://www.googleapis.com/books/v1/volumes?q=search+terms";
    private String TAG = getClass().toString();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitymain);
         bookView = findViewById(R.id.list);
        ArrayList<Book> books = new ArrayList<>();
        bookAdapter = new BookAdapter(this,books);
        BookAsyncTask bookAsyncTask = new BookAsyncTask();
        bookAsyncTask.execute(SAMPLE_URL);

       Button button = findViewById(R.id.search_button);
       button.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String SAMPLE_URL1 = null;
               EditText text = findViewById(R.id.search_query);
               SAMPLE_URL1 = text.getText().toString();
               SAMPLE_URL1 = SAMPLE_URL.substring(0,46) + SAMPLE_URL1;
               Log.d("SAMPLE_URL",SAMPLE_URL1);
               
               BookAsyncTask bookAsyncTask = new BookAsyncTask();

               bookAsyncTask.execute(SAMPLE_URL1);
           }
       });

    }
    private class BookAsyncTask extends AsyncTask<String,Void,ArrayList<Book>>{
        @Override
        protected ArrayList<Book> doInBackground(String... urls) {
            Log.d(TAG,"inside doinbg");
            URL url = QueryUtility.createURL(urls[0]);
            String JSONResponse = null;
            try{
                assert url != null;
                JSONResponse = QueryUtility.ReadFromStream(QueryUtility.MakeHTTPRequest(url));

            }catch (IOException ioException){
                ioException.printStackTrace();

            }
            try {
                return (ArrayList<Book>) QueryUtility.extractBooksFromJSON(JSONResponse);
            } catch (JSONException | IOException ioException) {
                ioException.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Book> books) {
            bookAdapter.addAll(books);
            bookView.setAdapter(bookAdapter);
            Log.d(TAG,"inside onpostexecute");
//            bookAsyncTask.cancel(true);
//            bookAsyncTask=new BookAsyncTask();

        }
    }

}










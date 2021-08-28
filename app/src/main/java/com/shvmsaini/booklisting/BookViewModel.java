package com.shvmsaini.booklisting;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class BookViewModel extends ViewModel {
    public static MutableLiveData<List<Book>> books;
    public String sample;

    public LiveData<List<Book>> getBooks(String url){
        sample =url;
        if (books == null) {
            books = new MutableLiveData<>();
            if (url.equals(BookActivity.SAMPLE_URL)){
                return books;
            }
            loadBooks(sample);
        }
        return books;
    }

    public void loadBooks(String s) {
        sample =s;
        Thread t = new Thread(thread);
        t.start();
    }
    Thread thread = new Thread(new Runnable() {
    @Override
    public void run() {
        URL url = QueryUtility.createURL(sample);
        try{
            assert url != null;
            String JSONResponse = QueryUtility.ReadFromStream(QueryUtility.MakeHTTPRequest(url));
            books.postValue(QueryUtility.extractBooksFromJSON(JSONResponse));
        }
        catch (IOException | JSONException exception){

            exception.printStackTrace();
        }
    }
    });

}

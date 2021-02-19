package com.shvmsaini.booklisting;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class QueryUtility {
    private QueryUtility() {
    }
    public static URL createURL(String sampleURL) {
        URL url;
        try {
            url = new URL(sampleURL);
            return url;
        } catch (MalformedURLException e) {
            Log.d("", e.toString());
        }
        return null;
    }

    public static InputStream MakeHTTPRequest(URL url) throws IOException {
        InputStream inputStream = null;
        HttpsURLConnection  httpsURLConnection = (HttpsURLConnection) url.openConnection();
        httpsURLConnection.setDoInput(true);
        httpsURLConnection.setConnectTimeout(15000);
        httpsURLConnection.setReadTimeout(10000);
        httpsURLConnection.setRequestMethod("GET");
        httpsURLConnection.connect();
        if (httpsURLConnection.getResponseCode() == 200) {
            inputStream = httpsURLConnection.getInputStream();
        }
        return inputStream;

    }

    public static String ReadFromStream(InputStream inputStream) throws IOException {
        BufferedReader bufferedOutputStream = new BufferedReader(new InputStreamReader(inputStream, Charset.defaultCharset()));
        String line = bufferedOutputStream.readLine();
        StringBuilder stringBuilder = new StringBuilder();

        while (line != null) {
            stringBuilder.append(line);
            line = bufferedOutputStream.readLine();
        }
        return stringBuilder.toString();
    }

    public static List<Book> extractBooksFromJSON(String JSONresponse) throws JSONException, IOException {
        ArrayList<Book> books = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(JSONresponse);
        JSONArray jsonArray = jsonObject.getJSONArray("items");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject currentJSONObject = jsonArray.getJSONObject(i);
            String title = currentJSONObject.getJSONObject("volumeInfo").getString("title");
            String author;
            try {
                 author = currentJSONObject.getJSONObject("volumeInfo").getJSONArray("authors").getString(0);
                Log.d("IN","inside try");
            }
            catch (Exception e){
                Log.d("IN","inside catch");
                e.printStackTrace();
                    author = "NOT FOUND";

            }
//            String author = "currentJSONObject";
            String saleability = currentJSONObject.getJSONObject("saleInfo").getString("saleability");
            String price;
            if (saleability.equals("NOT_FOR_SALE")) {
                price = "NOT_FOR_SALE";
            } else {
                price = currentJSONObject.getJSONObject("saleInfo").getJSONObject("retailPrice").getString("amount");
                Locale locale = new Locale("en","IN");
                DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getCurrencyInstance(locale);
                DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance(locale);
                dfs.setCurrencySymbol("\u20B9");
                    decimalFormat.setDecimalFormatSymbols(dfs);
                price = decimalFormat.format(Float.parseFloat(price));
            }

           String imagelink = (currentJSONObject.getJSONObject("volumeInfo").getJSONObject("imageLinks").getString("thumbnail"));
//           URL imageURL = new URL("http://books.google.com/books/content?id=zYw3sYFtz9kC&printsec=frontcover&img=1&zoom=1&edge=curl&source=gbs_api");
            if (imagelink.charAt(4)!='s'){
                imagelink = "https://" + imagelink.substring(7);
            }
            URL imageURL = new URL(imagelink);
            Log.d("URL",imageURL.toString());

            InputStream imageStream = MakeHTTPRequest(imageURL);
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
//            Bitmap bitmap = BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.ic_launcher_background);


            books.add(new Book(title, author, price,bitmap));

        }
        return books;
    }

}

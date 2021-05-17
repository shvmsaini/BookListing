package com.shvmsaini.booklisting;

import android.content.res.Resources;
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
           e.printStackTrace();
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
        JSONArray jsonArray;
        try{
            jsonArray = jsonObject.getJSONArray("items");
        }catch (JSONException e){
            Log.d("INSIDE","IOgfU");
            return books;
        }

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject currentJSONObject = jsonArray.getJSONObject(i);
            String title = currentJSONObject.getJSONObject("volumeInfo").getString("title");
            String author;
            try {
                author = currentJSONObject.getJSONObject("volumeInfo").getJSONArray("authors").getString(0);
            }
            catch (Exception e){
                e.printStackTrace();
                author = "NOT FOUND";

            }
            String price;
            String saleability = currentJSONObject.getJSONObject("saleInfo").getString("saleability");
            if (saleability.equals("NOT_FOR_SALE")) {
                price = "NOT_FOR_SALE";
            }
            else if((saleability.equals("FREE"))) {
                price = "FREE";
            }
            else{
                price = currentJSONObject.getJSONObject("saleInfo").getJSONObject("retailPrice").getString("amount");
                Locale locale = new Locale("en","IN");
                DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getCurrencyInstance(locale);
                DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance(locale);
                dfs.setCurrencySymbol("\u20B9");
                    decimalFormat.setDecimalFormatSymbols(dfs);
                price = decimalFormat.format(Float.parseFloat(price));
            }
//            String selfLink = jsonObject.getString("selfLink");
            URL selfLink;
            try {
                selfLink = new URL(currentJSONObject.getString("selfLink"));
            }catch (Exception e){
                selfLink = new URL("https://www.google.com");
            }
            Log.d("INSIDE",selfLink.toString());
            String imageLink;
            try {
               imageLink = (currentJSONObject.getJSONObject("volumeInfo").getJSONObject("imageLinks").getString("thumbnail"));
           }
           catch (JSONException jsonException){
               Bitmap ic = BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.ic_baseline_image_not_supported_24);
               books.add(new Book(title,author,price,ic,selfLink));
                continue;
           }
            if (imageLink.charAt(4)!='s'){
                imageLink = "https://" + imageLink.substring(7);

            }
            URL imageURL = new URL(imageLink);
            InputStream imageStream = MakeHTTPRequest(imageURL);
            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
            books.add(new Book(title, author, price, bitmap,selfLink));

        }
        return books;
    }
    public static Book extractBookDetailFromJSON(String JSONresponse) throws JSONException, IOException {
        Book book;
        JSONObject jsonObject = new JSONObject(JSONresponse);
        String pageCount;
        try {
            pageCount = jsonObject.getJSONObject("volumeInfo").getString("pageCount");
        }catch(Exception e){
            pageCount = "";
        }
        String description;
        try {
            description = jsonObject.getJSONObject("volumeInfo").getString("description");
        }catch(Exception e){
            description = "";
        }
        String publishingDate;
        try {
            publishingDate = jsonObject.getJSONObject("volumeInfo").getString("publishedDate");
        }catch(Exception e){
            publishingDate = "";
        }
        String buyLink;
        try {
            buyLink = jsonObject.getJSONObject("saleInfo").getString("buyLink");
        }catch(Exception e){
            buyLink = "";
        }
        String pdfLink;
        try {
            pdfLink = jsonObject.getJSONObject("accessInfo").getJSONObject("pdf").getString("downloadLink");
        }catch(Exception e){
            pdfLink = "";
        }
        book = new Book(description,buyLink,publishingDate,pageCount,pdfLink);
        return book;
    }

}

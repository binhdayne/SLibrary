package com.qlthuvien.api;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import com.qlthuvien.model.BookFromAPI;
import org.json.JSONArray;
import org.json.JSONObject;

public class GoogleBooksAPI {

    public static BookFromAPI fetchBookByISBN(String isbn) throws IOException {
        String urlString = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + isbn;
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        // Kiểm tra mã phản hồi
        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        }

        // Đọc phản hồi từ API
        StringBuilder inline = new StringBuilder();
        Scanner scanner = new Scanner(url.openStream());
        while (scanner.hasNext()) {
            inline.append(scanner.nextLine());
        }
        scanner.close();

        // Parse JSON
        JSONObject dataObj = new JSONObject(inline.toString());
        JSONArray itemsArray = dataObj.optJSONArray("items");
        if (itemsArray != null && itemsArray.length() > 0) {
            JSONObject volumeInfo = itemsArray.getJSONObject(0).getJSONObject("volumeInfo");

            String title = volumeInfo.getString("title");
            String author = volumeInfo.optJSONArray("authors") != null ? volumeInfo.getJSONArray("authors").getString(0) : "Unknown Author";
            String publisher = volumeInfo.optString("publisher", "Unknown Publisher");
            String publishedDate = volumeInfo.optString("publishedDate", "Unknown Date");
            String description = volumeInfo.optString("description", "No description available");

            // Trả về BookFromAPI với status mặc định là "available"
            return new BookFromAPI(0, title, author, "available", isbn, publisher, publishedDate, description);
        }
        return null;
    }
}


package com.rabbiajahangir.android.quizr;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by Jahangir on 11/23/2015.
 */
public class Server {
    private static final String TAG = "Server";
    static final String LOGIN_URL = "http://192.168.56.1:3000/login";
    static final String SIGNUP_URL = "http://192.168.56.1:3000/signup";
    static final String AUTH_URL = "http://192.168.56.1:3000/authenticate";

    public static String postRequest(String email, String password, String link) {
        String charset = "UTF-8";
        String query = null;
        URL url = null;
        HttpURLConnection connection = null;
        try {
            query =
                    String.format("email=%s&password=%s", URLEncoder.encode(email, charset), URLEncoder.encode(password, charset));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        try {
            url = new URL(link);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
            connection = (HttpURLConnection) url.openConnection();
            connection.setChunkedStreamingMode(0);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
            connection.setInstanceFollowRedirects(true);
            HttpURLConnection.setFollowRedirects(true);
            connection.setDoOutput(true); // Triggers POST.
            OutputStream output = connection.getOutputStream();
            output.write(query.getBytes(charset));
            String jsonReply;
            if (connection.getResponseCode() == 201 || connection.getResponseCode() == 200) {
                InputStream response = connection.getInputStream();
                jsonReply = convertStreamToString(response);
                response.close();
                return jsonReply;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean postLogin(String email, String password) {
        String reply = postRequest(email, password, LOGIN_URL);
        boolean status = false;
        if (reply != null) {
            Log.d(TAG, reply);
            try {
                JSONObject jsonReply = new JSONObject(reply);
                if (jsonReply.has("loggedIn")) {
                    String content = jsonReply.getString("loggedIn");
                    if (content.equals("true")) {
                        status = true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return status;
    }

    public boolean postSignup(String email, String password) {
        String reply = postRequest(email, password, SIGNUP_URL);
        boolean status = false;
        if (reply != null) {
            Log.d(TAG, reply);
            try {
                JSONObject jsonReply = new JSONObject(reply);
                if (jsonReply.has("signedUp")) {
                    String content = jsonReply.getString("signedUp");
                    if (content.equals("true")) {
                        status = true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return status;
    }

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;

        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

//    public boolean authenticate() {
//
//        URL url = null;
//        try {
//            url = new URL(Server.AUTH_URL);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        HttpURLConnection conn = null;
//        try {
//            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
//            conn = (HttpURLConnection) url.openConnection();
//            conn.setDoOutput(true);
//            InputStream in = conn.getInputStream();
//            String reply = convertStreamToString(in);
//            Log.d(TAG, reply);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return false;
//
//    }

}



package com.rabbiajahangir.android.quizr;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by Jahangir on 11/23/2015.
 */
public class Server {
    private static Context context;

    public Server(Context c) {
        this.context = c;
    }

    //    To connect the app via genymotion, use : 192.168.56.1
    private static final String TAG = "Server";
    private static final String SERVER_ROOT = "http://10.65.7.250:3000";
    static final String LOGIN_URL = SERVER_ROOT + "/login";
    static final String SIGNUP_URL = SERVER_ROOT + "/signup";
    static final String AUTHENT_URL = SERVER_ROOT + "/authenticate";

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
//            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setChunkedStreamingMode(0);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
            connection.setInstanceFollowRedirects(false);
            HttpURLConnection.setFollowRedirects(true);
            connection.setDoOutput(true); // Triggers POST.
            OutputStream output = connection.getOutputStream();
            output.write(query.getBytes(charset));
            String jsonReply;
            if (connection.getResponseCode() == 201 || connection.getResponseCode() == 200) {
                InputStream response = connection.getInputStream();
                jsonReply = convertStreamToString(response);
                response.close();
                connection.disconnect();
                List<HttpCookie> list = cookieManager.getCookieStore().getCookies();
                System.out.println("The length of cookie is: " + list.size());
                for (int i = 0; i < list.size(); i++) {
                    HttpCookie cookie = list.get(i);
                    if (cookie.getName().equals("connect.sid")) {
                        PersistentCookiestore.saveValue(context, cookie.getValue());
                        break;
                    }
                }
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

    public String getRequest() {

        URL url = null;
        try {
            url = new URL(Server.AUTHENT_URL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection conn = null;
        try {
//            CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
            CookieManager cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            conn = (HttpURLConnection) url.openConnection();
            System.out.println("Sending the cookies: " + PersistentCookiestore.getValue(context));
            conn.setRequestProperty("Cookie", "connect.sid=" + PersistentCookiestore.getValue(context));
            conn.connect();


//            conn.setDoOutput(true);
            Log.d(TAG, "Request sent");
            InputStream response;
            String jsonReply;
            if (conn.getResponseCode() == 201 || conn.getResponseCode() == 200) {
                response = conn.getInputStream();
                jsonReply = convertStreamToString(response);
                List<HttpCookie> list = cookieManager.getCookieStore().getCookies();
                System.out.println("The length of cookie is: " + list.size());
                for (int i = 0; i < list.size(); i++) {
                    HttpCookie cookie = list.get(i);
                    if (cookie.getName().equals("connect.sid")) {
                        PersistentCookiestore.saveValue(context, cookie.getValue());
                        break;
                    }
                }
                response.close();
                conn.disconnect();
                return jsonReply;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public boolean authenticate() {
        String reply = getRequest();
        boolean status = false;
        if (reply != null) {
            Log.d(TAG, reply);
            try {
                JSONObject jsonReply = new JSONObject(reply);
                if (jsonReply.has("authenticated")) {
                    String content = jsonReply.getString("authenticated");
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

}



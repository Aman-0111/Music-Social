package com.example.music_social;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaSync;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SpotService {

    //Used for connecting to the API and storing the users info
    private static final String ENDPOINT = "https://api.spotify.com/v1/me";
    private SharedPreferences sPref;
    private RequestQueue queue;
    private SpotUser user;

    //Sets request queue and sharedpreferences
    public SpotService(RequestQueue queue, SharedPreferences sharedPreferences) {
        this.queue = queue;
        this.sPref = sharedPreferences;
    }

    //Returns User
    public SpotUser getUser() {
        return user;
    }

    //Sets User details from API
    public void get(final VolleyCallBack callBack) {
        //Creates JSON request
        JsonObjectRequest request = new JsonObjectRequest(ENDPOINT, null, response -> {
            //Uses GSON to map JSON object to SpotUser object
            Gson gson = new Gson();
            user = gson.fromJson(response.toString(), SpotUser.class);
            callBack.onSuccess();
        }, error -> get(() -> {
            //Handles Errors
        })) {
            //Maps authentication token to JSON request
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sPref.getString("token", "");
                String auth = "Bearer " + token;
                headers.put("Authorization", auth);
                return headers;
            }
        };
        //Adds request to queue
        queue.add(request);
    }

}

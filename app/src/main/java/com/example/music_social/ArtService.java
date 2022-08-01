package com.example.music_social;

import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

//Provides the link between the JSON and the artists
public class ArtService {

    //Used for connecting to api ans storing info
    private String endpoint;
    private SharedPreferences sPref;
    private RequestQueue queue;
    private Artist artist;

    //Constructor for class
    public ArtService(RequestQueue queue, SharedPreferences sharedPreferences, String endpoint) {
        this.queue = queue;
        this.sPref = sharedPreferences;
        this.endpoint = endpoint;

    }

    //Returns artist object
    public Artist getArtist() {
        return artist;
    }


    public void get(final VolleyCallBack callBack) {
        //Requests JSON object
        JsonObjectRequest request = new JsonObjectRequest(endpoint, null, response -> {
            //Maps JSON object onto Artist Object
            Gson mapper = new Gson();
            artist = mapper.fromJson(response.toString(), Artist.class);
            callBack.onSuccess();
            //Error handler
        }, error -> get(() -> {

        })) {
            //Maps authentication token to JSON request
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sPref.getString("token", "");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        //Adds request to request queue
        queue.add(request);
    }
}

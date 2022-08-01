package com.example.music_social;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TrackService {

    //Used for connecting to the API and storing the song info
    private SharedPreferences sPref;
    private RequestQueue queue;
    private String endpoint = "https://api.spotify.com/v1/me/player/recently-played";
    private ArrayList<Track> recentlyPlayed = new ArrayList<>();

    //Sets request queue and sharedpreferences
    public TrackService(Context context){
        sPref = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);
    }

    //Returns ArrayList of Songs
    public ArrayList<Track> getRecentlyPlayed() {
        return recentlyPlayed;
    }

    //Gets recently played songs
    public void getRecentTracks(final VolleyCallBack callBack) {
        //Creates JSON request
        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.GET, endpoint, null, response -> {
                    Gson gson = new Gson();
                    JSONArray songs = response.optJSONArray("items");
                    //Maps JSON Track objects to Track class using GSON
                    for (int n = 0; n < songs.length(); n++) {
                        try {
                            JSONObject track = songs.getJSONObject(n);
                            track = track.optJSONObject("track");
                            Track song = gson.fromJson(track.toString(), Track.class);
                            recentlyPlayed.add(song);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    callBack.onSuccess();
                }, error -> {
                    //Error Handler
                }) {
            //Maps authentication token to JSON request
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String token = sPref.getString("token", "");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        //Adds request to Queue
        queue.add(request);
    }

}

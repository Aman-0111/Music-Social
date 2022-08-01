package com.example.music_social;


import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Recommendation {

    //Holds Artists API links
    private ArrayList<String> artists = new ArrayList<>();
    //Holds info about API
    private SharedPreferences sPref;
    private RequestQueue queue;

    public Recommendation(Context context){
        sPref = context.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(context);
    }

    //Returns API links
    public ArrayList<String> getArtists() {
        return artists;
    }

    //Gets Recommended Artists and stores the API links in the ArrayLists
    public void getRecommendedArtists(final VolleyCallBack callBack) {
        //Sets endpoint so random artists are obtained
        String endpoint = "https://api.spotify.com/v1/recommendations?seed_artists=4NHQUGzhtTLFvgF5SZesLK&seed_genres=rock%2Crap%2Cpop%20music&seed_tracks=0c6xIDDpzE81m2q797ordA";
        JsonObjectRequest request = new JsonObjectRequest
                (Request.Method.GET, endpoint, null, response -> {
                    //Loops through API JSON until artists arrays are obtained
                    JSONArray jsonTracks = response.optJSONArray("tracks");
                    for (int n = 0; n < 4; n++) {
                        try {
                            JSONObject track = jsonTracks.getJSONObject(n);
                            for (int m = 0; m < track.length(); m++){
                                JSONArray allArtists = track.getJSONArray("artists");
                                //Once Array of artists is obtained all artist links are added to ArrayList
                                for (int i = 0; i < allArtists.length(); i++){
                                    JSONObject artist = allArtists.getJSONObject(i);
                                    String url  = artist.getString("href");
                                    artists.add(url);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    callBack.onSuccess();
                    //Error handler
                }, error -> {

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
        //Adds request to queue
        queue.add(request);
    }

}

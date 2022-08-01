package com.example.music_social;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class HomeFragment extends Fragment implements View.OnClickListener {

    //Holds info about authentication
    private SharedPreferences sPref;
    private RequestQueue queue;

    //All xml views
    private ImageView artist1;
    private ImageView artist2;
    private ImageView artist3;
    private ImageView artist4;
    public Button displayArtists;
    public TextView info;

    //Class to obtain recommendations
    private Recommendation recom;

    //holds info about recommended artists
    private ArrayList<String> links_artists = new ArrayList<>();
    private ArrayList<Artist> artists = new ArrayList<>();
    private ArrayList<String> spoti_links = new ArrayList<>();


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Sets view
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        //Sets button and delays its appearance
        displayArtists = (Button) v.findViewById(R.id.showArtists);
        displayArtists .setVisibility(View.INVISIBLE);
        displayArtists .postDelayed(new Runnable() {
            public void run() {
                displayArtists .setVisibility(View.VISIBLE);
            }
        }, 1500);

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        //Sets all artist imageview values
        artist1 = (ImageView) requireView().findViewById(R.id.artist1);
        artist2 = (ImageView) requireView().findViewById(R.id.artist2);
        artist3 = (ImageView) requireView().findViewById(R.id.artist3);
        artist4 = (ImageView) requireView().findViewById(R.id.artist4);

        //Sets username Textview
        info = (TextView) getView().findViewById(R.id.viewProfile);

        //Sets all onclick listener
        displayArtists.setOnClickListener(this);
        artist1.setOnClickListener(this);
        artist2.setOnClickListener(this);
        artist3.setOnClickListener(this);
        artist4.setOnClickListener(this);

        //Sets sharedpreferences and request queue
        sPref = getContext().getSharedPreferences("SPOTIFY",0);
        queue = Volley.newRequestQueue(getContext());

        //Sets user details to textview and imageview
        ImageView img = (ImageView) requireView().findViewById(R.id.pic);
        TextView uname = (TextView) requireView().findViewById(R.id.username);
        uname.setText(sPref.getString("uname",""));
        String url = sPref.getString("prof","");
        setImage(img,url);

        //Sets recommendation class and obtains links
        recom = new Recommendation(getContext());
        getLinks();

    }

    //Gets Artists API links and stores in an ArrayList
    private void getLinks() {
        recom.getRecommendedArtists(() -> {
            links_artists = recom.getArtists();
            getArtists();
        });
    }

    //Gets all artists details and stores in an ArrayList
    private void getArtists(){
        for (int n = 0 ;n< links_artists.size();n++){
            ArtService artService = new ArtService(queue, sPref, links_artists.get(n));
            artService.get(()  -> {
                Artist artist = artService.getArtist();
                artists.add(artist);
            });
        }
    }

    //Sets an ArrayList of artists Spotify Urls
    public void getUrls() {
        //Removes repeated names
        ArrayList <String> names = filterArtists();

        //Puts all image views in an ArrayList
        ArrayList<ImageView> images = new ArrayList<>();
        images.add(artist1);
        images.add(artist2);
        images.add(artist3);
        images.add(artist4);

        //Sets all imageViews to Artist images and stores corresponding URL links in an ArrayList
        for (int x = 0; x < 4;x++){
            for (int y = 0; y < artists.size();y++){
                if (names.contains(artists.get(y).name)){
                    String url = artists.get(y).images[0].url;
                    spoti_links.add(artists.get(y).external_urls.spotify);
                    setImage(images.get(x),url);
                    names.remove(artists.get(y).name);
                    break;
                }
            }
        }
    }

    //Filters out all duplicate artist names
    private ArrayList<String> filterArtists() {
        ArrayList<String> temp = new ArrayList<>();
        temp.add(artists.get(0).name);
        for (int x = 1; x < artists.size();x++){
            if (!temp.contains(artists.get(x).name)){
                temp.add(artists.get(x).name);
            }
        }
        return temp;
    }

    //Sets imageview with an image via URL
    public void setImage(ImageView img, String url){
        Picasso.get().load(url).resize(350,350).into(img);
    }

    //Redirects to artists Spotify page
    public void redirectToSpotify(String url){
        Intent intent = new Intent("android.intent.action.VIEW",
                        Uri.parse(url));
        startActivity(intent);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //Displays Artists images and removes Display Button
            case R.id.showArtists:
                getUrls();
                displayArtists.setClickable(false);
                displayArtists.setVisibility(View.GONE);
                info.setVisibility(View.VISIBLE);
                break;
                //Redirects user to artists spotify page based on image tapped
            case R.id.artist1:
                redirectToSpotify(spoti_links.get(0));
                break;
            case R.id.artist2:
                redirectToSpotify(spoti_links.get(1));
                break;
            case R.id.artist3:
                redirectToSpotify(spoti_links.get(2));
                break;
            case R.id.artist4:
                redirectToSpotify(spoti_links.get(3));
                break;
        }
    }
}
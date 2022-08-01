package com.example.music_social;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.ArrayList;


public class RecentFragment extends Fragment implements View.OnClickListener {

    //Obtains users recently played songs
    private TrackService trackService;

    //Sets all views
    public TextView info;
    public Button displayTracks;
    public Button trackButton1;
    public Button trackButton2;
    public Button trackButton3;
    public Button trackButton4;
    public Button trackButton5;
    public Button trackButton6;
    public Button trackButton7;

    //Used for playing Songs
    private SpotifyAppRemote mSpotifyAppRemote;
    private String CLIENT_ID = "989d269d67714732a66797a6416f4534";
    private String REDIRECT_URI = "http://com.example.music-social/callback";

    //ArrayLists for recently played songs, external links and internal links
    private ArrayList<Track> recentlyPlayed = new ArrayList<>();
    private ArrayList<String> Uris = new ArrayList<>();
    private ArrayList<String> Urls = new ArrayList<>();


    public RecentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Sets view
        View v = inflater.inflate(R.layout.fragment_recent, container, false);

        //Checks build version of user
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //Sets Notification channel if user has a build version greater than oreo
            NotificationChannel channel = new NotificationChannel("Song Player", "Song Player", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getActivity().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        //Sets link between recently played tracks and api
        trackService = new TrackService(getContext());

        //Sets display button and delays visibility
        displayTracks = (Button) v.findViewById(R.id.showTracks);
        displayTracks .postDelayed(new Runnable() {
            public void run() {
                displayTracks .setVisibility(View.VISIBLE);
            }
        }, 1500);

        return v;
    }


    @Override
    public void onStart() {
        super.onStart();

        //Gets Recently played tracks
        getTracks();

        //Sets all views
        info = (TextView) getView().findViewById(R.id.playTracks);
        trackButton1 = (Button) getView().findViewById(R.id.track1);
        trackButton2 = (Button) getView().findViewById(R.id.track2);
        trackButton3 = (Button) getView().findViewById(R.id.track3);
        trackButton4 = (Button) getView().findViewById(R.id.track4);
        trackButton5 = (Button) getView().findViewById(R.id.track5);
        trackButton6 = (Button) getView().findViewById(R.id.track6);
        trackButton7 = (Button) getView().findViewById(R.id.track7);

        //Sets all onclick listeners
        displayTracks.setOnClickListener(this);
        trackButton1.setOnClickListener(this);
        trackButton2.setOnClickListener(this);
        trackButton3.setOnClickListener(this);
        trackButton4.setOnClickListener(this);
        trackButton5.setOnClickListener(this);
        trackButton6.setOnClickListener(this);
        trackButton7.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //Display Recently played tracks and textview
            case R.id.showTracks:
                setButtons();
                displayTracks.setVisibility(View.GONE);
                info.setVisibility(View.VISIBLE);
                break;
                //Attempts to connect song player when button is clicked
            case R.id.track1:
                connectPlayer(0);
                break;
            case R.id.track2:
                connectPlayer(1);
                break;
            case R.id.track3:
                connectPlayer(2);
                break;
            case R.id.track4:
                connectPlayer(3);
                break;
            case R.id.track5:
                connectPlayer(4);
                break;
            case R.id.track6:
                connectPlayer(5);
                break;
            case R.id.track7:
                connectPlayer(6);
                break;
        }
    }

    //Gets ArrayList of recently played songs
    private void getTracks() {
        trackService.getRecentTracks(() -> {
            recentlyPlayed = trackService.getRecentlyPlayed();
        });
    }

    //Connects to spotify app music player
    private void connectPlayer(int i){

        //Gets connection parameters
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        //Attempts to Connect to Spotify player and calls playTrack
        SpotifyAppRemote.connect(getContext(), connectionParams,
                new Connector.ConnectionListener() {
                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        playTrack(Uris.get(i), Urls.get(i));
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        playTrack(Uris.get(i), Urls.get(i));
                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });

    }

    //Attempts to play song
    private void playTrack(String uri, String url) {
        try{
            //Plays track if song player connection was established and sends notification
            createNotification("Song Is Now Playing");
            mSpotifyAppRemote.getPlayerApi().play(uri);
        }catch (Exception e) {
            //Redirects user to Spotify song link otherwise and sends notification
            createNotification("Song Page Is Now Open");
            Intent intent = new Intent("android.intent.action.VIEW",
                    Uri.parse(url));
            startActivity(intent);
        }
    }

    //Creates notification with passed message
    private void createNotification(String Message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "Song Player");
        builder.setContentTitle(Message);
        builder.setSmallIcon(R.drawable.icon);
        builder.setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getContext());
        managerCompat.notify(1,builder.build());

    }

    //Sets Button text
    private void setButtons(){
        //Adds all buttons to a list
        ArrayList<Button> trackButtons = new ArrayList<>();
        trackButtons.add(trackButton1);
        trackButtons.add(trackButton2);
        trackButtons.add(trackButton3);
        trackButtons.add(trackButton4);
        trackButtons.add(trackButton5);
        trackButtons.add(trackButton6);
        trackButtons.add(trackButton7);

        //Loops through buttons and writes the recently played track names to the buttons
        //Adds corresponding Urls and Uris to ArrayLists
        for (int x = 0; x < trackButtons.size(); x++){
            for (int y = 0; y < recentlyPlayed.size();y++){
                if (!recentlyPlayed.get(y).is_local){
                    trackButtons.get(x).setText(recentlyPlayed.get(y).name);
                    trackButtons.get(x).setVisibility(View.VISIBLE);
                    Uris.add(recentlyPlayed.get(y).uri);
                    Urls.add(recentlyPlayed.get(y).external_urls.spotify);
                    recentlyPlayed.remove(y);
                    break;
                }
            }
        }
    }
}
package com.example.music_social;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

//Authorises spotify account connection
public class Authorization extends AppCompatActivity {

    //Users Spotify information Stored
    private SpotUser user;

    //Holds info about authentication
    private SharedPreferences.Editor editor;
    private SharedPreferences sPref;
    private RequestQueue queue;

    //Used to verify connect and set restrictions
    private static final int REQUEST_CODE = 5634;
    private static final String CLIENT_ID = "989d269d67714732a66797a6416f4534";
    private static final String REDIRECT_URI = "http://com.example.music-social/callback";
    private static final String SCOPES = "user-read-recently-played,user-library-modify,user-read-email,user-read-private";

    //Starts authenticator and sets sharedpreferences and request queue
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        authenticator();

        sPref = this.getSharedPreferences("SPOTIFY", 0);
        queue = Volley.newRequestQueue(this);

    }

    //Authenticates Spotify Connection
    private void authenticator(){
        AuthorizationRequest.Builder builder =
                new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{SCOPES});
        builder.setShowDialog(true);
        AuthorizationRequest request = builder.build();

        AuthorizationClient.openLoginActivity(this, REQUEST_CODE,request);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);

        //Validates connection
        if (requestCode == REQUEST_CODE){
            //Gets response and checks type
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);

            switch (response.getType()) {

                case TOKEN:
                    // Handle successful response
                    editor = getSharedPreferences("SPOTIFY", 0).edit();
                    editor.putString("token", response.getAccessToken());
                    editor.apply();
                    connected();
                    break;


                // Auth flow returned an error
                case ERROR:
                    break;
                // Most likely auth flow was cancelled
                default:
                    break;

            }
        }

    }

    //Obtains user spotify info and stores it in shared preferences
    private void connected() {
        SpotService userService = new SpotService(queue, sPref);
        userService.get(() -> {
            user = userService.getUser();
            editor = getSharedPreferences("SPOTIFY", 0).edit();
            editor.putString("userid", user.id);
            editor.putString("prof",user.images[0].url);
            editor.putString("uname",user.display_name);
            editor.putString("url",user.external_urls.spotify);
            // We use commit instead of apply because we need the information stored immediately
            editor.commit();
            login();
        });
    }

    //Starts next activity
    private void login(){
        Intent intent = new Intent(Authorization.this, Container.class);
        startActivity(intent);
    }
}
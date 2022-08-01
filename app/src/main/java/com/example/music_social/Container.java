package com.example.music_social;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ShareActionProvider;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.music_social.databinding.ActivityOpenBinding;


//Container activity for fragments
public class Container extends AppCompatActivity {

    //Binds Fragments
    private ActivityOpenBinding binding;

    //Used to share users Spotify profile
    private ShareActionProvider shareprovider;

    //Holds user profile
    private SharedPreferences sPref;

    //Used to detect swipes
    private GestureDetectorCompat gestDetect;

    //Fragments for navigation bar
    private Fragment home = new HomeFragment();
    private Fragment recent = new RecentFragment();
    private Fragment setting = new SettingFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Binding sets view to hold fragments
        binding = ActivityOpenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Starts default fragment
        setFrag(1,1);

        //Displays different fragment based on navbar
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home_nav:
                    setFrag(1,2);
                    break;
                case R.id.recent_nav:
                    setFrag(2,2);
                    break;
                case R.id.setting_nav:
                    setFrag(3,2);
                    break;
            }
            return true;
        });

        //Sets sharedPreferences
        sPref = getSharedPreferences("SPOTIFY",0);

        //Sets gesture detector
        gestDetect = new GestureDetectorCompat(this , new GestureListener());

    }

    //Creates ShareActionViewProvider
    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.share, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        shareprovider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        setShareActionIntent();
        return super.onCreateOptionsMenu(menu);
    }

    //Sets the share feature to share the users Spotify URL
    private void setShareActionIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,"Check out my Spotify Account \n"+sPref.getString("url",""));
        shareprovider.setShareIntent (intent);
    }


    //Displays Fragments
    private void setFrag(int i, int n){
        //Starts Fragment manager and transaction
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        //Adds fragments to transaction if function is called for first time
        if (n ==1) {
            ft.add(R.id.frame_layout, home)
                    .add(R.id.frame_layout, recent)
                    .add(R.id.frame_layout, setting);
        }

        //Shows and hides fragments based on navbar press
        //Prevents slowdown due to API calls unlike replace
        if (i == 1){
            ft.show(home).hide(recent).hide(setting);
        } else if (i == 2){
            ft.hide(home).show(recent).hide(setting);
        } else {
            ft.hide(home).hide(recent).show(setting);
        }
        ft.commit();
    }

    //Reads gestures
    private class GestureListener extends GestureDetector.SimpleOnGestureListener{

        //Displays fragments based on swipe direction
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (velocityX < 0){
                if (home.isVisible()){
                    setFrag(2,2);
                } else if(recent.isVisible()){
                    setFrag(3,2);
                }
            }
            if (velocityX > 0){
                if (setting.isVisible()){
                    setFrag(2,2);
                } else if(recent.isVisible()){
                    setFrag(1,2);
                }
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    //Checks for swipe whenever a touch event occurs
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestDetect.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

}
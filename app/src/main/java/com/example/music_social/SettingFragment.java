package com.example.music_social;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.fragment.app.Fragment;

//Fragment for settings
public class SettingFragment extends Fragment implements View.OnClickListener {

    //All views
    private Button exitButton;
    private Button guideButton;
    private Button exitWeb;
    private WebView userGuide;

    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }


    @Override
    public void onStart() {
        super.onStart();

        //Sets all Views
        exitButton = (Button) getView().findViewById(R.id.exitApp);
        guideButton = (Button) getView().findViewById(R.id.guideButton);
        exitWeb = (Button) getView().findViewById(R.id.exitWeb);

        //Sets onclick listeners
        exitButton.setOnClickListener(this);
        guideButton.setOnClickListener(this);
        exitWeb.setOnClickListener(this);

        //Sets web view attributes
        userGuide = (WebView) getView().findViewById(R.id.webView);
        WebSettings settings = userGuide.getSettings();
        settings.setBuiltInZoomControls(true);
        settings.setJavaScriptEnabled(true);
        userGuide.setWebViewClient(new Callback());
        userGuide.loadUrl("file:///android_asset/UserGuide.html");
    }

    //Prevents the URL from being overridden
    private class Callback extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return false;
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            //Exits app on button click
            case R.id.exitApp:
                System.exit(0);
                break;
                //Changes visibility to show web view
            case R.id.guideButton:
                userGuide.setVisibility(View.VISIBLE);
                exitWeb.setVisibility(View.VISIBLE);
                guideButton.setVisibility(View.INVISIBLE);
                exitButton.setVisibility(View.INVISIBLE);
                break;
            //Changes visibility to disable web view
            case R.id.exitWeb:
                userGuide.setVisibility(View.INVISIBLE);
                exitWeb.setVisibility(View.INVISIBLE);
                guideButton.setVisibility(View.VISIBLE);
                exitButton.setVisibility(View.VISIBLE);
                break;
        }
    }
}
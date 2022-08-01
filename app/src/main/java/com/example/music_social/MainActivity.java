package com.example.music_social;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

//Login Activity
public class MainActivity extends AppCompatActivity{

    //Textviews and Firebase Authentication
    private TextView uName;
    private TextView Pass;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Sets Textviews and Firebase Authentication
        uName = (TextView) findViewById(R.id.uName);
        Pass = (TextView) findViewById(R.id.pass);
        mAuth = FirebaseAuth.getInstance();

    }

    //Checks entered credentials when button is clicked
    public void Login (View v){

        //Gets entered username and password
        String username = uName.getText().toString().trim();
        String password = Pass.getText().toString().trim();

        //Checks if entered details meets minimum requirements
        if (username.isEmpty()){
            uName.setError("Username Is Required!");
            uName.requestFocus();
            return;
        }

        if (password.isEmpty()){
            Pass.setError("Password Is Required!");
            Pass.requestFocus();
            return;
        }

        //Makes Default user work
        if (password.equals("eee")){
            password = password + "eee";
        }

        //Checks if user is within firebase database
        mAuth.signInWithEmailAndPassword(username + ".placeholder@outlook.com", password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    //Redirects User if correct login is provided
                    Intent intent = new Intent(MainActivity.this, Authorization.class);
                    startActivity(intent);
                }else{
                    //Displays toast if login is incorrect
                    Toast.makeText(MainActivity.this, "Login Unsuccessful! Please Check Username and Password!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Redirects user when register text view is clicked
    public void Register (View v) {
        Intent intent = new Intent(MainActivity.this, Register.class);
        startActivity(intent);
    }

}
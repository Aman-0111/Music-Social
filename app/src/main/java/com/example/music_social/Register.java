package com.example.music_social;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    //EditText views and Firebase Authentication
    private FirebaseAuth mAuth;
    private EditText uName, Pass, rePass;

    //Sets EditText views and Firebase Authentication
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        uName = (EditText) findViewById(R.id.uName);
        Pass = (EditText) findViewById(R.id.pass);
        rePass = (EditText) findViewById(R.id.rePass);

    }

    //Returns to Login when button is clicked
    public void returnLog(View v){
        Intent intent = new Intent(Register.this, MainActivity.class);
        startActivity(intent);
    }

    //Registers User when button is clicked
    public void registerUser (View v){
        //Obtains User entered values
        String username = uName.getText().toString().trim();
        String password = Pass.getText().toString().trim();
        String repassword = rePass.getText().toString().trim();

        //Checks entries to see if they meet minimum requirements
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

        if (password.length() < 6){
            Pass.setError("Password Must Be At Least 6 Characters!");
            Pass.requestFocus();
            return;
        }

        if (repassword.isEmpty()){
            rePass.setError("Password Must Be Re-Entered!");
            rePass.requestFocus();
            return;
        }

        if (!(repassword.equals(password))) {
            rePass.setError("Re-enter Same Password!");
            rePass.requestFocus();
            return;
        }

        //Connects to Firebase
        mAuth.createUserWithEmailAndPassword(username + ".placeholder@outlook.com",password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Adds user to firebase
                        if (task.isSuccessful()){
                            User user = new User (username);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    //Displays toast based on outcome
                                    if (task.isSuccessful()){
                                        Toast.makeText(Register.this, "Registration Successful",Toast.LENGTH_SHORT).show();
                                        returnLog(v);
                                    } else{
                                        Toast.makeText(Register.this, "Registration Unsuccessful",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            //Displays toast based on outcome
                        } else{
                            Toast.makeText(Register.this, "Registration Unsuccessful",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
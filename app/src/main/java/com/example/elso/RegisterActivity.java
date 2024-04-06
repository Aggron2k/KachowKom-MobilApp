package com.example.elso;

import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 99;


    EditText usernameEditText;
    EditText userEmialEditText;
    EditText passwordEditText;
    EditText passwordReEditText;

    private SharedPreferences preferences;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Bundle bundle = getIntent().getExtras();
        //bundle.getInt("SECRET_KEY");
        int secret_key = getIntent().getIntExtra("SECRET_KEY",0);

        if (secret_key != 99){
            finish();
        }


        usernameEditText = findViewById(R.id.userNameEditText);
        userEmialEditText = findViewById(R.id.userEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        passwordReEditText = findViewById(R.id.editTextPasswordAgain);

        preferences = getSharedPreferences(PREF_KEY,MODE_PRIVATE);
        String userName = preferences.getString("userName", "");
        String password = preferences.getString("password", "");

        usernameEditText.setText(userName);
        passwordEditText.setText(password);
        passwordReEditText.setText(password);

        mAuth = FirebaseAuth.getInstance();

        Log.i(LOG_TAG, "onCreate");

    }

    public void regiszter(View view) {


        String userNameStr = usernameEditText.getText().toString();
        String userEmailStr = userEmialEditText.getText().toString();

        String passwordStr = passwordEditText.getText().toString();
        String passwordreStr = passwordReEditText.getText().toString();

        if (passwordStr.equals(passwordreStr)){
            Log.e(LOG_TAG,"Nem megfelelő a két jelszó nem ugyanaz");
        }

        Log.i(LOG_TAG, "Regisztrált: " + userNameStr + "  Email: "+ userEmailStr);

        mAuth.createUserWithEmailAndPassword(userEmailStr, passwordStr).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Log.d(LOG_TAG,"USER CREATED!");
                    startHomeActivity();
                }else{
                    Log.d(LOG_TAG, "USER CREATE ERROR!");
                    String errorMessage = task.getException().getMessage();
                    // AlertDialog megjelenítése a hibaüzenettel
                    new AlertDialog.Builder(RegisterActivity.this)
                            .setTitle("Regisztráció sikertelen")
                            .setMessage(errorMessage)
                            .setPositiveButton("OK", null)
                            .show();
                }
            }
        });
    }

    public void cancle(View view) {
        finish();
    }

    private void startHomeActivity(/*regiszter user data*/){
        Intent intent = new Intent(this, HomeActivity.class);
        //intent.putExtra("SECRET_KEY", SECRET_KEY);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
    }
}
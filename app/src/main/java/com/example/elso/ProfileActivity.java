package com.example.elso;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText newPasswordEditText;
    private EditText newPasswordreEditText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        Button updateBtn = findViewById(R.id.update_button);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newPasswordEditText = findViewById(R.id.password);
                newPasswordreEditText = findViewById(R.id.passwordre);
                if (newPasswordEditText.getText().toString().equals(newPasswordreEditText.getText().toString())){
                    updatePassword(newPasswordEditText.getText().toString());
                }else{
                    Toast.makeText(ProfileActivity.this, "Két jelszó nem egyezik!", Toast.LENGTH_LONG).show();
                }

            }
        });
        Button deleteBtn = findViewById(R.id.delete_button);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Biztos törölni akarod?")
                        .setMessage("A törlés visszavonhatatlan! :O")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseAuth.getInstance().signOut();
                                                Toast.makeText(ProfileActivity.this, "Felhasználó sikeresen törölve", Toast.LENGTH_SHORT).show();
                                                Intent backtohome =  new Intent(ProfileActivity.this, MainActivity.class);
                                                startActivity(backtohome);
                                            } else {
                                                Toast.makeText(ProfileActivity.this, "Felhasználó törlése sikertelen", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        })
                        .setNegativeButton("Mégse", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });
    }


    private void updatePassword(final String newPassword) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && !TextUtils.isEmpty(newPassword)) {
            user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "Jelszó frissítve!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Hiba lépett fel: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }



}

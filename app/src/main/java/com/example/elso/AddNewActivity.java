package com.example.elso;

import static androidx.core.content.PackageManagerCompat.LOG_TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.UUID;

public class AddNewActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private CollectionReference items;
    String userId;
    String userUid;

    String Id;
    EditText desc;
    EditText name;
    EditText price;
    EditText ratedInfo;
    Button addButton;

    public AddNewActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);

        // Button inicializálása
        addButton = findViewById(R.id.add_recipe_button);

        // Edittext-ek inicializálása
        name = findViewById(R.id.name);
        desc = findViewById(R.id.desc);
        price = findViewById(R.id.price);
        ratedInfo = findViewById(R.id.rate);


        // Firebase inicializálása
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();
        db = FirebaseFirestore.getInstance();
        items = db.collection("users");
        Id = UUID.randomUUID().toString();

        // Metódusok hívása
        getEmail();
        addCsomag();
    }

    public void getEmail(){
        Thread thread = new Thread(new Runnable() {
            @SuppressLint("RestrictedApi")
            @Override
            public void run() {
                items.whereEqualTo("id", userId).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                        User user = documentSnapshot.toObject(User.class);
                        userUid = user.getUid();
                        @SuppressLint("RestrictedApi") String userEmail = user.getUid();
                        Toast.makeText(AddNewActivity.this, "Felhasználó e-mail címe: " + userEmail, Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Log.e(LOG_TAG, "Hiba történt az e-mail cím lekérdezése során: " + e.getMessage());
                });
            }
        });
        thread.start();
    }



    public void addCsomag(){
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.getText().toString().isEmpty() || desc.getText().toString().isEmpty() || price.getText().toString().isEmpty() || ratedInfo.getText().toString().isEmpty()){
                    Toast.makeText(AddNewActivity.this, "Kérlek az összes mezőt töltsd ki!", Toast.LENGTH_SHORT).show();
                }else{
                    String mname = name.getText().toString();
                    String mdesc = desc.getText().toString();
                    String mprice = price.getText().toString() + " Ft";

                    // Float érték beállítása (a jelenlegi módszer helyett)
                    float mrate = Float.parseFloat(ratedInfo.getText().toString());

                    Log.d("Activity", userId);
                    Item itemek = new Item(mname,mdesc,mprice,mrate,2131230980);
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            db.collection("Items").document(Id).set(itemek).addOnSuccessListener(new OnSuccessListener<Void>() {

                                @SuppressLint("MissingPermission")
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(AddNewActivity.this, "Jelentkezz ki, meg be és látszodni fog! <3", Toast.LENGTH_SHORT).show();

                                    Log.d("Activity", "Sikeresen hozzáadva");

                                    AlertDialog.Builder builder = new AlertDialog.Builder(AddNewActivity.this);
                                    builder.setTitle("Sikeres hozzáadás!")
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {}
                                            })
                                            .show();



                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent returnIntent = new Intent();
                                            returnIntent.putExtra("result", "success");
                                            setResult(RESULT_OK, returnIntent);
                                            finish();
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("Activity", "Hiba történt");
                                }
                            });
                        }
                    });
                    thread.start();
                }
            }
        });
    }
}

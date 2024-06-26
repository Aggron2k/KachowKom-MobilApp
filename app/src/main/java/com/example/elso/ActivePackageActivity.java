package com.example.elso;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ActivePackageActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private ArrayList<Item> activePackagesList;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profillist);


        recyclerView = findViewById(R.id.recyclerViewActivated); // Átnevezve a RecyclerView id-je
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ActivePackageActivity.this));

        activePackagesList = new ArrayList<>();
        adapter = new ItemAdapter(this, activePackagesList);

        recyclerView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {

            loadActivePackages();
        }
    }


    //SZÁL: READ
    private void loadActivePackages() {
        // Háttérszál indítása
        new Thread(new Runnable() {
            @Override
            public void run() {
                String currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                firestore.collection("userPackages")
                        .document(currentUserUID)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                // A dokumentum létezik, hozzáadhatod az adatokat az activePackagesList-hez
                                                activePackagesList.add(new Item(
                                                        document.getString("name"),
                                                        document.getString("info"),
                                                        document.getString("price"),
                                                        document.getDouble("ratedInfo").floatValue(),
                                                        document.getLong("imageResource").intValue()
                                                ));
                                                adapter.notifyDataSetChanged();
                                            } else {
                                                // A dokumentum nem létezik
                                                Toast.makeText(ActivePackageActivity.this, "Nem található csomag a felhasználóhoz.", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            // Hiba történt a Firestore lekérdezés közben
                                            Toast.makeText(ActivePackageActivity.this, "Hiba történt a csomagok betöltése közben.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
            }
        }).start();
    }



}
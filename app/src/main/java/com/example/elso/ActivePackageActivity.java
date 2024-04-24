package com.example.elso;

import android.os.Bundle;
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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        activePackagesList = new ArrayList<>();
        adapter = new ItemAdapter(this, activePackagesList);

        recyclerView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {

            loadActivePackages();
        }
    }



    private void loadActivePackages() {
        firestore.collection("userPackages")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()){
                                activePackagesList
                                        .add(new Item(
                                                doc.getString("name"),
                                                doc.getString("info"),
                                                doc.getString("price"),
                                                doc.getDouble("ratedInfo").floatValue(),
                                                doc.getLong("imageResource").intValue()
                                        ));
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(ActivePackageActivity.this, "Hiba", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

}
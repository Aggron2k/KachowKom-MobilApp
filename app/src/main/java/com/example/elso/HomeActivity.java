package com.example.elso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    private static final String LOG_TAG = HomeActivity.class.getName();
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    private RecyclerView mRecyclerView;
    private ArrayList<Item> mItemsData;
    private ItemAdapter mAdapter;

    private int gridNumber = 1;
    private int cartItems = 0;
    private Integer itemLimit = 5;

    private FrameLayout redCricle ;
    private TextView contentTextView;

    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mAuth = FirebaseAuth.getInstance();
        //mAuth.signOut();

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(LOG_TAG, "AUTH user!");
        } else {
            Log.d(LOG_TAG, "NOOOAUTH user!");
            finish();
        }

        // recycle view
        mRecyclerView = findViewById(R.id.recyclerView);
        // Set the Layout Manager.
        mRecyclerView.setLayoutManager(new GridLayoutManager(
                this, gridNumber));
        // Initialize the ArrayList that will contain the data.
        mItemsData = new ArrayList<>();
        // Initialize the adapter and set it to the RecyclerView.
        mAdapter = new ItemAdapter(this, mItemsData);
        mRecyclerView.setAdapter(mAdapter);

        mFirestore = FirebaseFirestore.getInstance();
        mItems = mFirestore.collection("Items");
        initializeData();
    }
    private void initializeData() {
        // Get the resources from the XML file.
        String[] itemsList = getResources()
                .getStringArray(R.array.items_name);
        String[] itemsInfo = getResources()
                .getStringArray(R.array.item_desc);
        String[] itemsPrice = getResources()
                .getStringArray(R.array.item_price);
        TypedArray itemsImageResources =
                getResources().obtainTypedArray(R.array.item_images);
        TypedArray itemRate = getResources().obtainTypedArray(R.array.item_rates);

        // Create the ArrayList of Sports objects with the titles and
        // information about each sport.
        mItems.get().addOnCompleteListener(kollekson -> {
            if (kollekson.getResult().size() == 0) {
                for (int i = 0; i < itemsList.length; i++) {
                    mItems.add(new Item(
                            itemsList[i],
                            itemsInfo[i],
                            itemsPrice[i],
                            itemRate.getFloat(i, 0),
                            itemsImageResources.getResourceId(i, 0)));
                }
            }
            queryData();
        });

        // Recycle the typed array.
        //itemsImageResources.recycle();
    }
    private void queryData() {
        mItemsData.clear();
        mItems.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document:
                            task.getResult()) {
                        mItemsData.add(new Item(
                                document.getString("name"),
                                document.getString("info"),
                                document.getString("price"),
                                document.getDouble("ratedInfo").floatValue(),
                                document.getLong("imageResource").intValue()
                        ));
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.list_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);//SEARCHBAR
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        if (itemId == R.id.log_out_button) {
            Log.d(LOG_TAG, "Logout clicked!");
            FirebaseAuth.getInstance().signOut();
            finish();
            return true;
        } else if (itemId == R.id.settings_button) {
            Log.d(LOG_TAG, "Setting clicked!");
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setTitle("Beállítások")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {}
                    })
                    .show();
            return true;
        } else if (itemId == R.id.cart) {
            Log.d(LOG_TAG, "Cart clicked!");
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setTitle("CART")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {}
                    })
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem alertMenuItem = menu.findItem(R.id.cart);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

        redCricle = (FrameLayout) rootView.findViewById(R.id.view_alert_red_circle);
        contentTextView = (TextView) rootView.findViewById(R.id.view_alert_count_textview);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(alertMenuItem);
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    public void updateAlertIcon(){
        cartItems = (cartItems+1);
        if (0 < cartItems){
            contentTextView.setText(String.valueOf(cartItems));
        }else{
            contentTextView.setText("");
        }
    }
}
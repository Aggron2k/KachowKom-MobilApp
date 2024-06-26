package com.example.elso;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> implements Filterable {

    private static final String CHANNEL_ID = "my_channel_id";
    private static final int MY_NOTIFICATION_ID = 12345;

    private ArrayList<Item> mItemsData;
    private ArrayList<Item> mItemsDataAll;
    private Context mContext;
    private int lastPos = -1;
    private FirebaseFirestore mFirestore;

    ItemAdapter(Context context, ArrayList<Item> itemsData) {
        this.mItemsData = itemsData;
        this.mItemsDataAll = itemsData;
        this.mContext = context;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "KachowKom Értesítés";
            String description = "HALO! :O";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = mContext.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @NonNull
    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemAdapter.ViewHolder holder, int position) {
        Item currentItem = mItemsData.get(position);
        holder.bindTo(currentItem);

        if (holder.getAdapterPosition() > lastPos) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPos = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mItemsData.size();
    }

    @Override
    public Filter getFilter() {
        return getItemFilter;
    }

    private Filter getItemFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Item> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                results.count = mItemsDataAll.size();
                results.values = mItemsDataAll;
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Item item : mItemsDataAll) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mItemsData = (ArrayList) results.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitleText;
        private TextView mInfoText;
        private TextView mPriceText;
        private ImageView mItemImage;
        private RatingBar mRatingBar;
        private Button mActivateButton;
        private FirebaseFirestore mFirestore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mFirestore = FirebaseFirestore.getInstance();

            mTitleText = itemView.findViewById(R.id.itemTitle);
            mInfoText = itemView.findViewById(R.id.subTitle);
            mPriceText = itemView.findViewById(R.id.price);
            mItemImage = itemView.findViewById(R.id.itemImage);
            mRatingBar = itemView.findViewById(R.id.ratingBar);
            mActivateButton = itemView.findViewById(R.id.activate);

            mActivateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.anotheranim);
                    v.startAnimation(animation);
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Item currentItem = mItemsData.get(position);
                        String title = currentItem.getName();
                        String info = currentItem.getInfo();
                        String price = currentItem.getPrice();
                        float ratedInfo = currentItem.getRatedInfo();
                        int imageResource = currentItem.getImageResource();

                        if (mFirestore != null) {
                            String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            mFirestore.collection("userPackages").document(userUid).set(new Item(title, info, price, ratedInfo, imageResource))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(mContext, "Csomag sikeresen kiválasztva!", Toast.LENGTH_SHORT).show();

                                            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                                            } else {
                                                NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext, CHANNEL_ID)
                                                        .setSmallIcon(R.drawable.mc)
                                                        .setContentTitle("Sikeres hozzáadás!")
                                                        .setContentText("Jó netezést bátyya!")
                                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(mContext);
                                                notificationManager.notify(MY_NOTIFICATION_ID, builder.build());
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(mContext, "Hiba történt a csomag kiválasztása közben.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(mContext, "A Firestore nincs inicializálva.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

        public void bindTo(Item currentItem) {
            mTitleText.setText(currentItem.getName());
            mInfoText.setText(currentItem.getInfo());
            mPriceText.setText(currentItem.getPrice());
            mRatingBar.setRating(currentItem.getRatedInfo());
            Glide.with(mContext).load(currentItem.getImageResource()).into(mItemImage);
        }
    }
}

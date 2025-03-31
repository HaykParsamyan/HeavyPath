package com.example.heavypath_project;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder> {

    private Context context;
    private List<Announcement> announcementList;

    public AnnouncementAdapter(Context context, List<Announcement> announcementList) {
        this.context = context;
        this.announcementList = announcementList;
    }

    @NonNull
    @Override
    public AnnouncementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_announcement, parent, false);
        return new AnnouncementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementViewHolder holder, int position) {
        Announcement announcement = announcementList.get(position);
        holder.imageView.setImageURI(Uri.parse(announcement.getImageUri()));
        holder.titleTextView.setText(announcement.getTitle());
        holder.carModelTextView.setText(announcement.getCarModel());
        holder.rentingPriceTextView.setText(String.format("Rent: %s/hour", announcement.getRentingPrice()));
        holder.descriptionTextView.setText(announcement.getDescription());
    }

    @Override
    public int getItemCount() {
        return announcementList.size();
    }

    static class AnnouncementViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView, carModelTextView, rentingPriceTextView, descriptionTextView;

        public AnnouncementViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.announcement_image);
            titleTextView = itemView.findViewById(R.id.announcement_title);
            carModelTextView = itemView.findViewById(R.id.announcement_car_model);
            rentingPriceTextView = itemView.findViewById(R.id.announcement_renting_price);
            descriptionTextView = itemView.findViewById(R.id.announcement_description);
        }
    }
}

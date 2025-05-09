package com.example.heavypath_project;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyPostsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMyPosts;
    private AnnouncementAdapter announcementAdapter;
    private List<Announcement> announcementList;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);

        recyclerViewMyPosts = findViewById(R.id.recyclerViewMyPosts);
        announcementList = new ArrayList<>();
        announcementAdapter = new AnnouncementAdapter(this, announcementList);

        recyclerViewMyPosts.setAdapter(announcementAdapter);
        recyclerViewMyPosts.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        fetchMyPosts();
    }

    private void fetchMyPosts() {
        db.collection("announcement").whereEqualTo("email", FirebaseAuth.getInstance().getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        announcementList.clear();
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            Announcement announcement = document.toObject(Announcement.class);
                            announcementList.add(announcement);
                        }
                        announcementAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Failed to fetch all posts: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

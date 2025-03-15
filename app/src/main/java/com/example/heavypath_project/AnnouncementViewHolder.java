
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.TextView;
import android.widget.ImageView;

import com.example.heavypath_project.R;


public class AnnouncementViewHolder extends RecyclerView.ViewHolder {
    TextView textViewTitle;
    TextView textViewPrice;
    TextView textViewDescription;
    ImageView imageViewAnnouncement;

    public AnnouncementViewHolder(View itemView) {
        super(itemView);
        textViewTitle = itemView.findViewById(R.id.textViewTitle);
        textViewPrice = itemView.findViewById(R.id.textViewPrice);
        textViewDescription = itemView.findViewById(R.id.textViewDescription);
        imageViewAnnouncement = itemView.findViewById(R.id.imageViewAnnouncement);
    }
}


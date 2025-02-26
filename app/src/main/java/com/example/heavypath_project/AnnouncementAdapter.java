public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder> {
    private List<Announcement> announcementList;

    public static class AnnouncementViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewAnnouncement;
        public TextView textViewTitle;
        public TextView textViewCarModel;
        public TextView textViewRentingPrice;
        public TextView textViewDescription;

        public AnnouncementViewHolder(View itemView) {
            super(itemView);
            imageViewAnnouncement = itemView.findViewById(R.id.imageViewAnnouncement);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewCarModel = itemView.findViewById(R.id.textViewCarModel);
            textViewRentingPrice = itemView.findViewById(R.id.textViewRentingPrice);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
        }
    }

    public AnnouncementAdapter(List<Announcement> announcementList) {
        this.announcementList = announcementList;
    }

    @Override
    public AnnouncementViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_announcement, parent, false);
        return new AnnouncementViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AnnouncementViewHolder holder, int position) {
        Announcement announcement = announcementList.get(position);
        holder.textViewTitle.setText(announcement.getTitle());
        holder.textViewCarModel.setText(announcement.getCarModel());
        holder.textViewRentingPrice.setText(announcement.getRentingPrice());
        holder.textViewDescription.setText(announcement.getDescription());
        holder.imageViewAnnouncement.setImageURI(announcement.getImageUri());
    }

    @Override
    public int getItemCount() {
        return announcementList.size();
    }
}

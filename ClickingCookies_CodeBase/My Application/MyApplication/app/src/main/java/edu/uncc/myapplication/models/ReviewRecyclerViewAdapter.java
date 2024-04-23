package edu.uncc.myapplication.models;

import static android.app.PendingIntent.getActivity;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import edu.uncc.myapplication.R;

public class ReviewRecyclerViewAdapter extends RecyclerView.Adapter<ReviewRecyclerViewAdapter.ReviewViewHolder>{

    final String TAG = "james";
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ArrayList<Review> reviews;
    ReviewRecyclerViewAdapter.IReviewRecycler mListener;

    public ReviewRecyclerViewAdapter(ArrayList<Review> data, ReviewRecyclerViewAdapter.IReviewRecycler mListener){
        this.mListener = mListener;
        this.reviews = data;
    }

    @NonNull
    @Override
    public ReviewRecyclerViewAdapter.ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reviewlistitem, parent, false);
        ReviewRecyclerViewAdapter.ReviewViewHolder reviewViewHolder = new ReviewRecyclerViewAdapter.ReviewViewHolder(view, mListener);

        return reviewViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewRecyclerViewAdapter.ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);

        Log.d(TAG, "onBindViewHolder: " + review.getName() +" "+ review.getText() + "" + review.getRating());
        String userId = review.getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        String imagePath = "images/" + userId + "/userImage.jpg"; // Adjust this path as needed.
        StorageReference imageRef = storageReference.child(imagePath);

        Glide.with(holder.itemView.getContext())
                .load(R.drawable.transparent) // Replace placeholder_image with your actual placeholder image resource
                .into(holder.profileImage);

        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Use Glide or Picasso to load the image
                Glide.with(holder.itemView.getContext())
                        .load(uri)
                        .into(holder.profileImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Error fetching image: ", e);
                // Handle the case where the image is not found or error
                Glide.with(holder.itemView.getContext())
                        .load(R.drawable.user) // Default user image
                        .into(holder.profileImage);
            }
        });


        holder.userText.setText(review.getName());
        holder.reviewText.setText(review.getText());
        holder.review = review;
        holder.ratingBar.setRating(review.getRating());

        if(review.getUid().equals(mAuth.getCurrentUser().getUid())){
            holder.deleteImage.setVisibility(View.VISIBLE);
        } else {
            holder.deleteImage.setVisibility(View.GONE);
        }

        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (review.getReviewdocid() != null) {  // Assuming Review model has a reviewId field
                    String id = review.getUid();
                    String userName = review.getName().toString();
                    mListener.goToProfile(id, userName);
                }
            }
        });


        holder.deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (review.getReviewdocid() != null) {  // Assuming Review model has a reviewId field
                    db.collection("reviews").document(review.getReviewdocid())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(holder.itemView.getContext(), "Review Deleted!", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(holder.itemView.getContext(), "Failed to Delete Review", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.reviews.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder{
        TextView userText;
        TextView reviewText;
        ImageView deleteImage;
        ImageView profileImage;
        RatingBar ratingBar;
        ReviewRecyclerViewAdapter.IReviewRecycler mListener;
        Review review;
        public ReviewViewHolder(@NonNull View itemView, ReviewRecyclerViewAdapter.IReviewRecycler mListener) {
            super(itemView);
            this.mListener = mListener;
            userText = itemView.findViewById(R.id.ReviewUserName);
            reviewText = itemView.findViewById(R.id.ReviewText);
            profileImage = itemView.findViewById(R.id.ReviewUserImage);
            deleteImage = itemView.findViewById(R.id.DeleteImageView);
            ratingBar = itemView.findViewById(R.id.ReviewRatingBar);

        }
    }


    public interface IReviewRecycler{

        void goToProfile(String id, String userName);

    }
}

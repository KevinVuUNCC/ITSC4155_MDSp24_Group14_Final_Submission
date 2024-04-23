package edu.uncc.myapplication.models;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.uncc.myapplication.R;
import edu.uncc.myapplication.fragments.SearchTagsFragment;

public class TagsRecyclerViewAdapter extends RecyclerView.Adapter<TagsRecyclerViewAdapter.TagsViewHolder>{

    final String TAG = "james";
    ArrayList<String> tags;
    ITagsRecycler mListener;

    public TagsRecyclerViewAdapter(ArrayList<String> data, ITagsRecycler mListener){
        this.mListener = mListener;
        this.tags = data;
    }

    @NonNull
    @Override
    public TagsRecyclerViewAdapter.TagsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.taglistitem, parent, false);
        TagsViewHolder tagsViewHolder = new TagsViewHolder(view, mListener);

        return tagsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TagsViewHolder holder, int position) {
        String tag = tags.get(position);
        Log.d(TAG, "onBindViewHolder: " + tag);
        holder.nameText.setText(tag);
        holder.tag = tag;
    }

    @Override
    public int getItemCount() {
        return this.tags.size();
    }

public static class TagsViewHolder extends RecyclerView.ViewHolder{
    TextView nameText;

    ITagsRecycler mListener;

    String tag;
    public TagsViewHolder(@NonNull View itemView, ITagsRecycler mListener) {
        super(itemView);
        this.mListener = mListener;
        nameText = itemView.findViewById(R.id.textViewName);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.toRecipeFilter(tag);
            }
        });

    }
}
    public interface ITagsRecycler{

        void toRecipeFilter(String tag);
    }
}

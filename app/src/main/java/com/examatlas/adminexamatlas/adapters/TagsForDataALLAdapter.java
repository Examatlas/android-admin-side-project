package com.examatlas.adminexamatlas.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.examatlas.adminexamatlas.R;
import com.examatlas.adminexamatlas.models.TagsForDataALLModel;

import java.util.List;

public class TagsForDataALLAdapter extends RecyclerView.Adapter<TagsForDataALLAdapter.TagViewHolder> {
    private List<TagsForDataALLModel> tagsList;

    public TagsForDataALLAdapter(List<TagsForDataALLModel> tagsList) {
        this.tagsList = tagsList;
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_text_layout, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        TagsForDataALLModel tag = tagsList.get(position);
        holder.tagText.setText(tag.getTagName());
        holder.removeTag.setOnClickListener(v -> {
            // Handle tag removal
            tagsList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, tagsList.size());
        });
    }

    @Override
    public int getItemCount() {
        return tagsList.size();
    }

    static class TagViewHolder extends RecyclerView.ViewHolder {
        TextView tagText;
        ImageView removeTag;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            tagText = itemView.findViewById(R.id.tagText);
            removeTag = itemView.findViewById(R.id.crossBtn);
        }
    }
}


package com.examatlas.adminexamatlas.adapters;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.examatlas.adminexamatlas.R;
import com.examatlas.adminexamatlas.models.ShowAllEBooksModel;

import java.util.ArrayList;
import java.util.Collections;

public class ShowAllEBookAdapter extends RecyclerView.Adapter<ShowAllEBookAdapter.ViewHolder> {
    private ArrayList<ShowAllEBooksModel> ebookModelArrayList;
    private ArrayList<ShowAllEBooksModel> originalEbookModelArrayList;
    private Fragment context;
    private String currentQuery = "";

    public ShowAllEBookAdapter(ArrayList<ShowAllEBooksModel> ebookModelArrayList, Fragment context) {
        this.originalEbookModelArrayList = new ArrayList<>(ebookModelArrayList);
        this.ebookModelArrayList = new ArrayList<>(originalEbookModelArrayList);
        Collections.reverse(this.ebookModelArrayList);
        this.context = context;
    }

    @NonNull
    @Override
    public ShowAllEBookAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_ebook_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowAllEBookAdapter.ViewHolder holder, int position) {
        // Access the last item first
        ShowAllEBooksModel currentEbook = ebookModelArrayList.get(ebookModelArrayList.size() - 1 - position);
        holder.itemView.setTag(currentEbook);

        holder.setHighlightedText(holder.title, currentEbook.getTitle(), currentQuery);
        holder.setHighlightedText(holder.content, currentEbook.getContent(), currentQuery);
        holder.setHighlightedText(holder.tags, currentEbook.getTags(), currentQuery);
        holder.setHighlightedText(holder.author, "By: " + currentEbook.getAuthor(), currentQuery);
    }

    @Override
    public int getItemCount() {
        return ebookModelArrayList.size();
    }

    public void filter(String query) {
        currentQuery = query; // Store current query
        ebookModelArrayList.clear();
        if (query.isEmpty()) {
            ebookModelArrayList.addAll(originalEbookModelArrayList); // Restore the original list if no query
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (ShowAllEBooksModel eBooksModel : originalEbookModelArrayList) {
                if (eBooksModel.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                        eBooksModel.getContent().toLowerCase().contains(lowerCaseQuery) ||
                        eBooksModel.getTags().toLowerCase().contains(lowerCaseQuery) ||
                        eBooksModel.getPrice().toLowerCase().contains(lowerCaseQuery)) {
                    ebookModelArrayList.add(eBooksModel); // Add matching eBook to the filtered list
                }
            }
        }
        notifyDataSetChanged(); // Notify adapter of data change
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, content, tags, author;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.txtBookTitle);
            content = itemView.findViewById(R.id.txtContent);
            tags = itemView.findViewById(R.id.txtTags);
            author = itemView.findViewById(R.id.txtAuthor);
        }

        public void setHighlightedText(TextView textView, String text, String query) {
            if (query == null || query.isEmpty()) {
                textView.setText(text);
                return;
            }
            SpannableString spannableString = new SpannableString(text);
            int startIndex = text.toLowerCase().indexOf(query.toLowerCase());
            while (startIndex >= 0) {
                int endIndex = startIndex + query.length();
                spannableString.setSpan(new android.text.style.BackgroundColorSpan(Color.YELLOW), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                startIndex = text.toLowerCase().indexOf(query.toLowerCase(), endIndex);
            }
            textView.setText(spannableString);
        }
    }

    public void updateOriginalList(ArrayList<ShowAllEBooksModel> newList) {
        originalEbookModelArrayList.clear();
        originalEbookModelArrayList.addAll(newList);
    }
}

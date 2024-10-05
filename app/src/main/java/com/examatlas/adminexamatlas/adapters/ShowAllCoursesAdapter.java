package com.examatlas.adminexamatlas.adapters;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.examatlas.adminexamatlas.R;
import com.examatlas.adminexamatlas.models.ShowAllBlogModel;
import com.examatlas.adminexamatlas.models.ShowAllCAModel;
import com.examatlas.adminexamatlas.models.ShowAllCourseModel;

import java.util.ArrayList;
import java.util.Collections;

public class ShowAllCoursesAdapter extends RecyclerView.Adapter<ShowAllCoursesAdapter.ViewHolder> {
    Fragment context;
    ArrayList<ShowAllCourseModel> showAllCourseModelArrayList;
    ArrayList<ShowAllCourseModel> orginalShowAllBlogModelArrayList;
        private String currentQuery = "";
    public ShowAllCoursesAdapter(Fragment context, ArrayList<ShowAllCourseModel> showAllCourseModelArrayList2) {
        this.context = context;
        this.showAllCourseModelArrayList = showAllCourseModelArrayList2;
        this.orginalShowAllBlogModelArrayList = new ArrayList<>(showAllCourseModelArrayList2);
        Collections.reverse(this.showAllCourseModelArrayList);
    }

    @NonNull
    @Override
    public ShowAllCoursesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_course_layout_items,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowAllCoursesAdapter.ViewHolder holder, int position) {
        ShowAllCourseModel currentCourse = showAllCourseModelArrayList.get(showAllCourseModelArrayList.size() - 1 - position);
        holder.itemView.setTag(currentCourse);

        holder.setHighlightedText(holder.title, currentCourse.getTitle(), currentQuery);
        holder.setHighlightedText(holder.price,"₹ " + currentCourse.getPrice(), currentQuery);

    }

    @Override
    public int getItemCount() {
        return showAllCourseModelArrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView title,price;
        ImageView courseImage;
        public ViewHolder(@NonNull View itemView){
            super(itemView);

            title = itemView.findViewById(R.id.title);
            price = itemView.findViewById(R.id.txtPrice);
            courseImage = itemView.findViewById(R.id.courseImage);

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
    public void filter(String query) {
        currentQuery = query; // Store current query
        showAllCourseModelArrayList.clear();
        if (query.isEmpty()) {
            showAllCourseModelArrayList.addAll(orginalShowAllBlogModelArrayList); // Restore the original list if no query
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (ShowAllCourseModel blog : orginalShowAllBlogModelArrayList) {
                if (blog.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                        blog.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                        blog.getPrice().toLowerCase().contains(lowerCaseQuery)) {
                    showAllCourseModelArrayList.add(blog); // Add matching blog to the filtered list
                }
            }
        }
        notifyDataSetChanged(); // Notify adapter of data change
    }
    public void updateOriginalList(ArrayList<ShowAllCourseModel> newList) {
        orginalShowAllBlogModelArrayList.clear();
        orginalShowAllBlogModelArrayList.addAll(newList);
    }
}

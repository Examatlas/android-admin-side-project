package com.examatlas.adminexamatlas.adapters;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.examatlas.adminexamatlas.R;
import com.examatlas.adminexamatlas.extraClasses.Constant;
import com.examatlas.adminexamatlas.extraClasses.MySingletonFragment;
import com.examatlas.adminexamatlas.fragment.CurrentAffairCreateDeleteFragment;
import com.examatlas.adminexamatlas.models.ShowAllBlogModel;
import com.examatlas.adminexamatlas.models.ShowAllCAModel;
import com.examatlas.adminexamatlas.models.TagsForDataALLModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ShowAllCAAdapter extends RecyclerView.Adapter<ShowAllCAAdapter.ViewHolder> {
    private ArrayList<ShowAllCAModel> showAllCAModelArrayList;
    private ArrayList<ShowAllCAModel> orginalShowAllCAModelArrayList;
    private Fragment context;
    private String currentQuery = "";
    public ShowAllCAAdapter(ArrayList<ShowAllCAModel> showAllCAModelArrayList, Fragment context) {
        this.showAllCAModelArrayList = showAllCAModelArrayList;
        this.context = context;
        this.orginalShowAllCAModelArrayList = new ArrayList<>(showAllCAModelArrayList);
        Collections.reverse(this.showAllCAModelArrayList);
    }

    @NonNull
    @Override
    public ShowAllCAAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_current_affair_item_layout, parent, false);
        return new ShowAllCAAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShowAllCAAdapter.ViewHolder holder, int position) {

        ShowAllCAModel currentCA = showAllCAModelArrayList.get(showAllCAModelArrayList.size() - 1 - position);
        holder.itemView.setTag(currentCA);

        // Set highlighted text
        holder.setHighlightedText(holder.title, currentCA.getTitle(), currentQuery);
        holder.setHighlightedText(holder.keyword, currentCA.getKeyword(), currentQuery);
        holder.setHighlightedText(holder.content, currentCA.getContent(), currentQuery);
        holder.setHighlightedText(holder.tags, currentCA.getTags(), currentQuery);
        holder.createdDate.setText(currentCA.getCreatedDate());

        holder.editCABtn.setOnClickListener(view -> openEditBlogDialog(currentCA));
        holder.deleteCABtn.setOnClickListener(view -> quitDialog(position));

    }

    @Override
    public int getItemCount() {
        return showAllCAModelArrayList.size();
    }

    public void filter(String query) {
        currentQuery = query; // Store current query
        showAllCAModelArrayList.clear();
        if (query.isEmpty()) {
            showAllCAModelArrayList.addAll(orginalShowAllCAModelArrayList); // Restore the original list if no query
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (ShowAllCAModel CA : orginalShowAllCAModelArrayList) {
                if (CA.getTitle().toLowerCase().contains(lowerCaseQuery) ||
                        CA.getKeyword().toLowerCase().contains(lowerCaseQuery) ||
                        CA.getContent().toLowerCase().contains(lowerCaseQuery)) {
                    showAllCAModelArrayList.add(CA); // Add matching blog to the filtered list
                }
            }
        }
        notifyDataSetChanged(); // Notify adapter of data change
    }
    private void openEditBlogDialog(ShowAllCAModel caModel) {
        Dialog editBlogDialogBox = new Dialog(context.requireContext());
        editBlogDialogBox.setContentView(R.layout.create_data_dialog_box);

        ArrayList<TagsForDataALLModel> tagsForDataALLModelArrayList = new ArrayList<>();
        TagsForDataALLAdapter tagsForDataALLAdapter = new TagsForDataALLAdapter(tagsForDataALLModelArrayList);
        RecyclerView tagsRecyclerView = editBlogDialogBox.findViewById(R.id.tagsRecycler);
        tagsRecyclerView.setVisibility(View.VISIBLE);
        tagsRecyclerView.setLayoutManager(new GridLayoutManager(context.requireContext(), 2));
        tagsRecyclerView.setAdapter(tagsForDataALLAdapter);

        TextView headerTxt = editBlogDialogBox.findViewById(R.id.txtAddData);
        EditText titleEditTxt = editBlogDialogBox.findViewById(R.id.titleEditTxt);
        EditText keywordEditTxt = editBlogDialogBox.findViewById(R.id.keywordEditText);
        EditText contentEditTxt = editBlogDialogBox.findViewById(R.id.contentEditText);
        EditText tagsEditTxt = editBlogDialogBox.findViewById(R.id.tagsEditText);

        headerTxt.setText("Edit Current Affairs");
        titleEditTxt.setText(caModel.getTitle());
        keywordEditTxt.setText(caModel.getKeyword());
        contentEditTxt.setText(caModel.getContent());

        String[] tagsArray = caModel.getTags().split(",");
        for (String tag : tagsArray) {
            tagsForDataALLModelArrayList.add(new TagsForDataALLModel(tag.trim()));
        }
        tagsForDataALLAdapter.notifyDataSetChanged();

        Button uploadBlogDetailsBtn = editBlogDialogBox.findViewById(R.id.btnSubmit);

        tagsEditTxt.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String tagText = tagsEditTxt.getText().toString().trim();
                if (!tagText.isEmpty()) {
                    tagsForDataALLModelArrayList.add(new TagsForDataALLModel(tagText));
                    tagsForDataALLAdapter.notifyItemInserted(tagsForDataALLModelArrayList.size() - 1);
                    tagsEditTxt.setText("");
                    tagsRecyclerView.setVisibility(View.VISIBLE);
                }
                return true;
            }
            return false;
        });

        uploadBlogDetailsBtn.setOnClickListener(view -> {
            sendingCADetails(caModel.getCaID(),
                    titleEditTxt.getText().toString().trim(),
                    keywordEditTxt.getText().toString().trim(),
                    contentEditTxt.getText().toString().trim(),
                    tagsForDataALLModelArrayList);
            editBlogDialogBox.dismiss();
        });

        ImageView btnCross = editBlogDialogBox.findViewById(R.id.btnCross);
        btnCross.setOnClickListener(view -> editBlogDialogBox.dismiss());

        editBlogDialogBox.show();
        WindowManager.LayoutParams params = editBlogDialogBox.getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;

        // Set the window attributes
        editBlogDialogBox.getWindow().setAttributes(params);

        // Now, to set margins, you'll need to set it in the root view of the dialog
        FrameLayout layout = (FrameLayout) editBlogDialogBox.findViewById(android.R.id.content);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) layout.getLayoutParams();

        layoutParams.setMargins(0, 50, 0, 50);
        layout.setLayoutParams(layoutParams);

        // Background and animation settings
        editBlogDialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        editBlogDialogBox.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    private void sendingCADetails(String caID, String title, String keyword, String content, ArrayList<TagsForDataALLModel> tagsForDataALLModelArrayList) {
        String updateURL = Constant.BASE_URL + "currentAffair/updateCA/" + caID;

        // Create JSON object to send in the request
        JSONObject caDetails = new JSONObject();
        try {
            caDetails.put("title", title);
            caDetails.put("keyword", keyword);
            caDetails.put("content", content);

            // Convert tags to a JSONArray
            JSONArray tagsArray = new JSONArray();
            for (TagsForDataALLModel tag : tagsForDataALLModelArrayList) {
                tagsArray.put(tag.getTagName()); // Assuming `getTag()` returns the tag string
            }
            caDetails.put("tags", tagsArray);

        } catch (JSONException e) {
            Log.e("JSON_ERROR", "Error creating JSON object: " + e.getMessage());
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, updateURL, caDetails,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                Toast.makeText(context.getContext(), "Current Affairs Updated Successfully", Toast.LENGTH_SHORT).show();
                                ((CurrentAffairCreateDeleteFragment) context).showAllCAFunction();
                            } else {
                                Toast.makeText(context.getContext(), "Failed to update Current Affairs", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e("JSON_ERROR", "Error parsing JSON response: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = "Error: " + error.toString();
                if (error.networkResponse != null) {
                    try {
                        String responseData = new String(error.networkResponse.data, "UTF-8");
                        errorMessage += "\nStatus Code: " + error.networkResponse.statusCode;
                        errorMessage += "\nResponse Data: " + responseData;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(context.getContext(), errorMessage, Toast.LENGTH_LONG).show();
//                Log.e("CurrentAffairsUpdateError", errorMessage);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        MySingletonFragment.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }


    private void quitDialog(int position) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context.requireContext());
        builder.setTitle("Delete Current Affairs")
                .setMessage("Are you sure you want to delete this current affairs?")
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Delete", (dialog, which) -> deleteCurrentAffairs(position))
                .show();
    }

    private void deleteCurrentAffairs(int position) {
        String deleteURL = Constant.BASE_URL + "currentAffair/deleteById/" + showAllCAModelArrayList.get(position).getCaID();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, deleteURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("status");
                            if (status) {
                                Toast.makeText(context.getContext(), "Current Affairs Deleted Successfully", Toast.LENGTH_SHORT).show();
                                showAllCAModelArrayList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, showAllCAModelArrayList.size());
                                ((CurrentAffairCreateDeleteFragment) context).showAllCAFunction();
                            }
                        } catch (JSONException e) {
                            Log.e("JSON_ERROR", "Error parsing JSON: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errorMessage = "Error: " + error.toString();
                if (error.networkResponse != null) {
                    try {
                        String responseData = new String(error.networkResponse.data, "UTF-8");
                        errorMessage += "\nStatus Code: " + error.networkResponse.statusCode;
                        errorMessage += "\nResponse Data: " + responseData;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(context.getContext(), errorMessage, Toast.LENGTH_LONG).show();
//                Log.e("BlogFetchError", errorMessage);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        MySingletonFragment.getInstance(context).addToRequestQueue(jsonObjectRequest);
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, keyword, content, tags, createdDate;
        ImageView editCABtn, deleteCABtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txtTitle);
            keyword = itemView.findViewById(R.id.txtCAKeyword);
            content = itemView.findViewById(R.id.txtContent);
            tags = itemView.findViewById(R.id.tagTxt);
            createdDate = itemView.findViewById(R.id.txtDate);
            editCABtn = itemView.findViewById(R.id.editCABtn);
            deleteCABtn = itemView.findViewById(R.id.deleteCABtn);
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
    public void updateOriginalList(ArrayList<ShowAllCAModel> newList) {
        orginalShowAllCAModelArrayList.clear();
        orginalShowAllCAModelArrayList.addAll(newList);
    }
}

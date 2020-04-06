package com.example.healthcare.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.Model.CategoryModel;
import com.example.healthcare.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    Context mcontext;
    List<CategoryModel> categoryModelList;

    public CategoryAdapter(Context context, List<CategoryModel> categoryModelList) {
        mcontext=context;
        this.categoryModelList=categoryModelList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.category_layout,parent,false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.tvcategory.setText(categoryModelList.get(position).getCategoryName());
    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvcategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvcategory=itemView.findViewById(R.id.tvcategorylist);
        }
    }
}

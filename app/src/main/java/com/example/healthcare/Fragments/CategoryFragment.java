package com.example.healthcare.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.healthcare.Adapter.CategoryAdapter;
import com.example.healthcare.Model.CategoryModel;
import com.example.healthcare.R;

import java.util.ArrayList;
import java.util.List;


public class CategoryFragment extends Fragment {
    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private List<CategoryModel> categoryModelList;
    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_category, container, false);
        categoryRecyclerView=view.findViewById(R.id.Rvcategory);

        setCategoryAdapter();
        return view;
    }

    public void setCategoryAdapter(){
        categoryModelList=new ArrayList<>();
        categoryModelList.clear();
        categoryModelList.add(new CategoryModel("Category"));
        categoryModelList.add(new CategoryModel("Category"));
        categoryModelList.add(new CategoryModel("Category"));
        categoryModelList.add(new CategoryModel("Category"));
        categoryModelList.add(new CategoryModel("Category"));
        categoryModelList.add(new CategoryModel("Category"));categoryModelList.add(new CategoryModel("Category"));
        categoryModelList.add(new CategoryModel("Category"));categoryModelList.add(new CategoryModel("Category"));
        categoryModelList.add(new CategoryModel("Category"));categoryModelList.add(new CategoryModel("Category"));
        categoryModelList.add(new CategoryModel("Category"));categoryModelList.add(new CategoryModel("Category"));
        categoryModelList.add(new CategoryModel("Category"));categoryModelList.add(new CategoryModel("Category"));
        categoryModelList.add(new CategoryModel("Category"));categoryModelList.add(new CategoryModel("Category"));
        categoryModelList.add(new CategoryModel("Category"));categoryModelList.add(new CategoryModel("Category"));
        categoryModelList.add(new CategoryModel("Category"));categoryModelList.add(new CategoryModel("Category"));
        categoryModelList.add(new CategoryModel("Category"));categoryModelList.add(new CategoryModel("Category"));
        categoryModelList.add(new CategoryModel("Category"));categoryModelList.add(new CategoryModel("Category"));

        categoryAdapter=new CategoryAdapter(getContext(),categoryModelList);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        categoryRecyclerView.setAdapter(categoryAdapter);

    }
}

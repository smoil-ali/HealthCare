package com.example.healthcare.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.healthcare.R;

import java.util.ArrayList;
import java.util.List;

public class AskQuestion extends AppCompatActivity {
    Spinner categorySpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_question);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CategoriesSpinner();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                if(getParentActivityIntent()==null){
                    onBackPressed();
                }else{
                    NavUtils.navigateUpFromSameTask(this);
                }
               return true;

            default:
                 return super.onOptionsItemSelected(item);
        }


    }

    public void CategoriesSpinner(){
        categorySpinner=findViewById(R.id.categorySpinner);
        List<String> categoryList=new ArrayList<>();
        categoryList.add("Select Categories");
        categoryList.add("Categories");
        categoryList.add("Categories");
        categoryList.add("Categories");
        categoryList.add("Categories");
        categoryList.add("Categories");
        categoryList.add("Categories");

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,categoryList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categorySpinner.setAdapter(adapter);

    }
}

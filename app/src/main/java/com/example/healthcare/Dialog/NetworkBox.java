package com.example.healthcare.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.healthcare.R;

public class NetworkBox extends DialogFragment {

    TextView alertMessageNetwork;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=requireActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.networklayout,null);
        builder.setView(view);
        alertMessageNetwork=view.findViewById(R.id.alertMessageConnection);

        return builder.create();
    }
}

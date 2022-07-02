package com.example.mainproject.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.android.multidex.mainproject.R;

import java.util.Objects;

public class NoInternetConnectionFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.no_internet_connection_fragment, container, false);

        ImageView cross = view.findViewById(R.id.iv_nicf_cross);
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().remove(NoInternetConnectionFragment.this).commit();
            }
        });

        return view;
    }

}

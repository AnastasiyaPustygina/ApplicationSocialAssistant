package com.example.mainproject.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mainproject.OpenHelper;
import com.example.android.multidex.mainproject.R;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class SignInFragment extends Fragment {
    private EditText ed_data;
    private EditText ed_pass;
    private AppCompatButton btSignIn, btReg;
    private CheckBox cbData;

    public static final String APP_PREFERENCES = "my_pref";
    public static SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sign_in_fragment, container, false);

        sharedPreferences = getContext().getSharedPreferences
                (APP_PREFERENCES, Context.MODE_PRIVATE);
        TextView checking = view.findViewById(R.id.tv_check);
        ed_data = view.findViewById(R.id.ed_signIn_data);
        ed_pass = view.findViewById(R.id.ed_signIn_pass);
        btSignIn = view.findViewById(R.id.bt_signIn_fr_signIn);
        btReg = view.findViewById(R.id.bt_reg_fr_signIn);
        cbData = view.findViewById(R.id.cb_signIn);
        btReg.setOnClickListener((view1) -> {
            NavHostFragment.findNavController(SignInFragment.this).navigate(
                    R.id.action_signInFragment_to_regFragment);
        });
        btSignIn.performClick();
        OpenHelper oh = new OpenHelper(getContext(), "OpenHelper", null, OpenHelper.VERSION);
        OpenHelper openHelper = new OpenHelper(getContext(), "OpenHelper", null, OpenHelper.VERSION);
        if(sharedPreferences.getString("last_password", " ").equals(
                openHelper.findPassByLogin(sharedPreferences.getString("last_login", " ")))) {
            Bundle bundle = new Bundle();
            bundle.putString("LOG", sharedPreferences.getString("last_login", " "));
            btSignIn.setOnClickListener((view1) -> {
                NavHostFragment.findNavController(SignInFragment.this).navigate(
                        R.id.action_signInFragment_to_mainFragment, bundle);
            });
            btSignIn.performClick();
        }

        btSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String encodedHash = "";
                try {
                    MessageDigest digest = MessageDigest.getInstance("SHA-256");
                    encodedHash = Arrays.toString(digest.digest(
                            ed_pass.getText().toString().getBytes(StandardCharsets.UTF_8)));
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                if (ed_pass.getText().toString().isEmpty() || ed_data.getText().toString().isEmpty())
                    checking.setText("Не все поля заполнены");
                else if ((encodedHash).equals(oh.findPassByLogin(ed_data.getText().toString())))
                {
                    if(cbData.isChecked()){
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("last_login", ed_data.getText().toString());
                        editor.putString("last_password", encodedHash);
                        editor.commit();
                    }
                    Bundle bundle = new Bundle();
                    bundle.putString("LOG", ed_data.getText().toString());
                    btSignIn.setOnClickListener((view1) -> {
                        NavHostFragment.findNavController(SignInFragment.this).navigate(
                                R.id.action_signInFragment_to_mainFragment, bundle);
                    });
                    btSignIn.performClick();
                } else {
                    checking.setText("Логин или пароль не верны ");
                }
            }
        });

        return view;
    }


}
package com.example.mainproject.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mainproject.OpenHelper;
import com.example.android.multidex.mainproject.R;
import com.example.mainproject.domain.Person;
import com.example.mainproject.rest.AppApiVolley;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class RegFragment extends Fragment {

    private String data;
    private String name;
    private int age;
    private String dateOfBirth;
    private String city;
    private String password1;
    private String password2;
    private AppCompatButton btOfTel;
    private AppCompatButton btOfEmail;
    private EditText edTelOrEmail;
    private AppCompatButton bt_reg_fr_reg;
    private TextView checking, tv_data;
    private final String IMAGE_URL = "https://firebasestorage.googleapis.com/v0/b/social-assistant-7a25d.appspot.com/o/images%2FavaForProject.jpg?alt=media&token=e0821c60-2fc5-4d68-92fa-2538d3baca1a";



    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reg_fragment, container, false);


        edTelOrEmail = view.findViewById(R.id.ed_reg_data);
        checking = view.findViewById(R.id.checking);
        tv_data = view.findViewById(R.id.tv_reg_data);
        EditText edName = view.findViewById(R.id.ed_reg_name);
        EditText edAge = view.findViewById(R.id.ed_reg_age);
        bt_reg_fr_reg = view.findViewById(R.id.bt_reg_fr_reg);
        EditText edBateOfBirth = view.findViewById(R.id.ed_reg_dateOfBirth);
        EditText edCity = view.findViewById(R.id.ed_reg_city);
        EditText edPass1 = view.findViewById(R.id.ed_reg_pass1);
        EditText edPass2 = view.findViewById(R.id.ed_reg_pass2);
        btOfTel = view.findViewById(R.id.bt_reg_telephone);
        btOfEmail = view.findViewById(R.id.bt_reg_email);

        btOfTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btOfTel.setBackgroundResource(R.drawable.dark_circle_button);
                btOfTel.setTextColor(getResources().getColor(R.color.white));
                btOfEmail.setBackgroundResource(R.drawable.light_circle_button);
                btOfEmail.setTextColor(getResources().getColor(R.color.purple_700));
                tv_data.setText("Номер телефона");
                edTelOrEmail.setHint(Html.fromHtml("<small>"
                        + getString(R.string.ed_tel) + "<small>"));
            }
        });
        btOfEmail.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                btOfEmail.setBackgroundResource(R.drawable.dark_circle_button);
                btOfEmail.setTextColor(getResources().getColor(R.color.white));
                btOfTel.setBackgroundResource(R.drawable.light_circle_button);
                btOfTel.setTextColor(getResources().getColor(R.color.purple_700));
                tv_data.setText("Адрес электронной почты");
                edTelOrEmail.setHint(Html.fromHtml("<small>"
                        + getString(R.string.ed_email) + "<small>"));
            }
        });
        bt_reg_fr_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int check = 0;
                name = edName.getText().toString();
                try {
                    age = Integer.parseInt(edAge.getText().toString());
                } catch (Exception e) {
                    checking.setText("Введите корректные данные");
                    check = 1;
                }
                data = edTelOrEmail.getText().toString();
                OpenHelper openHelper = new OpenHelper(
                        getContext(), "OpenHelper", null, OpenHelper.VERSION);
                if(openHelper.findAllName().contains(name)) {
                    check = -1;
                    checking.setText("Такий логин уже существует");
                }
                dateOfBirth = edBateOfBirth.getText().toString();
                city = edCity.getText().toString();
                password1 = edPass1.getText().toString();
                password2 = edPass2.getText().toString();
                if (name.isEmpty()
                        || age == 0
                        || data.isEmpty()
                        || dateOfBirth.isEmpty()
                        || city.isEmpty()
                        || password1.isEmpty()
                        || password2.isEmpty()) {
                    checking.setText("Не все поля заполнены");
                }
                else if (!password1.equals(password2)) {
                    checking.setText("Пароли не совпадают");
                }
                else if(!isOnline()){
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().add(R.id.fl_reg, new NoInternetConnectionFragment()).commit();
                }
                else if(edTelOrEmail.getHint().toString().equals(getString(R.string.ed_email)) &&
                        !edTelOrEmail.getText().toString().contains("@"))
                    checking.setText("Введите корректный адрес электронной почты");
                else if(edTelOrEmail.getHint().toString().equals(getString(R.string.ed_tel)) &&
                        (edTelOrEmail.getText().toString()).matches(".*[a-zA-Z]+.*"))
                    checking.setText("Введите корректный номер телефона");
                else if (check == 0){
                    String encodedHash = null;
                    try {
                        MessageDigest digest = MessageDigest.getInstance("SHA-256");
                        encodedHash = Arrays.toString(digest.digest(
                                password1.getBytes(StandardCharsets.UTF_8)));
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }

                    openHelper.insert(new Person(data, name, age,IMAGE_URL, dateOfBirth, city, encodedHash));

                    new AppApiVolley(getContext()).addPerson
                            (openHelper.findPersonByLogin(name));

                    bt_reg_fr_reg.setOnClickListener((view1) -> {
                        NavHostFragment.
                                findNavController(RegFragment.this).navigate(
                                R.id.action_regFragment_to_signInFragment);
                    });
                    bt_reg_fr_reg.performClick();
                }
            }
        });

        return view;
    }

    public boolean isOnline(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo == null) return false;
        else return networkInfo.isConnected();
    }
}
package com.example.mainproject.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;

import com.example.mainproject.OpenHelper;
import com.example.android.multidex.mainproject.R;
import com.example.mainproject.domain.Chat;
import com.example.mainproject.domain.Organization;
import com.example.mainproject.rest.AppApiVolley;
import com.squareup.picasso.Picasso;

public class FullInfoFragment extends Fragment {

    private ImageView photoOrg;
    private TextView name, type, desc, address, needs, link;
    private AppCompatButton bt_help;
    private ImageView bt_prev;
    private final int height  = Resources.getSystem().getDisplayMetrics().heightPixels;
    private final int width  = Resources.getSystem().getDisplayMetrics().widthPixels;
    private float scale = Resources.getSystem().getDisplayMetrics().density;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.full_info_fragment, container, false);

        bt_prev = view.findViewById(R.id.bt_fullInfo_arrowBack);
        bt_help = view.findViewById(R.id.bt_fullInfo_help);
        photoOrg = view.findViewById(R.id.iv_fullInfo_photoOrg);
        name = view.findViewById(R.id.tv_fullInfo_nameOrg);
        type = view.findViewById(R.id.tv_fullInfo_typeOrg);
        desc = view.findViewById(R.id.tv_fullInfo_descOrg);
        address = view.findViewById(R.id.tv_fullInfo_addressOrg);
        needs = view.findViewById(R.id.tv_fullInfo_needsOrg);
        link = view.findViewById(R.id.tv_fullInfo_linkToWeb);
        TextView tv_prof = view.findViewById(R.id.tv_fullInfo_profile);

        OpenHelper openHelper = new OpenHelper(getContext(), "OpenHelper", null, OpenHelper.VERSION);
        Organization organization = openHelper.findOrgByName(getArguments().getString("NameOrg"));

        try{
            if(organization.getPhotoOrg() == null)
                photoOrg.setImageDrawable(getResources().getDrawable(R.drawable.ava_for_project));
            else Picasso.get().load(organization.getPhotoOrg()).into(photoOrg);
        }catch (Exception e){
            photoOrg.setImageDrawable(getResources().getDrawable(R.drawable.ava_for_project));
        }
        name.setText(organization.getName());
        String tmp = getColoredSpanned(organization.getType(), "#000000");
        type.setText(Html.fromHtml("Тип:" + "\n" + tmp));
        tmp = getColoredSpanned(organization.getDescription(), "#000000");
        desc.setText(Html.fromHtml("Описание: \n \n" + tmp));
        tmp = getColoredSpanned(organization.getAddress(), "#000000");
        address.setText(Html.fromHtml("Адрес: \n \n" + tmp));
        tmp = getColoredSpanned(organization.getNeeds(), "#000000");
        needs.setText(Html.fromHtml("Потребности: \n \n" +tmp));
        if(organization.getLinkToWebsite() != null) {
            tmp = getColoredSpanned(organization.getLinkToWebsite(), "#000000");
            link.setText(Html.fromHtml("Официальный сайт: \n \n" +tmp));
        }
        else link.setText("Официальный сайт: \n \n" + "(не указан)");

        int data = Math.max(width, height);
        int size20 = (int) (scale * (data / 80) + 0.5f);
        int size10 = (int) (scale * (data / 140) + 0.5f);
        int size40 = (int) (scale * (data / 45) + 0.5f);
        int size60 = (int) (scale * (data / 30) + 0.5f);
        int size30 = (int) (scale * (data / 70) + 0.5f);
        int size80 = (int) (scale * (data / 20) + 0.5f);
        float sizeForTV15 = (float) data / 160;

        ViewGroup.MarginLayoutParams paramsProf = (ViewGroup.MarginLayoutParams) tv_prof.getLayoutParams();
        paramsProf.setMargins(0, size60, 0, size60);
        tv_prof.requestLayout();
        ViewGroup.MarginLayoutParams paramsArrow = (ViewGroup.MarginLayoutParams) bt_prev.getLayoutParams();
        paramsArrow.setMargins(size10, 0, 0, 0);
        bt_prev.requestLayout();
        tv_prof.setTextSize((float) (data / 100));
        name.setTextSize((float) data / 130);
        name.setPadding(0, size20, 0, size20);
        photoOrg.setPadding(size20, 0, 0, 0);
        type.setTextSize(sizeForTV15);
        type.setPadding(size20, size40, size20, size10);

        desc.setTextSize(sizeForTV15);
        desc.setPadding(size20, size20, size20, size10);

        address.setTextSize(sizeForTV15);
        address.setPadding(size20, size20, size20, size10);

        needs.setTextSize(sizeForTV15);
        needs.setPadding(size20, size20, size20, size10);

        link.setTextSize(sizeForTV15);
        link.setPadding(size20, size20, size20, size10);

        bt_help.setTextSize(sizeForTV15);

        ViewGroup.MarginLayoutParams paramsBt = (ViewGroup.MarginLayoutParams) bt_help.getLayoutParams();
        paramsBt.setMargins(size30, size40, size30, size80);
        bt_help.requestLayout();


        bt_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isOnline()) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().add(R.id.fl_fif, new NoInternetConnectionFragment()).commit();
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("LOG", getArguments().getString("LOG"));
                    bundle.putString("NameOrg", getArguments().getString("NameOrg"));

                    openHelper.insertChat(new Chat(
                            openHelper.findPersonByLogin(getArguments().getString("LOG")),
                            organization));
                    new AppApiVolley(getContext()).addChat(openHelper.findChatByPersonIdAndOrgId(
                            openHelper.findPersonByLogin(getArguments().getString("LOG")).getId(),
                            organization.getId()
                    ));

                    bt_help.setOnClickListener((view1) -> {
                        NavHostFragment.
                                findNavController(FullInfoFragment.this).navigate(
                                R.id.action_fullInfoFragment_to_chatFragment, bundle);
                    });
                    bt_help.performClick();
                }
            }
        });
        bt_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getArguments().getString("PrevFragment").equals("list")){
                    Bundle bundle = new Bundle();
                    bundle.putString("LOG", getArguments().getString("LOG"));
                    bundle.putString("NameOrg", getArguments().getString("NameOrg"));
                    bt_prev.setOnClickListener((view1) -> {
                        NavHostFragment.
                                findNavController(FullInfoFragment.this).navigate(
                                R.id.action_fullInfoFragment_to_listFragment, bundle);
                    });
                    bt_prev.performClick();
                }
                else if(getArguments().getString("PrevFragment").equals("fav")){
                    Bundle bundle = new Bundle();
                    bundle.putString("LOG", getArguments().getString("LOG"));
                    bundle.putString("NameOrg", getArguments().getString("NameOrg"));
                    bt_prev.setOnClickListener((view1) -> {
                        NavHostFragment.
                                findNavController(FullInfoFragment.this).navigate(
                                R.id.action_fullInfoFragment_to_favouritesFragment, bundle);
                    });
                    bt_prev.performClick();
                }
                else if(getArguments().getString("PrevFragment").equals("map")){
                    try {
                        Bundle bundle = new Bundle();
                        bundle.putString("LOG", getArguments().getString("LOG"));
                        bundle.putString("NameOrg", getArguments().getString("NameOrg"));
                        bt_prev.setOnClickListener((view1) -> {
                            NavHostFragment.
                                    findNavController(FullInfoFragment.this).navigate(
                                    R.id.action_fullInfoFragment_to_mapFragment, bundle);
                        });
                        bt_prev.performClick();
                    }catch (Exception e){
                        Log.d("FavFragment", "Получение разрешения на определение геолокации");
                    }
                }
                else{
                    Bundle bundle = new Bundle();
                    bundle.putString("LOG", getArguments().getString("LOG"));
                    bundle.putString("NameOrg", getArguments().getString("NameOrg"));
                    bt_prev.setOnClickListener((view1) -> {
                        NavHostFragment.
                                findNavController(FullInfoFragment.this).navigate(
                                R.id.action_fullInfoFragment_to_chatFragment, bundle);
                    });
                    bt_prev.performClick();
                }
            }
        });

        return view;
    }

    private String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }
    public boolean isOnline(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo == null) return false;
        else return networkInfo.isConnected();
    }
}
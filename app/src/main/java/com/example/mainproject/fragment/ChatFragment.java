package com.example.mainproject.fragment;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mainproject.OpenHelper;
import com.example.android.multidex.mainproject.R;
import com.example.mainproject.adapter.ChatArrayAdapter;
import com.example.mainproject.domain.Message;
import com.example.mainproject.domain.Organization;
import com.example.mainproject.rest.AppApiVolley;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatFragment extends Fragment {

    private ImageView imOrg, ivMicro, bt_arrow_back;
    private TextView nameOrg;
    private EditText et_msg;
    private ChatArrayAdapter recyclerAdapter;
    private MyChatThread myChatThread;
    private RecyclerView rec;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.chat_fragment, container, false);

        bt_arrow_back = view.findViewById(R.id.bt_chat_arrowBack);
        et_msg = view.findViewById(R.id.et_chat_msg);
        imOrg = view.findViewById(R.id.iv_ch_imOrg);
        ivMicro = view.findViewById(R.id.iv_chat_micro);
        nameOrg = view.findViewById(R.id.tv_ch_nameOrg);
        rec = view.findViewById(R.id.rec_chat);

        OpenHelper openHelper = new OpenHelper(getContext(), "OpenHelper", null, OpenHelper.VERSION);

        int perId = openHelper.findPersonByLogin(
                getArguments().getString("LOG")).getId();
        Organization org = openHelper.findOrgByName(getArguments().getString("NameOrg"));
        try {

            recyclerAdapter = new ChatArrayAdapter(getContext(),
                    ChatFragment.this, openHelper.findChatIdByOrgIdAndPerId(org.getId(), perId));
            rec.setAdapter(recyclerAdapter);
            rec.scrollToPosition(openHelper.findMsgByChatId(
                    openHelper.findChatIdByOrgIdAndPerId(org.getId(), perId)).size() - 1);
        } catch (CursorIndexOutOfBoundsException ignored) {
        }
        try{
            Picasso.get().load(org.getPhotoOrg()).into(imOrg);
        }catch (Exception e){
            imOrg.setImageDrawable(getResources().getDrawable(R.drawable.ava_for_project));
        }
        imOrg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myChatThread.changeBool();
                Bundle bundleForFullDesc = new Bundle();
                bundleForFullDesc.putString("LOG", getArguments().getString("LOG"));
                bundleForFullDesc.putString("NameOrg", getArguments().getString("NameOrg"));
                bundleForFullDesc.putString("PrevFragment", "chat");
                imOrg.setOnClickListener((view1) -> {
                    NavHostFragment.
                            findNavController(ChatFragment.this).navigate(
                            R.id.action_chatFragment_to_fullInfoFragment, bundleForFullDesc);
                });
                imOrg.performClick();
            }
        });
        bt_arrow_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myChatThread.changeBool();
                Bundle bundleLog = new Bundle();
                bundleLog.putString("LOG", getArguments().getString("LOG"));
                bt_arrow_back.setOnClickListener((view1) -> {
                    NavHostFragment.
                            findNavController(ChatFragment.this).navigate(
                            R.id.action_chatFragment_to_listOfChatsFragment, bundleLog);
                });
                bt_arrow_back.performClick();
            }
        });


        nameOrg.setText(org.getName());

        et_msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ivMicro.setImageDrawable(getResources().getDrawable(R.drawable.bt_send_msg));
                ivMicro.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isOnline()) {
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction().add(R.id.fl_chat, new NoInternetConnectionFragment()).commit();
                        } else {
                            String curTime = new SimpleDateFormat(
                                    "HH:mm", Locale.getDefault()).format(new Date());
                            Message myMsg = new Message("person",
                                    openHelper.findChatIdByOrgIdAndPerId(org.getId(), perId), et_msg.getText().toString(),
                                    curTime);

                            openHelper.insertMsg(myMsg);
                            new AppApiVolley(getContext()).addMessage(
                                    openHelper.findLastMessageByChatId(
                                            openHelper.findChatIdByOrgIdAndPerId(org.getId(), perId)));
                            ChatArrayAdapter recyclerAdapter1 = new ChatArrayAdapter(getContext(),
                                    ChatFragment.this, openHelper.
                                    findChatIdByOrgIdAndPerId(org.getId(), perId));
                            rec.setAdapter(recyclerAdapter1);
                            rec.scrollToPosition(openHelper.findMsgByChatId(
                                    openHelper.findChatIdByOrgIdAndPerId(org.getId(), perId)).size() - 1);
                            et_msg.setText("");
                        }
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        myChatThread = new MyChatThread(getContext());
        myChatThread.start();
    }

    public void updateAdapter(){
        OpenHelper openHelper = new OpenHelper(getContext(), "OpenHelper", null,
                OpenHelper.VERSION);
        ChatArrayAdapter recyclerAdapter;
        RecyclerView rec = getActivity().findViewById(R.id.rec_chat);
        int perId = openHelper.findPersonByLogin(
                getArguments().getString("LOG")).getId();
        Organization org = openHelper.findOrgByName(getArguments().getString("NameOrg"));
        recyclerAdapter = new ChatArrayAdapter(getContext(),
                ChatFragment.this, openHelper.findChatIdByOrgIdAndPerId(org.getId(), perId));
        try {
            rec.setAdapter(recyclerAdapter);
        }catch (Exception e){
            Log.e("UPDATE_ADAPTER", e.getMessage());
        }
    }
    class MyChatThread extends Thread {
        private Context context;
        private OpenHelper openHelper;
        private boolean b = true;

        public MyChatThread(Context context) {
            this.context = context;
            openHelper = new OpenHelper(context, "OpenHelper", null, OpenHelper.VERSION);
        }

        @Override
        public void run() {
            try {
                while (b) {
                    new AppApiVolley(context).checkNewMsg();
                    try {
                        sleep(3 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(!b) break;
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                rec.getLayoutManager().onRestoreInstanceState(
                                        rec.getLayoutManager().onSaveInstanceState());
                                updateAdapter();
                            }catch (Exception e){
                                Log.e("UPDATE_ADAPTER", e.getMessage());
                            }
                        }
                    });
                }
            }catch (Exception e){
                Log.e("MSG_THREAD", e.getMessage());
            }
        }

        public void changeBool() {
            b = false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        myChatThread.changeBool();
    }
    public boolean isOnline(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo == null) return false;
        else return networkInfo.isConnected();
    }
}
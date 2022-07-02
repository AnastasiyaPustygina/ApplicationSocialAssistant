package com.example.mainproject.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mainproject.OpenHelper;
import com.example.mainproject.adapter.ChatArrayAdapter;
import com.example.mainproject.adapter.ChatListArrayAdapter;
import com.example.android.multidex.mainproject.R;
import com.example.mainproject.domain.Organization;
import com.example.mainproject.rest.AppApiVolley;

public class ListOfChatsFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatListArrayAdapter chatListArrayAdaptor;
    private MyThread myThread;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.list_of_chats_fragment, container, false);
        recyclerView = view.findViewById(R.id.rec_listOfChats);
        chatListArrayAdaptor = new ChatListArrayAdapter(getContext(),
                ListOfChatsFragment.this, getArguments().getString("LOG"));
        recyclerView.setAdapter(chatListArrayAdaptor);
        AppCompatButton btFav, btProfile, btList, btMap;
        btMap = view.findViewById(R.id.bt_listOfChats_maps);
        btFav = view.findViewById(R.id.bt_listOfChats_fav);
        btProfile = view.findViewById(R.id.bt_listOfChats_profile);
        btList = view.findViewById(R.id.bt_listOfChats_list);
        Bundle bundleLog = new Bundle();
        bundleLog.putString("LOG", getArguments().getString("LOG"));
        btFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btFav.setOnClickListener((view1) -> {
                    NavHostFragment.
                            findNavController(ListOfChatsFragment.this).navigate(
                            R.id.action_listOfChatsFragment_to_favouritesFragment, bundleLog);
                });
                btFav.performClick();
            }
        });
        btProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btProfile.setOnClickListener((view1) -> {
                    NavHostFragment.
                            findNavController(ListOfChatsFragment.this).navigate(
                            R.id.action_listOfChatsFragment_to_mainFragment, bundleLog);
                });
                btProfile.performClick();
            }
        });
        btList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btList.setOnClickListener((view1) -> {
                    NavHostFragment.
                            findNavController(ListOfChatsFragment.this).navigate(
                            R.id.action_listOfChatsFragment_to_listFragment, bundleLog);
                });
                btList.performClick();
            }
        });
        try {
            btMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btMap.setOnClickListener((view1) -> {
                        NavHostFragment.
                                findNavController(ListOfChatsFragment.this).navigate(
                                R.id.action_listOfChatsFragment_to_mapFragment, bundleLog);
                    });
                    btMap.performClick();
                }
            });
        }catch (Exception e){
            Log.d("FavFragment", "Получение разрешения на определение геолокации");
        }

        return view;
    }
    public void updateAdapter(){
        chatListArrayAdaptor.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        myThread = new MyThread(getContext());
        myThread.start();
    }

    class MyThread extends Thread {
        private Context context;
        private OpenHelper openHelper;
        private boolean b = true;

        public MyThread(Context context) {
            this.context = context;
            openHelper = new OpenHelper(context, "OpenHelper", null, OpenHelper.VERSION);
        }

        @Override
        public void run() {
            try {
                while (b) {
                    new AppApiVolley(context).checkNewChat();
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
                                recyclerView.getLayoutManager().onRestoreInstanceState(
                                        recyclerView.getLayoutManager().onSaveInstanceState());
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
        myThread.changeBool();
    }
}
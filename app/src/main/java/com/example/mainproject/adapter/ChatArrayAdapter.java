package com.example.mainproject.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.multidex.mainproject.R;
import com.example.mainproject.OpenHelper;
import com.example.mainproject.fragment.ChatFragment;
import com.example.mainproject.domain.Message;

import java.util.ArrayList;

public class ChatArrayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private LayoutInflater inflater;
    private ChatFragment fragment;
    private ArrayList<Message> allMsg = new ArrayList<>();
    private int width  = Resources.getSystem().getDisplayMetrics().widthPixels;

    public ChatArrayAdapter(Context context, ChatFragment fragment, int chatId) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.fragment = fragment;
        OpenHelper openHelper = new OpenHelper(context, "OpenHelper", null, OpenHelper.VERSION);
        allMsg = openHelper.findMsgByChatId(chatId);
    }

    @Override
    public int getItemViewType(int position) {
        if(allMsg.get(position).getWhose().equals("person")) return 0;
        else return 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View itemLayoutView;
        switch (viewType){
            case 0:
                itemLayoutView = inflater.inflate(R.layout.msg_my, parent, false);
                vh = new HolderZeroType(itemLayoutView);
                break;
            case 1:
                itemLayoutView = inflater.inflate(R.layout.msg_other, parent, false);
                vh = new HolderFirstType(itemLayoutView);
                break;
            default:
                itemLayoutView = inflater.inflate(R.layout.msg_other, parent, false);
                vh = new HolderFirstType(itemLayoutView);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (this.getItemViewType(position)){
            case 0:
                HolderZeroType zero = (HolderZeroType) holder;
                zero.main.setText(allMsg.get(position).getValues());
                zero.time.setText(allMsg.get(position).getTime());
                break;
            case 1:
                HolderFirstType first = (HolderFirstType) holder;
                first.main.setText(allMsg.get(position).getValues());
                first.time.setText(allMsg.get(position).getTime());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return allMsg.size();
    }

    public class HolderZeroType extends RecyclerView.ViewHolder{
        TextView main, time;
        public HolderZeroType(@NonNull View itemView) {
            super(itemView);
            main = itemView.findViewById(R.id.tv_myMsg);
            time = itemView.findViewById(R.id.tv_myMsg_time);
            main.setMaxWidth((int) (width * 0.7));
        }
    }
    public class HolderFirstType extends RecyclerView.ViewHolder{
        TextView main, time;
        public HolderFirstType(@NonNull View itemView) {
            super(itemView);
            main = itemView.findViewById(R.id.tv_otherMsg);
            time = itemView.findViewById(R.id.tv_otherMsg_time);
            main.setMaxWidth((int) (width * 0.7));
        }

    }
}
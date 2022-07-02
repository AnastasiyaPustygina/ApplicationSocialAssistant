package com.example.mainproject.adapter;

import android.content.Context;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mainproject.OpenHelper;
import com.example.android.multidex.mainproject.R;
import com.example.mainproject.domain.Chat;
import com.example.mainproject.fragment.ListOfChatsFragment;
import com.example.mainproject.domain.Organization;
import com.example.mainproject.rest.AppApiVolley;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatListArrayAdapter extends RecyclerView.Adapter<ChatListArrayAdapter.ViewHolder>{

    private Context context;
    private LayoutInflater inflater;
    private ListOfChatsFragment fragment;
    private String name;

    public ChatListArrayAdapter(Context context, ListOfChatsFragment fragment, String name) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.fragment = fragment;
        this.name = name;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.small_chat_window, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try{
        OpenHelper openHelper = new OpenHelper(context, "OpenHelper", null, OpenHelper.VERSION);
        ArrayList<String> arrListLastMsg = openHelper.findLastMsgValuesByPerName(name);
        ArrayList<Integer> arrListChatId = openHelper.findLastChatIdByLogin(name);
        ArrayList<Organization> arrayListLastOrg = new ArrayList<>();

        for (int i = 0; i < arrListChatId.size(); i++) {
            try {
                arrayListLastOrg.add(openHelper.findOrgByChatId(arrListChatId.get(i)));
            } catch (Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        Organization organization = arrayListLastOrg.get(arrayListLastOrg.size() - position - 1);
            try{
                if(organization.getPhotoOrg() == null)
                    holder.ivOrgAva.setImageDrawable(context.getResources().getDrawable(R.drawable.ava_for_project));
                else Picasso.get().load(organization.getPhotoOrg()).into(holder.ivOrgAva);
            }catch (Exception e){
                holder.ivOrgAva.setImageDrawable(context.getResources().getDrawable(R.drawable.ava_for_project));
            }

            holder.lastMsg.setText(arrListLastMsg.get(arrListLastMsg.size() - position - 1));
            holder.tvNameOrg.setText(organization.getName());
            Bundle bundle = new Bundle();
            bundle.putString("LOG", name);
            bundle.putString("NameOrg", arrayListLastOrg.get(arrayListLastOrg.size() - position - 1).
                    getName());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openHelper.insertChat(new Chat(
                            openHelper.findPersonByLogin(name),
                            organization));
                    new AppApiVolley(context).addChat(openHelper.findChatByPersonIdAndOrgId(
                            openHelper.findPersonByLogin(name).getId(),
                            organization.getId()
                    ));
                    holder.itemView.setOnClickListener((view1) -> {
                        NavHostFragment.
                                findNavController(fragment).navigate(
                                R.id.action_listOfChatsFragment_to_chatFragment, bundle);
                    });
                    holder.itemView.performClick();
                }
            });
        } catch (Exception e){
            Log.e("CHAT_LIST_ARRAY_ADAPTER", e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        OpenHelper openHelper = new OpenHelper(context, "OpenHelper", null, OpenHelper.VERSION);
        try{
            return openHelper.findLastMsgValuesByPerName(name).size();
        }catch (CursorIndexOutOfBoundsException e){return 0;}
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView ivOrgAva;
        TextView tvNameOrg, lastMsg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivOrgAva = itemView.findViewById(R.id.iv_SmallChatWin_avaOrg);
            tvNameOrg = itemView.findViewById(R.id.tv_SmallChatWin_nameOfOrg);
            lastMsg = itemView.findViewById(R.id.tv_SmallChatWin_lastMsg);
        }
    }
}
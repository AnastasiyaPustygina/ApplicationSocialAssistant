package com.example.mainproject.rest;

import android.graphics.Bitmap;
import android.icu.text.StringPrepParseException;

import androidx.recyclerview.widget.RecyclerView;

import com.example.mainproject.domain.Chat;
import com.example.mainproject.domain.Message;
import com.example.mainproject.domain.Person;

import java.util.ArrayList;

public interface AppApi {

    void fillPerson();
    void fillOrganization();
    void fillChats();
    void fillMsg();

    void addPerson(Person person);
    void addChat(Chat chat);
    void addMessage(Message message);

    void checkNewMsg();
    void checkNewOrganization();
    void checkNewChat();

    void updatePerson(int id, String telephone, String email, String name, String photoPer,
                      int age, String dateOfBirth, String city, String password, ArrayList<String> favOrg);

}




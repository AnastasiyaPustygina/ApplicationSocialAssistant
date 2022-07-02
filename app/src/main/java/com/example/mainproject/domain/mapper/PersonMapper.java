package com.example.mainproject.domain.mapper;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.mainproject.OpenHelper;
import com.example.mainproject.domain.Person;
import com.example.mainproject.fragment.SignInFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PersonMapper {
    public static Person personFromJson(JSONObject jsonObject, Context context) {
        Person person = null;
        try {
            ArrayList<String> favOrgArray = new ArrayList<>();
            JSONArray favOrgJsonArray = new JSONArray();
            try {
                JSONObject object = new JSONObject(jsonObject.getString("favourite_organization"));
                favOrgJsonArray = object.optJSONArray("fav_org");
                for (int i = 0; i < favOrgJsonArray.length(); i++) {
                    favOrgArray.add(favOrgJsonArray.getString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            OpenHelper openHelper = new OpenHelper(context, "OpenHelper", null, OpenHelper.VERSION);
            String data = jsonObject.getString("telephone").isEmpty() ||
                    jsonObject.getString("telephone").equals("null") ||
                    jsonObject.getString("telephone") == null ?
                    jsonObject.getString("email") : jsonObject.getString("telephone");
            person = new Person(jsonObject.getInt("id"), data,
                    jsonObject.getString("name"), jsonObject.getInt("age"),
                    jsonObject.getString("photo"),
                    jsonObject.getString("date_of_birth"),
                    jsonObject.getString("city"),
                    jsonObject.getString("password"), favOrgArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return person;
    }

    public static Person personFromChatJson(JSONObject jsonObject, Context context) {
        Person person = null;
        try {
            person = personFromJson(jsonObject.getJSONObject("personDto"), context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return person;
    }
}
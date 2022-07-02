package com.example.mainproject.rest;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.CursorIndexOutOfBoundsException;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mainproject.OpenHelper;
import com.example.mainproject.domain.Chat;
import com.example.mainproject.domain.Message;
import com.example.mainproject.domain.Organization;
import com.example.mainproject.domain.Person;
import com.example.mainproject.domain.mapper.ChatMapper;
import com.example.mainproject.domain.mapper.MessageMapper;
import com.example.mainproject.domain.mapper.OrganizationMapper;
import com.example.mainproject.domain.mapper.PersonMapper;
import com.example.mainproject.fragment.MainFragment;
import com.example.mainproject.fragment.SignInFragment;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AppApiVolley implements  AppApi {



    private final Context context;
    public static final String BASE_URL = "http://78.40.217.59:9995";

    private com.android.volley.Response.ErrorListener errorListener;



    public AppApiVolley(Context context) {
        this.context = context;
        errorListener = new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("API_TEST", error.toString());
            }
        };
    }

    @Override
    public void fillPerson() {
        String url = BASE_URL + "/person";
        RequestQueue referenceQueue = Volley.newRequestQueue(context);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        OpenHelper openHelper = new OpenHelper(
                                context, "OpenHelper", null, OpenHelper.VERSION);
                        openHelper.deleteAllPeople();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                Person person = PersonMapper.
                                        personFromJson(jsonObject, context);

                                ArrayList<String> arrListName = new ArrayList<String>();
                                for (int j = 0; j < openHelper.findAllPeople().size(); j++) {
                                    arrListName.add(openHelper.findAllPeople().get(j).getName());
                                }

                                if(!arrListName.contains(person.getName())) {
                                    openHelper.insert(person);
                                }
                                else{
                                    openHelper.changePhotoByPersonLogin(person.getName(), person.getPhotoPer());
                                }
                            }

                        }catch (JSONException e) {
                            Log.e("API_TEST_FILL_PER", e.getMessage());
                        }
                    }
                },
                errorListener);
        referenceQueue.add(jsonArrayRequest);
    }


    @Override
    public void fillOrganization() {
        String url = BASE_URL + "/organization";
        RequestQueue referenceQueue = Volley.newRequestQueue(context);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        OpenHelper openHelper = new OpenHelper(
                                context, "OpenHelper", null, OpenHelper.VERSION);
                        openHelper.deleteAllOrganization();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                Organization organization = OrganizationMapper.
                                        organizationFromJson(jsonObject);

                                ArrayList<String> arrListName = new ArrayList<String>();
                                for (int j = 0; j < openHelper.findAllOrganizations().size(); j++) {
                                    arrListName.add(openHelper.findAllOrganizations().get(j).getName());
                                }

                                if(!arrListName.contains(organization.getName())) {
                                    openHelper.insertOrg(new Organization(organization.getName(),
                                            organization.getType(), organization.getPhotoOrg(),
                                            organization.getDescription(), organization.getAddress(),
                                            organization.getNeeds(), organization.getLinkToWebsite()));
                                }
                                else{
                                    openHelper.changePhotoByOrgLogin(organization.getName(),
                                            organization.getPhotoOrg());
                                    openHelper.changeDescByLog(organization.getName(),
                                            organization.getDescription());
                                    openHelper.changeNeedsByLog(organization.getName(),
                                            organization.getNeeds());
                                }
                            }

                        }catch (JSONException e) {
                            Log.e("API_TEST_FILL_ORG", e.getMessage());
                        }
                    }
                },
                errorListener);
        referenceQueue.add(jsonArrayRequest);
    }

    @Override
    public void addPerson(Person person) {
        String url = BASE_URL + "/person";

        JSONObject params = new JSONObject();
        try {
            params.put("id", person.getId());
            params.put("name", person.getName());
            params.put("telephone", person.getTelephone());
            params.put("email", person.getEmail());
            params.put("city", person.getCity());
            params.put("photo", person.getPhotoPer());
            params.put("date_of_birth", person.getDateOfBirth());
            params.put("age", person.getAge());
            params.put("password", person.getPassword());
            params.put("favourite_organization", person.getArr_fav_org());

        } catch (JSONException e) {
            Log.e("API_TASK_ADD_PER", e.getMessage());
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("API_TEST_ADD_PERSON", response.toString());
            }
        }, errorListener
        );
        RequestQueue referenceQueue = Volley.newRequestQueue(context);
        referenceQueue.add(jsonObjectRequest);
    }

    @Override
    public void updatePerson(int id, String telephone, String email, String name, String photoPer,
                             int age, String dateOfBirth, String city, String password, ArrayList<String> favOrg) {
        String url = BASE_URL + "/person/" + id;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fav_org", new JSONArray(favOrg));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject params = new JSONObject();
        try {
            params.put("id", id);
            params.put("name", name);
            params.put("telephone", telephone);
            params.put("email", email);
            params.put("city", city);
            params.put("photo", photoPer);
            params.put("date_of_birth", dateOfBirth);
            params.put("age", age);
            params.put("password", password);
            params.put("favourite_organization", jsonObject.toString());
        } catch (JSONException e) {
            Log.e("API_TASK_UPD_PER", e.getMessage());
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("API_TEST_UPD_PERSON", response.toString());
            }
        }, errorListener
        );
        RequestQueue referenceQueue = Volley.newRequestQueue(context);
        referenceQueue.add(jsonObjectRequest);


    }



    @Override
    public void addChat(Chat chat) {
        OpenHelper openHelper = new OpenHelper(context, "OpenHelper",
                null, OpenHelper.VERSION);

        try {
            if (openHelper.findChatIdByOrgIdAndPerId(chat.getOrganization().getId(),
                    chat.getPerson().getId()) == -100)
                forAddChat(chat, openHelper);
        } catch (CursorIndexOutOfBoundsException e) {
            forAddChat(chat, openHelper);
        }
    }

    private void forAddChat(Chat chat, OpenHelper openHelper) {
        openHelper.insertChat(chat);
        chat = openHelper.findChatByPersonIdAndOrgId(chat.getPerson().getId(), chat.getOrganization().getId());
        String url = BASE_URL + "/chat";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, getChatJson(chat), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("API_TEST_ADD_CHAT", response.toString());
            }
        }, errorListener
        );
        RequestQueue referenceQueue = Volley.newRequestQueue(context);
        referenceQueue.add(jsonObjectRequest);
    }

    @NonNull
    private JSONObject getChatJson(Chat chat) {
        JSONObject params = new JSONObject();
        SharedPreferences sharedPreferences = SignInFragment.sharedPreferences;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fav_org", new JSONArray(chat.getPerson().getArr_fav_org()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            params.put("id", chat.getId());

            JSONObject person = new JSONObject();
            person.put("id", chat.getPerson().getId());
            person.put("name", chat.getPerson().getName());
            person.put("telephone", chat.getPerson().getTelephone());
            person.put("email", chat.getPerson().getEmail());
            person.put("city", chat.getPerson().getCity());
            person.put("photo", chat.getPerson().getPhotoPer());
            person.put("date_of_birth", chat.getPerson().getDateOfBirth());
            person.put("age", chat.getPerson().getAge());
            person.put("password", chat.getPerson().getPassword());
            person.put("favourite_organization", jsonObject.toString());
            params.put("personDto", person);

            JSONObject org = new JSONObject();
            Organization organization = chat.getOrganization();
            org.put("id", organization.getId());
            org.put("name", organization.getName());
            org.put("type", organization.getType());
            org.put("login", sharedPreferences.getString("org_login" + organization.getAddress(), "Login Not Found!"));
            org.put("organizationPhoto", chat.getOrganization().getPhotoOrg());
            org.put("description", organization.getDescription());
            org.put("address", organization.getAddress());
            org.put("needs", organization.getNeeds());
            org.put("linkToWebsite", organization.getLinkToWebsite());
            org.put("password", sharedPreferences.getString("org_pass" + organization.getAddress(), "Password Not Found!"));



            params.put("organizationDto", org);
        } catch (JSONException e) {
            Log.e("API_TASK_ADD_CHAT", e.getMessage());
        }
        return params;
    }

    @Override
    public void fillChats() {
        String url = BASE_URL + "/chat";
        RequestQueue referenceQueue = Volley.newRequestQueue(context);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        OpenHelper openHelper = new OpenHelper(context,
                                "OpenHelper", null, OpenHelper.VERSION);
                        openHelper.deleteAllChat();
                        Chat chat = null;
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                chat = ChatMapper.chatFromJson(jsonObject, context);
                                openHelper.insertChat(chat);
                            }
                        }catch (JSONException e) {
                            Log.e("API_TEST_FILL_CHAT", e.getMessage());
                        }
                        if(chat != null) Log.d("FILL CHAT", chat.toString() + "");
                    }
                },
                errorListener);
        referenceQueue.add(jsonArrayRequest);
    }

    @Override
    public void checkNewMsg() {
        String url = BASE_URL + "/message/size";
        RequestQueue referenceQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int size = Integer.parseInt(response);
                OpenHelper openHelper = new OpenHelper(context, "OpenHelper",
                        null, OpenHelper.VERSION);
                try{
                    if(openHelper.findAllMsg().size() != size) {
                        fillMsg();
                    }
                }catch (Exception e){
                    Log.e("AppApiCheckNew", e.getMessage());
                }
            }
        }, errorListener);
        referenceQueue.add(stringRequest);
    }
    @Override
    public void checkNewOrganization() {
        String url = BASE_URL + "/organization/size";
        RequestQueue referenceQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int size = Integer.parseInt(response);
                OpenHelper openHelper = new OpenHelper(context, "OpenHelper",
                        null, OpenHelper.VERSION);
                try{
                    if(openHelper.findAllOrganizations().size() != size) {
                        fillOrganization();
                    }
                }catch (Exception e){
                    Log.e("AppApiCheckNewOrg", e.getMessage());
                }
            }
        }, errorListener);
        referenceQueue.add(stringRequest);
    }
    @Override
    public void checkNewChat() {
        String url = BASE_URL + "/chat/size";
        RequestQueue referenceQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int size = Integer.parseInt(response);
                OpenHelper openHelper = new OpenHelper(context, "OpenHelper",
                        null, OpenHelper.VERSION);
                try{
                    if(openHelper.findAllChats().size() != size) {
                        fillChats();
                    }
                    checkNewMsg();
                }catch (Exception e){
                    Log.e("AppApiCheckNewChat", e.getMessage());
                }
            }
        }, errorListener);
        referenceQueue.add(stringRequest);
    }

    @Override
    public void fillMsg() {
        String url = BASE_URL + "/message";
        RequestQueue referenceQueue = Volley.newRequestQueue(context);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        OpenHelper openHelper = new OpenHelper(context, "OpenHelper",
                                null, OpenHelper.VERSION);
                        openHelper.deleteAllMessage();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                Message message = MessageMapper.messageFromJson(jsonObject, context);
                                openHelper.insertMsg(message);

                            }
                        }catch (JSONException e) {
                            Log.e("API_TEST", e.getMessage());
                        }
                    }
                },
                errorListener);
        referenceQueue.add(jsonArrayRequest);
    }

    @Override
    public void addMessage(Message message) {
        String url = BASE_URL + "/message";
        OpenHelper openHelper = new OpenHelper(context, "OpenHelper",
                null, OpenHelper.VERSION);
        JSONObject params = new JSONObject();
        try {
            params.put("id", message.getId());
            params.put("whose", message.getWhose());
            params.put("value", message.getValues());
            params.put("time", message.getTime());
            params.put("chatDto", getChatJson(openHelper.findChatById(message.getChat_id())));
        } catch (JSONException e) {
            Log.e("API_TASK_ADD_MSG", e.getMessage());
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("API_TEST_ADD_MSG", response.toString());
            }
        }, errorListener
        );
        RequestQueue referenceQueue = Volley.newRequestQueue(context);
        referenceQueue.add(jsonObjectRequest);
    }
}
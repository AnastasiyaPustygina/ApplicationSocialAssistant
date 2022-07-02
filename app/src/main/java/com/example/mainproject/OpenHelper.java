package com.example.mainproject;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.mainproject.domain.Chat;
import com.example.mainproject.domain.Message;
import com.example.mainproject.domain.Organization;
import com.example.mainproject.domain.Person;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class OpenHelper extends SQLiteOpenHelper {

    public static final String TABLE_PERSON_NAME = "person";
    public static final String TABLE_ORG_NAME = "organization";
    public static final String COLUMN_PERSON_ID = "idPer";
    public static final String COLUMN_PERSON_CHAT_ID = "idChatPer";
    public static final String COLUMN_ORGANIZATION_ID = "idOrg";
    public static final String COLUMN_ORGANIZATION_CHAT_ID = "idChatOrg";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TELEPHONE = "telephone";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PERSON_PHOTO = "person_photo";
    public static final String COLUMN_ORGANIZATION_PHOTO = "organization_photo";
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_DATE_OF_BIRTH = "date_of_birth";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_AGE = "age";
    public static final String COLUMN_FAV_ORG = "favourite_organizations";
    public static final String COLUMN_ORGNAME = "orgName";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_NEEDS = "needs";
    public static final String COLUMN_LINKWEB = "linkToWebsite";
    public static final String TABLE_CHAT_NAME = "chat";
    public static final String TABLE_MSG_NAME = "message";
    public static final String COLUMN_MSG_WHOSE = "whoseMsg";
    public static final String COLUMN_CHAT_ID = "idChat";
    public static final String COLUMN_MSG_CHAT_ID = "idChat";
    public static final String COLUMN_MSG_ID = "idMsg";
    public static final String COLUMN_MSG_VALUE = "msgValue";
    public static final String COLUMN_MSG_TIME = "msgTime";
    private Context context;
    private static final String APP_PREFERENCES = "my_pref";
    private SharedPreferences sharedPreferences;
    public static final int VERSION = 1;



    public OpenHelper(@Nullable Context context,
                      @Nullable String name,
                      @Nullable SQLiteDatabase.CursorFactory factory,
                      int version) {

        super(context, name, factory, version);
        sharedPreferences = context.getSharedPreferences
                (APP_PREFERENCES, Context.MODE_PRIVATE);
        this.context = context;
        assert context != null;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sharedPreferences = context.getSharedPreferences
                (APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("per_id", 1);
        editor.putInt("org_id", 1);
        editor.putInt("chat_id", 1);
        editor.putInt("msg_id", 1);
        editor.commit();


        String query = "CREATE TABLE " + TABLE_PERSON_NAME + "(" +
                COLUMN_PERSON_ID + " INTEGER, " +
                COLUMN_NAME + " TEXT, "
                + COLUMN_AGE + " INTEGER, " +
                COLUMN_TELEPHONE + " TEXT, "
                + COLUMN_EMAIL + " TEXT, "
                + COLUMN_PERSON_PHOTO + " TEXT, "
                + COLUMN_DATE_OF_BIRTH + " TEXT, "
                + COLUMN_FAV_ORG + " TEXT, "
                + COLUMN_PASSWORD + " TEXT, "
                + COLUMN_CITY + " TEXT);";
        sqLiteDatabase.execSQL(query);

        query = "CREATE TABLE " + TABLE_ORG_NAME + "(" +
                COLUMN_ORGANIZATION_ID + " INTEGER, " +
                COLUMN_ORGNAME + " TEXT, " +
                COLUMN_TYPE + " TEXT, " +
                COLUMN_ORGANIZATION_PHOTO + " TEXT, "
                + COLUMN_DESCRIPTION + " TEXT, "
                + COLUMN_ADDRESS + " TEXT, "
                + COLUMN_NEEDS + " TEXT, "
                + COLUMN_LINKWEB + " TEXT);";
        sqLiteDatabase.execSQL(query);
        query = "CREATE TABLE " + TABLE_CHAT_NAME + "(" +
                COLUMN_CHAT_ID + " INTEGER, " +
                COLUMN_ORGANIZATION_CHAT_ID + " INTEGER, " +
                COLUMN_PERSON_CHAT_ID + " INTEGER);";
        sqLiteDatabase.execSQL(query);

        query = "CREATE TABLE " + TABLE_MSG_NAME + "(" +
                COLUMN_MSG_ID + " INTEGER, " +
                COLUMN_MSG_WHOSE + " TEXT, " +
                COLUMN_MSG_CHAT_ID + " INTEGER, " +
                COLUMN_MSG_VALUE + " TEXT, "
                + COLUMN_MSG_TIME + " TEXT);";
        sqLiteDatabase.execSQL(query);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PERSON_NAME);
        onCreate(db);
    }


    public long insert(Person person) {
        int idPer = sharedPreferences.getInt("per_id", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("per_id", idPer + 1);
        editor.commit();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fav_org", new JSONArray(person.getArr_fav_org()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_PERSON_ID, idPer);
        contentValues.put(COLUMN_NAME, person.getName());
        contentValues.put(COLUMN_FAV_ORG, "");
        contentValues.put(COLUMN_TELEPHONE, person.getTelephone());
        contentValues.put(COLUMN_EMAIL, person.getEmail());
        contentValues.put(COLUMN_PERSON_PHOTO, person.getPhotoPer());
        contentValues.put(COLUMN_CITY, person.getCity());
        contentValues.put(COLUMN_DATE_OF_BIRTH, person.getDateOfBirth());
        contentValues.put(COLUMN_PASSWORD, person.getPassword());
        contentValues.put(COLUMN_AGE, person.getAge());
        contentValues.put(COLUMN_FAV_ORG, jsonObject.toString());
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_PERSON_NAME, null, contentValues);
    }
    public long insertOrg(Organization org) {
        int idOrg = sharedPreferences.getInt("org_id", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("org_id", idOrg + 1);
        editor.commit();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_ORGANIZATION_ID, idOrg);
        contentValues.put(COLUMN_ORGNAME, org.getName());
        contentValues.put(COLUMN_TYPE, org.getType());
        contentValues.put(COLUMN_ORGANIZATION_PHOTO, org.getPhotoOrg());
        contentValues.put(COLUMN_DESCRIPTION, org.getDescription());
        contentValues.put(COLUMN_ADDRESS, org.getAddress());
        contentValues.put(COLUMN_NEEDS, org.getNeeds());
        contentValues.put(COLUMN_LINKWEB, org.getLinkToWebsite());
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_ORG_NAME, null, contentValues);
    }
    public long insertChat(Chat chat){

        int idChat = sharedPreferences.getInt("chat_id", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("chat_id", idChat + 1);
        editor.commit();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_CHAT_ID, idChat);
        contentValues.put(COLUMN_PERSON_CHAT_ID, chat.getPerson().getId());
        contentValues.put(COLUMN_ORGANIZATION_CHAT_ID, chat.getOrganization().getId());
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_CHAT_NAME, null, contentValues);
    }
    public long insertMsg(Message msg) {

        int idMsg = sharedPreferences.getInt("msg_id", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("msg_id", idMsg + 1);
        editor.commit();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_MSG_ID, idMsg);
        contentValues.put(COLUMN_MSG_WHOSE, msg.getWhose());
        contentValues.put(COLUMN_MSG_CHAT_ID, msg.getChat_id());
        contentValues.put(COLUMN_MSG_VALUE, msg.getValues());
        contentValues.put(COLUMN_MSG_TIME, msg.getTime());
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_MSG_NAME, null, contentValues);
    }

    public void deleteAllPeople() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("per_id");
        editor.putInt("per_id", 1);
        editor.commit();
        SQLiteDatabase mDataBase = getWritableDatabase();
        mDataBase.delete(TABLE_PERSON_NAME, null, null);
    }

    public void deleteAllOrganization() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("org_id");
        editor.putInt("org_id", 1);
        editor.commit();
        SQLiteDatabase mDataBase = getWritableDatabase();
        mDataBase.delete(TABLE_ORG_NAME, null, null);
    }
    public void deleteAllChat() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("chat_id");
        editor.putInt("chat_id", 1);
        editor.commit();
        SQLiteDatabase mDataBase = getWritableDatabase();
        mDataBase.delete(TABLE_CHAT_NAME, null, null);
    }
    public void deleteAllMessage() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("msg_id");
        editor.putInt("msg_id", 1);
        editor.commit();
        SQLiteDatabase mDataBase = getWritableDatabase();
        mDataBase.delete(TABLE_MSG_NAME, null, null);
    }



    public String findPassByLogin(String log){
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT " + COLUMN_PASSWORD +
                    " FROM " + TABLE_PERSON_NAME +
                    " WHERE " + COLUMN_NAME + " = ?", new String[]{log});
            cursor.moveToFirst();
            return cursor.getString((cursor.getColumnIndexOrThrow(COLUMN_PASSWORD)));
        } catch (CursorIndexOutOfBoundsException e) {
            return "";
        }
    }
    public Chat findChatByPersonIdAndOrgId(int perId, int orgId){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_CHAT_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
        cursor.moveToFirst();
        do {
            int idPer = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PERSON_CHAT_ID));
            int chat_id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CHAT_ID));
            int org_id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORGANIZATION_CHAT_ID));

            if(idPer == perId && org_id == orgId)
                return new Chat(chat_id, findPersonById(idPer), findOrgById(org_id));
        }while (cursor.moveToNext());
        return new Chat(null, null);
    }
    public ArrayList<String> findFavOrgByLogin(String log){
        SQLiteDatabase db = getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("SELECT " + COLUMN_FAV_ORG +
                    " FROM " + TABLE_PERSON_NAME +
                    " WHERE " + COLUMN_NAME + " = ?", new String[]{log});
            cursor.moveToFirst();
            String fav =  cursor.getString((cursor.getColumnIndexOrThrow(COLUMN_FAV_ORG)));
            ArrayList<String> favOrgArray = new ArrayList<>();
            JSONArray favOrgJsonArray = new JSONArray();
            try {
                JSONObject object = new JSONObject(fav);
                favOrgJsonArray = object.optJSONArray("fav_org");
                for (int i = 0; i < favOrgJsonArray.length(); i++) {
                    favOrgArray.add(favOrgJsonArray.getString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return favOrgArray;
        } catch (CursorIndexOutOfBoundsException e) {
            return new ArrayList<>();
        }
    }
    public void changeDescByLog(String name, String description){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.execSQL("UPDATE " + TABLE_ORG_NAME + " SET " +
                COLUMN_DESCRIPTION + " = '" + description + "' WHERE " + COLUMN_ORGNAME
                + " = '" + name + "'");
    }
    public void changeNeedsByLog(String name, String needs){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.execSQL("UPDATE " + TABLE_ORG_NAME + " SET " +
                COLUMN_NEEDS + " = '" + needs + "' WHERE " + COLUMN_ORGNAME
                + " = '" + name + "'");
    }

    public void changePhotoByPersonLogin(String login, String photo){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.execSQL("UPDATE " + TABLE_PERSON_NAME + " SET " +
                COLUMN_PERSON_PHOTO + " = '" + photo + "' WHERE " + COLUMN_NAME
                + " = '" + login + "'");
    }

    public void changePhotoByOrgLogin(String login, String photo){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.execSQL("UPDATE " + TABLE_ORG_NAME + " SET " +
                COLUMN_ORGANIZATION_PHOTO + " = '" + photo + "' WHERE " + COLUMN_ORGNAME
                + " = '" + login + "'");
    }

    public void changeFavOrg(String log, ArrayList<String> arrayListFavOrg){
        SQLiteDatabase db = getWritableDatabase();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("fav_org", new JSONArray(arrayListFavOrg));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String query = "UPDATE " + TABLE_PERSON_NAME + " SET " +
                    COLUMN_FAV_ORG + " = " + "'" + jsonObject.toString() + "'" + " WHERE "
                    + COLUMN_NAME + " = '" + log + "';";
            db.execSQL(query);
    }


    public Person findPersonByLogin(String log){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.query(TABLE_PERSON_NAME, null, null,
                null, null, null, null);
        cur.moveToFirst();
        if(!cur.isAfterLast()){
            do{
                int id = cur.getInt(cur.getColumnIndexOrThrow(COLUMN_PERSON_ID));
                String pass = cur.getString(cur.getColumnIndexOrThrow(COLUMN_PASSWORD));
                int age = cur.getInt(cur.getColumnIndexOrThrow(COLUMN_AGE));
                String telephone = cur.getString(cur.getColumnIndexOrThrow(COLUMN_TELEPHONE));
                String email = cur.getString(cur.getColumnIndexOrThrow(COLUMN_EMAIL));
                String photo = cur.getString(cur.getColumnIndexOrThrow(COLUMN_PERSON_PHOTO));
                String dateOfBirth = cur.getString(cur.getColumnIndexOrThrow(COLUMN_DATE_OF_BIRTH));
                String city = cur.getString(cur.getColumnIndexOrThrow(COLUMN_CITY));
                String name = cur.getString(cur.getColumnIndexOrThrow(COLUMN_NAME));
                String  favOrg = cur.getString(cur.getColumnIndexOrThrow(COLUMN_FAV_ORG));
                ArrayList<String> arrayList = new ArrayList<>();
                try {
                    JSONArray jsonArray = new JSONObject(favOrg).optJSONArray("fav_org");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        arrayList.add(jsonArray.optString(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(name.equals(log)) {
                    String data = telephone == null ? email : telephone;
                    return new Person(id, data, name, age, photo, dateOfBirth, city, pass, arrayList);
                }
            }while (cur.moveToNext());
        }
        return new Person(0, null, null, 0,  null, null, null, null);
    }
    public Person findPersonById(int idPer){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.query(TABLE_PERSON_NAME, null, null,
                null, null, null, null);
        cur.moveToFirst();
        if(!cur.isAfterLast()){
            do{
                int id = cur.getInt(cur.getColumnIndexOrThrow(COLUMN_PERSON_ID));
                String pass = cur.getString(cur.getColumnIndexOrThrow(COLUMN_PASSWORD));
                int age = cur.getInt(cur.getColumnIndexOrThrow(COLUMN_AGE));
                String telephone = cur.getString(cur.getColumnIndexOrThrow(COLUMN_TELEPHONE));
                String email = cur.getString(cur.getColumnIndexOrThrow(COLUMN_EMAIL));
                String dateOfBirth = cur.getString(cur.getColumnIndexOrThrow(COLUMN_DATE_OF_BIRTH));
                String photo = cur.getString(cur.getColumnIndexOrThrow(COLUMN_PERSON_PHOTO));
                String city = cur.getString(cur.getColumnIndexOrThrow(COLUMN_CITY));
                String name = cur.getString(cur.getColumnIndexOrThrow(COLUMN_NAME));
                String  favOrg = cur.getString(cur.getColumnIndexOrThrow(COLUMN_FAV_ORG));
                ArrayList<String> arrayList = new ArrayList<>();
                try {
                    JSONArray jsonArray = new JSONObject(favOrg).optJSONArray("fav_org");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        arrayList.add(jsonArray.optString(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(id == idPer) {
                    String data = telephone == null ? email : telephone;
                    return new Person(id, data, name, age, photo, dateOfBirth, city, pass, arrayList);
                }
            }while (cur.moveToNext());
        }
        return new Person(-100, null, null, null, 0, null, null, null, null);
    }

    public Organization findOrgByName(String nameOrg){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.query(TABLE_ORG_NAME, null, null,
                null, null, null, null);
        cur.moveToFirst();
        if(!cur.isAfterLast()){
            do{
                int id = cur.getInt(cur.getColumnIndexOrThrow(COLUMN_ORGANIZATION_ID));
                String name = cur.getString(cur.getColumnIndexOrThrow(COLUMN_ORGNAME));
                String type = cur.getString(cur.getColumnIndexOrThrow(COLUMN_TYPE));
                String desc = cur.getString(cur.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                String photo = cur.getString(cur.getColumnIndexOrThrow(COLUMN_ORGANIZATION_PHOTO));
                String address = cur.getString(cur.getColumnIndexOrThrow(COLUMN_ADDRESS));
                String needs = cur.getString(cur.getColumnIndexOrThrow(COLUMN_NEEDS));
                String link = cur.getString(cur.getColumnIndexOrThrow(COLUMN_LINKWEB));

                if(name.equals(nameOrg)) {
                    return new Organization(id, name, type, photo, desc, address, needs, link);
                }
            }while (cur.moveToNext());
        }
        return new Organization(-100, null, null, null,null,
                null, null, null);
    }

    public List<Organization> findOrgByCity(String city){
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Organization> org_array = new ArrayList<>();
        Cursor cur = db.query(TABLE_ORG_NAME, null, null,
                null, null, null, null);
        cur.moveToFirst();
        if(!cur.isAfterLast()){
            do{
                int id = cur.getInt(cur.getColumnIndexOrThrow(COLUMN_ORGANIZATION_ID));
                String name = cur.getString(cur.getColumnIndexOrThrow(COLUMN_ORGNAME));
                String type = cur.getString(cur.getColumnIndexOrThrow(COLUMN_TYPE));
                String desc = cur.getString(cur.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                String photo = cur.getString(cur.getColumnIndexOrThrow(COLUMN_ORGANIZATION_PHOTO));
                String address = cur.getString(cur.getColumnIndexOrThrow(COLUMN_ADDRESS));
                String needs = cur.getString(cur.getColumnIndexOrThrow(COLUMN_NEEDS));
                String link = cur.getString(cur.getColumnIndexOrThrow(COLUMN_LINKWEB));
                String[] arr_address = null, arr_address1 = null, arr_address2 = null;
                try{
                    arr_address = address.split(", ");
                }catch (Exception ignored){}
                try{
                    arr_address1 = address.split(" ");
                }catch (Exception ignored){}
                try{
                    arr_address2 = address.split(",");
                }catch (Exception ignored){}
                try{
                    if(city.equals(arr_address[0]) || city.equals(arr_address[1]) ||
                            city.equals(arr_address1[0]) || city.equals(arr_address1[1]) ||
                            city.equals(arr_address2[0]) || city.equals(arr_address2[1])) {
                        org_array.add(new Organization(id, name, type, photo, desc,
                                address, needs, link));
                    }
                }catch (Exception ignored){}
            }while (cur.moveToNext());
        }
        return org_array;
    }
    public List<Organization> findOrgByType(String typeOrg){
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Organization> org_array = new ArrayList<>();
        Cursor cur = db.query(TABLE_ORG_NAME, null, null,
                null, null, null, null);
        cur.moveToFirst();
        if(!cur.isAfterLast()){
            do{
                int id = cur.getInt(cur.getColumnIndexOrThrow(COLUMN_ORGANIZATION_ID));
                String name = cur.getString(cur.getColumnIndexOrThrow(COLUMN_ORGNAME));
                String type = cur.getString(cur.getColumnIndexOrThrow(COLUMN_TYPE));
                String desc = cur.getString(cur.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                String address = cur.getString(cur.getColumnIndexOrThrow(COLUMN_ADDRESS));
                String needs = cur.getString(cur.getColumnIndexOrThrow(COLUMN_NEEDS));
                String photo = cur.getString(cur.getColumnIndexOrThrow(COLUMN_ORGANIZATION_PHOTO));
                String link = cur.getString(cur.getColumnIndexOrThrow(COLUMN_LINKWEB));

                if(type.equals(typeOrg)) {
                    org_array.add(new Organization(id, name, type, photo, desc, address, needs, link));
                }
            }while (cur.moveToNext());
        }
        return org_array;
    }

    public Organization findOrgByAddress(String addr){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.query(TABLE_ORG_NAME, null, null,
                null, null, null, null);
        cur.moveToFirst();
        if(!cur.isAfterLast()){
            do{
                int id = cur.getInt(cur.getColumnIndexOrThrow(COLUMN_ORGANIZATION_ID));
                String name = cur.getString(cur.getColumnIndexOrThrow(COLUMN_ORGNAME));
                String type = cur.getString(cur.getColumnIndexOrThrow(COLUMN_TYPE));
                String desc = cur.getString(cur.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                String address = cur.getString(cur.getColumnIndexOrThrow(COLUMN_ADDRESS));
                String photo = cur.getString(cur.getColumnIndexOrThrow(COLUMN_ORGANIZATION_PHOTO));
                String needs = cur.getString(cur.getColumnIndexOrThrow(COLUMN_NEEDS));
                String link = cur.getString(cur.getColumnIndexOrThrow(COLUMN_LINKWEB));

                if(address.equals(addr)) {
                    return new Organization(id, name, type, photo,  desc, address, needs, link);
                }
            }while (cur.moveToNext());
        }
        return new Organization(-100, null, null,null, null,
                null, null, null);
    }

    public Organization findOrgById(int idOrg){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.query(TABLE_ORG_NAME, null, null,
                null, null, null, null);
        cur.moveToFirst();
        if(!cur.isAfterLast()){
            do{
                int id = cur.getInt(cur.getColumnIndexOrThrow(COLUMN_ORGANIZATION_ID));
                String name = cur.getString(cur.getColumnIndexOrThrow(COLUMN_ORGNAME));
                String type = cur.getString(cur.getColumnIndexOrThrow(COLUMN_TYPE));
                String desc = cur.getString(cur.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                String address = cur.getString(cur.getColumnIndexOrThrow(COLUMN_ADDRESS));
                String needs = cur.getString(cur.getColumnIndexOrThrow(COLUMN_NEEDS));
                String photo = cur.getString(cur.getColumnIndexOrThrow(COLUMN_ORGANIZATION_PHOTO));
                String link = cur.getString(cur.getColumnIndexOrThrow(COLUMN_LINKWEB));

                if(id == idOrg) {
                    return new Organization(id, name, type, photo, desc,
                            address, needs, link);
                }
            }while (cur.moveToNext());
        }
        return new Organization(-1000, null,null, null,
                null, null, null, null);
    }

    public Chat findChatById(int chat_id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cur = db.query(TABLE_CHAT_NAME, null, null,
                null, null, null, null);
        cur.moveToFirst();
        if(!cur.isAfterLast()){
            do{
                int id = cur.getInt(cur.getColumnIndexOrThrow(COLUMN_CHAT_ID));
                Person person = findPersonById(
                        cur.getInt(cur.getColumnIndexOrThrow(COLUMN_PERSON_CHAT_ID)));
                Organization organization = findOrgById(
                        cur.getInt(cur.getColumnIndexOrThrow(COLUMN_ORGANIZATION_CHAT_ID)));

                if(id == chat_id) {
                    return new Chat(id, person, organization);
                }
            }while (cur.moveToNext());
        }
        return new Chat(null, null);
    }

    public ArrayList<Message> findMsgByChatId(int chatId){
        ArrayList<Message> res = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_MSG_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
        cursor.moveToFirst();
        do {
            String msg = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MSG_VALUE));
            Integer curChatId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MSG_CHAT_ID));
            String whose = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MSG_WHOSE));
            String time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MSG_TIME));
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MSG_ID));
            if(chatId == curChatId) res.add(new Message(id, whose, curChatId, msg, time));
        }while (cursor.moveToNext());
        return res;
    }

    public int findPersonIdByChatId(int chatId){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_CHAT_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
        cursor.moveToFirst();
        do {
            int idPer = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PERSON_CHAT_ID));
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CHAT_ID));

            if(chatId == id) return idPer;
        }while (cursor.moveToNext());
        return -100;
    }
    public ArrayList<String> findLastMsgValuesByPerName(String name){
        ArrayList<String> arr_msgValue = new ArrayList<>();
        ArrayList<Integer> arr_chat_id = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_MSG_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
        cursor.moveToFirst();
        do {
            String msg = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MSG_VALUE));
            Integer chatId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MSG_CHAT_ID));
            if(name.equals(findPersonById(findPersonIdByChatId(chatId)).getName())) {
                if (arr_chat_id.contains((Object) chatId)) {
                    arr_msgValue.remove(arr_chat_id.indexOf(chatId));
                    arr_chat_id.remove((Object) chatId);
                    arr_chat_id.add(chatId);
                    arr_msgValue.add(msg);
                } else {
                    arr_msgValue.add(msg);
                    arr_chat_id.add(chatId);
                }
            }

        }while (cursor.moveToNext());
        return arr_msgValue;
    }
    public ArrayList<Integer> findLastChatIdByLogin(String log){
        ArrayList<Integer> arr_chat_id = new ArrayList<>();
        int perId = findPersonByLogin(log).getId();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_MSG_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
        cursor.moveToFirst();
        do {
            int chatId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MSG_CHAT_ID));
            int id = findPersonIdByChatId(chatId);
            if(id == perId) {
                if (arr_chat_id.contains((Object) chatId)) {
                    arr_chat_id.remove((Object) chatId);
                    arr_chat_id.add(chatId);
                } else {
                    arr_chat_id.add(chatId);
                }
            }
        }while (cursor.moveToNext());
        return arr_chat_id;
    }
    public Organization findOrgByChatId(Integer chat_id){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_CHAT_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
        cursor.moveToFirst();
        do {
            int currentChatId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CHAT_ID));
            int orgId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORGANIZATION_CHAT_ID));
            if(currentChatId == chat_id) return findOrgById(orgId);
        }while (cursor.moveToNext());
        return new Organization(0, null, null, null,null,
                null, null, null);
    }
    public int findChatIdByOrgIdAndPerId(int orgId, int perId){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_CHAT_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
        cursor.moveToFirst();
        do {
            int currentPerId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PERSON_CHAT_ID));
            int currentChatId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CHAT_ID));
            int currentOrgId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORGANIZATION_CHAT_ID));
            if(orgId == currentOrgId && perId == currentPerId) return currentChatId;
        }while (cursor.moveToNext());
        return -100;
    }

    public Message findLastMessageByChatId(int chat_id){
        SQLiteDatabase db = getReadableDatabase();
        Message message = null;
        Cursor cursor = db.query(TABLE_MSG_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
        cursor.moveToFirst();
        do {
            String msg = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MSG_VALUE));
            int chatId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MSG_CHAT_ID));
            String whose = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MSG_WHOSE));
            String time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MSG_TIME));
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MSG_ID));
            if(chatId == chat_id){
                message = new Message(id, whose, chat_id,
                        msg, time);
            }
        }while (cursor.moveToNext());
        return message;
    }

    public List<Person> findAllPeople() {
        List<Person> people = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try{
            Cursor cursor = db.query(TABLE_PERSON_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);
            cursor.moveToFirst();
            int columnIdIndex = cursor.getColumnIndex(COLUMN_PERSON_ID);
            int columnNameIndex = cursor.getColumnIndex(COLUMN_NAME);
            int columnAgeIndex = cursor.getColumnIndex(COLUMN_AGE);
            int columnTelephoneIndex = cursor.getColumnIndex(COLUMN_TELEPHONE);
            int columnEmailIndex = cursor.getColumnIndex(COLUMN_EMAIL);
            int columnDateOfBirthIndex = cursor.getColumnIndex(COLUMN_DATE_OF_BIRTH);
            int columnCityIndex = cursor.getColumnIndex(COLUMN_CITY);
            int columnPasswordIndex = cursor.getColumnIndex(COLUMN_PASSWORD);
            int columnFavIndex = cursor.getColumnIndex(COLUMN_FAV_ORG);

            ArrayList<String> arr_fav_org = new ArrayList<>();
            do {
                int id = cursor.getInt(columnIdIndex);
                String name = cursor.getString(columnNameIndex);
                int age = cursor.getInt(columnAgeIndex);
                String telephone = cursor.getString(columnTelephoneIndex);
                String email = cursor.getString(columnEmailIndex);
                String photo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PERSON_PHOTO));
                String dateOfBirth = cursor.getString(columnDateOfBirthIndex);
                String city = cursor.getString(columnCityIndex);
                String password = cursor.getString(columnPasswordIndex);
                String favOrg = cursor.getString(columnFavIndex);
                arr_fav_org.add(favOrg);
                String data = telephone == null ? email : telephone;
                people.add(new Person(id, data, name, age, photo, dateOfBirth, city, password));
            }while (cursor.moveToNext());
        } catch (Exception e){
            Log.e("FIND_ALL_PEOPLE", e.getMessage());
        };
        return people;
    }

    public List<String> findAllName() {
        List<String> names = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try{
            Cursor cursor = db.query(TABLE_PERSON_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);
            cursor.moveToFirst();
            int columnNameIndex = cursor.getColumnIndex(COLUMN_NAME);

            ArrayList<String> arr_fav_org = new ArrayList<>();
            do {
                String name = cursor.getString(columnNameIndex);
                names.add(name);
            }while (cursor.moveToNext());
        } catch (Exception e){
            Log.e("FIND_ALL_NAME", e.getMessage());
        };
        return names;
    }

    public ArrayList<Organization> findAllOrganizations() {
        ArrayList<Organization> arrOrg = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try{
            Cursor cursor = db.query(TABLE_ORG_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null);
            cursor.moveToFirst();
            int columnIdIndex = cursor.getColumnIndex(COLUMN_ORGANIZATION_ID);
            int columnNameIndex = cursor.getColumnIndex(COLUMN_ORGNAME);
            int columnTypeIndex = cursor.getColumnIndex(COLUMN_TYPE);
            int columnDescIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);
            int columnAddressIndex = cursor.getColumnIndex(COLUMN_ADDRESS);
            int columnNeedsIndex = cursor.getColumnIndex(COLUMN_NEEDS);
            int columnLinkIndex = cursor.getColumnIndex(COLUMN_LINKWEB);
            int columnPhotoIndex = cursor.getColumnIndex(COLUMN_ORGANIZATION_PHOTO);


            do {
                int id = cursor.getInt(columnIdIndex);
                String name = cursor.getString(columnNameIndex);
                String type = cursor.getString(columnTypeIndex);
                String desc = cursor.getString(columnDescIndex);
                String address = cursor.getString(columnAddressIndex);
                String needs = cursor.getString(columnNeedsIndex);
                String photo = cursor.getString(columnPhotoIndex);
                String link = cursor.getString(columnLinkIndex);

                arrOrg.add(new Organization(id, name, type, photo,
                        desc, address, needs, link));
            }while (cursor.moveToNext());
        } catch (Exception e){
            Log.e("MY_LOG", e.getMessage());
        };
        return arrOrg;
    }
    public ArrayList<Chat> findAllChats(){
        ArrayList<Chat> arrChat = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_CHAT_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
        cursor.moveToFirst();
        do {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CHAT_ID));
            int currentPersonId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PERSON_CHAT_ID));
            int currentOrgId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ORGANIZATION_CHAT_ID));

            arrChat.add(new Chat(id, findPersonById(currentPersonId), findOrgById(currentOrgId)));
        }while (cursor.moveToNext());
        return arrChat;
    }
    public ArrayList<Integer> findAllChatIdByLog(String log){
        ArrayList<Integer> arrChat = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_CHAT_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
        cursor.moveToFirst();
        do {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MSG_CHAT_ID));
            String login = findPersonById(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PERSON_CHAT_ID))).getName();

            if(login.equals(log)) arrChat.add(id);
        }while (cursor.moveToNext());
        return arrChat;
    }
    public ArrayList<Message> findAllMsg(){
        ArrayList<Message> arrMsg = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_MSG_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
        cursor.moveToFirst();
        do {
            String msg = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MSG_VALUE));
            int chatId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MSG_CHAT_ID));
            String whose = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MSG_WHOSE));
            String time = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MSG_TIME));
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MSG_ID));
            Message message = new Message(id, whose, chatId ,msg, time);
            arrMsg.add(message);
        }while (cursor.moveToNext());
        return arrMsg;
    }
    public ArrayList<Integer> findAllChatId(){
        ArrayList<Integer> arrChat = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_CHAT_NAME,
                null,
                null,
                null,
                null,
                null,
                null);
        cursor.moveToFirst();
        do {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MSG_CHAT_ID));
            arrChat.add(id);
        }while (cursor.moveToNext());
        return arrChat;
    }
}
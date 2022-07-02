package com.example.mainproject.domain;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.mainproject.fragment.SignInFragment;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Person {
    private int id;
    private String telephone;
    private String email;
    private String name;
    private String photoPer;
    private String dateOfBirth;
    private String city;
    private int age;
    private String password;
    private ArrayList<String> arr_fav_org = new ArrayList<>();

    public Person(int id, String data, String name, int age,
                  String photoPer, String dateOfBirth, String city, String password) {
        this.id = id;
        if(data.contains("@")) this.email = data;
        else this.telephone = data;
        this.name = name;
        this.photoPer = photoPer;
        this.age = age;
        this.dateOfBirth = dateOfBirth;
        this.city = city;
        this.password = password;
    }

    public Person(String data, String name, int age,
                  String photoPer, String dateOfBirth,
                  String city, String password) {
        if(data.contains("@")) this.email = data;
        else this.telephone = data;
        this.name = name;
        this.photoPer = photoPer;
        this.age = age;
        this.dateOfBirth = dateOfBirth;
        this.city = city;
        this.password = password;
    }

    public Person(int id, String telephone, String email, String name,
                  int age, String photoPer, String dateOfBirth, String city, String password) {
        this.id = id;
        this.telephone = telephone;
        this.email = email;
        this.name = name;
        this.photoPer = photoPer;

        this.age = age;
        this.dateOfBirth = dateOfBirth;
        this.city = city;
        this.password = password;
    }
    public Person(int id, String data, String name, int age, String photoPer,
                  String dateOfBirth, String city, String password, ArrayList<String> arr_fav_org) {
        this.id = id;
        if(data.contains("@")) this.email = data;
        else this.telephone = data;
        this.name = name;
        this.photoPer = photoPer;
        this.age = age;
        this.dateOfBirth = dateOfBirth;
        this.city = city;
        this.password = password;
        this.arr_fav_org = arr_fav_org;
    }

    public int getId() {
        return id;
    }
    public String getTelephone() {
        return telephone;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getCity() {
        return city;
    }

    public String getPassword() {
        return password;
    }

    public ArrayList<String> getArr_fav_org() {
        return arr_fav_org;
    }

    public void changeFavOrg(String nameOfOrg){
        if(arr_fav_org.contains(nameOfOrg)) arr_fav_org.remove(nameOfOrg);
        else arr_fav_org.add(nameOfOrg);
    }

    public String getPhotoPer() {
        return photoPer;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", telephone='" + telephone + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", photoPer='" + photoPer + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", city='" + city + '\'' +
                ", age=" + age +
                ", arr_fav_org=" + arr_fav_org +
                '}';
    }
}
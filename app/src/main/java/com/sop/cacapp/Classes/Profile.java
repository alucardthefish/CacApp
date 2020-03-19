package com.sop.cacapp.Classes;

import com.google.firebase.Timestamp;

import java.util.Date;

public class Profile {

    private String name;
    private String email;
    private int age;
    private int height;
    private double weight;
    private String gender;
    private Timestamp registerDate;

    public Profile(){

    }

    public Profile(String name, String email, int age, int height, double weight, String gender) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
        this.registerDate = new Timestamp(new Date());
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getAge() {
        return age;
    }

    public int getHeight() {
        return height;
    }

    public double getWeight() {
        return weight;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Timestamp getRegisterDate() {
        return registerDate;
    }
}

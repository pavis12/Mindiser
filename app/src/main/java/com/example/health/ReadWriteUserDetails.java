package com.example.health;

public class ReadWriteUserDetails {
    public String fullname,dob,gender,mobile;
    //constructor
    public ReadWriteUserDetails(){};
    public ReadWriteUserDetails(String fullname,String textdob,String textGender,String textMobile){
        this.fullname=fullname;
        this.dob=textdob;
        this.gender=textGender;
        this.mobile=textMobile;
    }
}

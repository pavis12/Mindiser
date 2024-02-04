package com.example.health;

public class ReadWriteUserDetails {
    public String fullname,dob,gender,mobile,last_login;
    //constructor
    public ReadWriteUserDetails(){};
    public int awesome,good,okay,bad,terrible;
    public ReadWriteUserDetails(String fullname,String textdob,String textGender,String textMobile,String last_login,int awesome,int good,int okay,int bad,int terrible){
        this.fullname=fullname;
        this.dob=textdob;
        this.gender=textGender;
        this.mobile=textMobile;
        this.awesome=awesome;
        this.good=good;
        this.okay=okay;
        this.bad=bad;
        this.terrible=terrible;
        this.last_login=last_login;
    }
}

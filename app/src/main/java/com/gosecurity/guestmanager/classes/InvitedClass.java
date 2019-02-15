package com.gosecurity.guestmanager.classes;

public class InvitedClass {

    public String name;
    public String last_name;
    public String event;
    public String invited_by;

    public InvitedClass(){

    }


    public InvitedClass( String name ,String last_name , String event , String invited_by){

        this.name = name;
        this.last_name = last_name;
        this.event = event;
        this.invited_by = invited_by;
    }

    @Override
    public String toString(){

        return  this.name + " " + this.last_name;
    }
}

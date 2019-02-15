package com.gosecurity.guestmanager.classes;

public class EventClass {

    public String id;
    public String name;

    public EventClass(){
    }

    public EventClass(String id , String name){
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString(){
        return  this.name;
    }
}

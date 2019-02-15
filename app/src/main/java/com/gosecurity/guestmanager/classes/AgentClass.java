package com.gosecurity.guestmanager.classes;

public class AgentClass {


    public String id;
    public String name;
    public String last_name;

    public AgentClass(){
    }

    public AgentClass(String id , String name , String last_name){
        this.id = id;
        this.name = name;
        this.last_name = last_name;
    }

    @Override
    public String toString(){
       return  this.name + " " +this.last_name;
    }


}
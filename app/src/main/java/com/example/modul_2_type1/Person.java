package com.example.modul_2_type1;

public class Person
{
    private int Age;

    private int Height;
    private String gender;
    private  int Weight;
    private double  DistanceGoal=0;
    public Person(int Age,int Height,int Weight,String gender,double DistanceGoal){
        this.Age=Age;
        this.Height=Height;
        this.Weight=Weight;
        this.gender=gender;
        this.DistanceGoal=DistanceGoal;
    }
    public void setGoal(double Goal){this.DistanceGoal=Goal;}
    public void setAge(int Age){
        this.Age=Age;
    }
    public void setGender(String Gender){
        this.gender=Gender;
    }
    public void setWeight(int Weight){
        this.Weight=Weight;
    }
    public void setHeight(int Height){
        this.Height=Height;
    }
    public int getAge(){
        return this.Age;
    }
    public int getHeight(){
        return this.Height;
    }
    public int getWeight(){
        return this.Weight;
    }
    public String getGender(){
        return this.gender;
    }
    public double getGoal(){
        return this.DistanceGoal;
    }
}

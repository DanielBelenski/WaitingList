package com.example.waitinglist.database.model;

public class Student {

    //name of the table this model is used for
    public static final String TABLE_NAME = "Students";

    //immutable strings of column identifiers to minimize typos
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_NAME = "NAME";
    public static final String COLUMN_COURSE = "COURSE";
    public static final String PRIORITY = "PRIORITY";

    //attributes of the student object, these correspond to database columns
    private int id;
    private String name;
    private String info;
    private int priority;

    //String to create the database table, to improve readability
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NAME + " TEXT,"
            + COLUMN_COURSE + " TEXT,"
            + PRIORITY + " INTEGER)";

    //constructor for blank student object
    public Student(){

    }

    //constructor for student object with all information
    public Student(int id, String name, String info, int priority){
        this.id = id;
        this.name = name;
        this.info = info;
        this.priority = priority;
    }

    //returns the id number of a student object
    public int getId(){return id;}

    //returns the name of a student object
    public String getStudent() {
        return name;
    }

    //set the name of a student object
    public void setStudent(String note) {
        this.name = note;
    }

    //set the id number of a student object
    public void setId(int id) {
        this.id = id;
    }

    //returns course info of the student object
    public String getCourse(){
        return info;
    }

    //set course info for student object
    public void setCourse(String course){
        this.info = course;
    }

    //get priority status of a student object
    public int getPriority(){
        int priority = this.priority;
        return (priority);
    }

    //set priority status of a student object
    public void setPriority(int priority){
        this.priority = priority;
    }
}

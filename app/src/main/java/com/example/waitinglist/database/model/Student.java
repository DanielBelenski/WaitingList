package com.example.waitinglist.database.model;

public class Student {
    public static final String TABLE_NAME = "Students";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "Student_Name";
    public static final String COLUMN_COURSE = "Course_Info";
    public static final String PRIORITY = "Priority";

    private int id;
    private String name;
    private String info;
    private int priority;

    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NAME + "TEXT,"
            + COLUMN_COURSE + "TEXT,"
            + PRIORITY + "INTEGER)";

    public Student(){

    }

    public Student(int id, String name, String info, boolean priority){
        this.id = id;
        this.name = name;
        this.info = info;
        this.priority = priority ? 1 : 0;
    }

    public int getId(){return id;}

    public String getStudent() {
        return name;
    }

    public void setStudent(String note) {
        this.name = note;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCourse(){
        return info;
    }

    public void setCourse(String course){
        this.info = course;
    }

    public boolean getPriority(){
        int priority = this.priority;
        return (priority == 1);
    }

    public void setPriority(boolean priority){
        if (priority){
            this.priority = 1;
        }
        else{
            this.priority = 0;
        }
    }
}

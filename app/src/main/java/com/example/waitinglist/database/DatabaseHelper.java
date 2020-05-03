package com.example.waitinglist.database;

//imports
import android.content.ContentValues;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.waitinglist.database.model.Student;

import java.util.ArrayList;
import java.util.List;

//class for interracting with the database, using elemsnets from the Student model
public class DatabaseHelper extends SQLiteOpenHelper {

    //database version for database creation
    public static final int DATABASE_VERSION = 1;

    //database name for database creation
    public static final String DATABASE_NAME = "Students.db";

    //creates database and object to interact with it
    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //creates student table using static string from student model
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(Student.CREATE_TABLE);
    }

    //method to upgrade the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + Student.TABLE_NAME);
    }

    //method to insert a student objects information into the database
    public long insertStudent(String name, String course, int priority){
        //get database
        SQLiteDatabase db = this.getWritableDatabase();

        //group of values for database interaction
        ContentValues values = new ContentValues(3);

        //putting in values and their column keys
        values.put(Student.COLUMN_NAME, name);
        values.put(Student.COLUMN_COURSE, course);
        values.put(Student.PRIORITY,priority);

        //inserting the new student and getting the id for the new entry
        long id = db.insert(Student.TABLE_NAME, null, values);

        //close database
        db.close();

        //return the id of the new entry
        return id;
    }

    //retrieve a students info from the database and return a student object with that info
    public Student getStudent(long id){
        //get database
        SQLiteDatabase db = this.getReadableDatabase();

        //query the database for entries whose id matches the input
        Cursor cursor = db.query(Student.TABLE_NAME,
                new String[]{Student.COLUMN_ID, Student.COLUMN_NAME, Student.COLUMN_COURSE, Student.PRIORITY},
                Student.COLUMN_ID+" =?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        //if the entry exists
        if (cursor != null){
            //select first result
            cursor.moveToFirst();
        }

        //create a student object and taking the values from the database query
        Student student = new Student(
                cursor.getInt(cursor.getColumnIndex(Student.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(Student.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(Student.COLUMN_COURSE)),
                cursor.getInt(cursor.getColumnIndex(Student.PRIORITY)));

        //close cursor
        cursor.close();
        //close database
        db.close();

        //return desired entry as a student object
        return student;
    }

    //remove a student from the database
    public void deleteStudent(Student student){
        //get database
        SQLiteDatabase db = this.getWritableDatabase();
        //delete entry from database
        db.delete(Student.TABLE_NAME, Student.COLUMN_ID + "=?",new String[]{String.valueOf(student.getId())});
        //close database
        db.close();
    }

    //method to get a list of all students in the database, listed by priority
    public List<Student> getAllStudents(){
        //create list to hold results
        List<Student> students = new ArrayList<>();

        //query for all entries, sorted by priority
        String selectQuery = "SELECT * FROM " + Student.TABLE_NAME + " ORDER BY " + Student.PRIORITY + " DESC";

        //get database
        SQLiteDatabase db = this.getWritableDatabase();
        //query
        Cursor cursor = db.rawQuery(selectQuery, null);

        //create a student object for all entries and add them to the list
        if (cursor.moveToFirst()){
            do{
                Student student = new Student();
                student.setId(cursor.getInt(cursor.getColumnIndex(Student.COLUMN_ID)));
                student.setStudent(cursor.getString(cursor.getColumnIndex(Student.COLUMN_NAME)));
                student.setCourse(cursor.getString(cursor.getColumnIndex(Student.COLUMN_COURSE)));
                student.setPriority(cursor.getInt(cursor.getColumnIndex(Student.PRIORITY)));

                students.add(student);
            } while(cursor.moveToNext());
        }

        //close database
        db.close();

        //return list of students
        return students;
    }

    //get total number of student records
    public int getStudentCount(){
        //write query to get all entries from database
        String countQuery = "SELECT * FROM " + Student.TABLE_NAME;
        //get database
        SQLiteDatabase db = this.getReadableDatabase();
        //query database
        Cursor cursor = db.rawQuery(countQuery, null);

        //get number of entries returned
        int count = cursor.getCount();
        //close cursor
        cursor.close();

        //return number of entries in the database
        return count;
    }

    //method to update a students information in the database
    public int updateStudent(Student student){
        //get database
        SQLiteDatabase db = this.getWritableDatabase();

        //values and column name pairs for the update query
        ContentValues values = new ContentValues();
        values.put(Student.COLUMN_NAME, student.getStudent());
        values.put(Student.COLUMN_COURSE, student.getCourse());
        values.put(Student.PRIORITY, student.getPriority());

        //update selected entry
        return db.update(Student.TABLE_NAME, values, Student.COLUMN_ID + " = ?",
                new String[]{String.valueOf(student.getId())});
    }


}

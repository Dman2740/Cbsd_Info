package com.example.cbsdinfo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;



public class DatabaseHelper extends SQLiteOpenHelper
{
    /*
     * This is to initialize the table attributes with the column names and table name
     */
    Context context;
    private static final String DATABASE_NAME = "radioInfo.db";
    private static final String TABLE_NAME = "RadioInformation";
    private static final String TAG = "DatabaseHelper";
    private static final String COL1 = "UI";
    private static final String COL2 = "ID";
    private static final String COL3 = "Site";
    private static final String COL4 = "State";
    private static final String COL5 = "Latitude";
    private static final String COL6 = "Longitude";
    private static final String COL7 = "grantId";
    private static final String COL8 = "lowFrequency";
    private static final String COL9 = "highFrequency";
    private static final String COL10 = "maxEirp";
    private static final String COL11="grantedTimestamp";

    private static final String TABLE2_NAME="login_information";
    private static final String TAG2="UserDatabaseHelper";
    private static final String NAME="NAME";
    private static final String ID="ID";
    private static final String EMAIL="EMAIL";
    private static final String PHONENUM="PHONENUMBER";
    private static final String PASSWORD="PASSWORD";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
        this.context = context;
    }

    /*
     * This is to create the table with the various columns and their type is text
     */
    @Override
    public void onCreate(SQLiteDatabase db)
    {

        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL2 + " TEXT, "
                + COL3 + " TEXT, "
                + COL4 + " TEXT, "
                + COL5 + " DOUBLE, "
                + COL6 + " DOUBLE, "
                + COL7 + " TEXT, "
                + COL8 + " TEXT, "
                + COL9 + " TEXT, "
                + COL10 + " DOUBLE, "
                + COL11 + " TEXT" + ")";

        String CREATE_TABLE2 = "CREATE TABLE " + TABLE2_NAME + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME + " TEXT, "
                + EMAIL + " TEXT, "
                + PHONENUM+ " TEXT, "
                + PASSWORD + " TEXT" + ")";

        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_TABLE2);
        //Log.d(TAG, "Database was created successfully");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE2_NAME);
        onCreate(db);
    }

    /*
     * I am now populating the database with the fields that were entered by the user
     * Also for error checking I am making sure that the data was entered correctly
     */
    public boolean insertData(String id, String location, String state, double latitude, double longitude,String grantId, String lowFrequency, String highFrequency,double maxEirp, String grantedTimestamp)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(COL2,id);
        contentValues.put(COL3,location);
        contentValues.put(COL4,state);
        contentValues.put(COL5,latitude);
        contentValues.put(COL6,longitude);
        contentValues.put(COL7,grantId);
        contentValues.put(COL8,lowFrequency);
        contentValues.put(COL9,highFrequency);
        contentValues.put(COL10,maxEirp);
        contentValues.put(COL11,grantedTimestamp);
        long result=db.insert(TABLE_NAME,null,contentValues);
        if(result==-1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean insertUser(String namey,String email,String pn,String pw)
    {
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(NAME,namey);
        cv.put(EMAIL,email);
        cv.put(PHONENUM,pn);
        cv.put(PASSWORD,pw);
        //Log.d(TAG2,"insertUser:Adding "+namey+" "+email+" "+pn+" "+pw+" "+" "+" to "+ TABLE2_NAME);
        long result=db.insert(TABLE2_NAME,null,cv);
        if(result==-1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public boolean checkEmail(String email)
    {
        SQLiteDatabase db=getReadableDatabase();
        Cursor emailFinder=db.query(TABLE2_NAME,null,"EMAIL = ?",new String[]{String.valueOf(email)},null,null,null);
        if(emailFinder.getCount()>0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public String checkCredentials(String email)
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM login_information where EMAIL = ?",new String[]{email});
        String a,b;
        b="NOT FOUND";
        if(cursor.moveToFirst())
        {
            do {
                a=cursor.getString(2);

                if(a.equals(email))
                {
                    b=cursor.getString(4);
                    break;
                }

            }
            while(cursor.moveToNext());
        }
        return b;
    }


    public boolean noDuplicate(String id)
    {
        SQLiteDatabase db=getReadableDatabase();
        Cursor duplicateFinder=db.query(TABLE_NAME,null,"ID = ?",new String[]{String.valueOf(id)},null,null,null);
        if(duplicateFinder.getCount()>0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public Cursor getRadioData()
    {
        SQLiteDatabase db=this.getReadableDatabase();
        String query="SELECT * FROM "+TABLE_NAME;
        Cursor data=db.rawQuery(query,null);
        return data;
    }

    public Cursor getState(String state) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor=db.rawQuery("SELECT * FROM " + TABLE_NAME +
                " WHERE State =?",new String[]{String.valueOf(state)});
        return cursor;
    }

}

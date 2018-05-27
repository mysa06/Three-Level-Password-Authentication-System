package ysnyldrm.com.mysa;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteHelper2 extends SQLiteOpenHelper {

    //DATABASE NAME
    public static final String DATABASE_NAME = "mysadatabase2";

    //DATABASE VERSION
    public static final int DATABASE_VERSION = 1;

    //TABLE NAME
    public static final String TABLE_USERS2 = "users2";


    //TABLE USERS COLUMNS
    public static final String KEY_ID = "id";
    public static final String KEY_VENDORID = "vendorid";
    public static final String KEY_PRODUCTID = "productid";
    public static final String KEY_GUID = "guid";
    public static final String KEY_RANDOMSTR = "randomstr";

    public static final String SQL_TABLE_USERS2 = " CREATE TABLE " + TABLE_USERS2
            + " ( "
            + KEY_ID + " INTEGER PRIMARY KEY, "
            + KEY_VENDORID + " TEXT, "
            + KEY_PRODUCTID + " TEXT, "
            + KEY_GUID + " TEXT, "
            + KEY_RANDOMSTR + " TEXT"
            + " ) ";

    public static String number = "";

    public SqliteHelper2(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Create Table when oncreate gets called
        sqLiteDatabase.execSQL(SQL_TABLE_USERS2);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //drop table to create new one if database version updated
        sqLiteDatabase.execSQL(" DROP TABLE IF EXISTS " + TABLE_USERS2);
    }

    //using this method we can add users to user table
    public void addUser(User2 user2) {


        //get writable database
        SQLiteDatabase db = this.getWritableDatabase();

        //create content values to insert
        ContentValues values = new ContentValues();

        values.put(KEY_VENDORID, user2.vendorid);
        values.put(KEY_PRODUCTID, user2.productid);
        values.put(KEY_GUID, user2.guid);
        values.put(KEY_RANDOMSTR, user2.randomstr);

        // insert row
        long todo_id = db.insert(TABLE_USERS2, null, values);
    }



    public boolean isOTGexists(String keyid) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS2,// Selecting Table
                new String[]{KEY_ID, KEY_VENDORID, KEY_PRODUCTID, KEY_GUID, KEY_RANDOMSTR},//Selecting columns want to query
                KEY_ID + "=?",
                new String[]{keyid},//Where clause
                null, null, null);


        if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {

            //if cursor has value then in user database there is user associated with this given email so return true
            return true;
        }

        //if email does not exist return false
        return false;
    }


    public String getRandomStr() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS2,// Selecting Table
                new String[]{KEY_ID, KEY_VENDORID, KEY_PRODUCTID, KEY_GUID, KEY_RANDOMSTR},//Selecting columns want to query
                KEY_ID + "=?",
                new String[]{"1"},//Where clause
                null, null, null);


        if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
            //if cursor has value then in user database there is user associated with this given email
            User2 user3 = new User2(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
            //Match both passwords check they are same or not

            return user3.randomstr;
        }
        else
            return null;


    }

    public String getGuid() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS2,// Selecting Table
                new String[]{KEY_ID, KEY_VENDORID, KEY_PRODUCTID, KEY_GUID, KEY_RANDOMSTR},//Selecting columns want to query
                KEY_ID + "=?",
                new String[]{"1"},//Where clause
                null, null, null);


        if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
            //if cursor has value then in user database there is user associated with this given email
            User2 user3 = new User2(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
            //Match both passwords check they are same or not

            return user3.vendorid;
        }
        else
            return null;


    }



}
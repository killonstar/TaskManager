package greenrabbit.taskmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;

/**
 * Created by Sergey on 18.07.2018.
 */

public class DataBase implements BaseColumns{

    private static final String DB_NAME = "task_manager.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "library";

    public static final String column_id = BaseColumns._ID;
    public static final String column_title = "title";
    public static final String column_status = "status";
    public static final String column_note = "note";
    public static final String column_date = "date";

    private static final String DB_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    column_id + " integer primary key autoincrement, " +
                    column_title + " text not null, " +
                    column_status + " text not null, " +
                    column_note + " text not null, " +
                    column_date + " text not null);";

    private final Context context;
    private DBHelper dbHelper;
    private SQLiteDatabase sqLiteDatabase;

    public DataBase(Context context) {
        this.context = context;
    }

    // Open DataBase
    public void openDB(){
        dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
        sqLiteDatabase = dbHelper.getWritableDatabase();
    }

    // Close DataBase
    public void closeDB(){
        if(dbHelper != null) dbHelper.close();
    }

    // Get data
    public Cursor getAllData(){
        return sqLiteDatabase.query(TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                DataBase.column_id + " DESC");
    }
    // Sort data
    public Cursor sortData(String status){
        return sqLiteDatabase.query(TABLE_NAME,
                null,
                column_status + " = ?",
                new String[] {status},
                null,
                null,
                DataBase.column_id + " DESC");
    }
    // Add line in DB
    public void addRec(String t, String s, String n, String d){
        ContentValues cv = new ContentValues();
        cv.put(column_title, t);
        cv.put(column_status, s);
        cv.put(column_note, n);
        cv.put(column_date, d);
        sqLiteDatabase.insert(TABLE_NAME, null, cv);
    }
    // Widget data
    public ArrayList<String> widgetData(){
        ArrayList<String> detaillist = new ArrayList<>();
        Cursor c = sqLiteDatabase.query(TABLE_NAME,
                null,
                column_status + " = ?",
                new String[] {"New task"},
                null,
                null,
                DataBase.column_id + " DESC");

        while(c.moveToNext()){
            int titleColIndex = c.getColumnIndex(column_title);
            int statusColIndex = c.getColumnIndex(column_status);
            detaillist.add(c.getString(titleColIndex));
            detaillist.add(c.getString(statusColIndex));
        }
        c.close();
        return detaillist;
    }

    // Delete Row
    public void delRec(long id){
        sqLiteDatabase.delete(TABLE_NAME, column_id + " = " + id, null);
    }

    // Delete Row All
    public void delRecAll(){
        sqLiteDatabase.delete(TABLE_NAME, null, null);
    }

    // Edit Row
    public void editRec(String t, String s, String n, String d, String id){
        ContentValues cv = new ContentValues();
        cv.put(column_title, t);
        cv.put(column_status, s);
        cv.put(column_note, n);
        cv.put(column_date, d);
        sqLiteDatabase.update(TABLE_NAME, cv, column_id + " = ?", new String[] {id});
    }

    // Details Row
    public ArrayList detailsRec(long id){
        ArrayList<String> detaillist = new ArrayList<>();
        Cursor c = sqLiteDatabase.query(TABLE_NAME, null,column_id + " = ?",
                new String[] {Long.toString(id)},null,null,null);

        while(c.moveToNext()){
            int idColIndex = c.getColumnIndex(column_id);
            int titleColIndex = c.getColumnIndex(column_title);
            int statusColIndex = c.getColumnIndex(column_status);
            int noteColIndex = c.getColumnIndex(column_note);
            int dateColIndex = c.getColumnIndex(column_date);
            detaillist.add(String.valueOf(c.getInt(idColIndex)));
            detaillist.add(c.getString(titleColIndex));
            detaillist.add(c.getString(statusColIndex));
            detaillist.add(c.getString(noteColIndex));
            detaillist.add(c.getString(dateColIndex));
        }
        c.close();
        return detaillist;
    }

    public class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }
}

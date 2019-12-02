package fun.zzti.memoire;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
//新建一个类DatabaseHelper继承自SQLiteOpenHelper
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_MEMO = "create table Memory (" +
            "id integer primary key autoincrement, " +////primary key 将id列设为主键    autoincrement表示id列是自增长的
            "title text, " +
            "content text, " +
            "date text," +
            "uri text)";
    private Context mContext;
    //构造方法：第一个参数Context，第二个参数数据库名，第三个参数cursor允许我们在查询数据的时候返回一个自定义的光标位置，
    // 一般传入的都是null，第四个参数表示目前库的版本号（用于对库进行升级）

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    //调用SQLiteDatabase中的execSQL（）执行建表语句。
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_MEMO);
    }

    @Override
    //onUpgrade方法会在数据库需要升级的时候调用。可以用来增删表或者其他任何操作。
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
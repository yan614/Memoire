package fun.zzti.memoire;


import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //注册组件
    private Button add;
    private Button search;
    private DatabaseHelper databaseHelper;
    private ArrayList<Memory> items;//定义一个数组items
    private ListView list;
    private EditText search_content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //创建Memo数据库
        databaseHelper = new DatabaseHelper(this,"Memo.db", null, 1);
        list = (ListView)findViewById(R.id.list);
        //显示数据库的内容到复杂列表中
        show("");
        //创建长按弹出contextmenu菜单
        list.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                //menu.setHeaderTitle("选择操作");
                //组概念，项目编号，顺序，选项中显示的文字
                menu.add(0,0,0, "查看详情");
                menu.add(0,1,0, "删除");
            }
        });
        add = (Button)findViewById(R.id.add);
        //点击ADD按钮，跳转到添加界面
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //新建一个intent，跳转到添加界面
                Intent intent = new Intent(MainActivity.this, Add_memo.class);
                startActivity(intent);//启动Add_meno
            }
        });
        search_content = (EditText)findViewById(R.id.search_content);
        search = (Button)findViewById(R.id.search);
        //点击SEARCH按钮，搜索内容并显示
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = search_content.getText().toString();//将搜索栏中输入的文字赋给str
                ArrayList<Memory> al = query(str);//创建了一个动态的数组al
                if(al.size()>0) {
                    QueryAdapter queryAdapter = new QueryAdapter(MainActivity.this, al);
                    list.setAdapter(queryAdapter);
                }
                else {
                    Toast.makeText(MainActivity.this, "查找不到相关内容",Toast.LENGTH_SHORT).show();
                    show("");
                    search_content.setText("");
                }
            }
        });
    }
    //查询数据库
    public ArrayList<Memory> query(String str) {
        ArrayList<Memory> list = new ArrayList<>();
        //创建或打开可读的数据库
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor;
        if(str.equals(""))
            //cursor对select到的语句进行遍历
            cursor = db.rawQuery("select * from memory order by date desc",null);//select语句，zhanweifu
        else {  //模糊查找关键字
            //搜索在任何位置包含str的所有字符串
            cursor = db.rawQuery("select * from memory where content like '%" + str + "%' or date like '%" + str + "%' order by date desc", null);
            //输出语句
            Log.d("db", "sql is " + "select * from memory where content like '%" + str + "%'or date like '%" + str + "%' order by date desc");
        }
        //若数据库符合条件，创建对象封装数据
        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            String uri = cursor.getString(cursor.getColumnIndex("uri"));
            Memory memo = new Memory(id, title, content,date, uri);
            list.add(memo);
        }
        cursor.close();
        db.close();
        return list;
    }

    public void show(String str) {
        items = query(str);
        QueryAdapter queryAdapter = new QueryAdapter(MainActivity.this, items);
        list.setAdapter(queryAdapter);
    }
    //长按弹出菜单的设置
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case 0:     //若点击第一个菜单，查看详情
                more(items.get(info.position));
                return true;
            case 1:     //若点击第二个菜单，删除内容
                deleteData(items.get(info.position));
                show("");
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
    //查看详情
    private void more(Memory memo) {
        Intent intent = new Intent(MainActivity.this, Add_memo.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("memo",memo);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    //删除数据库中相应记录
    private void deleteData(Memory memo) {
        String sql = "delete from memory where id = ?";
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        db.execSQL(sql, new Integer[]{memo.getId()});
    }

}

package com.example.tuan10;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Console;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/*Activity sử dụng cho ListView*/
public class MainActivity extends AppCompatActivity {

    //database
    final String DATABASE_NAME = "notes.sqlite";
    SQLiteDatabase database;

    //adapter
    ListView listView;
    ArrayList<GhiChu> list;
    Adapter adapter;

    //lấy object cơ bản
    TextView txt_editedAt, txt_createdAt;
    EditText editMa,editTen,editSdt,editDiach;

    Button btnThem;
    FloatingActionButton fab;

    //Intent - Bundle
    final static String ID = "ID";
    final static String BUNDLE="BUNDLE";

    //Notification
    private Notification.Builder notBuilder;
    private static final int MY_NOTIFICATION_ID = 12345;
    private static final int MY_REQUEST_CODE = 100;

    //backup
    final int UPDATE_RATE = 5000;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        database = Database.initDatabase(this,DATABASE_NAME);
        Cursor cursor = database.rawQuery("SELECT * FROM notes",null);
        cursor.moveToFirst();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setControl();
        setEvent();

        handler = new Handler();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(myRunnable,UPDATE_RATE);
    }

    public void setControl()
    {
        txt_createdAt = findViewById(R.id.txt_createdAt);
        txt_editedAt = findViewById(R.id.txt_editedAt);


        //listView của project
        listView =(ListView) findViewById(R.id.listView);
        list = new ArrayList<>();
        adapter = new Adapter(this,list);
        listView.setAdapter(adapter);

        //thanh action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("NotePro");
        floatting();
        getdata();

        this.notBuilder = new Notification.Builder(this);

        // Thông báo sẽ tự động bị hủy khi người dùng click vào Panel
        this.notBuilder.setAutoCancel(true);
    }

    public void floatting()
    {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this,ThemGhiChu.class);
                startActivity(intent);
            }
        });
    }

    public void setEvent()
    {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
              // list.get(position).getId();
                Intent intent = new Intent(getApplicationContext(),ChiTiet.class);
                Bundle bundle = new Bundle();

                bundle.putInt(ID,list.get(position).getId());
                intent.putExtra(BUNDLE,bundle);
                startActivity(intent);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);


        //dùng để search dữ liệu trong list
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_Search));

        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                searchData(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                searchData(query);
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                getdata();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_them)
        {
            Intent intent =new Intent(MainActivity.this,ThemGhiChu.class);
            startActivity(intent);
        }

        if(id==R.id.action_macDinh)
        {
            getdata();
        }
        if(id==R.id.action_daDanhDau)
        {
            hasLevel();
        }
        return super.onOptionsItemSelected(item);
    }

    //lấy list mặc định
    private void getdata()
    {
        database = Database.initDatabase(this,DATABASE_NAME);
        Cursor cursor = database.rawQuery("SELECT * FROM notes ORDER BY updated_at DESC",null);
        list.clear();

        for (int i =0; i < cursor.getCount();i++)
        {
            cursor.moveToPosition(i);
            int id = cursor.getInt(0);//id
            String tieude = cursor.getString(1);//title
            String noidung = cursor.getString(2);//description
            int level = cursor.getInt(3);//level
            byte[] anh = cursor.getBlob(4);//image
            String alarm_timestamp = cursor.getString(5);//alarm_timestamp
            String created_at = cursor.getString(6);//created_at
            String updated_at = cursor.getString(7);//updated_at
            list.add(new GhiChu(id,tieude,noidung,level,anh,alarm_timestamp,created_at,updated_at));
        }
        adapter.notifyDataSetChanged();
    }

    //ham search
    private void searchData(String query){
        database = Database.initDatabase(this,DATABASE_NAME);
        Cursor cursor = database.rawQuery("SELECT * FROM notes WHERE title LIKE " +"'%"+ query +"%'" + " OR description like " + "'%"+ query +"%'",null);
        list.clear();

        for (int i =0; i < cursor.getCount();i++)
        {
            cursor.moveToPosition(i);
            int id = cursor.getInt(0);//id
            String tieude = cursor.getString(1);//title
            String noidung = cursor.getString(2);//description
            int level = cursor.getInt(3);//level
            byte[] anh = cursor.getBlob(4);//image
            String alarm_timestamp = cursor.getString(5);//alarm_timestamp
            String created_at = cursor.getString(6);//created_at
            String updated_at = cursor.getString(7);//updated_at
            list.add(new GhiChu(id,tieude,noidung,level,anh,alarm_timestamp,created_at,updated_at));
        }
        adapter.notifyDataSetChanged();
    }

    //list notes đã đánh dấu
    private void hasLevel(){
        database = Database.initDatabase(this,DATABASE_NAME);
        Cursor cursor = database.rawQuery("SELECT * FROM notes where level = 1",null);
        list.clear();

        for (int i =0; i < cursor.getCount();i++)
        {
            cursor.moveToPosition(i);
            int id = cursor.getInt(0);//id
            String tieude = cursor.getString(1);//title
            String noidung = cursor.getString(2);//description
            int level = cursor.getInt(3);//level
            byte[] anh = cursor.getBlob(4);//image
            String alarm_timestamp = cursor.getString(5);//alarm_timestamp
            String created_at = cursor.getString(6);//created_at
            String updated_at = cursor.getString(7);//updated_at
            list.add(new GhiChu(id,tieude,noidung,level,anh,alarm_timestamp,created_at,updated_at));
        }
        adapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void notificationNote(String title, String description)  {

        // --------------------------
        // Chuẩn bị một thông báo
        // --------------------------

        this.notBuilder.setSmallIcon(R.mipmap.ic_launcher);
        this.notBuilder.setTicker("This is a ticker");

        // Sét đặt thời điểm sự kiện xẩy ra.
        // Các thông báo trên Panel được sắp xếp bởi thời gian này.

        this.notBuilder.setWhen(System.currentTimeMillis()+ 10* 1000);

        this.notBuilder.setContentTitle(title);
        description = (description.length() > 250)?description.substring(0,250) + "...":description;
        this.notBuilder.setContentText(description);

        // Tạo một Intent
        Intent intent = new Intent(this, MainActivity.class);


        // PendingIntent.getActivity(..) sẽ start mới một Activity và trả về
        // đối tượng PendingIntent.
        // Nó cũng tương đương với gọi Context.startActivity(Intent).
        PendingIntent pendingIntent = PendingIntent.getActivity(this, MY_REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);


        this.notBuilder.setContentIntent(pendingIntent);

        // Lấy ra dịch vụ thông báo (Một dịch vụ có sẵn của hệ thống).
        NotificationManager notificationService  =
                (NotificationManager)this.getSystemService(NOTIFICATION_SERVICE);

        // Xây dựng thông báo và gửi nó lên hệ thống.

        Notification notification =  notBuilder.build();
        notificationService.notify(MY_NOTIFICATION_ID, notification);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public final Runnable myRunnable = new Runnable() {
        @Override
        public void run()
        {
            for (int i =0; i < list.size();i++)
            {
                    if(MyFunction.getCurrentDate("HH:mm").equals(list.get(i).getAlarm_timestamp())){
                        notificationNote(list.get(i).getTieude(),list.get(i).getNoidung());
                        return;
                    }
            }
            handler.postDelayed(myRunnable, UPDATE_RATE);
        }
    };
}


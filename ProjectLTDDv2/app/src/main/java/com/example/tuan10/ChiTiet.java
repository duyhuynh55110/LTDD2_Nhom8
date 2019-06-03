package com.example.tuan10;

import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Calendar;

public class ChiTiet extends AppCompatActivity {

    //database
    final String DATABASE_NAME = "notes.sqlite";
    final String TABLE_NAME = "notes";
    SQLiteDatabase database;

    //ID và level và alarm_timestamp của của note
    int id;
    int level = 0;
    String alarm_timestamp = null;

    //lấy phần tử
    TextInputEditText txtTitle, txtContent;
    TextView txtNhacNho;
    ImageView imgHinh;
    ImageView temp;
    Button btnRemove;
    FrameLayout framHinh;

    int REQUEST_CODE_CAMARA = 123;
    int REQUEST_CODE_FOLDER = 345 ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        id = getID();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chitiet);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        setControl();
        setEvent();
        getNote(id);
    }

    public void setControl(){
        txtTitle = findViewById(R.id.txtTitle_sua);
        txtContent = findViewById(R.id.txtContent_sua);
        imgHinh = findViewById(R.id.imgHinh_sua);
        txtNhacNho = findViewById(R.id.txtNhacNho_sua);
        btnRemove = findViewById(R.id.btnRemove);
        framHinh = findViewById(R.id.framHinh);
        framHinh.setVisibility(View.INVISIBLE);
    }
    public void  setEvent(){
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgHinh.setImageResource(0);
                framHinh.setVisibility(View.INVISIBLE);
            }
        });
    }

    //lấy id truyền qa
    public int getID(){
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(MainActivity.BUNDLE);

        int id = bundle.getInt(MainActivity.ID);
        return id;


    }

    //lấy giá trị bỏ vào 2 Input Text
    private void getNote(int id)
    {
        database = Database.initDatabase(this,DATABASE_NAME);
        Cursor cursor = database.rawQuery("SELECT * FROM notes WHERE id = " + id,null);

        cursor.moveToPosition(0);
        txtTitle.setText(cursor.getString(1));
        txtContent.setText(cursor.getString(2));
        txtNhacNho.setText("Nhắc nhở: " + cursor.getString(5));

        //xác định xem note này có đánh dấu không
        level = cursor.getInt(3);

        if(cursor.getBlob(4) != null){
            //gán ảnh
            Bitmap bitmap = BitmapFactory.decodeByteArray(cursor.getBlob(4),0, cursor.getBlob(4).length);
            imgHinh.setImageBitmap(bitmap);
            framHinh.setVisibility(View.VISIBLE);
        }
    }

    //ham sửa Notes
    private void editNote(){
        int id = getID();
        byte[] hinh = MyFunction.getByteArrayFromImageView(imgHinh);


        ContentValues contentValues = new ContentValues();
        contentValues.put("title", txtTitle.getText().toString());
        contentValues.put("description", txtContent.getText().toString());
        contentValues.put("image", hinh);
        contentValues.put("level", level);
        contentValues.put("alarm_timestamp",alarm_timestamp);
        contentValues.put("updated_at",MyFunction.getCurrentDate("yyyy-MM-DD HH:MM:SS"));

        SQLiteDatabase database = Database.initDatabase(this, DATABASE_NAME);
        database.update(TABLE_NAME,contentValues,"id ="+"'"+id+"'", null);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    //hàm xóa
    private void deleteNote(){
        int id = getID();
        SQLiteDatabase database = Database.initDatabase(this, DATABASE_NAME);
        database.delete(TABLE_NAME,"id ="+"'"+id+"'",null);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    //hiện form xác nhận có/không thay đổi Notes
    private void confirmEditNote(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Bạn có chắc muốn thay đổi? ");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        editNote();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    //hiện form xác nhận có/không xóa Notes
    private void confirmDeleteNote(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Bạn có chắc muốn xóa ? ");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteNote();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu2,menu);
        if(level == 1){
            menu.getItem(2).setIcon(R.drawable.ic_star2_black_24dp);
        }else {
            menu.getItem(2).setIcon(R.drawable.ic_star_border_black_24dp);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_sua)
        {
            confirmEditNote();//sửa
        }
        if(id == R.id.action_xoa_sua){
            confirmDeleteNote();//xóa
        }
        if(id== R.id.action_chupHinh_sua)
        {
            imgHinh = findViewById(R.id.imgHinh_sua);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent,REQUEST_CODE_CAMARA);
        }
        if(id == R.id.action_chonHinh_sua)
        {
            imgHinh = findViewById(R.id.imgHinh_sua);
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent,REQUEST_CODE_FOLDER);
        }
        if(id == R.id.action_danhDau_sua)
        {
            if(level == 1){
                level = 0;

                item.setIcon(R.drawable.ic_star_border_black_24dp);
            }else{
                level = 1;

                item.setIcon(R.drawable.ic_star2_black_24dp);
            }
        }
        if(id == R.id.action_henGio_sua){
            final Calendar c = Calendar.getInstance();
            final int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) { //khi ấn OK
                    alarm_timestamp = hour + ":" + minute;
                    try {
                        alarm_timestamp = MyFunction.convertDate(alarm_timestamp,"HH:mm","HH:mm");
                        txtNhacNho.setText("Nhắc nhở: " + alarm_timestamp);
                    }
                    catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(),"Đã đặt nhắc nhở lúc: " + alarm_timestamp,Toast.LENGTH_LONG).show();
                }
            }, hour, minute , false);

            //thêm nút hủy nhắc nhở
            timePickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Hủy nhắc nhở", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) { //hủy nhắc nhở
                    alarm_timestamp = null;
                    txtNhacNho.setText("Nhắc nhở: " + alarm_timestamp);
                }
            });
            timePickerDialog.show();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CODE_FOLDER && resultCode == RESULT_OK && data != null)
        {
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap1 = BitmapFactory.decodeStream(inputStream);
                imgHinh.setImageBitmap(bitmap1);
                framHinh.setVisibility(View.VISIBLE);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        if(requestCode == REQUEST_CODE_CAMARA && resultCode == RESULT_OK && data != null)
        {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imgHinh.setImageBitmap(bitmap);
            framHinh.setVisibility(View.VISIBLE);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


}


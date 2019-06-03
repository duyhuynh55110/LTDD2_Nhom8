package com.example.tuan10;

import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;


public class ThemGhiChu extends AppCompatActivity {

    ListView listView ;
    ArrayList<GhiChu> data = new ArrayList<>();
    GhiChu adapter = null;
    EditText editTieude,editNoidung;
    TextView txtNgayTao,txtSua;
    ImageView imghinh;
    ImageView img;
    TextView txtNhacNho;
    Button btnRemove;
    FrameLayout framHinh;

    int REQUEST_CODE_CAMARA = 123;
    int REQUEST_CODE_FOLDER = 345 ;
    EditText txtTitle_them,txtContent_them;

    //đánh dấu
    int level = 0;

    //hẹn giờ
    String alarm_timestamp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them);
        setControl();
        setEvent();
    }

    public void setControl(){
        txtNhacNho = (TextView) findViewById(R.id.txtNhacNho_them);
        img = (ImageView) findViewById(R.id.imgHinh);
        editTieude = (EditText) findViewById(R.id.txtTitle_them);
        editNoidung = (EditText) findViewById(R.id.txtContent_them);
        btnRemove = findViewById(R.id.btnRemove_them);
        framHinh = findViewById(R.id.framHinh_them);
        framHinh.setVisibility(View.INVISIBLE);
    }
    public void  setEvent(){
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img.setImageResource(0);
                framHinh.setVisibility(View.INVISIBLE);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_them)
        {
            insert();
        }
        if(id== R.id.action_chupHinh)
        {
            setControl();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent,REQUEST_CODE_CAMARA);
        }
        if(id == R.id.action_chonHinh)
        {
            setControl();
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent,REQUEST_CODE_FOLDER);
        }
        if(id == R.id.action_danhDau_them)
        {
            if(level == 1){
                level = 0;

                item.setIcon(R.drawable.ic_star_border_black_24dp);
            }else{
                level = 1;

                item.setIcon(R.drawable.ic_star2_black_24dp);
            }
        }
        if(id == R.id.action_henGio_them){
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
                img.setImageBitmap(bitmap1);
                framHinh.setVisibility(View.VISIBLE);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        if(requestCode == REQUEST_CODE_CAMARA && resultCode == RESULT_OK && data != null)
        {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            img.setImageBitmap(bitmap);
            framHinh.setVisibility(View.VISIBLE);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    //hàm thêm
    private void insert(){

        String td = editTieude.getText().toString();
        String nd = editNoidung.getText().toString();
        byte[] hinh = MyFunction.getByteArrayFromImageView(img);

        /*lấy dữ liệu cho việc thêm
        *
        * */
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", td);
        contentValues.put("description", nd);
        contentValues.put("image", hinh);
        contentValues.put("level", level);
        contentValues.put("alarm_timestamp", alarm_timestamp);
        contentValues.put("created_at",MyFunction.getCurrentDate("yyyy-MM-DD HH:MM:SS"));
        contentValues.put("updated_at",MyFunction.getCurrentDate("yyyy-MM-DD HH:MM:SS"));

        SQLiteDatabase database = Database.initDatabase(this, "notes.sqlite");
        database.insert("notes",null, contentValues);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


}

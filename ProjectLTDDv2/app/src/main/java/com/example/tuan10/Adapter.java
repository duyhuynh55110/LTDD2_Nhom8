package com.example.tuan10;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.util.ArrayList;

public class Adapter extends BaseAdapter {
    Activity context;
    ArrayList<GhiChu> list;

    public Adapter(Activity context, ArrayList<GhiChu> list) {
        this.context = context;
        this.list = list;
    }

    //đếm phần tử trong danh sách
    @Override
    public int getCount() {
        return list.size();
    }

    //lấy dối tượng dựa trên position
    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        //gán giá trị cho item
        View row = inflater.inflate(R.layout.list_khachhang,null);

        ImageView img = (ImageView)row.findViewById(R.id.imgImage);
        TextView title = (TextView) row.findViewById(R.id.txtTitle);
        final TextView description = (TextView) row.findViewById(R.id.txtDescription);
        TextView created_at = (TextView) row.findViewById(R.id.txt_createdAt);
        TextView edited_at = (TextView) row.findViewById(R.id.txt_editedAt);
        ImageView level_star  = (ImageView) row.findViewById(R.id.level_star);


        //gán gí trị vào dòng
        final GhiChu GC = list.get(position);

        title.setText(GC.tieude + "");
        description.setText(GC.noidung + "");

        //format dữ liệu hiển thị
        String string_createdAt = null;
        String string_editedAt = null;
        try {
            string_createdAt = MyFunction.convertDate(GC.created_at,"yyyy-MM-DD HH:MM:SS","dd-MM-yyyy");
            string_editedAt = MyFunction.convertDate(GC.updated_at,"yyyy-MM-DD HH:MM:SS","dd-MM-yyyy");
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        created_at.setText(string_createdAt);
        edited_at.setText(string_editedAt);
        if(GC.level == 1){
            level_star.setVisibility(View.VISIBLE);
        }else {
            level_star.setVisibility(View.INVISIBLE);
        }

        if(GC.getAnh() != null){
            //gán ảnh
            Bitmap bitmap = BitmapFactory.decodeByteArray(GC.anh,0, GC.anh.length);
            img.setImageBitmap(bitmap);
        }else {
            img.setVisibility(View.INVISIBLE);
        }

        return row;
    }


    //khi ấn vào một item trong list

}

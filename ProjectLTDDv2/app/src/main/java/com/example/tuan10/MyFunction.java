package com.example.tuan10;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyFunction {

    //chuyển ngày về dạng dd/MM/yyyy
    public static String convertDate(String valueDate, String typeInput, String typeFormat) throws ParseException {
        SimpleDateFormat sdfDateNormal = new SimpleDateFormat(typeInput); //chuyển chuỗi valueDate thành ngày
        SimpleDateFormat sdfDateFormat = new SimpleDateFormat(typeFormat);//format date

        Date date = (Date) sdfDateNormal.parse(valueDate);
        String result = sdfDateFormat.format(date);

        return result;
    }

    //chuyển chuỗi về kiểu Date dạng HH:MM
    public static Date getDate(String valueDate) throws ParseException{
        SimpleDateFormat sdfDateNormal = new SimpleDateFormat("HH:mm"); //chuyển chuỗi valueDate thành ngày
        Date date = (Date) sdfDateNormal.parse(valueDate);

        return date;
    }

    //chuyển Date về dạng HH:MM
    public static String getCurrent() throws ParseException{
        SimpleDateFormat sdfDateNormal = new SimpleDateFormat("HH:MM"); //chuyển chuỗi valueDate thành ngày
        String result = sdfDateNormal.format(new Date());

        return result;
    }
    //lấy ngày hiện tại trong máy
    public static String getCurrentDate(String format){
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date date = new Date();
        System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43
        return dateFormat.format(date);
    }

    //hàm lấy ảnh và chuyển về kiểu byte[]
    public static byte[] getByteArrayFromImageView(ImageView imgv){

        BitmapDrawable drawable = (BitmapDrawable) imgv.getDrawable();

        //nếu như người dùng có chụp hoặc up ảnh
        if(drawable != null){
            Bitmap bmp = drawable.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            return byteArray;
        }
        return null;
    }
}

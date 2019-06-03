package com.example.tuan10;

public class GhiChu {
        public  int id;
        public  String tieude;
        public  String noidung;
        public  byte[] anh;
        public  int level;
        public  String alarm_timestamp;
        public  String created_at;
        public  String updated_at;

        public GhiChu(){}
        public GhiChu(int id, String tieude, String noidung, int level,byte[] anh,String alarm_timestamp, String created_at, String updated_at) {
            this.id = id;
            this.tieude = tieude;
            this.noidung = noidung;
            this.level = level;
            this.anh = anh;
            this.alarm_timestamp = alarm_timestamp;
            this.created_at = created_at;
            this.updated_at = updated_at;

        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }

        public String getAlarm_timestamp() {
            return alarm_timestamp;
        }

        public void setAlarm_timestamp(String alarm_timestamp) {
            this.alarm_timestamp = alarm_timestamp;
        }

        public int getId() {
                return id;
            }

        public void setId(int id) {
            this.id = id;
        }

        public String getTieude() {
            return tieude;
        }

        public void setTieude(String tieude) {
            this.tieude = tieude;
        }

        public String getNoidung() {
            return noidung;
        }

        public void setNoidung(String noidung) {
            this.noidung = noidung;
        }

        public byte[] getAnh() {
            return anh;
        }

        public void setAnh(byte[] anh) {
            this.anh = anh;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }
}

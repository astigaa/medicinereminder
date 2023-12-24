package com.example.medmanager.mydatabase;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.NonNull;
// SQLite veritabanı işlemleri için kullanılan özel sınıf
public class MedicalDB extends SQLiteOpenHelper {
    // Singleton tasarım deseni için örnek oluşturulması
    public static MedicalDB sInstance;
    // Singleton tasarım deseni: Tek bir örnek döndüren metot
    public static synchronized MedicalDB getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new MedicalDB(context.getApplicationContext());
        }
        return sInstance;
    }
    // Veritabanı versiyon numarası
    public static final int version = 1;
    // MedicalDB sınıfının kurucu metodu
    public MedicalDB(Context context) {
        super(context, "database", null, version);
    }
    // Veritabanı oluşturulduğunda çağrılan metot
    @Override
    public void onCreate(SQLiteDatabase db) {
        // MEDICINE tablosunu oluşturan SQL sorgusu
        String create_med_table = "CREATE TABLE MEDICINE (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "MED_NAME TEXT NOT NULL," +
                "QTY INTEGER NOT NULL," +
                "DATE_TIME TEXT NOT NULL," +
                "DAYS TEXT NOT NULL," +
                "ENABLE INT NOT NULL);";
        // SQL sorgularını çalıştırarak tabloları oluştur
        db.execSQL(create_med_table);
    }
    // Veritabanı versiyonu değiştiğinde çağrılan metot
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Güncelleme işlemleri bu metoda eklenir, ancak bu örnekte boş bırakıldı
    }

    // İlaç (MEDICINE) CRUD işlemleri
    // İlaç eklemek için metot
    public void addMedicine(SQLiteDatabase db, @NonNull String med_name, @NonNull int quantity, @NonNull String date_time, @NonNull String days) {
        ContentValues values = new ContentValues();
        values.put("MED_NAME", med_name);
        values.put("QTY", quantity);
        values.put("DATE_TIME", date_time);
        values.put("DAYS", days);
        values.put("ENABLE", false);

        db.insert("MEDICINE", null, values);
    }
    // İlaç silmek için metot
    public void deleteMedicine(SQLiteDatabase db, @NonNull int med_id) {
        db.delete("MEDICINE", "_id=?", new String[]{"" + med_id});
    }

    // İlaç etkinliğini güncellemek için metot
    public void setEnable(SQLiteDatabase db, int id, int b) {
        ContentValues values = new ContentValues();
        values.put("ENABLE", b);
        db.update("MEDICINE", values, "_id=?", new String[]{"" + id});
    }
    // Belirli bir kullanıcının ilaç listesini almak için metot
    public Cursor getAllMedicine(SQLiteDatabase db) {
        return db.rawQuery("SELECT * FROM MEDICINE;", new String[]{});
    }
    // Belirli bir ilacın bilgilerini almak için metot
    public Cursor getMedicine(SQLiteDatabase db, int med_id) {
        return db.rawQuery("SELECT * FROM MEDICINE WHERE _id=" + med_id + ";", new String[]{});
    }
}

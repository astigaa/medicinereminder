package com.example.medmanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medmanager.mydatabase.MedicalDB;

import java.util.Calendar;

// İlaç listesi için RecyclerView adaptörü
public class MedicineListAdapter extends RecyclerView.Adapter {
    // İlaç listesi verilerini tutan Cursor
    private Cursor med_list;
    // Uygulama bağlamı
    public Context context;
    // Veritabanı yardımcı sınıfı
    public MedicalDB helper;

    // Adaptörün oluşturulması
    public MedicineListAdapter(Context context, MedicalDB helper) {
        this.context = context;
        this.helper = helper;
    }

    // Veri setini güncelleyen metot
    public void setUserData(Cursor cursor) {
        this.med_list = cursor;
        if (med_list != null) {
            med_list.moveToFirst();
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MedicineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // ViewHolder'ın oluşturulması
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.medicine_card, parent, false);
        MedicineHolder vh = new MedicineHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (med_list != null) {
            // ViewHolder'ın ilgili öğelerini doldurma
            ((MedicineHolder) holder).medName.setText(med_list.getString(1));
            ((MedicineHolder) holder).qty.setText("Adet: " + med_list.getInt(2));
            ((MedicineHolder) holder).id = med_list.getInt(0);
            ((MedicineHolder) holder).time.setText("Saat: "+med_list.getString(3));

            // İlaç durumu (açık/kapalı) için anahtarlama düğmesinin durumunu ayarlama
            boolean isChecked = med_list.getInt(5) == 1 ? true : false;
            ((MedicineHolder) holder).toggleSwitch.setChecked(isChecked);

            // Anahtarlama düğmesine tıklanınca çağrılan listener
            ((MedicineHolder) holder).toggleSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // İlaç durumunu güncelleme
                    if (((MedicineHolder) holder).toggleSwitch.isChecked()) {
                        helper.setEnable(helper.getWritableDatabase(), ((MedicineHolder) holder).id, 1);
                    } else {
                        helper.setEnable(helper.getWritableDatabase(), ((MedicineHolder) holder).id, 0);
                    }

                    // Bildirim için alarmı ayarlama
                    Cursor c = helper.getMedicine(helper.getWritableDatabase(), ((MedicineHolder) holder).id);
                    c.moveToFirst();
                    String[] raw_time = c.getString(3).split(":");
                    int hour = Integer.parseInt(raw_time[0]);
                    int min = Integer.parseInt(raw_time[1]);

                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.HOUR_OF_DAY, hour);
                    cal.set(Calendar.MINUTE, min);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);

                    Calendar now = Calendar.getInstance();
                    now.set(Calendar.SECOND, 0);
                    now.set(Calendar.MILLISECOND, 0);

                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
                    intent.putExtra("medName", c.getString(1));
                    intent.putExtra("medQty", c.getString(2));

                    if (((MedicineHolder) holder).toggleSwitch.isChecked()) {
                        // Tekrarlama günlerini kontrol et
                        String days = c.getString(4);
                        if (days.equals("0000000")) {
                            // Gün belirtilmemişse, bir sonraki güne ayarla
                            if (cal.before(now)) {
                                cal.add(Calendar.DATE, 1);
                            }
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,((MedicineHolder) holder).id, intent, 0);
                            alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

                            Toast.makeText(context, "Hatırlatıcı " + c.getString(1) + " ilacı için saat " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ", tarih " + cal.get(Calendar.DATE) + "/" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.YEAR) + " olarak ayarlandı.", Toast.LENGTH_LONG).show();
                        } else {
                            // Gün belirtilmişse, her bir gün için ayrı alarm ayarla
                            int ct = 1;
                            for (char d : days.toCharArray()) {
                                if (d == '1') {
                                    cal.set(Calendar.DAY_OF_WEEK, ct);
                                    if (cal.before(now)) {
                                        cal.add(Calendar.DATE, 7);
                                    }
                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(((MedicineHolder) holder).id + "" + ct), intent, 0);
                                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
                                }
                                ct++;
                            }
                            Toast.makeText(context, "Hatırlatıcı " + c.getString(1) + " ilacı için saat " + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + " olarak ayarlandı.", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        // İlaç kapatıldıysa, ilgili alarmı iptal et
                        String days = c.getString(4);
                        if (days.equals("0000000")) {
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,((MedicineHolder) holder).id, intent, 0);
                            alarmManager.cancel(pendingIntent);
                        } else {
                            int ct = 1;
                            for (char d : days.toCharArray()) {
                                if (d == '1') {
                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(((MedicineHolder) holder).id + "" + ct), intent, 0);
                                    alarmManager.cancel(pendingIntent);
                                }
                                ct++;
                            }
                        }
                    }
                }
            });

            // İlaç silme düğmesine tıklanınca çağrılan listener
            ((MedicineHolder) holder).deleteMed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // İlaç silme işlemini gerçekleştir
                    helper.deleteMedicine(helper.getWritableDatabase(), ((MedicineHolder) holder).id);
                    // Veri setini güncelle ve RecyclerView'yi yeniden ayarla
                    setUserData(helper.getAllMedicine(helper.getWritableDatabase()));
                }
            });
            // Cursor'daki sonraki ilaca geçme
            med_list.moveToNext();
        }
    }

    @Override
    public int getItemCount() {
        // Adaptörün tuttuğu öğe sayısını döndürme
        return med_list.getCount();
    }

    // İlaç öğesini tutan ViewHolder sınıfı
    public class MedicineHolder extends RecyclerView.ViewHolder {
        TextView medName, time, qty;
        ImageButton deleteMed;
        int id;
        Switch toggleSwitch;

        // ViewHolder'ın oluşturulması
        public MedicineHolder(@NonNull View itemView) {
            super(itemView);
            // ViewHolder içindeki öğelerin atanması
            medName = (TextView) itemView.findViewById(R.id.med_name);
            time = (TextView) itemView.findViewById(R.id.med_time);
            qty = (TextView) itemView.findViewById(R.id.med_quantity);
            deleteMed = (ImageButton) itemView.findViewById(R.id.delete_med);
            toggleSwitch = (Switch) itemView.findViewById(R.id.toggle_switch);
        }
    }
}

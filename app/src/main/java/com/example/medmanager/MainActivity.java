package com.example.medmanager;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medmanager.mydatabase.MedicalDB;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

// İlaçlarını gösteren aktivite sınıfı
public class MainActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    // Kullanıcının ilaç listesini gösteren RecyclerView
    public RecyclerView medList;
    // İlaç listesi adaptörü
    public MedicineListAdapter medListAdapter;
    // İlaç ekleme butonu
    public FloatingActionButton medFab;
    // İlaç bilgileri girişi için UI elemanları
    Button medTime;
    EditText medName, medQty;
    Switch isRepeat;
    ChipGroup chipGroup;
    Chip pzt, sal, crs, prs, cum, cts, pzr;

    // Veritabanı nesnesi
    public MedicalDB DbHelper;

    // Aktivite oluşturulduğunda çağrılan metot
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine);

        // Veritabanı nesnesinin oluşturulması
        DbHelper = MedicalDB.getInstance(getApplicationContext());

        // View'ları aktiviteye bağla
        medList = findViewById(R.id.med_list);
        medFab = findViewById(R.id.med_fab);

        // RecyclerView için layout manager'ın ayarlanması
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        medList.setLayoutManager(linearLayoutManager);

        // İlaç listesi adaptörünün oluşturulması ve verilerin atanması
        medListAdapter = new MedicineListAdapter(getApplicationContext(), DbHelper);
        medListAdapter.setUserData(DbHelper.getAllMedicine(DbHelper.getWritableDatabase()));
        medList.setAdapter(medListAdapter);

        // İlaç ekleme butonuna tıklanınca çağrılan listener
        medFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // İlaç ekleme dialogunu gösteren metot
                medicineAdder().show();
            }
        });
    }

    // İlaç ekleme dialogunu oluşturan metot
    private AlertDialog medicineAdder() {
        // Dialogun içeriğini belirten layout'un tanımlanması
        View layout = View.inflate(this, R.layout.add_med_dialog, null);

        // İlaç detayları
        medName = layout.findViewById(R.id.add_med_name);
        medQty = layout.findViewById(R.id.add_med_qty);
        medTime = layout.findViewById(R.id.add_med_time);
        // UI elemanları
        isRepeat = layout.findViewById(R.id.repeat_switch);
        chipGroup = layout.findViewById(R.id.chip_group);
        setChildrenEnabled(chipGroup, false);
        pzt = layout.findViewById(R.id.pazartesi);
        sal = layout.findViewById(R.id.sali);
        crs = layout.findViewById(R.id.carsamba);
        prs = layout.findViewById(R.id.persembe);
        cum = layout.findViewById(R.id.cuma);
        cts = layout.findViewById(R.id.cumartesi);
        pzr = layout.findViewById(R.id.pazar);

        // Saat seçimi için tıklanınca çağrılan listener
        medTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        // Tekrarlama switch'ine tıklanınca çağrılan listener
        isRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tekrarlama açık ise günleri etkinleştir, değilse devre dışı bırak
                if (!isRepeat.isChecked()) {
                    setChildrenEnabled(chipGroup, false);
                } else {
                    setChildrenEnabled(chipGroup, true);
                }
            }
        });

        // AlertDialog oluşturucu
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout);// Layout'un AlertDialog'a atanması

        // "EKLE" butonuna tıklanınca yapılacak işlemler
        builder.setPositiveButton("EKLE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // İlaç miktarını sayıya çevir
                int qty = 0;//default değer
                if (!"".equals(medQty.getText().toString()))
                    qty = Integer.parseInt(medQty.getText().toString());

                // Günlerin formatını belirle
                String days = "0000000";//default değer
                if (isRepeat.isChecked()) {
                    days = setDaysFormat(pzr, pzt, sal, crs, prs, cum, cts);
                }

                // İlaç eklemeyi gerçekleştir
                DbHelper.addMedicine(DbHelper.getWritableDatabase(),medName.getText().toString(), qty, medTime.getText().toString(), days);

                // İlaç listesini güncelle ve RecyclerView'yi yeniden ayarla
                medListAdapter.setUserData(DbHelper.getAllMedicine(DbHelper.getWritableDatabase()));
                medListAdapter.notifyDataSetChanged();
                medList.setAdapter(medListAdapter);
            }
        });

        // "İPTAL" butonuna tıklanınca yapılacak işlemler
        builder.setNegativeButton("İPTAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        return builder.create();  // Oluşturulan AlertDialog'un döndürülmesi
    }
    // Günlerin formatını belirleyen metot
    public String setDaysFormat(Chip pzr, Chip pzt, Chip sal, Chip crs, Chip prs, Chip cum, Chip cts) {
        String dayString = "" + (pzr.isChecked() ? "1" : "0") + (pzt.isChecked() ? "1" : "0") + (sal.isChecked() ? "1" : "0") + (crs.isChecked() ? "1" : "0") + (prs.isChecked() ? "1" : "0") + (cum.isChecked() ? "1" : "0") + (cts.isChecked() ? "1" : "0");
        return dayString;
    }

    // ChipGroup içindeki tüm elemanların tıklanabilir olma etkinliğini ayarlayan metot
    public void setChildrenEnabled(ChipGroup chipGroup, Boolean enable) {
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            chipGroup.getChildAt(i).setEnabled(enable);
        }
    }

    // Saat seçildiğinde çağrılan metot
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Seçilen saati ekrana yaz
        medTime.setText(hourOfDay + ":" + minute);
    }

}

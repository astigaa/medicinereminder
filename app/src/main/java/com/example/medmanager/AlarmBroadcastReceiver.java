package com.example.medmanager;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.example.medmanager.App.CHANNEL_ID;

// BroadcastReceiver sınıfından türetilen AlarmBroadcastReceiver sınıfı
public class AlarmBroadcastReceiver extends BroadcastReceiver {
    private NotificationManagerCompat notificationManager;
    // Bildirim numarasını takip etmek için kullanılan değişken
    public static int NOTIFICATION_NUMBER = 1;

    // BroadcastReceiver sınıfının onReceive metodu, alarm tetiklendiğinde çağrılır
    @Override
    public void onReceive(Context context, Intent intent) {
        // Intent'ten ilaç ve kullanıcı bilgilerini alma
        String medName = intent.getStringExtra("medName");
        String medQty = intent.getStringExtra("medQty");

        // MediaPlayer kullanarak alarm sesi çalma (bu kısım şu an yorum satırı)
        // MediaPlayer player = MediaPlayer.create(context, Settings.System.DEFAULT_RINGTONE_URI);
        // player.start();
        // Bildirim yöneticisi oluşturma ve bildirimi gönderme
        notificationManager = NotificationManagerCompat.from(context);
        sendOnChannel(context, "Lütfen " + medQty + " adet " + medName + " ilacınızı alınız.", intent);
    }
    // Bildirimi belirtilen kanala gönderme metodu
    public void sendOnChannel(Context context, String message, Intent intent) {
        // Bildirim oluşturma
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.appicon)  // Küçük simge
                .setContentTitle("İlaç Hatırlatıcı")  // Bildirim başlığı
                .setContentText(message)  // Bildirim içeriği
                .setPriority(NotificationCompat.PRIORITY_HIGH)  // Bildirim önceliği
                .setCategory(NotificationCompat.CATEGORY_ALARM)  // Bildirim kategorisi
                .setAutoCancel(true)  // Bildirime tıklanınca otomatik kapatma
                .build();
        // Bildirimi gönderme
        notificationManager.notify(NOTIFICATION_NUMBER++, notification);
    }
}

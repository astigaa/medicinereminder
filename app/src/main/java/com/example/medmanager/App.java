package com.example.medmanager;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

// Uygulama başlatıldığında çalışan özel Application sınıfı
public class App extends Application {
  // Bildirim kanalı için benzersiz bir tanımlayıcı
  public static final String CHANNEL_ID = "channel";
  // Uygulama oluşturulduğunda çağrılan onCreate metodu
  @Override
  public void onCreate() {
    super.onCreate();
    // Bildirim kanalını oluşturan metot
    createNotificationChannels();
  }
  // Bildirim kanalını oluşturan metot
  private void createNotificationChannels() {
    // Android sürümü 8.0 (Oreo) veya daha yüksekse
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      // Yeni bir bildirim kanalı oluştur
      NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "MedManager notifications", NotificationManager.IMPORTANCE_HIGH);
      channel.setDescription("Medmanager notifications appear here");  // Kanalın açıklaması

      // Bildirim yöneticisi alınması
      NotificationManager manager = getSystemService(NotificationManager.class);

      // Bildirim kanalını bildirim yöneticisine ekleme
      manager.createNotificationChannel(channel);
    }
  }
}

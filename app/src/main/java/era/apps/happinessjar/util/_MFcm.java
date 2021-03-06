package era.apps.happinessjar.util;

import android.appwidget.AppWidgetManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

import era.apps.happinessjar.data.models.AppMessage;
import era.apps.happinessjar.data.networking.AppApi;
import era.apps.happinessjar.data.repository.MessageRepository;
import era.apps.happinessjar.ui.view.masseges.MessageWidget;
import kotlinx.coroutines.GlobalScope;

public class _MFcm extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        Log.e("Test Fcm ",remoteMessage.getData().toString());
        if (remoteMessage.getData().get("type").contains("ChatMessage")) {
            if (!DataManger.getInstance().isChatOpen()) {
                NotificationClass.getInstance().showNotification(getApplicationContext(), "chat" + "لديك رد على رسالتك ");
            }
        }
        if (remoteMessage.getData().get("type").contains("Message")) {
            if (!DataManger.getInstance().isChatOpen()) {
                String message = remoteMessage.getData().get("Message");
                NotificationClass.getInstance().showNotification(getApplicationContext(),"message"+ message);
                getApplicationContext().getSharedPreferences("message", 0)
                        .edit().putString("WidgetMessage", message).apply();
                AppWidgetManager appWidgetManager
                        = AppWidgetManager.getInstance(getApplicationContext());
                int mAppWidgetId = getApplicationContext()
                        .getSharedPreferences("msg", 0).getInt("widgId", 0);

                MessageWidget.sUpdateAppWidget(getApplicationContext(), appWidgetManager, mAppWidgetId);
                // NewAppWidget.updateCurrentWidget(getApplicationContext(),messg);
               /* icon = R.drawable.logo;
                CreateNotifa.getInstance(getApplicationContext())
                        .Notify("صباح الخير رسالتك جاهزه", icon,type);
                MyRepository repo=new MyRepository(getApplication());
                MyRow row=new MyRow(0,"",messg,type,"");
                repo.Insert(row);*/
                AppMessage appMessage=new AppMessage(0,message,"message");

              MessageRepository repository=  new MessageRepository(getApplication());

            }
        }


    }


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("DeviceTokens");
        myRef.child(s).setValue(s).addOnSuccessListener(aVoid -> AppApi.getInstance()
                .notifyUserThereIsNewMessage(s, "ربنا معاك وبيجبر بخاطرك ديما و بيحبك ف تفائل ❤️", "message"));
        getSharedPreferences("info", 0)
                .edit().putString("fcm", s).apply();
    }





}

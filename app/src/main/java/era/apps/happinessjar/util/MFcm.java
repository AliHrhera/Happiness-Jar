package era.apps.happinessjar.util;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;

public class MFcm extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {




    }


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("DeviceTokens");
        myRef.child(s).setValue(s);

    }
}
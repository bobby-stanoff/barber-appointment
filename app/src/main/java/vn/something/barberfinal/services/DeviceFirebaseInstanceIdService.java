package vn.something.barberfinal.services;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;

public class DeviceFirebaseInstanceIdService extends FirebaseMessagingService {

    public void onMessageSent(@NonNull RemoteMessage message){

    }


}

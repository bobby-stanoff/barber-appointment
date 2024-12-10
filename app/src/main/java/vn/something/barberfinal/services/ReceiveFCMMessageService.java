package vn.something.barberfinal.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import vn.something.barberfinal.DataModel.Appointment;
import vn.something.barberfinal.MainActivity;
import vn.something.barberfinal.R;

public class ReceiveFCMMessageService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("EnhancedIntentService", "From: " + remoteMessage.getFrom());

        // datapayload example:
        //{
        // referencePicture=isnull,
        // date=12/12/2024,
        // note=isnull,
        // time=12:12,
        // type=appointment,
        // customerName=Pham,
        // service=Cắt tóc,
        // messengerUserId=8783764061717403,
        // customerPhone=097296362
        // }
        if (!remoteMessage.getData().isEmpty()) {
            Log.d("recived notificationservive", "Message data payload: " + remoteMessage.getData());
            saveAppointment(new Appointment("fe","fe","fefe","fweg","efe","few","fewf","efw","few","fwq"));
            sendNotification("recived something from fcm, hello");


        }
        //see: https://firebase.google.com/docs/cloud-messaging/android/receive#handling_messages
        if (remoteMessage.getNotification() != null) {
            Log.d("whatever", "Message Notification Body: " + remoteMessage.getNotification().getBody());

        }


    }
    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("onNewToken", "Refreshed token: " + token);

    }
    private void saveAppointment(Appointment appointment){

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference appointmentsRef = database.collection("appointments").document();

        // Generate a unique key for the appointment
        String appointmentId = appointmentsRef.getId();
        appointment.setAppointmentId(appointmentId);

        // Save to Firebase
        if (appointmentId != null) {
            appointmentsRef.set(appointment)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("firestoresave", "Appointment saved successfully");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("firestoresave", "Error saving appointment", e);
                    });
        }
    }
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_IMMUTABLE);

        String channelId = "fcm_default_channel";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this,channelId)
                        .setContentTitle("May co khach")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSmallIcon(R.drawable.ic_dashboard_black_24dp)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);



        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}

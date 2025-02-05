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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import vn.something.barberfinal.DataModel.Appointment;
import vn.something.barberfinal.DataModel.BarberShop;

import vn.something.barberfinal.DataModel.BarberUser;
import vn.something.barberfinal.MainActivity;
import vn.something.barberfinal.R;

public class ReceiveFCMMessageService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https_://goo.gl/39bRNJ
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
        // messengerUserId=909009988,
        // customerPhone=097296362
        // }
        if (!remoteMessage.getData().isEmpty()) {
            Log.d("recived notificationservive", "Message data payload: " + remoteMessage.getData());
            String shopId = getSharedPreferences("ShopPrefs",0).getString("shopId",null);
            Map<String, String> reciveddata = remoteMessage.getData();
            Appointment newAppointment = new Appointment(
                    shopId,
                    reciveddata.get("referencePicture"),
                    reciveddata.get("date"),
                    reciveddata.get("note"),
                    reciveddata.get("time"),
                    reciveddata.get("customerName"),
                    reciveddata.get("service"),
                    reciveddata.get("messengerUserId"),
                    reciveddata.get("customerPhone")

                    );
            saveAppointment(newAppointment);
            sendNotification(" "+newAppointment.getCustomerName()+": Đặt lịch hẹn từ trang Facebook ");

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
        Log.d("TAG", "saveAppointment: "+ appointment.getShopId());
        CollectionReference appointmentsRef =  database.collection("shops").document(appointment.getShopId()).collection("appointments");
        appointmentsRef.add(appointment)
                .addOnSuccessListener(aVoid -> {
                    Log.d("firestoresave", "Appointment saved successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("firestoresave", "Error saving appointment", e);
                });
        BarberUser appointmentUser = new BarberUser(appointment.getMessengerUserId());
        DocumentReference userRef = database.collection("shops").document(appointment.getShopId()).collection("users").document(appointmentUser.getPSID());
        userRef.get().addOnCompleteListener(documentSnapshot -> {
            if(documentSnapshot.isSuccessful()){
                DocumentSnapshot document = documentSnapshot.getResult();
                BarberUser existingUser = document.toObject(BarberUser.class);
                if(existingUser != null){
                    userRef.update("numberOfReservation",existingUser.getNumberOfReservation()+1).addOnCompleteListener(a->{
                        Log.d("TAG", "saveAppointment: update reservation: "+ existingUser.getNumberOfReservation()+1);
                    }).addOnFailureListener(e -> {
                        Log.d("TAG", "saveAppointment: something update reservation wrong"+ e);
                    });
                }
                else {
                    userRef.set(appointmentUser).addOnCompleteListener(a ->{
                        Log.d("TAG", "saveAppointment: save new user successs");
                    });
                }
            }
        });

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

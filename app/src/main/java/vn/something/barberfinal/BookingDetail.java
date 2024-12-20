package vn.something.barberfinal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import vn.something.barberfinal.DataModel.Appointment;
import vn.something.barberfinal.DataModel.BarberUser;

public class BookingDetail extends AppCompatActivity {
    Appointment itemData = null;
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    TextView textViewReServId;
    ImageView clientProfilePic;
    Button user_report_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking_detail);

        itemData = (Appointment) getIntent().getSerializableExtra("item");
        textViewReServId = findViewById(R.id.reservation_id_textview);
        clientProfilePic = findViewById(R.id.clientProfilePic);
        user_report_button = findViewById(R.id.user_report_button);
        ImageView closeButton = findViewById(R.id.close_button);
        Button openMessengerButton = findViewById(R.id.btn_open_messenger);
        getUserInfo(itemData.getMessengerUserId());
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        openMessengerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openExternal();
            }
        });
        user_report_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BookingDetail.this);
                builder.setTitle("Chặn người này?").setNegativeButton("Chặn vĩnh viễn ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String blockType = "FOREVER";
                        handleBlockButton(blockType);
                        Toast.makeText(BookingDetail.this, "Đã chặn !", Toast.LENGTH_SHORT).show();
                    }
                }).setNeutralButton("Chặn 3 ngày", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String blockType = "DAY3";
                        handleBlockButton(blockType);
                        Toast.makeText(BookingDetail.this, "Đã chặn !", Toast.LENGTH_SHORT).show();
                    }
                }).setPositiveButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });


                // Show the dialog
                builder.create().show();
            }
        });

    }
    private void openExternal() {

        try {
            // Try to open Facebook Business Suite app
            Intent intent = getPackageManager()
                    .getLaunchIntentForPackage("com.facebook.pages.app");
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://business.facebook.com/latest/inbox"));
                startActivity(intent);
            }
        } catch (Exception e) {
            Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.facebook.orca"));
            startActivity(playStoreIntent);
        }
    }

    public void getUserInfo(String psid) {
        String pageAccessToken = getSharedPreferences("ShopPrefs", MODE_PRIVATE).getString("shopPageToken",null);
        Log.d("TAG", "getUserInfo: "+ pageAccessToken );
        String fields = "id,name,profile_pic";
        GraphRequest request = new GraphRequest(
                null,
                "/" + psid, 
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {

                        if (response.getError() != null) {
                            Log.e("GraphAPI", "Error: " + response.getError().getErrorMessage());
                        } else {

                            try {
                                JSONObject jsonObject = response.getJSONObject();
                                String profilename = jsonObject.optString("name");
                                String profilePic = jsonObject.optString("profile_pic");
                                Log.d("BOOKING DETAIL", "onCompleted: "+ profilename+ " "+ profilePic);
                                UpdateUI(profilePic);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

        Bundle params = new Bundle();
        params.putString("access_token", pageAccessToken);
        params.putString("fields", fields);
        request.setParameters(params);


        request.executeAsync();
    }
    public void UpdateUI(String ProfilePicUrl){

        textViewReServId.setText(itemData.getShortId());
        Glide.with(this).load(ProfilePicUrl).into(clientProfilePic);

    }
    public void handleBlockButton(String blockType){


        String shopId = getSharedPreferences("ShopPrefs",MODE_PRIVATE).getString("shopId",null);
        DocumentReference userRef = database.collection("shops").document(shopId).collection("users").document(itemData.getMessengerUserId());
        userRef.get().addOnCompleteListener(documentSnapshot -> {
            if(documentSnapshot.isSuccessful()){
                DocumentSnapshot document = documentSnapshot.getResult();
                BarberUser existingUser = document.toObject(BarberUser.class);
                existingUser.setBlockType(blockType);
                if(blockType.equals("DAY3")){
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DAY_OF_YEAR, 3);
                    Date expireddate = calendar.getTime();
                    existingUser.setExpiredBlockDate(expireddate);

                }
                userRef.set(existingUser);
            }
        });


    }

}
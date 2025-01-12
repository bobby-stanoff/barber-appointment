package vn.something.barberfinal;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import vn.something.barberfinal.DataModel.Appointment;
import vn.something.barberfinal.DataModel.BarberUser;

public class BookingDetail extends AppCompatActivity {
    Appointment itemData = null;
    String[] timestamps = {"08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00"};
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    TextView textViewReServId;
    ImageView clientProfilePic, reference_image;
    Button user_report_button, callButton, save_app_button;
    TextView res_note_rec,your_name_rec,your_phone_rec,your_namefb_rec,res_date_rec,res_type_rec,
             res_status_rec,res_cost_rec;
    Spinner res_time_rec;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking_detail);

        itemData = (Appointment) getIntent().getSerializableExtra("item");
        textViewReServId = findViewById(R.id.reservation_id_textview);
        clientProfilePic = findViewById(R.id.clientProfilePic);
        user_report_button = findViewById(R.id.user_report_button);
        your_name_rec = findViewById(R.id.your_name_rec);
        your_phone_rec = findViewById(R.id.your_phone_rec);
        your_namefb_rec = findViewById(R.id.your_namefb_rec);
        res_date_rec = findViewById(R.id.res_date_rec);
        res_time_rec = findViewById(R.id.res_time_rec);
        res_type_rec = findViewById(R.id.res_type_rec);
        res_status_rec = findViewById(R.id.res_status_rec);
        res_cost_rec = findViewById(R.id.res_cost_rec);
        reference_image = findViewById(R.id.reference_image);
        res_note_rec = findViewById(R.id.res_note_rec);
        save_app_button = findViewById(R.id.save_app_button);
        ImageView closeButton = findViewById(R.id.close_button);
        Button openMessengerButton = findViewById(R.id.btn_open_messenger);
        getUserInfo(itemData.getMessengerUserId());

        //setup spinner
        @SuppressLint("ResourceType")
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.drawable.spinner_dropdown_item, timestamps);
        res_time_rec.setAdapter(adapter);
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
        callButton = findViewById(R.id.user_call_rec);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = your_phone_rec.getText().toString();


                if (PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)) {

                    Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                    startActivity(callIntent);
                } else {

                    Toast.makeText(BookingDetail.this, "Sdt invalid", Toast.LENGTH_SHORT).show();
                }
            }
        });
        save_app_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shopId = getSharedPreferences("ShopPrefs", 0).getString("shopId",null);
                CollectionReference appointmentsRef = FirebaseFirestore.getInstance().collection("shops").document(shopId).collection("appointments");

                //get data timestamp from view;
                itemData.setTime(res_time_rec.getSelectedItem().toString());
                appointmentsRef.document(itemData.getAppointmentId()).set(itemData);
                onBackPressed();
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
                null, "/" + psid, null, HttpMethod.GET,
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        if (response.getError() != null) {
                            Log.e("GraphAPI", "Error: " + response.getError().getErrorMessage());
                            // vì app này sử dụng fb sdk ở chế độ test nên fb không cho phép
                            // lấy dữ liệu từ người dùng bên ngoài scope test,
                            // thay link anh to default and fb name to fb psid instead
                            UpdateUI("https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Ftse3.mm.bing.net%2Fth%3Fid%3DOIP.LZ3s7MG_gOSuQTiN0IxmmgAAAA%26pid%3DApi&f=1&ipt=bae5104cf07479330e9614677eac66fd44cbf47d657b36e80fa3b0701686d18e&ipo=images",psid);
                        } else {
                            try {
                                JSONObject jsonObject = response.getJSONObject();

                                String profilename = jsonObject.optString("name");
                                String profilePic = jsonObject.optString("profile_pic");

                                UpdateUI(profilePic,profilename);

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
    public void UpdateUI(String ProfilePicUrl,String profileName){

        textViewReServId.setText(itemData.getShortId());
        try{
            Glide.with(this).load(ProfilePicUrl).into(clientProfilePic);
            Glide.with(this).load(itemData.getReferencePicture()).into(reference_image);
        }
        catch (Exception e){
            Log.d("TAG", "UpdateUI: simply not have, unhandled file or EON");
        }
        your_name_rec.setText(itemData.getCustomerName());
        your_phone_rec.setText(itemData.getCustomerPhone());
        your_namefb_rec.setText(profileName);
        res_date_rec.setText(itemData.getDate());
        res_type_rec.setText(itemData.getServiceId());
        res_status_rec.setText(itemData.getStatus());
        res_cost_rec.setText("50,000 VND");
        res_note_rec.setText(itemData.getNote());

        if(itemData.getTime() !=null){
            int position = Arrays.asList(timestamps).indexOf(itemData.getTime());
            if (position != -1) {
                res_time_rec.setSelection(position);
            }

        }



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
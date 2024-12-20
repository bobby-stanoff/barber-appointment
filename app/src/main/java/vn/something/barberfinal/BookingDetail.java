package vn.something.barberfinal;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

import org.json.JSONObject;

import vn.something.barberfinal.DataModel.Appointment;

public class BookingDetail extends AppCompatActivity {
    Appointment itemData = null;

    TextView textViewReServId;
    ImageView clientProfilePic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking_detail);

        itemData = (Appointment) getIntent().getSerializableExtra("item");
        textViewReServId = findViewById(R.id.reservation_id_textview);
        clientProfilePic = findViewById(R.id.clientProfilePic);
        getUserInfo(itemData.getMessengerUserId());
        ImageView closeButton = findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Button openMessengerButton = findViewById(R.id.btn_open_messenger);
        openMessengerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openExternal();
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
}
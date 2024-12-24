package vn.something.barberfinal;

import static android.app.PendingIntent.getActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import vn.something.barberfinal.DataModel.BarberService;

public class AddServiceActivity extends AppCompatActivity {
    private EditText serviceNameEditText, serviceDurationEditText, servicePriceEditText;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_service);
        serviceNameEditText = findViewById(R.id.service_name);
        serviceDurationEditText = findViewById(R.id.service_duration);
        servicePriceEditText = findViewById(R.id.service_price);
        saveButton = findViewById(R.id.save_button);

        saveButton.setOnClickListener(v -> {

            String name = serviceNameEditText.getText().toString();
            String duration = serviceDurationEditText.getText().toString();
            String price = servicePriceEditText.getText().toString();


            BarberService newService = new BarberService(name, duration, price);

            addServicetoFireBase(newService);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("new_service", newService);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
    public void addServicetoFireBase(BarberService service){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        String shopId = getSharedPreferences("ShopPrefs", 0).getString("shopId",null);
        CollectionReference serviceRef = database.collection("shops").document(shopId).collection("services");

        serviceRef.add(service)
                .addOnSuccessListener(aVoid -> {
                    Log.d("firestoresave", "appointment saved successfully");
                })
                .addOnFailureListener(e -> {
                    Log.e("firestoresave", "Error saving appointment", e);
                });

    }
}
package vn.something.barberfinal;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.Manifest;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.ScanIntentResult;
import com.journeyapps.barcodescanner.ScanOptions;

import vn.something.barberfinal.DataModel.Appointment;


public class QRScanActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST = 101;
    private CaptureManager capture;
    private ActivityResultLauncher<Intent> scanQrResultLauncher;
    FirebaseFirestore database = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_qrscan);
        scanQrResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                resultData ->{
                    if (resultData.getResultCode() == RESULT_OK) {
                        ScanIntentResult result = ScanIntentResult.parseActivityResult(resultData.getResultCode(), resultData.getData());

                        //this will be qr activity result
                        if (result.getContents() == null) {
                            Toast.makeText(this, "cancelled", Toast.LENGTH_LONG).show();
                            

                        } else {
                            String qrContents = result.getContents();
                            Log.d("QRScanActivity", "Scanned data " + qrContents);
                            //TODO Handle qr result here
                            tryOpenDetail(qrContents);
                        }

                    }
                });
        checkCameraPermissionAndStartScanner();

    }
    private void checkCameraPermissionAndStartScanner() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST);
        } else {

            startQrScanner();
        }
    }
    private void startQrScanner() {

        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Scan Appointment Id");
        options.setOrientationLocked(true);

        scanQrResultLauncher.launch(options.createScanIntent(this));
        //finish();

    }
    private void tryOpenDetail(String appointmentId){
        String shopId = getSharedPreferences("ShopPrefs",MODE_PRIVATE).getString("shopId",null);
        DocumentReference userRef = database.collection("shops").document(shopId).collection("appointments").document(appointmentId);
        userRef.get().addOnCompleteListener(documentSnapshot ->{
            if(documentSnapshot.isSuccessful()){
                DocumentSnapshot document = documentSnapshot.getResult();
                if(document.exists()){
                    Appointment appointment = document.toObject(Appointment.class);
                    Intent intent = new Intent(this, BookingDetail.class);
                    intent.putExtra("item", appointment);
                    startActivity(intent);
                }

            }
            else {
                Log.d("TAG", "tryOpenDetail: "+ documentSnapshot.getException());
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted, start scanner
                startQrScanner();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}
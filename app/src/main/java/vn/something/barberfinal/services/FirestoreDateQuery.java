package vn.something.barberfinal.services;


import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FirestoreDateQuery {

    private static final String TAG = "FirestoreDateQuery";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();


    private final String appointmentsCollection = "appointments";

    public interface FirestoreQueryCallback {
        void onDataLoaded(String report);
    }

    public void getAppointmentsForCurrentMonth(FirestoreQueryCallback callback) {

        // 1. Get start and end date of current month
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1); // Start of the month
        Date startOfMonth = calendar.getTime();

        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DATE, -1); //End of Month
        Date endOfMonth = calendar.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        //3. Query firestore
        db.collection(appointmentsCollection)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        StringBuilder reportData = new StringBuilder();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String dateString = document.getString("date");
                            if (dateString == null) continue;

                            try {
                                Date appointmentDate = sdf.parse(dateString);
                                if (appointmentDate != null &&
                                        appointmentDate.compareTo(startOfMonth) >= 0 &&
                                        appointmentDate.compareTo(endOfMonth) <= 0
                                ) {
                                    //Convert report to string
                                    reportData.append(document.getData().toString()).append("\n");
                                }

                            } catch (ParseException e) {
                                Log.e(TAG, "ParseException " + e.getMessage());
                                continue;
                            }
                        }
                        callback.onDataLoaded(reportData.toString());
                    } else {
                        Log.e(TAG, "Error getting document", task.getException());
                        callback.onDataLoaded("Error Loading data");
                    }
                });

    }
}
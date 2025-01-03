package vn.something.barberfinal.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import vn.something.barberfinal.DataModel.Appointment;
import vn.something.barberfinal.DataModel.BarberService;
import vn.something.barberfinal.DataModel.BarberShop;
import vn.something.barberfinal.R;
import vn.something.barberfinal.databinding.FragmentNotificationsBinding;
import vn.something.barberfinal.services.FirestoreDateQuery;

public class NotificationsFragment extends Fragment {
    private TextView dailyBookingsTitle;
    private TextView dailyBookingsData;
    private TextView reportDataTextView;
    private TextView monthlyBookingsTitle;
    private TextView monthlyBookingsData;
    private TextView servicePopularityTitle;
    private TextView servicePopularityData;
    private TextView cancellationTitle;
    private TextView cancellationData;
    private TextView timeSlotTitle;
    private TextView timeSlotData;


    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initializeViews(root);
        FirebaseFirestore.getInstance().collection("shops").whereEqualTo("firebaseUserUid", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnCompleteListener(querySnapshotTask ->{
                    if(querySnapshotTask.isSuccessful()){
                        for(QueryDocumentSnapshot doc : querySnapshotTask.getResult()){
                            BarberShop barberShop = doc.toObject(BarberShop.class);
                            reportDataTextView.setText(barberShop.getName());
                        }
                    }

                });


        generateAllReports();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void initializeViews(View view){
        dailyBookingsTitle = view.findViewById(R.id.dailyBookingsTitle);
        dailyBookingsData = view.findViewById(R.id.dailyBookingsData);
        monthlyBookingsTitle = view.findViewById(R.id.monthlyBookingsTitle);
        monthlyBookingsData = view.findViewById(R.id.monthlyBookingsData);
        reportDataTextView = view.findViewById(R.id.reportDataTextView);

    }
    private void generateAllReports() {
        generateDailyReport();
        generateMonthlyReport();
        //generateServiceReport();
        //generateCancellationReport();
        generateMoneyReport();
    }

    private void generateDailyReport() {

        String shopId = getActivity().getSharedPreferences("ShopPrefs", 0).getString("shopId",null);
        CollectionReference appointmentsRef = FirebaseFirestore.getInstance().collection("shops").document(shopId).collection("appointments");

        appointmentsRef.whereEqualTo("date",getTodayDate()).get().addOnSuccessListener(queryDocumentSnapshots -> {

            int msum = 0, cancelcount = 0;
            for (QueryDocumentSnapshot item: queryDocumentSnapshots
            ) {
                Appointment appointment = item.toObject(Appointment.class);
                appointment.setAppointmentId(item.getId());

                if(appointment.getStatus().equals("FINISHED")){
                    msum += 1;
                }
                else if( appointment.getStatus().equals("CANCELLED")){
                    cancelcount +=1;
                }


            }
            String reportData = "Tổng lịch hẹn hôm nay: "+queryDocumentSnapshots.size()+ " \n" +
                    "Đã hoàn thành: "+msum+"\n"+
                    "Đã hủy: "+cancelcount;
            dailyBookingsData.setText(reportData);



        });

    }
    private void generateMonthlyReport() {
        String shopId = getActivity().getSharedPreferences("ShopPrefs", 0).getString("shopId",null);
        FirestoreDateQuery.getAppointmentsForCurrentMonth(shopId,new FirestoreDateQuery.FirestoreQueryCallback() {
            @Override
            public void onDataLoaded(ArrayList<Appointment> appointments) {

                int msum = 0, cancelcount = 0;
                for (Appointment appo : appointments){
                    if(appo.getStatus().equals("FINISHED")){
                        msum += 1;
                    }
                    else if( appo.getStatus().equals("CANCELLED")){
                        cancelcount +=1;
                    }
                }
                String reportData = "Tổng lịch hẹn tháng này: "+appointments.size()+ " \n" +
                        "Đã hoàn thành: "+msum+"\n"+
                        "Đã hủy: "+cancelcount;
                monthlyBookingsData.setText(reportData);

            }
        });

    }
//    private void generateServiceReport() {
//        Map<String, Integer> serviceCounts = new HashMap<>();
//        String shopId = getActivity().getSharedPreferences("ShopPrefs", 0).getString("shopId",null);
//        CollectionReference appointmentsRef = FirebaseFirestore.getInstance().collection("shops").document(shopId).collection("appointments");
//        CollectionReference serviceRef = FirebaseFirestore.getInstance().collection("shops").document(shopId).collection("services");
//
//
//        appointmentsRef.get().addOnSuccessListener(appointmentSnapshot -> {
//
//            for (QueryDocumentSnapshot item: appointmentSnapshot
//            ) {
//                Appointment appointment = item.toObject(Appointment.class);
//                if (appointment == null || appointment.getServiceId() == null) {
//                    Log.w("ServiceReport", "Invalid appointment or service ID: " + item.getId());
//                    continue;
//                }
//                String apserviceId = appointment.getServiceId();
//                serviceCounts.put(apserviceId, serviceCounts.getOrDefault(apserviceId, 0) + 1);
//
//            }
//            StringBuilder reportBuilder = new StringBuilder();
//            for(Map.Entry<String, Integer> entry : serviceCounts.entrySet()) {
//                String serviceId = entry.getKey();
//                int count = entry.getValue();
//                getServiceNameFromId(serviceId, new OnServiceNameRetrievedCallback() {
//                    @Override
//                    public void onServiceNameRetrieved(String serviceName) {
//                        if(serviceName != null) {
//                            reportBuilder.append(serviceName).append(": ").append(count).append(" Lịch hẹn\n");
//                            String reportData = reportBuilder.toString();
//                            servicePopularityData.setText(reportData);
//                        }
//                    }
//                });
//
//
//
//            }
//
//
//        });
//
//
//    }

    private void generateMoneyReport(){

    }
    private static String getTodayDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        return dateFormat.format(calendar.getTime());
    }
    private void getServiceNameFromId(String serviceId,final OnServiceNameRetrievedCallback callback) {
        String shopId = getActivity().getSharedPreferences("ShopPrefs", 0).getString("shopId", null);

        CollectionReference serviceRef = FirebaseFirestore.getInstance().collection("shops").document(shopId).collection("services");
        serviceRef.document(serviceId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                BarberService service = documentSnapshot.toObject(BarberService.class);
                callback.onServiceNameRetrieved(service.getName());
            } else {
                callback.onServiceNameRetrieved(null);
            }
        });



    }
    public interface OnServiceNameRetrievedCallback {
        void onServiceNameRetrieved(String serviceName);

    }

}
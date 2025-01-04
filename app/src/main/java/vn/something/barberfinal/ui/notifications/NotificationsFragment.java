package vn.something.barberfinal.ui.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import vn.something.barberfinal.BookingDetail;
import vn.something.barberfinal.DataModel.Appointment;
import vn.something.barberfinal.DataModel.BarberService;
import vn.something.barberfinal.DataModel.BarberShop;
import vn.something.barberfinal.DataModel.ScheduleData;
import vn.something.barberfinal.R;
import vn.something.barberfinal.ScheduleLayoutActivity;
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
    private TableLayout scheduleTable;

    ArrayList<Appointment> appointments = new ArrayList<>();
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

        String shopId = getActivity().getSharedPreferences("ShopPrefs", 0).getString("shopId",null);
        CollectionReference appointmentsRef = FirebaseFirestore.getInstance().collection("shops").document(shopId).collection("appointments");

        appointmentsRef.get().addOnSuccessListener(queryDocumentSnapshots -> {


            for (QueryDocumentSnapshot item: queryDocumentSnapshots
            ) {
                Appointment appointment = item.toObject(Appointment.class);
                appointment.setAppointmentId(item.getId());
                appointments.add(appointment);

            }
            generateAllReports();

        });


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
        scheduleTable = view.findViewById(R.id.scheduleTable);



    }
    private void generateAllReports() {
        generateDailyReport();
        generateMonthlyReport();
        //generateServiceReport();
        //generateCancellationReport();
        //generateMoneyReport();
        ScheduleData scheduleData = new ScheduleData();
        for (Appointment item: appointments){
            if (item.getDate() != null && item.getTime() != null){
                //appointment date string: dd/mm/yyyy;
                //scheduleData save date as :dd/mm;
                //==> convert first;
                String converteddate = item.getDate().substring(0,5);
                scheduleData.addData(converteddate, item.getTime(),item);
            }
        }
        generateSchedule(scheduleData);
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
    private void generateSchedule(ScheduleData scheduleData) {
        // Timestamps (Rows)
        String[] timestamps = {"08:00","09:00","10:00","11:00", "12:00", "13:00", "14:00", "15:00", "16:00"};

        // Current Date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());


        // Add header row for Dates
        TableRow headerRow = new TableRow(getContext());
        addTextViewToRow(headerRow, "", true); // empty first cell
        for (int day = 0; day < 7; day++){
            String formattedDate = dateFormat.format(calendar.getTime());
            addTextViewToRow(headerRow, formattedDate, true);
            if(day != 6){
                addSeparator(headerRow);
            }
            calendar.add(Calendar.DATE, 1);
        }
        scheduleTable.addView(headerRow);

        calendar = Calendar.getInstance(); //Reset Calendar for subsequent rows

        // Generate Schedule Table
        for(String timestamp: timestamps) {

            TableRow tableRow = new TableRow(getContext());
            addTextViewToRow(tableRow, timestamp, true); // Add timestamp as the first cell

            // Generate Column For each Date
            for (int day = 0; day < 7; day++) {
                String currentDay = dateFormat.format(calendar.getTime());

                Appointment currentAppointment = scheduleData.getAppointment(currentDay,timestamp);
                if(currentAppointment != null){
                    addTextViewToRow(tableRow, currentAppointment.getShortId(), false);
                    if(day != 6) addSeparator(tableRow);
                }
                else {
                    addTextViewToRow(tableRow, "", false);
                    if(day != 6) addSeparator(tableRow);
                }


                calendar.add(Calendar.DATE, 1);
            }
            calendar.setTime(new Date()); // Reset calendar
            scheduleTable.addView(tableRow);
        }
    }

    private void addSeparator(TableRow row){
        View separator = new View(getContext());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(1, TableRow.LayoutParams.MATCH_PARENT); // Line Width and Height
        separator.setLayoutParams(lp);
        separator.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        row.addView(separator);

    }
    private void addTextViewToRow(TableRow row, String text, boolean isBold){
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setPadding(20,20,20,20);
        if(isBold){
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16); // Set larger text size for date headers
            textView.setTypeface(textView.getTypeface(), android.graphics.Typeface.BOLD);
        }

        row.addView(textView);

    }

    public interface OnServiceNameRetrievedCallback {
        void onServiceNameRetrieved(String serviceName);

    }

}
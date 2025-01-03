package vn.something.barberfinal.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import vn.something.barberfinal.DataModel.Appointment;
import vn.something.barberfinal.R;
import vn.something.barberfinal.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment {
    private TextView dailyBookingsTitle;
    private TextView dailyBookingsData;

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
        servicePopularityTitle = view.findViewById(R.id.servicePopularityTitle);
        servicePopularityData = view.findViewById(R.id.servicePopularityData);
        cancellationTitle = view.findViewById(R.id.cancellationTitle);
        cancellationData = view.findViewById(R.id.cancellationData);
        timeSlotTitle = view.findViewById(R.id.timeSlotTitle);
        timeSlotData = view.findViewById(R.id.timeSlotData);
    }
    private void generateAllReports() {
        generateDailyReport();
        generateMonthlyReport();
        generateServiceReport();
        generateCancellationReport();
        generateTimeSlotReport();
    }

    private void generateDailyReport() {
        ArrayList<Appointment> dataList = new ArrayList<>();
        String shopId = getActivity().getSharedPreferences("ShopPrefs", 0).getString("shopId",null);
        CollectionReference appointmentsRef = FirebaseFirestore.getInstance().collection("shops").document(shopId).collection("appointments");

        appointmentsRef.whereEqualTo("date",getTodayDate()).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot item: queryDocumentSnapshots
            ) {
                Appointment appointment = item.toObject(Appointment.class);
                appointment.setAppointmentId(item.getId());
                dataList.add(appointment);
            }
            int msum = 0, cancelcount = 0;
            for (Appointment appo : dataList){
                if(appo.getStatus().equals("FINISHED")){
                    msum += 1;
                }
                else if( appo.getStatus().equals("CANCELLED")){
                    cancelcount +=1;
                }
            }
            String reportData = "Tổng lịch hẹn hôm nay: "+dataList.size()+ " \n" +
                    "Đã hoàn thành: "+msum+"\n"+
                    "Đã hủy: "+cancelcount;
            dailyBookingsData.setText(reportData);
        });

    }
    private void generateMonthlyReport() {

        String reportData = "";
        monthlyBookingsData.setText(reportData);
    }
    private void generateServiceReport() {

        String reportData = "";
        servicePopularityData.setText(reportData);
    }
    private void generateCancellationReport(){

        String reportData = "C: 5\n" +
                " 'Cắt tóc': 2\n";
        cancellationData.setText(reportData);
    }

    private static String getTodayDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        return dateFormat.format(calendar.getTime());
    }
}
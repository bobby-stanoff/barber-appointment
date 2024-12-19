package vn.something.barberfinal.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import vn.something.barberfinal.BookingDetail;
import vn.something.barberfinal.DataModel.Appointment;
import vn.something.barberfinal.R;
import vn.something.barberfinal.adapter.CardAdapterBooking;

public class UpCommingFragment extends Fragment implements CardAdapterBooking.OnItemClickListener{
    private RecyclerView recyclerView;
    private TextView emptyText;
    private CardAdapterBooking cardAdapter;
    private List<Appointment> dataList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.upcoming_fragment_tab, container, false);
        emptyText = root.findViewById(R.id.empty_text);
        recyclerView = root.findViewById(R.id.recyclerViewBookingCardu);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        emptyText.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        final SwipeRefreshLayout pullToRefresh = root.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAppointments();
                pullToRefresh.setRefreshing(false);
            }
        });
        getAppointments();

        return root;
    }
    public void getAppointments(){
        dataList = new ArrayList<>();
        ///request data from firebase or some shit
        String shopId = getActivity().getSharedPreferences("ShopPrefs", 0).getString("shopId",null);
        db.collection("shops").document(shopId).collection("appointments")
                .whereEqualTo("status","UPCOMING")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc: queryDocumentSnapshots
                    ) {
                        Appointment appointment = doc.toObject(Appointment.class);
                        appointment.setAppointmentId(doc.getId());
                        dataList.add(appointment);
                    }
                    emptyText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    cardAdapter = new CardAdapterBooking(dataList, this);
                    recyclerView.setAdapter(cardAdapter);

                }).addOnFailureListener(fail ->{
                    if(dataList == null || dataList.isEmpty()){
                        emptyText.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                });

    }

    @Override
    public void onItemClick(int position) {
        Appointment clickedItem = dataList.get(position);

        Intent intent = new Intent(getContext(), BookingDetail.class);
        intent.putExtra("item", clickedItem);
        startActivity(intent);
    }

    @Override
    public void onDeclineClick(int position) {
        Appointment item = dataList.get(position);
        dataList.remove(position);
        cardAdapter.notifyItemRemoved(position);
        item.setStatus("CANCELLED");
        String shopId = getActivity().getSharedPreferences("ShopPrefs", 0).getString("shopId",null);
        CollectionReference appointmentsRef = FirebaseFirestore.getInstance().collection("shops").document(shopId).collection("appointments");

        appointmentsRef.document(item.getAppointmentId()).set(item);

    }

    @Override
    public void onAcceptClick(int position) {
        Appointment item = dataList.get(position);
        dataList.remove(position);
        cardAdapter.notifyItemRemoved(position);
        Toast.makeText(getContext(), "Card ben tab sap toi: " + item, Toast.LENGTH_SHORT).show();

        item.setStatus("FINISHED");

        String shopId = getActivity().getSharedPreferences("ShopPrefs", 0).getString("shopId",null);
        CollectionReference appointmentsRef = FirebaseFirestore.getInstance().collection("shops").document(shopId).collection("appointments");

        appointmentsRef.document(item.getAppointmentId()).set(item);
    }

}

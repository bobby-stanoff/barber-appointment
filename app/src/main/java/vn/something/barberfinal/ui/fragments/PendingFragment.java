package vn.something.barberfinal.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import vn.something.barberfinal.BookingDetail;
import vn.something.barberfinal.DataModel.Appointment;
import vn.something.barberfinal.R;
import vn.something.barberfinal.adapter.CardAdapterBooking;

public class PendingFragment extends Fragment implements CardAdapterBooking.OnItemClickListener{
    private RecyclerView recyclerView;
    private CardAdapterBooking cardAdapter;
    private TextView emptyText;
    private List<Appointment> dataList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.pending_fragment_tab, container, false);

        emptyText = root.findViewById(R.id.empty_text);
        recyclerView = root.findViewById(R.id.recyclerViewBookingCard);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        emptyText.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        getAppointments();

        return root;
    }

    private void getAppointments(){
        String shopId = getActivity().getSharedPreferences("ShopPrefs", 0).getString("shopId",null);
        CollectionReference appointmentsRef = FirebaseFirestore.getInstance().collection("shops").document(shopId).collection("appointments");

        appointmentsRef.whereEqualTo("status","PENDING").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot item: queryDocumentSnapshots
                 ) {
                Appointment appointment = item.toObject(Appointment.class);
                appointment.setAppointmentId(item.getId());
                dataList.add(appointment);

                emptyText.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
            prepareCardView();
        }).addOnFailureListener(onfailuer -> {
            if(dataList == null || dataList.isEmpty()){
                emptyText.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }

        });


    }
    private void prepareCardView(){
        cardAdapter = new CardAdapterBooking(dataList, this);
        recyclerView.setAdapter(cardAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(0, ItemTouchHelper.RIGHT);
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Appointment item = dataList.get(position);

                if (direction == ItemTouchHelper.RIGHT ) {
                    dataList.remove(position);
                    cardAdapter.notifyItemRemoved(position);
                    Toast.makeText(getContext(), "Card ben tab sap toi: " + item, Toast.LENGTH_SHORT).show();
                    //push data to firebase or some shit, change swipe interface with button because conflict with tab view
                    // save to local db first then push to firebase later
                    item.setStatus("UPCOMING");

                    String shopId = getActivity().getSharedPreferences("ShopPrefs", 0).getString("shopId",null);
                    CollectionReference appointmentsRef = FirebaseFirestore.getInstance().collection("shops").document(shopId).collection("appointments");

                    appointmentsRef.document(item.getAppointmentId()).set(item);

                }
            }
        });

        // Attach the ItemTouchHelper to the RecyclerView
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
    @Override
    public void onItemClick(int position) {
        Appointment clickedItem = dataList.get(position);

        Intent intent = new Intent(getContext(), BookingDetail.class);
        intent.putExtra("item", "clickedItem");
        startActivity(intent);
    }
}

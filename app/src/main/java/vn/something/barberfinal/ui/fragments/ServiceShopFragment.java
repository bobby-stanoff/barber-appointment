package vn.something.barberfinal.ui.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;

import vn.something.barberfinal.AddServiceActivity;
import vn.something.barberfinal.DataModel.Appointment;
import vn.something.barberfinal.DataModel.BarberService;
import vn.something.barberfinal.R;
import vn.something.barberfinal.adapter.CardAdapterBooking;
import vn.something.barberfinal.adapter.CardAdapterService;

public class ServiceShopFragment extends Fragment {
    RecyclerView recyclerView;
    ArrayList<BarberService> services;
    Button addServiceButton;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.service_shop_fragment, container, false);
        recyclerView = root.findViewById(R.id.recycler_view_service);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        services = new ArrayList<>();
        fetchService();
        CardAdapterService cardAdapter = new CardAdapterService(services, new CardAdapterService.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
            }
            @Override
            public void onLongItemClick(int posititon) {
                showDeleteDialog(posititon);
            }
        });
        recyclerView.setAdapter(cardAdapter);

        addServiceButton = root.findViewById(R.id.add_service_button);
        addServiceButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddServiceActivity.class);
            startActivityForResult(intent, 1);

        });
        return root;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {


            fetchService();

        }
    }
    private void showDeleteDialog(int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Service")
                .setMessage("Are you sure you want to delete this service?")
                .setPositiveButton("Yes", (dialog, which) -> {

                    RemoveServiceFromFireBase(position);

                })
                .setNegativeButton("No", null)
                .show();
    }
    public void fetchService(){
        services.clear();
        String shopId = getActivity().getSharedPreferences("ShopPrefs", 0).getString("shopId",null);
        CollectionReference servicesCollection = FirebaseFirestore.getInstance().collection("shops").document(shopId).collection("services");

        servicesCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot item: queryDocumentSnapshots
            ) {
                BarberService appointment = item.toObject(BarberService.class);
                services.add(appointment);
            }
            recyclerView.getAdapter().notifyDataSetChanged();

        }).addOnFailureListener(onfailuer -> {
            Log.d("TAG", "fetchService: error fetching service");

        });

    }
    private void RemoveServiceFromFireBase(int position){

        BarberService servicerm = services.get(position);
        services.remove(position);


        String shopId = getActivity().getSharedPreferences("ShopPrefs", 0).getString("shopId",null);
        CollectionReference servicesCollection = FirebaseFirestore.getInstance().collection("shops").document(shopId).collection("services");

        servicesCollection.document(servicerm.getId()).delete().addOnSuccessListener(t -> {
            recyclerView.getAdapter().notifyItemRemoved(position);
        });
    }
}

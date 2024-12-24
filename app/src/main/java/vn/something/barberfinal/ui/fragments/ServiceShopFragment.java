package vn.something.barberfinal.ui.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

import vn.something.barberfinal.AddServiceActivity;
import vn.something.barberfinal.DataModel.BarberService;
import vn.something.barberfinal.R;
import vn.something.barberfinal.adapter.CardAdapterBooking;
import vn.something.barberfinal.adapter.CardAdapterService;

public class ServiceShopFragment extends Fragment {
    RecyclerView recyclerView;
    ArrayList<BarberService> services;
    Button addServiceButton;
    //ArrayList<String> services;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.service_shop_fragment, container, false);

        recyclerView = root.findViewById(R.id.recycler_view_service);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        services = new ArrayList<>();
        BarberService sv1 = new BarberService("hair cut", "30 minutes", "50USD");
        BarberService sv2 = new BarberService("hair dress", "30 minutes", "150USD");
        services.add(sv1);
        services.add(sv2);
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

            BarberService newService = (BarberService) data.getSerializableExtra("new_service");

            services.add(newService);
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }
    private void showDeleteDialog(int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Service")
                .setMessage("Are you sure you want to delete this service?")
                .setPositiveButton("Yes", (dialog, which) -> {

                    services.remove(position);
                    recyclerView.getAdapter().notifyItemRemoved(position);
                })
                .setNegativeButton("No", null)
                .show();
    }
}

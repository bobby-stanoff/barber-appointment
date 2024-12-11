package vn.something.barberfinal.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

import vn.something.barberfinal.DataModel.BarberService;
import vn.something.barberfinal.R;
import vn.something.barberfinal.adapter.CardAdapterBooking;
import vn.something.barberfinal.adapter.CardAdapterService;

public class ServiceShopFragment extends Fragment {
    RecyclerView recyclerView;
    //ArrayList<BarberService> services;
    ArrayList<String> services;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.service_shop_fragment, container, false);

        recyclerView = root.findViewById(R.id.recycler_view_service);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        services = new ArrayList<>();
        services.add("hi");
        services.add("egwg");
        CardAdapterService cardAdapter = new CardAdapterService(services);
        recyclerView.setAdapter(cardAdapter);
        return root;
    }
}

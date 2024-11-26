package vn.something.barberfinal.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vn.something.barberfinal.BookingDetail;
import vn.something.barberfinal.R;
import vn.something.barberfinal.adapter.CardAdapterBooking;

public class UpCommingFragment extends Fragment implements CardAdapterBooking.OnItemClickListener{
    private RecyclerView recyclerView;
    private CardAdapterBooking cardAdapter;
    private List<String> dataList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.upcoming_fragment_tab, container, false);
        ///request data from firebase or some shit
        System.out.println("recivedBund");
        recyclerView = root.findViewById(R.id.recyclerViewBookingCardu);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        dataList = new ArrayList<>(Arrays.asList("Item1", "Item2", "Item3"));
        cardAdapter = new CardAdapterBooking(dataList, this);
        recyclerView.setAdapter(cardAdapter);

        return root;
    }


    @Override
    public void onItemClick(int position) {
        String clickedItem = dataList.get(position);

        Intent intent = new Intent(getContext(), BookingDetail.class);
        intent.putExtra("item", clickedItem);
        startActivity(intent);
    }
}

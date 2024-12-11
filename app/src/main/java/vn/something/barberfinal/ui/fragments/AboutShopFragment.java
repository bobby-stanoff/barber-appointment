package vn.something.barberfinal.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import vn.something.barberfinal.DataModel.BarberShop;
import vn.something.barberfinal.R;

public class AboutShopFragment extends Fragment {
    private BarberShop barberShop;
    private TextView shop_phone,shop_email,shop_address,shop_website;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.about_shop_fragment, container, false);


        if (getArguments() != null) {
            barberShop = (BarberShop) getArguments().getSerializable("barberShop");
        }
        shop_phone = root.findViewById(R.id.shop_phone);
        shop_email = root.findViewById(R.id.shop_email);
        shop_address = root.findViewById(R.id.shop_address);
        shop_website = root.findViewById(R.id.shop_website);
        shop_phone.setText(barberShop.getPhoneNumber());
        shop_email.setText(barberShop.getMail());
        shop_address.setText(barberShop.getAddress());
        shop_website.setText(barberShop.getWebsite());
        return root;
    }
}

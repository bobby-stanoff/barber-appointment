package vn.something.barberfinal.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import vn.something.barberfinal.DataModel.BarberShop;
import vn.something.barberfinal.ui.fragments.AboutShopFragment;
import vn.something.barberfinal.ui.fragments.PendingFragment;
import vn.something.barberfinal.ui.fragments.ServiceShopFragment;
import vn.something.barberfinal.ui.fragments.ShopSettingFragment;
import vn.something.barberfinal.ui.fragments.UpCommingFragment;

public class AboutShopAdapter extends FragmentStateAdapter  {
    BarberShop data;
    public AboutShopAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public AboutShopAdapter(FragmentManager childFragmentManager, Lifecycle lifecycle) {
        super(childFragmentManager,lifecycle);
    }
    public AboutShopAdapter(FragmentManager childFragmentManager, BarberShop data, Lifecycle lifecycle){
        super(childFragmentManager,lifecycle);
        this.data = data;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new AboutShopFragment();
                break;
            case 1:
                fragment = new ServiceShopFragment();
                break;
            case 2:
                fragment = new ShopSettingFragment();
                break;
            default:
                fragment = new Fragment();
                break;
        }
        if(data != null){
            Bundle bundle = new Bundle();
            bundle.putSerializable("barberShop", data);
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 3;
    }


}

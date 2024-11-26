package vn.something.barberfinal.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import vn.something.barberfinal.ui.fragments.PendingFragment;
import vn.something.barberfinal.ui.fragments.UpCommingFragment;

public class AppointmentAdapter extends FragmentStateAdapter  {

    public AppointmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public AppointmentAdapter(FragmentManager childFragmentManager, Lifecycle lifecycle) {
        super(childFragmentManager,lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PendingFragment();  // Fragment for Pending appointments
            case 1:
                return new UpCommingFragment(); // Fragment for Upcoming appointments
            case 2:
                return new Fragment();   // Fragment for Recent appointments
            default:
                return new Fragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;  // Three tabs: Pending, Upcoming, Recent
    }


}

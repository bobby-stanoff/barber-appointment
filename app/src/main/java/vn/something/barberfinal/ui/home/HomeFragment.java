package vn.something.barberfinal.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import vn.something.barberfinal.DataModel.BarberShop;
import vn.something.barberfinal.R;
import vn.something.barberfinal.adapter.AboutShopAdapter;
import vn.something.barberfinal.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private FirebaseFirestore database;
    private FragmentHomeBinding binding;
    private ViewPager2 viewPagerAbout;
    private TabLayout tabLayoutAbout;
    private BarberShop barberShop;
    private TextView text_user_name,text_followers_following_count;
    private ImageView image_user;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        viewPagerAbout = root.findViewById(R.id.view_pager_shop);
        tabLayoutAbout = root.findViewById(R.id.tabLayoutAbout);
        text_followers_following_count = root.findViewById(R.id.text_followers_following_count);
        text_user_name = root.findViewById(R.id.text_user_name);
        image_user = root.findViewById(R.id.image_user);

        database = FirebaseFirestore.getInstance();
        database.collection("shops").whereEqualTo("firebaseUserUid", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnCompleteListener(querySnapshotTask ->{
                    if(querySnapshotTask.isSuccessful()){
                        for(QueryDocumentSnapshot doc : querySnapshotTask.getResult()){
                            barberShop = doc.toObject(BarberShop.class);
                            UpdateUi(barberShop);
                        }
                    }
                    else {
                        Log.d("HomeFragment", "onCreateView: Error query document snapshot" + querySnapshotTask.getException());
                    }
                });

        return root;
    }

    private void UpdateUi(BarberShop object) {

        AboutShopAdapter adapter = new AboutShopAdapter(getChildFragmentManager(),object,getLifecycle());
        viewPagerAbout.setAdapter(adapter);
        new TabLayoutMediator(tabLayoutAbout,viewPagerAbout,(tab,pos) ->{
            switch (pos) {
                case 0:
                    tab.setText("Thông tin");

                    break;
                case 1:
                    tab.setText("Dịch vụ");
                    break;
                case 2:
                    tab.setText("Cài đặt");
                    break;
                default:
                    tab.setText("pos");
            }
        }).attach();

        text_user_name.setText(object.getName());
        text_followers_following_count.setText(object.getFollower_counts() + " follows");
        Glide.with(this).load(object.getAvatar_image()).into(image_user);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
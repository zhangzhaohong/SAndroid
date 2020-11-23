package com.tristana.sandroid.ui.illegalManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.tristana.sandroid.R;
import com.tristana.sandroid.ui.illegalManager.fragment.picture.IllegalPictureFragment;
import com.tristana.sandroid.ui.illegalManager.fragment.video.IllegalVideoFragment;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

public class IllegalManagerFragment extends Fragment {

    private IllegalManagerViewModel illegalManagerViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        if (illegalManagerViewModel == null)
            illegalManagerViewModel =
                    ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().getApplication()).create(IllegalManagerViewModel.class);
        View root = inflater.inflate(R.layout.fragment_illegal_manager, container, false);
        illegalManagerViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        final ViewPager2 viewPager2 = root.findViewById(R.id.viewPager2);
        viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        //适配器
        viewPager2.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                Fragment fragment = null;
                switch (position) {
                    case 0:
                        fragment = new IllegalVideoFragment();
                        break;
                    case 1:
                        fragment = new IllegalPictureFragment();
                        break;
                }
                return Objects.requireNonNull(fragment);
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        });
        viewPager2.setOffscreenPageLimit(1);
        //tab关联viewPager2
        final TabLayout tabLayout = root.findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager2, true, true, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.isSelected();
                        tab.setText(getString(R.string.title_illegal_video));
                        break;
                    case 1:
                        tab.isSelected();
                        tab.setText(getString(R.string.title_illegal_picture));
                        break;
                }
            }
        }).attach();
        return root;
    }
}
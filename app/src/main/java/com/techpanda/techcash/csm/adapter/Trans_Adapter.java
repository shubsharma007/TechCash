package com.techpanda.techcash.csm.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.techpanda.techcash.csm.fragment.HistoryFragment;
import com.techpanda.techcash.csm.fragment.RequestFragment;

public class Trans_Adapter extends FragmentStateAdapter {
    public Trans_Adapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position)
        {
            case 0:
                return new HistoryFragment();
            case 1:
                return new RequestFragment();
            default:
                return new HistoryFragment();

        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}

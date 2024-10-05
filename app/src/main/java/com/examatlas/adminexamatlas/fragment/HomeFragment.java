package com.examatlas.adminexamatlas.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.examatlas.adminexamatlas.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeFragment extends Fragment {

    public BottomNavigationView homeNavigation;
    public String currentFrag = "SALES";
    Fragment currentFragment ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        homeNavigation = view.findViewById(R.id.home_navigation);
        currentFragment = new SalesFragment();

        loadFragment(currentFragment);
        homeNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.sales) {
                    currentFrag = "SALES";
                    currentFragment = new SalesFragment();
                    loadFragment(new SalesFragment());
                } else if (item.getItemId() == R.id.learner) {
                    currentFrag = "LEARNERS";
                    currentFragment = new LearnersFragment();
                    loadFragment(new LearnersFragment());
                } else if (item.getItemId() == R.id.order) {
                    currentFrag = "ORDERS";
                    currentFragment = new OrderFragment();
                    loadFragment(new OrderFragment());
                } else if (item.getItemId() == R.id.apps) {
                    currentFrag = "APPS";
                    currentFragment = new AppsFragment();
                    loadFragment(new AppsFragment());
                }
                return true;
            }
        });
        return view;
    }
    public void loadFragment(Fragment fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
        currentFragment = fragment;
    }
}

package com.hwhhhh.taskmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import com.hwhhhh.taskmanager.ui.EditFragment.EditFragment;
import com.hwhhhh.taskmanager.ui.detail.DetailFragment;
import com.hwhhhh.taskmanager.ui.main.MainFragment;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private MainFragment mainFragment;
    private EditFragment editFragment;
    private DetailFragment detailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        if (mainFragment == null) {
            mainFragment = new MainFragment();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_host, mainFragment, MainFragment.class.getName())
                .commit();
    }

    public void switchFragment(Fragment from, Fragment to) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag(to.getClass().getName()) == null) {
            fragmentManager.beginTransaction().add(R.id.fragment_host, to, to.getClass().getName())
                    .commit();
        }
        fragmentManager.beginTransaction()
                .hide(from)
                .show(to)
                .addToBackStack(from.getClass().getName())
                .commit();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public EditFragment getEditFragment() {
        if (editFragment == null) {
            editFragment = new EditFragment();
        }
        return editFragment;
    }

    public DetailFragment getDetailFragment() {
        if (detailFragment == null) {
            detailFragment = new DetailFragment();
        }
        return detailFragment;
    }

    public MainFragment getMainFragment() {
        return mainFragment;
    }
}

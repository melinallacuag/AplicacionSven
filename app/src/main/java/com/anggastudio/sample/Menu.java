package com.anggastudio.sample;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.Toast;

import com.anggastudio.sample.Fragment.CierreXFragment;
import com.anggastudio.sample.Fragment.DasboardFragment;

public class Menu extends AppCompatActivity{

    private boolean atDashboard = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new DasboardFragment()).commit();


    }
   @Override
    public void onBackPressed() {

       if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
           getSupportFragmentManager().popBackStack();
       }else{
           getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DasboardFragment()).commit();
       }
    }
}
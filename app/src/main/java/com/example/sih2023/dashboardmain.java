package com.example.sih2023;

import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class dashboardmain extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    public  boolean check=false;
    DatabaseReference databaseReference;
    static String username="";
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_main);
        bottomNavigationView=findViewById(R.id.BottomNavigation_home);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();

                if(id==R.id.Vehicle_icon){
                    loadfrag(new vehicleFragment(),false);
                }
                else if(id==R.id.Setting_icon){
                    loadfrag(new settingFragment(),false);
                }
                else{
                    loadfrag(new MapsFragment(),true);
                }
                return true;
            }

        });
        bottomNavigationView.setSelectedItemId(R.id.ghar_home);
        databaseListner();
    }
    public  void loadfrag(Fragment fragment, boolean flag){
        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        if(flag){
            ft.add(R.id.flFragment,fragment);

        }
        else{
            ft.replace(R.id.flFragment,fragment);
        }
        ft.commit();

    }
    private void databaseListner() {
        databaseReference= FirebaseDatabase.getInstance().getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long s= (Long) snapshot.child("test/distance").getValue();
                String s2= (String) snapshot.child("police/traffic_accidents").getValue();
                int inc=Integer.parseInt(s2);
                int inz = Integer.parseInt(s.toString());
                String id= FirebaseAuth.getInstance().getCurrentUser().getUid();
                if(inz<4 && !check){
                    inc=inc+1;
                    databaseReference.child("police").child("traffic_accidents").setValue(String.valueOf(inc));
                    databaseReference.child("police").child("fir_generated").setValue(String.valueOf(inc));
                    LocalDateTime currentDateTime = LocalDateTime.now();

                    // Define a custom date and time format
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

                    // Format the current date and time using the custom format
                    String formattedDateTime = currentDateTime.format(formatter);
                    databaseReference.child("police").child("fir").child(id).child("time").setValue(String.valueOf(formattedDateTime));
                    databaseReference.child("police").child("fir").child(id).child("location").setValue(String.valueOf("Shreenidhi Institute of Science & Tech."));
                    databaseReference.child("police").child("fir").child(id).child("id").setValue(String.valueOf(id));
                    databaseReference.child("police").child("fir").child(id).child("name").setValue(String.valueOf(username));
                    databaseReference.child("police").child("fir").child(id).child("status").setValue("0");

                    SmsManager sms=SmsManager.getDefault();
                    sms.sendTextMessage("6283018883", null, "Crash Occured at \n https://maps.app." +
                            "goo.gl/Hkm4YGKK6WT7YsYQ7", null,null);
                    check=true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(dashboardmain.this,"Failed",Toast.LENGTH_SHORT).show();

            }
        });
    }
}

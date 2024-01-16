package com.example.sih2023;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;


public class vehicleFragment extends Fragment {

    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://sih2023-9eaea-default-rtdb.firebaseio.com/");
    CircleImageView Vehicle_info_profile;
    String name;

    public vehicleFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment     m
        View view=inflater.inflate(R.layout.fragment_vehicle, container, false);
        TextView dashboardname=view.findViewById(R.id.Name_Vechicle_info);
        Vehicle_info_profile=view.findViewById(R.id.Vehicle_info_profile);
        mAuth= FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        String id=user.getUid();
        StorageReference reference2= FirebaseStorage.getInstance().getReference(id+"/"+id+".jpg");
        try{
            File localfile=File.createTempFile("tempfile",".jpg");
            reference2.getFile(localfile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap= BitmapFactory.decodeFile(localfile.getAbsolutePath());
                    Vehicle_info_profile.setImageBitmap(bitmap);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        name(id,dashboardname,view);
        return view;
    }
    private   void name(String id, TextView dashboard, View view) {
        String nam;
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String nam = snapshot.child(id).child("details").child("fullname").getValue(String.class);
                name=nam;
                Map<String, Object> map2 = (Map<String, Object>) snapshot.child(id).child("two_wheeler").getValue();
                Map<String, Object> map3 = (Map<String, Object>) snapshot.child(id).child("three_wheeler").getValue();
                Map<String, Object> map4 = (Map<String, Object>) snapshot.child(id).child("four_wheeler").getValue();
                Map<String, Object> map5 = (Map<String, Object>) snapshot.child(id).child("other_wheeler").getValue();
                if(map3!=null)
                    map2.putAll(map3);
                if(map4!=null)
                    map2.putAll(map4);
                if(map5!=null)
                    map2.putAll(map5);




                Log.i("map", String.valueOf(map2.keySet()));


                if (nam.trim().length() < 6) {
                    dashboard.setText("Hii, " + nam);
                } else {
                    dashboard.setText("Hii, " + nam.substring(0, 5) + "..");
                }
//                LinearLayout linearlayout = new LinearLayout(getActivity());
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.MATCH_PARENT,
//                        LinearLayout.LayoutParams.WRAP_CONTENT);
//                linearlayout.setLayoutParams(params);
//                linearlayout.setOrientation(LinearLayout.HORIZONTAL);

//                for (Map.Entry<String, Object> entry : map2.entrySet()) {
//                    String key = entry.getKey();
//                    Object value = entry.getValue();
//
//                    Log.i("Vehicle Number for key " , String.valueOf(value));
//                }
                for (int i = 0; i < map2.size(); i++) {

                    final Button myButton = new Button(view.getContext());
                    Set<String> set =map2.keySet();
                    String arr[]=set.toArray(new String[0]);
                    myButton.setText(String.valueOf(arr[i]));
                    myButton.setId(View.generateViewId());
                    myButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            handleButtonClick(myButton.getText().toString());
                        }
                    });

                    myButton.setBackgroundColor(getResources().getColor(R.color.Blue));
                    myButton.setTextSize(18);
                    myButton.setPadding(20, 0, 20, 0);

                    LinearLayout linearlayout = (LinearLayout) view.findViewById(R.id.btnholder);
                    linearlayout.setOrientation(LinearLayout.VERTICAL);
                    linearlayout.setGravity(Gravity.CENTER | Gravity.TOP);

                    LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    buttonParams.setMargins(0, 50, 70, 10);

                    linearlayout.addView(myButton, buttonParams);

                }
                final Button myButton = new Button(view.getContext());
                myButton.setText("Add More Vehicle");
                myButton.setId(1000 + 1);
                myButton.setBackgroundColor(getResources().getColor(R.color.Blue));
                myButton.setTextSize(18);
                myButton.setPadding(20, 0, 20, 0);
                LinearLayout linearlayout = (LinearLayout) view.findViewById(R.id.btnholder);
                linearlayout.setOrientation(LinearLayout.VERTICAL);
                linearlayout.setGravity(Gravity.CENTER | Gravity.TOP);
                LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                buttonParams.setMargins(0, 20, 70, 10);

                linearlayout.addView(myButton, buttonParams);
                myButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i=new Intent(getActivity(),choose_vehicle.class);
                        startActivity(i);
                    }
                });



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void handleButtonClick(String buttonText) {
        // Handle button click, e.g., show a Toast
        Toast.makeText(getActivity(), "Button clicked: " + buttonText, Toast.LENGTH_SHORT).show();
        Intent i= new Intent(getContext(),vehicle_info.class);
        i.putExtra("carnum",buttonText);
        i.putExtra("name",name);
        startActivity(i);
        // You can perform other actions based on the button click
    }

}
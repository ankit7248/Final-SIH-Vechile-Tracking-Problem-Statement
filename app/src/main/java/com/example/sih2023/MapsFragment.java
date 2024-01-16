package com.example.sih2023;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapsFragment extends Fragment {
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://sih2023-9eaea-default-rtdb.firebaseio.com/");
    CircleImageView Dashboard_Profile;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            int height = 150;
            int width = 150;
            BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.locationpin);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
            BitmapDrawable bitmapdraw2 = (BitmapDrawable)getResources().getDrawable(R.drawable.policemap);
            Bitmap b2 = bitmapdraw2.getBitmap();
            Bitmap smallMarker2 = Bitmap.createScaledBitmap(b2, width, height, false);
            BitmapDrawable bitmapdraw3 = (BitmapDrawable)getResources().getDrawable(R.drawable.hospitalmap);
            Bitmap b3 = bitmapdraw3.getBitmap();
            Bitmap smallMarker3 = Bitmap.createScaledBitmap(b3, width, height, false);
            LatLng machhiwara = new LatLng( 17.456908774498686, 78.66511000662098);
            LatLng police = new LatLng(17.460549586068066, 78.66288158543725);
            LatLng hospital = new LatLng(17.45694142671698, 78.65731678129573);
            googleMap.addMarker(new MarkerOptions().position(machhiwara).title("My Location").icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
            googleMap.addMarker(new MarkerOptions().position(police).title("Police Station").icon(BitmapDescriptorFactory.fromBitmap(smallMarker2)));
            googleMap.addMarker(new MarkerOptions().position(hospital).title("Hospital").icon(BitmapDescriptorFactory.fromBitmap(smallMarker3)));

            googleMap.moveCamera(CameraUpdateFactory.newLatLng(machhiwara));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(police));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(hospital));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(machhiwara, 16f));
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_maps, container, false);
        TextView dashboardname=view.findViewById(R.id.Dashboard_Name);
        Dashboard_Profile=view.findViewById(R.id.Dashboard_Profile);

        
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
                    Dashboard_Profile.setImageBitmap(bitmap);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }


        name(id,dashboardname);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
    private   void name(String id, TextView dashboard){
        String nam;
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String nam=snapshot.child(id).child("details").child("fullname").getValue(String.class);
                dashboardmain.username=nam;
                if(nam.trim().length()<6){
                    dashboard.setText("Hii, "+nam);
                }
                else{
                    dashboard.setText("Hii, "+nam.substring(0,5)+"..");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
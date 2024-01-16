package com.example.sih2023;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class signup extends AppCompatActivity {
EditText name,mobile,mail,pass,cpass;
Button signup;
FirebaseAuth mAuth;
FirebaseUser user;
    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReferenceFromUrl("https://sih2023-9eaea-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        name=findViewById(R.id.Name);
        mobile=findViewById(R.id.Mobile);
        mail=findViewById(R.id.Email_id_1);
        pass=findViewById(R.id.Password_2);
        cpass=findViewById(R.id.Re_type_Password);
        signup=findViewById(R.id.Sign_Up_Button_1);
        mAuth=FirebaseAuth.getInstance();
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullname = name.getText().toString();
                String password = pass.getText().toString();
                String mobileno = mobile.getText().toString();
                String email = mail.getText().toString().trim();
                String cpassword = cpass.getText().toString();
                String m=mobileno;
                if(password.equals(cpassword) && !TextUtils.isEmpty(fullname) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(mobileno) && !TextUtils.isEmpty(email)){
                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                AuthResult uid =task.getResult();
                                String id= Objects.requireNonNull(uid.getUser()).getUid();
                                user=mAuth.getCurrentUser();
                                user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(signup.this,"Verification Mail Sent Successfully",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if(snapshot.hasChild(id)){
                                            Toast.makeText(signup.this,"Username already exists",Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            //username is unique
                                            databaseReference.child("users").child(id).child("details").child("password").setValue(password);
                                            databaseReference.child("users").child(id).child("details").child("fullname").setValue(fullname);
                                            databaseReference.child("users").child(id).child("details").child("email").setValue(email);
                                            databaseReference.child("users").child(id).child("details").child("mob").setValue(m);
                                            databaseReference.child("users").child(id).child("details").child("firsttime").setValue("1");

                                            Intent i= new Intent(signup.this,login.class);
                                            startActivity(i);
                                            finish();

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                            else{
                                Toast.makeText(signup.this,"Register error"+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }


                        }
                    });

                }
                else{
                    Toast.makeText(signup.this,"Please Fill details Carefully",Toast.LENGTH_SHORT).show();

                }


            }
        });
    }
}
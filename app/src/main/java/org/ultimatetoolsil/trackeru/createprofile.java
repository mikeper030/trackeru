package org.ultimatetoolsil.trackeru;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import utilities.PicassoClient;

public class createprofile extends AppCompatActivity {
    boolean available = true;
    TextInputEditText name,phone,username;
    int RESULT_LOAD_IMG=1;
    private FirebaseStorage storage;
    ProgressBar pb;
    private FirebaseDatabase mFirebaseDatabase;
    private String userID;
    private FirebaseApp app;
    private FirebaseAuth auth;
    private DatabaseReference myRef;
    private TextView title;
    private String usernamee;
    ImageView imageView;
    ImageButton rotate;
    private int deg=0;
    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
            final Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            final int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            final String picturePath = cursor.getString(columnIndex);
            cursor.close();

            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    pb.setVisibility(View.VISIBLE);
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                   pb.setVisibility(View.VISIBLE);
                }

                @Override
                protected Void doInBackground(Void... voids) {

                    StorageReference ref = storage.getReference("photos").child(userID).child("profilePic");
                    ref.putFile(selectedImage)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    String content = downloadUrl.toString();
                                    myRef.child("users").child(userID).child("picLink").setValue(content);
                                    myRef.child("public").child("public_pics").child(usernamee).child("picLink").setValue(content);
                                    pb.setVisibility(View.INVISIBLE);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {

                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {



                                }
                            });

                    return null;
                }
            }.execute();
        }
     File old = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Trackeru/profile.jpg");
        if(old.exists()){
            old.delete();
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createprofile);
         pb=(ProgressBar)findViewById(R.id.progressBar);
         pb.setIndeterminate(true);
        app = FirebaseApp.getInstance();
        storage =FirebaseStorage.getInstance(app);
         Button upload = (Button) findViewById(R.id.upload);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef=mFirebaseDatabase.getReference();
        auth = FirebaseAuth.getInstance();
        FirebaseUser user =auth.getCurrentUser();
        userID=user.getUid();
        phone = (TextInputEditText) findViewById(R.id.ph);
        name=(TextInputEditText) findViewById(R.id.nm);
        title=(TextView) findViewById(R.id.textView4);
        imageView = (ImageView) findViewById(R.id.profileimg);
        username=(TextInputEditText) findViewById(R.id.username);
        usernamee=PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("username", null);
        if(usernamee==null){
            usernamee=username.getText().toString();
        }
        rotate=(ImageButton)findViewById(R.id.rrotate);


        rotate.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View view) {
                imageView.setRotation(deg+90);
                deg+=90;
                myRef.child("users").child(userID).child("rotation").setValue(deg);


            }
        });
        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission(this)) {
                requestForSpecificPermission(this);
            }
        }
        if(getIntent().getStringExtra("from")!=null){
            myRef.child("users").child(userID).child("picLink").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final String image = dataSnapshot.getValue(String.class);

                        myRef.child("users").child(userID).child("rotation").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                int rotation = dataSnapshot.getValue(int.class);
                               // Log.d("profile",image);
                                PicassoClient.DownloadGroupImage(createprofile.this, image, imageView, rotation);
                                }catch (Exception e){
                                   // Log.d("profile",image);
                                    PicassoClient.DownloadGroupImage(createprofile.this, image, imageView, 0);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                PicassoClient.DownloadGroupImage(createprofile.this, image, imageView, 0);
                            }
                        });


                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });



             title.setText(R.string.profile_update);
         String f =   PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("full_name", null);
            String p =   PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("phone", null);
            String u=   PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("username", null);
            if(f!=null&&u!=null&&p!=null){
                name.setText(f);
                phone.setText(p);
                username.setText(u);
            }else{
                Toast.makeText(getBaseContext(),R.string.retry,Toast.LENGTH_SHORT).show();
            }

        }
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);


            }
        });


      Button next =(Button) findViewById(R.id.next);
      next.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
             if(isvalidusername()) {
                 if (!TextUtils.isEmpty(name.getText()) && !TextUtils.isEmpty(phone.getText().toString()) &&!TextUtils.isEmpty(username.getText().toString())) {
                     if (usernameIsAvailable()) {

                         // save data in database
                         myRef.child("users").child(userID).child("username").setValue(username.getText().toString());
                         myRef.child("users").child(userID).child("full_name").setValue(name.getText().toString());
                         myRef.child("users").child(userID).child("phone").setValue(phone.getText().toString());
                         myRef.child("public").child("UsersLocation").child(username.getText().toString()).setValue("");

                         // save username string in shared preferences
                         PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putString("username", username.getText().toString()).apply();

                         Intent i = new Intent(createprofile.this, MainActivity.class);
                         startActivity(i);
                         finish();
                         Toast.makeText(getBaseContext(), "success", Toast.LENGTH_SHORT).show();
                     } else {
                         Toast.makeText(getBaseContext(), R.string.username_not_av, Toast.LENGTH_LONG).show();
                     }


                 } else {
                     Toast.makeText(getBaseContext(), R.string.all_full, Toast.LENGTH_SHORT).show();
                 }
             }else {
                 Toast.makeText(getBaseContext(),R.string.not_valid,Toast.LENGTH_SHORT).show();
             }

          }
      });

   }
    public boolean isvalidusername(){
        if (username.getText().toString().contains(" ")) {

            Toast.makeText(getBaseContext(), "No Spaces Allowed",Toast.LENGTH_SHORT).show();
            return false;

        }else {
            if(username.getText().toString().indexOf('\n')>-1) {
                Toast.makeText(getBaseContext(), "No Spaces Allowed", Toast.LENGTH_SHORT).show();
                return false;
            }
         return true;
        }

    }



    public static boolean checkIfAlreadyhavePermission(Activity activity) {
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.GET_ACCOUNTS);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    public static void requestForSpecificPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.GET_ACCOUNTS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.SEND_SMS}, 101);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try
                    {
                        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Trackeru/";
                        File dir = new File(fullPath);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                    }
                    catch (Exception e) {
                        Log.d("App", "Exception" + e.getMessage());
                    }
                } else {
                   Toast.makeText(this,R.string.permission,Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean usernameIsAvailable() {

      myRef.child("public").child("UsersLocation").addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(DataSnapshot dataSnapshot) {
              for(DataSnapshot child : dataSnapshot.getChildren() ){
                 Log.d("vv",child.getKey());

                  if(child.getKey().equals(username.getText().toString())){
                      available= false;
                  }
              }

          }

          @Override
          public void onCancelled(DatabaseError databaseError) {

          }
      });





   return available;
    }

}

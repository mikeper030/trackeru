package org.ultimatetoolsil.trackeru;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.CountryPickerListener;

import java.util.concurrent.TimeUnit;

public class SmsVerification extends AppCompatActivity implements View.OnClickListener {
   Spinner countryList;
   ImageView countryi;
   TextView countryt;
  EditText mobile;
  Button next;
  ProgressBar v;
    private FirebaseAuth mAuth;
  boolean move =false;

  String phoneNum;
    private String mVerificationId;
   String TAG="SMS VERIFICATION";
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_verification);
        countryList=(Spinner)findViewById(R.id.spinner);
        countryi=(ImageView)findViewById(R.id.country_image);
        mobile=(EditText)findViewById(R.id.num);
        next=(Button)findViewById(R.id.next) ;
        next.setVisibility(View.INVISIBLE);
        v=(ProgressBar)findViewById(R.id.pro);
        next.setOnClickListener(this);
      mAuth = FirebaseAuth.getInstance();

      mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

          @Override
          public void onVerificationCompleted(PhoneAuthCredential credential) {
              // This callback will be invoked in two situations:
              // 1 - Instant verification. In some cases the phone number can be instantly
              //     verified without needing to send or enter a verification code.
              // 2 - Auto-retrieval. On some devices Google Play services can automatically
              //     detect the incoming verification SMS and perform verification without
              //     user action.
              Log.d(TAG, "onVerificationCompleted:" + credential);

              mAuth.signInWithCredential(credential)
                      .addOnCompleteListener(SmsVerification.this, new OnCompleteListener<AuthResult>() {
                          @Override
                          public void onComplete(@NonNull Task<AuthResult> task) {
                              if (task.isSuccessful()) {
                                  // Sign in success, update UI with the signed-in user's information
                                  Log.d(TAG, "signInWithCredential:success");
                                  FirebaseAuth.getInstance().getCurrentUser().unlink(PhoneAuthProvider.PROVIDER_ID).addOnCompleteListener(this);


                                  Intent i= new Intent(SmsVerification.this,MainActivity.class);
                                  startActivity(i);

                                  // ...
                              } else {
                                  // Sign in failed, display a message and update the UI
                                  Log.w(TAG, "signInWithCredential:failure", task.getException());
                                  if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                      Toast.makeText(getBaseContext(),"invalid code!",Toast.LENGTH_SHORT).show();
                                      // The verification code entered was invalid
                                  }
                              }
                          }
                      });

//             FirebaseAuth.getInstance().getCurrentUser().unlink(PhoneAuthProvider.PROVIDER_ID).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                  @Override
//                  public void onComplete(@NonNull Task<AuthResult> task) {
//                      Intent i= new Intent(SmsVerification.this,MainActivity.class);
//                      startActivity(i);
//                  }
//              });

              //signInWithPhoneAuthCredential(credential);
          }

          @Override
          public void onVerificationFailed(FirebaseException e) {
              // This callback is invoked in an invalid request for verification is made,
              // for instance if the the phone number format is not valid.
              Log.w(TAG, "onVerificationFailed", e);

              if (e instanceof FirebaseAuthInvalidCredentialsException) {
                  // Invalid request
                  // ...
              } else if (e instanceof FirebaseTooManyRequestsException) {
                  // The SMS quota for the project has been exceeded
                  // ...
              }

              // Show a message and update the UI
              // ...
          }

          @Override
          public void onCodeSent(String verificationId,
                                 PhoneAuthProvider.ForceResendingToken token) {
              // The SMS verification code has been sent to the provided phone number, we
              // now need to ask the user to enter the code and then construct a credential
              // by combining the code with a verification ID.

              Log.d(TAG, "onCodeSent:" + verificationId);

              // Save verification ID and resending token so we can use them later
              mVerificationId = verificationId;
              mResendToken = token;

              ((ConstraintLayout) findViewById(R.id.root)).removeAllViews();
              FragmentTransaction trans = getFragmentManager()
                      .beginTransaction();
              verify vr = new verify();
              Bundle bundle = new Bundle();
              bundle.putString("num", phoneNum);
              bundle.putString("id",mVerificationId);

              vr.setArguments(bundle);
              trans.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
              trans.replace(R.id.root, vr);
              trans.addToBackStack(null);
              trans.commit();
              // ...
          }
      };

        TextWatcher tt = new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           }

           @Override
           public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           }

           @Override
           public void afterTextChanged(Editable editable) {
             if(editable.length()>4){
                 next.setVisibility(View.VISIBLE);
             }
             if(editable.length()<4){
                 next.setVisibility(View.INVISIBLE);
             }

               Log.d("watcher",String.valueOf(editable.length()));
                  try {


                  char end = editable.charAt(editable.length() - 1);
                  if (end != '-') {
                      if (editable.length() == 3 || editable.length() == 7) {
                          mobile.append("-");

                      }
                  }
              }catch (Exception e){


                  }


           }

       };
       mobile.addTextChangedListener(tt);



        countryt=(TextView)findViewById(R.id.country_text);
        Country country=Country.getCountryFromSIM(this);
       countryt.setText(country.getName());
       int flag = country.getFlag();
       Drawable res= getResources().getDrawable(flag);
       countryi.setImageDrawable(res);
       String [] items = new String[]{country.getDialCode()};
       ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
               android.R.layout.simple_spinner_item, items);
       adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       countryList.setAdapter(adapter);
        countryList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d("motion",String.valueOf(motionEvent));
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    final CountryPicker picker = CountryPicker.newInstance("Select Country");  // dialog title
                    picker.setListener(new CountryPickerListener() {
                        @Override
                        public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                            // Implement your code here
                            countryi.setImageDrawable(getResources().getDrawable(flagDrawableResID));
                            countryt.setText(name);
                            String[] items = new String[]{dialCode};
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(),
                                    android.R.layout.simple_spinner_item, items);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            countryList.setAdapter(adapter);
                            picker.dismiss();

                        }
                    });

                    picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
                }
                return true;
            }
        });

    }

    @Override
    public void onClick(View view) {
        if(view.getId()==next.getId()){
            if(!TextUtils.isEmpty(next.getText())) {
                v.getIndeterminateDrawable().setColorFilter(0xFF6296ef,
                        android.graphics.PorterDuff.Mode.MULTIPLY);
                v.setVisibility(View.VISIBLE);



                phoneNum = countryList.getSelectedItem().toString() + mobile.getText().toString().replaceAll("-", "").trim();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNum,        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        this,               // Activity (for callback binding)
                        mCallbacks);        // OnVerificationStateChangedCallbacks
            }

        }
    }
}

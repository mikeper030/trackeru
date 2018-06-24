package org.ultimatetoolsil.trackeru;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import static android.content.ContentValues.TAG;

public class verify extends Fragment {
    EditText one,two,three,four,five,six;
    TextWatcher tw;
    private FirebaseAuth mAuth;
    TextView verify;
    Button next;
    String verificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        verify=(TextView)getView().findViewById(R.id.phonever);
        next=(Button)getView().findViewById(R.id.button3);
        one=(EditText)getView().findViewById(R.id.one);
        two=(EditText)getView().findViewById(R.id.two);
        three=(EditText)getView().findViewById(R.id.three);
        four=(EditText)getView().findViewById(R.id.four);
        five=(EditText)getView().findViewById(R.id.five);
        six=(EditText)getView().findViewById(R.id.six);

        Bundle b = getArguments();

        if(b.getString("num")!=null&&b.getString("id")!=null){
           verificationId=b.getString("id");
            verify.setText(b.getString("num"));
        }
      next.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              String code=six.getText().toString()+five.getText().toString()+four.getText().toString()+three.getText().toString()+two.getText().toString()+one.getText().toString();
              if(code.length()==6){
                  Log.d("code",code);
                  PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
                  mAuth.signInWithCredential(credential)
                              .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                  @Override
                                  public void onComplete(@NonNull Task<AuthResult> task) {
                                      if (task.isSuccessful()) {
                                          // Sign in success, update UI with the signed-in user's information
                                          Log.d(TAG, "signInWithCredential:success");
                                          FirebaseAuth.getInstance().getCurrentUser().unlink(PhoneAuthProvider.PROVIDER_ID).addOnCompleteListener(this);


                                          Intent i= new Intent(getActivity(),MainActivity.class);
                                          startActivity(i);

                                          // ...
                                      } else {
                                          // Sign in failed, display a message and update the UI
                                          Log.w(TAG, "signInWithCredential:failure", task.getException());
                                          if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                             Toast.makeText(getActivity(),"invalid code!",Toast.LENGTH_SHORT).show();
                                              // The verification code entered was invalid
                                          }
                                      }
                                  }
                              });


              }else {
                  Toast.makeText(getActivity(),"please enter six digits",Toast.LENGTH_SHORT).show();
              }
          }
      });

        tw=new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

          }

          @Override
          public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
              TextView text = (TextView)getActivity().getCurrentFocus();

              if (text != null && text.length() > 0)
              {
                  View next = text.focusSearch(View.FOCUS_RIGHT); // or FOCUS_FORWARD
                  if (next != null)
                      next.requestFocus();

                  //doSearch(); // Or whatever
              }

          }

          @Override
          public void afterTextChanged(Editable editable) {

//           if(editable.length()==1){
//             two.requestFocus();
//           }
          }
      };
     one.addTextChangedListener(tw);
     two.addTextChangedListener(tw);
     three.addTextChangedListener(tw);
     four.addTextChangedListener(tw);
     five.addTextChangedListener(tw);
     six.addTextChangedListener(tw);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        View view= inflater.inflate(R.layout.verify_fragment, container, false);
        return view;

    }






}

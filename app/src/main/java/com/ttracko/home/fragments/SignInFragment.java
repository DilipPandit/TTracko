package com.ttracko.home.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.ttracko.R;
import com.ttracko.home.Utils.Util;
import com.ttracko.home.activities.HomeActivity;
import com.ttracko.home.interfaces.MobileDialogListner;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by root on 27/6/18.
 */

public class SignInFragment extends Fragment implements View.OnClickListener {
    @InjectView(R.id.edMobile)
    EditText edMobile;
    @InjectView(R.id.btnSignIn)
    Button btnSignIn;
    @InjectView(R.id.btnGoogleSignIn)
    SignInButton btnGoogleSignIn;
    @InjectView(R.id.edOtp)
    EditText edOtp;
    @InjectView(R.id.btnVerifyOTP)
    Button btnVerifyOTP;
    @InjectView(R.id.llVerify)
    LinearLayout llVerify;
    @InjectView(R.id.llSign)
    LinearLayout llSign;
    @InjectView(R.id.tvTryOtherNumber)
    TextView tvTryOtherNumber;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1432;

    private static final String TAG = "PhoneAuthActivity";

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    ProgressDialog progressDialog;
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.inject(this, view);
        _init();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity)getActivity()).getIvAdd().setVisibility(View.GONE);
    }

    private void _init() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please Wait...");
        llSign.setVisibility(View.VISIBLE);
        llVerify.setVisibility(View.GONE);
        readPhoneNumber();
        FirebaseApp.initializeApp(getActivity());
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        mAuth = FirebaseAuth.getInstance();
        btnSignIn.setOnClickListener(this);
        btnGoogleSignIn.setOnClickListener(this);
        btnVerifyOTP.setOnClickListener(this);
        tvTryOtherNumber.setOnClickListener(this);

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
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                // [START_EXCLUDE silent]
                // Update the UI and attempt sign in with the phone credential
                // updateUI(STATE_VERIFY_SUCCESS, credential);
                // [END_EXCLUDE]
                //signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    edMobile.setError("Invalid phone number.");
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                   /* Toast.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();*/
                    // [END_EXCLUDE]
                }

                // Show a message and update the UI
                // [START_EXCLUDE]
                //updateUI(STATE_VERIFY_FAILED);
                // [END_EXCLUDE]
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

                // [START_EXCLUDE]
                // Update UI
                // updateUI(STATE_CODE_SENT);
                // [END_EXCLUDE]
            }
        };

    }

    int REQUEST_READ_PHONE_STATE = 10002;

    private void readPhoneNumber() {

        TelephonyManager tMgr = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        //ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);

            return;
        } else {
            if (tMgr.getLine1Number() != null) {
                edMobile.setText(tMgr.getLine1Number().replace("+91", ""));
                edMobile.setSelection(edMobile.getText().length());
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager tMgr = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            if (tMgr.getLine1Number() != null) {
                edMobile.setText(tMgr.getLine1Number().replace("+91", ""));
                edMobile.setSelection(edMobile.getText().length());
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // [START_EXCLUDE]
                // [END_EXCLUDE]
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        // [START_EXCLUDE silent]
        // [END_EXCLUDE]
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            final FirebaseUser user = mAuth.getCurrentUser();
                            if (user.getPhoneNumber() != null)
                                if (Util.validateIsMobile(user.getPhoneNumber()))
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.layoutContainer, new DashboardFragment()).commit();
                                else {
                                    Util.showMobileDialog(getActivity(), new MobileDialogListner() {
                                        @Override
                                        public void onMobileGet(String mobileNumber) {
                                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.layoutContainer, new DashboardFragment()).commit();
                                        }

                                        @Override
                                        public void onMobileCancel() {
                                            mAuth.signOut();
                                        }
                                    });
                                }
                            else {
                                Util.showMobileDialog(getActivity(), new MobileDialogListner() {
                                    @Override
                                    public void onMobileGet(String mobileNumber) {
                                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.layoutContainer, new DashboardFragment()).commit();
                                    }

                                    @Override
                                    public void onMobileCancel() {
                                        mAuth.signOut();
                                    }
                                });
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                        }

                        // [START_EXCLUDE]
                        // [END_EXCLUDE]
                    }
                });
    }

    private void revokeAccess() {
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(getActivity(),
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
        // PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
    }

    private void verifyPhoneNumber(String phoneNUmber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + phoneNUmber,        // Phone number to verify
                3,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        progressDialog.dismiss();
                        Util.showOKDialog(getActivity(),getString(R.string.otp_send_faild));
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        mVerificationId = verificationId;
                        progressDialog.dismiss();
                        mResendToken = forceResendingToken;
                        llSign.setVisibility(View.GONE);
                        llVerify.setVisibility(View.VISIBLE);
                        Util.showOKDialog(getActivity(),getString(R.string.otp_sent));
                    }
                });

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            progressDialog.dismiss();
                            FirebaseUser user = task.getResult().getUser();
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.layoutContainer, new DashboardFragment()).commit();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignIn:
                if (valdation()) {
                    progressDialog.show();
                    verifyPhoneNumber(edMobile.getText().toString().trim());
                }
                break;
            case R.id.btnGoogleSignIn:
                googleSignIn();
                progressDialog.show();
                break;
            case R.id.btnVerifyOTP:
                Util.hideKeyboardinFragment(edMobile,getActivity());
                progressDialog.show();
                PhoneAuthCredential credential;
                if (!edOtp.getText().toString().isEmpty()) {
                    credential = PhoneAuthProvider.getCredential(mVerificationId, edOtp.getText().toString());
                    signInWithPhoneAuthCredential(credential);
                } else {
                    Toast.makeText(getActivity(), "Please enter OTP number", Toast.LENGTH_SHORT).show();

                }
                break;
            case R.id.tvTryOtherNumber:
                llSign.setVisibility(View.VISIBLE);
                llVerify.setVisibility(View.GONE);
                break;
        }
    }

    private void googleSignIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private boolean valdation() {

        if (edMobile.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Please enter mobile number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}

package com.example.firebase3;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.squareup.picasso.Picasso;

import java.net.Authenticator;
import java.net.CookieHandler;
import java.util.BitSet;

public class MainActivity extends AppCompatActivity {

    private EditText emailEt, paswordEt;
    TextView id2, name1, email1;
    private Button SignInButton, signUpBtn;
    FirebaseAuth mAuth;
    ImageView image;
    private TextView info;
    private LoginButton log;
    CallbackManager callbackManager;

    private ImageView profile;
    private ProgressBar ojectprogrressbar;
    private FirebaseAuth objectfirebaseAuth;
    private final int R_SIGN_IN = 123;
    Button btn_login, signOutGoogleBtn;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        objectfirebaseAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_main);

        info = findViewById(R.id.info);
        log = findViewById(R.id.log);
        id2 = findViewById(R.id.idTV);
        name1 = findViewById(R.id.nameTV);
        email1 = findViewById(R.id.useremailTV);
        btn_login = findViewById(R.id.signGoogle);
        signOutGoogleBtn = findViewById(R.id.sigOutGoogle);



    /*
        CallbackManager callbackManager = CallbackManager.Factory.create();
        log.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                info.setText("user id " + loginResult.getAccessToken().getUserId());
                String imageURL = "http://graph.facebook.com/" + loginResult.getAccessToken().getUserId() + "/picture_ssl_resourcess=1";
                Picasso.get().load(imageURL).into(profile);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }
}



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
*/



        signOutGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(MainActivity.this, "SingOUT", Toast.LENGTH_SHORT).show();
                        email1.setText("Default User");
                        ojectprogrressbar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ojectprogrressbar.setVisibility(View.VISIBLE);
                FirebaseUser user=objectfirebaseAuth.getCurrentUser();

                Intent signIntent=mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signIntent,R_SIGN_IN);
            }
        });
        connectXMLObjects();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

    }
        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if(requestCode == R_SIGN_IN){
                Task<GoogleSignInAccount> task = GoogleSignIn.
                        getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount account=task.getResult(ApiException.class);
                    if(account != null){
                        FirebaseGoogleAuth(account);
                    }
                }catch (ApiException e){
                    e.printStackTrace();
                }
            }
        }


    private void FirebaseGoogleAuth(GoogleSignInAccount acc) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(acc.getIdToken(), null);
        objectfirebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Toast.makeText(MainActivity.this, "sign succesfully ", Toast.LENGTH_SHORT).show();


                if (task.isSuccessful()) {
                    ojectprogrressbar.setVisibility(View.INVISIBLE);
                    Log.d("TAG","signInWithCredential:success");
                    FirebaseUser user = objectfirebaseAuth.getCurrentUser();
                    updateUI(user);

                } else {
                    ojectprogrressbar.setVisibility(View.INVISIBLE);
                    Log.d("TAG","signInWithCredential:success",task.getException());
                    Toast.makeText(MainActivity.this, "Authentication failed ", Toast.LENGTH_SHORT).show();
updateUI(null);

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "login failed ", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String username = user.getDisplayName();
            String personEmail = user.getEmail();

            email1.setText(username);
            name1.setText(personEmail);
    } else {
        //  Picasso.with(MainActivity.this).load(R.drawable.firebase_logo).into(image);
        id2.setText("Firebase Login \n");

    }

}
    private  void sigIn(){


        objectfirebaseAuth.signInWithEmailAndPassword(emailEt.getText().toString(), paswordEt.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(MainActivity.this, "You are Logged In", Toast.LENGTH_LONG).show();


                        } else {
                            Toast.makeText(MainActivity.this, "You are Failed To logIN", Toast.LENGTH_LONG).show();

                        }

                    }
                });}

    private void checkIfUserExists()
    {
        try
        {
            if(!emailEt.getText().toString().isEmpty())
            {
                if(objectfirebaseAuth!=null)
                {
                    ojectprogrressbar.setVisibility(View.VISIBLE);
                    signUpBtn.setEnabled(false);

                    objectfirebaseAuth.fetchSignInMethodsForEmail(emailEt.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                    boolean check=task.getResult().getSignInMethods().isEmpty();
                                    if(!check)
                                    {
                                        signUpBtn.setEnabled(true);
                                        ojectprogrressbar.setVisibility(View.INVISIBLE);

                                        Toast.makeText(MainActivity.this, "User already exists", Toast.LENGTH_SHORT).show();
                                    }
                                    else if(check)
                                    {

                                        signupUser(); //Step 6
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    signUpBtn.setEnabled(true);
                                    ojectprogrressbar.setVisibility(View.INVISIBLE);

                                    Toast.makeText(MainActivity.this, "Fails to check if user exists:"
                                            +e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
            else
            {
                emailEt.requestFocus();
                Toast.makeText(this, "Please enter the email", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            signUpBtn.setEnabled(true);
            ojectprogrressbar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "checkIfUserExists:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void connectXMLObjects(){

        try {
            emailEt=findViewById(R.id.emailET);
            paswordEt=findViewById(R.id.passwordET);

            signUpBtn =findViewById(R.id.signUpBtn);
            SignInButton=findViewById(R.id.signInBtn);
            ojectprogrressbar=findViewById(R.id.signUpProgressBar);
            SignInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sigIn();
                }
            });
            signUpBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // signupUser();
                    checkIfUserExists();

                }
            });
        }catch (Exception e )
        {

            Toast.makeText(this, "connectXNLObjects"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    private  void signupUser(){

        try {

            if (!emailEt.getText().toString().isEmpty() &&
                    !paswordEt.getText().toString().isEmpty()
            ){
                if(objectfirebaseAuth!=null){
                    ojectprogrressbar.setVisibility(View.VISIBLE);
                    signUpBtn.setEnabled(false);

                    objectfirebaseAuth.createUserWithEmailAndPassword(emailEt.getText().toString(),paswordEt.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {

                            ojectprogrressbar.setVisibility(View.INVISIBLE);
                            Toast.makeText(MainActivity.this, "SuccessFullyCreated", Toast.LENGTH_SHORT).show();
                            if(authResult.getUser()!=null){
                                objectfirebaseAuth.signOut();
                                emailEt.setText("");
                                paswordEt.setText("");

                                signUpBtn.setEnabled(true);
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            ojectprogrressbar.setVisibility(View.INVISIBLE);
                            Toast.makeText(MainActivity.this, "Failed to Add"+e.getMessage(), Toast.LENGTH_SHORT).show();
                            signUpBtn.setEnabled(true);
                            emailEt.requestFocus();
                            ojectprogrressbar.setVisibility(View.INVISIBLE);
                        }
                    });

                }
            }
            else if(emailEt.getText().toString().isEmpty()){


               ojectprogrressbar.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "Enter The Email", Toast.LENGTH_SHORT).show();
                emailEt.requestFocus();
            }
            else if(paswordEt.getText().toString().isEmpty()){
                paswordEt.requestFocus();
                Toast.makeText(this, "Enter The Password", Toast.LENGTH_SHORT).show();
            }



        }catch (Exception e)
        {

            ojectprogrressbar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "signUpUser"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

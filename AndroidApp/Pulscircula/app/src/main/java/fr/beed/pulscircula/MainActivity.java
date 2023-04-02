package fr.beed.pulscircula;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Spinner;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    Intent intent1, intent2;
    String userID;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null)
        {
            userID = user.getUid();
        }
        intent1 = new Intent(this, HomePageActivity.class);
        intent2 = new Intent(this, RegistrationActivity.class);
        mAuthListener = firebaseAuth -> {
            if (user != null) {
                // User is signed in
                startActivity(intent1);
            } else {
                // User is signed out
                startActivity(intent2);
            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }



}

package fr.beed.pulscircula;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ConnectionActivity extends AppCompatActivity {

    Button connectionButton;
    EditText ETEmail, ETPassword;
    ImageView backButton;
    RegistrationActivity registrationActivity;
    FirebaseAuth mAuth;
    TextView passwordForgotten;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        mAuth = FirebaseAuth.getInstance();

        connectionButton = findViewById(R.id.ButtonConnection);
        ETEmail = findViewById(R.id.ETEmail);
        ETPassword = findViewById(R.id.ETPassword);
        backButton = findViewById(R.id.backArrow);
        registrationActivity = new RegistrationActivity();
        passwordForgotten = findViewById(R.id.TVPasswordForgotten);

        backButton.setOnClickListener(new View.OnClickListener()
        {@Override public void onClick(View v) { goBack(); }});

        connectionButton.setOnClickListener(new View.OnClickListener()
        {@Override public void onClick(View v) { connexion(); }});

        passwordForgotten.setOnClickListener(new View.OnClickListener()
        {@Override public void onClick(View v) { passwordForgotten(); }});

        registrationActivity.setWatcher(ETEmail);
        registrationActivity.setWatcher(ETPassword);
    }

    public void connexion()
    {
        String Email, MDP;
        Email = ETEmail.getText().toString();
        boolean inputIsCorrect = registrationActivity.checkContent(Email,ETEmail);
        //if email is entered,
        inputIsCorrect &= registrationActivity.isEmailValid(ETEmail);
        //valid,

        MDP = ETPassword.getText().toString();
        inputIsCorrect &= registrationActivity.checkContent(MDP, ETPassword);
        //and a password is written; inputIsCorrect.


        if (inputIsCorrect) {

            mAuth.signInWithEmailAndPassword(Email,MDP).addOnCompleteListener(this, task -> {
                if (task.isSuccessful())
                {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    assert user != null;
                    if (user.isEmailVerified()) {
                        Intent intent = new Intent(ConnectionActivity.this, HomePageActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        RegistrationActivityNext i = new RegistrationActivityNext();
                        i.sendEmailVerification();
                        Toast.makeText(ConnectionActivity.this, this.getResources().getString(R.string.TextCheckMails), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ConnectionActivity.this, this.getResources().getString(R.string.TextWrongEP), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    public void goBack() {
        //go to previous page
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    public void passwordForgotten() {
        RegistrationActivity A = new RegistrationActivity();
        if(A.isEmailValid(ETEmail)) {
            String email = ETEmail.getText().toString();
            mAuth.sendPasswordResetEmail(email).addOnSuccessListener(unused -> Toast.makeText(ConnectionActivity.this,  this.getResources().getString(R.string.TextRecoveryEmail), Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(ConnectionActivity.this, this.getResources().getString(R.string.TextEmailUnknown), Toast.LENGTH_SHORT).show());
        }
    }

    public void supervisor(View v) {
        startActivity(new Intent(this, SupervisorActivity.class));
    }

}
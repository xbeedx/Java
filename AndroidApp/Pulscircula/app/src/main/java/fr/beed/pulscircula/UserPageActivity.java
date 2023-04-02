package fr.beed.pulscircula;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Objects;

public class UserPageActivity extends AppCompatActivity {

    private DatabaseReference myRef;
    TextView ButtonEdit;
    EditText ETFullName, ETDateOfBirth, ETEmail, ETPassword;
    String strPassword, strPasswordHidden, strOldPassword;
    FirebaseUser currentFirebaseUser;
    boolean editable = false;
    User user;
    RegistrationActivityNext registrationActivityNext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        ETFullName = findViewById(R.id.ETFullName);
        ETDateOfBirth = findViewById(R.id.ETAge);
        ETEmail = findViewById(R.id.ETEmail_user);
        ETPassword = findViewById(R.id.ETPassword_user);
        ButtonEdit  =findViewById(R.id.ButtonEdit);

        registrationActivityNext = new RegistrationActivityNext();

        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance("https://pulscircula-4a6fc-default-rtdb.europe-west1.firebasedatabase.app");
        myRef = mFirebaseDatabase.getReference();
        myRef.child("Users").child( Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                updateDataPage();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        ETEmail.setHint(currentFirebaseUser.getEmail());
    }

    public void Edit(View v)
    {
        if(!editable) {
            ButtonEdit.setText(R.string.TextButtonSave);
            editable = true;
            ETPassword.setFocusableInTouchMode(true);
            ETDateOfBirth.setFocusableInTouchMode(true);
            ETFullName.setFocusableInTouchMode(true);
        } else {
            ButtonEdit.setText(R.string.TextButtonEdit);
            ETPassword.setFocusable(false);
            ETDateOfBirth.setFocusable(false);
            ETFullName.setFocusable(false);
            editable = false;
            boolean edition = false;
            if(!ETFullName.getText().toString().matches(""))
            {
                String[] Firstname;
                Firstname = ETFullName.getText().toString().split(" ");
                if(Firstname.length<2) {
                    ETFullName.setError("Name Missing");
                } else {
                    edition = true;
                    user.setFirstName(Firstname[0]);
                    user.setLastName(Firstname[1]);
                }
            }

            String ddn = ETDateOfBirth.getText().toString();
            strPassword = ETPassword.getText().toString();


            if(!strPassword.matches("") && !strPassword.isEmpty()) {
                if(ETPassword.length()<6)
                {
                    ETPassword.setError(getResources().getString(R.string.TextErrorShortPassword));
                    RegistrationActivity IA = new RegistrationActivity();
                    IA.EditTextToRed(ETPassword);
                } else {
                    strOldPassword = user.getPassword();
                    user.setPassword(strPassword);
                    updatePWD();
                    edition = true;
                }
            }
            if(!ddn.matches("")) { user.setDateOfBirth(ddn); edition = true;}

            if(edition){
                updateDataUser();}
        }
    }

    public void updateDataUser()
    {
        myRef.child("Users").child( Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("password").setValue(user.getPassword());
        myRef.child("Users").child( Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("dateOfBirth").setValue(user.getDateOfBirth());
        myRef.child("Users").child( Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("firstName").setValue(user.getFirstName());
        myRef.child("Users").child( Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("lastName").setValue(user.getLastName());
        updateDataPage();
    }

    public void updateDataPage()
    {
        ETPassword.setText("");
        ETFullName.setText("");
        String firstname = user.getFirstName();
        ETFullName.setHint(firstname.substring(0, 1).toUpperCase() + firstname.substring(1) +" "+ user.getLastName().toUpperCase());
        registrationActivityNext.removeTextWatcherFoDate(ETDateOfBirth);
        ETDateOfBirth.setText("");
        ETDateOfBirth.setHint(user.getDateOfBirth());
        registrationActivityNext.addTextWatcherFoDate(ETDateOfBirth);

        strPassword = user.getPassword();
        strPasswordHidden ="";
        for (int i = 0; i< strPassword.length(); i++)
        {
            strPasswordHidden +="*";
        }
        ETPassword.setHint(strPasswordHidden);

    }

    public void updatePWD ()
    {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        AuthCredential credential = EmailAuthProvider
                .getCredential(Objects.requireNonNull(firebaseUser.getEmail()), strOldPassword);
        firebaseUser.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful())
            {
                firebaseUser.updatePassword(strPassword).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        user.setPassword(strPassword);
                    }
                });
            }
        });
    }

    public void goToHomePage(View v) { startActivity(new Intent(this, HomePageActivity.class)); }
    public void goToSettings(View v) {
        Intent intent = new Intent(UserPageActivity.this, SettingsActivity.class);
        intent.putExtra("User", user);
        startActivity(intent);
    }

    public void seePassword(View v)
    {
        if(ETPassword.getHint().toString().matches(strPassword))
        {
            ETPassword.setHint(strPasswordHidden);
        } else {
            ETPassword.setHint(strPassword);
        }
    }

}

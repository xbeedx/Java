package fr.beed.pulscircula;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity {

    Button ButtonNext;
    TextView TVConnection;
    EditText ETLastName, ETFirstName, ETEmail, ETPassword;
    FirebaseAuth mAuth;
    boolean emailIsUsed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButtonNext = findViewById(R.id.ButtonNext);
        TVConnection = findViewById(R.id.ButtonConnection);
        ETLastName = findViewById(R.id.ETLastName);
        ETFirstName = findViewById(R.id.ETFirstName);
        ETEmail = findViewById(R.id.ETEmail);
        ETPassword = findViewById(R.id.ETPassword);

        mAuth= FirebaseAuth.getInstance();

        setWatcher(ETLastName);
        setWatcher(ETFirstName);
        setWatcher(ETEmail);
        setWatcher(ETPassword);
    }

    public void onClick(View v) {
        if( v== ButtonNext)
        {
            ETLastName = findViewById(R.id.ETLastName);
            String lastName = ETLastName.getText().toString();
            //If a lastname,
            boolean inputIsCorrect = checkContent(lastName, ETLastName);


            ETFirstName = findViewById(R.id.ETFirstName);
            String firstName = ETFirstName.getText().toString();
            // a firstname,
            inputIsCorrect &= checkContent(firstName, ETFirstName);

            ETEmail = findViewById(R.id.ETEmail);
            String Email = ETEmail.getText().toString();
            // a valid email
            inputIsCorrect &= checkContent(Email,ETEmail);


            if (isEmailValid(ETEmail)) {
                mAuth.fetchSignInMethodsForEmail(ETEmail.getText().toString()).addOnCompleteListener(task -> {
                    if (Objects.requireNonNull(task.getResult().getSignInMethods()).size() != 0) {
                        emailIsUsed = false;
                        ETEmail.setError("Email already used");
                        EditTextToRed(ETEmail);
                    } else {
                        emailIsUsed = true;
                    }
                });
            }
            inputIsCorrect &= emailIsUsed;

            String password = ETPassword.getText().toString();
            // and a password have been entered,
            // Then inputIsCorrect return true
            inputIsCorrect &= checkContent(password, ETPassword);
            if(password.length()<6)
            {
                inputIsCorrect = false;
                ETPassword.setError("password is too short! Must be longer than 6 characters.");
                EditTextToRed(ETPassword);
            }
            if (inputIsCorrect) {
                //intent to the next page
                Intent intent = new Intent(this, RegistrationActivityNext.class);

                // with the entered data
                intent.putExtra("LastName", lastName);
                intent.putExtra("FirstName", firstName);
                intent.putExtra("Email", Email);
                intent.putExtra("Password", password);
                startActivity(intent);
            }
        } else if (v== TVConnection) {
            //intent to connection page
            Intent intent = new Intent(this, ConnectionActivity.class);
            startActivity(intent);
        }
    }

    //Check if email is valid
    public boolean isEmailValid(EditText ET) {
        String email = ET.getText().toString();
        //Regex
        String expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);

        //if input look like an email
        if (matcher.matches()) {
            return true;
        } else {
            EditTextToRed(ET);
            ET.setError("invalid email");
            return false;
        }

    }

    //Check if something is entered
    public boolean checkContent(String s, EditText ET) {
        if (s == null || s.length() == 0)
        {
            ET.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            ET.setHintTextColor(Color.RED);
            return false;
        }
        return true;
    }

    //Add TextChangedListener to EditText
    public void setWatcher(EditText ET){
        ET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            //change font color to white
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ET.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                ET.setHintTextColor(Color.WHITE);
            }

            @Override
            public void afterTextChanged (Editable editable){
            }
        });
    }

    //change color of EditText to red
    public void EditTextToRed(EditText ET)
    {
        ET.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
        ET.setHintTextColor(Color.RED);
    }


}
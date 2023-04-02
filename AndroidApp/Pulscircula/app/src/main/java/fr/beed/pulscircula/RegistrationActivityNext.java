package fr.beed.pulscircula;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Calendar;
import java.util.Objects;

public class RegistrationActivityNext extends AppCompatActivity {

    User user;
    FirebaseAuth mAuth;
    Button genderMButton, genderFButton, registrationButton;
    EditText ETHeight, ETWeight, ETDateOfBirth;
    String gender;
    RegistrationActivity registrationActivity;
    String lastName, firstName,Email, Password;
    Float weight;
    int day, mon, year, Height;
    String DateOfBirth;
    ImageView backArrow;
    TextWatcher tw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_following);

        mAuth=FirebaseAuth.getInstance();
        registrationActivity = new RegistrationActivity();

        genderMButton = findViewById(R.id.ButtonGenderM);
        genderFButton = findViewById(R.id.ButtonGenderF);
        registrationButton = findViewById(R.id.ButtonRegister);
        ETHeight = findViewById(R.id.ETHeight);
        ETWeight = findViewById(R.id.ETWeight);
        ETDateOfBirth = findViewById(R.id.ETDateOfBirth);
        backArrow = findViewById(R.id.backArrow);
        Intent intent = getIntent();

        lastName = intent.getStringExtra("LastName").trim();
        firstName = intent.getStringExtra("FirstName").trim();
        Email = intent.getStringExtra("Email").trim();
        Password = intent.getStringExtra("Password").trim();

        registrationActivity.setWatcher(ETHeight);
        registrationActivity.setWatcher(ETWeight);
        registrationActivity.setWatcher(ETDateOfBirth);

        addTextWatcherFoDate(ETDateOfBirth);
        backArrow.setOnClickListener(new View.OnClickListener()
        {@Override public void onClick(View v) { goBack(); }});
    }

    public void goBack()
    {
        //back to previous page
        startActivity(new Intent(this, RegistrationActivity.class));
    }

    public void onClick(View v) {
         if (v== genderMButton)
        {
            //buttonM pushed
            genderMButton.setBackgroundResource(R.drawable.boutton_ma);
            genderFButton.setBackgroundResource(R.drawable.boutton_f);
            gender ="M";
        } else if (v== genderFButton) {
            //buttonF pushed
            genderMButton.setBackgroundResource(R.drawable.boutton_m);
            genderFButton.setBackgroundResource(R.drawable.boutton_fa);
            gender ="F";
        } else if (v== registrationButton)
        {
            //If an height,
            boolean inputIsCorrect = registrationActivity.checkContent(ETHeight.getText().toString(), ETHeight);
            // a weight,
            inputIsCorrect &= registrationActivity.checkContent(ETWeight.getText().toString(), ETWeight);
            // a date of birth
            inputIsCorrect &= registrationActivity.checkContent(ETDateOfBirth.getText().toString(), ETDateOfBirth);
            // and a gender have been entered. inputIsCorrect is true
            inputIsCorrect &= gender != null;

            if (inputIsCorrect) {
                weight = Float.parseFloat(String.valueOf(ETWeight.getText()));
                Height = Integer.parseInt(String.valueOf(ETHeight.getText()));
                String M = mon < 10 ? "0" + mon : String.valueOf(mon);
                String D = day < 10 ? "0" + day : String.valueOf(day);
                DateOfBirth = (D + '/' + M + '/' + year);
                Registration();
            }
        }
    }

    public void Registration()
    {
        mAuth.createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance("https://pulscircula-4a6fc-default-rtdb.europe-west1.firebasedatabase.app");
                        DatabaseReference myRef = database.getReference("Users");
                        String key = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                        user = new User(lastName, firstName, weight, Height, DateOfBirth, gender, Password,key);
                        myRef.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(user).addOnSuccessListener(suc ->
                                sendEmailVerification()).addOnFailureListener(er ->
                                Toast.makeText(RegistrationActivityNext.this, "" + er.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                });
        }

    public void sendEmailVerification() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            user.sendEmailVerification().addOnCompleteListener(task -> {
                if (task.isSuccessful())
                {
                    Toast.makeText(this,this.getResources().getString(R.string.TextVerificationEmail),Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //Change of string to format date DD/MM/YYYY
    public void addTextWatcherFoDate(EditText ET)
    {
        tw = new TextWatcher() {
            private String current = "";
            private final Calendar cal = Calendar.getInstance();

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @SuppressLint("DefaultLocale")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    //deletion of all  characters =! of number
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) { sel++; }

                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8){
                        String ddmmyyyy = "DDMMYYYY";
                        clean = clean + ddmmyyyy.substring(clean.length());
                    }else{
                        day  = Integer.parseInt(clean.substring(0,2));
                        mon  = Integer.parseInt(clean.substring(2,4));
                        year = Integer.parseInt(clean.substring(4,8));

                        //if the month is less than 1, month equals 1
                        //And 12 if it's greater than 12.
                        mon = mon < 1 ? 1 : Math.min(mon, 12);
                        cal.set(Calendar.MONTH, mon-1);

                        //if the year is less than the current year -100
                        //or, if it's greater than the current year, year equals current year
                        year = (year<cal.get(Calendar.YEAR)-100)?cal.get(Calendar.YEAR): Math.min(year, cal.get(Calendar.YEAR));
                        cal.set(Calendar.YEAR, year);

                        //if the day is greater than the max number of days in the current month, day equals this max
                        //And 1 if it's lesser than 1
                        day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE): Math.max(day, 1);
                        clean = String.format("%02d%02d%02d",day, mon, year);
                    }
                    clean = String.format("%s/%s/%s", clean.substring(0, 2), clean.substring(2, 4), clean.substring(4, 8));
                    sel = Math.max(sel, 0);
                    current = clean;
                    ET.setText(current);
                    ET.setSelection(Math.min(sel, current.length()));
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {  }
        };
        ET.addTextChangedListener(tw);
    }
    public void removeTextWatcherFoDate(EditText ET)
    {
        ET.removeTextChangedListener(tw);
    }
}
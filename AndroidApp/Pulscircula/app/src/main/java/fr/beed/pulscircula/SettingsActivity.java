package fr.beed.pulscircula;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.OffsetTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;


public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText ETWeight, ETHeight;
    User user;
    Spinner spinner;
    AlarmManager alarmManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ETWeight = findViewById(R.id.TVSettingsWeight);
        ETHeight = findViewById(R.id.ETSettingsHeight);
        spinner = findViewById(R.id.SpinnerNotifications);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.notificationsState, R.layout.spinner_color);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        user = (User) getIntent().getSerializableExtra("User");
        ETWeight.setText(String.valueOf(user.getWeight()));
        ETHeight.setText(String.valueOf(user.getHeight()));

        changeValueOnUpdate(ETWeight);
        changeValueOnUpdate(ETHeight);
        createNotificationChannel();
    }

    @Override
    protected void onStart() {
        super.onStart();
        DataProccessor dataProccessor = new DataProccessor(this);
        String savedValue= dataProccessor.getStr("stateNotifs");
        if(savedValue.equals(spinner.getItemAtPosition(0).toString())) {
            spinner.setSelection(0);
        } else { spinner.setSelection(1); }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createAlarm()
    {
        OffsetTime offset = OffsetTime.now();
        Calendar setCalendar = GregorianCalendar.getInstance();
        setCalendar.setTimeInMillis(System.currentTimeMillis());
        setCalendar.set(Calendar.HOUR_OF_DAY, offset.getHour()+1);
        setCalendar.set(Calendar.MINUTE,offset.getMinute());
        setCalendar.set(Calendar.SECOND,offset.getSecond());
        Intent i = new Intent(this, AlarmReceiver.class);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pi = PendingIntent.getBroadcast(this, 0,i , PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, setCalendar.getTimeInMillis(), 1000 * 60 * 60, pi);
    }
    public void cancelAlarm()
    {
        Intent i = new Intent(this, AlarmReceiver.class);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pi = PendingIntent.getBroadcast(this, 0,i , PendingIntent.FLAG_UPDATE_CURRENT);
        if(alarmManager == null){
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        }
        alarmManager.cancel(pi);

    }

    public void changeValueOnUpdate(EditText ET)
    {
        TextWatcher TWWeight = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable editable) {
                if (ET.getText().toString().length() != 0) {
                    FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance("https://pulscircula-4a6fc-default-rtdb.europe-west1.firebasedatabase.app");
                    DatabaseReference myRef = mFirebaseDatabase.getReference();
                    if(ET== ETWeight) {
                        DecimalFormat df = new DecimalFormat("#.##");
                        df.setRoundingMode(RoundingMode.CEILING);
                        String Weight = df.format(Float.parseFloat(ETWeight.getText().toString()));
                        myRef.child("Users").child( Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("weight").setValue(Float.parseFloat(Weight));
                    } else if (ET== ETHeight){
                        int Height = Integer.parseInt(ETHeight.getText().toString());
                        myRef.child("Users").child( Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("height").setValue(Height);
                    }
                }
            }
        };
        ET.addTextChangedListener(TWWeight);
    }

    public void LogOut(View v)
    {
        AuthUI.getInstance().signOut(this).addOnCompleteListener(task -> {
            // user is now signed out
            //intent to registration page
            startActivity(new Intent(SettingsActivity.this, RegistrationActivity.class));
            finish();
        });
    }

    public void goBack(View v) {startActivity(new Intent(SettingsActivity.this, HomePageActivity.class));}

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        DataProccessor dataProccessor = new DataProccessor(this);
        dataProccessor.setStr("stateNotifs",spinner.getSelectedItem().toString());
        if(spinner.getSelectedItem() == spinner.getItemAtPosition(0))
        {
            createAlarm();
        } else {
            cancelAlarm();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void createNotificationChannel()
    {
        CharSequence name = "ReminderChannel";
        String description = "Channel for Alarm Manager";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel("Alarm",name,importance);
        channel.setDescription(description);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}

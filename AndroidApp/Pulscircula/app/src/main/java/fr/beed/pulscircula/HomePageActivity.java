package fr.beed.pulscircula;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.time.OffsetTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Objects;


public class HomePageActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager sensorManager;
    Sensor stepDetectorSensor, accelerometerSensor;
    TextView stepCounter;
    Calendar calendar;
    int day;
    String strDay;
    TextView date;
    String strDt, strDt2;
    String[] months;
    User user;
    TextView distance;
    private DatabaseReference myRef;
    FirebaseDatabase mFirebaseDatabase;
    @SuppressLint("StaticFieldLeak")
    AlertDialog alertDialog;
    ValueEventListener VEL;
    DailyReport dailyReport;
    AlarmManager alarmManager;
    double longitude = 0, latitude = 0;
    private LocationRequest locationRequest;
    float valDistance;
    DataProccessor dataProccessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        dataProccessor = new DataProccessor(this);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(HomePageActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            } else {
                try {
                    if (isGPSEnabled()) {
                        turnOnGPS();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        months = new String[]{this.getResources().getString(R.string.TextM01), this.getResources().getString(R.string.TextM02), this.getResources().getString(R.string.TextM03), this.getResources().getString(R.string.TextM04), this.getResources().getString(R.string.TextM05), this.getResources().getString(R.string.TextM06), this.getResources().getString(R.string.TextM07), this.getResources().getString(R.string.TextM08), this.getResources().getString(R.string.TextM09), this.getResources().getString(R.string.TextM10), this.getResources().getString(R.string.TextM11), this.getResources().getString(R.string.TextM12)};
        stepCounter = findViewById(R.id.TVStepsCount);
        distance = findViewById(R.id.TVDistance);
        mFirebaseDatabase = FirebaseDatabase.getInstance("https://pulscircula-4a6fc-default-rtdb.europe-west1.firebasedatabase.app");
        myRef = mFirebaseDatabase.getReference().child("Users");
        AlertDialog.Builder builder1 = new AlertDialog.Builder(HomePageActivity.this, R.style.AlertDialogTheme);
        builder1.setMessage(getResources().getString(R.string.TextQuestionAccess));
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                getResources().getString(R.string.TextYes),
                (dialog, id) -> {
                    dialog.cancel();
                    myRef.child(user.getKey()).child("requestAccess").setValue(false);
                    myRef.child(user.getKey()).child("access").setValue(true);
                    myRef.child(user.getKey()).child("requestAnswer").setValue(true);
                    if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0); }
                        }
                        sensorManager.registerListener(HomePageActivity.this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
                    } else {
                        Toast.makeText(HomePageActivity.this, this.getResources().getString(R.string.TextNoAccelerometer), Toast.LENGTH_SHORT).show();
                    }
                });

        builder1.setNegativeButton(
                getResources().getString(R.string.TextNo),
                (dialog, id) -> {
                    dialog.cancel();
                    myRef.child(user.getKey()).child("requestAccess").setValue(false);
                    myRef.child(user.getKey()).child("access").setValue(false);
                    myRef.child(user.getKey()).child("requestAnswer").setValue(true);
                });

        alertDialog = builder1.create();

        VEL = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot sp : snapshot.getChildren()) {
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        user = new User((HashMap) Objects.requireNonNull(sp.getValue()));
                        break;
                    }
                }
                if (user != null) {

                    if (user.getListDailyReport() != null && user.getListDailyReport().get(strDt2) != null) {
                        myRef.child(user.getKey()).child("listDailyReport").child(strDt2).get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                dailyReport = task.getResult().getValue(DailyReport.class);
                                stepCounter.setText(String.valueOf(dailyReport.getNumber_of_steps()));
                                showDistance(dailyReport.getDistance());
                                if (dailyReport.getInjury_report().equals("0")) {
                                    Intent intent = new Intent(HomePageActivity.this, InjuryReportActivity.class);
                                    intent.putExtra("DailyReport", dailyReport);
                                    intent.putExtra("User", user);
                                    startActivity(intent);
                                }
                            }
                        });
                    } else {
                        stepCounter.setText("0");
                        dailyReport = new DailyReport(strDt2);
                        Intent intent = new Intent(HomePageActivity.this, InjuryReportActivity.class);
                        intent.putExtra("DailyReport", dailyReport);
                        intent.putExtra("User", user);
                        startActivity(intent);
                    }
                    if (user.isRequestAccess()) {
                        alertDialog.show();
                    }
                    if (!user.isAccess()){
                        sensorManager.unregisterListener(HomePageActivity.this,accelerometerSensor);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };

    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;
        if (locationManager == null) {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        }
        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;
    }

    private void turnOnGPS() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(HomePageActivity.this, 2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            }
        });
    }

    protected void onDestroy() {
        super.onDestroy();
        user.setAccess(false);
        user.setRequestAccess(false);
        myRef.child(user.getKey()).child("access").setValue(false);
        myRef.child(user.getKey()).child("requestAccess").setValue(false);

    }

    protected void onResume() {
        super.onResume();
        myRef.addValueEventListener(VEL);

        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(HomePageActivity.this, this.getResources().getString(R.string.TextNoCounterSensor), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStart() {
        super.onStart();
        calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case Calendar.SUNDAY:
                strDay = getResources().getString(R.string.TextD7);
                break;
            case Calendar.MONDAY:
                strDay = getResources().getString(R.string.TextD1);
                break;
            case Calendar.TUESDAY:
                strDay = getResources().getString(R.string.TextD2);
                break;
            case Calendar.WEDNESDAY:
                strDay = getResources().getString(R.string.TextD3);
                break;
            case Calendar.THURSDAY:
                strDay = getResources().getString(R.string.TextD4);
                break;
            case Calendar.FRIDAY:
                strDay = getResources().getString(R.string.TextD5);
                break;
            case Calendar.SATURDAY:
                strDay = getResources().getString(R.string.TextD6);
                break;
        }
        strDt = strDay + " " + calendar.get(Calendar.DAY_OF_MONTH) + " " + months[calendar.get(Calendar.MONTH)] + " " + calendar.get(Calendar.YEAR);
        strDt2 = calendar.get(Calendar.DAY_OF_MONTH) + String.valueOf(calendar.get(Calendar.MONTH)) + calendar.get(Calendar.YEAR);
        date = findViewById(R.id.TVDate);
        date.setText(strDt);

        setAlarm();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setAlarm() {
        String savedValue = dataProccessor.getStr("stateNotifs");
        if (savedValue.equals(this.getResources().getString(R.string.TextEnabled))) {
            OffsetTime offset = OffsetTime.now();
            Calendar setCalendar = GregorianCalendar.getInstance();
            setCalendar.setTimeInMillis(System.currentTimeMillis());
            setCalendar.set(Calendar.HOUR_OF_DAY, offset.getHour() + 1);
            setCalendar.set(Calendar.MINUTE, offset.getMinute());
            setCalendar.set(Calendar.SECOND, offset.getSecond());
            Intent i = new Intent(this, AlarmReceiver.class);
            @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, setCalendar.getTimeInMillis(), 1000 * 60 * 60, pi);
        } else if (savedValue.equals(this.getResources().getString(R.string.TextDisabled))) {
            Intent i = new Intent(this, AlarmReceiver.class);
            @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            if (alarmManager == null) {
                alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            }
            alarmManager.cancel(pi);
        }
    }

    public void showDistance(float val){
        distance.setText(String.valueOf(val));
    }

    public void goToUser(View v) {
        Intent intent = new Intent(this, UserPageActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if ( sensorEvent.sensor == stepDetectorSensor) {
            int steps;
            if (stepCounter.getText() == null || stepCounter.getText().equals("")) {
                steps = 1;
            } else {
                steps = Integer.parseInt(String.valueOf(stepCounter.getText())) + 1;
            }
            stepCounter.setText(String.valueOf(steps));
            myRef.child(user.getKey()).child("listDailyReport").child(strDt2).child("number_of_steps").setValue(steps);

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(HomePageActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                } else {
                    try {
                        if (!isGPSEnabled()) {
                            turnOnGPS();
                        } else {
                            LocationServices.getFusedLocationProviderClient(HomePageActivity.this)
                                    .requestLocationUpdates(locationRequest, new LocationCallback() {
                                        @Override
                                        public void onLocationResult(@NonNull LocationResult locationResult) {
                                            super.onLocationResult(locationResult);
                                            LocationServices.getFusedLocationProviderClient(HomePageActivity.this)
                                                    .removeLocationUpdates(this);
                                            if (locationResult != null && locationResult.getLocations().size() > 0) {
                                                int index = locationResult.getLocations().size() - 1;
                                                if(latitude==0 && longitude==0) {
                                                    latitude = locationResult.getLocations().get(index).getLatitude();
                                                    longitude = locationResult.getLocations().get(index).getLongitude();
                                                } else {
                                                    double newLatitude = locationResult.getLocations().get(index).getLatitude();
                                                    double newLongitude = locationResult.getLocations().get(index).getLongitude();
                                                    float[] results = new float[1];
                                                    Location.distanceBetween(
                                                            latitude, longitude,
                                                            newLatitude, newLongitude, results);
                                                    double valIni = Double.valueOf((String) distance.getText());
                                                    valDistance = (float) valIni + results[0];
                                                    myRef.child(user.getKey()).child("distance").setValue(valDistance);
                                                    showDistance(valDistance);
                                                    Log.i("infooooo", String.valueOf(valDistance));
                                                    latitude = newLatitude;
                                                    longitude = newLongitude;
                                                }
                                                Log.i("infooooo", String.valueOf(latitude + " " + longitude));
                                            }
                                        }
                                    }, Looper.getMainLooper());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        if (sensorEvent.sensor == accelerometerSensor)
        {
            if(user != null && user.isAccess() )
            {
                myRef.child(user.getKey()).child("accelerationX").setValue(sensorEvent.values[0]);
                myRef.child(user.getKey()).child("accelerationY").setValue(sensorEvent.values[1]);
                myRef.child(user.getKey()).child("accelerationZ").setValue(sensorEvent.values[2]);
            }
        }
    }
    @Override public void onAccuracyChanged(Sensor sensor, int i) { }
}
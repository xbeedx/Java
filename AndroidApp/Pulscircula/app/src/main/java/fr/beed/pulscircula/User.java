package fr.beed.pulscircula;

import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;

public class User implements Serializable {
    String LastName, FirstName, DateOfBirth, Gender, Password;
    Float Weight,AccelerationX,AccelerationY,AccelerationZ;
    int Height;
    boolean access, requestAccess, requestAnswer;
    String key;
    HashMap<String,DailyReport> listDailyReport;

    public User() {
        this.setLastName("");
        this.setFirstName("");
        this.setWeight(0f);
        this.setHeight(0);
        this.setGender("");
        this.setPassword("");
        this.setDateOfBirth("");
        this.setAccess(false);
        this.setRequestAccess(false);
        this.setKey("");
        this.setAccelerationX(0f);
        this.setAccelerationY(0f);
        this.setAccelerationZ(0f);
        this.setListDailyReport(new HashMap<String,DailyReport>());
        this.setRequestAnswer(false);
    }

    public User(HashMap hashMap){
        this.setLastName((String) hashMap.get("lastName"));
        this.setFirstName((String) hashMap.get("firstName"));
        this.setWeight(Float.valueOf((Long) hashMap.get("weight")));
        this.setHeight(Math.toIntExact((Long) hashMap.get("height")));
        this.setGender((String) hashMap.get("gender"));
        this.setPassword((String) hashMap.get("password"));
        this.setDateOfBirth((String) hashMap.get("dateOfBirth"));
        this.setAccess((Boolean) hashMap.get("access"));
        this.setRequestAccess((Boolean) hashMap.get("requestAccess"));
        this.setKey((String) hashMap.get("key"));
        this.setRequestAnswer((Boolean) hashMap.get("requestAnswer"));
        this.setAccelerationX(0f);
        this.setAccelerationY(0f);
        this.setAccelerationZ(0f);
        this.setListDailyReport((HashMap<String, DailyReport>) hashMap.get("listDailyReport"));
    }

    public User(String lastName, String firstName, Float weight, int height, String ddn, String gender, String Password, String key) {
        this.setLastName(lastName);
        this.setFirstName(firstName);
        this.setWeight(weight);
        this.setHeight(height);
        this.setGender(gender);
        this.setPassword(Password);
        this.setDateOfBirth(ddn);
        this.setAccess(false);
        this.setRequestAccess(false);
        this.setKey(key);
        this.setAccelerationX(0f);
        this.setAccelerationY(0f);
        this.setAccelerationZ(0f);
        this.setListDailyReport(new HashMap<String,DailyReport>());
        this.setRequestAnswer(false);
    }

    public HashMap<String,DailyReport> getListDailyReport() {
        return listDailyReport;
    }

    public void setListDailyReport(HashMap<String,DailyReport> listDailyReport) {
        this.listDailyReport = listDailyReport;
    }

    public void addDailyReport(DailyReport dailyReport)
    {
        listDailyReport.put(dailyReport.Date,dailyReport);
    }

    public DailyReport getDailyReport(String date)
    {
        return listDailyReport.get(date);

    }

    public void changeValueDailyReport(DailyReport dailyReport)
    {
        listDailyReport.replace(dailyReport.Date, dailyReport);
    }

    public boolean isRequestAnswer() {
        return requestAnswer;
    }

    public void setRequestAnswer(boolean requestAnswer) {
        this.requestAnswer = requestAnswer;
    }

    public Float getAccelerationX() {
        return AccelerationX;
    }

    public void setAccelerationX(Float accelerationX) {
        AccelerationX = accelerationX;
    }

    public Float getAccelerationZ() {
        return AccelerationZ;
    }

    public void setAccelerationZ(Float accelerationZ) {
        AccelerationZ = accelerationZ;
    }

    public Float getAccelerationY() {
        return AccelerationY;
    }

    public void setAccelerationY(Float accelerationY) {
        AccelerationY = accelerationY;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isRequestAccess() {
        return requestAccess;
    }

    public void setRequestAccess(boolean requestAccess) {
        this.requestAccess = requestAccess;
    }

    public boolean isAccess() {
        return access;
    }

    public void setAccess(boolean access) {
        this.access = access;
    }

    public String getPassword() {return Password; }

    public void setPassword(String password) {this.Password = password; }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public Float getWeight() {
        return Weight;
    }

    public void setWeight(Float weight) {
        Weight = weight;
    }

    public int getHeight() {
        return Height;
    }

    public void setHeight(int height) {
        Height = height;
    }

    public void setDateOfBirth(String dateOfBirth) { this.DateOfBirth = dateOfBirth; }

    public String getDateOfBirth() {
        return DateOfBirth;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    @NonNull
    @Override
    public String toString() {
        String back=System.getProperty("line.separator");
        String DA = (isRequestAccess())?"/access asked for/":"/access not asked for/";
        String Ac = (isAccess())?"/access activated/":"/access declined/";
        return LastName.toUpperCase() +" "+ FirstName.substring(0, 1).toUpperCase() + FirstName.substring(1) + back + Weight + "Kg "+ Height + "cm "+ DateOfBirth +" "+ Gender +" "+DA+" "+Ac + " DailyReports: "+listDailyReport.toString();

    }

    public String getFullName() {
        return LastName.toUpperCase() +" "+ FirstName.substring(0, 1).toUpperCase() + FirstName.substring(1);
    }

    public String getData()
    {
        return Weight + "Kg "+ Height + "cm "+ DateOfBirth +" "+ Gender;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public int getAge()
    {
        LocalDate ddn;
        String[] DATE = DateOfBirth.split("/");
        ddn = LocalDate.parse(DATE[2]+"-"+DATE[1]+"-"+DATE[0]);
        LocalDate curDate = LocalDate.now();
        return Period.between(ddn,curDate).getYears();
    }
}

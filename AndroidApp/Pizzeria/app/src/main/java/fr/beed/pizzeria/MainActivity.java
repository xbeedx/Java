package fr.beed.pizzeria;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button Napolitaine;
    private Button Royale;
    private Button QuatresFromages;
    private Button Montagnarde;
    private Button Raclette;
    private Button Hawai;
    private Button PannaCotta;
    private Button Tiramisu;

    private int cptN=0;
    private int cptR=0;
    private int cptQ=0;
    private int cptM=0;
    private int cptRa=0;
    private int cptH=0;
    private int cptP=0;
    private int cptT=0;

    String textN;
    String textR;
    String textQ;
    String textM;
    String textRa;
    String textH;
    String textP;
    String textT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Napolitaine = findViewById(R.id.napolitaine);
        Royale = findViewById(R.id.royale);
        QuatresFromages = findViewById(R.id.QuatreFromages);
        Montagnarde = findViewById(R.id.montagnarde);
        Raclette = findViewById(R.id.raclette);
        Hawai = findViewById(R.id.hawai);
        PannaCotta = findViewById(R.id.pannacota);
        Tiramisu = findViewById(R.id.tiramisu);

        Napolitaine.setOnClickListener(this);
        Royale.setOnClickListener(this);
        QuatresFromages.setOnClickListener(this);
        Montagnarde.setOnClickListener(this);
        Raclette.setOnClickListener(this);
        Hawai.setOnClickListener(this);
        PannaCotta.setOnClickListener(this);
        Tiramisu.setOnClickListener(this);

        textN = (String) Napolitaine.getText();
        textR = (String) Royale.getText();
        textQ= (String) QuatresFromages.getText();
        textM = (String) Montagnarde.getText();
        textRa = (String) Raclette.getText();
        textH = (String) Hawai.getText();
        textP = (String) PannaCotta.getText();
        textT = (String) Tiramisu.getText();

        if (savedInstanceState != null)
        {
            cptN = savedInstanceState.getInt("cptN");
            Napolitaine.setText(textN+": "+cptN);

            cptR = savedInstanceState.getInt("cptR");
            Royale.setText(textR+": "+cptR);

            cptQ = savedInstanceState.getInt("cptQ");
            QuatresFromages.setText(textQ+": "+cptQ);

            cptM = savedInstanceState.getInt("cptM");
            Montagnarde.setText(textM+": "+cptM);

            cptRa = savedInstanceState.getInt("cptRa");
            Raclette.setText(textRa+": "+cptRa);

            cptH = savedInstanceState.getInt("cptH");
            Hawai.setText(textH+": "+cptH);

            cptP = savedInstanceState.getInt("cptP");
            PannaCotta.setText(textP+": "+cptP);

            cptT = savedInstanceState.getInt("cptT");
            Tiramisu.setText(textT+": "+cptT);
        }
    }

    public void onClick(View v)
    {
        System.out.println(v);
        if (v == Napolitaine)
        {
            cptN++;
            Napolitaine.setText(textN+": "+cptN);
        }
        else if (v == Royale)
        {
            cptR++;
            Royale.setText(textR+": "+cptR);
        }
        else if (v == QuatresFromages)
        {
            cptQ++;
            QuatresFromages.setText(textQ+": "+cptQ);
        }
        else if (v == Montagnarde)
        {
            cptM++;
            Montagnarde.setText(textM+": "+cptM);
        }
        else if (v == Raclette)
        {
            cptRa++;
            Raclette.setText(textRa+": "+cptRa);
        }
        else if (v == Hawai)
        {
            cptH++;
            Hawai.setText(textH+": "+cptH);
        }
        else if (v == PannaCotta)
        {
            cptP++;
            PannaCotta.setText(textP+": "+cptP);
        }
        else if (v == Tiramisu)
        {
            cptT++;
            Tiramisu.setText(textT+": "+cptT);
        }

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("cptN",cptN);
        outState.putInt("cptR",cptR);
        outState.putInt("cptQ",cptQ);
        outState.putInt("cptM",cptM);
        outState.putInt("cptRa",cptRa);
        outState.putInt("cptH",cptH);
        outState.putInt("cptP",cptP);
        outState.putInt("cptT",cptT);
    }

}
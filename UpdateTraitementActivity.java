package com.example.lmachillot.ths;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UpdateTraitementActivity extends AppCompatActivity {

    public static long IDTRAITEMENT=-1;
    Traitement traitement;

    Spinner listehormones, listetypes;
    EditText ednom, eddosage, edfrequence;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_traitement);

        //récupérer variable
        final Intent intent = getIntent();
        IDTRAITEMENT = intent.getLongExtra("IDTRAITEMENT", -1);

        if(IDTRAITEMENT<0) {
            Toast.makeText(this, "pas de traitement associé!", Toast.LENGTH_SHORT).show();
            // redirection
            Intent intent2 = new Intent(UpdateTraitementActivity.this, MainActivity.class);
            finish();
            startActivity(intent2);
        }

        Log.d("#####################", "# ID TRAITEMENT "+IDTRAITEMENT+"");

        TraitementDAO tdao = new TraitementDAO(this);
        tdao.open();
        traitement = tdao.getTraitementParId(IDTRAITEMENT);
        tdao.close();

        Log.d("################  ", "TRAITEMENT"+traitement.toString());


        // Spinner hormones :

        listehormones = (Spinner) findViewById(R.id.listehormonesupdate);
        int hormone = 0;

        List<String> liste = new ArrayList<>();
        int i=0;
        for (Hormone h : Hormone.values()) {
            liste.add(h.name());
            if(h.name().equals(traitement.getHormone().name())) {
                hormone=i;
            }
            i++;
        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, liste);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listehormones.setAdapter(adapter);
        //pré selectionner hormone du traitement
        listehormones.setSelection(hormone);


        //Spinner type
        listetypes = (Spinner) findViewById(R.id.typeupdate);

        List<String> types = new ArrayList<>();
        int type = 0;
        i=0;
        for (Type t : Type.values()) {
            types.add(t.name());
            if(t.name().equals(traitement.getType().name())) {
                type=i;
            }
            i++;
        }

        ArrayAdapter adaptertype = new ArrayAdapter(this, android.R.layout.simple_spinner_item, types);
        adaptertype.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listetypes.setAdapter(adaptertype);

        //pré selectionner type traitement
        listetypes.setSelection(type);


        //préremplir les champs edittext
        //nom
        ednom = (EditText) findViewById(R.id.nomtraitementupdate);
        ednom.setText(traitement.getNom());

        //dosage
        eddosage = (EditText) findViewById(R.id.dosageupdate);
        eddosage.setText(traitement.getDosage());

        //frequence
        edfrequence = (EditText) findViewById(R.id.freqtraitementupdate);
        edfrequence.setText(traitement.getFrequence()+"");


    }

    public void updateTraitement(View view) {
        String nom = ednom.getText().toString();
        String dosage = eddosage.getText().toString();
        String frequencestr = edfrequence.getText().toString();
        int frequence = Integer.parseInt(frequencestr);
        String hormonestr = listehormones.getSelectedItem().toString();
        String typestr = listetypes.getSelectedItem().toString();

        boolean frequencediff=false;
        if(frequence!=traitement.getFrequence()) {
            frequencediff=true;
        }

        Hormone hormone = null;
        for(Hormone h : Hormone.values()) {
            if(hormonestr.equals(h.name())) {
                hormone=h;
            }
        }

        if(hormone==null) {
            Log.d("-----------------", "problème pour trouver hormone update traitement");
        }

        Type type = null;
        for(Type t : Type.values()) {
            if(typestr.equals(t.name())) {
                type=t;
            }
        }

        if(type==null) {
            Log.d("-----------------", "problème pour trouver type update traitement");
        }

        traitement.setNom(nom);
        traitement.setDosage(dosage);
        traitement.setFrequence(frequence);
        traitement.setHormone(hormone);
        traitement.setType(type);

        // editer traitement base de donnée
        TraitementDAO tdao = new TraitementDAO(this);
        tdao.open();
        long modif = tdao.updateTraitement(traitement);
        tdao.close();

        //editer date renouvellement si frequence différente
        if(frequencediff) {
            PriseDAO pdao = new PriseDAO(this);
            pdao.open();
            Prise prise = pdao.dernierePriseFrom(traitement);
            pdao.close();

            Date dernierredate = prise.getDateprise();
            Calendar cal = Calendar.getInstance();
            cal.setTime(dernierredate);
            cal.add(Calendar.DATE, frequence);
            Date prochainedate = cal.getTime();

            traitement.setDate_renouvellement(prochainedate);

            TraitementDAO tdao2 = new TraitementDAO(this);
            tdao2.open();
            tdao2.majDateRenouvellement(traitement);
            tdao2.close();

            // update rappels
            RappelDAO rdao = new RappelDAO(this);
            rdao.open();
            List<Rappel> rappels = rdao.getRappelsFrom(traitement);

            //maj rappels
            for(Rappel r : rappels) {
                cal.setTime(prochainedate);
                cal.add(Calendar.DATE, -r.getDelai());
                r.setDaterappel(cal.getTime());

                //maj dans BD
                rdao.updateDateRappel(r);
                rdao.close();

            }


        }



        Intent intent =new Intent(UpdateTraitementActivity.this, ProchaineDateActivity.class);
        startActivity(intent);



    }

    public void updateZones(View view) {
        updateTraitement(view);

        //todo : activité modifier zones du traitement T
    }
}

package com.example.lmachillot.ths;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // DEBUG supprimer tables
/*
        DAOBase dao = new TraitementDAO(this);
        SQLiteDatabase mDb = dao.open();

        DatabaseHandler dh = dao.getmHandler();
        dh.supprimerTables(mDb);
*/




        java.util.Date utilDate = new java.util.Date();

        Traitement t = new Traitement("essai", Hormone.œstrogènes, utilDate, 14, 0, Type.comprimé);

        TraitementDAO dao = new TraitementDAO(this);
        dao.open();

        //dao.supprimerTousTraitements();


        long idtraitement=dao.ajouterTraitement(t);
        t.setId(idtraitement);


        Cursor cursor = dao.mDb.rawQuery("SELECT _id, intituletype, denomination FROM type", null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                while(!cursor.isLast()) {
                    Log.d("+++++++++++++++++++", cursor.getInt(0)+" "+cursor.getString(1)+", "+cursor.getString(2));
                    cursor.moveToNext();

                }
                Log.d("+++++++++++++++++++", cursor.getInt(0)+" "+cursor.getString(1)+", "+cursor.getString(2));


            } else {
                Log.d("----", "+++++++++++++++++++++++++pas de lignes ?????????????????");

            }
        } else {
            Log.d("--------", "----------------problème");
        }
        cursor.close();

        Log.d("#####", "#####################fin type début traitement");

        cursor = dao.mDb.rawQuery("SELECT nomtraitement, hormone, premiere_prise, frequence, date_renouvellement, duree_stock, _id_type FROM traitement", null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.moveToFirst();
                while(!cursor.isLast()) {
                    Log.d("+++++++++++++++++++", " "+cursor.getString(0)+", "+cursor.getString(1)+", "+cursor.getString(2)+", "+cursor.getString(3)+", "+cursor.getString(4)+", "+cursor.getString(5)+", "+cursor.getString(6));
                    cursor.moveToNext();

                }
                Log.d("+++++++++++++++++++", " "+cursor.getString(0)+", "+cursor.getString(1)+", "+cursor.getString(2)+", "+cursor.getString(3)+", "+cursor.getString(4)+", "+cursor.getString(5)+", "+cursor.getInt(6));

                // retransformer string en date
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

                try {

                    Date dt = format.parse(cursor.getString(2));
                    Log.d("format ++", "********************  "+dt);
                } catch (ParseException e) {
                    Log.d("erreur format", "//////////////////////////////////////");
                    e.printStackTrace();
                }


            } else {
                Log.d("----++++++++", "+++++++++++++++++pas de lignes ?????????????????");

            }
        } else {
            Log.d("---------", "---------------problème");
        }
        cursor.close();


        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new java.util.Date());
        cal.add(Calendar.DATE, 10);
        Date newdate = cal.getTime();

        Log.d("-----nouvelle date", "-------------"+newdate);


        Prise prise = new Prise(newdate, t);
        Log.d("nouvelle prochaine date", "------------------"+prise.getTraitement().getDate_renouvellement());



        dao.close();


    }
}

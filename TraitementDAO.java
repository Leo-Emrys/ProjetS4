package com.example.lmachillot.ths;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

/**
 * Created by lmachillot on 13/03/17.
 */

public class TraitementDAO extends DAOBase {

    public String nomtable = "traitement";

    public static String ID = "_id";
    public static String NOM = "nomtraitement";
    public static String HORMONE = "hormone";
    public static String FREQUENCE = "frequence";
    public static String PREMIERE_PRISE = "premiere_prise";
    public static String DATE_RENOUVELLEMENT = "date_renouvellement";
    public static String DUREE_STOCK = "duree_stock";
    public static String TYPE = "_id_type";

    public TraitementDAO(Context pContext) {
        super(pContext);


    }

    public long ajouterTraitement(Traitement t) {

        // récupérer clé du type voulu
        String req = "SELECT _id FROM type WHERE intituletype = '"+t.getType().name()+"'";
        Cursor cursor = mDb.rawQuery(req, null);

        cursor.moveToFirst();
        int idtype = cursor.getInt(0);

        //conversion des dates en format sql

        java.sql.Date sqlDatePrem = new java.sql.Date(t.getPremiereprise().getTime());
        Log.d("sqldate première prise", " ////////////////////////////// "+sqlDatePrem+"");

        java.sql.Date sqlDateSuiv = new java.sql.Date(t.getDate_renouvellement().getTime());
        Log.d("sqldate prochaine prise", " ////////////////////////////// "+sqlDateSuiv+"");

        //insertion
        ContentValues value = new ContentValues();
        value.put(NOM, t.getNom());
        value.put(HORMONE, t.getHormone().toString());
        value.put(FREQUENCE, t.getFrequence());
        value.put(PREMIERE_PRISE, sqlDatePrem.toString());
        value.put(DATE_RENOUVELLEMENT, sqlDateSuiv.toString());
        value.put(DUREE_STOCK, t.getDurée_stock());
        value.put(TYPE, idtype);

        Log.d("value content", " :::::::::::::::::::::::::::::::"+value+"");


        long id = mDb.insert(nomtable, null, value);

        Log.d("ID CREE", " :::::::::::::::::::::::::::::::"+id+"");



/*

        String requete = "INSERT INTO "+nomtable+" VALUES (null, '"+t.getNom() +"', '"+t.getHormone().toString()+"', "+t.getFrequence()+", '"+sqlDatePrem+"', '"+sqlDateSuiv+"', "+t.getDurée_stock()+", "+idtype+")";

        mDb.execSQL(requete);
*/


        return id;
    }

    public void majDateRenouvellement(Traitement t) {
        String req = "UPDATE "+DATE_RENOUVELLEMENT+" FROM "+nomtable+" WHERE "+ID+" = ";
    }

    public void supprimerTousTraitements() {
        mDb.delete(nomtable, null, null);
    }


}

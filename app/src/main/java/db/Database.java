package db;

import android.content.Context;
import android.database.Cursor;


/**
 * Created by admin on 3/6/17.
 */

public class Database {

    private DBHelper dbHelper;

    public Database(Context context) {
        dbHelper = new DBHelper(context, "mydiagnostic", null, 1);
    }
    Cursor getDatabaseVersion() {
        return dbHelper.getReadableDatabase().rawQuery(
                "select id_version as id , version, To_date as date from database_version"
                , null);
    }
    public Cursor getDiseases(){
        return dbHelper.getReadableDatabase().rawQuery(
                "select d.id_diseases as id ,d.name_disease as name ,d.description as description," +
                "COUNT(sd.id_diseases) as symptons from diseases d, symptoms_diseases sd " +
                "where d.id_diseases=sd.id_diseases group by sd.id_diseases"
                ,null);
    }
    public Cursor getDiseaseSymptoms(String id_disease) {
        return dbHelper.getReadableDatabase().rawQuery("select s.id_symptom as id,s.symptom as name " +
                "from symptoms_diseases sd, symptoms s where sd.id_symptom=s.id_symptom and " +
                "sd.id_diseases=?"
                ,new String[]{id_disease});
    }
    public Cursor getSymptoms() {
        return dbHelper.getReadableDatabase().rawQuery(
                "select id_symptom as id , symptom as name from symptoms"
                , null);
    }
    void saveSymptom(String id, String symptom) {
        dbHelper.getWritableDatabase().execSQL(String.format("insert into symptoms values('%s','%s')", id, symptom));
    }
    void deleteData(){
        dbHelper.getWritableDatabase().execSQL("delete from symptoms");
        dbHelper.getWritableDatabase().execSQL("delete from User_system");
        dbHelper.getWritableDatabase().execSQL("delete from medicalhistory");
        dbHelper.getWritableDatabase().execSQL("delete from symptoms_diseases");
        dbHelper.getWritableDatabase().execSQL("delete from diseases");
        dbHelper.getWritableDatabase().execSQL("delete from country");
        dbHelper.getWritableDatabase().execSQL("delete from allergies");
        dbHelper.getWritableDatabase().execSQL("delete from bloodtype");
        dbHelper.getWritableDatabase().execSQL("delete from database_version");
    }
    /*public void saveUser(SingleUser user){
        //dbHelper.getWritableDatabase().insertWithOnConflict();
    }*/
    public DBHelper getDbHelper() {
        return dbHelper;
    }

}

package utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;

import org.dev4u.hv.my_diagnostic.DownloadActivity;
import org.dev4u.hv.my_diagnostic.MainActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DataBaseVersion;
import db.Database;
import db.Disease;

/**
 * Created by admin on 12/7/17.
 */

public class SearchUpdates {
    private Context mContext;
    private Gson gson;
    private SharedPreferences savedData;
    private int versionNumber;
    private String versionDate;
    private boolean isFromMainActivity;
    private SharedPreferences.Editor editSavedData;

    public SearchUpdates(Context mContext,boolean isFromMainActivity) {
        this.mContext   = mContext;
        this.isFromMainActivity = isFromMainActivity;
        gson            = new Gson();
        savedData       = this.mContext.getSharedPreferences("Data", Context.MODE_PRIVATE);
        editSavedData   = savedData.edit();
        versionNumber   = savedData.getInt("DB_VERSION",1);
    }

    public void getVersion(final boolean saveVersion) {
        if(isNetworkAvailable(mContext)) {
            VolleySingleton.
                    getInstance(mContext).
                    addToRequestQueue(
                            new JsonObjectRequest(
                                    Request.Method.GET,
                                    ConnectionSettings.GETData,
                                    null,
                                    new Response.Listener<JSONObject>() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            processVersion(response, saveVersion);
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            //error
                                            Toast.makeText(mContext, "1 An error has occurred while My Diagnostic trying to connect the server", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                            )
                    );
        }else if(!isFromMainActivity){
            Toast.makeText(mContext, "You do not have access to the internet", Toast.LENGTH_SHORT).show();
        }
    }
    private void processVersion(JSONObject response,boolean save){
        try {
            switch (response.getString("estado")) {
                case "1": // SUCCESS
                    JSONArray mensaje = response.getJSONArray("dataversion");
                    DataBaseVersion[] version = gson.fromJson(mensaje.toString(), DataBaseVersion[].class);

                    int db_version  = Integer.parseInt(version[0].getVersion());
                    versionDate     = version[0].getTo_date();
                    if(!save && db_version>versionNumber){
                        versionDialog();
                    }else if(save){
                        versionNumber  = db_version;
                        editSavedData.putInt("DB_VERSION",db_version);
                        editSavedData.commit();
                    }else if(!isFromMainActivity){
                        Toast.makeText(mContext,"Your database version is already update",Toast.LENGTH_SHORT).show();
                    }
                    return;
                default: // FAIL
                    //hasError();
                    Toast.makeText(mContext,"2 An error has occurred while My Diagnostic trying to connect the server",Toast.LENGTH_SHORT).show();
                    return;
            }
        } catch (JSONException e) {
            Toast.makeText(mContext,"3 An error has occurred while My Diagnostic trying to connect the server",Toast.LENGTH_SHORT).show();
        }
    }


    public void versionDialog(){
        AlertDialog.Builder alertDialogBuilider = new AlertDialog.Builder(mContext);
        alertDialogBuilider.setTitle("Update");
        alertDialogBuilider.setMessage("Do you want download the latest database version?");
        alertDialogBuilider.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent =  new Intent(mContext, DownloadActivity.class);
                intent.putExtra("IS_UPDATE",true);
                mContext.startActivity(intent);
                if(!isFromMainActivity){
                    Intent intent2 = new Intent(MainActivity.FINISH_ALERT);
                    mContext.sendBroadcast(intent2);
                }
                ((Activity) mContext).finish();
            }
        }).setNegativeButton("Later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialogBuilider.create().show();
    }

    private boolean isNetworkAvailable(Context c) {
        ConnectivityManager connectivityManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            //we are connected to a network
            return true;
        } else
            return false;

    }
}

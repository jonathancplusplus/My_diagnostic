package org.dev4u.hv.my_diagnostic.Fragments;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.dev4u.hv.my_diagnostic.R;

import db.MedicalHistory;
import utils.DiseaseUtilitesSingleton;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryDetailFragment extends BaseFragment {

    private View view;
    private String id;
    private TextView lblStateDisease;
    private TextView lblDate;
    private TextView lblNameDisease;
    private EditText txtTitle;
    private EditText txtDescription;
    private MedicalHistory md;
    private FloatingActionButton btnSaveHistory;
    public HistoryDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_history_detail, container, false);
        setHasOptionsMenu(true);
        AppCompatActivity appCompatActivity = (AppCompatActivity)getActivity();
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        id              = getArguments().getString("ID_HISTORY");
        lblDate         = (TextView)view.findViewById(R.id.lblDateHistory);
        txtTitle        = (EditText)view.findViewById(R.id.txtTitleHistory);
        txtDescription  = (EditText)view.findViewById(R.id.txtDescriptionHistory);
        lblStateDisease = (TextView)view.findViewById(R.id.lblStateDiseaseHistory);
        lblNameDisease  = (TextView)view.findViewById(R.id.lblNameDiseaseHistory);
        btnSaveHistory  = (FloatingActionButton)view.findViewById(R.id.btnSaveHistory);

        md = DiseaseUtilitesSingleton.getInstance().getHistory(id);
        if(md!=null){
            lblDate.setText("Date : "+md.getDate_time());
            txtTitle.setText(md.getTitle());
            txtDescription.setText(md.getDescription());
            if(md.getId_diseases().equals("NULL")){
                lblStateDisease.setText("Disease : Not assigned");
                lblNameDisease.setVisibility(View.INVISIBLE);
            }else{
                lblStateDisease.setText("Disease :");
                lblNameDisease.setText(md.getName_disease());
            }
        }
        btnSaveHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveHistory();
            }
        });
        return view;
    }

    private void saveHistory(){
        md.setTitle(txtTitle.getText().toString());
        md.setDescription(txtDescription.getText().toString());
        DiseaseUtilitesSingleton.getInstance().historyAdapter.notifyDataSetChanged();
        DiseaseUtilitesSingleton.getInstance().saveOrUpdateHistory(false,md);
        getActivity().onBackPressed();
    }

}

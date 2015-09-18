package org.tndata.android.compass.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Package;
import org.tndata.android.compass.task.ConsentAcknowledgementTask;
import org.tndata.android.compass.task.PackageLoaderTask;

import java.util.List;


/**
 * Created by isma on 9/17/15.
 */
public class PackageEnrollmentActivity
        extends AppCompatActivity
        implements
                View.OnClickListener,
                PackageLoaderTask.PackageLoaderCallback,
                ConsentAcknowledgementTask.ConsentAcknowledgementCallback{

    public static final String PACKAGE_ID_KEY = "org.tndata.compass.PackageId";

    private int mPackageId;

    private CompassApplication mApplication;

    private TextView mTitle;
    private TextView mDescription;
    private TextView mConsentSummary;
    private TextView mConsent;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_enrollment);

        mPackageId = getIntent().getIntExtra(PACKAGE_ID_KEY, -1);

        mApplication = (CompassApplication)getApplication();

        //Get and set the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.package_toolbar);
        setSupportActionBar(toolbar);

        mTitle = (TextView)findViewById(R.id.package_title);
        mDescription = (TextView)findViewById(R.id.package_description);
        mConsentSummary = (TextView)findViewById(R.id.package_consent_summary);
        mConsent = (TextView)findViewById(R.id.package_consent);
        findViewById(R.id.package_accept).setOnClickListener(this);

        new PackageLoaderTask(mApplication.getToken(), this).execute(mPackageId);
    }

    @Override
    public void onPackagesLoaded(List<Package> packages){
        mTitle.setText(packages.get(0).getTitle());
        mDescription.setText(packages.get(0).getDescription());
        mConsentSummary.setText(packages.get(0).getConsentSummary());
        mConsent.setText(packages.get(0).getConsent());
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.package_accept:
                new ConsentAcknowledgementTask(mApplication.getToken(), this).execute(mPackageId);
        }
    }

    @Override
    public void onAcknowledgementSuccessful(){
        Log.d("PackageEnrollment", "succeeded");
    }

    @Override
    public void onAcknowledgementFailed(){
        Log.d("PackageEnrollment", "failed");
    }
}

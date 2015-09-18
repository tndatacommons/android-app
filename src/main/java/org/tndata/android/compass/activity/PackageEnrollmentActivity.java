package org.tndata.android.compass.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Package;
import org.tndata.android.compass.task.ConsentAcknowledgementTask;
import org.tndata.android.compass.task.PackageLoaderTask;
import org.tndata.android.compass.util.CompassTagHandler;

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

    private ProgressBar mProgressBar;
    private ScrollView mContent;
    private TextView mTitle;
    private ViewSwitcher mAcceptSwitcher;
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

        mProgressBar = (ProgressBar)findViewById(R.id.package_progress);
        mContent = (ScrollView)findViewById(R.id.package_content);
        mTitle = (TextView)findViewById(R.id.package_title);
        mAcceptSwitcher = (ViewSwitcher)findViewById(R.id.package_accept_switcher);
        mDescription = (TextView)findViewById(R.id.package_description);
        mConsentSummary = (TextView)findViewById(R.id.package_consent_summary);
        mConsent = (TextView)findViewById(R.id.package_consent);
        findViewById(R.id.package_accept).setOnClickListener(this);

        new PackageLoaderTask(mApplication.getToken(), this).execute(mPackageId);
    }

    @Override
    public void onPackagesLoaded(List<Package> packages){
        mProgressBar.setVisibility(View.GONE);
        if (packages == null){
            Toast.makeText(this, "The package information couldn't be loaded", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run(){
                    finish();
                }
            }, 2000);
        }
        else{
            mContent.setVisibility(View.VISIBLE);
            CompassTagHandler tagHandler = new CompassTagHandler(this);
            Package myPackage = packages.get(0);
            mTitle.setText(myPackage.getTitle());
            if (myPackage.getHtmlDescription().isEmpty()){
                mDescription.setText(myPackage.getDescription());
            }
            else{
                mDescription.setText(Html.fromHtml(myPackage.getHtmlDescription(), null, tagHandler));
            }
            if (myPackage.getHtmlConsentSummary().isEmpty()){
                mConsentSummary.setText(myPackage.getConsentSummary());
            }
            else{
                mConsentSummary.setText(Html.fromHtml(myPackage.getHtmlConsentSummary(), null, tagHandler));
            }
            if (myPackage.getHtmlConsent().isEmpty()){
                mConsent.setText(myPackage.getConsent());
            }
            else{
                mConsent.setText(Html.fromHtml(myPackage.getHtmlConsent(), null, tagHandler));
            }
        }
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.package_accept:
                mAcceptSwitcher.showNext();
                new ConsentAcknowledgementTask(mApplication.getToken(), this).execute(mPackageId);
        }
    }

    @Override
    public void onAcknowledgementSuccessful(){
        finish();
    }

    @Override
    public void onAcknowledgementFailed(){
        mAcceptSwitcher.showPrevious();
        Toast.makeText(this, "The package couldn't be consented", Toast.LENGTH_SHORT).show();
    }
}

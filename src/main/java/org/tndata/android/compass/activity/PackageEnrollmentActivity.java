package org.tndata.android.compass.activity;

import android.app.NotificationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.model.Package;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.CompassTagHandler;
import org.tndata.android.compass.util.NetworkRequest;
import org.tndata.android.compass.util.NotificationUtil;
import org.tndata.android.compass.util.Parser;


/**
 * Activity that displays the package enrollment consent form and lets the user
 * consent to it.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class PackageEnrollmentActivity
        extends AppCompatActivity
        implements
                View.OnClickListener,
                NetworkRequest.RequestCallback{

    //Keys
    public static final String PACKAGE_ID_KEY = "org.tndata.compass.PackageId";

    //The package id for this consent
    private int mPackageId;

    //A reference to the application class
    private CompassApplication mApplication;

    //UI components
    private ProgressBar mProgressBar;
    private ScrollView mContent;
    private TextView mTitle;
    private ViewSwitcher mAcceptSwitcher;
    private TextView mDescription;
    private TextView mConsentSummary;
    private TextView mConsent;

    //Request codes
    private int mGetPackageRequestCode;
    private int mPutConsentRequestCode;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_enrollment);

        mPackageId = getIntent().getIntExtra(PACKAGE_ID_KEY, -1);

        mApplication = (CompassApplication)getApplication();

        //Get and set the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.package_toolbar);
        setSupportActionBar(toolbar);

        //Retrieve the UI components and set the button's listener
        mProgressBar = (ProgressBar)findViewById(R.id.package_progress);
        mContent = (ScrollView)findViewById(R.id.package_content);
        mTitle = (TextView)findViewById(R.id.package_title);
        mAcceptSwitcher = (ViewSwitcher)findViewById(R.id.package_accept_switcher);
        mDescription = (TextView)findViewById(R.id.package_description);
        mConsentSummary = (TextView)findViewById(R.id.package_consent_summary);
        mConsent = (TextView)findViewById(R.id.package_consent);
        findViewById(R.id.package_accept).setOnClickListener(this);

        //This setting cannot be set from XML
        TextView explanation = (TextView)findViewById(R.id.package_accept_explanation);
        explanation.setMovementMethod(LinkMovementMethod.getInstance());

        //Fetch the package
        mGetPackageRequestCode = NetworkRequest.get(this, this, API.getPackageUrl(mPackageId),
                mApplication.getToken());
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetPackageRequestCode){
            mProgressBar.setVisibility(View.GONE);
            populateUI(new Parser().parsePackage(result));
        }
        else if (requestCode == mPutConsentRequestCode){
            //If the acknowledgement was successful, dismiss the notification and kill the activity
            ((NotificationManager)getSystemService(NOTIFICATION_SERVICE))
                    .cancel(NotificationUtil.NOTIFICATION_TYPE_ENROLLMENT_TAG, mPackageId);
            finish();
        }
    }

    @Override
    public void onRequestFailed(int requestCode, String message){
        if (requestCode == mGetPackageRequestCode){
            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(this, R.string.package_load_error, Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run(){
                    finish();
                }
            }, 2000);
        }
        else if (requestCode == mPutConsentRequestCode){
            //If the acknowledgement failed let the user know
            mAcceptSwitcher.showPrevious();
            Toast.makeText(this, R.string.package_consent_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void populateUI(Package myPackage){mContent.setVisibility(View.VISIBLE);
        CompassTagHandler tagHandler = new CompassTagHandler(this);
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

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.package_accept:
                //Show the progress bar and fire up the acknowledgement task
                mAcceptSwitcher.showNext();
                mPutConsentRequestCode = NetworkRequest.post(this, this,
                        API.getPutConsentAcknowledgementUrl(mPackageId), mApplication.getToken(),
                        API.getPutConsentAcknowledgementBody());
                break;
        }
    }
}

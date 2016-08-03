package org.tndata.android.compass.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Toast;

import org.tndata.android.compass.R;
import org.tndata.android.compass.databinding.ActivityPackageEnrollmentBinding;
import org.tndata.android.compass.model.TDCPackage;
import org.tndata.android.compass.parser.Parser;
import org.tndata.android.compass.parser.ParserModels;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.NotificationUtil;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;


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
                HttpRequest.RequestCallback,
                Parser.ParserCallback{

    //Keys
    public static final String PACKAGE_ID_KEY = "org.tndata.compass.PackageId";

    //Data and binding
    private TDCPackage mPackage;
    private ActivityPackageEnrollmentBinding mBinding;

    //Request codes
    private int mGetPackageRequestCode;
    private int mPutConsentRequestCode;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_package_enrollment);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_package_enrollment);
        mBinding.setActivity(this);

        //Toolbar
        setSupportActionBar(mBinding.packageToolbar);

        //This setting cannot be set from XML
        mBinding.packageAcceptExplanation.setMovementMethod(LinkMovementMethod.getInstance());

        //Fetch the package
        int packageId = getIntent().getIntExtra(PACKAGE_ID_KEY, -1);
        mGetPackageRequestCode = HttpRequest.get(this, API.URL.getPackage(packageId));
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode == mGetPackageRequestCode){
            Parser.parse(result, TDCPackage.class, this);
        }
        else if (requestCode == mPutConsentRequestCode){
            //If the acknowledgement was successful, dismiss the notification and kill the activity
            NotificationUtil.cancel(this, NotificationUtil.ENROLLMENT_TAG, (int)mPackage.getId());
            finish();
        }
    }

    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){
        if (requestCode == mGetPackageRequestCode){
            mBinding.packageProgress.setVisibility(View.GONE);
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
            mBinding.packageAcceptSwitcher.showPrevious();
            Toast.makeText(this, R.string.package_consent_error, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onProcessResult(int requestCode, ParserModels.ResultSet result){
        if (result instanceof TDCPackage){
            mPackage = (TDCPackage)result;
        }
    }

    @Override
    public void onParseSuccess(int requestCode, ParserModels.ResultSet result){
        populateUI(mPackage);
        mBinding.packageProgress.setVisibility(View.GONE);
    }

    private void populateUI(TDCPackage myPackage){
        mBinding.packageContent.setVisibility(View.VISIBLE);
        mBinding.setTdcPackage(myPackage);
    }

    public void accept(){
        //Show the progress bar and fire the acknowledgement request
        mBinding.packageAcceptSwitcher.showNext();
        mPutConsentRequestCode = HttpRequest.put(this, API.URL.putConsentAcknowledgement(mPackage),
                API.BODY.putConsentAcknowledgement());
    }

    public void decline(){
        NotificationUtil.cancel(this, NotificationUtil.ENROLLMENT_TAG, (int)mPackage.getId());
        finish();
    }
}

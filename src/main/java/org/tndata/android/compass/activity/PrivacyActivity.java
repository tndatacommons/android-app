package org.tndata.android.compass.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.tndata.android.compass.R;
import org.tndata.android.compass.util.Constants;

/**
 * Activity used to display the privacy information.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class PrivacyActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        Toolbar toolbar = (Toolbar)findViewById(R.id.transparent_tool_bar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.grow_primary));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        WebView webView = (WebView)findViewById(R.id.privacy_web_view);
        webView.loadUrl(Constants.PRIVACY_URL);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return true;
    }
}

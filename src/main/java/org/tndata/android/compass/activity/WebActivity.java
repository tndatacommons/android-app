package org.tndata.android.compass.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.tndata.android.compass.R;


/**
 * Activity used to display the privacy information.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class WebActivity extends AppCompatActivity{
    public static final String TITLE_KEY = "org.tndata.compass.Web.Title";
    public static final String URL_KEY = "org.tndata.compass.Web.Url";


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        String title = getIntent().getExtras().getString(TITLE_KEY, "");
        String url = getIntent().getStringExtra(URL_KEY);

        Toolbar toolbar = (Toolbar)findViewById(R.id.transparent_tool_bar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.primary));
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        WebView webView = (WebView)findViewById(R.id.web_web_view);
        webView.loadUrl(url);
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

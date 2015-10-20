package org.tndata.android.compass.fragment;

import android.annotation.SuppressLint;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewFragment;

@SuppressLint("SetJavaScriptEnabled")
public class WebFragment extends WebViewFragment{
    private String mUrl = "";

    public void setUrl(String url){
        mUrl = url;
    }

    public void onResume(){
        super.onResume();
        setupWebView();
    }

    private void setupWebView(){
        WebView webView = getWebView();
        webView.getSettings().setJavaScriptEnabled(true);
        getActivity().setProgressBarVisibility(true);

        webView.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view, int progress){
                //Activities and WebViews measure progress with different scales. The
                //  progress meter will automatically disappear when we reach 100%
                if (getActivity() != null){
                    getActivity().setProgress(progress * 100);
                }
            }
        });
        webView.loadUrl(mUrl);
    }
}

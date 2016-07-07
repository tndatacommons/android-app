package org.tndata.android.compass.activity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.SourcesAdapter;
import org.tndata.android.compass.databinding.ActivitySourcesBinding;

import java.util.ArrayList;
import java.util.List;


/**
 * Activity used to display sources and open them in the browser.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class SourcesActivity extends AppCompatActivity implements SourcesAdapter.OnSourceClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        ActivitySourcesBinding binding;
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sources);

        binding.sourcesToolBar.toolbar.setTitle("Sources");
        setSupportActionBar(binding.sourcesToolBar.toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        List<Source> sources = new ArrayList<>();
        sources.add(new Source("\"Stay strong with Compass\" messaging inspired by the work of the Character Lab. Thanks, Dr. Duckworth!", "https://characterlab.org/resources"));
        sources.add(new Source("Illustrations: Michael Cook (Cookicons)", "http://cookicons.co"));
        sources.add(new Source("Icons: designed by flaticon", "http://www.flaticon.com"));
        sources.add(new Source("Misc art: designed by freepik", "http://www.freepik.com"));
        sources.add(new Source("Robolectric", "http://robolectric.org/"));
        sources.add(new Source("BetterPickers", "https://github.com/derekbrameyer/android-betterpickers"));
        sources.add(new Source("Hamcrest", "https://github.com/hamcrest/JavaHamcrest"));
        sources.add(new Source("Circle Indicator", "https://github.com/ongakuer/CircleIndicator"));
        sources.add(new Source("HTTP-Requests", "https://github.com/Sandwatch/HTTP-Requests"));
        sources.add(new Source("FloatingActionButton", "https://github.com/Clans/FloatingActionButton"));

        binding.sourcesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.sourcesRecyclerView.setAdapter(new SourcesAdapter(this, sources, this));
    }

    @Override
    public void onSourceClick(String url){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }


    /**
     * Data type for a Source,
     *
     * @author Ismael Alonso
     * @version 1.0.0
     */
    public static final class Source{
        private final String mCaption;
        private final String mUrl;


        /**
         * Constructor.
         *
         * @param caption the source caption.
         * @param url the source url.
         */
        public Source(String caption, String url){
            mCaption = caption;
            mUrl = url;
        }

        /**
         * Caption getter.
         *
         * @return the caption.
         */
        public String getCaption(){
            return mCaption;
        }

        /**
         * Url getter.
         *
         * @return the url.
         */
        public String getUrl(){
            return mUrl;
        }
    }
}

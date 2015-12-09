package org.tndata.android.compass.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.tndata.android.compass.CompassApplication;
import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.ChooseCategoriesAdapter;
import org.tndata.android.compass.fragment.ChooseCategoriesFragment;
import org.tndata.android.compass.model.Category;
import org.tndata.android.compass.util.API;
import org.tndata.android.compass.util.NetworkRequest;
import org.tndata.android.compass.util.Parser;

import java.util.ArrayList;
import java.util.List;


/**
 * Activity that wraps ChooseCategoriesFragment for standalone display.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class ChooseCategoriesActivity
        extends AppCompatActivity
        implements
                ChooseCategoriesAdapter.OnCategoriesSelectedListener,
                NetworkRequest.RequestCallback{

    private CompassApplication mApplication;

    private List<Category> mSelection;

    //Request codes
    private int mInitialPostCategoryRequestCode;
    private int mLastPostCategoryRequestCode;
    private int mInitialDeleteCategoryRequestCode;
    private int mLastDeleteCategoryRequestCode;
    private int mGetDataRequestCode;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        mApplication = (CompassApplication) getApplication();

        Bundle args = new Bundle();
        args.putBoolean(ChooseCategoriesFragment.ON_BOARDING_KEY, false);

        ChooseCategoriesFragment fragment = new ChooseCategoriesFragment();
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.base_content, fragment).commit();
    }

    @Override
    public void onCategoriesSelected(List<Category> selection){
        mSelection = selection;

        List<Category> toAdd = new ArrayList<>();
        for (Category category:selection){
            if (!mApplication.getCategories().contains(category)){
                toAdd.add(category);
            }
        }

        if (toAdd.size() > 0){
            for (int i = 0; i < toAdd.size(); i++){
                if (i == 0){
                    mInitialPostCategoryRequestCode = NetworkRequest.post(this, this,
                            API.getUserCategoriesUrl(), mApplication.getToken(),
                            API.getPostUserCategoryBody(toAdd.get(i).getId()));
                    mLastPostCategoryRequestCode = mInitialPostCategoryRequestCode+toAdd.size();
                }
                else{
                    NetworkRequest.post(this, this, API.getUserCategoriesUrl(), mApplication.getToken(),
                            API.getPostUserCategoryBody(toAdd.get(i).getId()));
                }
            }
        }
        else{
            deleteCategories();
        }
    }

    /**
     * Deletes the unselected categories.
     */
    private void deleteCategories(){
        List<Category> toDelete = new ArrayList<>();
        for (Category category:mApplication.getCategories()){
            if (!mSelection.contains(category)){
                toDelete.add(category);
            }
        }

        if (toDelete.size() > 0){
            for (int i = 0; i < toDelete.size(); i++){
                //TODO fix this
                if (i == 0){
                    mInitialDeleteCategoryRequestCode = NetworkRequest.delete(this, this,
                            API.getUserCategoriesUrl(), mApplication.getToken(),
                            API.getDeleteUserCategoryBody(toDelete.get(i).getMappingId()));
                    mLastDeleteCategoryRequestCode = mInitialDeleteCategoryRequestCode+toDelete.size();
                }
                else{
                    NetworkRequest.delete(this, this, API.getUserCategoriesUrl(), mApplication.getToken(),
                            API.getDeleteUserCategoryBody(toDelete.get(i).getMappingId()));
                }
            }
        }
        else{
            getUserData();
        }
    }

    /**
     * Triggers data retrieval from the API.
     */
    private void getUserData(){
        mGetDataRequestCode = NetworkRequest.get(this, this, API.getUserDataUrl(),
                mApplication.getToken(), 60 * 1000);
    }

    @Override
    public void onRequestComplete(int requestCode, String result){
        if (requestCode < mLastPostCategoryRequestCode){
            mInitialPostCategoryRequestCode++;
            if (mInitialPostCategoryRequestCode == mLastPostCategoryRequestCode){
                deleteCategories();
            }
        }
        else if (requestCode < mLastDeleteCategoryRequestCode){
            mInitialDeleteCategoryRequestCode++;
            if (mInitialDeleteCategoryRequestCode == mLastDeleteCategoryRequestCode){
                getUserData();
            }
        }
        else if (requestCode == mGetDataRequestCode){
            mApplication.setUserData(new Parser().parseUserData(this, result));
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void onRequestFailed(int requestCode){
        if (requestCode < mLastPostCategoryRequestCode){
            mInitialPostCategoryRequestCode++;
            if (mInitialPostCategoryRequestCode == mLastPostCategoryRequestCode){
                deleteCategories();
            }
        }
        else if (requestCode < mLastDeleteCategoryRequestCode){
            mInitialDeleteCategoryRequestCode++;
            if (mInitialDeleteCategoryRequestCode == mLastDeleteCategoryRequestCode){
                getUserData();
            }
        }
        else if (requestCode == mGetDataRequestCode){
            finish();
        }
    }
}

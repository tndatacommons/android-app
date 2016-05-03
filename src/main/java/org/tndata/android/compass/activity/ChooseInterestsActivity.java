package org.tndata.android.compass.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.tndata.android.compass.R;
import org.tndata.android.compass.adapter.ChooseInterestsAdapter;
import org.tndata.android.compass.fragment.ChooseInterestsFragment;
import org.tndata.android.compass.model.TDCCategory;
import org.tndata.android.compass.model.UserCategory;
import org.tndata.android.compass.util.API;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.sandwatch.httprequests.HttpRequest;
import es.sandwatch.httprequests.HttpRequestError;


/**
 * Activity that wraps ChooseCategoriesFragment for standalone display.
 *
 * @author Ismael Alonso
 * @version 1.0.0
 */
public class ChooseInterestsActivity
        extends AppCompatActivity
        implements
                ChooseInterestsAdapter.OnCategoriesSelectedListener,
                HttpRequest.RequestCallback{

    private List<TDCCategory> mSelection;
    private Map<Long, UserCategory> mSelectedMap;

    //Request codes
    private int mInitialPostCategoryRequestCode;
    private int mLastPostCategoryRequestCode;
    private int mInitialDeleteCategoryRequestCode;
    private int mLastDeleteCategoryRequestCode;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        Bundle args = new Bundle();
        args.putBoolean(ChooseInterestsFragment.ON_BOARDING_KEY, false);

        ChooseInterestsFragment fragment = new ChooseInterestsFragment();
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.base_content, fragment).commit();
    }

    @Override
    public void onCategoriesSelected(List<TDCCategory> selection, List<UserCategory> original){
        mSelection = selection;

        mSelectedMap = new HashMap<>();
        for (UserCategory category:original){
            mSelectedMap.put(category.getContentId(), category);
        }

        List<TDCCategory> toAdd = new ArrayList<>();
        for (TDCCategory category:selection){
            if (!mSelectedMap.containsKey(category.getId())){
                toAdd.add(category);
            }
        }

        if (toAdd.size() > 0){
            for (int i = 0; i < toAdd.size(); i++){
                if (i == 0){
                    mInitialPostCategoryRequestCode = HttpRequest.post(this,
                            API.getUserCategoriesUrl(), API.getPostCategoryBody(toAdd.get(i)));
                    mLastPostCategoryRequestCode = mInitialPostCategoryRequestCode+toAdd.size();
                }
                else{
                    HttpRequest.post(this, API.getUserCategoriesUrl(),
                            API.getPostCategoryBody(toAdd.get(i)));
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
        List<UserCategory> toDelete = new ArrayList<>();
        for (UserCategory userCategory:mSelectedMap.values()){
            if (!mSelection.contains(userCategory.getCategory())){
                toDelete.add(userCategory);
            }
        }

        if (toDelete.size() > 0){
            for (int i = 0; i < toDelete.size(); i++){
                if (i == 0){
                    mInitialDeleteCategoryRequestCode = HttpRequest.delete(this,
                            API.getDeleteCategoryUrl(toDelete.get(i)));
                    mLastDeleteCategoryRequestCode = mInitialDeleteCategoryRequestCode+toDelete.size();
                }
                else{
                    HttpRequest.delete(this, API.getDeleteCategoryUrl(toDelete.get(i)));
                }
            }
        }
        else{
            setResult(RESULT_OK);
            finish();
        }
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
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    @Override
    public void onRequestFailed(int requestCode, HttpRequestError error){
        if (requestCode < mLastPostCategoryRequestCode){
            mInitialPostCategoryRequestCode++;
            if (mInitialPostCategoryRequestCode == mLastPostCategoryRequestCode){
                deleteCategories();
            }
        }
        else if (requestCode < mLastDeleteCategoryRequestCode){
            mInitialDeleteCategoryRequestCode++;
            if (mInitialDeleteCategoryRequestCode == mLastDeleteCategoryRequestCode){
                setResult(RESULT_OK);
                finish();
            }
        }
    }
}

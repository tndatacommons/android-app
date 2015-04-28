package org.tndata.android.grow.adapter;

import java.util.ArrayList;

import org.tndata.android.grow.R;
import org.tndata.android.grow.model.Category;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChooseCategoryAdapter extends ArrayAdapter<Category> {
    public interface ChooseCategoryAdapterListener {
        public ArrayList<Category> getCurrentlySelectedCategories();
    }

    private Context mContext;
    private ArrayList<Category> mItems;
    private ChooseCategoryAdapterListener mCallback;

    public ChooseCategoryAdapter(Context context, int resource,
                                 ArrayList<Category> objects,
                                 ChooseCategoryAdapterListener callback) {
        super(context, resource, objects);
        this.mItems = objects;
        this.mContext = context;
        this.mCallback = callback;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    public void updateEntries(ArrayList<Category> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    @SuppressLint({"InflateParams", "DefaultLocale"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_item_category_grid, null);
        }

        final Category category = mItems.get(position);
        if (category.getColor() != null && !category.getColor().isEmpty()) {
            v.setBackgroundColor(Color.parseColor(
                    category.getColor()));
        }

        final TextView title = (TextView) v
                .findViewById(R.id.list_item_category_grid_category_textview);

        title.setText(category.getTitle().toUpperCase());
        final ImageView check = (ImageView) v
                .findViewById(R.id.list_item_category_grid_category_imageview);
        final ArrayList<Category> selectedItems = mCallback
                .getCurrentlySelectedCategories();
        if (selectedItems.contains(category)) {
            check.setVisibility(View.VISIBLE);
        } else {
            check.setVisibility(View.GONE);
        }

        return v;
    }
}

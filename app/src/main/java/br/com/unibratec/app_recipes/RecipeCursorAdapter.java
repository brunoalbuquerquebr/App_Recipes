package br.com.unibratec.app_recipes;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import br.com.unibratec.app_recipes.database.RecipeContract;

/**
 * Created by brunoalbuquerque on 30/09/16.
 */

public class RecipeCursorAdapter extends SimpleCursorAdapter {

    private static final int LAYOUT = R.layout.item_recipe;

    public RecipeCursorAdapter(Context context, Cursor c) {
        super(context, LAYOUT, c, RecipeContract.LIST_COLUMNS, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(LAYOUT, parent, false);

        VH vh = new VH();
        vh.imageViewImage_url = (ImageView) view.findViewById(R.id.recipe_item_image_url);
        vh.textViewTitle = (TextView) view.findViewById(R.id.recipe_item_text_title);
        //vh.textViewPublisher = (TextView) view.findViewById(R.id.recipe_item_text_publisher);
        view.setTag(vh);

        ViewCompat.setTransitionName(vh.imageViewImage_url, "capa");
        ViewCompat.setTransitionName(vh.textViewTitle, "titulo");
        //ViewCompat.setTransitionName(vh.textViewPublisher, "publisher");

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String image = cursor.getString(cursor.getColumnIndex(RecipeContract.COL_IMAGE_URL));
        String title = cursor.getString(cursor.getColumnIndex(RecipeContract.COL_TITLE));
        String publisher = cursor.getString(cursor.getColumnIndex(RecipeContract.COL_PUBLISHER));

        VH vh = (VH)view.getTag();
        Glide.with(context)
                .load(image)
                .placeholder(R.drawable.ic_placeholder)
                .into(vh.imageViewImage_url);
        vh.textViewTitle.setText(title);
        //vh.textViewPublisher.setText(publisher);
    }

    class VH {
        ImageView imageViewImage_url;
        TextView textViewTitle;
        //TextView textViewPublisher;
    }
}

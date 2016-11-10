package br.com.unibratec.app_recipes;

import android.content.Context;
import android.database.Cursor;

import com.github.clans.fab.FloatingActionButton;

import br.com.unibratec.app_recipes.database.RecipeContract;
import br.com.unibratec.app_recipes.database.RecipeProvider;

//import android.support.design.widget.FloatingActionButton;

/**
 * Created by brunoalbuquerque on 30/09/16.
 */

public class RecipeDetailUtils {

    public static boolean isFavorite(Context ctx, String recipe_id){
        Cursor cursor = ctx.getContentResolver().query(
                RecipeProvider.RECIPES_URI,
                new String[]{ RecipeContract._ID },
                RecipeContract.COL_RECIPE_ID +" = ?",
                new String[]{ recipe_id },
                null
        );
        boolean isFavorite = false;
        if (cursor != null) {
            isFavorite = cursor.getCount() > 0;
            cursor.close();
        }
        return isFavorite;
    }

    public static void toggleFavorite(Context ctx, FloatingActionButton fab2, String recipe_id){
        if (RecipeDetailUtils.isFavorite(ctx, recipe_id)){
            fab2.setImageResource(R.drawable.ic_favorite);
        } else {
            fab2.setImageResource(R.drawable.ic_unfavorite);
        }
    }
}

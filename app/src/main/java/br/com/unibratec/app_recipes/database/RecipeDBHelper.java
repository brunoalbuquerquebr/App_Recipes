package br.com.unibratec.app_recipes.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by brunoalbuquerque on 30/09/16.
 */

public class RecipeDBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "dbRecipes";
    private static final int DB_VERSION = 1;

    public RecipeDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE "+ RecipeContract.TABLE_NAME +" (" +
                        RecipeContract._ID +" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        RecipeContract.COL_RECIPE_ID  +" TEXT UNIQUE NOT NULL, "+
                        RecipeContract.COL_TITLE    +" TEXT NOT NULL, "+
                        RecipeContract.COL_PUBLISHER     +" TEXT NOT NULL, "+
                        RecipeContract.COL_IMAGE_URL   +" TEXT NOT NULL, "+
                        RecipeContract.COL_INGREDIENTS   +" TEXT NOT NULL, "+
                        RecipeContract.COL_SOCIAL_RANK   +" TEXT NOT NULL, "+
                        RecipeContract.COL_SOURCE_URL   +" TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}

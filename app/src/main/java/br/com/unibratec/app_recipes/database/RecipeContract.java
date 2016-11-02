package br.com.unibratec.app_recipes.database;

import android.provider.BaseColumns;

/**
 * Created by brunoalbuquerque on 30/09/16.
 */

public interface RecipeContract extends BaseColumns {
    // Nome da tabela no banco de dados
    String TABLE_NAME = "Recipes";

    // Colunas do banco de dados
    String COL_RECIPE_ID  = "recipe_id";
    String COL_TITLE    = "title";
    String COL_PUBLISHER     = "publisher";
    String COL_IMAGE_URL   = "image_url";
    String COL_INGREDIENTS   = "ingredients";
    String COL_SOCIAL_RANK   = "social_rank";
    String COL_SOURCE_URL   = "source_url";

    // Colunas utilizadas pelo adapter do fragment de favoritos
    String[] LIST_COLUMNS = new String[]{
            RecipeContract._ID,
            RecipeContract.COL_RECIPE_ID,
            RecipeContract.COL_TITLE,
            RecipeContract.COL_IMAGE_URL,
            RecipeContract.COL_PUBLISHER,
            RecipeContract.COL_SOURCE_URL
    };
}

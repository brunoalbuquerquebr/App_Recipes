package br.com.unibratec.app_recipes.http;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import br.com.unibratec.app_recipes.model.Recipe;

/**
 * Created by brunoalbuquerque on 30/09/16.
 */

// Essa tarefa está carregando as informações da receita baseado no ID do recipe_id
public class RecipeByIdTask extends AsyncTaskLoader<Recipe> {

    private Recipe mRecipe;
    private String mId;

    public RecipeByIdTask(Context context, String id) {
        super(context);
        mId = id;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (mRecipe == null || !mRecipe.getRecipe_id().equals(mId)) {
            forceLoad();
        } else {
            deliverResult(mRecipe);
        }
    }

    @Override
    public Recipe loadInBackground() {
        mRecipe = RecipeHttp.loadRecipeById(mId);
        return mRecipe;
    }
}

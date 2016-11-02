package br.com.unibratec.app_recipes.http;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.ArrayList;
import java.util.List;

import br.com.unibratec.app_recipes.model.Recipe;

/**
 * Created by brunoalbuquerque on 30/09/16.
 */

// Essa tarefa carrega a lista dos filmes baseado nos parâmetros da busca
public class RecipeSearchTask extends AsyncTaskLoader<List<Recipe>> {
    List<Recipe> recipes;
    String query;

    public RecipeSearchTask(Context context, String query) {
        super(context);
        this.query = query;
        this.recipes = new ArrayList<>();
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (query != null) {
            forceLoad();
        } else {
            deliverResult(recipes);
        }
    }

    @Override
    public List<Recipe> loadInBackground() {
        recipes.addAll(RecipeHttp.searchRecipe(query));
        return recipes;
    }
}

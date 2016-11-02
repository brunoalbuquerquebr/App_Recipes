package br.com.unibratec.app_recipes;

import android.view.View;

import br.com.unibratec.app_recipes.model.Recipe;

/**
 * Created by brunoalbuquerque on 30/09/16.
 */

public interface OnRecipeClickListener {
    void onRecipeClick(View view, Recipe recipe, int position);
}

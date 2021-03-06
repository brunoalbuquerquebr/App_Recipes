package br.com.unibratec.app_recipes;

import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import br.com.unibratec.app_recipes.database.RecipeContract;
import br.com.unibratec.app_recipes.database.RecipeProvider;
import br.com.unibratec.app_recipes.model.Recipe;

/**
 * Created by brunoalbuquerque on 30/09/16.
 */

public class FavoriteRecipesFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    OnRecipeClickListener mRecipeClickListener;
    RecipeCursorAdapter mAdapter;
    boolean mFirstRun;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Registrando o listener para saber quando recipe foi clicado
        // Essa abordagem é a mais usada, e mais rápida
        // entretanto requer um atributo adicional
        if (context instanceof OnRecipeClickListener) {
            mRecipeClickListener = (OnRecipeClickListener) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirstRun = savedInstanceState == null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_favorite_recipes, container, false);
        ListView listView = (ListView) view.findViewById(R.id.favorites_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mRecipeClickListener != null) {
                    // Pegamos o cursor do adapter
                    Cursor cursor = mAdapter.getCursor();
                    // Movemos para a posição correspondente da lista
                    selectRecipe(view, position, cursor);
                }
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            listView.setNestedScrollingEnabled(true);
        }

        // Inicializamos e definimos o adapter da lista
        mAdapter = new RecipeCursorAdapter(getActivity(), null);
        listView.setAdapter(mAdapter);

        // Definimos a view a ser exibida se a lista estiver vazia
        listView.setEmptyView(view.findViewById(R.id.empty_view_root));

        // Inicializamos o loader para trazer os registros em background
        getLoaderManager().initLoader(0, null, this);

        return view;
    }
    /*
    private void selectRecipe(View view, int position, Cursor cursor) {
        if (cursor.moveToPosition(position)) {
            // Criamos um objeto Recipe para passamos para a MainActivity
            // perceba que esse Recipe não tem todos os campos. Pois na tela
            // de listagem apenas os campos necessários são utilizados
            Recipe recipe = new Recipe();
            recipe.setId(cursor.getLong(cursor.getColumnIndex(RecipeContract._ID)));
            recipe.setRecipe_id(cursor.getString(cursor.getColumnIndex(RecipeContract.COL_RECIPE_ID)));
            recipe.setTitle(cursor.getString(cursor.getColumnIndex(RecipeContract.COL_TITLE)));
            recipe.setImage_url(cursor.getString(cursor.getColumnIndex(RecipeContract.COL_IMAGE_URL)));
            recipe.setPublisher(cursor.getString(cursor.getColumnIndex(RecipeContract.COL_PUBLISHER)));
            recipe.setSource_url(cursor.getString(cursor.getColumnIndex(RecipeContract.COL_SOURCE_URL)));
            mRecipeClickListener.onRecipeClick(view, recipe, position);
        }
    }
    */

    private void selectRecipe(View view, int position, Cursor cursor) {
        if (cursor.moveToPosition(position)) {
            // Criamos um objeto Movie para passamos para a MainActivity
            // perceba que esse Movie não tem todos os campos. Pois na tela
            // de listagem apenas os campos necessários são utilizados
            Recipe recipe = RecipeDetailUtils.recipeItemFromCursor(cursor);
            mRecipeClickListener.onRecipeClick(view, recipe, position);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Realizando a query em background (ver método query do RecipeProvider)
        return new CursorLoader(getActivity(),
                RecipeProvider.RECIPES_URI,
                RecipeContract.LIST_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {
        mAdapter.swapCursor(data);
        if (data != null
                && data.getCount() > 0
                && getResources().getBoolean(R.bool.tablet)
                && mFirstRun){

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    selectRecipe(null, 0, data);
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}

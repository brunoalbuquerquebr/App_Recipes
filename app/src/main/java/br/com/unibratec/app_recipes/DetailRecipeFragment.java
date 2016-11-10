package br.com.unibratec.app_recipes;

import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.Arrays;

import br.com.unibratec.app_recipes.database.RecipeContract;
import br.com.unibratec.app_recipes.database.RecipeProvider;
import br.com.unibratec.app_recipes.http.RecipeByIdTask;
import br.com.unibratec.app_recipes.model.Recipe;

/**
 * Created by brunoalbuquerque on 30/09/16.
 */

public class DetailRecipeFragment extends Fragment {

    private static final String EXTRA_RECIPE = "recipe";
    private static final int LOADER_DB = 0;
    private static final int LOADER_WEB = 1;

    ImageView imgImage;
    TextView txtTitle;
    TextView  txtPublisher;
    TextView  txtTitleIngredients;
    TextView  txtIngredients;
    RatingBar social_rank;
    //TextView source_url;


    Recipe mRecipe;
    LocalBroadcastManager mLocalBroadcastManager;
    RecipeEventReceiver mReceiver;
    ShareActionProvider mShareActionProvider;
    Intent mShareIntent;

    // Para criarmos um DetailRecipeFragment precisamos passar um objeto Recipe
    public static DetailRecipeFragment newInstance(Recipe recipe) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_RECIPE, recipe);

        DetailRecipeFragment detailRecipeFragment = new DetailRecipeFragment();
        detailRecipeFragment.setArguments(args);
        return detailRecipeFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inicializando o layout do fragment...
        View view = inflater.inflate(R.layout.fragment_detail_recipe, container, false);
        imgImage               = (ImageView)view.findViewById(R.id.detail_image_url);
        txtTitle                = (TextView)view.findViewById(R.id.detail_text_title);
        txtPublisher                 = (TextView)view.findViewById(R.id.detail_text_publisher);
        txtTitleIngredients     = (TextView)view.findViewById(R.id.detail_text_title_ingredients);
        txtIngredients               = (TextView)view.findViewById(R.id.detail_text_ingredients);
        social_rank                  = (RatingBar)view.findViewById(R.id.detail_social_rank);



        // Animação de transição de tela
        ViewCompat.setTransitionName(imgImage, "capa");
        ViewCompat.setTransitionName(txtTitle, "titulo");
        ViewCompat.setTransitionName(txtPublisher, "publisher");

        // Inicializamos mRecipe (ver onSaveInsatnceState)
        if (savedInstanceState == null){
            // Se não tem um estado anterior, use o que foi passado no método newInstance.
            mRecipe = (Recipe)getArguments().getSerializable(EXTRA_RECIPE);
        } else {
            // Se há um estado anterior, use-o
            mRecipe = (Recipe)savedInstanceState.getSerializable(EXTRA_RECIPE);
        }

        // Se o objeto mRecipe possui um ID (no banco local), carregue do banco local,
        // senão carregue do servidor.
        boolean isFavorite = RecipeDetailUtils.isFavorite(getActivity(), mRecipe.getRecipe_id());
        if (isFavorite){
            // Faz a requisição em background ao banco de dados (ver mCursorCallback)
            getLoaderManager().initLoader(LOADER_DB, null, mCursorCallback);
        } else {
            // Faz a requisição em background ao servidor (ver mRecipeCallback)
            getLoaderManager().initLoader(LOADER_WEB, null, mRecipeCallback);
        }

        // Registramos o receiver para tratar sabermos quando o botão de favoritos da
        // activity de detalhes foi chamado.
        mReceiver = new RecipeEventReceiver();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(RecipeEvent.UPDATE_FAVORITE));

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Precisamos manter o objeto mRecipe atualizado pois ele pode ter sido
        // incluído e excluído dos favoritos.
        outState.putSerializable(EXTRA_RECIPE, mRecipe);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Desregistramos o receiver ao destruir a View do fragment
        mLocalBroadcastManager.unregisterReceiver(mReceiver);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_detail, menu);

        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        if (mShareIntent != null){
            mShareActionProvider.setShareIntent(mShareIntent);
        }
    }

    // --------------- LoaderManager.LoaderCallbacks<Recipe>
    // Esse callback trata o retorno da requisição feita ao servidor
    LoaderManager.LoaderCallbacks mRecipeCallback = new LoaderManager.LoaderCallbacks<Recipe>() {
        @Override
        public Loader<Recipe> onCreateLoader(int id, Bundle args) {
            // inicializa a requisição em background para o servidor usando AsyncTaskLoader
            // (veja a classe RecipeByIdTask)
            return new RecipeByIdTask(getActivity(), mRecipe.getRecipe_id());
        }

        @Override
        public void onLoadFinished(Loader<Recipe> loader, Recipe recipe) {
            updateUI(recipe);
        }

        @Override
        public void onLoaderReset(Loader<Recipe> loader) {
        }
    };

    // --------------- LoaderManager.LoaderCallbacks<Cursor>
    // Esse callback trata o retorno da requisição feita ao servidor
    LoaderManager.LoaderCallbacks<Cursor> mCursorCallback = new LoaderManager.LoaderCallbacks<Cursor>(){

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            // inicializa a requisição em background para o ContentProvider usando CursorLoader
            // perceba que estamos utilizando a Uri específica
            // (veja o método query do RecipeProvider)
            return new CursorLoader(getActivity(),
                    RecipeProvider.RECIPES_URI,
                    null,
                    RecipeContract.COL_RECIPE_ID +" = ?",
                    new String[]{ mRecipe.getRecipe_id() }, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            // Ao receber o retorno do cursor, criamos um objeto Recipe e preenchemos a tela
            // (ver updateUI)
            if (cursor != null && cursor.moveToFirst()) {
                Recipe recipe = new Recipe();
                recipe.setId(cursor.getLong(cursor.getColumnIndex(RecipeContract._ID)));
                recipe.setRecipe_id(cursor.getString(cursor.getColumnIndex(RecipeContract.COL_RECIPE_ID)));
                recipe.setTitle(cursor.getString(cursor.getColumnIndex(RecipeContract.COL_TITLE)));
                recipe.setImage_url(cursor.getString(cursor.getColumnIndex(RecipeContract.COL_IMAGE_URL)));
                recipe.setPublisher(cursor.getString(cursor.getColumnIndex(RecipeContract.COL_PUBLISHER)));
                recipe.setIngredients(cursor.getString(cursor.getColumnIndex(RecipeContract.COL_INGREDIENTS)).split(","));
                recipe.setSocial_rank(cursor.getFloat(cursor.getColumnIndex(RecipeContract.COL_SOCIAL_RANK)));
                recipe.setSource_url(cursor.getString(cursor.getColumnIndex(RecipeContract.COL_SOURCE_URL)));
                updateUI(recipe);
            }


        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    };

    private void createShareIntent(Recipe recipe) {
        mShareIntent = new Intent(Intent.ACTION_SEND);
        mShareIntent.setType("text/plain");
        mShareIntent.putExtra(Intent.EXTRA_TEXT,
                getString(R.string.share_text, recipe.getTitle() + ", " + recipe.getSource_url()));
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(mShareIntent);
        }
    }

    // --------------- INNER
    // Esse receiver é chamado pelo FAB da DetailActivity para iniciar o processo
    // de inserir/excluir o recipe nos favoritos
    class RecipeEventReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(RecipeEvent.UPDATE_FAVORITE)) {
                toggleFavorite();
            }
        }
    }

    // --------------- PRIVATE

    private void updateUI(Recipe recipe){
        // Atualiza o objeto mRecipe com os dados vindos dos callbacks
        // (ver mCursorCallback e mRecipeCallback)
        mRecipe = recipe;
        txtTitle.setText(recipe.getTitle());
        txtPublisher.setText(recipe.getPublisher());
        social_rank.setRating(recipe.getSocial_rank() / 20);
        //source_url.setText(recipe.getSource_url());



        // Tanto no JSON quanto no Banco, estamos salvando a lista
        // de atores separado por vírgula
        //TODO Criar uma nova tabela e fazer chave estrangeira
        StringBuffer sb = new StringBuffer();
        for (String ingredient :
                recipe.getIngredients()) {
            sb.append(ingredient).append('\n');
        }
        txtIngredients.setText(sb.toString());

        // Enviando mensagem para todos que querem saber que o filme carregou
        // (ver DetailActivity.RecipeReceiver)
        notifyUpdate(RecipeEvent.RECIPE_LOADED);

        // Quando estiver em tablet, exiba o poster no próprio fragment
        if (getResources().getBoolean(R.bool.tablet)){
            imgImage.setVisibility(View.VISIBLE);
            Glide.with(imgImage.getContext()).load(recipe.getImage_url()).into(imgImage);
        }
        createShareIntent(recipe);
    }



    // Método auxiliar que insere/remove o recipe no banco de dados
    private void toggleFavorite() {
        if (mRecipe == null) return; // isso não deve acontecer...

        // Primeiro verificamos se o livro está no banco de dados
        boolean isFavorite = RecipeDetailUtils.isFavorite(getActivity(), mRecipe.getRecipe_id());

        boolean success = false;
        if (isFavorite) {
            // Se já é favorito, exclua
            if (deleteFavorite(mRecipe.getId())){
                success = true;
                mRecipe.setId(0);
                getLoaderManager().destroyLoader(LOADER_DB);
            }
            //TODO Mensagem de erro ao excluir

        } else {
            // Se não é favorito, inclua...
            long id = insertFavorite(mRecipe);
            success = id > 0;
            mRecipe.setId(id);
        }

        // Se deu tudo certo...
        if (success) {
            // Envia a mensagem para as activities (para atualizar o FAB)
            notifyUpdate(RecipeEvent.RECIPE_FAVORITE_UPDATED);

            // Exibe o snackbar que permite o "desfazer"
            //TODO Internailizar a aplicação
            /*
            Snackbar.make(getView(),
                    isFavorite ? R.string.msg_removed_favorites : R.string.msg_added_favorites,
                    Snackbar.LENGTH_LONG)
                    .setAction(R.string.text_undo, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            toggleFavorite();
                        }
                    }).show();
            */
            Toast.makeText(getActivity(), isFavorite ? R.string.msg_removed_favorites : R.string.msg_added_favorites,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void notifyUpdate(String action){
        // Cria a intent e dispara o broadcast
        Intent it = new Intent(action);
        it.putExtra(RecipeEvent.EXTRA_RECIPE, mRecipe);
        mLocalBroadcastManager.sendBroadcast(it);
    }

    // Método auxiliar para excluir nos favoritos
    //TODO fazer delete em background
    private boolean deleteFavorite(long recipeId){
        return getActivity().getContentResolver().delete(
                ContentUris.withAppendedId(RecipeProvider.RECIPES_URI, recipeId),
                null, null) > 0;
    }

    // Método auxiliar para inserir nos favoritos
    //TODO fazer insert em background
    private long insertFavorite(Recipe recipe){
        ContentValues contentValues = new ContentValues();
        contentValues.put(RecipeContract.COL_RECIPE_ID , recipe.getRecipe_id());
        contentValues.put(RecipeContract.COL_TITLE   , recipe.getTitle());
        contentValues.put(RecipeContract.COL_PUBLISHER    , recipe.getPublisher());
        contentValues.put(RecipeContract.COL_IMAGE_URL  , recipe.getImage_url());
        contentValues.put(RecipeContract.COL_INGREDIENTS  , Arrays.toString(recipe.getIngredients()).replaceAll("\\[|\\]", ""));
        contentValues.put(RecipeContract.COL_SOCIAL_RANK  , recipe.getSocial_rank());
        contentValues.put(RecipeContract.COL_SOURCE_URL   , recipe.getSource_url());

        Uri uri = getActivity().getContentResolver().insert(RecipeProvider.RECIPES_URI, contentValues);
        //TODO mensagem de erro ao falhar
        return ContentUris.parseId(uri);
    }
}

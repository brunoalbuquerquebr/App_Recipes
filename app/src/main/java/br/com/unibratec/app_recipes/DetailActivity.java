package br.com.unibratec.app_recipes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import br.com.unibratec.app_recipes.model.Recipe;

/**
 * Created by brunoalbuquerque on 30/09/16.
 */

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_RECIPE = "recipe"; // vindo dos favoritos

    Recipe mRecipe;
    FloatingActionMenu fab;
        FloatingActionButton fab1;
        FloatingActionButton fab2;

    LocalBroadcastManager mLocalBroadcastManager;
    RecipeReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // A MainActivity passará um objeto Recipe,
        // então criamos o fragment de detalhe com esse objeto
        mRecipe = (Recipe)getIntent().getSerializableExtra(EXTRA_RECIPE);
        DetailRecipeFragment detailRecipeFragment;
        detailRecipeFragment = DetailRecipeFragment.newInstance(mRecipe);

        // Todas as informações do filme estão no DetailRecipeFragment,
        // exceto a capa que já carregamos aqui, uma vez que essa informação
        // já existe no objeto Recipe.
        ImageView imgImage = (ImageView)findViewById(R.id.detail_image_url);
        ViewCompat.setTransitionName(imgImage, "capa");
        Glide.with(imgImage.getContext()).load(mRecipe.getImage_url()).into(imgImage);

        // Esse receiver detectará se o Recipe foi adicionado ou removido dos favoritos
        // TODO Substituir pelo EventBus?
        mReceiver = new RecipeReceiver();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(RecipeEvent.RECIPE_LOADED));
        mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(RecipeEvent.RECIPE_FAVORITE_UPDATED));

        // O FAB faz parte do layout da Activity, mas precisa ser atualizado
        // quando a receita é inserida ou removida dos favoritos. mReceiver fará isso ;)
        fab = (FloatingActionMenu) findViewById(R.id.fab);
        //fab.setOnClickListener(new View.OnClickListener() {
        //   @Override
        //    public void onClick(View view) {
        //       // Quando clicamos no botão, estamos avisando ao fragment de detalhes para
        //        // inserir/remover o Recipe no banco. Veja DetailRecipeFragment.RecipeEventReceiver.
        //        Intent it = new Intent(RecipeEvent.UPDATE_FAVORITE);
        //        mLocalBroadcastManager.sendBroadcast(it);
        //    }
        //});

        fab.showMenuButton(true);
        fab.setClosedOnTouchOutside(true);

        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);

        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);

        if (savedInstanceState == null) {
            // Adicionando o fragment de detalhes na tela
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.placeholderDetail, detailRecipeFragment)
                    .commit();
        }

        //TODO barra de status transparente?
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.fab1:
                //Toast.makeText(this, "Receita",Toast.LENGTH_SHORT).show();
                customTabs();
                fab.close(true);
                break;
            case R.id.fab2:
                // Quando clicamos no botão, estamos avisando ao fragment de detalhes para
                // inserir/remover o Recipe no banco. Veja DetailRecipeFragment.RecipeEventReceiver.
                Intent it = new Intent(RecipeEvent.UPDATE_FAVORITE);
                mLocalBroadcastManager.sendBroadcast(it);
                fab.close(true);
                break;
        }

    }

    public void customTabs(){
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ActivityCompat.getColor(this, R.color.colorAccent));
        CustomTabsIntent intent = builder.build();
        String urlRecipe = mRecipe.getSource_url();
        intent.launchUrl(this, Uri.parse(urlRecipe));

    }

    // Esse receiver atualizará o status do botão de favoritos.
    class RecipeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            fab.setVisibility(View.VISIBLE);
            Recipe recipe = (Recipe)intent.getSerializableExtra(RecipeEvent.EXTRA_RECIPE);
            RecipeDetailUtils.toggleFavorite(context, fab2, recipe.getRecipe_id());
        }
    }
}
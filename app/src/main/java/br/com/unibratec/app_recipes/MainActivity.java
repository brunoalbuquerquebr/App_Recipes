package br.com.unibratec.app_recipes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import br.com.unibratec.app_recipes.model.Recipe;


public class MainActivity extends AppCompatActivity implements OnRecipeClickListener {

    FloatingActionButton fab;

    LocalBroadcastManager mLocalBroadcastManager;
    RecipeReceiver mReceiver;
    Recipe mRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Inicializando o PagerAdapter, ViewPager e TabLayout para exibir as abas
        RecipesPagerAdapter pagerAdapter = new RecipesPagerAdapter(getSupportFragmentManager());

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        // Definimos alguns comportamentos especiais para tablets...
        if (getResources().getBoolean(R.bool.tablet)){
            // As abas ficam alinhadas a esquerda
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

            // Inicializamos esse receiver para saber quando o filme no fragment de detalhe
            // foi carregado (ver método notifyUpdate da DetailRecipeFragment)
            mReceiver = new RecipeReceiver();
            mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
            mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(RecipeEvent.RECIPE_LOADED));
            mLocalBroadcastManager.registerReceiver(mReceiver, new IntentFilter(RecipeEvent.RECIPE_FAVORITE_UPDATED));

            // O FAB envia a mensagem para o DetailFragment inserir/excluir filme no banco
            fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent it = new Intent(RecipeEvent.UPDATE_FAVORITE);
                    mLocalBroadcastManager.sendBroadcast(it);
                }
            });


        }
    }

    @Override
    public void onRecipeClick(View view, Recipe recipe, int position) {
        // Esse método é chamado pelas telas de listagem quando o usuário
        // clica em um item da lista (ver RecipeListFragment e FavoriteRecipesFragment)
        if (getResources().getBoolean(R.bool.phone)) {
            ActivityOptionsCompat optionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                            Pair.create(view.findViewById(R.id.recipe_item_image_url), "capa"),
                            Pair.create(view.findViewById(R.id.recipe_item_text_title), "titulo"));
                            //Pair.create(view.findViewById(R.id.recipe_item_text_publisher), "publisher"));

            // Se for smartphone, abra uma nova activity
            Intent it = new Intent(MainActivity.this, DetailActivity.class);
            it.putExtra(DetailActivity.EXTRA_RECIPE, recipe);
            ActivityCompat.startActivity(this, it, optionsCompat.toBundle());

        } else {
            // Se for tablet, exiba um fragment a direita
            DetailRecipeFragment detailRecipeFragment = DetailRecipeFragment.newInstance(recipe);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.placeholderDetail, detailRecipeFragment)
                    .commit();
        }
    }

    // Esse receiver será chamado quando o fragment de detalhe carrega os dados do filme
    // (ver método notifyUpdate de DetailRecipeFragment)
    class RecipeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Recipe recipe = (Recipe)intent.getSerializableExtra(RecipeEvent.EXTRA_RECIPE);
            fab.setVisibility(View.VISIBLE);
            RecipeDetailUtils.toggleFavorite(context, fab, recipe.getRecipe_id());
        }
    }

    public void customTabs(){
        CustomTabsIntent intent = new CustomTabsIntent.Builder().build();
        String urlRecipe = mRecipe.getSource_url();
        intent.launchUrl(this, Uri.parse(urlRecipe));
    }

    // O PagerAdapter é o que determina o que será exibido em cada aba
    class RecipesPagerAdapter extends FragmentPagerAdapter {
        public RecipesPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            if (position == 1){
                RecipeListFragment recipeListFragment = new RecipeListFragment();
                return recipeListFragment;
            } else {
                FavoriteRecipesFragment favoriteRecipesFragment = new FavoriteRecipesFragment();
                return favoriteRecipesFragment;
            }
        }
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 1) return getString(R.string.tab_search);
            else return getString(R.string.tab_favorites);
        }
        @Override
        public int getCount() {
            return 2;
        }
    }
}

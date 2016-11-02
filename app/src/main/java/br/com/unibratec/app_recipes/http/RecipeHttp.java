package br.com.unibratec.app_recipes.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.unibratec.app_recipes.model.Recipe;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by brunoalbuquerque on 30/09/16.
 */

public class RecipeHttp {

    public static final String URL_SEARCH_BY_TITLE = "http://food2fork.com/api/search?q=%s&key=f2af11cf7629d588c2b49965e44f4375";
    public static final String URL_SEARCH_BY_RECIPE_ID = "http://food2fork.com/api/get?key=f2af11cf7629d588c2b49965e44f4375&rId=%s";

    public static List<Recipe> searchRecipe(String query){
        List<Recipe> recipes = new ArrayList<>();

        // Abre a conexão com o servidor
        OkHttpClient client = new OkHttpClient();
        String url = String.format(URL_SEARCH_BY_TITLE, query);
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = null;
        try {
            // Realiza a chamada ao servidor
            response = client.newCall(request).execute();

            // response.body retorna o corpo da resposta, que no nosso caso é JSON
            String json = response.body().string();

            // Esse JSON retorna um objeto JSON onde a propriedade "Search" traz
            // a lista dos resultados. Por isso, obtemos o JSONArray com esse resultado
            // e só então passamos para o GSON ler.
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("recipes");
            String jsonList = jsonArray.toString();

            Gson gson = new Gson();
            Recipe[] recipesArray = gson.fromJson(jsonList, Recipe[].class);
            recipes.addAll(Arrays.asList(recipesArray));

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return recipes;
    }

    public static Recipe loadRecipeById(String recipe_id){

        // Abre a conexão com o servidor
        OkHttpClient client = new OkHttpClient();
        String url = String.format(URL_SEARCH_BY_RECIPE_ID, recipe_id);
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = null;
        try {
            // Realiza a chamada ao servidor
            response = client.newCall(request).execute();

            // response.body retorna o corpo da resposta, que no nosso caso é JSON
            String json = response.body().string();

            // Essa resposta já traz apenas um objeto com todas as informações do filme
            // Então é só passar para o JSON fazer o parser
            JSONObject jsonObject = new JSONObject(json);
            JSONObject jsonObj = jsonObject.getJSONObject("recipe");

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Recipe.class, new RecipeDeserializer());
            Gson gson = gsonBuilder.create();
            return gson.fromJson(String.valueOf(jsonObj), Recipe.class);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}

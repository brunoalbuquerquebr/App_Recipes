package br.com.unibratec.app_recipes.http;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import br.com.unibratec.app_recipes.model.Recipe;

/**
 * Created by brunoalbuquerque on 30/09/16.
 */

// Classe que customiza o GSON para fazer a leitura do JSON vindo do servidor.
// Utilizada na classe RecipeHttp
public class RecipeDeserializer implements JsonDeserializer<Recipe> {

    @Override
    public Recipe deserialize(JsonElement json,
                              Type typeOfT,
                              JsonDeserializationContext context) throws JsonParseException {
        try {
            JsonObject jsonObject = (JsonObject) json;
            Recipe recipe = new Recipe();
            recipe.setRecipe_id(jsonObject.get("recipe_id").getAsString());
            recipe.setTitle(jsonObject.get("title").getAsString());
            recipe.setPublisher(jsonObject.get("publisher").getAsString());
            recipe.setImage_url(jsonObject.get("image_url").getAsString());
            recipe.setIngredients(jsonObject.getAsJsonArray("ingredients").toString().replace("[", "").replace("]", "").replace("\"", "").split(","));
            recipe.setSocial_rank(asFloat(jsonObject.get("social_rank").getAsString()));
            recipe.setSource_url(jsonObject.get("source_url").getAsString());

            return recipe;

        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    float asFloat(String str) {
        String formatado = "";
        float novaStr = 0;
        try {
            novaStr = Float.parseFloat(str);
            formatado = String.format("%.2f", novaStr);
            return Float.parseFloat((formatado).replace(",", "."));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}

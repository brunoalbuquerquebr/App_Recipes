package br.com.unibratec.app_recipes;

/**
 * Created by brunoalbuquerque on 30/09/16.
 */

public class RecipeEvent {
    // Eventos disparados/recebidos pelos Broadcasts da aplicação
    public static final String RECIPE_LOADED = "loaded";
    public static final String UPDATE_FAVORITE = "favorite";
    public static final String RECIPE_FAVORITE_UPDATED = "updated";

    // Chave para obter o Recipe a partir das intents de broadcast
    public static final String EXTRA_RECIPE = "recipe";
}

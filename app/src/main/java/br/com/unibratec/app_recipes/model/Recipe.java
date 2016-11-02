package br.com.unibratec.app_recipes.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by brunoalbuquerque on 30/09/16.
 */

//TODO Utilizar Parcelable ou o Parceler
public class Recipe implements Serializable {
    private long id;
    @SerializedName("recipe_id")
    private String recipe_id;
    @SerializedName("title")
    private String   title;
    @SerializedName("publisher")
    private String   publisher;
    @SerializedName("image_url")
    private String   image_url;
    @SerializedName("ingredients")
    private String[] ingredients;
    @SerializedName("social_rank")
    private float    social_rank;
    @SerializedName("source_url")
    private String   source_url;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRecipe_id() {
        return recipe_id;
    }

    public void setRecipe_id(String recipe_id) {
        this.recipe_id = recipe_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String[] getIngredients() {
        return ingredients;
    }

    public void setIngredients(String[] ingredients) {
        this.ingredients = ingredients;
    }

    public float getSocial_rank() {
        return social_rank;
    }

    public void setSocial_rank(float social_rank) {
        this.social_rank = social_rank;
    }

    public String getSource_url() { return source_url;
    }

    public void setSource_url(String source_url) { this.source_url = source_url;
    }
}

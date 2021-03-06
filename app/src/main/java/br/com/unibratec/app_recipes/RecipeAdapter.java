package br.com.unibratec.app_recipes;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import br.com.unibratec.app_recipes.model.Recipe;

/**
 * Created by brunoalbuquerque on 30/09/16.
 */

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.VH> {

    private List<Recipe> mRecipes;
    private Context mContext;
    private OnRecipeClickListener mRecipeClickListener;

    public RecipeAdapter(Context ctx, List<Recipe> mRecipes) {
        this.mContext = ctx;
        this.mRecipes = mRecipes;
    }

    public void setRecipeClickListener(OnRecipeClickListener mcl) {
        this.mRecipeClickListener = mcl;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.item_recipe, parent, false);
        final VH viewHolder = new VH(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = viewHolder.getAdapterPosition();
                if (mRecipeClickListener != null){
                    mRecipeClickListener.onRecipeClick(view, mRecipes.get(pos), pos);
                }
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        Recipe recipe = mRecipes.get(position);
        Glide.with(mContext)
                .load(recipe.getImage_url())
                .placeholder(R.drawable.ic_placeholder)
                .into(holder.imageViewImage_url);
        holder.textViewTitle.setText(recipe.getTitle());
//        holder.textViewPublisher.setText(recipe.getPublisher());
    }

    @Override
    public int getItemCount() {
        return mRecipes.size();
    }

    class VH extends RecyclerView.ViewHolder {
        ImageView imageViewImage_url;
        TextView textViewTitle;
        TextView textViewPublisher;

        public VH(View itemView) {
            super(itemView);
            imageViewImage_url = (ImageView)itemView.findViewById(R.id.recipe_item_image_url);
            textViewTitle = (TextView) itemView.findViewById(R.id.recipe_item_text_title);
            //textViewPublisher = (TextView)itemView.findViewById(R.id.recipe_item_text_publisher);

            ViewCompat.setTransitionName(imageViewImage_url, "capa");
            ViewCompat.setTransitionName(textViewTitle, "titulo");
            //ViewCompat.setTransitionName(textViewPublisher, "ano");
        }
    }
}

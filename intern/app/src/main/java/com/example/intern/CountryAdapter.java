package com.example.intern;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.CountryViewHolder> {
    private Context mctx;
    private List<Country> cnList;

    public CountryAdapter(Context mctx, List<Country> cnList) {
        this.mctx = mctx;
        this.cnList = cnList;
    }

    @NonNull
    @Override
    public CountryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(mctx);
        View view = inflater.inflate(R.layout.card,null);
        return new CountryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CountryViewHolder holder, int position) {
        Country cn=cnList.get(position);
        holder.name.setText(cn.getName());
        holder.cap_population.setText(cn.getCapital()+", "+cn.getPopulation());
        holder.region.setText(cn.getRegion()+", "+cn.getSubregion());
        holder.languages.setText(cn.getLanguages());
        holder.borders.setText(cn.getBorders());
        Utils.fetchSvg(mctx,cn.getFlag(),holder.img);
//        Picasso.with(mctx).load("https://restcountries.eu/data/brn.svg").into(holder.img);
        Log.d("abcdflags",cn.getFlag());
    }

    @Override
    public int getItemCount() {
        return cnList.size();
    }

    public class CountryViewHolder extends RecyclerView.ViewHolder {
        TextView name,region,languages,borders,cap_population;
        ImageView img;
        public CountryViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.textViewTitle);
            cap_population=itemView.findViewById(R.id.textViewShortDesc);
            region=itemView.findViewById(R.id.textViewRegion);
            languages=itemView.findViewById(R.id.textViewlanguages);
            borders=itemView.findViewById(R.id.textViewBorder);
            img=itemView.findViewById(R.id.imageView);
        }
    }
}

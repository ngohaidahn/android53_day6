package com.example.btvnday06;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private ArrayList<ProductModel> mlistProduct;
    private Context mcontext;

    public ProductAdapter(ArrayList<ProductModel> mlistProduct, Context mcontext) {
        this.mlistProduct = mlistProduct;
        this.mcontext = mcontext;
    }

    @NonNull
    @Override
    public ProductAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_items,parent,false);
        this.mcontext = parent.getContext();

        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ProductViewHolder holder, int position) {
        ProductModel productModel = mlistProduct.get(position);
        holder.tvGia.setText("$"+productModel.getPrice().toString());
        holder.tvTen.setText(productModel.getTitle().toString());
        holder.tvRating.setText(productModel.getRating().toString());
        Glide.with(mcontext).load((productModel.getFirstImg())).into(holder.imgAnhSanPham);
    }

    @Override
    public int getItemCount() {
        return mlistProduct != null ? mlistProduct.size() : 0;
    }

    public void updateData(ArrayList<ProductModel> data) {
        this.mlistProduct = data;
        notifyDataSetChanged();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAnhSanPham;
        TextView tvTen, tvGia, tvRating;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAnhSanPham = itemView.findViewById(R.id.imgAnh);
            tvTen = itemView.findViewById(R.id.tvTenSanPham);
            tvGia = itemView.findViewById(R.id.tvGiaSanPham);
            tvRating = itemView.findViewById(R.id.tvRatingSanPham);
        }
    }
}

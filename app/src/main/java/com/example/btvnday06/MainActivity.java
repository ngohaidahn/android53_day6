package com.example.btvnday06;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DBHelper mDbHelper;
    ImageButton btnSearch,btnmenu;
    EditText editSearch;
    RecyclerView recyclerView;
    ArrayList<ProductModel> list;
    ArrayList<ProductModel> list1;

    ProductAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initData();
        initView();
    }

    private void initView() {
        recyclerView = findViewById(R.id.rvListItems);
        adapter = new ProductAdapter(list1,this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
        btnSearch = findViewById(R.id.btnSearch);
        editSearch = findViewById(R.id.edSearch);
        btnmenu = findViewById(R.id.btnmenu);
        btnmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list = mDbHelper.getProducts();
                updateData(list);
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String yeucau = editSearch.getText().toString().trim();
                if(yeucau.length()==0){
                    Toast.makeText(MainActivity.this, "Nhập sản phẩm cần tìm", Toast.LENGTH_SHORT).show();
                }
                else {
                    list = mDbHelper.getProductByBrand(yeucau);
                    updateData(list);
                }
            }
        });
    }

    private void initData() {
        mDbHelper = new DBHelper(this);
        list1 = new ArrayList<>();
//        String url = "https://dummyjson.com/products";
        String url = "https://dummyjson.com/products?limit=0";//No limit size product load from api
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //lấy api
                    JSONObject jsonObject = new JSONObject(response);

                    // lay ra list san pham
                    JSONArray jsonArraySanPham = jsonObject.getJSONArray("products");

                    for (int i = 0;i< jsonArraySanPham.length();i++) {
                        JSONObject jsonObjectSanPham = jsonArraySanPham.getJSONObject(i);
                        ProductModel productModel = new ProductModel();
                        productModel.setId(jsonObjectSanPham.getInt("id"));
                        productModel.setTitle(jsonObjectSanPham.getString("title"));
                        productModel.setDescription(jsonObjectSanPham.getString("description"));
                        productModel.setPrice(Integer.valueOf(jsonObjectSanPham.getString("price")));
                        productModel.setDiscountPercentage(Double.valueOf(jsonObjectSanPham.getString("discountPercentage")));
                        productModel.setRating(Double.valueOf(jsonObjectSanPham.getString("rating")));
                        productModel.setStock(Integer.valueOf(jsonObjectSanPham.getString("stock")));
                        productModel.setBrand(jsonObjectSanPham.getString("brand"));
                        productModel.setCategory(jsonObjectSanPham.getString("category"));
                        productModel.setThumbnail(jsonObjectSanPham.getString("thumbnail"));

                        List<String> images = new ArrayList<>();
                        JSONArray jsonArrayImg = jsonObjectSanPham.getJSONArray("images");
                        for (int j = 0; j < jsonArrayImg.length(); j++) {
                            String temp = jsonArrayImg.getString(j);
                            images.add(temp);
                        }
                        productModel.setImages(images);
                        mDbHelper.addProduct(productModel);
                        list1.add(productModel);//<= tới đấy mới chỉ update data trong list thôi chưa update trong adapter

                    }

                    updateData(list1);//cập nhật list data ms cho adapter
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        requestQueue.add(stringRequest);
    }

    private void updateData(ArrayList<ProductModel> data) {
        if (adapter != null && data != null && data.size() > 0) {
            adapter.updateData(data);
        }
    }
}
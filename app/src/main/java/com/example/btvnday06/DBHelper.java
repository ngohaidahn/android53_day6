package com.example.btvnday06;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "product1.db";
    public static final int VERSION = 1;
    public static final String TABLE_NAME = "product";
    public static final String PRODUCT_ID = "product_id";
    public static final String PRODUCT_TITLE = "title";
    public static final String PRODUCT_DES = "description";
    public static final String PRODUCT_RATING = "rating";
    public static final String PRODUCT_DISCOUNT = "discountPercentage";
    public static final String PRODUCT_PRICE = "price";
    public static final String PRODUCT_STOCK = "stock";
    public static final String PRODUCT_BRAND = "brand";
    public static final String PRODUCT_CATEGORY = "category";
    public static final String PRODUCT_THUMBNAIL = "thumbnail";
    public static final String PRODUCT_IMAGES = "images";


    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Bỏ  AUTOINCREMENT do api trả về đã có trường id nên ta sẽ sử dụng trường id của api luôn
        String sql = "CREATE TABLE " + TABLE_NAME + "("
                + PRODUCT_ID + " INTEGER NOT NULL PRIMARY KEY,"
                + PRODUCT_TITLE + " TEXT NOT NULL,"
                + PRODUCT_DES + " TEXT NOT NULL,"
                + PRODUCT_PRICE + " TEXT NOT NULL,"
                + PRODUCT_DISCOUNT + " TEXT NOT NULL,"
                + PRODUCT_RATING + " TEXT NOT NULL,"
                + PRODUCT_STOCK + " TEXT NOT NULL,"
                + PRODUCT_BRAND + " TEXT NOT NULL,"
                + PRODUCT_CATEGORY + " TEXT NOT NULL,"
                + PRODUCT_THUMBNAIL + " TEXT NOT NULL,"
                + PRODUCT_IMAGES + " TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addProduct(ProductModel product) {
        if (isExisted(product)) {
            updateProduct(product.getId(), product);
            return true;
        }
        Log.d("TAG", "addProduct: "+product.getId());
        if (product != null) {
            //mở vào chế độ viết lên cơ sở dữ liệu
            SQLiteDatabase db = this.getWritableDatabase();
            //tạo biến lưu trữ sản phẩm
            ContentValues contentValues = new ContentValues();
            //lưu thông tin vào biến theo cấu trúc dữ liệu map
            contentValues.put(PRODUCT_TITLE, product.getTitle());
            contentValues.put(PRODUCT_DES, product.getDescription());
            contentValues.put(PRODUCT_PRICE, product.getPrice());
            contentValues.put(PRODUCT_DISCOUNT, product.getDiscountPercentage());
            contentValues.put(PRODUCT_RATING, product.getRating());
            contentValues.put(PRODUCT_STOCK, product.getStock());
            contentValues.put(PRODUCT_BRAND, product.getBrand());
            contentValues.put(PRODUCT_CATEGORY, product.getCategory());
            contentValues.put(PRODUCT_THUMBNAIL, product.getThumbnail());

            //lưu thông tin theo liểu ArayList vào biến bằng cách biến nó thành json
            Gson gson = new Gson();
            Type typeToken = new TypeToken<List<String>>() {
            }.getType();
            String data = gson.toJson(product.getImages(), typeToken);
            contentValues.put(PRODUCT_IMAGES, data);
            long response = db.insert(TABLE_NAME, null, contentValues);
            db.close();
            if (response > -1) {
                return false;
            }
            return true;
        }
        return true;
    }

    /*
    * Check product is existed in database
    * */
    private boolean isExisted(ProductModel productModel) {
        if (getProducts().stream().filter(data -> data.getId() == productModel.getId()).count() > 0) {
            Log.d("TAG", "isExisted:");
            return true;
        }
        return false;
    }

    public boolean updateProduct(int productID, ProductModel product) {
        Log.d("TAG", "updateProduct: "+productID);
        if (product != null && productID >= 0) {
            SQLiteDatabase db = this.getWritableDatabase();
            String whereClause = PRODUCT_ID + " =?";
            String[] whereArgs = {productID + ""};
            ContentValues contentValues = new ContentValues();
            contentValues.put(PRODUCT_TITLE, product.getTitle());
            contentValues.put(PRODUCT_DES, product.getDescription());
            contentValues.put(PRODUCT_PRICE, product.getPrice());
            contentValues.put(PRODUCT_DISCOUNT, product.getDiscountPercentage());
            contentValues.put(PRODUCT_RATING, product.getRating());
            contentValues.put(PRODUCT_STOCK, product.getStock());
            contentValues.put(PRODUCT_BRAND, product.getBrand());
            contentValues.put(PRODUCT_CATEGORY, product.getCategory());
            contentValues.put(PRODUCT_THUMBNAIL, product.getThumbnail());

            Gson gson = new Gson();
            Type typeToken = new TypeToken<List<String>>() {
            }.getType();
            String data = gson.toJson(product.getImages(), typeToken);
            contentValues.put(PRODUCT_IMAGES, data);
            db.update(TABLE_NAME, contentValues, whereClause, whereArgs);
            db.close();
        }
        return false;
    }
    public boolean deleteProduct(int productId){
        if(productId >=0){
            SQLiteDatabase db = this.getWritableDatabase();
            String whereClause = PRODUCT_ID+ " =?";
            String[] whereArgs = {productId+""};

            db.delete(TABLE_NAME,whereClause,whereArgs);
            db.close();
            return true;
        }
        return false;
    }
    @SuppressLint("Range")
    // lấy ra list danh sách sản phẩm
    public ArrayList<ProductModel> getProducts(){

        ArrayList<ProductModel> listProduct = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(sql,null);
        if(cursor!=null){
            while (cursor.moveToNext()){
                ProductModel productModel = new ProductModel();

                //lấy thông tin
                productModel.setId(cursor.getInt(cursor.getColumnIndex(PRODUCT_ID)));
                productModel.setTitle(cursor.getString(cursor.getColumnIndex(PRODUCT_TITLE)));
                productModel.setDescription(cursor.getString(cursor.getColumnIndex(PRODUCT_DES)));

                int price = Integer.parseInt(cursor.getString(cursor.getColumnIndex(PRODUCT_PRICE)));
                productModel.setPrice(price);
                double discount = Double.parseDouble(cursor.getString(cursor.getColumnIndex(PRODUCT_DISCOUNT)));
                productModel.setDiscountPercentage(discount);
                int Stock = Integer.parseInt(cursor.getString(cursor.getColumnIndex(PRODUCT_STOCK)));
                productModel.setStock(Stock);
                double rating = Double.parseDouble(cursor.getString(cursor.getColumnIndex(PRODUCT_RATING)));
                productModel.setRating(rating);

                productModel.setBrand(cursor.getString(cursor.getColumnIndex(PRODUCT_BRAND)));
                productModel.setCategory(cursor.getString(cursor.getColumnIndex(PRODUCT_CATEGORY)));
                productModel.setThumbnail(cursor.getString(cursor.getColumnIndex(PRODUCT_THUMBNAIL)));

                Gson gson = new Gson();
                Type typeToken = new TypeToken<List<String>>(){}.getType();
                List<String> data = gson.fromJson(cursor.getString(cursor.getColumnIndex(PRODUCT_IMAGES)),typeToken);
                productModel.setImages(data);

                listProduct.add(productModel);
            }
        }
        return listProduct;
    }


    @SuppressLint("Range")
    public ArrayList<ProductModel> getProductByBrand(String yeucau){

        ArrayList<ProductModel> listProduct = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME;
        String dieukien = "WHERE "+PRODUCT_TITLE+" like \'%"+yeucau+"%\'";
        Cursor cursor = db.rawQuery(sql+"\n"+dieukien,null);
        if(cursor!=null){
            while (cursor.moveToNext()){
                ProductModel productModel = new ProductModel();

                //lấy thông tin
                productModel.setId(cursor.getInt(cursor.getColumnIndex(PRODUCT_ID)));
                productModel.setTitle(cursor.getString(cursor.getColumnIndex(PRODUCT_TITLE)));
                productModel.setDescription(cursor.getString(cursor.getColumnIndex(PRODUCT_DES)));

                int price = Integer.parseInt(cursor.getString(cursor.getColumnIndex(PRODUCT_PRICE)));
                productModel.setPrice(price);
                double discount = Double.parseDouble(cursor.getString(cursor.getColumnIndex(PRODUCT_DISCOUNT)));
                productModel.setDiscountPercentage(discount);
                int Stock = Integer.parseInt(cursor.getString(cursor.getColumnIndex(PRODUCT_STOCK)));
                productModel.setStock(Stock);
                double rating = Double.parseDouble(cursor.getString(cursor.getColumnIndex(PRODUCT_RATING)));
                productModel.setRating(rating);

                productModel.setBrand(cursor.getString(cursor.getColumnIndex(PRODUCT_BRAND)));
                productModel.setCategory(cursor.getString(cursor.getColumnIndex(PRODUCT_CATEGORY)));
                productModel.setThumbnail(cursor.getString(cursor.getColumnIndex(PRODUCT_THUMBNAIL)));

                Gson gson = new Gson();
                Type typeToken = new TypeToken<List<String>>(){}.getType();
                List<String> data = gson.fromJson(cursor.getString(cursor.getColumnIndex(PRODUCT_IMAGES)),typeToken);
                productModel.setImages(data);

                listProduct.add(productModel);
            }
        }
        return listProduct;
    }



}

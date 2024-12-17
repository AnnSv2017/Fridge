package com.example.fridge;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "helper.db";
    private static final int DATABASE_VERSION = 1;

    // SQL-запросы для создания таблиц

    private static final String TYPE_TABLE =
            "CREATE TABLE type(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT" +
                    ");";

    private static final String PRODUCT_TYPE_TABLE =
            "CREATE TABLE product_type (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT, " +
                    "type_id INTEGER, " +
                    "mass_value DOUBLE, " +
                    "mass_unit TEXT, " +
                    "proteins DOUBLE, " +
                    "fats DOUBLE, " +
                    "carbohydrates DOUBLE, " +
                    "calories_kcal INTEGER, " +
                    "calories_KJ INTEGER, " +
                    "measurement_type TEXT, " +
                    "FOREIGN KEY(type_id) REFERENCES type(id)" +
                    ");";

    private static final String PRODUCT_TABLE =
            "CREATE TABLE product (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "product_type_id Integer, " +
                    "manufacture_date TEXT, " + // Формат ISO 8601
                    "expiry_date TEXT, " +
                    "FOREIGN KEY(type_product_id) REFERENCES product_type(id)" +
                    ");";

    private static final String ALLERGENS_TABLE =
            "CREATE TABLE allergens(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT" +
                    ");";

    private static final String PRODUCT_ALLERGENS_TABLE =
            "CREATE TABLE product_allergens(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "product_type_id INTEGER, " +
                    "allergen_id INTEGER, " +
                    "FOREIGN KEY(product_id) REFERENCES product_type(id), " +
                    "FOREIGN KEY(allergen_id) REFERENCES allergen(id)" +
                    ");";

    private static final String PRODUCT_HISTORY_TABLE =
            "CREATE TABLE product_history(" +
                    "id INTEGER, " +
                    "product_id INTEGER, " +
                    "operation_type TEXT, " +
                    "quantity DOUBLE, " +
                    "FOREIGN KEY(product_id) REFERENCES product(id)" +
                    ");";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Выполнение SQL-запроса для создания таблицы
        db.execSQL(TYPE_TABLE);
        db.execSQL(PRODUCT_TYPE_TABLE);
        db.execSQL(PRODUCT_TABLE);
        db.execSQL(ALLERGENS_TABLE);
        db.execSQL(PRODUCT_ALLERGENS_TABLE);
        db.execSQL(PRODUCT_HISTORY_TABLE);
    }

    // Добавления новых объектов в таблицы

    public void addType(DataType data){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("name", data.getName());

        db.insert(TYPE_TABLE, null, cv);
        db.close();
    }

    public void addProductType(DataProductType data){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("name", data.getName());
        cv.put("type_id", data.getType_id());
        cv.put("mass_value", data.getMass_value());
        cv.put("mass_unit", data.getMass_unit());
        cv.put("proteins", data.getProteins());
        cv.put("fats", data.getFats());
        cv.put("carbohydrates", data.getCarbohydrates());
        cv.put("calories_kcal", data.getCalories_kcal());
        cv.put("calories_KJ", data.getCalories_KJ());
        cv.put("measurement_type", data.getMeasurement_type());

        db.insert(PRODUCT_TYPE_TABLE, null, cv);
        db.close();
    }

    public void addProduct(DataProduct data){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("product_type_id", data.getProduct_type_id());
        cv.put("manufacture_date", data.getManufacture_date());
        cv.put("expiry_date", data.getExpiry_date());

        db.insert(PRODUCT_TABLE, null, cv);
        db.close();
    }

    public void addAllergen(DataAllergen data){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("name", data.getName());

        db.insert(ALLERGENS_TABLE, null, cv);
        db.close();
    }

    public void addProductAllergens(DataProductAllergens data){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("product_type_id", data.getProduct_type_id());
        cv.put("allergen_id", data.getAllergen_id());

        db.insert(PRODUCT_ALLERGENS_TABLE, null, cv);
        db.close();
    }

    public void addProductHistory(DataProductHistory data){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("product_id", data.getProduct_id());
        cv.put("operation_type", data.getOperation_type());
        cv.put("quantity", data.getQuantity());

        db.insert(PRODUCT_HISTORY_TABLE, null, cv);
        db.close();
    }

    // Получение списка различных объектов

    public ArrayList<DataType> getAllTypes(){
        ArrayList<DataType> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase(); //getWritableDatabase() если не заработает

        Cursor cursor = db.query(TYPE_TABLE, null, null, null, null, null, "name ASC");
        if(cursor.moveToFirst()){
            int idIndex = cursor.getColumnIndex("id");
            int nameIndex = cursor.getColumnIndex("name");
            do{
                int id = cursor.getInt(idIndex);
                String name = cursor.getString(nameIndex);
                DataType data = new DataType(id, name);
                list.add(data);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // Получение списка имён некоторых объектов

    public ArrayList<String> getAllTypeNames(){
        ArrayList<String> list = new ArrayList<>();
        ArrayList<DataType> data = getAllTypes();
        for(DataType d : data){
            list.add(d.getName());
        }
        return list;
    }

    // Обновление БД
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Удаление старых таблиц
        db.execSQL("DROP TABLE IF EXISTS type");
        db.execSQL("DROP TABLE IF EXISTS product_type");
        db.execSQL("DROP TABLE IF EXISTS product");
        db.execSQL("DROP TABLE IF EXISTS allergen");
        db.execSQL("DROP TABLE IF EXISTS product_allergens");
        db.execSQL("DROP TABLE IF EXISTS product_history");
        onCreate(db); // Создание новой БД
    }
}


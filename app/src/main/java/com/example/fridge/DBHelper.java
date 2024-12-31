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
    private static final int DATABASE_VERSION = 2;

    // SQL-запросы для создания таблиц

    private static final String TYPE_TABLE = "type_table";
    private static final String CREATE_TYPE_TABLE =
            "CREATE TABLE " + TYPE_TABLE + "(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT" +
                    ");";

    private static final String FIRM_TABLE = "firm_table";
    private static final String CREATE_FIRM_TABLE =
            "CREATE TABLE " + FIRM_TABLE + "(" +
                    "id INTEGER PRIMARY KEY, " +
                    "name TEXT" +
                    ");";

    private static final String PRODUCT_TABLE = "product_table";
    private static final String CREATE_PRODUCT_TABLE =
            "CREATE TABLE " + PRODUCT_TABLE + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "type TEXT, " +
                    "name TEXT, " +
                    "firm TEXT, " +
                    "mass_value DOUBLE, " +
                    "mass_unit TEXT, " +
                    "proteins DOUBLE, " +
                    "fats DOUBLE, " +
                    "carbohydrates DOUBLE, " +
                    "calories_kcal INTEGER, " +
                    "calories_KJ INTEGER, " +
                    "measurement_type TEXT" +
                    ");";

    private static final String ALLERGENS_TABLE = "allergens_table";
    private static final String CREATE_ALLERGENS_TABLE =
            "CREATE TABLE " + ALLERGENS_TABLE + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT" +
                    ");";

    private static final String PRODUCT_ALLERGENS_TABLE = "product_allergens_table";
    private static final String CREATE_PRODUCT_ALLERGENS_TABLE =
            "CREATE TABLE " + PRODUCT_ALLERGENS_TABLE + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "product_id INTEGER, " +
                    "allergen TEXT, " +
                    "FOREIGN KEY(product_id) REFERENCES product(id)" +
                    ");";

    private static final String PRODUCT_LOGS_TABLE = "product_logs_table";
    private static final String CREATE_PRODUCT_LOGS_TABLE =
            "CREATE TABLE " + PRODUCT_LOGS_TABLE + " (" +
                    "id INTEGER, " +
                    "manufacture_date TEXT, " + // Формат ISO 8601
                    "expiry_date TEXT, " +
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
        db.execSQL(CREATE_TYPE_TABLE);
        db.execSQL(CREATE_FIRM_TABLE);
        db.execSQL(CREATE_PRODUCT_TABLE);
        db.execSQL(CREATE_ALLERGENS_TABLE);
        db.execSQL(CREATE_PRODUCT_ALLERGENS_TABLE);
        db.execSQL(CREATE_PRODUCT_LOGS_TABLE);
    }

    // Добавления новых объектов в таблицы

    public void addType(DataType data){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("name", data.getName());

        db.insert(TYPE_TABLE, null, cv);
        db.close();
    }

    public void addProduct(DataProduct data){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("type", data.getType());
        cv.put("name", data.getName());
        cv.put("firm", data.getFirm());
        cv.put("mass_value", data.getMass_value());
        cv.put("mass_unit", data.getMass_unit());
        cv.put("proteins", data.getProteins());
        cv.put("fats", data.getFats());
        cv.put("carbohydrates", data.getCarbohydrates());
        cv.put("calories_kcal", data.getCalories_kcal());
        cv.put("calories_KJ", data.getCalories_KJ());
        cv.put("measurement_type", data.getMeasurement_type());

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
        cv.put("product_id", data.getProduct_id());
        cv.put("allergen", data.getAllergen());

        db.insert(PRODUCT_ALLERGENS_TABLE, null, cv);
        db.close();
    }

    public void addProductLogs(DataProductLogs data){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("manufacture_date", data.getManufacture_date());
        cv.put("expiry_date", data.getExpiry_date());
        cv.put("product_id", data.getProduct_id());
        cv.put("operation_type", data.getOperation_type());
        cv.put("quantity", data.getQuantity());

        db.insert(PRODUCT_LOGS_TABLE, null, cv);
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

    public ArrayList<DataAllergen> getAllAllergens(){
        ArrayList<DataAllergen> list = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase(); //getWritableDatabase() если не заработает

        Cursor cursor = db.query(ALLERGENS_TABLE, null, null, null, null, null, "name ASC");
        if(cursor.moveToFirst()){
            int idIndex = cursor.getColumnIndex("id");
            int nameIndex = cursor.getColumnIndex("name");
            do{
                int id = cursor.getInt(idIndex);
                String name = cursor.getString(nameIndex);
                DataAllergen data = new DataAllergen(id, name);
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

    public ArrayList<String> getAllAllergensNames(){
        ArrayList<String> list = new ArrayList<>();
        ArrayList<DataAllergen> data = getAllAllergens();
        for(DataAllergen d : data){
            list.add(d.getName());
        }
        return list;
    }

    // Удаление объектов из БД
    public void deleteAllergenByName(String name){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(ALLERGENS_TABLE, "name = ?", new String[]{name});
        db.close();
    }

    // Обновление БД
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Удаление старых таблиц
        db.execSQL("DROP TABLE IF EXISTS " + TYPE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + FIRM_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PRODUCT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ALLERGENS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PRODUCT_ALLERGENS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PRODUCT_LOGS_TABLE);
        onCreate(db); // Создание новой БД
    }
}


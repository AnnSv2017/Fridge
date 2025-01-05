package com.example.fridge;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper dbInstance;
    private Context context;
    DBManager dbManager;
    private static final String DATABASE_NAME = "helper.db";
    private static final int DATABASE_VERSION = 4;

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
                    "mass_value REAL, " +
                    "mass_unit TEXT, " +
                    "proteins REAL, " +
                    "fats REAL, " +
                    "carbohydrates REAL, " +
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
                    "FOREIGN KEY(product_id) REFERENCES product_table(id)" +
                    ");";


    private static final String PRODUCTS_IN_FRIDGE_TABLE = "product_in_fridge_table";
    private static final String CREATE_PRODUCTS_IN_FRIDGE_TABLE =
            "CREATE TABLE " + PRODUCTS_IN_FRIDGE_TABLE + " (" +
                    "id INTEGER, " +
                    "manufacture_date TEXT, " +
                    "expiry_date TEXT, " +
                    "product_id INTEGER, " +
                    "quantity INTEGER, " +
                    "FOREIGN KEY(product_id) REFERENCES product_table(id)" +
                    ");";

    private static final String PRODUCT_LOGS_TABLE = "product_logs_table";
    private static final String CREATE_PRODUCT_LOGS_TABLE =
            "CREATE TABLE " + PRODUCT_LOGS_TABLE + " (" +
                    "id INTEGER, " +
                    "manufacture_date TEXT, " + // Формат ISO 8601
                    "expiry_date TEXT, " +
                    "product_id INTEGER, " +
                    "operation_type TEXT, " +
                    "quantity INTEGER, " +
                    "FOREIGN KEY(product_id) REFERENCES product_table(id)" +
                    ");";

    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context.getApplicationContext(); // Убедитесь, что это ApplicationContext
        //dbManager = DBManager.getInstance(this.context); // Нельзя! Иначе всё зациклится!
        //this.context = context;
        //dbManager = DBManager.getInstance(this.context);
    }

    public static synchronized DBHelper getInstance(Context context) {
        if (dbInstance == null) {
            dbInstance = new DBHelper(context.getApplicationContext());
        }
        return dbInstance;
    }

    private DBManager getDbManager() {
        if (dbManager == null) {
            dbManager = DBManager.getInstance(this.context);
        }
        return dbManager;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Выполнение SQL-запроса для создания таблицы
        db.execSQL(CREATE_TYPE_TABLE);
        db.execSQL(CREATE_FIRM_TABLE);
        db.execSQL(CREATE_PRODUCT_TABLE);
        db.execSQL(CREATE_ALLERGENS_TABLE);
        db.execSQL(CREATE_PRODUCT_ALLERGENS_TABLE);
        db.execSQL(CREATE_PRODUCTS_IN_FRIDGE_TABLE);
        db.execSQL(CREATE_PRODUCT_LOGS_TABLE);
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
        db.execSQL("DROP TABLE IF EXISTS " + PRODUCTS_IN_FRIDGE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PRODUCT_LOGS_TABLE);
        onCreate(db); // Создание новой БД
    }

    // Добавления новых объектов в таблицы

    public void addType(DataType data){
        SQLiteDatabase db = getDbManager().getDatabase();

        ContentValues cv = new ContentValues();
        cv.put("name", data.getName());

        db.insert(TYPE_TABLE, null, cv);
    }

    public void addProductIfNotExist(DataProduct data){
        SQLiteDatabase db = getDbManager().getDatabase();

        // Проверка на то существует ли уже продукт в БД
        if(productAlreadyExist(data)) {
            return;
        }

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
    }

    public void addAllergen(DataAllergen data){
        SQLiteDatabase db = getDbManager().getDatabase();

        ContentValues cv = new ContentValues();
        cv.put("name", data.getName());

        db.insert(ALLERGENS_TABLE, null, cv);
    }

    public void addProductAllergens(DataProductAllergens data){
        SQLiteDatabase db = getDbManager().getDatabase();

        ContentValues cv = new ContentValues();
        cv.put("product_id", data.getProduct_id());
        cv.put("allergen", data.getAllergen());

        db.insert(PRODUCT_ALLERGENS_TABLE, null, cv);
    }

    public void addProductInFridge(DataProductInFridge data){
        SQLiteDatabase db = getDbManager().getDatabase();

        int productInFridgeId = getProductInFridgeIdIfItInFridge(data.getManufacture_date(), data.getExpiry_date(), data.getProduct_id());

        ContentValues cv = new ContentValues();

        // Если продукта нет в холодильнике, то мы его добавляем
        if(productInFridgeId == -1){
            cv.put("manufacture_date", data.getManufacture_date());
            cv.put("expiry_date", data.getExpiry_date());
            cv.put("product_id", data.getProduct_id());
            cv.put("quantity", data.getQuantity());

            db.insert(PRODUCTS_IN_FRIDGE_TABLE, null, cv);
        }
        // Если продукт есть в холодильнике, то мы обновляем его количество
        else {
            int quantity = getProductInFridgeById(productInFridgeId).getQuantity() + data.getQuantity();
            cv.put("quantity", quantity);
            db.update(PRODUCTS_IN_FRIDGE_TABLE, cv, "id = ?", new String[]{String.valueOf(productInFridgeId)});
        }
    }

    public void addProductLogs(DataProductLogs data){
        SQLiteDatabase db = getDbManager().getDatabase();

        ContentValues cv = new ContentValues();
        cv.put("manufacture_date", data.getManufacture_date());
        cv.put("expiry_date", data.getExpiry_date());
        cv.put("product_id", data.getProduct_id());
        cv.put("operation_type", data.getOperation_type());
        cv.put("quantity", data.getQuantity());

        db.insert(PRODUCT_LOGS_TABLE, null, cv);
    }

    // Получение id объекта по имени и не только
    public int getAllergenIdByName(String name){
        SQLiteDatabase db = getDbManager().getDatabase();

        int allergenId = -1;
        Cursor cursor = db.query(ALLERGENS_TABLE, new String[]{"id"}, "name = ?", new String[]{name}, null, null, null);
        if(cursor != null && cursor.moveToFirst()){
            int allergenIdIndex = cursor.getColumnIndex("id");
            allergenId = cursor.getInt(allergenIdIndex);
        }
        cursor.close();

        return allergenId;
    }

    public int getProductIdByInfo(String type, String name, String firm, Double mass_value, String mass_unit){
        SQLiteDatabase db = getDbManager().getDatabase();

        int productId = -1;
        Cursor cursor = db.query(
                PRODUCT_TABLE,
                new String[]{"id"},
                "type = ? AND name = ? AND firm = ? AND mass_value = ? AND mass_unit = ?",
                new String[]{type, name, firm, String.valueOf(mass_value), mass_unit},
                null,
                null,
                null
        );
        if(cursor != null && cursor.moveToFirst()){
            int productIdIndex = cursor.getColumnIndex("id");
            productId = cursor.getInt(productIdIndex);
        }
        cursor.close();

        return productId;
    }

    public int getProductInFridgeIdIfItInFridge(String manufacture_date, String expiry_date, int product_id){
        SQLiteDatabase db = getDbManager().getDatabase();

        int productInFridgeId = -1;
        Cursor cursor = db.query(
                PRODUCTS_IN_FRIDGE_TABLE,
                new String[]{"id"},
                "manufacture_date = ? AND expiry_date = ? AND product_id = ?",
                new String[]{manufacture_date, expiry_date, String.valueOf(product_id)},
                null,
                null,
                null
        );
        if(cursor.moveToFirst()){
            int productInFridgeIdIndex = cursor.getColumnIndex("id");
            productInFridgeId = cursor.getInt(productInFridgeIdIndex);
        }
        cursor.close();

        return productInFridgeId;
    }
    // Получение ответа на вопрос существует ли данный объект уже или нет
    public boolean productAlreadyExist(DataProduct data) {
        SQLiteDatabase db = getDbManager().getDatabase();
        String query = "SELECT COUNT(*) FROM " + PRODUCT_TABLE +
                " WHERE type = ? AND name = ? AND firm = ? AND mass_value = ? AND mass_unit = ?";

        String[] selectionArgs = new String[]{
                data.getType(),
                data.getName(),
                data.getFirm(),
                String.valueOf(data.getMass_value()),
                data.getMass_unit()
        };

        Cursor cursor = db.rawQuery(query, selectionArgs);
        boolean exists = false;

        if (cursor.moveToFirst()) {
            exists = cursor.getInt(0) > 0;
        }
        cursor.close();
        return exists;
    }


    // Получение объекта по его id в БД
    public DataProductInFridge getProductInFridgeById(int idPK){
        SQLiteDatabase db = getDbManager().getDatabase();

        Cursor cursor = db.query(
                PRODUCTS_IN_FRIDGE_TABLE,
                null,
                "id = ?",
                new String[]{String.valueOf(idPK)},
                null,
                null,
                null
        );
        DataProductInFridge data = null;
        if(cursor != null && cursor.moveToFirst()){
            int idIndex = cursor.getColumnIndex("id");
            int manufactureDateIndex = cursor.getColumnIndex("manufacture_date");
            int expiryDateIndex = cursor.getColumnIndex("expiry_date");
            int productIdIndex = cursor.getColumnIndex("product_id");
            int quantityIndex = cursor.getColumnIndex("quantity");

            int id = cursor.getInt(idIndex);
            String manufacture_date = cursor.getString(manufactureDateIndex);
            String expiry_date = cursor.getString(expiryDateIndex);
            int product_id = cursor.getInt(productIdIndex);
            int quantity = cursor.getInt(quantityIndex);

            data = new DataProductInFridge(id, manufacture_date, expiry_date, product_id, quantity);
        }
        cursor.close();

        return data;
    }

    // Получение списка различных объектов

    public ArrayList<DataType> getAllTypes(){
        ArrayList<DataType> list = new ArrayList<>();
        SQLiteDatabase db = getDbManager().getDatabase(); //getWritableDatabase() если не заработает

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
        return list;
    }

    public ArrayList<DataAllergen> getAllAllergens(){
        ArrayList<DataAllergen> list = new ArrayList<>();
        SQLiteDatabase db = getDbManager().getDatabase();

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
        return list;
    }

    public ArrayList<DataProduct> getAllProducts(){
        ArrayList<DataProduct> list = new ArrayList<>();
        SQLiteDatabase db = getDbManager().getDatabase();

        Cursor cursor = db.query(PRODUCT_TABLE, null, null, null, null, null, "name ASC");
        if(cursor.moveToFirst()){
            int idIndex = cursor.getColumnIndex("id");
            int typeIndex = cursor.getColumnIndex("type");
            int nameIndex = cursor.getColumnIndex("name");
            int firmIndex = cursor.getColumnIndex("firm");
            int massValueIndex = cursor.getColumnIndex("mass_value");
            int massUnitIndex = cursor.getColumnIndex("mass_unit");
            int proteinsIndex = cursor.getColumnIndex("proteins");
            int fatsIndex = cursor.getColumnIndex("fats");
            int carbohydratesIndex = cursor.getColumnIndex("carbohydrates");
            int caloriesKcalIndex = cursor.getColumnIndex("calories_kcal");
            int caloriesKJIndex = cursor.getColumnIndex("calories_kj");
            int measurementTypeIndex = cursor.getColumnIndex("measurement_type");
            do{
                int id = cursor.getInt(idIndex);
                String type = cursor.getString(typeIndex);
                String name = cursor.getString(nameIndex);
                String firm = cursor.getString(firmIndex);
                Double mass_value = cursor.getDouble(massValueIndex);
                String mass_unit = cursor.getString(massUnitIndex);
                Double proteins = cursor.getDouble(proteinsIndex);
                Double fats = cursor.getDouble(fatsIndex);
                Double carbohydrates = cursor.getDouble(carbohydratesIndex);
                int calories_kcal = cursor.getInt(caloriesKcalIndex);
                int calories_kj = cursor.getInt(caloriesKJIndex);
                String measurement_type = cursor.getString(measurementTypeIndex);
                DataProduct data = new DataProduct(id, type, name, firm, mass_value, mass_unit, proteins, fats, carbohydrates, calories_kcal, calories_kj, measurement_type);
                list.add(data);
            }while (cursor.moveToNext());
        }
        cursor.close();
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

    // Изменение объектов
    public void editAllergenNameById(int allergenId, String newName){
        SQLiteDatabase db = getDbManager().getDatabase();

        ContentValues cv = new ContentValues();

        cv.put("name", newName);

        db.update(ALLERGENS_TABLE, cv, "id = ?", new String[]{String.valueOf(allergenId)});
    }

    // Удаление объектов из БД
    public void deleteAllergenByName(String name){
        SQLiteDatabase db = getDbManager().getDatabase();

        db.delete(ALLERGENS_TABLE, "name = ?", new String[]{name});
    }
}


package com.example.fridge;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper dbInstance;
    private Context context;
    DBManager dbManager;
    private static final String DATABASE_NAME = "helper.db";
    private static final int DATABASE_VERSION = 10;

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
                    "calories_kj INTEGER, " +
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

    private static final String PRODUCTS_IN_FRIDGE_TABLE = "products_in_fridge_table";
    private static final String CREATE_PRODUCTS_IN_FRIDGE_TABLE =
            "CREATE TABLE " + PRODUCTS_IN_FRIDGE_TABLE + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "manufacture_date TEXT, " +
                    "expiry_date TEXT, " +
                    "product_id INTEGER, " +
                    "quantity INTEGER, " +
                    "FOREIGN KEY(product_id) REFERENCES product_table(id)" +
                    ");";

    private static final String PRODUCT_LOGS_TABLE = "product_logs_table";
    private static final String CREATE_PRODUCT_LOGS_TABLE =
            "CREATE TABLE " + PRODUCT_LOGS_TABLE + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "date TEXT, " +
                    "manufacture_date TEXT, " + // Формат ISO 8601
                    "expiry_date TEXT, " +
                    "product_id INTEGER, " +
                    "operation_type TEXT, " +
                    "quantity INTEGER, " +
                    "FOREIGN KEY(product_id) REFERENCES product_table(id)" +
                    ");";

    private static final String PRODUCTS_IN_SHOPPING_LIST_TABLE = "products_in_shopping_list_table";
    private static final String CREATE_PRODUCTS_IN_SHOPPING_LIST_TABLE =
            "CREATE TABLE " + PRODUCTS_IN_SHOPPING_LIST_TABLE + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "product_id INTEGER, " +
                    "quantity INTEGER, " +
                    "FOREIGN KEY(product_id) REFERENCES product_table(id)" +
                    ");";


    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context.getApplicationContext(); // Убедитесь, что это ApplicationContext
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
        db.execSQL(CREATE_PRODUCTS_IN_SHOPPING_LIST_TABLE);
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
        db.execSQL("DROP TABLE IF EXISTS " + PRODUCTS_IN_SHOPPING_LIST_TABLE);
        onCreate(db); // Создание новой БД
    }

    // Курсоры
    private DataProduct getDataProductFromCursor(Cursor cursor) {
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
        return new DataProduct(id, type, name, firm, mass_value, mass_unit, proteins, fats, carbohydrates, calories_kcal, calories_kj, measurement_type);
    }

    private DataProductInFridge getDataProductInFridgeFromCursor(Cursor cursor){
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

        return new DataProductInFridge(id, manufacture_date, expiry_date, product_id, quantity);
    }

    private DataProductInShoppingList getDataProductInShoppingListFromCursor(Cursor cursor){
        int idIndex = cursor.getColumnIndex("id");
        int productIdIndex = cursor.getColumnIndex("product_id");
        int quantityIndex = cursor.getColumnIndex("quantity");

        int id = cursor.getInt(idIndex);
        int product_id = cursor.getInt(productIdIndex);
        int quantity = cursor.getInt(quantityIndex);

        return new DataProductInShoppingList(id, product_id, quantity);
    }

    private DataProductLogs getDataProductLogsFromCursor(Cursor cursor){
        int idIndex = cursor.getColumnIndex("id");
        int dateIndex = cursor.getColumnIndex("date");
        int manufactureDateIndex = cursor.getColumnIndex("manufacture_date");
        int expiryDateIndex = cursor.getColumnIndex("expiry_date");
        int productIdIndex = cursor.getColumnIndex("product_id");
        int operationTypeIndex = cursor.getColumnIndex("operation_type");
        int quantityIndex = cursor.getColumnIndex("quantity");

        int id = cursor.getInt(idIndex);
        String date = cursor.getString(dateIndex);
        String manufacture_date = cursor.getString(manufactureDateIndex);
        String expiry_date = cursor.getString(expiryDateIndex);
        int product_id = cursor.getInt(productIdIndex);
        String operation_type = cursor.getString(operationTypeIndex);
        int quantity = cursor.getInt(quantityIndex);

        return new DataProductLogs(id, date, manufacture_date, expiry_date, product_id, operation_type, quantity);
    }

    // Добавления новых объектов в таблицы

    public void addType(DataType data){
        SQLiteDatabase db = getDbManager().getDatabase();

        if(typeAlreadyExist(data)){
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put("name", data.getName());

        db.insert(TYPE_TABLE, null, cv);
    }

    public void addFirm(DataFirm data){
        SQLiteDatabase db = getDbManager().getDatabase();

        if(firmAlreadyExist(data)){
            return;
        }

        ContentValues cv = new ContentValues();
        cv.put("name", data.getName());
        db.insert(FIRM_TABLE, null, cv);
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
            DataProductInFridge existingProduct = getProductInFridgeById(productInFridgeId);
            if (existingProduct != null) {
                int quantity = existingProduct.getQuantity() + data.getQuantity();
                cv.put("quantity", quantity);
                db.update(PRODUCTS_IN_FRIDGE_TABLE, cv, "id = ?", new String[]{String.valueOf(productInFridgeId)});
            } else {
                throw new IllegalStateException("Product with ID " + productInFridgeId + " not found in the database");
            }
        }
    }

    public void addProductInShoppingList(DataProductInShoppingList data){
        SQLiteDatabase db = getDbManager().getDatabase();

        int productInShoppingListId = getProductInShoppingListId(data.getProduct_id());

        ContentValues cv = new ContentValues();

        // Если продукта нет в холодильнике, то мы его добавляем
        if(productInShoppingListId == -1){
            cv.put("product_id", data.getProduct_id());
            cv.put("quantity", data.getQuantity());

            db.insert(PRODUCTS_IN_SHOPPING_LIST_TABLE, null, cv);
        }
        // Если продукт есть в списке покупок, то мы обновляем его количество
        else {
            DataProductInShoppingList existingProduct = getProductInShoppingListById(productInShoppingListId);
            if (existingProduct != null) {
                int quantity = existingProduct.getQuantity() + data.getQuantity();
                cv.put("quantity", quantity);
                db.update(PRODUCTS_IN_SHOPPING_LIST_TABLE, cv, "id = ?", new String[]{String.valueOf(productInShoppingListId)});
            } else {
                throw new IllegalStateException("Product with ID " + productInShoppingListId + " not found in the database");
            }
        }
    }

    public void addProductLogs(DataProductLogs data){
        SQLiteDatabase db = getDbManager().getDatabase();

        int productLogsId = getProductLogsIdByData(data);

        ContentValues cv = new ContentValues();

        // Если продукта нет в логах, добавляем его в логи
        if(productLogsId == -1){
            cv.put("date", data.getDate());
            cv.put("manufacture_date", data.getManufacture_date());
            cv.put("expiry_date", data.getExpiry_date());
            cv.put("product_id", data.getProduct_id());
            cv.put("operation_type", data.getOperation_type());
            cv.put("quantity", data.getQuantity());

            db.insert(PRODUCT_LOGS_TABLE, null, cv);
        }
        // Если продукт есть в логах, то мы меняем количество
        else{
            DataProductLogs existingProduct = getProductLogsById(productLogsId);
            if (existingProduct != null) {
                int quantity = existingProduct.getQuantity() + data.getQuantity();
                cv.put("quantity", quantity);
                db.update(PRODUCT_LOGS_TABLE, cv, "id = ?", new String[]{String.valueOf(productLogsId)});
            } else {
                throw new IllegalStateException("Product with ID " + productLogsId + " not found in the database");
            }
        }
    }

    // Получение id объекта по имени и не только
    public int getTypeIdByName(String name){
        SQLiteDatabase db = getDbManager().getDatabase();

        int typeId = -1;
        Cursor cursor = db.query(TYPE_TABLE, new String[]{"id"}, "name = ?", new String[]{name}, null, null, null);
        if(cursor != null && cursor.moveToFirst()){
            int typeIdIndex = cursor.getColumnIndex("id");
            typeId = cursor.getInt(typeIdIndex);
        }
        cursor.close();

        return typeId;
    }

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

    public int getProductIdByFullName(String type, String name, String firm, Double mass_value, String mass_unit){
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

    public int getProductIdByFullName(String fullName){
        String[] parts = fullName.split(", ");
        String type = parts[0];
        String name = parts[1];
        String firm = parts[2];
        String mass_value = parts[3].split(" ")[0];
        String mass_unit = parts[3].split(" ")[1];

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

    public int getProductLogsIdByData(DataProductLogs data){
        SQLiteDatabase db = getDbManager().getDatabase();

        int productLogsId = -1;
        Cursor cursor = db.query(
                PRODUCT_LOGS_TABLE,
                new String[]{"id"},
                "date = ? AND manufacture_date = ? AND expiry_date = ? AND product_id = ? AND operation_type = ?",
                new String[]{data.getDate(), data.getManufacture_date(), data.getExpiry_date(), String.valueOf(data.getProduct_id()), data.getOperation_type()},
                null,
                null,
                null
        );
        if(cursor.moveToFirst()){
            int productLogsIdIndex = cursor.getColumnIndex("id");
            productLogsId = cursor.getInt(productLogsIdIndex);
        }
        cursor.close();

        return productLogsId;
    }

    public int getProductInShoppingListId(int product_id){
        SQLiteDatabase db = getDbManager().getDatabase();

        int productInShoppingListId = -1;
        Cursor cursor = db.query(
                PRODUCTS_IN_SHOPPING_LIST_TABLE,
                new String[]{"id"},
                "product_id = ?",
                new String[]{String.valueOf(product_id)},
                null,
                null,
                null
        );
        if(cursor.moveToFirst()){
            int productInShoppingListIdIndex = cursor.getColumnIndex("id");
            productInShoppingListId = cursor.getInt(productInShoppingListIdIndex);
        }
        cursor.close();

        return productInShoppingListId;
    }

    // Получение ответа на вопрос существует ли данный объект уже или нет
    public boolean typeAlreadyExist(DataType data){
        SQLiteDatabase db = getDbManager().getDatabase();
        String query = "SELECT COUNT(*) FROM " + TYPE_TABLE + " WHERE name = ?";
        String[] selectionArgs = new String[]{data.getName()};

        Cursor cursor = db.rawQuery(query, selectionArgs);
        boolean exists = false;
        if (cursor.moveToFirst()) {
            exists = cursor.getInt(0) > 0;
        }
        cursor.close();
        return exists;
    }

    public boolean firmAlreadyExist(DataFirm data){
        SQLiteDatabase db = getDbManager().getDatabase();
        String query = "SELECT COUNT(*) FROM " + FIRM_TABLE + " WHERE name = ?";
        String[] selectionArgs = new String[]{data.getName()};

        Cursor cursor = db.rawQuery(query, selectionArgs);
        boolean exists = false;
        if (cursor.moveToFirst()) {
            exists = cursor.getInt(0) > 0;
        }
        cursor.close();
        return exists;
    }

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

    public boolean productInShoppingListIsExist(int id){
        SQLiteDatabase db = getDbManager().getDatabase();

        Cursor cursor = db.query(
                PRODUCTS_IN_SHOPPING_LIST_TABLE,
                new String[]{"id"},
                "id = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );

        boolean exists = false;
        if (cursor != null && cursor.moveToFirst()) {
            exists = true;
        }
        if(cursor != null){
            cursor.close();
        }

        return exists;
    }

    // Получение объекта по его id в БД
    public DataProduct getProductById(int idPK){
        SQLiteDatabase db = getDbManager().getDatabase();

        Cursor cursor = db.query(
                PRODUCT_TABLE,
                null,
                "id = ?",
                new String[]{String.valueOf(idPK)},
                null,
                null,
                null
        );
        DataProduct data = null;
        if(cursor != null && cursor.moveToFirst()) {
            data = getDataProductFromCursor(cursor);
        }
        if(cursor != null){
            cursor.close();
        }
        return data;
    }

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

        if (cursor == null || !cursor.moveToFirst()) {
            Log.e("DBHelper", "No product found with ID: " + idPK);
            if (cursor != null) {
                cursor.close();
            }
            return null;
        }

        DataProductInFridge data = null;
        if(cursor != null && cursor.moveToFirst()){
            data = getDataProductInFridgeFromCursor(cursor);
        }
        if(cursor != null){
            cursor.close();
        }

        return data;
    }

    public DataProductInShoppingList getProductInShoppingListById(int idPK){
        SQLiteDatabase db = getDbManager().getDatabase();

        Cursor cursor = db.query(
                PRODUCTS_IN_SHOPPING_LIST_TABLE,
                null,
                "id = ?",
                new String[]{String.valueOf(idPK)},
                null,
                null,
                null
        );

        if (cursor == null || !cursor.moveToFirst()) {
            Log.e("DBHelper", "No product found with ID: " + idPK);
            if (cursor != null) {
                cursor.close();
            }
            return null;
        }

        DataProductInShoppingList data = null;
        if(cursor != null && cursor.moveToFirst()){
            data = getDataProductInShoppingListFromCursor(cursor);
        }
        if(cursor != null){
            cursor.close();
        }

        return data;
    }

    public DataProductLogs getProductLogsById(int idPK){
        SQLiteDatabase db = getDbManager().getDatabase();

        Cursor cursor = db.query(
                PRODUCT_LOGS_TABLE,
                null,
                "id = ?",
                new String[]{String.valueOf(idPK)},
                null,
                null,
                null
        );

        if (cursor == null || !cursor.moveToFirst()) {
            Log.e("DBHelper", "No product found with ID: " + idPK);
            if (cursor != null) {
                cursor.close();
            }
            return null;
        }

        DataProductLogs data = null;
        if(cursor != null && cursor.moveToFirst()){
            data = getDataProductLogsFromCursor(cursor);
        }
        if(cursor != null){
            cursor.close();
        }

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

    public ArrayList<String> getAllAllergensByProductId(int productId){
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = getDbManager().getDatabase();

        Cursor cursor = db.query(
                PRODUCT_ALLERGENS_TABLE,
                new String[]{"allergen"},
                "product_id = ?",
                new String[]{String.valueOf(productId)},
                null,
                null,
                "allergen ASC"
        );
        if(cursor.moveToFirst()){
            do{
                int allergenIndex = cursor.getColumnIndex("allergen");
                String allergen = cursor.getString(allergenIndex);
                list.add(allergen);
            }while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public ArrayList<DataProduct> getAllProducts(){
        ArrayList<DataProduct> list = new ArrayList<>();
        SQLiteDatabase db = getDbManager().getDatabase();

        Cursor cursor = db.query(PRODUCT_TABLE, null, null, null, null, null, "name ASC");
        if(cursor.moveToFirst()){
            do{
                list.add(getDataProductFromCursor(cursor));
            }while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public ArrayList<DataProductInFridge> getAllProductsInFridge(){
        ArrayList<DataProductInFridge> list = new ArrayList<>();
        SQLiteDatabase db = getDbManager().getDatabase();

        // Запрос с JOIN для соединения двух таблиц и сортировки по имени
        String query = "SELECT pif.*, p.name AS product_name " +
                "FROM " + PRODUCTS_IN_FRIDGE_TABLE + " pif " +
                "JOIN " + PRODUCT_TABLE + " p " +
                "ON pif.product_id = p.id " +
                "ORDER BY p.name ASC";

        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do{
                list.add(getDataProductInFridgeFromCursor(cursor));
            }while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public ArrayList<DataProductInShoppingList> getAllProductsInShoppingList(){
        ArrayList<DataProductInShoppingList> list = new ArrayList<>();
        SQLiteDatabase db = getDbManager().getDatabase();

        // Запрос с JOIN для соединения двух таблиц и сортировки по имени
        String query = "SELECT pif.*, p.name AS product_name " +
                "FROM " + PRODUCTS_IN_SHOPPING_LIST_TABLE + " pif " +
                "JOIN " + PRODUCT_TABLE + " p " +
                "ON pif.product_id = p.id " +
                "ORDER BY p.name ASC";

        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do{
                list.add(getDataProductInShoppingListFromCursor(cursor));
            }while (cursor.moveToNext());
            cursor.close();
        }
        return list;
    }

    public ArrayList<DataProductInFridge> getAllProductsInFridgeByType(String type){
        ArrayList<DataProductInFridge> list = new ArrayList<>();
        SQLiteDatabase db = getDbManager().getDatabase();

        // Запрос с JOIN для соединения двух таблиц и сортировки по имени
        String query = "SELECT pif.*, p.name AS product_name " +
                "FROM " + PRODUCTS_IN_FRIDGE_TABLE + " pif " +
                "JOIN " + PRODUCT_TABLE + " p " +
                "ON pif.product_id = p.id " +
                "WHERE p.type = ?" +
                "ORDER BY p.name ASC";

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, new String[]{type});
            if (cursor.moveToFirst()) {
                do {
                    list.add(getDataProductInFridgeFromCursor(cursor));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return list;
    }

    public ArrayList<DataProductInShoppingList> getAllProductsInShoppingListByType(String type){
        ArrayList<DataProductInShoppingList> list = new ArrayList<>();
        SQLiteDatabase db = getDbManager().getDatabase();

        // Запрос с JOIN для соединения двух таблиц и сортировки по имени
        String query = "SELECT pif.*, p.name AS product_name " +
                "FROM " + PRODUCTS_IN_SHOPPING_LIST_TABLE + " pif " +
                "JOIN " + PRODUCT_TABLE + " p " +
                "ON pif.product_id = p.id " +
                "WHERE p.type = ?" +
                "ORDER BY p.name ASC";

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, new String[]{type});
            if (cursor.moveToFirst()) {
                do {
                    list.add(getDataProductInShoppingListFromCursor(cursor));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return list;
    }



    // Получение списка имён некоторых объектов

    public ArrayList<String> getAllTypesNames(){
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

    public void editProductInShoppingListQuantityById(int id, int newQuantity){
        SQLiteDatabase db = getDbManager().getDatabase();

        ContentValues cv = new ContentValues();

        cv.put("quantity", newQuantity);

        db.update(PRODUCTS_IN_SHOPPING_LIST_TABLE, cv, "id = ?", new String[]{String.valueOf(id)});
    }
    // Удаление объектов из БД
    public void deleteAllergenByName(String name){
        SQLiteDatabase db = getDbManager().getDatabase();

        db.delete(ALLERGENS_TABLE, "name = ?", new String[]{name});
    }

    public void deleteProduct(int id){
        SQLiteDatabase db = getDbManager().getDatabase();

        db.delete(PRODUCT_TABLE, "id = ?", new String[]{String.valueOf(id)});
    }

    public boolean deleteProductFromFridge(int idPK, int quantityToDelete){
        SQLiteDatabase db = getDbManager().getDatabase();

        Cursor cursor = db.query(
                PRODUCTS_IN_FRIDGE_TABLE,
                new String[]{"id", "quantity"},
                "id = ?",
                new String[]{String.valueOf(idPK)},
                null,
                null,
                null
                );
        boolean isDeleted = false;
        if(cursor != null && cursor.moveToFirst()){
            int quantityIndex = cursor.getColumnIndex("quantity");
            int quantity = cursor.getInt(quantityIndex);
            if(quantity == quantityToDelete){
                db.delete(PRODUCTS_IN_FRIDGE_TABLE, "id = ?", new String[]{String.valueOf(idPK)});
                isDeleted = true;
            }
            else if(quantity > quantityToDelete){
                ContentValues cv = new ContentValues();
                cv.put("quantity", quantity - quantityToDelete);
                db.update(PRODUCTS_IN_FRIDGE_TABLE, cv, "id = ?", new String[]{String.valueOf(idPK)});
                isDeleted = true;
            }
        }
        if(cursor != null){
            cursor.close();
        }
        return isDeleted;
    }

    public void deleteProductFromShoppingList(int id){
        SQLiteDatabase db = getDbManager().getDatabase();

        db.delete(PRODUCTS_IN_SHOPPING_LIST_TABLE, "id = ?", new String[]{String.valueOf(id)});
    }

    // Поиск объектов
    public ArrayList<String> searchProducts(String query) {
        ArrayList<String> results = new ArrayList<>();
        SQLiteDatabase db = getDbManager().getDatabase();

        String sqlQuery = "SELECT type || ', ' || name || ', ' || firm || ', ' || mass_value || ' ' || mass_unit AS full_name " +
                "FROM product_table " +
                " WHERE full_name LIKE ? " +
                " ORDER BY type ASC, name ASC, firm ASC, mass_value ASC";
        Cursor cursor = db.rawQuery(sqlQuery, new String[]{"%" + query + "%"});

        if (cursor.moveToFirst()) {
            do {
                int fullNameIndex = cursor.getColumnIndex("full_name");
                String full_name = cursor.getString(fullNameIndex);
                results.add(full_name);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return results;
    }

    // Методы для получения Категорий

    public ArrayList<Category> getAllCategoriesForFridge(){
        ArrayList<Category> categories = new ArrayList<>();
        ArrayList<String> types = getAllTypesNames();
        ArrayList<DataProductInFridge> products;
        for(String type : types){
            products = getAllProductsInFridgeByType(type);
            if(products != null && !products.isEmpty()){
                Category category = new Category(type, false, products);
                categories.add(category);
            }
        }
        return categories;
    }

    // Вызывается когда пользователь что-то выбрал в предложенном списке продуктов
    public ArrayList<Category> getCategoryForFridgeByFullName(String fullName){
        int productId = getProductIdByFullName(fullName);
        ArrayList<Integer> productsId = new ArrayList<>();
        productsId.add(productId);
        String[] parts = fullName.split(", ");
        String type = parts[0];
        ArrayList<DataProductInFridge> products = getAllProductInFridgeByTypeANDProductsId(type, productsId);
        ArrayList<Category> categories = new ArrayList<>();
        Category category = new Category(type, true, products);
        categories.add(category);
        return categories;
    }

    // Вызывается когда пользователь что-то ввёл в поисковую строку, но ещё не выбрал
    public ArrayList<Category> getAllCategoriesForFridgeByQuery(String query){
        ArrayList<Category> categories = new ArrayList<>();
        ArrayList<String> fullNames = searchProducts(query);
        ArrayList<DataProductInFridge> products;
        ArrayList<Integer> productsId = new ArrayList<>();
        for(String full_name : fullNames){
            productsId.add(getProductIdByFullName(full_name));
        }
        ArrayList<String> typesList = getAllTypesByProductsId(productsId);
        for(String type : typesList){
            products = getAllProductInFridgeByTypeANDProductsId(type, productsId);
            if(products != null && !products.isEmpty()){
                Category category = new Category(type, false, products);
                categories.add(category);
            }
        }
        return categories;
    }

    public ArrayList<String> getAllTypesByProductsId(ArrayList<Integer> productsId) {
        ArrayList<String> types = new ArrayList<>();
        SQLiteDatabase db = getDbManager().getDatabase();

        // Формируем список ID в формате строки, разделённой запятыми
        StringBuilder idListBuilder = new StringBuilder();
        for (int i = 0; i < productsId.size(); i++) {
            idListBuilder.append(productsId.get(i));
            if (i < productsId.size() - 1) {
                idListBuilder.append(","); // добавляем запятую между ID
            }
        }
        String idList = idListBuilder.toString();

        // SQL-запрос
        String query = "SELECT DISTINCT type FROM " + PRODUCT_TABLE + " WHERE id IN (" + idList + ") ORDER BY type ASC";
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                do {
                    int typeIndex = cursor.getColumnIndex("type");
                    String type = cursor.getString(typeIndex);
                    types.add(type);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return types;
    }

    public ArrayList<DataProductInFridge> getAllProductInFridgeByTypeANDProductsId(String type, ArrayList<Integer> productsId) {
        ArrayList<DataProductInFridge> dataList = new ArrayList<>();
        SQLiteDatabase db = getDbManager().getDatabase();

        if (productsId == null || productsId.isEmpty()) {
            // Если список ID пустой, сразу возвращаем пустой результат
            return dataList;
        }

        // Формируем строку с плейсхолдерами для параметризованного запроса
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < productsId.size(); i++) {
            placeholders.append("?");
            if (i < productsId.size() - 1) {
                placeholders.append(",");
            }
        }

        // SQL-запрос
        String query = "SELECT pif.*, p.* " +
                "FROM " + PRODUCTS_IN_FRIDGE_TABLE + " pif " +
                "JOIN " + PRODUCT_TABLE + " p ON pif.product_id = p.id " +
                "WHERE p.type = ? " +
                "AND pif.product_id IN (" + placeholders + ") " +
                "ORDER BY p.type ASC, p.name ASC, p.firm ASC, p.mass_value ASC";

        // Подготовка аргументов для параметризованного запроса
        String[] args = new String[productsId.size() + 1];
        args[0] = type;
        for (int i = 0; i < productsId.size(); i++) {
            args[i + 1] = String.valueOf(productsId.get(i));
        }

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, args);
            if (cursor.moveToFirst()) {
                do {
                    DataProductInFridge data = getDataProductInFridgeFromCursor(cursor);
                    dataList.add(data);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return dataList;
    }

    public ArrayList<Category> getAllCategoriesForShoppingList(){
        ArrayList<Category> categories = new ArrayList<>();
        ArrayList<String> types = getAllTypesNames();
        ArrayList<DataProductInShoppingList> products;
        for(String type : types){
            products = getAllProductsInShoppingListByType(type);
            if(products != null && !products.isEmpty()){
                Category category = new Category(type, false, products);
                categories.add(category);
            }
        }
        return categories;
    }

    // TODO: Аналитика

    public ArrayList<Category> getAllCategoriesForAnalytics(){
        ArrayList<Category> categories = new ArrayList<>();
        ArrayList<String> types = getAllTypesNames();
        ArrayList<AnalyticsProduct> products;
        for(String type : types){
            products = getAllAnalyticsProductByType(type);
            if(products != null && !products.isEmpty()){
                Category category = new Category(type, false, products);
                categories.add(category);
            }
        }
        return categories;
    }

    public ArrayList<Category> getAllCategoriesByDatesForAnalytics(String firstDate, String lastDate){
        ArrayList<Category> categories = new ArrayList<>();
        ArrayList<String> types = getAllTypesNames();
        ArrayList<AnalyticsProduct> products;
        for(String type : types){
            products = getAllAnalyticsProductByTypeANDDates(type, firstDate, lastDate);
            if(products != null && !products.isEmpty()){
                Category category = new Category(type, false, products);
                categories.add(category);
            }
        }
        return categories;
    }

    public ArrayList<Category> getCategoryForAnalyticsByFullName(String fullName){
        int productId = getProductIdByFullName(fullName);
        ArrayList<AnalyticsProduct> products = getAnalyticsProductByProductId(productId);
        ArrayList<Category> categories = new ArrayList<>();
        DataProduct dataProduct = getProductById(productId);
        Category category = new Category(dataProduct.getType(), true, products);
        categories.add(category);
        return categories;
    }

    public ArrayList<Category> getCategoryForAnalyticsByFullNameWithDates(String fullName, String firstDate, String lastDate){
        int productId = getProductIdByFullName(fullName);

        Log.e("getCategoryForAnalyticsByFullNameWithDates", "productId = " + String.valueOf(productId));

        ArrayList<AnalyticsProduct> products = getAnalyticsProductByProductIdANDDates(productId, firstDate, lastDate);
        Log.e("getCategoryForAnalyticsByFullNameWithDates", "len products = " + products.size());
        ArrayList<Category> categories = new ArrayList<>();
        DataProduct dataProduct = getProductById(productId);
        Category category = new Category(dataProduct.getType(), true, products);
        categories.add(category);
        return categories;
    }

    public ArrayList<AnalyticsProduct> getAnalyticsProductByProductId(int product_id) {
        // По факту всегда один элемент, просто передаётся в списке
        ArrayList<AnalyticsProduct> analyticsProducts = new ArrayList<>();
        SQLiteDatabase db = getDbManager().getDatabase();

        String query = "SELECT " +
                "product_id, " +
                "manufacture_date, " +
                "expiry_date, " +
                "SUM(CASE WHEN operation_type = 'add' THEN quantity ELSE 0 END) AS addLogsCount, " +
                "SUM(CASE WHEN operation_type = 'used' THEN quantity ELSE 0 END) AS usedLogsCount, " +
                "SUM(CASE WHEN operation_type = 'overdue' THEN quantity ELSE 0 END) AS overdueLogsCount " +
                "FROM " + PRODUCT_LOGS_TABLE + " " +
                "WHERE product_id = ? " +
                "GROUP BY product_id, manufacture_date, expiry_date";


        Cursor cursor = null;

        try {
            cursor = db.rawQuery(query, new String[]{String.valueOf(product_id)});

            if (cursor.moveToFirst()) {
                do {
                    int productId = cursor.getInt(cursor.getColumnIndexOrThrow("product_id"));
                    String manufactureDate = cursor.getString(cursor.getColumnIndexOrThrow("manufacture_date"));
                    String expiryDate = cursor.getString(cursor.getColumnIndexOrThrow("expiry_date"));
                    int addLogsCount = cursor.getInt(cursor.getColumnIndexOrThrow("addLogsCount"));
                    int usedLogsCount = cursor.getInt(cursor.getColumnIndexOrThrow("usedLogsCount"));
                    int overdueLogsCount = cursor.getInt(cursor.getColumnIndexOrThrow("overdueLogsCount"));

                    analyticsProducts.add(new AnalyticsProduct(
                            productId,
                            manufactureDate,
                            expiryDate,
                            addLogsCount,
                            usedLogsCount,
                            overdueLogsCount
                    ));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return analyticsProducts;
    }

    public ArrayList<AnalyticsProduct> getAnalyticsProductByProductIdANDDates(int product_id, String firstDate, String lastDate) {
        // По факту всегда один элемент, просто передаётся в списке
        ArrayList<AnalyticsProduct> analyticsProducts = new ArrayList<>();
        SQLiteDatabase db = getDbManager().getDatabase();

        Log.e("QueryParams", "product_id: " + product_id + ", firstDate: " + firstDate + ", lastDate: " + lastDate);


        String query = "SELECT " +
                "product_id, " +
                "manufacture_date, " +
                "expiry_date, " +
                "SUM(CASE WHEN operation_type = 'add' THEN quantity ELSE 0 END) AS addLogsCount, " +
                "SUM(CASE WHEN operation_type = 'used' THEN quantity ELSE 0 END) AS usedLogsCount, " +
                "SUM(CASE WHEN operation_type = 'overdue' THEN quantity ELSE 0 END) AS overdueLogsCount " +
                "FROM " + PRODUCT_LOGS_TABLE + " " +
                "WHERE product_id = ? AND date >= ? AND date <= ? " +
                "GROUP BY product_id, manufacture_date, expiry_date";


        Cursor cursor = null;

        try {
            cursor = db.rawQuery(query, new String[]{String.valueOf(product_id), firstDate, lastDate});

            Log.d("CursorCount", "Rows returned: " + cursor.getCount());

            if (cursor.moveToFirst()) {
                do {
                    int productId = cursor.getInt(cursor.getColumnIndexOrThrow("product_id"));
                    String manufactureDate = cursor.getString(cursor.getColumnIndexOrThrow("manufacture_date"));
                    String expiryDate = cursor.getString(cursor.getColumnIndexOrThrow("expiry_date"));
                    int addLogsCount = cursor.getInt(cursor.getColumnIndexOrThrow("addLogsCount"));
                    int usedLogsCount = cursor.getInt(cursor.getColumnIndexOrThrow("usedLogsCount"));
                    int overdueLogsCount = cursor.getInt(cursor.getColumnIndexOrThrow("overdueLogsCount"));

                    analyticsProducts.add(new AnalyticsProduct(
                            productId,
                            manufactureDate,
                            expiryDate,
                            addLogsCount,
                            usedLogsCount,
                            overdueLogsCount
                    ));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return analyticsProducts;
    }

    public ArrayList<AnalyticsProduct> getAllAnalyticsProductByType(String type) {
        ArrayList<AnalyticsProduct> list = new ArrayList<>();
        SQLiteDatabase db = getDbManager().getDatabase();

        String query = "SELECT " +
                "product_id, " +
                "manufacture_date, " +
                "expiry_date, " +
                "SUM(CASE WHEN operation_type = 'add' THEN quantity ELSE 0 END) AS addLogsCount, " +
                "SUM(CASE WHEN operation_type = 'used' THEN quantity ELSE 0 END) AS usedLogsCount, " +
                "SUM(CASE WHEN operation_type = 'overdue' THEN quantity ELSE 0 END) AS overdueLogsCount " +
                "FROM " + PRODUCT_LOGS_TABLE + " " +
                "WHERE product_id IN (SELECT id FROM " + PRODUCT_TABLE + " WHERE type = ?) " +
                "GROUP BY product_id, manufacture_date, expiry_date";

        Cursor cursor = null;

        try {
            cursor = db.rawQuery(query, new String[]{type});

            if (cursor.moveToFirst()) {
                do {
                    int productId = cursor.getInt(cursor.getColumnIndexOrThrow("product_id"));
                    String manufactureDate = cursor.getString(cursor.getColumnIndexOrThrow("manufacture_date"));
                    String expiryDate = cursor.getString(cursor.getColumnIndexOrThrow("expiry_date"));
                    int addLogsCount = cursor.getInt(cursor.getColumnIndexOrThrow("addLogsCount"));
                    int usedLogsCount = cursor.getInt(cursor.getColumnIndexOrThrow("usedLogsCount"));
                    int overdueLogsCount = cursor.getInt(cursor.getColumnIndexOrThrow("overdueLogsCount"));

                    list.add(new AnalyticsProduct(
                            productId,
                            manufactureDate,
                            expiryDate,
                            addLogsCount,
                            usedLogsCount,
                            overdueLogsCount
                    ));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return list;
    }

    public ArrayList<AnalyticsProduct> getAllAnalyticsProductByTypeANDDates(String type, String firstDate, String lastDate) {
        ArrayList<AnalyticsProduct> list = new ArrayList<>();
        SQLiteDatabase db = getDbManager().getDatabase();

        String query = "SELECT " +
                "product_id, " +
                "manufacture_date, " +
                "expiry_date, " +
                "SUM(CASE WHEN operation_type = 'add' THEN quantity ELSE 0 END) AS addLogsCount, " +
                "SUM(CASE WHEN operation_type = 'used' THEN quantity ELSE 0 END) AS usedLogsCount, " +
                "SUM(CASE WHEN operation_type = 'overdue' THEN quantity ELSE 0 END) AS overdueLogsCount " +
                "FROM " + PRODUCT_LOGS_TABLE + " " +
                "WHERE product_id IN (SELECT id FROM " + PRODUCT_TABLE + " WHERE type = ?) " +
                "AND date BETWEEN ? AND ? " +
                "GROUP BY product_id, manufacture_date, expiry_date";

        Cursor cursor = null;

        try {
            cursor = db.rawQuery(query, new String[]{type, firstDate, lastDate});

            if (cursor.moveToFirst()) {
                do {
                    int productId = cursor.getInt(cursor.getColumnIndexOrThrow("product_id"));
                    String manufactureDate = cursor.getString(cursor.getColumnIndexOrThrow("manufacture_date"));
                    String expiryDate = cursor.getString(cursor.getColumnIndexOrThrow("expiry_date"));
                    int addLogsCount = cursor.getInt(cursor.getColumnIndexOrThrow("addLogsCount"));
                    int usedLogsCount = cursor.getInt(cursor.getColumnIndexOrThrow("usedLogsCount"));
                    int overdueLogsCount = cursor.getInt(cursor.getColumnIndexOrThrow("overdueLogsCount"));

                    list.add(new AnalyticsProduct(
                            productId,
                            manufactureDate,
                            expiryDate,
                            addLogsCount,
                            usedLogsCount,
                            overdueLogsCount
                    ));
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return list;
    }

    // Уведомления
    public List<DataProductInFridge> getExpiredProductsInFridge() {
        List<DataProductInFridge> list = new ArrayList<>();
        SQLiteDatabase db = getDbManager().getDatabase();

        String currentDate = LocalDate.now().toString();

        String query = "SELECT * FROM " + PRODUCTS_IN_FRIDGE_TABLE +
                " WHERE DATE(expiry_date) <= DATE(?)";

        Cursor cursor = db.rawQuery(query, new String[]{currentDate});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                DataProductInFridge product = getDataProductInFridgeFromCursor(cursor);
                list.add(product);
            }
            cursor.close();
        }
        Log.i("getExpiredProductsInFridge", "len list of notifications = " + list.size());
        return list;
    }


}



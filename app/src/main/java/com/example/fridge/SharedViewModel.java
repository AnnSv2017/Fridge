package com.example.fridge;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

/** Этот класс используется для хранения данных, которые должны быть общими для нескольких фрагментов
 * (между CreateProductFragment и CheckAllergensFragment).
 * Благодаря этому данные сохраняются даже при переходах между фрагментами.**/

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<List<String>> selectedAllergens = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<String>> getSelectedAllergens() {
        return selectedAllergens;
    }

    public void setSelectedAllergens(List<String> allergens) {
        selectedAllergens.setValue(allergens);
    }
}

package com.example.fridge;

// Расширенный(есть метод обновления) интерфейс для взаимодействия с активностью во фрагментах (активность реализует этот интерфейс)
public interface OnFragmentInteractionExpendedListener {
    void onActivityUpdate();
    void onFragmentClose();
}

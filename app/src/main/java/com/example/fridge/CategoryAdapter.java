package com.example.fridge;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends BaseAdapter {
    private Context context;
    private List<Category> categories;
    private ProductAdapter productAdapter;
    private OnProductClickListener productClickListener;

    public interface OnProductClickListener {
        void onProductClick(DataProductInFridge product);
    }

    public CategoryAdapter(Context context, List<Category> categories, OnProductClickListener listener) {
        this.context = context;
        this.categories = categories;
        this.productClickListener = listener;
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.category_item, viewGroup, false);
        }

        Category category = categories.get(position);

        TextView textViewType = view.findViewById(R.id.text_view_type);
        TextView textViewQuantity = view.findViewById(R.id.text_view_quantity);
        ImageView imageViewSwitch = view.findViewById(R.id.image_view_list_switch);
        RelativeLayout rlShowInfoProduct = view.findViewById(R.id.rl_show_info_product);

        textViewType.setText(category.getName());
        imageViewSwitch.setImageResource(category.isExpanded() ? R.drawable.ic_open_list : R.drawable.ic_close_list);

        // Обработка нажатия на переключение видимости продуктов
        rlShowInfoProduct.setOnClickListener(v -> {
            category.setExpanded(!category.isExpanded());
            notifyDataSetChanged(); // Обновляем вид категорий
        });

        // Выставляем количество зелёных и красных продуктов
        productAdapter = new ProductAdapter(context, category.getProducts());
        int[] counts = productAdapter.countGreenAndRed();
        String greenCount = String.valueOf(counts[0]);
        String redCount = String.valueOf(counts[1]);
        textViewQuantity.setText(greenCount + "/" + redCount);

        // Если категория раскрыта, отображаем продукты
        ListView productsListView = view.findViewById(R.id.products_list_view);
        if (category.isExpanded()) {
            Log.d("CategoryAdapter", "Category expanded: " + category.getName());
            productsListView.setAdapter(productAdapter);
            // Устанавливаем высоту ListView
            setListViewHeightBasedOnChildren(productsListView);

            // Нажимаем на элемент продукта
            productsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    ArrayList<DataProductInFridge> dataProductInFridge = category.getProducts();
                    showInfoProduct(dataProductInFridge.get(i));
                }
            });
            productsListView.setVisibility(View.VISIBLE);
        } else {
            Log.d("CategoryAdapter", "Category collapsed: " + category.getName());
            productsListView.setVisibility(View.GONE);
        }

        return view;
    }

    private void showInfoProduct(DataProductInFridge data) {
        Log.e("CategoryAdapter", "показ инфы продукта " + data.getId());
        if (productClickListener != null) {
            productClickListener.onProductClick(data);
        }
    }

    public void updateCategories(List<Category> newCategories) {
        Log.d("CategoryAdapter", "Updating categories. New size: " + newCategories.size());
        this.categories.clear();
        this.categories.addAll(newCategories);
        notifyDataSetChanged();
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(
                    View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

}


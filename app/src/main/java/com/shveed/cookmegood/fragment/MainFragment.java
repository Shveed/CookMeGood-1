package com.shveed.cookmegood.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.shveed.cookmegood.adapter.RecipesGridAdapter;
import com.shveed.cookmegood.data.NetworkService;
import com.shveed.cookmegood.data.RuntimeStorage;
import com.shveed.cookmegood.data.dto.Category;
import com.shveed.cookmegood.listener.FragmentChangeListener;
import com.shveed.wallpapperparser.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainFragment extends Fragment implements RecipesGridAdapter.ItemClickListener {

    @BindView(R.id.mainFragmentProgressBar)
    ProgressBar progressBar;

    @BindView(R.id.mainFragmentToolbar)
    Toolbar toolbar;

    @BindView(R.id.mainFragmentRecycler)
    RecyclerView recyclerView;

    private RecipesGridAdapter adapter;
    private List<String> data = new ArrayList<>();
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.f_main, container, false);

        ButterKnife.bind(this, view);

        data = RuntimeStorage.newInstance().categories;

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new RecipesGridAdapter(getContext(), data);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        recyclerView.setVisibility(View.GONE);

        try {
            getCategoriesFromServer();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return view;

    }
    @Override
    public void onItemClick(View view, int position) {
        Fragment category = new CategoryFragment();
        FragmentChangeListener fc = (FragmentChangeListener)getActivity();
        fc.replaceFragment(category);
    }

    private void getCategoriesFromServer(){
        NetworkService.getInstance()
                .getCategoryApi()
                .getAllCategories()
                .enqueue(new Callback<List<Category>>() {
                    @Override
                    public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {

                        showList();

                        for(Category category: response.body()){
                            RuntimeStorage.newInstance().categories.add(category.getName());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Category>> call, Throwable t) {

                        showList();

                        Toast.makeText(getContext(), "Нет связи с сервером", Toast.LENGTH_SHORT).show();

                        RuntimeStorage.newInstance().categories =
                                Arrays.asList("Каши", "Салаты", "Супы", "Рыба и Мясо", "Выпечка", "Закуски", "Десерты", "Напитки", "Заготовки на зиму");

                        adapter.onUpdateList(RuntimeStorage.newInstance().categories);
                    }
                });
    }

    private void showList(){
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

}

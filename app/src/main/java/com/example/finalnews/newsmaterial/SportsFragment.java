package com.example.finalnews.newsmaterial;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.finalnews.R;
import com.example.finalnews.newsmaterial.model.Datum;
import com.example.finalnews.newsmaterial.model.Root;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SportsFragment extends Fragment {

    String API_KEY = "3df1cb13739b43afb5006585e75c6bdf";
    RecyclerView recyclerView;
    Adapter adapter;
    ArrayList<Datum> datumArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.fragment_home,null);

        recyclerView = v.findViewById(R.id.home_recyclerview);
        datumArrayList = new ArrayList<>();
        adapter = new Adapter(getContext(),datumArrayList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getNews();

        return v;
    }

    void getNews(){
        ApiUtilities.getApiInterface().getTopHeadlines("dfRXTGJ3LHJaSpw2mrE7wydaxXoyPfDYUPi1cqT1","in","sports").enqueue(new Callback<Root>() {
            @Override
            public void onResponse(Call<Root> call, Response<Root> response) {
                if (response.isSuccessful()){
                    datumArrayList.addAll(response.body().getData());
                    datumArrayList.addAll(response.body().getData());
                    datumArrayList.addAll(response.body().getData());
                    datumArrayList.addAll(response.body().getData());
                    datumArrayList.addAll(response.body().getData());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Root> call, Throwable t) {

            }
        });
    }
}
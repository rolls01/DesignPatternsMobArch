package com.jonbott.knownspies.Activities.SpyList;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jonbott.knownspies.Activities.Details.SpyDetailsActivity;
import com.jonbott.knownspies.Coordinators.RootCoordinator;
import com.jonbott.knownspies.Dependencies.DependencyRegistry;
import com.jonbott.knownspies.Helpers.Constants;
import com.jonbott.knownspies.Helpers.Threading;
import com.jonbott.knownspies.ModelLayer.DTOs.SpyDTO;
import com.jonbott.knownspies.ModelLayer.Database.Realm.Spy;
import com.jonbott.knownspies.ModelLayer.Enums.Source;
import com.jonbott.knownspies.ModelLayer.Translation.SpyTranslator;
import com.jonbott.knownspies.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SpyListActivity extends AppCompatActivity {

    private static final String TAG = "SpyListActivity";

    private SpyListPresenter spyListPresenter;
    private RootCoordinator rootCoordinator;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_spy_list);

        attachUI();

        DependencyRegistry.shared.inject(this);
    }

    //region InjectionMethods
    public void configureWith(SpyListPresenter spyListPresenter, RootCoordinator rootCoordinator) {
        this.spyListPresenter = spyListPresenter;
        this.rootCoordinator = rootCoordinator;
        loadData();
        setupObservables();
    }

    private void setupObservables() {
        spyListPresenter.spies().subscribe(spies ->{
            SpyViewAdapter adapter = (SpyViewAdapter) recyclerView.getAdapter();
            adapter.setSpies(spies);
        });
    }
    //endregion


    //region Helper Methods

    private void attachUI() {

        Button newSpyButton = (Button) findViewById(R.id.new_spy_button);
        newSpyButton.setOnClickListener(view -> spyListPresenter.addNewSpy());

        LinearLayoutManager manager = new LinearLayoutManager(this);

        recyclerView = (RecyclerView) findViewById(R.id.spy_recycler_view);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);

        initializeListView();
    }
    //}
    //    }
    //        e.printStackTrace();
    //    } catch (Exception e) {
    //        initializeData();
    //        initializeListView();
    //        try {
//    private void setupData() {

    //endregion

    //region Data Process specific to SpyListActivity


    private void loadData() {
        spyListPresenter.loadData(this::onDataReceived);

    }

    //        });
    //            persistJson(json, () -> loadSpiesFromLocal());
    //            notifyDataReceived(Source.network);
    //        loadJson(json -> {
    //
    //        notifyDataReceived(Source.local);
    //        loadSpiesFromLocal();
    //
//    private void initializeData() throws Exception {

//    }

    //endregion

    //region User Interaction

    private void rowTapped(int position) {
        SpyDTO spy = spyListPresenter.spies().getValue().get(position);
        gotoSpyDetails(spy.id);
    }

    private void onDataReceived(Source source) {
        String message = String.format("Data from %s", source.name());
        Toast.makeText(SpyListActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    //endregion


    //region List View Adapter

    private void initializeListView() {
        SpyViewAdapter adapter = new SpyViewAdapter((v, position) -> rowTapped(position));
        recyclerView.setAdapter(adapter);
    }

    //endregion

    //region Navigation

    private void gotoSpyDetails(int spyId) {

        rootCoordinator.handleSpyCellTapped(this, spyId);

    }

    //endregion

}

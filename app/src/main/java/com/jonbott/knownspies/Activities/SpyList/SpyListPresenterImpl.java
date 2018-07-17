package com.jonbott.knownspies.Activities.SpyList;

import com.jonbott.knownspies.ModelLayer.DTOs.SpyDTO;
import com.jonbott.knownspies.ModelLayer.Enums.Source;
import com.jonbott.knownspies.ModelLayer.ModelLayer;

import java.util.List;

import io.reactivex.functions.Consumer;

public class SpyListPresenterImpl implements SpyListPresenter {

    private static final String TAG = "SpyListPresenter";


    ModelLayer modelLayer;

    public SpyListPresenterImpl(ModelLayer modelLayer) {
        this.modelLayer = modelLayer;
    }

    //region Presenter Methods
    @Override
    public void loadData(Consumer<List<SpyDTO>> onNewResults, Consumer<Source> notifyDataReceiver){
        modelLayer.loadData(onNewResults, notifyDataReceiver);
    }
    //endregion




}

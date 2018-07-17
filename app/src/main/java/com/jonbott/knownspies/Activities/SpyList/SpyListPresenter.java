package com.jonbott.knownspies.Activities.SpyList;

import com.jonbott.knownspies.ModelLayer.DTOs.SpyDTO;
import com.jonbott.knownspies.ModelLayer.Enums.Source;

import java.util.List;

import io.reactivex.functions.Consumer;

public interface SpyListPresenter {
    //region Presenter Methods
    void loadData(Consumer<List<SpyDTO>> onNewResults, Consumer<Source> notifyDataReceiver);
}

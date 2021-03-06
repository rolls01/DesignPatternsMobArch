package com.jonbott.knownspies.ModelLayer;

import com.jonbott.knownspies.ModelLayer.DTOs.SpyDTO;
import com.jonbott.knownspies.ModelLayer.Enums.Source;

import java.util.List;

import io.reactivex.functions.Consumer;

public interface ModelLayer {
    void loadData(Consumer<List<SpyDTO>> onNewResults, Consumer<Source> notifyDataReceiver);

    //    public Spy spyForId(int spyId) {
    //        Spy tempSpy = realm.where(Spy.class).equalTo("id", spyId).findFirst();
    //        Log.d("tempSpy", tempSpy == null ? "null": tempSpy.name);
    //        return realm.copyFromRealm(tempSpy);
    //    }
    SpyDTO spyForId(int spyId);
}

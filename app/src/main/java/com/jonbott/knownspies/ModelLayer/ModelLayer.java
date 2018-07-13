package com.jonbott.knownspies.ModelLayer;

import android.util.Log;

import com.jonbott.knownspies.Helpers.Threading;
import com.jonbott.knownspies.ModelLayer.DTOs.SpyDTO;
import com.jonbott.knownspies.ModelLayer.Database.DataLayer;
import com.jonbott.knownspies.ModelLayer.Database.Realm.Spy;
import com.jonbott.knownspies.ModelLayer.Enums.Source;
import com.jonbott.knownspies.ModelLayer.Network.NetworkLayer;
import com.jonbott.knownspies.ModelLayer.Translation.TranslationLayer;

import java.util.List;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.realm.Realm;

public class ModelLayer {

    private static final String TAG = "ModelLayer";
    private NetworkLayer networkLayer = new NetworkLayer();
    private DataLayer dataLayer = new DataLayer();
    private Realm realm = Realm.getDefaultInstance();
    private TranslationLayer translationLayer = new TranslationLayer();

    public void loadData(Consumer<List<Spy>> onNewResults, Consumer<Source> notifyDataReceiver) {
        try{
            dataLayer.loadSpiesFromLocal(onNewResults);
            notifyDataReceiver.accept(Source.local);
        }catch (Exception e){
            e.printStackTrace();
        }

        networkLayer.loadJson(json ->{
            notifyDataReceiver.accept(Source.network);
            persistJson(json, ()->dataLayer.loadSpiesFromLocal(onNewResults));
        });
    }

    private void persistJson(String json, Action finished) {
        translationLayer.convertJson(json);

//        Threading.async(() -> {
//
//            clearSpies(() -> {
//                List<SpyDTO> dtos = translationLayer.convertJson(json);
//                dtos.forEach(dto -> dto.initialize());
//                persistDTOs(dtos);
//
//                Threading.dispatchMain(() -> finished.run());
//            });
//
//            return true;
//        });
//    }

//    private void clearSpies(Action finished) throws Exception {
//        Log.d(TAG, "clearing DB");
//
//        Realm backgroundRealm = Realm.getInstance(realm.getConfiguration());
//        backgroundRealm.executeTransaction(r -> r.delete(Spy.class));
//
//        finished.run();
//    }
//    private void persistDTOs(List<SpyDTO> dtos) {
//        Log.d(TAG, "persisting dtos to DB");
//
//        Realm backgroundRealm = Realm.getInstance(realm.getConfiguration());
//        backgroundRealm.executeTransaction(r -> r.delete(Spy.class));
//
//        //ignore result and just save in realm
//        dtos.forEach(dto -> spyTranslator.translate(dto, backgroundRealm));
    }

    public Spy spyForId(int spyId) {
        Spy tempSpy = realm.where(Spy.class).equalTo("id", spyId).findFirst();
        return realm.copyFromRealm(tempSpy);
    }
}

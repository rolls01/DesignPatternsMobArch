package com.jonbott.knownspies.ModelLayer.Database;

import android.util.Log;

import com.jonbott.knownspies.ModelLayer.DTOs.SpyDTO;
import com.jonbott.knownspies.ModelLayer.Database.Realm.Spy;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.realm.Realm;
import io.realm.RealmResults;

public class DataLayerImpl implements DataLayer {

    private static final String TAG = "DataLayer";
    private Realm realm = Realm.getDefaultInstance();
    private Callable<Realm> newRealmInstanceOnCurrentThread;

    public DataLayerImpl(Realm realm, Callable<Realm> newRealmInstanceOnCurrentThread) {
        this.realm = realm;
        this.newRealmInstanceOnCurrentThread = newRealmInstanceOnCurrentThread;
    }

    //region Database Methods

    @Override
    public void loadSpiesFromLocal(Function<Spy, SpyDTO> translationBlock, Consumer<List<SpyDTO>> onNewResults) throws Exception {
        Log.d(TAG, "Loading spies from DB");
        loadSpiesFromRealm(spies -> {
            List<SpyDTO> dtos = translate(spies, translationBlock);
            onNewResults.accept(dtos);

        });
    }

    private List<SpyDTO> translate(List<Spy> spies, Function<Spy, SpyDTO> translateBlock){
        List<SpyDTO> dtos = Observable.fromArray(spies)
                                        .flatMapIterable(list -> list)
                                        .map(translateBlock::apply)
                                        .toList()
                                        .blockingGet();
        return dtos;
    }

//
//    private void persistJson(String json, Action finished) {
//        Threading.async(() -> {
//
//            clearSpies(() -> {
//                List<SpyDTO> dtos = networkLayer.convertJson(json);
//                dtos.forEach(dto -> dto.initialize());
//                persistDTOs(dtos);
//
//                Threading.dispatchMain(() -> finished.run());
//            });
//
//            return true;
//        });
//    }

    @Override
    public void loadSpiesFromRealm(Consumer<List<Spy>> finished) {
        RealmResults<Spy> spyResults = realm.where(Spy.class).findAll();

        List<Spy> spies = realm.copyFromRealm(spyResults);
        try {
            finished.accept(spies);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearSpies(Action finished) throws Exception {
        Log.d(TAG, "clearing DB");

        Realm backgroundRealm = newRealmInstanceOnCurrentThread.call();
        backgroundRealm.executeTransaction(r -> r.delete(Spy.class));
        backgroundRealm.close();

        finished.run();
    }

    @Override
    public void persistDTOs(List<SpyDTO> dtos, BiFunction<SpyDTO, Realm, Spy> translationBlock) {
        Log.d(TAG, "persisting dtos to DB");

        try {
            Realm backgroundRealm = newRealmInstanceOnCurrentThread.call();
            //ignore result and just save in realm
            dtos.forEach(dto -> convertToSpy(translationBlock, backgroundRealm, dto));
            backgroundRealm.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void convertToSpy(BiFunction<SpyDTO, Realm, Spy> translationBlock, Realm backgroundRealm, SpyDTO dto){
        try{
            translationBlock.apply(dto, backgroundRealm);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    //endregion

    @Override
    public Spy spyForId(int spyId){
        Spy tempSpy = realm.where(Spy.class).equalTo("id", spyId).findFirst();
        return realm.copyFromRealm(tempSpy);
    }

}

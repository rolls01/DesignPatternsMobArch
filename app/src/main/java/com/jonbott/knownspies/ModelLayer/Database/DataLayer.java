package com.jonbott.knownspies.ModelLayer.Database;

import android.util.Log;

import com.jonbott.knownspies.Helpers.Threading;
import com.jonbott.knownspies.ModelLayer.DTOs.SpyDTO;
import com.jonbott.knownspies.ModelLayer.Database.Realm.Spy;
import com.jonbott.knownspies.ModelLayer.Enums.DTOType;
import com.jonbott.knownspies.ModelLayer.Network.NetworkLayer;
import com.jonbott.knownspies.ModelLayer.Translation.SpyTranslator;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.realm.Realm;
import io.realm.RealmResults;

public class DataLayer {

    private static final String TAG = "DataLayer";
    private Realm realm = Realm.getDefaultInstance();
    private SpyTranslator spyTranslator = new SpyTranslator();
    private NetworkLayer networkLayer = new NetworkLayer();

    //region Database Methods

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

    public void loadSpiesFromRealm(Consumer<List<Spy>> finished) {
        RealmResults<Spy> spyResults = realm.where(Spy.class).findAll();

        List<Spy> spies = realm.copyFromRealm(spyResults);
        try {
            finished.accept(spies);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearSpies(Action finished) throws Exception {
        Log.d(TAG, "clearing DB");

        Realm backgroundRealm = Realm.getInstance(realm.getConfiguration());
        backgroundRealm.executeTransaction(r -> r.delete(Spy.class));

        finished.run();
    }

    public void persistDTOs(List<SpyDTO> dtos, BiFunction<SpyDTO, Realm, Spy> translationBlock) {
        Log.d(TAG, "persisting dtos to DB");

        Realm backgroundRealm = Realm.getInstance(realm.getConfiguration());
        backgroundRealm.executeTransaction(r -> r.delete(Spy.class));

        //ignore result and just save in realm
        dtos.forEach(dto -> convertToSpy(translationBlock, backgroundRealm, dto));
    }
    private void convertToSpy(BiFunction<SpyDTO, Realm, Spy> translationBlock, Realm backgroundRealm, SpyDTO dto){
        try{
            translationBlock.apply(dto, backgroundRealm);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    //endregion

    public Spy spyForId(int spyId){
        Spy tempSpy = realm.where(Spy.class).equalTo("id", spyId).findFirst();
        return realm.copyFromRealm(tempSpy);
    }

}

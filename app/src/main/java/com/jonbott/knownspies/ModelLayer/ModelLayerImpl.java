package com.jonbott.knownspies.ModelLayer;

import com.jonbott.knownspies.Helpers.Threading;
import com.jonbott.knownspies.ModelLayer.DTOs.SpyDTO;
import com.jonbott.knownspies.ModelLayer.Database.DataLayer;
import com.jonbott.knownspies.ModelLayer.Database.Realm.Spy;
import com.jonbott.knownspies.ModelLayer.Enums.Source;
import com.jonbott.knownspies.ModelLayer.Network.NetworkLayer;
import com.jonbott.knownspies.ModelLayer.Translation.SpyTranslator;
import com.jonbott.knownspies.ModelLayer.Translation.TranslationLayer;

import java.util.List;

import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

public class ModelLayerImpl implements ModelLayer {

    private static final String TAG = "ModelLayer";
    private NetworkLayer networkLayer;// = new NetworkLayer();
    private DataLayer dataLayer;// = new DataLayer();
    private TranslationLayer translationLayer;// = new TranslationLayer();

    public ModelLayerImpl(NetworkLayer networkLayer, DataLayer dataLayer, TranslationLayer translationLayer) {
        this.networkLayer = networkLayer;
        this.dataLayer = dataLayer;
        this.translationLayer = translationLayer;
    }

    @Override
    public void loadData(Consumer<List<SpyDTO>> onNewResults, Consumer<Source> notifyDataReceiver) {
        SpyTranslator spyTranslator = translationLayer.translatorFor(SpyDTO.dtoType);
        try{
            dataLayer.loadSpiesFromLocal(spyTranslator::translate, onNewResults);
            notifyDataReceiver.accept(Source.local);
        }catch (Exception e){
            e.printStackTrace();
        }

        networkLayer.loadJson(json ->{
            notifyDataReceiver.accept(Source.network);
            persistJson(json, ()->dataLayer.loadSpiesFromLocal(spyTranslator::translate, onNewResults));
        });
    }

    private void persistJson(String json, Action finished) {
        List<SpyDTO> spyDTOS = translationLayer.convertJson(json);

        Threading.async(() ->{
           dataLayer.clearSpies(() -> {
               spyDTOS.forEach(dto -> dto.initialize());

                   SpyTranslator spyTranslator = translationLayer.translatorFor(SpyDTO.dtoType);
                    dataLayer.persistDTOs(spyDTOS, spyTranslator::translate);

                    Threading.dispatchMain(() -> finished.run());

           });
            return true;
        });

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

//    public Spy spyForId(int spyId) {
//        Spy tempSpy = realm.where(Spy.class).equalTo("id", spyId).findFirst();
//        Log.d("tempSpy", tempSpy == null ? "null": tempSpy.name);
//        return realm.copyFromRealm(tempSpy);
//    }
    @Override
    public SpyDTO spyForId(int spyId){
        Spy spy = dataLayer.spyForId(spyId);
        SpyDTO spyDTO = translationLayer.translate(spy);
        return spyDTO;
    }
}

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
    @Override
    public SpyDTO spyForName(String name) {
        Spy spy = dataLayer.spyForName(name);
        SpyDTO spyDTO = translationLayer.translate(spy);
        return spyDTO;
    }

    @Override
    public void save(List<SpyDTO> dtos, Action finished) {
        Threading.async(() -> {
            persistDTOs(dtos, finished);
            return true;
        });
    }

    private void persistDTOs(List<SpyDTO> dtos, Action finished) {
        SpyTranslator spyTranslator = translationLayer.translatorFor(SpyDTO.dtoType);
        dataLayer.persistDTOs(dtos, spyTranslator::translate);

        Threading.dispatchMain(() -> finished.run());
    }

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
                    persistDTOs(spyDTOS, finished);

           });
            return true;
        });
    }

    @Override
    public SpyDTO spyForId(int spyId){
        Spy spy = dataLayer.spyForId(spyId);
        SpyDTO spyDTO = translationLayer.translate(spy);
        return spyDTO;
    }
}

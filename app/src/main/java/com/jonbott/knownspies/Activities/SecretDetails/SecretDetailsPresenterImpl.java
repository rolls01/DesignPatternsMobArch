package com.jonbott.knownspies.Activities.SecretDetails;

import com.jonbott.knownspies.Helpers.Threading;
import com.jonbott.knownspies.ModelLayer.DTOs.SpyDTO;
import com.jonbott.knownspies.ModelLayer.ModelLayer;

import java.util.function.Consumer;

public class SecretDetailsPresenterImpl implements SecretDetailsPresenter {
    public String password;

    private int spyId;

    @Override
    public String getPassword() {
        return password;
    }

    private SpyDTO spy;
    private ModelLayer modelLayer;

    public SecretDetailsPresenterImpl(int spyId, ModelLayer modelLayer) {
        this.spyId = spyId;
        this.modelLayer = modelLayer;
        
        configureData();
    }

    private void configureData() {
        spy = this.modelLayer.spyForId(spyId);
        password = spy.password;
    }

//    public SecretDetailsPresenter(int spyId, ModelLayer moduleLayer){
//        spy = modelLayer.spyForId(spyId); //getSpy(spyId);
//
//        password = spy.password;
//    }


    @Override
    public void crackPassword(Consumer<String> finish) {
        Threading.async(()-> {
            //fake processing work
            Thread.sleep(2000);
            return true;
        }, success -> {
            finish.accept(password);
        });
    }


    //region Data loading
//    public Spy getSpy(int id) {
//        Spy tempSpy = realm.where(Spy.class).equalTo("id", id).findFirst();
//        return realm.copyFromRealm(tempSpy);
//    }
    //endregion

}

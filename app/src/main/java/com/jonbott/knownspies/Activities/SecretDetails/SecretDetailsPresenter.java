package com.jonbott.knownspies.Activities.SecretDetails;

import android.view.View;

import com.jonbott.knownspies.Helpers.Threading;
import com.jonbott.knownspies.ModelLayer.Database.Realm.Spy;
import com.jonbott.knownspies.ModelLayer.ModelLayer;

import java.util.function.Consumer;

import io.realm.Realm;

class SecretDetailsPresenter {

//    private Realm realm = Realm.getDefaultInstance();

    ModelLayer modelLayer = new ModelLayer();

    private Spy spy;
    public String password;

    SecretDetailsPresenter(int spyId){
        spy = modelLayer.spyForId(spyId); //getSpy(spyId);

        password = spy.password;
    }


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

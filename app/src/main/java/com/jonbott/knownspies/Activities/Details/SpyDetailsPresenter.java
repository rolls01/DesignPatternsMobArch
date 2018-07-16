package com.jonbott.knownspies.Activities.Details;

import android.content.Context;

import com.jonbott.knownspies.Helpers.Helper;
import com.jonbott.knownspies.ModelLayer.DTOs.SpyDTO;
import com.jonbott.knownspies.ModelLayer.Database.Realm.Spy;
import com.jonbott.knownspies.ModelLayer.ModelLayer;

import io.realm.Realm;

class SpyDetailsPresenter {

//    private Realm realm = Realm.getDefaultInstance();

    private ModelLayer modelLayer = new ModelLayer();

    SpyDTO spy;

    public int spyId;
    public String age;
    public String name;
    public String gender;
    public String imageName;
    public int imageId;

    private Context context;

    public SpyDetailsPresenter(int spyId) {
        this.spyId = spyId;

        spy = modelLayer.spyForId(spyId);
        configureSpy(spy);

    }

    private void configureSpy(SpyDTO spy) {
        age = String.valueOf(spy.age);
        name = spy.name;
        gender = spy.gender.name();
        imageName = spy.imageName;
    }

//    private Spy getSpy(int spyId) {
//        Spy tempSpy = realm.where(Spy.class).equalTo("id", spyId).findFirst();
//        return realm.copyFromRealm(tempSpy);
//    }

    public void configureWithContext(Context context) {
        this.context = context;
        imageId = Helper.resourceIdWith(context, imageName);
    }
}

package com.jonbott.knownspies.Activities.Details;

import android.content.Context;

import com.jonbott.knownspies.Helpers.Helper;
import com.jonbott.knownspies.ModelLayer.DTOs.SpyDTO;
import com.jonbott.knownspies.ModelLayer.ModelLayer;

public class SpyDetailsPresenterImpl implements SpyDetailsPresenter {

//    private Realm realm = Realm.getDefaultInstance();


    //region Getters
    @Override
    public String getAge() {
        return age;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getGender() {
        return gender;
    }

    @Override
    public String getImageName() {
        return imageName;
    }

    @Override
    public int getImageId() {
        return imageId;
    }

    @Override
    public int getSpyId() {
        return spyId;
    }
    //endregion

    private SpyDTO spy;

    public String age;
    public String name;
    public String gender;
    public String imageName;
    public int imageId;

    private Context context;
    public int spyId;
    private ModelLayer modelLayer;

    public SpyDetailsPresenterImpl(int spyId, Context context, ModelLayer modelLayer) {
        this.context = context;
        this.spyId = spyId;
        this.modelLayer = modelLayer;

        configureData();
    }

    private void configureData() {
        this.spy = modelLayer.spyForId(spyId);
        this.age = String.valueOf(this.spy.age);
        this.name = this.spy.name;
        this.gender = this.spy.gender.name();
        this.imageName = this.spy.imageName;

        this.imageId = Helper.resourceIdWith(context, imageName);
    }
}

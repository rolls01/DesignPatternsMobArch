package com.jonbott.knownspies.Activities.Details;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jonbott.knownspies.Activities.SecretDetails.SecretDetailsActivity;
import com.jonbott.knownspies.Coordinators.RootCoordinator;
import com.jonbott.knownspies.Dependencies.DependencyRegistry;
import com.jonbott.knownspies.Helpers.Constants;
import com.jonbott.knownspies.R;

public class SpyDetailsActivity extends AppCompatActivity {

//    private Realm realm = Realm.getDefaultInstance();
//
//    private int spyId = -1;

    private SpyDetailsPresenter spyDetailsPresenter;
    private RootCoordinator rootCoordinator;
    private ImageView profileImage;
    private TextView nameTextView;
    private TextView ageTextView;
    private TextView genderTextView;
    private ImageButton calculateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spy_details);
        attachUI();

        Bundle bundle = getIntent().getExtras();
        DependencyRegistry.shared.inject(this, bundle);
    }

//    public void configure(SpyDetailsPresenter spyDetailsPresenter){
//        this.spyDetailsPresenter = spyDetailsPresenter;
//        this.spyDetailsPresenter.configureWithContext(this);
//    }

    //region UI Methods

    private void attachUI() {
        profileImage = (ImageView) findViewById(R.id.details_profile_image);
        nameTextView = (TextView) findViewById(R.id.details_name);
        ageTextView = (TextView) findViewById(R.id.details_age);
        genderTextView = (TextView) findViewById(R.id.details_gender);
        calculateButton = (ImageButton) findViewById(R.id.calculate_button);

        calculateButton.setOnClickListener(v -> gotoSecretDetails());
    }


    //region navigation

    private void gotoSecretDetails() {
        if (spyDetailsPresenter == null) return;

        rootCoordinator.handleSecretButtonTapped(this, spyDetailsPresenter.getSpyId());
    }

    public void configureWith(SpyDetailsPresenter presenter, RootCoordinator rootCoordinator) {
        this.spyDetailsPresenter = presenter;
        this.rootCoordinator = rootCoordinator;
        ageTextView.setText(spyDetailsPresenter.getAge());
        profileImage.setImageResource(spyDetailsPresenter.getImageId());
        nameTextView.setText(spyDetailsPresenter.getName());
        genderTextView.setText(spyDetailsPresenter.getGender());
    }

    //endregion

}

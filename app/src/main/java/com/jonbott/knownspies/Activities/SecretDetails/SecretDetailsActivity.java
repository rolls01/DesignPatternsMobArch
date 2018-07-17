package com.jonbott.knownspies.Activities.SecretDetails;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jonbott.knownspies.Activities.SpyList.SpyListActivity;
import com.jonbott.knownspies.Dependencies.DependencyRegistry;
import com.jonbott.knownspies.R;

public class SecretDetailsActivity extends AppCompatActivity {

    SecretDetailsPresenter secretDetailsPresenter;
    ProgressBar progressBar;
    TextView crackingLabel;
    Button finishedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret_details);

        attachUI();
//        parseBundle();

        Bundle bundle = getIntent().getExtras();
        DependencyRegistry.shared.inject(this, bundle);
    }

    public void configureWith(SecretDetailsPresenter secretDetailsPresenter){
        this.secretDetailsPresenter = secretDetailsPresenter;
        this.secretDetailsPresenter.crackPassword(password ->{
            progressBar.setVisibility(View.GONE);
            crackingLabel.setText(secretDetailsPresenter.getPassword());
        });
    }

    //region Helper Methods

    private void attachUI() {
        progressBar    = (ProgressBar) findViewById(R.id.secret_progress_bar);
        crackingLabel  = (TextView)    findViewById(R.id.secret_cracking_label);
        finishedButton = (Button)      findViewById(R.id.secret_finished_button);

        finishedButton.setOnClickListener(v -> finishedClicked() );
    }

    //endregion

    //region User Interaction

    private void finishedClicked() {
        Intent intent = new Intent(this, SpyListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    //endregion



}

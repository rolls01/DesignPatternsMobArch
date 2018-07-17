package com.jonbott.knownspies.Activities.SecretDetails;

import java.util.function.Consumer;

public interface SecretDetailsPresenter {
    String getPassword();

    void crackPassword(Consumer<String> finish);
}

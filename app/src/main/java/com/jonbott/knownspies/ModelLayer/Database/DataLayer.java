package com.jonbott.knownspies.ModelLayer.Database;

import com.jonbott.knownspies.ModelLayer.DTOs.SpyDTO;
import com.jonbott.knownspies.ModelLayer.Database.Realm.Spy;

import java.util.List;

import io.reactivex.functions.Action;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.realm.Realm;

public interface DataLayer {
    void loadSpiesFromLocal(Function<Spy, SpyDTO> translationBlock, Consumer<List<SpyDTO>> onNewResults) throws Exception;

    void loadSpiesFromRealm(Consumer<List<Spy>> finished);

    void clearSpies(Action finished) throws Exception;

    void persistDTOs(List<SpyDTO> dtos, BiFunction<SpyDTO, Realm, Spy> translationBlock);

    Spy spyForId(int spyId);
}

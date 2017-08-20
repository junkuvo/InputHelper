package junkuvo.apps.inputhelper;


import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class App extends Application {

    private AppRealmMigration realmMigration = new AppRealmMigration();
    private Realm realm;

    @Override
    public void onCreate() {
        super.onCreate();

        // realmの初期化
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(1)
                .migration(realmMigration).build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        realm.close();
    }

    public Realm getRealm() {
        return realm;
    }
}

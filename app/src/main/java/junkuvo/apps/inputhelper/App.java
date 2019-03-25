package junkuvo.apps.inputhelper;


import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.MobileAds;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class App extends Application {

    private AppRealmMigration realmMigration = new AppRealmMigration();
    private Realm realm;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        // realmの初期化
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(1)
                .migration(realmMigration).build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();

        MobileAds.initialize(this, "ca-app-pub-1630604043812019~8921937516");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        realm.close();
    }

    public Realm getRealm() {
        return realm;
    }

    private int adW = 0;
    private int adH = 0;

    public void prepareAdSize() {
        float scale = getResources().getDisplayMetrics().density;
        float w = getResources().getDisplayMetrics().widthPixels;
        float h = getResources().getDisplayMetrics().heightPixels;
        float ratio = h / w;

        // 広告用の幅・高さ
        adW = (int) (w / scale);
        adH = (int) ((w / scale) * 5 / 32);// バナー広告の比率

    }

    public int getAdW() {
        if (adW == 0) {
            prepareAdSize();
        }
        return adW;
    }

    public int getAdH() {
        if (adH == 0) {
            prepareAdSize();
        }
        return adH;
    }
}

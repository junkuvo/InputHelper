package junkuvo.apps.inputhelper;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;
import io.realm.RealmSchema;


public class AppRealmMigration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        // DynamicRealm exposes an editable schema
        RealmSchema schema = realm.getSchema();

//        if (oldVersion == 0) {
//            schema.get("ListItemData")
//                    .addField("memo", String.class);
//            oldVersion++;
//        }
    }

    //http://stackoverflow.com/questions/36907001/open-realm-with-new-realmconfiguration
    @Override
    public int hashCode() {
        return BuildConfig.VERSION_CODE;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof AppRealmMigration);
    }
}

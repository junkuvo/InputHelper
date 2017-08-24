package junkuvo.apps.inputhelper.util;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import junkuvo.apps.inputhelper.fragment.item.ListItemData;

public class RealmUtil {

    public static void insertItem(Realm realm, final ListItemData listItemData) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // プライマリキーが一致するデータがすでに存在すれば、データを更新し、無ければ新しくオブジェクトを作成します。
                realm.copyToRealmOrUpdate(listItemData);
//                copyToRealmObject(listItemData, realm.createObject(ListItemData.class, System.currentTimeMillis()));
            }
        });
    }

    public static void insertHistoryItemAsync(final Realm realm, final ListItemData listItemData, final realmTransactionCallbackListener realmTransactionCallbackListener) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                ListItemData realmHistoryItem = bgRealm.createObject(ListItemData.class, System.currentTimeMillis());
                copyToRealmObject(listItemData, realmHistoryItem);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (realmTransactionCallbackListener != null) {
                    realmTransactionCallbackListener.OnSuccess();
                }
//                // Transaction was a success.
//                // FIXME : これはここでいいのか？
//                realm.close();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
                if (realmTransactionCallbackListener != null) {
                    realmTransactionCallbackListener.OnError();
                }
//                // Transaction failed and was automatically canceled.
//                // FIXME : これはここでいいのか？
//                realm.close();
            }
        });
    }

    public static RealmResults<ListItemData> selectAllItem(Realm realm) {
        RealmResults<ListItemData> realmResults = null;
        try {
            realmResults = realm.where(ListItemData.class).findAll()
                    .sort("id", Sort.ASCENDING);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return realmResults;
    }

    public static RealmResults<ListItemData> selectAllItemAsync(Realm realm, String key, Sort sort) {
        RealmResults<ListItemData> realmResults = null;
        try {
            realmResults = realm.where(ListItemData.class).findAllAsync()
                    .sort(key, sort);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return realmResults;
    }

    public static RealmResults<ListItemData> selectListItemById(Realm realm, long id) {
        RealmResults<ListItemData> realmResults = null;
        try {
            realmResults = realm.where(ListItemData.class).equalTo("id", id).findAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return realmResults;
    }

    public static void updateInputItem(Realm realm, final RealmObject realmResults) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // This will create a new object in Realm or throw an exception if the
                // object already exists (same primary key)
                // realm.copyToRealm(obj);
//                ((ListItemData) realmResults.get(0)).setMemo(body);

                // This will update an existing object with the same primary key
                // or create a new object if an object with no primary key = 42
                realm.copyToRealmOrUpdate(realmResults);
            }
        });
    }

    // FIXME MEMOに依存している、こういうのDIで解決できる？
    public static void updateHistoryMemoAsync(Realm realm, final long id, final String body, final realmTransactionCallbackListener realmTransactionCallbackListener) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // This will create a new object in Realm or throw an exception if the
                // object already exists (same primary key)
                // realm.copyToRealm(obj);
                RealmResults realmResults = RealmUtil.selectListItemById(Realm.getDefaultInstance(), id);
                if (realmResults.size() == 1) {
//                    ((ListItemData) realmResults.get(0)).setMemo(body);

                    // This will update an existing object with the same primary key
                    // or create a new object if an object with no primary key = 42
                    realm.copyToRealmOrUpdate(realmResults);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (realmTransactionCallbackListener != null) {
                    realmTransactionCallbackListener.OnSuccess();
                }
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
                if (realmTransactionCallbackListener != null) {
                    realmTransactionCallbackListener.OnError();
                }
            }
        });
    }

    public static void copyToRealmObject(ListItemData from, ListItemData to) {
//        to.setStartDateTime(from.getStartDateTime());
//        to.setEndDateTime(from.getEndDateTime());
//        to.setStepCount(from.getStepCount());
//        to.setStepCountAlert(from.getStepCountAlert());
    }

    public static void deleteInputItem(Realm realm, long id) {
        final RealmResults<ListItemData> historyItemModels = selectListItemById(realm, id);
        // All changes to data must happen in a transaction
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // remove single match
                historyItemModels.deleteFirstFromRealm();
            }
        });
    }

    public static boolean hasHistoryItem(Realm realm) {
        RealmResults<ListItemData> realmResults = RealmUtil.selectAllItem(realm);
        return realmResults != null && !realmResults.isEmpty();
    }

    public interface realmTransactionCallbackListener {
        void OnSuccess();

        void OnError();
    }
}

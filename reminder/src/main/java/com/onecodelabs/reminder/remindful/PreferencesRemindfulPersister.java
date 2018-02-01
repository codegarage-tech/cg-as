package com.onecodelabs.reminder.remindful;

import android.app.Application;
import android.content.Context;

import com.onecodelabs.reminder.bundle.ReminderBundle;
import com.onecodelabs.reminder.util.StorageUtils;

public class PreferencesRemindfulPersister implements RemindfulPersister {

    private static final String PREFERENCES_NAME = "ReminderSharedPreferences";
    private static final int PREFERENCES_MODE = Context.MODE_PRIVATE;

    private final StorageUtils storageUtils;

    public static PreferencesRemindfulPersister init(Application application) {
        StorageUtils.init(application);
        StorageUtils storageUtils = StorageUtils.begin(PREFERENCES_NAME, PREFERENCES_MODE);
        return new PreferencesRemindfulPersister(storageUtils);
    }

    public PreferencesRemindfulPersister(StorageUtils storageUtils) {
        this.storageUtils = storageUtils;
    }

    @Override
    public void save(String id, ReminderBundle bundle) {
        storageUtils.put(id, bundle);
        storageUtils.commit();
    }

    @Override
    public ReminderBundle get(String id) {
        return storageUtils.get(id, ReminderBundle.class);
    }

    @Override
    public void deleteAll() {
        storageUtils.clear();
        storageUtils.commit();
    }

}

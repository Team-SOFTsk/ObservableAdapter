package sk.teamsoft.observableadapterdemo;

import android.app.Application;

import timber.log.Timber;

/**
 * @author Dusan Bartos
 *         Created on 19.05.2017.
 */

public class DemoApp extends Application {

    @Override public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }
}

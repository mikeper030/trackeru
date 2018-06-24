package org.ultimatetoolsil.trackeru;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by mike on 29 Jan 2018.
 */

public class app extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}

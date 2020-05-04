package ir.ahnaei.telegramcleaner;

import android.app.Application;

import me.cheshmak.android.sdk.core.Cheshmak;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import static ir.ahnaei.telegramcleaner.config.CHESHMACK_TOKEN;

public class app extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(
                new CalligraphyConfig.Builder()
                        .setDefaultFontPath("a.ttf")
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );

        Cheshmak.with(this);
        Cheshmak.initTracker(CHESHMACK_TOKEN);

    }
}

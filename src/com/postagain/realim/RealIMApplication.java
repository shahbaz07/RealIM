package com.postagain.realim;

import android.app.Application;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.parse.Parse;
import com.parse.ParseObject;
import com.postagain.realim.model.ChatMessage;

public class RealIMApplication extends Application {

	private static final String APPLICATION_ID = "jjFBUf5TeeK5fDiF6HX9A3TaQ2GcNYREaSlAaQPl";
	private static final String CLIENT_KEY = "aC1QvAXp32FlnQosYUl0OPLofzC0OB28F8mAtxXF";
    
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(ChatMessage.class);
        Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);
        
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
        .cacheOnDisk(true)
		.cacheInMemory(true)
		.imageScaleType(ImageScaleType.EXACTLY)
		.displayer(new FadeInBitmapDisplayer(300)).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.defaultDisplayImageOptions(defaultOptions)
				.memoryCache(new WeakMemoryCache())
				.diskCacheSize(100 * 1024 * 1024).build();
		
		ImageLoader.getInstance().init(config);
    }
}

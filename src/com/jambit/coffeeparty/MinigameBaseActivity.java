package com.jambit.coffeeparty;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.ui.activity.BaseGameActivity;


public class MinigameBaseActivity extends BaseGameActivity {

	private Camera mCamera;
	private static final int CAMERA_WIDTH = 800;
    private static final int CAMERA_HEIGHT = 480;
	
    @Override
	public Engine onLoadEngine() {
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera));
	}

	@Override
	public void onLoadResources() {
		// TODO Auto-generated method stub

	}

	@Override
	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());
		 
        final Scene scene = new Scene(1);
        scene.setBackground(new ColorBackground(0, 0, 0.8784f));
        return scene;
	}

	@Override
	public void onLoadComplete() {
		// TODO Auto-generated method stub

	}
	

}

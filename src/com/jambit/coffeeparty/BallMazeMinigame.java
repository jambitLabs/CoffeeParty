package com.jambit.coffeeparty;

import org.anddev.andengine.entity.Entity;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.SpriteBackground;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.util.Vector2Pool;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.sensor.accelerometer.AccelerometerData;
import org.anddev.andengine.sensor.accelerometer.IAccelerometerListener;

import android.hardware.SensorManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public class BallMazeMinigame extends MinigameBaseActivity implements IAccelerometerListener {

    private BitmapTextureAtlas bitmapTextureAtlas;
    private BitmapTextureAtlas ballTextureAtlas;
    private TextureRegion backgroundTexture;
    private TextureRegion ballSpriteTexture;
    private SensorManager sensorManager;
    private int accellerometerSpeedX;
    private int accellerometerSpeedY;
    private Sprite ballSprite;
    private int sX, sY;
    private PhysicsWorld mPhysicsWorld;
    private Entity pAccelerometerData;

    private static final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);

    @Override
    public Scene onLoadScene() {

        // final Shape ground = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH, 2);

        // sensorManager = (SensorManager) this.getSystemService(this.SENSOR_SERVICE);
        // sensorManager.registerListener(this,
        // sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
        // sensorManager.SENSOR_DELAY_GAME);
        //
        //
        // this.mEngine.registerUpdateHandler(new IUpdateHandler() {
        // public void onUpdate(float pSecondsElapsed) {
        // updateSpritePosition();
        // }
        //
        // public void reset() {
        // // TODO Auto-generated method stub
        // }
        // });
        Scene scene = super.onLoadScene();
        Sprite backgroundSprite = new Sprite(0, 0, backgroundTexture);

        scene.setBackground(new SpriteBackground(backgroundSprite));
        this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);

        final Body ballBody;

        ballSprite = new Sprite(100, 100, ballSpriteTexture);
        ballBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, ballSprite, BodyType.DynamicBody, FIXTURE_DEF);

        scene.attachChild(ballSprite);
        this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(ballSprite, ballBody, true, true));

        final Shape ground = new Rectangle(0, cameraHeight - 2, cameraWidth, 2);
        final Shape roof = new Rectangle(0, 0, cameraWidth, 2);
        final Shape left = new Rectangle(0, 0, 2, cameraHeight);
        final Shape right = new Rectangle(cameraWidth - 2, 0, 2, cameraHeight);

        final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef);

        scene.attachChild(ground);
        scene.attachChild(roof);
        scene.attachChild(left);
        scene.attachChild(right);

        scene.registerUpdateHandler(this.mPhysicsWorld);

        return scene;
    }

    protected void updateSpritePosition() {

        if ((accellerometerSpeedX != 0) || (accellerometerSpeedY != 0)) {
            // Set the Boundary limits
            int tL = 0;
            int lL = 0;
            int rL = cameraWidth - (int) ballSprite.getWidth();
            int bL = cameraHeight - (int) ballSprite.getHeight();

            // Calculate New X,Y Coordinates within Limits
            if (sX >= lL)
                sX += accellerometerSpeedX;
            else
                sX = lL;
            if (sX <= rL)
                sX += accellerometerSpeedX;
            else
                sX = rL;
            if (sY >= tL)
                sY += accellerometerSpeedY;
            else
                sY = tL;
            if (sY <= bL)
                sY += accellerometerSpeedY;
            else
                sY = bL;

            // Double Check That New X,Y Coordinates are within Limits
            if (sX < lL)
                sX = lL;
            else if (sX > rL)
                sX = rL;
            if (sY < tL)
                sY = tL;
            else if (sY > bL)
                sY = bL;

            ballSprite.setPosition(sX, sY);
        }

    }

    @Override
    public void onLoadResources() {

        this.bitmapTextureAtlas = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR);
        ballTextureAtlas = new BitmapTextureAtlas(128, 128, TextureOptions.BILINEAR);

        this.backgroundTexture = (TextureRegion) BitmapTextureAtlasTextureRegionFactory.createFromAsset(bitmapTextureAtlas,
                                                                                                        this,
                                                                                                        "ballmaze.png",
                                                                                                        0,
                                                                                                        0);

        this.ballSpriteTexture = (TextureRegion) BitmapTextureAtlasTextureRegionFactory.createFromAsset(ballTextureAtlas,
                                                                                                        this,
                                                                                                        "kugel.png",
                                                                                                        0,
                                                                                                        0);

        this.mEngine.getTextureManager().loadTexture(this.bitmapTextureAtlas);
        this.mEngine.getTextureManager().loadTexture(this.ballTextureAtlas);
        super.onLoadResources();
    }

    @Override
    public void onAccelerometerChanged(AccelerometerData pAccelerometerData) {
        final Vector2 gravity = Vector2Pool.obtain(pAccelerometerData.getX(), pAccelerometerData.getY());
        this.mPhysicsWorld.setGravity(gravity);
        Vector2Pool.recycle(gravity);

    }

    @Override
    public void onResumeGame() {
        super.onResumeGame();

        this.enableAccelerometerSensor(this);
    }

    @Override
    public void onPauseGame() {
        super.onPauseGame();

        this.disableAccelerometerSensor();
    }

}

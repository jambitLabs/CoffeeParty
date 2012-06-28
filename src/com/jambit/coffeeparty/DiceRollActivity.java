package com.jambit.coffeeparty;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.Entity;
import org.anddev.andengine.entity.primitive.Line;
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
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;

public class DiceRollActivity extends BaseGameActivity implements SensorEventListener, ContactListener {

    private SensorManager sensorManager;
    private Sensor accSensor;
    private final float RESULT_THRESHOLD = 15;
    private static final float RANDOMIZE_THRESHOLD = 10;
    private static final float BASE_GRAVITY = 5;
    private int result = 0;

    private SoundPool soundPool;
    private int soundID;

    private PhysicsWorld mPhysicsWorld;
    private Sprite diceSprite;
    private TextureRegion diceSpriteTexture;
    private TextureRegion backgroundTexture;
    private int cameraWidth;
    private int cameraHeight;
    private Camera mCamera;
    private BitmapTextureAtlas backgroundTextureAtlas;
    private BitmapTextureAtlas diceTextureAtlas;

    private Sprite cupSprite;
    private TextureRegion cupTexture;
    private BitmapTextureAtlas cupTextureAtlas;
    private Sprite cupSpriteBg;

    private static final FixtureDef DICE_FIXTURE_DEF = PhysicsFactory.createFixtureDef(1f, 0.2f, 0.2f);

    private static final FixtureDef WALL_FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.2f, 0.2f);
    private float[] gravities = { 0f, 0f, 0f };
    private List<Body> walls = new ArrayList();
    private boolean disableTrigger = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);

        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);

        soundID = soundPool.load(this, R.raw.roll_dice, 1);

    }

    public void onDestroy(Bundle savedInstanceState) {
        sensorManager.unregisterListener(this);
    }

    public void onSensorChanged(SensorEvent event) {
        String s = "Value 0: " + event.values[0] + " Value 1: " + event.values[1] + " Value 2: " + event.values[2];
        Log.d("T", s);

        if (mPhysicsWorld != null) {
            final float alpha = 0.8f;

            gravities[0] = alpha * gravities[0] + (1 - alpha) * event.values[0];
            gravities[1] = alpha * gravities[1] + (1 - alpha) * event.values[1];
            gravities[2] = alpha * gravities[2] + (1 - alpha) * event.values[2];

            float[] linear_acceleration = { 0f, 0f, 0f };
            linear_acceleration[0] = event.values[0] - gravities[0];
            linear_acceleration[1] = event.values[1] - gravities[1];
            linear_acceleration[2] = event.values[2] - gravities[2];

            final Vector2 virtualGravity = Vector2Pool.obtain(10f * linear_acceleration[1], 4f * linear_acceleration[0]
                    + BASE_GRAVITY);
            this.mPhysicsWorld.setGravity(virtualGravity);
            Vector2Pool.recycle(virtualGravity);
        }
    }

    private void rollTriggered() {
        disableTrigger = true;
        Random r = new Random();
        result = r.nextInt(6) + 1;
        // Button b = (Button) findViewById(R.id.dice_roll_confirmbutton);
        // b.setVisibility(Button.VISIBLE);
        // TextView rtv = (TextView) findViewById(R.id.dice_roll_result_textview);
        // rtv.setText("" + result);
        // rtv.setVisibility(ImageView.VISIBLE);
        // TextView tv = (TextView) findViewById(R.id.dice_roll_textview);
        // tv.setVisibility(View.INVISIBLE);

        playRollSound();
        // can't change the outcome if you don't like it!
        sensorManager.unregisterListener(this);
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast toast = Toast.makeText(DiceRollActivity.this, "Result: " + result, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();

            }
        });

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                finishDiceRoll();
            }
        }, 3000);
    }

    private void playRollSound() {
        AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = streamVolumeCurrent / streamVolumeMax;

        /* Play the sound with the correct volume */
        soundPool.play(soundID, volume, volume, 1, 0, 1f);
    }

    public void finishDiceRoll() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(getString(R.string.dice_result), result);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not interested
    }

    @Override
    public Engine onLoadEngine() {
        final Display display = getWindowManager().getDefaultDisplay();
        cameraWidth = display.getWidth();
        cameraHeight = display.getHeight();
        this.mCamera = new Camera(0, 0, 800, 480);
        return new Engine(new EngineOptions(true,
                                            ScreenOrientation.LANDSCAPE,
                                            new RatioResolutionPolicy(cameraWidth, cameraHeight),
                                            this.mCamera));
    }

    @Override
    public void onLoadResources() {
        this.backgroundTextureAtlas = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR);
        diceTextureAtlas = new BitmapTextureAtlas(512, 512, TextureOptions.BILINEAR);

        this.backgroundTexture = (TextureRegion) BitmapTextureAtlasTextureRegionFactory.createFromAsset(backgroundTextureAtlas,
                                                                                                        this,
                                                                                                        "craps_table_bg.jpg",
                                                                                                        0,
                                                                                                        0);

        this.diceSpriteTexture = (TextureRegion) BitmapTextureAtlasTextureRegionFactory.createFromAsset(diceTextureAtlas,
                                                                                                        this,
                                                                                                        "wuerfelmitbohnen.png",
                                                                                                        0,
                                                                                                        0);
        cupTextureAtlas = new BitmapTextureAtlas(512, 512, TextureOptions.BILINEAR);

        this.cupTexture = (TextureRegion) BitmapTextureAtlasTextureRegionFactory.createFromAsset(cupTextureAtlas,
                                                                                                 this,
                                                                                                 "wuerfelbecher.gif",
                                                                                                 0,
                                                                                                 0);

        this.mEngine.getTextureManager().loadTexture(this.cupTextureAtlas);

        this.mEngine.getTextureManager().loadTexture(this.backgroundTextureAtlas);
        this.mEngine.getTextureManager().loadTexture(this.diceTextureAtlas);

    }

    @Override
    public Scene onLoadScene() {
        Scene scene = new Scene();
        Sprite backgroundSprite = new Sprite(0, 0, backgroundTexture);

        scene.setBackground(new SpriteBackground(backgroundSprite));
        this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);
        mPhysicsWorld.setContactListener(this);
        final Body diceBody;

        diceSprite = new Sprite(200, 200, diceSpriteTexture);
        diceSprite.setScale(0.3f);
        diceSprite.setZIndex(2);
        diceBody = PhysicsFactory.createCircleBody(this.mPhysicsWorld,
                                                   diceSprite.getX(),
                                                   diceSprite.getY(),
                                                   (diceSprite.getWidth() - 300) / 2.f,
                                                   0.f,
                                                   BodyType.DynamicBody,
                                                   DICE_FIXTURE_DEF);

        cupSprite = new Sprite(100, 200, cupTexture);
        cupSpriteBg = new Sprite(100, 200, cupTexture);

        cupSprite.setAlpha(0.7f);
        cupSprite.setZIndex(3);
        scene.attachChild(cupSprite);

        cupSpriteBg.setZIndex(1);
        scene.attachChild(cupSpriteBg);

        this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(diceSprite, diceBody, true, true));
        scene.attachChild(diceSprite);

        // outer walls
        walls.add(createWall(0, cameraHeight - 2, cameraWidth, 2, scene, WALL_FIXTURE_DEF));
        walls.add(createWall(0, 0, cameraWidth, 2, scene, WALL_FIXTURE_DEF));
        walls.add(createWall(0, 0, 2, cameraHeight, scene, WALL_FIXTURE_DEF));
        walls.add(createWall(cameraWidth - 2, 0, 2, cameraHeight, scene, WALL_FIXTURE_DEF));

        createLineBody(100, 200, 125, 450, scene);
        createLineBody(300, 200, 275, 450, scene);
        createLineBody(125, 450, 275, 450, scene);

        scene.registerUpdateHandler(this.mPhysicsWorld);
        return scene;
    }

    private Body createLineBody(float pX1, float pY1, float pX2, float pY2, Entity scene) {
        final Line pLine = new Line(pX1, pY1, pX2, pY2);
        Body line = PhysicsFactory.createLineBody(mPhysicsWorld, pLine, WALL_FIXTURE_DEF);
        return line;
    }

    private Body createWall(int x, int y, int dx, int dy, Scene scene, FixtureDef wallFixtureDef) {

        final Shape newWall = new Rectangle(x, y, dx, dy);
        Body wall = PhysicsFactory.createBoxBody(this.mPhysicsWorld, newWall, BodyType.StaticBody, wallFixtureDef);
        scene.attachChild(newWall);
        return wall;
    }

    @Override
    public void onLoadComplete() {
        // TODO Auto-generated method stub

    }

    @Override
    public void beginContact(Contact contact) {
        playRollSound();
        if (walls.contains(contact.getFixtureA().getBody()) || walls.contains(contact.getFixtureB().getBody())) {
            // end dice activity
            if (!disableTrigger) {
                disableAccelerometerSensor();
                final Vector2 virtualGravity = Vector2Pool.obtain(0f, BASE_GRAVITY);
                this.mPhysicsWorld.setGravity(virtualGravity);
                Vector2Pool.recycle(virtualGravity);
                rollTriggered();
            }
        }

    }

    @Override
    public void endContact(Contact contact) {
        // TODO Auto-generated method stub

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        // TODO Auto-generated method stub

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        // TODO Auto-generated method stub

    }
}

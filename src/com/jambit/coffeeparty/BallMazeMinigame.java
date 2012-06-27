package com.jambit.coffeeparty;

import java.util.ArrayList;
import java.util.List;

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
import android.util.Log;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;

public class BallMazeMinigame extends MinigameBaseActivity implements IAccelerometerListener, ContactListener {

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
    private static final FixtureDef Hole_FIXTURE_DEF = PhysicsFactory.createFixtureDef(0f, 0f, 0f, true);
    private static final float INITIAL_SCORE = 20;

    private List<Body> holeBodies = new ArrayList<Body>();
    private Body goalBody;

    @Override
    public Scene onLoadScene() {

        Scene scene = super.onLoadScene();
        Sprite backgroundSprite = new Sprite(0, 0, backgroundTexture);

        scene.setBackground(new SpriteBackground(backgroundSprite));
        this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);
        mPhysicsWorld.setContactListener(this);

        final Body ballBody;

        ballSprite = new Sprite(100, 100, ballSpriteTexture);
        ballBody = PhysicsFactory.createCircleBody(this.mPhysicsWorld,
                                                   ballSprite.getX(),
                                                   ballSprite.getY(),
                                                   ballSprite.getWidth() / 2.f,
                                                   0.f,
                                                   BodyType.DynamicBody,
                                                   FIXTURE_DEF);

        this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(ballSprite, ballBody, true, true));
        scene.attachChild(ballSprite);

        final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);

        // outer walls
        createWall(0, cameraHeight - 25, cameraWidth, 25, scene, wallFixtureDef);
        createWall(0, 0, cameraWidth, 25, scene, wallFixtureDef);
        createWall(0, 0, 25, cameraHeight, scene, wallFixtureDef);
        createWall(cameraWidth - 25, 0, 25, cameraHeight, scene, wallFixtureDef);

        //
        createWall(90, 25, 40, 60, scene, wallFixtureDef);
        createWall(160, 65, 150, 35, scene, wallFixtureDef);
        createWall(108, 120, 32, 230, scene, wallFixtureDef);
        createWall(33, 325, 33, 90, scene, wallFixtureDef);
        createWall(245, 205, 271, 75, scene, wallFixtureDef);
        createWall(401, 285, 52, 81, scene, wallFixtureDef);
        createWall(465, 94, 194, 42, scene, wallFixtureDef);
        createWall(599, 211, 69, 100, scene, wallFixtureDef);
        createWall(508, 356, 102, 40, scene, wallFixtureDef);
        createWall(582, 404, 45, 45, scene, wallFixtureDef);

        createHole(38, 184, scene, Hole_FIXTURE_DEF);

        goalBody = PhysicsFactory.createCircleBody(this.mPhysicsWorld,
                                                   679,
                                                   418,
                                                   15,
                                                   0.f,
                                                   BodyType.StaticBody,
                                                   Hole_FIXTURE_DEF);

        scene.registerUpdateHandler(this.mPhysicsWorld);

        return scene;
    }

    private void createHole(int x, int y, Scene scene, FixtureDef holeFixtureDef) {

        Body holeBody = PhysicsFactory.createCircleBody(this.mPhysicsWorld,
                                                        x,
                                                        y,
                                                        15,
                                                        0.f,
                                                        BodyType.StaticBody,
                                                        holeFixtureDef);
        holeBodies.add(holeBody);

    }

    private void createWall(int x, int y, int dx, int dy, Scene scene, FixtureDef wallFixtureDef) {

        final Shape newWall = new Rectangle(x, y, dx, dy);
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, newWall, BodyType.StaticBody, wallFixtureDef);
        // scene.attachChild(newWall);
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

        setScore((int) Math.floor(getFractionOfPassedTime() * INITIAL_SCORE));
        updateScoreDisplay();
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

    @Override
    public void beginContact(Contact contact) {
        Log.i("BallMazeMinigame", "Contact detected: " + contact.toString());
        if (contact.getFixtureA().getBody() == goalBody || contact.getFixtureB().getBody() == goalBody) {
            setScore((int) Math.floor(getFractionOfPassedTime() * INITIAL_SCORE));
            onGameFinished();
        }
        if (holeBodies.contains(contact.getFixtureA().getBody())
                || holeBodies.contains(contact.getFixtureB().getBody())) {
            setScore(0);
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

    @Override
    public void onLoadComplete() {
        startCountDownTimer(20);
    }
}

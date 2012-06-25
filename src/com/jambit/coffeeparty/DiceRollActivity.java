package com.jambit.coffeeparty;

import java.util.Random;

import android.app.Activity;
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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DiceRollActivity extends Activity implements SensorEventListener{

    private SensorManager sensorManager;
    private Sensor accSensor;
    private final int THRESHOLD = 10;
    private int result = 0;
    
    private SoundPool soundPool;
    private int soundID;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dice_roll_layout);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        
        accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
        
        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
        
        soundID = soundPool.load(this, R.raw.roll_dice, 1);
    }
    public void onDestroy(Bundle savedInstanceState) {
        sensorManager.unregisterListener(this);
    }
    
    public void onSensorChanged(SensorEvent event){
        String s = "Value 0: " + event.values[0] +
                " Value 1: " + event.values[1] + 
                " Value 2: " + event.values[2];
        Log.d("T", s);
        if (event.values[0] > THRESHOLD || event.values[1] > THRESHOLD || event.values[2] > THRESHOLD) {
           this.rollTriggered(); 
        }
    }
    private void rollTriggered() {
        Random r = new Random();
        result = r.nextInt(6) + 1;
        Button b = (Button) findViewById(R.id.dice_roll_confirmbutton);
        b.setVisibility(Button.VISIBLE);
        TextView rtv = (TextView) findViewById(R.id.dice_roll_result_textview);
        rtv.setText(""+result);
        rtv.setVisibility(ImageView.VISIBLE);
        TextView tv = (TextView) findViewById(R.id.dice_roll_textview);
        tv.setVisibility(View.INVISIBLE);
        playRollSound();
        //can't change the outcome if you don't like it!
        sensorManager.unregisterListener(this);
    }
    
    private void playRollSound () {
        AudioManager mgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);    
        float volume = streamVolumeCurrent / streamVolumeMax;
        
        /* Play the sound with the correct volume */
        soundPool.play(soundID, volume, volume, 1, 0, 1f);
    }
    
    public void onConfirmClicked(View v) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(getString(R.string.dice_result), result);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
    
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //not interested
    }
}

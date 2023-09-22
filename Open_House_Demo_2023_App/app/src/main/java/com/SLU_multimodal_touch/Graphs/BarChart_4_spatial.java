package com.SLU_multimodal_touch.Graphs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.Manifest;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class BarChart_4_spatial extends AppCompatActivity {

    /**************************************************************************************
     * DISABLE THE BACK BUTTON
     **************************************************************************************/
    @Override
    public void onBackPressed() {
        // Do nothing
    }

    /**************************************************************************************
     * Global Variables
     **************************************************************************************/
    // TTS and the multi-finger gesture tracker
    TextToSpeech tts; // Initiate the TextToSpeech object
    SimpleFingerGestures_Mod sfg = new SimpleFingerGestures_Mod(); //SimpleFingerGestures_Mod object

    // To get screen dimensions, just in case
    int screen_width = Resources.getSystem().getDisplayMetrics().widthPixels;
    int screen_height = Resources.getSystem().getDisplayMetrics().heightPixels;

    // Define area colors
    int inside_red = 237;
    int inside_green = 125;
    int inside_blue = 49;

    // Define area colors for TOP BAR
    int top_bar_red = 0;
    int top_bar_green = 112;
    int top_bar_blue = 192;

    // For Vibration Control
    VibrationManager vib = new VibrationManager();
    int vib_freq = 0;
    final int vib_freq_inside = 5; // Hz


    // To track WHICH finger is on screen
    int f0_index = -1;
    int f1_index = -1;
    int f2_index = -1;
    int f3_index = -1;
    int f4_index = -1;

    // For SoundPool control
    private SoundPool soundPool;
    private int empty_sound_id;
    private int empty_sound_stream_id;
    private int bells_sound_id;
    private int bells_sound_stream_id;
    private int healing_sound_id;
    private int healing_sound_stream_id;
    int priority;
    int loop = -1; // Loop forever
    int bells_loop = 0; // loop twice
    int healing_loop = 2; // loop twice
    private Boolean empty_sound_is_playing = false;
    private Boolean bells_sound_is_playing = false;
    private Boolean healing_sound_is_playing = false;
    private Boolean spatial_audio_activated = true;
    float pitch_threshold = (float) 0.01;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Specify which layout this activity uses
        setContentView(R.layout.barchart_4_spatial);

        /**************************************************************************************
         * SET THE BACKGROUND IMAGE FOR EACH ORIENTATION
         **************************************************************************************/

        // Get a reference to the ImageView, based on the phone orientation
        ImageView mv = findViewById(R.id.mt_view);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape
            mv.setImageResource(R.drawable.bar_chart_2);
        } else {
            // In portrait
            mv.setImageResource(R.drawable.bar_chart_2);
        }

        /**************************************************************************************
         * LOCK THE ORIENTATION, HIDE NAVIGATION BUTTONS, AND START LOCK TASK MODE
         **************************************************************************************/
        // Lock the orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // Hide the navigation buttons
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        // Start Lock Task mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startLockTask();
        }

        /**************************************************************************************
         * TEXT TO SPEECH (TTS) CONFIGURATION
         **************************************************************************************/
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.ENGLISH);
                }
            }
        });
        // Set the speed of the TTS
        tts.setSpeechRate(1);

        /**************************************************************************************
         * VIBRATION MANAGER
         **************************************************************************************/
        //Tell the VibrationManager that THIS is the activity we are referencing
        vib.setActivity(this); // "this" references this entire activity


        /**************************************************************************************
         * MULTI-TOUCH LIBRARY
         **************************************************************************************/
        // Taken from: https://github.com/championswimmer/SimpleFingerGestures_Android_Library/blob/master/sample/src/main/java/in/championswimmer/sfg/sample/MainActivity.java
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        sfg.setDebug(false);
        sfg.setConsumeTouchEvents(true);

        sfg.setOnFingerGestureListener(new SimpleFingerGestures_Mod.OnFingerGestureListener() {
            @Override
            public boolean onSwipeUp(int fingers, long gestureDuration, double gestureDistance) {
                if (fingers == 1) {
                    //Write your code here for swiping UP with one finger
                }
                else if (fingers == 2) {
                    // Write your code here for swiping UP with 2 fingers
                }
                else if (fingers == 3) {
                    // Write your code here for swiping UP with 3 fingers
                }
                else if (fingers == 4) {
                    // Write your code here for swiping UP with 4 fingers
                    // Stop and CLOSE all sounds and vibrations
                    soundPool.stop(empty_sound_stream_id);
                    empty_sound_is_playing = false;
                    soundPool.unload(empty_sound_id);
                    vib.stop();

                    // Go back to GraphSelector
                    Intent back_to_graph_selector = new Intent(BarChart_4_spatial.this, GraphSelector.class);
                    startActivity(back_to_graph_selector);
                }
                else if (fingers == 5) {
                    // Write your code here for swiping UP with 4 fingers
                }
                return false;
            }

            @Override
            public boolean onSwipeDown(int fingers, long gestureDuration, double gestureDistance) {
                if (fingers == 1) {
                    //Write your code here for swiping DOWN with one finger
                }
                else if (fingers == 2) {
                    // Write your code here for swiping DOWN with 2 fingers
                }
                else if (fingers == 3) {
                    // Write your code here for swiping DOWN with 3 fingers
                }
                else if (fingers == 4) {
                    // Write your code here for swiping DOWN with 4 fingers
                }
                return false;
            }

            @Override
            public boolean onSwipeLeft(int fingers, long gestureDuration, double gestureDistance) {
                if (fingers == 1) {
                    //Write your code here for swiping LEFT with one finger
                }
                else if (fingers == 2) {
                    // Write your code here for swiping LEFT with 2 fingers
                }
                else if (fingers == 3) {
                    // Write your code here for swiping LEFT with 3 fingers
                }
                else if (fingers == 4) {
                    // Write your code here for swiping LEFT with 4 fingers
                }
                return false;
            }

            @Override
            public boolean onSwipeRight(int fingers, long gestureDuration, double gestureDistance) {
                if (fingers == 1) {
                    //Write your code here for swiping RIGHT with one finger
                }
                else if (fingers == 2) {
                    // Write your code here for swiping RIGHT with 2 fingers
                }
                else if (fingers == 3) {
                    // Write your code here for swiping RIGHT with 3 fingers
                }
                else if (fingers == 4) {
                    // Write your code here for swiping RIGHT with 4 fingers
                }
                return false;
            }

            @Override
            public boolean onPinch(int fingers, long gestureDuration, double gestureDistance) {
                if (fingers == 2) {
                    // Write your code here for PINCHING with 2 fingers
                }
                else if (fingers == 3) {
                    // Write your code here for PINCHING with 3 fingers
                }
                else if (fingers == 4) {
                    // Write your code for PINCHING with 4 fingers

                }
                return false;
            }

            @Override
            public boolean onUnpinch(int fingers, long gestureDuration, double gestureDistance) {
                if (fingers == 2) {
                    // Write your code here for PINCHING with 2 fingers
                }
                else if (fingers == 3) {
                    // Write your code here for PINCHING with 3 fingers
                }
                else if (fingers == 4) {
                    // Write your code here for PINCHING with 4 fingers
                }
                return false;
            }

            @Override
            public boolean onDoubleTap(int fingers) {
                // Write your code here for DOUBLE TAPPING
                return false;
            }

        });

        // Set the OnTouch (no TalkBack) and onHover (with TalkBack) listeners
        mv.setOnTouchListener(sfg);
        mv.setOnHoverListener(new HoverToTouchAdapter(sfg));

        // Initiate the textView objects for the screen and for each finger
        TextView coord_view = findViewById(R.id.coordinate_view);
        TextView output_view = findViewById(R.id.output_view);

        // Now for the code to be executed constantly during the duration of this activity
        monitor(mv, coord_view, output_view);
    }

    /**************************************************************************************
     * CONSTANTLY RUNNING CODE
     **************************************************************************************/
    public void monitor(ImageView mv, TextView coord_view, TextView output_view) {
        // Variables to be used in any part of this function
        final Handler handler = new Handler();

        /**************************************************************************************
         * SOUNDPOOL CONFIGURATION
         **************************************************************************************/
        // Give each of the raw files an ID to be used by the SoundPool later on
        soundPool = new SoundPool(10,AudioManager.STREAM_MUSIC, 0);
        empty_sound_id = soundPool.load(this, R.raw.waves_trim, 1);

        bells_sound_id = soundPool.load(this, R.raw.bells_sound, 1);
        healing_sound_id = soundPool.load(this, R.raw.waves_trim, 1);

        // Write your constantly running code here
        handler.post(new Runnable() {
            @Override
            public void run() {
                // Your constantly running code here:

                // Finger status/location indicator
                //coord_view.setText("There are " + sfg.finger_count + " finger(s) on screen.\nTheir X coordinates are " + Arrays.toString(sfg.X_coords) + "\nTheir Y coordinates are " + Arrays.toString(sfg.Y_coords));


                /**************************************************************************************
                 * IMAGE SCALING PART 1. Based on: https://stackoverflow.com/questions/67078759/imageview-get-color-of-touched-pixel
                 **************************************************************************************/
                // Part 2 is done in later sections of the code, depending on the amount of fingers on screen
                // Obtain the VIEW and IMAGE dimensions. This will be used later to obtain the color of a pixel each finger is touching
                // Get the VIEW width and height
                double viewWidth = mv.getWidth();
                double viewHeight = mv.getHeight();
                // Get the IMAGE info as a bitmap
                Bitmap image = ((BitmapDrawable)mv.getDrawable()).getBitmap();
                // Get the IMAGE width and height
                double imageWidth = image.getWidth();
                double imageHeight = image.getHeight();

                /**************************************************************************************
                 * ZERO FINGERS
                 **************************************************************************************/
                // Reset all finder indexes
                f0_index = -1;
                f1_index = -1;
                f2_index = -1;
                f3_index = -1;
                f4_index = -1;
                if (sfg.finger_count == 0){
                    // Stop any ongoing vibrations
                    if (vib.isVibrating()) {
                        vib.stop();
                    }

                    // Clean the output view
                    output_view.setText("");

                    // Clean the coord view
                    coord_view.setText("");

                    // SoundPool
                    soundPool.stop(empty_sound_stream_id);
                    empty_sound_is_playing = false;

                    // stop bells sounds
                    soundPool.stop(bells_sound_stream_id);
                    bells_sound_is_playing = false;

                    // stop healing sounds
                    soundPool.stop(healing_sound_stream_id);
                    healing_sound_is_playing = false;
                }

                /**************************************************************************************
                 * ONE FINGER
                 **************************************************************************************/
                if (sfg.finger_count == 1){

                    // Get the coordinates and pixel color the fingers are touching, considering the different scaling factors of the VIEW and the IMAGE itself.

                    //First check WHICH FINGER is on screen!
                    // Scan the X_coords array to try and find which finger is on screen.
                    if (f0_index == -1) {
                        for (int i = 0; i < sfg.X_coords.length; i++) {
                            if (sfg.X_coords[i] != 0) {
                                f0_index = i;
                                break;
                            }
                        }
                    }
                    // If the finger was in X = 0.0, which means that finger_index is still -1, then scan  the Y_coords array to try and find which finger is on screen.
                    if (f0_index == -1) {
                        for (int i = 0; i < sfg.Y_coords.length; i++) {
                            if (sfg.Y_coords[i] != 0) {
                                f0_index = i;
                                break;
                            }
                        }
                    }
                    // IF the finger was found, THEN continue with the rest of the code. Otherwise, do nothing. The finger is in EXACTLY (0.0 , 0.0).
                    if (f0_index != -1) {
                        // Get the VIEW X and Y coords for FINGER0
                        double viewX = sfg.X_coords[f0_index];
                        double viewY = sfg.Y_coords[f0_index];
                        // Rule of 3 to scale the pixel values
                        double imageX = (viewX * (imageWidth / viewWidth));
                        double imageY = (viewY * (imageHeight / viewHeight));
                        // Limit the X and Y values to the bitmap max dimensions to avoid IllegalArgumentException when providing the pixel coordinate
                        if (imageX >= imageWidth) {
                            imageX = imageWidth - 0.01; // Modify it by 1/100 of a pixel to meet the imageX < bitmap.width condition!
                        }
                        else if (imageX < 0) {
                            imageX = 0; // Account for negative X values (bezels!)
                        }
                        if (imageY >= imageHeight) {
                            imageY = imageHeight - 0.01; // Modify it by 1/100 of a pixel to meet the imageY < bitmap.height condition!
                        }
                        else if (imageY < 0) {
                            imageY = 0; // Account for negative X values (bezels!)
                        }
                        // Get the pixel based on the IMAGE X and Y coordinates!
                        int pixel = image.getPixel((int)imageX, (int)imageY);
                        // Save the RGB values
                        int pixel_red = Color.red(pixel);
                        int pixel_green = Color.green(pixel);
                        int pixel_blue = Color.blue(pixel);
                        // coord_view.setText("COLOR = " + pixel_red + ", " + pixel_green + "," + pixel_blue);

                        // For AUDIO CHANNEL proportional control based on X axis coordinate
                        float audio_x = (float) (imageX / imageWidth);
                        float leftVolume = (1 - audio_x);
                        float rightVolume = audio_x;

                        // For AUDIO PITCH proportional control based on Y axis coordinate
                        float audio_y = (float) (imageY / imageHeight);
                        float pitch = (float) (1 - audio_y); // <1 = lower pitch, >1 = higher pitch. We use 1 - audio_y because y = 0 is the TOP of the screen, not the BOTTOM of the screen, and we want lower pitches at the BOTTOM of the screen
                        // Keep the pitch above the tablet threshold, otherwise we get an IllegalArgumentException
                        if (pitch < pitch_threshold) {
                            pitch = pitch_threshold;
                        }

                        // If finger is INSIDE the figure
                        if (pixel_red == inside_red && pixel_green == inside_green && pixel_blue == inside_blue) {
                            // Stop all other sounds
                            soundPool.stop(empty_sound_stream_id);
                            empty_sound_is_playing = false;

                            soundPool.stop(bells_sound_stream_id);
                            bells_sound_is_playing = false;

                            // play sound inside figure
                            if (!healing_sound_is_playing) {
                                if (!spatial_audio_activated) {
                                    // Start playing the sound at normal pitch, from both speakers
                                    healing_sound_stream_id = soundPool.play(healing_sound_id, (float) 1.0, (float) 1.0, priority, healing_loop, (float) 1.0);
                                }
                                else {
                                    // Start playing the sound at normal pitch, from both speakers
                                    healing_sound_stream_id = soundPool.play(healing_sound_id, leftVolume, rightVolume, priority, healing_loop, pitch);
                                }
                                healing_sound_is_playing = true;
                            }

                            // SPATIAL AUDIO constant modification
                            if (spatial_audio_activated) {
                                soundPool.setLoop(healing_sound_stream_id, loop);
                                soundPool.setVolume(healing_sound_stream_id, leftVolume, rightVolume);
                                soundPool.setRate(healing_sound_stream_id, pitch);
                            }

                            // Start vibrating
                            if (vib_freq != vib_freq_inside) {
                                // Stop the previous vibration if it is different from the one we are supposed to do
                                vib.stop();
                                // Update the vibration frequency
                                vib_freq = vib_freq_inside;
                                // This only happens ONCE when the vibration frequency CHANGES value, to avoid the motor having to STOPGOSTOPGOSTOPGOSTOPGO
                            }
                            if (!vib.isVibrating()) {
                                //vib.vibrateAtFrequencyForever(vib_freq);
                                vib.vibrateForever();
                            }

                            coord_view.setText("");
                        }

                        // If finger is INSIDE TOP BAR
                        else if (pixel_red == top_bar_red && pixel_green == top_bar_green && pixel_blue == top_bar_blue) {
                            // Stop all other sounds
                            soundPool.stop(empty_sound_stream_id);
                            empty_sound_is_playing = false;

                            soundPool.stop(healing_sound_stream_id);
                            healing_sound_is_playing = false;

                            // play sound on top bar
                            if (!bells_sound_is_playing) {
                                if (!spatial_audio_activated) {
                                    // Start playing the sound at normal pitch, from both speakers
                                    bells_sound_stream_id = soundPool.play(bells_sound_id, (float) 1.0, (float) 1.0, priority, bells_loop, (float) 1.0);
                                }
                                else {
                                    // Start playing the sound with spatial audio, without pitch modulation for the bell sound
                                    bells_sound_stream_id = soundPool.play(bells_sound_id, leftVolume, rightVolume, priority, bells_loop, (float) 1.0);
                                }
                                bells_sound_is_playing = true;
                            }
                            // SPATIAL AUDIO constant modification
                            if (spatial_audio_activated) {
                                soundPool.setLoop(bells_sound_stream_id, bells_loop);
                                soundPool.setVolume(bells_sound_stream_id, leftVolume, rightVolume);
                            }


                            // Start vibrating
                            if (!vib.isVibrating()) {
                                vib.vibrateForever();
                            }
                        }


                        // If finger is on EMPTY space
                        else {
                            // Stop all other sounds or vibrations
                            vib.stop();

                            /* // SoundPool output with or without Spatial Audio
                            if (!empty_sound_is_playing) {
                                if (!spatial_audio_activated) {
                                    // Start playing the sound at normal pitch, from both speakers
                                    empty_sound_stream_id = soundPool.play(empty_sound_id, (float) 1.0, (float) 1.0, priority, loop, (float) 1.0);
                                }
                                else {
                                    // Start playing the sound at normal pitch, from both speakers
                                    empty_sound_stream_id = soundPool.play(empty_sound_id, leftVolume, rightVolume, priority, loop, pitch);
                                }
                                empty_sound_is_playing = true;
                            }
                            // SPATIAL AUDIO constant modification
                            if (spatial_audio_activated) {
                                soundPool.setLoop(empty_sound_stream_id, loop);
                                soundPool.setVolume(empty_sound_stream_id, leftVolume, rightVolume);
                                soundPool.setRate(empty_sound_stream_id, pitch);
                            }
                            */
                            coord_view.setText("");
                            // stop bells sounds
                            soundPool.stop(bells_sound_stream_id);
                            bells_sound_is_playing = false;

                            // stop healing sounds
                            soundPool.stop(healing_sound_stream_id);
                            healing_sound_is_playing = false;
                        }
                    }
                }

                /**************************************************************************************
                 * TWO FINGERS
                 **************************************************************************************/
                if (sfg.finger_count >= 2){

                    /**************************************************************************************
                     * GET THE COORDINATES AND INDEXES FOR EACH FINGER
                     **************************************************************************************/

                    //First check WHICH FINGERS are on screen!
                    // Scan the X_coords and Y_coords arrays to find the first finger
                    if (f0_index == -1) {
                        for (int i = 0; i < sfg.X_coords.length; i++) {
                            if (sfg.X_coords[i] != 0) {
                                f0_index = i;
                                break;
                            }
                        }
                    }
                    // If the finger was in X = 0.0, which means that finger_index is still -1, then scan  the Y_coords array to try and find which finger is on screen.
                    if (f0_index == -1) {
                        for (int i = 0; i < sfg.Y_coords.length; i++) {
                            if (sfg.Y_coords[i] != 0) {
                                f0_index = i;
                                break;
                            }
                        }
                    }
                    // Scan the X_coords and Y_coords arrays to find the second finger
                    if (f1_index == -1) {
                        for (int i = 0; i < sfg.X_coords.length; i++) {
                            if (sfg.X_coords[i] != 0 && sfg.X_coords[i] != sfg.X_coords[f0_index]) {
                                f1_index = i;
                                break;
                            }
                        }
                    }
                    // If the finger was in X = 0.0, which means that finger_index is still -1, then scan  the Y_coords array to try and find which finger is on screen.
                    if (f1_index == -1) {
                        for (int i = 0; i < sfg.Y_coords.length; i++) {
                            if (sfg.Y_coords[i] != 0 && sfg.Y_coords[i] != sfg.Y_coords[f0_index]) {
                                f1_index = i;
                                break;
                            }
                        }
                    }

                    /**************************************************************************************
                     * FINGERS FOUND! PROCEED WITH THE REST OF THE CODE
                     **************************************************************************************/

                    // IF the fingers were found, THEN continue with the rest of the code. Otherwise, do nothing. The finger is in EXACTLY (0.0 , 0.0).
                    if (f0_index != -1 && f1_index != -1) {

                        /**************************************************************************************
                         * GET THE PIXELS UNDER THE FINGERS
                         **************************************************************************************/

                        // Get the VIEW X and Y coords for FINGER0
                        double viewX0 = sfg.X_coords[f0_index];
                        double viewY0 = sfg.Y_coords[f0_index];
                        // Rule of 3 to scale the pixel values
                        double imageX0 = (viewX0 * (imageWidth / viewWidth));
                        double imageY0 = (viewY0 * (imageHeight / viewHeight));
                        // Limit the X and Y values to the bitmap max dimensions to avoid IllegalArgumentException when providing the pixel coordinate
                        if (imageX0 >= imageWidth) {
                            imageX0 = imageWidth - 0.01; // Modify it by 1/100 of a pixel to meet the imageX < bitmap.width condition!
                        }
                        else if (imageX0 < 0) {
                            imageX0 = 0; // Account for negative X values (bezels!)
                        }
                        if (imageY0 >= imageHeight) {
                            imageY0 = imageHeight - 0.01; // Modify it by 1/100 of a pixel to meet the imageY < bitmap.height condition!
                        }
                        else if (imageY0 < 0) {
                            imageY0 = 0; // Account for negative X values (bezels!)
                        }
                        // Get the pixel based on the IMAGE X and Y coordinates!
                        int pixel0 = image.getPixel((int)imageX0, (int)imageY0);
                        // Save the RGB values
                        int pixel0_red = Color.red(pixel0);
                        int pixel0_green = Color.green(pixel0);
                        int pixel0_blue = Color.blue(pixel0);

                        // Get the VIEW X and Y coords for FINGER1
                        double viewX1 = sfg.X_coords[f1_index];
                        double viewY1 = sfg.Y_coords[f1_index];
                        // Rule of 3 to scale the pixel values
                        double imageX1 = (viewX1 * (imageWidth / viewWidth));
                        double imageY1 = (viewY1 * (imageHeight / viewHeight));
                        // Limit the X and Y values to the bitmap max dimensions to avoid IllegalArgumentException when providing the pixel coordinate
                        if (imageX1 >= imageWidth) {
                            imageX1 = imageWidth - 0.01; // Modify it by 1/100 of a pixel to meet the imageX < bitmap.width condition!
                        }
                        else if (imageX1 < 0) {
                            imageX1 = 0; // Account for negative X values (bezels!)
                        }
                        if (imageY1 >= imageHeight) {
                            imageY1 = imageHeight - 0.01; // Modify it by 1/100 of a pixel to meet the imageY < bitmap.height condition!
                        }
                        else if (imageY1 < 0) {
                            imageY1 = 0; // Account for negative X values (bezels!)
                        }
                        // Get the pixel based on the IMAGE X and Y coordinates!
                        int pixel1 = image.getPixel((int)imageX1, (int)imageY1);
                        // Save the RGB values
                        int pixel1_red = Color.red(pixel1);
                        int pixel1_green = Color.green(pixel1);
                        int pixel1_blue = Color.blue(pixel1);

                        // Create the SPATIAL AUDIO variables
                        float audio_x = 0;
                        float audio_y = 0;
                        float pitch = 0;
                        float leftVolume = (float) 1;
                        float rightVolume = (float) 1;

                        /**************************************************************************************
                         * DECIDE WHICH FINGER THE SPATIAL AUDIO WOULD BE BASED ON
                         **************************************************************************************/
                        if (spatial_audio_activated) {
                            // If FINGER0 is the one on empty space
                            if ((pixel0_red != inside_red && pixel0_green != inside_green && pixel0_blue != inside_blue) &&
                                    (pixel1_red == inside_red && pixel1_green == inside_green && pixel1_blue == inside_blue)) {

                                // For AUDIO CHANNEL proportional control based on X axis coordinate
                                audio_x = (float) (imageX0 / imageWidth);
                                leftVolume = (1 - audio_x);
                                rightVolume = audio_x;

                                // For AUDIO PITCH proportional control based on Y axis coordinate
                                audio_y = (float) (imageY0 / imageHeight);
                                pitch = (float) (1 - audio_y); // <1 = lower pitch, >1 = higher pitch. We use 1 - audio_y because y = 0 is the TOP of the screen, not the BOTTOM of the screen, and we want lower pitches at the BOTTOM of the screen
                                // Keep the pitch above the tablet threshold, otherwise we get an IllegalArgumentException
                                if (pitch < pitch_threshold) {
                                    pitch = pitch_threshold;
                                }

                            }
                            // If FINGER1 is the one on empty space
                            else if ((pixel0_red == inside_red && pixel0_green == inside_green && pixel0_blue == inside_blue) &&
                                    (pixel1_red != inside_red && pixel1_green != inside_green && pixel1_blue != inside_blue)) {

                                // For AUDIO CHANNEL proportional control based on X axis coordinate
                                audio_x = (float) (imageX1 / imageWidth);
                                leftVolume = (1 - audio_x);
                                rightVolume = audio_x;

                                // For AUDIO PITCH proportional control based on Y axis coordinate
                                audio_y = (float) (imageY1 / imageHeight);
                                pitch = (float) (1 - audio_y); // <1 = lower pitch, >1 = higher pitch. We use 1 - audio_y because y = 0 is the TOP of the screen, not the BOTTOM of the screen, and we want lower pitches at the BOTTOM of the screen
                                // Keep the pitch above the tablet threshold, otherwise we get an IllegalArgumentException
                                if (pitch < pitch_threshold) {
                                    pitch = pitch_threshold;
                                }
                            }
                            // If BOTH are on empty space, then follow finger0
                            else if ((pixel0_red != inside_red && pixel0_green != inside_green && pixel0_blue != inside_blue) &&
                                    (pixel1_red != inside_red && pixel1_green != inside_green && pixel1_blue != inside_blue)) {

                                // For AUDIO CHANNEL proportional control based on X axis coordinate
                                audio_x = (float) (imageX0 / imageWidth);
                                leftVolume = (1 - audio_x);
                                rightVolume = audio_x;

                                // For AUDIO PITCH proportional control based on Y axis coordinate
                                audio_y = (float) (imageY0 / imageHeight);
                                pitch = (float) (1 - audio_y); // <1 = lower pitch, >1 = higher pitch. We use 1 - audio_y because y = 0 is the TOP of the screen, not the BOTTOM of the screen, and we want lower pitches at the BOTTOM of the screen
                                // Keep the pitch above the tablet threshold, otherwise we get an IllegalArgumentException
                                if (pitch < pitch_threshold) {
                                    pitch = pitch_threshold;
                                }
                            }
                        }


                        /**************************************************************************************
                         * REACT BASED ON FINGER LOCATION AND PIXEL COLOR
                         **************************************************************************************/

                        // If BOTH fingers are inside a bar or point
                        if ((pixel0_red == inside_red && pixel0_green == inside_green && pixel0_blue == inside_blue) &&
                                (pixel1_red == inside_red && pixel1_green == inside_green && pixel1_blue == inside_blue)) {
                            if (vib_freq != vib_freq_inside) {
                                // Stop the previous vibration if it is different from the one we are supposed to do
                                vib.stop();
                                // Update the vibration frequency
                                vib_freq = vib_freq_inside;
                                // This only happens ONCE when the vibration frequency CHANGES value, to avoid the motor having to STOPGOSTOPGOSTOPGOSTOPGO
                            }
                            if (!vib.isVibrating()) {
                                //vib.vibrateAtFrequencyForever(vib_freq);
                                vib.vibrateForever();
                            }

                            // Stop other sounds
                            soundPool.stop(empty_sound_stream_id);
                            empty_sound_is_playing = false;
                        }

                        // If ONE finger is on a bar or point
                        else if ((pixel0_red == inside_red && pixel0_green == inside_green && pixel0_blue == inside_blue) ||
                                (pixel1_red == inside_red && pixel1_green == inside_green && pixel1_blue == inside_blue)) {
                            if (vib_freq != vib_freq_inside) {
                                // Stop the previous vibration if it is different from the one we are supposed to do
                                vib.stop();
                                // Update the vibration frequency
                                vib_freq = vib_freq_inside;
                                // This only happens ONCE when the vibration frequency CHANGES value, to avoid the motor having to STOPGOSTOPGOSTOPGOSTOPGO
                            }
                            if (!vib.isVibrating()) {
                                //vib.vibrateAtFrequencyForever(vib_freq);
                                vib.vibrateForever();
                            }
                        }

                        // If NO fingers are on a bar or point
                        else {
                            vib.stop();
                        }

                        // If one OR both fingers are on empty space
                        if (((pixel0_red != inside_red && pixel0_green != inside_green && pixel0_blue != inside_blue) ||
                                ((pixel1_red != inside_red && pixel1_green != inside_green && pixel1_blue != inside_blue)))) {

                            // Stop other sounds
                            // ...

                            // Play EMPTY sound
                            // SoundPool output with or without Spatial Audio
                            if (!empty_sound_is_playing) {
                                if (!spatial_audio_activated) {
                                    // Start playing the sound at normal pitch, from both speakers
                                    empty_sound_stream_id = soundPool.play(empty_sound_id, (float) 1.0, (float) 1.0, priority, loop, (float) 1.0);
                                }
                                else {
                                    // Start playing the sound at normal pitch, from both speakers
                                    empty_sound_stream_id = soundPool.play(empty_sound_id, leftVolume, rightVolume, priority, loop, pitch);
                                }
                                empty_sound_is_playing = true;
                            }
                            // SPATIAL AUDIO constant modification
                            if (spatial_audio_activated) {
                                soundPool.setLoop(empty_sound_stream_id, loop);
                                soundPool.setVolume(empty_sound_stream_id, leftVolume, rightVolume);
                                soundPool.setRate(empty_sound_stream_id, pitch);
                            }
                        }
                    }
                }

                handler.postDelayed(this, 0);
            }
        });
    }
}
package com.twohundrediq.a200iq;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class MainActivity extends AppCompatActivity {

    private TextView levelNumber;
    private ImageButton nextButton;
    private ImageButton previousButton;
    private Button playButton;
    private ImageView levelImage;
    private ImageView backImageforColor;
    int levelCount;
    int levelCountFromShared;

    //AlertDiaglog
    EditText input;
    TextView textLastLevel;
    AlertDialog alertDialog;

    RelativeLayout relativeLayout;
    Toolbar toolbar;
    Menu tempMenu;

    AdView mAdview;

    private SoundPool soundPool;
    private int passLevel;
    private int wronganswer;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    int[] answers = new int[]{15,26,72,23,144,11,68,38,31,21,39,4,46,150};
    String[] backgroundColorCodes = new String[]{"FFFFFF","EAEFF2","E9EEF1","B5E5F9",
            "E7966B","F0F0F0","DFF6FE","ADD6F4","D0E1E8","DFF6FE","01141B",
            "FFF2CF","0D0600","E0F7FF"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(getApplicationContext(),"ca-app-pub-5139815030095193~3544157752");

        levelNumber = findViewById(R.id.levelNumber);
        backImageforColor = findViewById(R.id.backImageforColor);
        nextButton = findViewById(R.id.nextButton);
        previousButton = findViewById(R.id.previousButton);
        playButton = findViewById(R.id.playButton);
        levelImage = findViewById(R.id.levelImage);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        input = new EditText(this);
        textLastLevel = new TextView(this);

        sharedPref = this.getSharedPreferences("com.twohundrediq.a200iq", Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putInt("hintCount",5);
        editor.putInt("levelCount",13);
        editor.commit();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("200 IQ");
        toolbar.setLogo(R.drawable.tabbarlogo);

        /*MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                //MobileAds.initialize(getApplicationContext(),"ca-app-pub-5139815030095193~3544157752");

            }
        });*/

        mAdview = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdview.loadAd(adRequest);


        setSound();
        handleLevels();
        handleAlertDialog();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPressed();
            }
        });
    }

    public void setSound(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(6)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        }
        passLevel = soundPool.load(this, R.raw.passlevel, 1);
        wronganswer = soundPool.load(this, R.raw.wronganswer, 1);

    }

    public void handleAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.answer);
        builder.setView(input);

        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(input.getText().toString().equalsIgnoreCase(String.valueOf(answers[levelCount-1]))){

                    if(levelCount!=answers.length){//son bölüm kontrolü
                        if(levelCountFromShared==levelCount){
                            levelCountFromShared++;
                            editor.putInt("levelCount",levelCount+1);
                            editor.putInt("hintCount",0);
                            editor.commit();
                            Log.d("editortest", "onClick: "+sharedPref.getInt("hintCount",0));
                        }
                        soundPool.play(passLevel, 1, 1, 0, 0, 1);
                        toastMessage(R.string.congratulation);
                        levelCount++;
                        setBackgroundImage();
                    }else{
                        //gameFinishedAlertDialog();
                        toastMessage(R.string.game_finished);
                    }

                }else{
                    soundPool.play(wronganswer, 1, 1, 0, 0, 1);
                    toastMessage(R.string.wrong_answer);
                }
                input.getText().clear();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                input.getText().clear();
                dialog.dismiss();
            }
        });

        alertDialog = builder.create();

    }

    public void toastMessage(int message){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.custom_toast_container));

        TextView text = layout.findViewById(R.id.text_toast);
        text.setText(message);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        if(Informations.didOpenFirstTime){
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);
        }else{
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(true);
        }

        tempMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.answerItem:
                alertDialog.show();
                break;
            case R.id.hintItem:
                Intent intent = new Intent(this, HintActivity.class);
                intent.putExtra("level",levelCount);

                if(levelCount==levelCountFromShared){
                    intent.putExtra("last",true);
                }else{
                    intent.putExtra("last",false);
                }
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void handleLevels(){

        levelCountFromShared = sharedPref.getInt("levelCount",1);
        if(Informations.didOpenFirstTime){
            levelCount = levelCountFromShared;
        }else{
            levelCount = Informations.lastPlayedLevel;
            handleViewsforPlay();
            setBackgroundImage();
        }
        levelNumber.setText(levelCount+"");
    }

    public void setBackgroundImage(){
        backImageforColor.setBackgroundColor(Color.parseColor("#"+backgroundColorCodes[levelCount-1]));
        String icon="level" + levelCount;
        int resID = getResources().getIdentifier(icon, "drawable", getPackageName());
        levelImage.setImageResource(resID);
        //levelImage.bringToFront();
    }

    public void previousTabbed(View view){
        if(levelCount!=1){
            levelCount--;
            levelNumber.setText(levelCount+"");
        }
    }

    public void nextTabbed(View view){
        Log.d("nextButontest", "onCsadsa");

        if(levelCount+1<=levelCountFromShared){
            levelCount++;
            levelNumber.setText(levelCount+"");
        }
    }

    public void playTabbed(View view){
        handleViewsforPlay();
        tempMenu.getItem(0).setVisible(true);
        tempMenu.getItem(1).setVisible(true);
        setBackgroundImage();
    }

    public void handleViewsforPlay(){
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        nextButton.setVisibility(View.GONE);
        previousButton.setVisibility(View.GONE);
        playButton.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();
        soundPool = null;
    }

    public void backPressed(){
        toolbar.setNavigationIcon(null);
        tempMenu.getItem(0).setVisible(false);
        tempMenu.getItem(1).setVisible(false);
        nextButton.setVisibility(View.VISIBLE);
        previousButton.setVisibility(View.VISIBLE);//view visibl func yapılması
        playButton.setVisibility(View.VISIBLE);

        backImageforColor.setBackgroundColor(0);
        levelImage.setImageResource(0);
        levelNumber.setText(levelCount+"");
    }

    @Override
    public void onBackPressed() {
        backPressed();
    }
}

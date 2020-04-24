package com.twohundrediq.a200iq;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

public class HintActivity extends AppCompatActivity implements RewardedVideoAdListener {

    Toolbar toolbar;

    ImageView imageView1;
    ImageView imageView2;
    ImageView imageView3;
    ImageView imageView4;
    ImageView imageView5;
    ImageView imageView6;
    ImageView imageView7;
    ImageView imageView8;
    ImageView backImageforColor2;
    Button adsButton;

    int openHints;

    AdView mAdview;
    private RewardedVideoAd mRewardedVideoAd;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    Bundle bundle;

    int level;
    boolean last;

    int[] hints = new int[]{6,7,8,7,7,7,8,7,8,8,8,2,6,8};
    String[] backgroundColorCodes = new String[]{"FFFFFF","EAEFF2","E9EEF1","B5E5F9",
            "E7966B","F0F0F0","DFF6FE","ADD6F4","D0E1E8","DFF6FE","01141B",
            "FFF2CF","0D0600","E0F7FF"};
    ImageView[] imageViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hint);

        toolbar = (Toolbar)findViewById(R.id.hintToolbar);

        imageView1 = findViewById(R.id.imageView1);
        imageView2 = findViewById(R.id.imageView2);
        imageView3 = findViewById(R.id.imageView3);
        imageView4 = findViewById(R.id.imageView4);
        imageView5 = findViewById(R.id.imageView5);
        imageView6 = findViewById(R.id.imageView6);
        imageView7 = findViewById(R.id.imageView7);
        imageView8 = findViewById(R.id.imageView8);
        backImageforColor2 = findViewById(R.id.backImageforColor2);
        adsButton = findViewById(R.id.adsButton);
        //adsButton.setVisibility(View.GONE);

        imageViews = new ImageView[]{imageView1,imageView2,imageView3,
                imageView4,imageView5,imageView6,imageView7,imageView8};

        sharedPref = this.getSharedPreferences("com.twohundrediq.a200iq",Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        bundle = getIntent().getExtras();

        if(bundle != null){
            level = bundle.getInt("level");
            last = bundle.getBoolean("last");
        }//else yazılabilir

        backImageforColor2.setBackgroundColor(Color.parseColor("#"+backgroundColorCodes[level-1]));

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("200 IQ");
        toolbar.setLogo(R.drawable.tabbarlogo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this,"ca-app-pub-5139815030095193~3544157752");
        mAdview = (AdView) findViewById(R.id.hintAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdview.loadAd(adRequest);

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        handleInformations();
        handleHints();
        loadRewardedVideoAd();
    }

    public void handleInformations(){
        Informations.didOpenFirstTime = false;
        Informations.lastPlayedLevel = level;

    }

    private void loadRewardedVideoAd() {
        //ca-app-pub-5139815030095193/9916088432
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());
    }

    public void handleHints(){
        if(last){
            openHints = sharedPref.getInt("hintCount",0);
            if(hints[level-1]!=openHints){
                imageViews[openHints].setImageResource(R.drawable.question_mark);
            }else{
                adsButton.setVisibility(View.GONE);
            }
        }else{
            openHints = hints[level-1];
            adsButton.setVisibility(View.GONE);
        }

        for (int i=1;i<=openHints;i++){
            String icon="hint"+level+"_"+i;
            int resID = getResources().getIdentifier(icon, "drawable", getPackageName());
            imageViews[i-1].setImageResource(resID);
        }
    }

    public void adsTabbed(View view){//button
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
    }

    public void showOneMoreHint(){
        openHints++;
        String icon="hint"+level+"_"+(openHints);
        int resID = getResources().getIdentifier(icon, "drawable", getPackageName());
        imageViews[openHints-1].setImageResource(resID);//açılacak olan kopya

        editor.putInt("hintCount",openHints);
        editor.commit();

        Log.d("edittesett", ": "+sharedPref.getInt("hintCount",0));
        if(hints[level-1]!=openHints){
            imageViews[openHints].setImageResource(R.drawable.question_mark); //Soru işareti
        }else{
            adsButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRewarded(RewardItem reward) {
        showOneMoreHint();
        // Reward the user.
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();
        //adsButton.setVisibility(View.GONE);
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        //adsButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRewardedVideoAdOpened() {
    }

    @Override
    public void onRewardedVideoStarted() {
    }

    @Override
    public void onRewardedVideoCompleted() {
    }

    @Override
    public void onResume() {
        mRewardedVideoAd.resume(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        mRewardedVideoAd.pause(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mRewardedVideoAd.destroy(this);
        super.onDestroy();
    }
}

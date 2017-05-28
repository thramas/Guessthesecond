package com.pukingminion.guessthesecond;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int TARGET = 1000;
    private static final String THEME_MUSIC = "theme_music.mp3";
    private static final int NUMBER_OF_CHANCES = 10;
    private long currentTimeInMs = 0;
    private long cumulativeOffset = 0;
    private long currentCount = 0;
    private long previousTimeInMs = 0;
    private long noOfPerfects = 0;
    private double accuracy = 0.0;
    private View arenaView;
    private TextView offsetTv;
    private TextView counterTv;
    private TextView accuracyTv;
    private TextView perfectsTv;
    private TextView introTv;
    private TextView resultsTv;
    private TextView startBtn;
    private TextView restartTv;
    private TextView shareBtn;
    private TextView levels;
    private TextView gameTitle;
    private LinearLayout introLayout;
    private LinearLayout resultLayout;
    private RelativeLayout shareLayout;
    private MediaPlayer mMediaPlayer;
    private TextView instructions;
    private String mPath;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initUiElements();
    }

    private void initUiElements() {
        arenaView = findViewById(R.id.arena_view);
        shareLayout = (RelativeLayout) findViewById(R.id.share_layout);
        introLayout = (LinearLayout) findViewById(R.id.intro_layout);
        resultLayout = (LinearLayout) findViewById(R.id.result_layout);
        offsetTv = (TextView) findViewById(R.id.difference_from_perfect);
        counterTv = (TextView) findViewById(R.id.counter_tv);
        introTv = (TextView) findViewById(R.id.intro_tv);
        instructions = (TextView) findViewById(R.id.instructions);
        levels = (TextView) findViewById(R.id.levels);
        levels.setText("Level : Child");
        resultsTv = (TextView) findViewById(R.id.results_tv);
        perfectsTv = (TextView) findViewById(R.id.total_perfects);
        accuracyTv = (TextView) findViewById(R.id.accuracy);
        shareBtn = (TextView) findViewById(R.id.share_btn);
        restartTv = (TextView) findViewById(R.id.restart_game);
        startBtn = (TextView) findViewById(R.id.start_button);
        gameTitle = (TextView) findViewById(R.id.game_title);
        arenaView.setVisibility(View.GONE);
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        if (mAdView != null) {
            mAdView.loadAd(adRequest);
        }
        DataHelper.setDifficultyLevel(1);

        setListeners();
        setTypefaces();
        resetGame();
    }

    private void setListeners() {
        arenaView.setOnClickListener(this);
        startBtn.setOnClickListener(this);
        shareBtn.setOnClickListener(this);
        restartTv.setOnClickListener(this);
        instructions.setOnClickListener(this);
        levels.setOnClickListener(this);
    }

    private void resetGame() {
        shareLayout.setVisibility(View.VISIBLE);
        resultLayout.setVisibility(View.GONE);
        introLayout.setVisibility(View.VISIBLE);
        counterTv.setVisibility(View.GONE);
        offsetTv.setVisibility(View.GONE);
        currentTimeInMs = 0;
        resetScore();
        play(THEME_MUSIC);
    }

    private void changeScore() {
        currentCount--;
        counterTv.setText(String.valueOf(currentCount));
        long offset = currentTimeInMs - previousTimeInMs - TARGET;
        if (Math.abs(offset) <= DataHelper.getDifficulty()) {
            offsetTv.setText("Perfect!!!");
            noOfPerfects++;
        } else {
            offsetTv.setText(String.valueOf(offset) + " ms");
        }

        previousTimeInMs = currentTimeInMs;
    }

    private void resetScore() {
        currentCount = NUMBER_OF_CHANCES;
        cumulativeOffset = 0;
        previousTimeInMs = 0;
        noOfPerfects = 0;
        accuracy = 0.0;
        counterTv.setText(String.valueOf(currentCount));
    }

    private void calculateScore() {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        accuracy = (1 - (Math.abs(cumulativeOffset) / (TARGET * 10.0))) * 100;
        accuracy =  Math.abs(accuracy - (NUMBER_OF_CHANCES - noOfPerfects) * 5);
        perfectsTv.setText("Perfects : " + String.valueOf(noOfPerfects));
        accuracyTv.setText("Accuracy : " + String.valueOf(df.format(accuracy) + "%"));
    }

    private void play(String fileName) {
        try {
            AssetFileDescriptor descriptor = getAssets().openFd(fileName);
            long start = descriptor.getStartOffset();
            long end = descriptor.getLength();
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(descriptor.getFileDescriptor(), start, end);
            mMediaPlayer.prepare();
            mMediaPlayer.setVolume(1.0f, 1.0f);
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setTypefaces() {
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Amatic-Bold.ttf");
        Typeface cfNew = Typeface.createFromAsset(getAssets(), "fonts/Lobster_1.3.otf");
        Typeface gameFont = Typeface.createFromAsset(getAssets(), "fonts/SEASRN__.ttf");

        shareBtn.setTypeface(custom_font);
        restartTv.setTypeface(custom_font);
        startBtn.setTypeface(custom_font);


        counterTv.setTypeface(cfNew);
        counterTv.setTextSize(150);

        offsetTv.setTypeface(custom_font);
        perfectsTv.setTypeface(custom_font);
        perfectsTv.setTextSize(50);
        accuracyTv.setTypeface(custom_font);
        accuracyTv.setTextSize(50);
        introTv.setTypeface(custom_font);
        introTv.setTextSize(50);
        resultsTv.setTypeface(custom_font);
        resultsTv.setTextSize(50);
        gameTitle.setTypeface(gameFont);
        instructions.setTypeface(custom_font);
        levels.setTypeface(custom_font);
        offsetTv.setTextSize(50);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(getFragmentManager().getBackStackEntryCount() > 1) {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.arena_view:
                currentTimeInMs = System.currentTimeMillis();
                if (currentCount > 0) {
                    cumulativeOffset += currentTimeInMs - previousTimeInMs - TARGET;
                    changeScore();
                    showLayout(R.id.counter_tv);
                } else {
                    calculateScore();
                    showLayout(R.id.result_layout);
                    arenaView.setVisibility(View.GONE);
                }
                if (mMediaPlayer != null) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                }
                play("tick.mp3");
                break;
            case R.id.start_button:
                resetGame();
                arenaView.setVisibility(View.VISIBLE);
                previousTimeInMs = System.currentTimeMillis();
                if (mMediaPlayer != null) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                }
                play("tick.mp3");
                showLayout(R.id.counter_tv);
                break;
            case R.id.share_btn:
                TakeScreenshotTask mTakeScreenshotTask = new TakeScreenshotTask();
                mTakeScreenshotTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;
            case R.id.restart_game:
                resetGame();
                if (mMediaPlayer != null) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                }
                play("tick.mp3");
                break;
            case R.id.levels:
                LevelsFragment levelsFragment = new LevelsFragment();
                levelsFragment.setOnLevelSelected(new ActionListener() {
                    @Override
                    public void onFinished(Object... args) {
                        levels.setText("Level : " + args[0]);
                    }
                });
                if (mMediaPlayer != null) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                }
                play("tick.mp3");
                openFragment(levelsFragment);
                break;
            case R.id.instructions:
                if (mMediaPlayer != null) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                }
                play("tick.mp3");
                openFragment(new InstructionsFragment());
                break;

        }
    }

    private void openFragment(Fragment fragment) {
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().
                addToBackStack("blankfragment");
        fragmentTransaction.add(android.R.id.content, fragment, "blankfragment").commit();
    }

    private String takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            Bitmap bitmap = getScreenShot(shareLayout);
            mPath = store(bitmap, String.valueOf(System.currentTimeMillis()));
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
        return mPath;
    }

    public String store(Bitmap bm, String fileName) {
        File filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File dir = new File(filePath, fileName + ".jpeg");
        if (!dir.exists()) {
            dir.getParentFile().mkdirs();
        }
        try {
            FileOutputStream fOut = new FileOutputStream(dir);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dir.getAbsolutePath();
    }

    public Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    private void showLayout(int id) {
        switch (id) {
            case R.id.counter_tv:
                shareLayout.setVisibility(View.GONE);
                counterTv.setVisibility(View.VISIBLE);
                offsetTv.setVisibility(currentCount == 10 ? View.GONE : View.VISIBLE);
                ObjectAnimator animator = getObjectAnimator(counterTv, 1, 1.05f, 500);
                ObjectAnimator animatorTwo = getObjectAnimator(counterTv, 1.05f, 1, 500);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playSequentially(animator, animatorTwo);
                animatorSet.start();
                break;
            case R.id.result_layout:
                shareLayout.setVisibility(View.VISIBLE);
                introLayout.setVisibility(View.GONE);
                resultLayout.setVisibility(View.VISIBLE);
//                mAdView.setVisibility(View.VISIBLE);
                counterTv.setVisibility(View.GONE);
                offsetTv.setVisibility(View.GONE);
                break;
            case R.id.intro_layout:
                shareLayout.setVisibility(View.VISIBLE);
                introLayout.setVisibility(View.VISIBLE);
                resultLayout.setVisibility(View.GONE);
                counterTv.setVisibility(View.GONE);
                offsetTv.setVisibility(View.GONE);

        }
    }

    private ObjectAnimator getObjectAnimator(final View imageView, float originalScale, float newScale, int duration) {
        PropertyValuesHolder zoomX = PropertyValuesHolder.ofFloat(View.SCALE_X, originalScale, newScale);
        PropertyValuesHolder zoomY = PropertyValuesHolder.ofFloat(View.SCALE_Y, originalScale, newScale);
        ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(imageView, zoomX, zoomY);
        animation.setDuration(duration);
        return animation;
    }

    private class TakeScreenshotTask extends AsyncTask<Void, Void, Void> {

        private String filePath;

        @Override
        protected Void doInBackground(Void... params) {
            filePath = takeScreenshot();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Uri imageUri = Uri.parse(filePath);
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("image/jpeg");
            shareIntent.setPackage("com.whatsapp");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Can you beat me here? \n https://goo.gl/21Cuif");
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            try {
                startActivity(shareIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(arenaView.getContext(), "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(aVoid);
        }
    }
}

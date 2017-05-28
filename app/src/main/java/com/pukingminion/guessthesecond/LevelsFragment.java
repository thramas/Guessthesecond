package com.pukingminion.guessthesecond;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;

/**
 * Created by Samarth on 13/05/17.
 */

public class LevelsFragment extends Fragment {
    private static final double NUMBER_OF_LEVELS = 4;
    View view;
    private ActionListener onLevelSelected;
    int difficulty;
    private MediaPlayer mMediaPlayer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.levels_layout, null);
        } else {
            if (view.getParent() != null) {
                ((ViewGroup) (view.getParent())).removeView(view);
            }
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }

    private void initViews() {
        SharedPreferences sp = getContext().getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        Typeface custom_font = Typeface.createFromAsset(getResources().getAssets(), "fonts/Amatic-Bold.ttf");

        LinearLayout parentList = (LinearLayout) view.findViewById(R.id.list_of_levels);

        final View.OnClickListener onLevelSelect = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int levelOfDifficulty = (Integer) v.getTag();
                DataHelper.setDifficultyLevel(levelOfDifficulty);
                if (mMediaPlayer != null) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                }
                play("tick.mp3");
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
                onLevelSelected.onFinished(DataHelper.getDifficultyName(levelOfDifficulty));
            }
        };
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(dp(70), dp(20), dp(70), 0);
        layoutParams.gravity = Gravity.CENTER;

        for (int i = 0; i < NUMBER_OF_LEVELS; i++) {
            TextView textView = new TextView(getContext());
            textView.setBackground(getGradientDrawable(getResources().getColor(R.color.colorPrimaryDark), 0, 0, 0));
            textView.setPadding(dp(15), dp(5), dp(15), dp(5));
            textView.setText(DataHelper.levelsListNames[i]);
            textView.setTextColor(getResources().getColor(R.color.colorAccent));
            textView.setTextSize(20);
            textView.setGravity(Gravity.CENTER);
            textView.setTypeface(custom_font);
            textView.setTag(i);
            textView.setLayoutParams(layoutParams);
            textView.setOnClickListener(onLevelSelect);
            parentList.addView(textView);
        }
    }

    private void play(String fileName) {
        try {
            AssetFileDescriptor descriptor = getContext().getAssets().openFd(fileName);
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

    @NonNull
    public static GradientDrawable getGradientDrawable(int bgColor, int cornerRadius,
                                                       int strokeWidth, int strokeColor) {
        GradientDrawable background = new GradientDrawable();
        background.setColor(bgColor);
        background.setCornerRadius(cornerRadius);
        if (strokeColor == 0 && strokeWidth == 0) return background;
        background.setStroke(strokeWidth, strokeColor);
        return background;
    }

    public int dp(float value) {
        if (value == 0) {
            return 0;
        }
        float density = getResources().getDisplayMetrics().density;
        return (int) Math.ceil(density * value);
    }

    public void setOnLevelSelected(ActionListener onLevelSelected) {
        this.onLevelSelected = onLevelSelected;
    }

}

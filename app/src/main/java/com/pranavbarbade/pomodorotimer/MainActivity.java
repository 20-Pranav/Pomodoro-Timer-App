package com.pranavbarbade.pomodorotimer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // UI Components
    private TextView timerText;
    private Button startButton, pauseButton, resetButton, historyButton;
    private ProgressBar progressBar;
    private SeekBar focusSeekBar, breakSeekBar;
    private TextView focusValue, breakValue;
    private TextView modeText;
    private Button skipBreakButton;

    // Timer variables
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;
    private long totalTimeMillis;
    private boolean timerRunning;
    private int focusMinutes = 25;

    // Break timer variables
    private boolean isBreakMode = false;
    private int breakMinutes = 5;
    private long breakTimeInMillis;
    private long totalBreakTimeMillis;

    // Database
    private DatabaseHelper dbHelper;

    // Sound
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Edge-to-edge handling
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        timerText = findViewById(R.id.timerText);
        startButton = findViewById(R.id.startButton);
        pauseButton = findViewById(R.id.pauseButton);
        resetButton = findViewById(R.id.resetButton);
        historyButton = findViewById(R.id.historyButton);
        progressBar = findViewById(R.id.progressBar);
        focusSeekBar = findViewById(R.id.durationSeekBar);
        focusValue = findViewById(R.id.durationValue);
        breakSeekBar = findViewById(R.id.breakSeekBar);
        breakValue = findViewById(R.id.breakDurationValue);
        modeText = findViewById(R.id.modeText);
        skipBreakButton = findViewById(R.id.skipBreakButton);

        // Set up Focus SeekBar (1 to 120 minutes)
        focusSeekBar.setMax(120);
        focusSeekBar.setProgress(focusMinutes);
        focusValue.setText(focusMinutes + " min");

        // Set up Break SeekBar (1 to 30 minutes) - NEW
        breakSeekBar.setMax(30);
        breakSeekBar.setProgress(breakMinutes);
        breakValue.setText(breakMinutes + " min");

        // Initial timer values
        totalTimeMillis = focusMinutes * 60 * 1000L;
        timeLeftInMillis = totalTimeMillis;
        totalBreakTimeMillis = breakMinutes * 60 * 1000L;
        breakTimeInMillis = totalBreakTimeMillis;

        updateTimerText();
        progressBar.setMax(100);
        progressBar.setProgress(0);
        updateModeDisplay();

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Focus SeekBar change listener
        focusSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 1) progress = 1;
                focusMinutes = progress;
                focusValue.setText(focusMinutes + " min");
                totalTimeMillis = focusMinutes * 60 * 1000L;

                if (!timerRunning && !isBreakMode) {
                    timeLeftInMillis = totalTimeMillis;
                    updateTimerText();
                    progressBar.setProgress(0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        // Break SeekBar change listener (NEW)
        breakSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 1) progress = 1;
                breakMinutes = progress;
                breakValue.setText(breakMinutes + " min");
                totalBreakTimeMillis = breakMinutes * 60 * 1000L;

                if (isBreakMode && !timerRunning) {
                    breakTimeInMillis = totalBreakTimeMillis;
                    updateTimerText();
                    progressBar.setProgress(0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        // Button click listeners
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseTimer();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });

        skipBreakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBreakMode) {
                    skipBreak();
                }
            }
        });
    }

    private void startTimer() {
        if (!timerRunning) {
            countDownTimer = new CountDownTimer(
                    isBreakMode ? breakTimeInMillis : timeLeftInMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (isBreakMode) {
                        breakTimeInMillis = millisUntilFinished;
                    } else {
                        timeLeftInMillis = millisUntilFinished;
                    }
                    updateTimerText();
                    updateProgressBar();
                }

                @Override
                public void onFinish() {
                    timerRunning = false;

                    if (isBreakMode) {
                        // Break finished
                        Toast.makeText(MainActivity.this, "Break finished! Ready to focus? 🎯", Toast.LENGTH_LONG).show();
                        playFinishSound();
                        resetToFocusMode();
                    } else {
                        // Focus session finished
                        timerText.setText("00:00");
                        progressBar.setProgress(100);

                        // Save completed session to database
                        saveSessionToDatabase();

                        Toast.makeText(MainActivity.this, "Focus session completed! Time for a break ☕", Toast.LENGTH_LONG).show();
                        playFinishSound();

                        // Start break
                        startBreak();
                    }
                }
            }.start();

            timerRunning = true;
            startButton.setText("PAUSE");
            startButton.setEnabled(true);
            pauseButton.setEnabled(true);
            skipBreakButton.setVisibility(isBreakMode ? View.VISIBLE : View.GONE);
        }
    }

    private void pauseTimer() {
        if (timerRunning) {
            countDownTimer.cancel();
            timerRunning = false;
            startButton.setText("RESUME");
            pauseButton.setEnabled(true);
        }
    }

    private void resetTimer() {
        if (timerRunning) {
            countDownTimer.cancel();
            timerRunning = false;
        }

        if (isBreakMode) {
            resetToFocusMode();
        } else {
            timeLeftInMillis = totalTimeMillis;
            updateTimerText();
            progressBar.setProgress(0);
            startButton.setText("START");
            startButton.setEnabled(true);
            pauseButton.setEnabled(true);
        }
    }

    private void startBreak() {
        isBreakMode = true;
        breakTimeInMillis = totalBreakTimeMillis;
        updateTimerText();
        progressBar.setProgress(0);
        updateModeDisplay();
        skipBreakButton.setVisibility(View.VISIBLE);

        // Auto-start break
        startTimer();
    }

    private void resetToFocusMode() {
        isBreakMode = false;
        timeLeftInMillis = totalTimeMillis;
        updateTimerText();
        progressBar.setProgress(0);
        updateModeDisplay();
        skipBreakButton.setVisibility(View.GONE);
        startButton.setText("START");
    }

    private void skipBreak() {
        if (timerRunning) {
            countDownTimer.cancel();
            timerRunning = false;
        }
        resetToFocusMode();
        Toast.makeText(this, "Break skipped. Back to focus!", Toast.LENGTH_SHORT).show();
    }

    private void saveSessionToDatabase() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());
        String currentTime = timeFormat.format(new Date());

        dbHelper.addSession(currentDate, currentTime, focusMinutes);
        Toast.makeText(MainActivity.this, "Session saved: " + focusMinutes + " min", Toast.LENGTH_SHORT).show();
    }

    private void updateModeDisplay() {
        if (modeText != null) {
            if (isBreakMode) {
                modeText.setText("☕ BREAK TIME");
                modeText.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
            } else {
                modeText.setText("🍅 FOCUS TIME");
                modeText.setTextColor(getResources().getColor(R.color.primary));
            }
        }
    }

    private void updateTimerText() {
        long millis = isBreakMode ? breakTimeInMillis : timeLeftInMillis;
        int minutes = (int) (millis / 1000) / 60;
        int seconds = (int) (millis / 1000) % 60;
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timerText.setText(timeFormatted);
    }

    private void updateProgressBar() {
        long current = isBreakMode ? breakTimeInMillis : timeLeftInMillis;
        long total = isBreakMode ? totalBreakTimeMillis : totalTimeMillis;
        int progress = (int) ((total - current) * 100 / total);
        if (progress >= 0) {
            progressBar.setProgress(progress);
        }
    }

    private void playFinishSound() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }

            mediaPlayer = MediaPlayer.create(this, R.raw.timer_finish);

            if (mediaPlayer == null) {
                android.net.Uri notification = Settings.System.DEFAULT_NOTIFICATION_URI;
                mediaPlayer = MediaPlayer.create(this, notification);
            }

            if (mediaPlayer != null) {
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                    }
                });
                mediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}

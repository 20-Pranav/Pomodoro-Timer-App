package com.pranavbarbade.pomodorotimer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private DatabaseHelper dbHelper;
    private TextView totalSessionsText, totalTimeText;
    private TextView studyPurposeText, workPurposeText, otherPurposeText;
    private Button allButton, studyButton, workButton;
    private List<Session> currentSessionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        totalSessionsText = findViewById(R.id.totalSessionsText);
        totalTimeText = findViewById(R.id.totalTimeText);
        studyPurposeText = findViewById(R.id.studyPurposeText);
        workPurposeText = findViewById(R.id.workPurposeText);
        otherPurposeText = findViewById(R.id.otherPurposeText);
        allButton = findViewById(R.id.allSessionsButton);
        studyButton = findViewById(R.id.studySessionsButton);
        workButton = findViewById(R.id.workSessionsButton);

        // Initialize database
        dbHelper = new DatabaseHelper(this);

        // Load all sessions
        loadSessions(dbHelper.getAllSessions());

        // Update purpose counts
        updatePurposeCounts();

        // Button click listeners for filtering
        allButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSessions(dbHelper.getAllSessions());
            }
        });

        studyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSessions(dbHelper.getSessionsByPurpose("study"));
            }
        });

        workButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSessions(dbHelper.getSessionsByPurpose("work"));
            }
        });
    }

    private void loadSessions(List<Session> sessionList) {
        currentSessionList = sessionList;
        adapter = new HistoryAdapter(sessionList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Update statistics
        int totalSessions = dbHelper.getSessionCount();
        int totalMinutes = dbHelper.getTotalFocusTime();
        int hours = totalMinutes / 60;
        int mins = totalMinutes % 60;

        totalSessionsText.setText("Total sessions: " + totalSessions);
        totalTimeText.setText("Total time: " + hours + "h " + mins + "m");
    }

    private void updatePurposeCounts() {
        int studyCount = dbHelper.getSessionCountByPurpose("study");
        int workCount = dbHelper.getSessionCountByPurpose("work");
        int otherCount = dbHelper.getSessionCountByPurpose("other");

        studyPurposeText.setText("📖 Study sessions: " + studyCount);
        workPurposeText.setText("💼 Work sessions: " + workCount);
        otherPurposeText.setText("🎯 Other sessions: " + otherCount);
    }
}

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
    private TextView subjectSummaryText;
    private Button allButton, studyButton, workButton, otherButton;
    private List<Session> currentSessionList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        totalSessionsText = findViewById(R.id.totalSessionsText);
        totalTimeText = findViewById(R.id.totalTimeText);
        subjectSummaryText = findViewById(R.id.subjectSummaryText);
        allButton = findViewById(R.id.allSessionsButton);
        studyButton = findViewById(R.id.studySessionsButton);
        workButton = findViewById(R.id.workSessionsButton);
        otherButton = findViewById(R.id.otherSessionsButton);

        // Initialize database
        dbHelper = new DatabaseHelper(this);

        // Load all sessions
        loadSessions(dbHelper.getAllSessions());

        // Update subject summary
        updateSubjectSummary();

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
                loadSessions(dbHelper.getSessionsBySubject("Study"));
            }
        });

        workButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSessions(dbHelper.getSessionsBySubject("Work"));
            }
        });

        otherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSessions(dbHelper.getSessionsBySubject("Other"));
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

    private void updateSubjectSummary() {
        List<Session> allSessions = dbHelper.getAllSessions();

        // Count sessions by subject
        int studyCount = 0;
        int workCount = 0;
        int otherCount = 0;

        for (Session session : allSessions) {
            String subject = session.getSubject().toLowerCase();
            if (subject.equals("study")) {
                studyCount++;
            } else if (subject.equals("work")) {
                workCount++;
            } else if (subject.equals("other")) {
                otherCount++;
            } else {
                // Count custom subjects as "General"
                otherCount++;
            }
        }

        String summary = "📚 Study: " + studyCount + "  |  💼 Work: " + workCount + "  |  🎯 Other: " + otherCount;
        subjectSummaryText.setText(summary);
    }
}

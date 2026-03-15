package com.pranavbarbade.pomodorotimer;

import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        totalSessionsText = findViewById(R.id.totalSessionsText);
        totalTimeText = findViewById(R.id.totalTimeText);

        // Initialize database
        dbHelper = new DatabaseHelper(this);

        // Get all sessions from database
        List<Session> sessionList = dbHelper.getAllSessions();

        // Check if any sessions exist
        if (sessionList.isEmpty()) {
            totalSessionsText.setText("No sessions yet");
            totalTimeText.setText("Complete a timer session to see history");
            // Create a dummy adapter with empty list
            adapter = new HistoryAdapter(sessionList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        } else {
            // Setup RecyclerView with data
            adapter = new HistoryAdapter(sessionList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);

            // Show statistics
            int totalSessions = dbHelper.getSessionCount();
            int totalMinutes = dbHelper.getTotalFocusTime();
            int hours = totalMinutes / 60;
            int mins = totalMinutes % 60;

            totalSessionsText.setText("Total sessions: " + totalSessions);
            totalTimeText.setText("Total time: " + hours + "h " + mins + "m");
        }
    }
}

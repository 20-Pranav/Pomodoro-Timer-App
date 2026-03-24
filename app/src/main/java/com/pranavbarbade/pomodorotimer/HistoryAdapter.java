package com.pranavbarbade.pomodorotimer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<Session> sessionList;

    public HistoryAdapter(List<Session> sessionList) {
        this.sessionList = sessionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Session session = sessionList.get(position);
        holder.dateText.setText(session.getDate());
        holder.timeText.setText(session.getTime());
        holder.durationText.setText(session.getDuration() + " min");

        // Set subject with emoji
        String subject = session.getSubject();
        if (subject != null) {
            if (subject.equalsIgnoreCase("study")) {
                holder.subjectText.setText("📚 " + subject);
            } else if (subject.equalsIgnoreCase("work")) {
                holder.subjectText.setText("💼 " + subject);
            } else if (subject.equalsIgnoreCase("other")) {
                holder.subjectText.setText("🎯 " + subject);
            } else {
                holder.subjectText.setText("📖 " + subject);
            }
        } else {
            holder.subjectText.setText("📖 General");
        }
    }

    @Override
    public int getItemCount() {
        return sessionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateText, timeText, durationText, subjectText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.dateText);
            timeText = itemView.findViewById(R.id.timeText);
            durationText = itemView.findViewById(R.id.durationText);
            subjectText = itemView.findViewById(R.id.subjectText);
        }
    }
}

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

        // Set purpose with emoji
        String purpose = session.getPurpose();
        if (purpose != null) {
            if (purpose.equals("study")) {
                holder.purposeText.setText("📚 Study");
                holder.purposeText.setTextColor(holder.itemView.getContext().getColor(R.color.primary));
            } else if (purpose.equals("work")) {
                holder.purposeText.setText("💼 Work");
                holder.purposeText.setTextColor(holder.itemView.getContext().getColor(android.R.color.holo_orange_dark));
            } else {
                holder.purposeText.setText("🎯 Other");
                holder.purposeText.setTextColor(holder.itemView.getContext().getColor(R.color.accent));
            }
        } else {
            holder.purposeText.setText("📚 Study");
            holder.purposeText.setTextColor(holder.itemView.getContext().getColor(R.color.primary));
        }
    }

    @Override
    public int getItemCount() {
        return sessionList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateText, timeText, durationText, purposeText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.dateText);
            timeText = itemView.findViewById(R.id.timeText);
            durationText = itemView.findViewById(R.id.durationText);
            purposeText = itemView.findViewById(R.id.purposeText);
        }
    }
}

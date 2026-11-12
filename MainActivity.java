package com.ou25pro.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import java.util.Locale;

public class MainActivity extends Activity {
    LinearLayout root;
    EditText matches;
    EditText history;
    EditText bankroll;
    TextView result;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(28, 34, 28, 28);
        root.setBackgroundColor(Color.rgb(7, 17, 31));

        TextView title = new TextView(this);
        title.setText("O/U 2.5 PRO");
        title.setTextColor(Color.WHITE);
        title.setTextSize(28);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        root.addView(title, new LinearLayout.LayoutParams(-1, -2));

        TextView sub = new TextView(this);
        sub.setText("OVER 2.5 + UNDER 2.5 Decision Support");
        sub.setTextColor(Color.rgb(145, 166, 194));
        sub.setTextSize(16);
        sub.setGravity(Gravity.CENTER);
        sub.setPadding(0, 8, 0, 18);
        root.addView(sub, new LinearLayout.LayoutParams(-1, -2));

        bankroll = field("Bankroll (₦)", "");
        root.addView(bankroll);

        history = field("Previous total goals, e.g. 3,1,4,2,5,3,2,1,4,3", "");
        root.addView(history);

        matches = new EditText(this);
        matches.setHint("Match | Over odds | Under odds\nMatch 1 | 1.80 | 1.90\nMatch 2 | 1.70 | 2.05");
        matches.setHintTextColor(Color.rgb(130, 150, 178));
        matches.setTextColor(Color.WHITE);
        matches.setTextSize(15);
        matches.setGravity(Gravity.TOP);
        matches.setMinLines(6);
        matches.setPadding(12, 12, 12, 12);
        root.addView(matches, new LinearLayout.LayoutParams(-1, 0, 1));

        Button analyze = new Button(this);
        analyze.setText("ANALYZE O/U 2.5");
        analyze.setTextSize(15);
        analyze.setOnClickListener(v -> analyzeMatches());
        root.addView(analyze, new LinearLayout.LayoutParams(-1, -2));

        result = new TextView(this);
        result.setTextColor(Color.WHITE);
        result.setTextSize(16);
        result.setPadding(0, 18, 0, 0);
        root.addView(result, new LinearLayout.LayoutParams(-1, -2));

        setContentView(root);
    }

    EditText field(String hint, String value) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setText(value);
        e.setTextColor(Color.WHITE);
        e.setHintTextColor(Color.rgb(130, 150, 178));
        e.setPadding(12, 12, 12, 12);
        return e;
    }

    void analyzeMatches() {
        String raw = matches.getText().toString().trim();
        if (raw.isEmpty()) {
            result.setText("Enter at least one match using:\nMatch | Over odds | Under odds");
            return;
        }

        double avgGoals = historyAverage();
        String[] lines = raw.split("\\r?\\n");
        StringBuilder out = new StringBuilder();
        out.append("PREDICTIONS\n\n");

        int valid = 0;
        for (String line : lines) {
            String[] p = line.split("\\|");
            if (p.length < 3) continue;

            try {
                String match = p[0].trim();
                double overOdds = Double.parseDouble(p[1].trim());
                double underOdds = Double.parseDouble(p[2].trim());

                double overScore = 100.0 / overOdds;
                double underScore = 100.0 / underOdds;

                // Small history adjustment. This is a heuristic, not a guarantee.
                if (avgGoals > 2.5) {
                    overScore += Math.min(8.0, (avgGoals - 2.5) * 4.0);
                } else if (avgGoals < 2.5) {
                    underScore += Math.min(8.0, (2.5 - avgGoals) * 4.0);
                }

                double total = overScore + underScore;
                double overPct = total > 0 ? overScore / total * 100.0 : 50.0;
                double underPct = total > 0 ? underScore / total * 100.0 : 50.0;
                double gap = Math.abs(overPct - underPct);

                String pick;
                if (gap >= 14.0) {
                    pick = overPct > underPct ? "OVER 2.5" : "UNDER 2.5";
                } else {
                    pick = "WAIT";
                }

                out.append(match).append("\n");
                out.append(String.format(Locale.US,
                        "OVER: %.1f%%   UNDER: %.1f%%\nSIGNAL: %s\n\n",
                        overPct, underPct, pick));
                valid++;
            } catch (Exception ignored) {
                // Ignore malformed rows and continue.
            }
        }

        if (valid == 0) {
            out.append("No valid matches found.\n");
            out.append("Use one match per line, for example:\n");
            out.append("Match 1 | 1.80 | 1.90");
        } else {
            out.append("History average goals: ")
               .append(String.format(Locale.US, "%.2f", avgGoals))
               .append("\n");
            out.append("Heuristic only — not a guaranteed prediction.");
        }

        result.setText(out.toString());
    }

    double historyAverage() {
        String h = history.getText().toString().trim();
        if (h.isEmpty()) return 2.5;

        String[] values = h.split("[,\\s]+");
        double total = 0.0;
        int n = 0;

        for (String x : values) {
            try {
                total += Double.parseDouble(x);
                n++;
            } catch (Exception ignored) {
            }
        }

        return n > 0 ? total / n : 2.5;
    }
}

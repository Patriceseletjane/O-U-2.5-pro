package com.ou25pro.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.*;
import java.util.Locale;

public class MainActivity extends Activity {
    EditText history, matches;
    TextView result;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(28, 34, 28, 28);
        root.setBackgroundColor(Color.rgb(7,17,31));

        TextView title = new TextView(this);
        title.setText("O/U 2.5 PRO");
        title.setTextColor(Color.WHITE);
        title.setTextSize(28);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        TextView sub = new TextView(this);
        sub.setText("OVER 2.5 + UNDER 2.5 Decision Support");
        sub.setTextColor(Color.rgb(145,165,190));
        sub.setGravity(Gravity.CENTER);
        root.addView(sub);

        history = field("Recent total goals (e.g. 3,1,4,2,5)", "3,1,4,2,5,3,2,1,4,3");
        root.addView(history);

        matches = field("Match | Over odds | Under odds", "");
        matches.setMinLines(5);
        matches.setGravity(Gravity.TOP);
        root.addView(matches);

        Button analyze = new Button(this);
        analyze.setText("ANALYZE O/U 2.5");
        root.addView(analyze);

        result = new TextView(this);
        result.setTextColor(Color.WHITE);
        result.setTextSize(17);
        result.setPadding(0, 22, 0, 0);
        root.addView(result);

        analyze.setOnClickListener(v -> analyze());
        setContentView(root);
    }

    EditText field(String hint, String value) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setText(value);
        e.setTextColor(Color.WHITE);
        e.setHintTextColor(Color.rgb(120,140,160));
        e.setPadding(10, 10, 10, 10);
        return e;
    }

    void analyze() {
        double avg = 2.5;
        String[] vals = history.getText().toString().split(",");
        double total = 0; int count = 0;
        for (String x : vals) {
            try { total += Double.parseDouble(x.trim()); count++; }
            catch (Exception ignored) {}
        }
        if (count > 0) avg = total / count;

        double overBase = 50 + ((avg - 2.5) * 20);
        overBase = Math.max(5, Math.min(95, overBase));

        StringBuilder out = new StringBuilder();
        out.append("PREDICTIONS\n\n");

        String raw = matches.getText().toString().trim();
        if (raw.isEmpty()) {
            out.append("Enter at least one match in this format:\n");
            out.append("Team A vs Team B | 1.70 | 2.10\n\n");
            out.append("Average goals: ")
               .append(String.format(Locale.US, "%.2f", avg));
            result.setText(out.toString());
            return;
        }

        for (String line : raw.split("\\n")) {
            String[] p = line.split("\\|");
            if (p.length < 3) continue;

            double oo, uo;
            try {
                oo = Double.parseDouble(p[1].trim());
                uo = Double.parseDouble(p[2].trim());
            } catch (Exception e) {
                continue;
            }

            double marketOver = (1.0 / oo) / ((1.0 / oo) + (1.0 / uo)) * 100.0;
            double over = (overBase * 0.65) + (marketOver * 0.35);
            over = Math.max(1, Math.min(99, over));
            double under = 100 - over;

            String signal = "WAIT";
            if (over >= 60) signal = "OVER 2.5";
            else if (under >= 60) signal = "UNDER 2.5";

            out.append(p[0].trim()).append("\n");
            out.append("OVER 2.5: ").append(Math.round(over)).append("%\n");
            out.append("UNDER 2.5: ").append(Math.round(under)).append("%\n");
            out.append("SIGNAL: ").append(signal).append("\n\n");
        }

        out.append("Average goals: ")
           .append(String.format(Locale.US, "%.2f", avg))
           .append("\n\nDecision-support only; no guaranteed outcome.");

        result.setText(out.toString());
    }
}

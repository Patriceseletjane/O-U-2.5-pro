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
    EditText bankroll;
    EditText history;
    EditText matches;
    TextView result;
    TextView confidence;
    TextView stake;

    int BG = Color.rgb(7, 17, 31);
    int CARD = Color.rgb(15, 29, 48);
    int TEXT = Color.WHITE;
    int MUTED = Color.rgb(150, 170, 195);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(28, 35, 28, 28);
        root.setBackgroundColor(BG);

        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(BG);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(0, 10, 0, 30);

        TextView title = text("O/U 2.5 PRO", 30, TEXT, true);
        title.setGravity(Gravity.CENTER);
        content.addView(title);

        TextView subtitle = text(
                "OVER 2.5 + UNDER 2.5 DECISION SUPPORT",
                15,
                MUTED,
                true
        );
        subtitle.setGravity(Gravity.CENTER);
        content.addView(subtitle);

        addSpace(content, 25);

        content.addView(sectionTitle("BANKROLL"));

        bankroll = input("Enter bankroll (₦)");
        content.addView(bankroll);

        addSpace(content, 18);

        content.addView(sectionTitle("PREVIOUS TOTAL GOALS"));

        history = input(
                "Example: 3,1,4,2,5,3,2,1,4,3"
        );
        content.addView(history);

        addSpace(content, 18);

        content.addView(sectionTitle("MATCHES + ODDS"));

        matches = input(
                "Match 1 | 1.80 | 1.90\n" +
                "Match 2 | 1.70 | 2.05\n" +
                "Match 3 | 1.85 | 1.88"
        );
        matches.setMinLines(5);
        matches.setGravity(Gravity.TOP);
        content.addView(matches);

        addSpace(content, 25);

        Button analyze = new Button(this);
        analyze.setText("ANALYZE O/U 2.5");
        analyze.setTextColor(TEXT);
        analyze.setTextSize(16);
        analyze.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        analyze.setBackgroundColor(Color.rgb(35, 95, 170));

        analyze.setOnClickListener(v -> analyzeMatches());

        content.addView(analyze);

        addSpace(content, 25);

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(25, 25, 25, 25);
        card.setBackgroundColor(CARD);

        TextView predictionLabel =
                text("PRIMARY SIGNAL", 13, MUTED, true);
        card.addView(predictionLabel);

        result = text("WAITING FOR ANALYSIS", 27, TEXT, true);
        result.setGravity(Gravity.CENTER);
        card.addView(result);

        addSpace(card, 15);

        confidence =
                text("Confidence: --", 17, TEXT, false);
        confidence.setGravity(Gravity.CENTER);
        card.addView(confidence);

        addSpace(card, 10);

        stake =
                text("Recommended stake: --", 16, MUTED, false);
        stake.setGravity(Gravity.CENTER);
        card.addView(stake);

        content.addView(card);

        addSpace(content, 20);

        TextView warning = text(
                "⚠ Decision support only. No prediction can guarantee a winning result.",
                13,
                MUTED,
                false
        );
        warning.setGravity(Gravity.CENTER);
        content.addView(warning);

        scroll.addView(content);
        root.addView(scroll);

        setContentView(root);
    }

    void analyzeMatches() {

        String historyText = history.getText().toString().trim();
        String matchText = matches.getText().toString().trim();
        String bankrollText = bankroll.getText().toString().trim();

        if (matchText.isEmpty()) {
            result.setText("ENTER MATCH DATA");
            confidence.setText("Confidence: --");
            stake.setText("Recommended stake: --");
            return;
        }

        int total = 0;
        int count = 0;

        if (!historyText.isEmpty()) {

            String[] numbers = historyText.split(",");

            for (String n : numbers) {
                try {
                    total += Integer.parseInt(n.trim());
                    count++;
                } catch (Exception ignored) {
                }
            }
        }

        double average = count > 0
                ? (double) total / count
                : 2.5;

        double overScore = 50;

        if (average > 2.5) {
            overScore += Math.min((average - 2.5) * 18, 25);
        } else {
            overScore -= Math.min((2.5 - average) * 18, 25);
        }

        String[] matchLines = matchText.split("\\n");

        double oddsBonus = 0;

        for (String line : matchLines) {

            String[] parts = line.split("\\|");

            if (parts.length >= 3) {

                try {

                    double overOdds =
                            Double.parseDouble(parts[1].trim());

                    double underOdds =
                            Double.parseDouble(parts[2].trim());

                    if (overOdds < underOdds) {
                        oddsBonus += 3;
                    } else {
                        oddsBonus -= 3;
                    }

                } catch (Exception ignored) {
                }
            }
        }

        overScore += Math.min(oddsBonus, 15);
        overScore = Math.max(5, Math.min(95, overScore));

        double underScore = 100 - overScore;

        String signal;

        if (overScore >= 65) {
            signal = "OVER 2.5";
        } else if (underScore >= 65) {
            signal = "UNDER 2.5";
        } else {
            signal = "WAIT";
        }

        double confidenceValue =
                Math.max(overScore, underScore);

        result.setText(signal);

        confidence.setText(
                String.format(
                        Locale.US,
                        "Confidence: %.0f%%",
                        confidenceValue
                )
        );

        if (!bankrollText.isEmpty()) {

            try {

                double bank =
                        Double.parseDouble(
                                bankrollText.replace(",", "")
                        );

                double recommended =
                        bank * 0.05;

                stake.setText(
                        String.format(
                                Locale.US,
                                "Recommended stake: ₦%.2f",
                                recommended
                        )
                );

            } catch (Exception e) {

                stake.setText(
                        "Recommended stake: Check bankroll"
                );
            }

        } else {

            stake.setText(
                    "Recommended stake: Enter bankroll"
            );
        }
    }

    TextView sectionTitle(String value) {
        return text(value, 13, MUTED, true);
    }

    EditText input(String hint) {

        EditText e = new EditText(this);

        e.setHint(hint);
        e.setHintTextColor(Color.rgb(120, 140, 165));
        e.setTextColor(TEXT);
        e.setTextSize(16);
        e.setPadding(20, 18, 20, 18);
        e.setBackgroundColor(CARD);

        return e;
    }

    TextView text(
            String value,
            float size,
            int color,
            boolean bold
    ) {

        TextView t = new TextView(this);

        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);

        if (bold) {
            t.setTypeface(
                    Typeface.DEFAULT,
                    Typeface.BOLD
            );
        }

        return t;
    }

    void addSpace(LinearLayout layout, int pixels) {

        Space space = new Space(this);

        layout.addView(
                space,
                new LinearLayout.LayoutParams(
                        1,
                        pixels
                )
        );
    }
}

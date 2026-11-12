package com.ou25pro.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    EditText resultsInput;
    EditText oddsInput;
    EditText bankrollInput;
    TextView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setPadding(28, 28, 28, 28);
        main.setBackgroundColor(Color.rgb(10, 15, 22));

        TextView title = new TextView(this);
        title.setText("OVER 2.5 PRO");
        title.setTextColor(Color.WHITE);
        title.setTextSize(28);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 20, 0, 10);

        TextView subtitle = new TextView(this);
        subtitle.setText("Virtual Football Prediction Assistant");
        subtitle.setTextColor(Color.LTGRAY);
        subtitle.setTextSize(14);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(0, 0, 0, 25);

        main.addView(title);
        main.addView(subtitle);

        resultsInput = createInput(
                "Previous results",
                "Example: 3-1, 2-2, 1-3, 4-0, 2-1"
        );

        oddsInput = createInput(
                "Over 2.5 odds",
                "Example: 1.70"
        );

        bankrollInput = createInput(
                "Bankroll (₦)",
                "Example: 10000"
        );

        main.addView(resultsInput);
        main.addView(oddsInput);
        main.addView(bankrollInput);

        Button predictButton = new Button(this);
        predictButton.setText("ANALYZE MATCH");
        predictButton.setTextSize(16);
        predictButton.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

        LinearLayout.LayoutParams buttonParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        buttonParams.setMargins(0, 20, 0, 15);
        main.addView(predictButton, buttonParams);

        resultView = new TextView(this);
        resultView.setTextColor(Color.WHITE);
        resultView.setTextSize(16);
        resultView.setPadding(20, 20, 20, 20);
        resultView.setBackgroundColor(Color.rgb(25, 32, 42));

        resultView.setText(
                "Prediction result will appear here."
        );

        main.addView(resultView);

        predictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                analyzeMatch();
            }
        });

        setContentView(main);
    }

    private EditText createInput(String hint, String example) {

        EditText input = new EditText(this);

        input.setHint(hint + "\n" + example);
        input.setHintTextColor(Color.GRAY);
        input.setTextColor(Color.WHITE);
        input.setTextSize(15);
        input.setPadding(18, 18, 18, 18);
        input.setSingleLine(false);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        params.setMargins(0, 8, 0, 8);

        input.setLayoutParams(params);

        return input;
    }

    private void analyzeMatch() {

        String results = resultsInput.getText().toString().trim();
        String oddsText = oddsInput.getText().toString().trim();
        String bankrollText = bankrollInput.getText().toString().trim();

        if (results.isEmpty() || oddsText.isEmpty()) {
            resultView.setText(
                    "Please enter previous results and Over 2.5 odds."
            );
            return;
        }

        double odds;

        try {
            odds = Double.parseDouble(oddsText);
        } catch (Exception e) {
            resultView.setText("Invalid odds. Example: 1.70");
            return;
        }

        int overCount = 0;
        int totalCount = 0;

        String[] matches = results.split(",");

        for (String match : matches) {

            String[] score = match.trim().split("-");

            if (score.length == 2) {

                try {
                    int home = Integer.parseInt(score[0].trim());
                    int away = Integer.parseInt(score[1].trim());

                    totalCount++;

                    if ((home + away) >= 3) {
                        overCount++;
                    }

                } catch (Exception ignored) {
                }
            }
        }

        if (totalCount == 0) {
            resultView.setText(
                    "I couldn't read the previous results.\n\n" +
                    "Use format such as:\n" +
                    "3-1, 2-2, 1-0, 4-1"
            );
            return;
        }

        double historicalRate =
                ((double) overCount / totalCount) * 100.0;

        double confidence = historicalRate;

        if (odds >= 1.70 && odds <= 2.00) {
            confidence += 5;
        }

        if (confidence > 95) {
            confidence = 95;
        }

        String signal;

        if (confidence >= 70) {
            signal = "OVER 2.5 — STRONG";
        } else if (confidence >= 55) {
            signal = "OVER 2.5 — WATCH";
        } else {
            signal = "WAIT / NO BET";
        }

        String stakeText = "";

        if (!bankrollText.isEmpty()) {

            try {
                double bankroll =
                        Double.parseDouble(bankrollText);

                double stake = bankroll * 0.05;

                stakeText =
                        "\nSuggested stake: ₦" +
                        String.format("%.0f", stake);

            } catch (Exception ignored) {
            }
        }

        String output =
                "OVER 2.5 PRO\n\n" +
                "Matches analyzed: " + totalCount + "\n" +
                "Previous Over 2.5: " + overCount + "\n" +
                "Historical rate: " +
                String.format("%.1f", historicalRate) + "%\n" +
                "Odds: " + odds + "\n\n" +
                "CONFIDENCE: " +
                String.format("%.1f", confidence) + "%\n\n" +
                "SIGNAL: " + signal +
                stakeText +
                "\n\n" +
                "This is a statistical decision-support signal, " +
                "not a guaranteed prediction.";

        resultView.setText(output);
    }
}

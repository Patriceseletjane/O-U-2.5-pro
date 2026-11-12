package com.ou25pro.app;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.*;

import java.util.Locale;

public class MainActivity extends Activity {
    EditText bankroll, previousGoals, matches;
    TextView result;

    private TextView label(String s) {
        TextView v = new TextView(this);
        v.setText(s);
        v.setTextColor(Color.rgb(155,180,210));
        v.setTextSize(14);
        v.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        v.setPadding(0,12,0,6);
        return v;
    }

    private EditText input(String hint) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setHintTextColor(Color.rgb(110,135,165));
        e.setTextColor(Color.WHITE);
        e.setTextSize(16);
        e.setPadding(14,10,14,10);
        e.setBackgroundColor(Color.rgb(15,32,53));
        return e;
    }

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);

        ScrollView scroll = new ScrollView(this);
        LinearLayout c = new LinearLayout(this);
        c.setOrientation(LinearLayout.VERTICAL);
        c.setPadding(18,18,18,24);
        c.setBackgroundColor(Color.rgb(5,14,28));

        TextView title = new TextView(this);
        title.setText("O/U 2.5 PRO");
        title.setTextColor(Color.WHITE);
        title.setTextSize(30);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        c.addView(title);

        TextView sub = new TextView(this);
        sub.setText("OVER 2.5 + UNDER 2.5 DECISION SUPPORT");
        sub.setTextColor(Color.rgb(145,170,205));
        sub.setTextSize(15);
        sub.setGravity(Gravity.CENTER);
        sub.setPadding(0,4,0,18);
        c.addView(sub);

        c.addView(label("BANKROLL"));
        bankroll = input("Enter bankroll (₦)");
        c.addView(bankroll);

        c.addView(label("PREVIOUS TOTAL GOALS"));
        previousGoals = input("Example: 3,1,4,2,5,3,2,1,4,3");
        c.addView(previousGoals);

        c.addView(label("MATCHES + ODDS"));
        matches = input("Match 1 | 1.80 | 1.90\nMatch 2 | 1.70 | 2.05\nMatch 3 | 1.85 | 1.88");
        matches.setMinLines(6);
        c.addView(matches);

        Button analyze = new Button(this);
        analyze.setText("ANALYZE O/U 2.5");
        analyze.setTextColor(Color.WHITE);
        analyze.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        analyze.setBackgroundColor(Color.rgb(38,105,180));
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(-1,58);
        bp.setMargins(0,16,0,16);
        c.addView(analyze,bp);

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(16,16,16,16);
        card.setBackgroundColor(Color.rgb(15,32,53));

        card.addView(label("PRIMARY SIGNAL"));
        result = new TextView(this);
        result.setText("WAITING FOR ANALYSIS\n\nConfidence: --\nRecommended stake: --");
        result.setTextColor(Color.WHITE);
        result.setTextSize(21);
        result.setGravity(Gravity.CENTER);
        result.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        card.addView(result);
        c.addView(card);

        TextView note = new TextView(this);
        note.setText("⚠ Decision support only. No prediction can guarantee a winning result.");
        note.setTextColor(Color.rgb(145,165,190));
        note.setPadding(0,14,0,0);
        c.addView(note);

        analyze.setOnClickListener(v -> analyze());
        scroll.addView(c);
        setContentView(scroll);
    }

    private void analyze() {
        String g = previousGoals.getText().toString().trim();
        if (g.isEmpty()) {
            result.setText("ENTER DATA\n\nAdd previous total goals first.");
            return;
        }

        String[] a = g.split("[,\\s]+");
        int n=0, over=0; double sum=0;
        for (String s : a) {
            try {
                double x=Double.parseDouble(s);
                n++; sum+=x;
                if (x>=3) over++;
            } catch(Exception ignored) {}
        }

        if(n==0) {
            result.setText("INVALID GOAL DATA\n\nExample: 3,1,4,2,5,3");
            return;
        }

        double rate=(double)over/n, avg=sum/n;
        String signal = (rate>=0.60 && avg>=2.5) ? "OVER 2.5"
                : (rate<=0.40 && avg<2.5) ? "UNDER 2.5" : "WAIT";

        double confidence=Math.min(95,50+Math.abs(rate-.5)*80+Math.min(10,Math.abs(avg-2.5)*8));
        String stake="--";
        try {
            double b=Double.parseDouble(bankroll.getText().toString().replace(",","").trim());
            stake=String.format(Locale.US,"₦%.0f",b*0.05);
        } catch(Exception ignored) {}

        result.setText(signal+"\n\nConfidence: "+String.format(Locale.US,"%.0f%%",confidence)
                +"\nRecommended stake: "+stake
                +"\n\nSample: "+n+" results"
                +"\nOver rate: "+String.format(Locale.US,"%.0f%%",rate*100)
                +"\nAverage goals: "+String.format(Locale.US,"%.2f",avg));
    }
}

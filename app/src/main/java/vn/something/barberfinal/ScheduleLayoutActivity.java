package vn.something.barberfinal;

import android.os.Bundle;
import android.util.TypedValue;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ScheduleLayoutActivity extends AppCompatActivity {
    private TableLayout scheduleTable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_schedule_layout);

        setContentView(R.layout.activity_schedule_layout); // Replace with your layout
        scheduleTable = findViewById(R.id.scheduleTable); // Ensure your TableLayout has this ID
        generateSchedule();
    }
    private void generateSchedule() {
        // Timestamps (Rows)
        String[] timestamps = {"11:00", "12:00", "13:00", "14:00", "15:00", "16:00"};

        // Current Date
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());


        // Add header row for Dates
        TableRow headerRow = new TableRow(this);
        addTextViewToRow(headerRow, "", true); // empty first cell
        for (int day = 0; day < 7; day++){
            String formattedDate = dateFormat.format(calendar.getTime());
            addTextViewToRow(headerRow, formattedDate, true);
            calendar.add(Calendar.DATE, 1); // Increment to the next day
        }
        scheduleTable.addView(headerRow);

        calendar = Calendar.getInstance(); //Reset Calendar for subsequent rows

        // Generate Schedule Table
        for(String timestamp: timestamps) {

            TableRow tableRow = new TableRow(this);
            addTextViewToRow(tableRow, timestamp, true); // Add timestamp as the first cell

            // Generate Column For each Date
            for (int day = 0; day < 7; day++) {
                addTextViewToRow(tableRow, "", false);  // create an empty text view
                calendar.add(Calendar.DATE, 1);
            }
            calendar.setTime(new Date()); // Reset calendar
            scheduleTable.addView(tableRow);
        }
    }
    private void addTextViewToRow(TableRow row, String text, boolean isBold){
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(20,20,20,20);
        if(isBold){
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16); // Set larger text size for date headers
            textView.setTypeface(textView.getTypeface(), android.graphics.Typeface.BOLD);
        }

        row.addView(textView);

    }
}
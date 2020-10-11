package rickysinclair.com.au;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * The Records class displays the values from the array list onto the devices screen
 */
public class Records extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);
        DisplayImage obj = new DisplayImage(); //create object of DisplayImage class

        LinearLayout LinearLayoutView = new LinearLayout(this);
        TextView DisplayStringArray = new TextView(this);

        DisplayStringArray.setTextSize(18); //set text size for display
        DisplayStringArray.append("Date_Time                    " + "Munsell       " + " RGB"); //create headers
        DisplayStringArray.append("\n\n");
        LinearLayoutView.addView(DisplayStringArray);
        DisplayStringArray.setTypeface(null, Typeface.NORMAL); //set text style

        //loop through array list and write values to text view
        for (int i = 0; i < obj.getRgbList().size(); i++) {
            DisplayStringArray.append(obj.getDateList().get(i) + "    " + obj.getRgbList().get(i) + "    " + obj.getHvcList().get(i));
            DisplayStringArray.append("\n");
        }
        setContentView(LinearLayoutView);

    }
}


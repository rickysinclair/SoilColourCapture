package rickysinclair.com.au;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class Records extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);
        DisplayImage obj = new DisplayImage();
        Log.d("BIG DOG WORKING HARD", "Here we go!");


        for (String rgb : obj.getRgbList()) {
            Log.d("RGB", rgb);
        }
        for (String hvc : obj.getHvcList()) {
            Log.d("RGB", hvc);
        }
        for (String date : obj.getDateList()) {
            Log.d("RGB", date);
        }
    }
}


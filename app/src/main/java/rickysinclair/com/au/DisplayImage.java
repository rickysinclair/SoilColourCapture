package rickysinclair.com.au;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DisplayImage extends AppCompatActivity {

    ImageView imageView;
    ArrayList<String> table = new ArrayList<String>();
    private static ArrayList<String> rgbList = new ArrayList<String>();
    private static ArrayList<String> hvcList = new ArrayList<String>();
    private static ArrayList<String> dateList = new ArrayList<String>();

    DateTimeFormatter myFormat = DateTimeFormatter.ofPattern("ddMMyyyy_hh:mm:ss");
    //Local date time instance
    ZoneId z = ZoneId.systemDefault(); //get the JVM’s current default time zone: ZoneId.systemDefault()
    ZonedDateTime localDateTime = ZonedDateTime.now(z);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        imageView = findViewById(R.id.mimageView);
        Bitmap bitmap = BitmapFactory.decodeFile(getIntent().getStringExtra("image_path"));
        imageView.setImageBitmap(bitmap);
    }

    public void convertValues(View view) {
        TextView tvHVC = findViewById(R.id.calculatedHVC);
        TextView tvRGB = findViewById(R.id.calculatedRGB);

        Bitmap bitmap = BitmapFactory.decodeFile(getIntent().getStringExtra("image_path"));


        //Get formatted String
        String dateTimeString = myFormat.format(localDateTime);

        try {
            table = readCSV(); // Create an ArrayList object
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            int index = getMinValue(findClosestRGB(rgbSample(bitmap), table));
//            Log.d("Distance", String.valueOf(index));
            String convertedVal = displayMunsell(index, table);
//            Log.d("munsell value", convertedVal);
            String[] parts = convertedVal.split(",");
            tvHVC.setText(parts[0]);
            tvRGB.setText(parts[1]);

            rgbList.add(parts[0]);
            hvcList.add(parts[1]);
            dateList.add(dateTimeString);

            for (String rgb : rgbList) {
                Log.d("RGB", rgb);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getRgbList() {
        return rgbList;
    }

    public static ArrayList<String> getHvcList() {
        return hvcList;
    }

    public static ArrayList<String> getDateList() {
        return dateList;
    }

    public String rgbSample(Bitmap bitmap) {
        int avgR = 0;
        int avgG = 0;
        int avgB = 0;

        int x;
        int y;

        int pixelCount = 0;
        int pixels = 1;

        EditText editText = findViewById(R.id.sampleSize);
        String temp = editText.getText().toString();
        if (!"".equals(temp)) {
            pixels = Integer.parseInt(temp);
        }
//        Log.d("staunch", String.valueOf(pixels));

        int imgWidth = bitmap.getWidth() / 2;
        int imgHeight = bitmap.getHeight() / 2;
        int colour;

        for (int i = (-pixels); i < pixels; i++) {
            for (int j = (-pixels); j < pixels; j++) {

                pixelCount++;
                x = imgHeight + i;
                y = imgWidth + j;

                colour = bitmap.getPixel(x, y);

//                Log.d("Pixel: ", String.valueOf(colour));
//                Log.d("Coordinates", "x: " + x + " y: " + y);
//                Log.d("Pixel count:", String.valueOf(pixelCount));
                avgR += Color.red(colour);
                avgG += Color.green(colour);
                avgB += Color.blue(colour);
//                Log.d("Colour Values: ", avgR + "," + avgG + "," + avgB);
            }
        }
        avgR = avgR / (pixelCount);
        avgG = avgG / (pixelCount);
        avgB = avgB / (pixelCount);
        String colourVal = avgR + "," + avgG + "," + avgB;
//        Log.d("RGB Value: ", colourVal);
        return colourVal;
    }

    public ArrayList<Double> findClosestRGB(String rgbValues, ArrayList<String> table) throws IOException {

//        rgbValues = "46,22,21";
//        Log.d("Passed", rgbValues);
        String[] test = rgbValues.split(",");
        int r1 = Integer.parseInt(test[0]);
        int g1 = Integer.parseInt(test[1]);
        int b1 = Integer.parseInt(test[2]);

        ArrayList<Double> numbers = new ArrayList<Double>(); // Create an ArrayList object

        for (String line : table) {
            String[] parts = line.split(",");
//            Log.d("Line", parts[1]);
            String[] rgb = parts[1].split("_");

            double dist;
            dist = (Math.pow(((Integer.parseInt(rgb[0]) - r1)), 2)
                    + Math.pow(((Integer.parseInt(rgb[1]) - g1)), 2)
                    + Math.pow(((Integer.parseInt(rgb[2]) - b1)), 2)); //euclidean distance
//            Log.d("Distance", String.valueOf(dist));
            numbers.add(dist);
        }
        return numbers;
    }

    public ArrayList<String> readCSV() throws IOException {
        ArrayList<String> table = new ArrayList<>(); // Create an ArrayList object

        InputStreamReader file = new InputStreamReader(getAssets().open("munsell.csv"));

        BufferedReader reader = new BufferedReader(file);
        reader.readLine();
        String line;
        while ((line = reader.readLine()) != null) {
            table.add(line);
//            Log.d("Zenith", line);
        }
        return table;
    }

    public static int getMinValue(ArrayList<Double> numbers) {
        double minValue = numbers.get(0);
        int index = 0;
        int place = 0;
        for (Double number : numbers) {
            if (number < minValue || number == minValue) {
                minValue = number;
                place = index;
                index++;
            } else index++;
        }
        return place;
    }

    public String displayMunsell(int index, ArrayList<String> table) {
        return table.get(index);
    }

}

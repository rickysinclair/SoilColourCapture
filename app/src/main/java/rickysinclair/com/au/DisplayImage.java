package rickysinclair.com.au;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
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
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.O)
/**
 * The DisplayImage class displays the image from the camera, analyses a group of pixels and calculates their average RGB values,
 * converts the RGB values into their closest Munsell values based on a CSV file and adds all of the values into an ArrayList.
 */
public class DisplayImage extends AppCompatActivity {

    ImageView imageView;

    // initialise array lists
    ArrayList<String> table = new ArrayList<String>();
    private static ArrayList<String> rgbList = new ArrayList<String>();
    private static ArrayList<String> hvcList = new ArrayList<String>();
    private static ArrayList<String> dateList = new ArrayList<String>();

    DateTimeFormatter myFormat = DateTimeFormatter.ofPattern("ddMMyyyy_hh:mm:ss"); //local date time instance
    ZoneId z = ZoneId.systemDefault(); //get the JVM’s current default time zone
    ZonedDateTime localDateTime = ZonedDateTime.now(z); //set date time

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        imageView = findViewById(R.id.mimageView);
        Bitmap bitmap = BitmapFactory.decodeFile(getIntent().getStringExtra("image_path")); //get saved image
        imageView.setImageBitmap(bitmap); //place image on image view
    }

    /**
     * The convertValues method displays the converted Munsell values.
     *
     * @param view
     */
    public void convertValues(View view) {
        TextView tvHVC = findViewById(R.id.calculatedHVC);
        TextView tvRGB = findViewById(R.id.calculatedRGB);

        Bitmap bitmap = BitmapFactory.decodeFile(getIntent().getStringExtra("image_path")); //get saved image

        String dateTimeString = myFormat.format(localDateTime); //get formatted String

        try {
            table = readCSV(); // create an ArrayList object from CSV
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            //determine the index value of the closest matching RGB to Munsell
            int index = getMinValue(findClosestRGB(rgbSample(bitmap), table));
            String convertedVal = displayMunsell(index, table);

            //display calculated RGB and Munsell values
            String[] parts = convertedVal.split(",");
            tvHVC.setText(parts[0]);
            tvRGB.setText(parts[1]);

            //add values and date to arraylist
            rgbList.add(parts[0]);
            hvcList.add(parts[1]);
            dateList.add(dateTimeString);

        } catch (IOException e) {
            e.printStackTrace();
        }
        //show message after save
        Context context = getApplicationContext();
        CharSequence text = "Munsell Values Saved!";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    /**
     * The following getters return the arraylists for RGB, Munsell and DateTime.
     *
     * @return
     */
    public static ArrayList<String> getRgbList() {
        return rgbList;
    }

    public static ArrayList<String> getHvcList() {
        return hvcList;
    }

    public static ArrayList<String> getDateList() {
        return dateList;
    }

    /**
     * The rgbSample method analyses the camera image and determines the RGB values of
     * a determined set of pixels.
     *
     * @param bitmap
     * @return
     */
    public String rgbSample(Bitmap bitmap) {

        int avgR = 0;
        int avgG = 0;
        int avgB = 0;
        int x;
        int y;
        int pixelCount = 0;
        int pixels = 1; //default number of analysed pixels
        int imgWidth = bitmap.getWidth() / 2; //determine size of image
        int imgHeight = bitmap.getHeight() / 2;
        int colour;

        //read user entered pixel grid value from edit text box
        EditText editText = findViewById(R.id.sampleSize);
        String temp = editText.getText().toString();
        if (!"".equals(temp)) {
            pixels = Integer.parseInt(temp);
        }

        //analyse each pixel in the determined grid
        for (int i = (-pixels); i < pixels; i++) {
            for (int j = (-pixels); j < pixels; j++) {
                pixelCount++;
                x = imgHeight + i;
                y = imgWidth + j;
                colour = bitmap.getPixel(x, y); //analyse pixel

                //read RGB value of each pixel
                avgR += Color.red(colour);
                avgG += Color.green(colour);
                avgB += Color.blue(colour);
            }
        }

        //calculate average RGB values
        avgR = avgR / (pixelCount);
        avgG = avgG / (pixelCount);
        avgB = avgB / (pixelCount);
        String colourVal = avgR + "," + avgG + "," + avgB;

        return colourVal;
    }

    /**
     * The findClosestRGB method uses the "euclidean distance" formula to match the RGB values from
     * the analysed image and the MunsellRGB.csv file to determine the closest matching RGB values
     *
     * @param rgbValues
     * @param table
     * @return
     * @throws IOException
     */
    public ArrayList<Double> findClosestRGB(String rgbValues, ArrayList<String> table) throws IOException {

        String[] test = rgbValues.split(",");
        int r1 = Integer.parseInt(test[0]);
        int g1 = Integer.parseInt(test[1]);
        int b1 = Integer.parseInt(test[2]);

        ArrayList<Double> numbers = new ArrayList<Double>(); // Create an ArrayList object

        for (String line : table) {
            String[] parts = line.split(",");
            String[] rgb = parts[1].split("_");

            double dist;
            dist = (Math.pow(((Integer.parseInt(rgb[0]) - r1)), 2)
                    + Math.pow(((Integer.parseInt(rgb[1]) - g1)), 2)
                    + Math.pow(((Integer.parseInt(rgb[2]) - b1)), 2)); //euclidean distance
            numbers.add(dist);
        }
        return numbers;
    }

    /**
     * The readCSV method uses the "munsell.csv" file to create an arraylist from the Munsell and RGB values
     * to use for comparison and calculations
     *
     * @return
     * @throws IOException
     */
    public ArrayList<String> readCSV() throws IOException {
        ArrayList<String> table = new ArrayList<>(); //create an ArrayList object
        InputStreamReader file = new InputStreamReader(getAssets().open("munsell.csv")); //read CSV file
        BufferedReader reader = new BufferedReader(file);
        reader.readLine();
        String line;
        while ((line = reader.readLine()) != null) {  //add each line from CSV to the arraylist
            table.add(line);
        }
        return table;
    }

    /**
     * The getMinValue calulates the minimum value of the Euclidean Distance
     *
     * @param numbers
     * @return
     */
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

    /**
     * The displayMunsell method finds the index of the closest Munsell value to the RGB readings
     *
     * @param index
     * @param table
     * @return
     */
    public String displayMunsell(int index, ArrayList<String> table) {
        return table.get(index);
    }

}

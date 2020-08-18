package rickysinclair.com.au;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class DisplayImage extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        imageView = findViewById(R.id.mimageView);

        Bitmap bitmap = BitmapFactory.decodeFile(getIntent().getStringExtra("image_path"));
        imageView.setImageBitmap(bitmap);
        String colourVals = rgbSample(bitmap);
        Log.d("Final Colour Values =",colourVals);
    }

    public String rgbSample(Bitmap bitmap){
        int avgR = 0;
        int avgG = 0;
        int avgB = 0;

        int x;
        int y;

        int pixels = 2;

        int pixelCount = 0;

        int imgWidth = bitmap.getWidth()/2;
        int imgHeight = bitmap.getHeight()/2;
        int colour;

        for (int i = (-pixels); i < pixels; i++) {
            for (int j = (-pixels); j < pixels; j++) {

                pixelCount ++;
                x = imgHeight + i;
                y = imgWidth + j;

                colour = bitmap.getPixel(x,y);

                Log.d("Pixel: ", String.valueOf(colour));
                Log.d("Coordinates", "x: "+ x + " y: "+ y);
                Log.d("Pixel count:", String.valueOf(pixelCount));
                avgR += Color.red(colour);
                avgG += Color.green(colour);
                avgB += Color.blue(colour);
                Log.d("Colour Values: ",avgR + "," + avgG + "," + avgB);
            }
       }
        avgR = avgR / (pixelCount);
        avgG = avgG / (pixelCount);
        avgB = avgB / (pixelCount);
        String colourVal=  avgR + "," + avgG + "," + avgB;
        return colourVal;
    }
}

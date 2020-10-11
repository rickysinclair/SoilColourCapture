package rickysinclair.com.au;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The MainActivity class contains the methods to capture images from the device camera and save these images
 * on the local device storage.
 */
public class MainActivity extends AppCompatActivity {

    String currentImagePath = null;
    public static final int IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * The captureImage method uses the devices camera to capture an image
     *
     * @param view
     */
    public void captureImage(View view) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File imageFile = null;
            try {
                imageFile = getImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (imageFile != null) {
                Uri imageUri = FileProvider.getUriForFile(this, "com.example.android.fileprovider", imageFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cameraIntent, IMAGE_REQUEST);
            }
        }
    }

    /**
     * The displayRecords method uses an intent to launch the display_image activity.
     */
    public void displayRecords(View view) {
        Intent intent = new Intent(this, Records.class);
        startActivity(intent);
    }

    /**
     * The displayImage method uses an intent to launch the display_image activity.
     */
    public void displayImage(View view) {
        Intent intent = new Intent(this, DisplayImage.class);
        intent.putExtra("image_path", currentImagePath);
        startActivity(intent);
    }

    /**
     * The getImageFile method returns the image taken from the camera and creates the
     * file name and extension for the saved image
     *
     * @return
     * @throws IOException
     */
    private File getImageFile() throws IOException {
        Date date = new Date();
        SimpleDateFormat DateFor = new SimpleDateFormat("ddMMyyyy");
        String timeStamp = DateFor.format(date);
        String imageName = "jpg_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File imageFile = File.createTempFile(imageName, ".jpg", storageDir);
        currentImagePath = imageFile.getAbsolutePath();
        return imageFile;
    }
}

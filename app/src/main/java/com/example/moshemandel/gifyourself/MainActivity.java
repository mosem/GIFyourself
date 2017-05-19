package com.example.moshemandel.gifyourself;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView mImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = (ImageView) findViewById(R.id.imageViewId);
    }

    public void takePhoto(View view) {
        dispatchTakePictureIntent();
    }


    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

/*
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
*/


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Toast.makeText(this,"image captured",
                    Toast.LENGTH_SHORT).show();
            Intent serverIntent = new Intent(this,GifActivity.class);

            File origFile = new File(mCurrentPhotoPath);
            String origFileSize = String.valueOf(origFile.length()/1024);
            String compressedImgPath = null;
            try {
                compressedImgPath = compressImage(mCurrentPhotoPath);
            } catch(IOException e){
                Log.d("MainActivity", "IOException!!!!!!");
            }

            File compressedFile = new File(compressedImgPath);
            String newFileSize = String.valueOf(compressedFile.length()/1024);
            Log.d("ServerComm", origFileSize + ", " + newFileSize);

            serverIntent.putExtra("imgPath",compressedImgPath);
//            serverIntent.putExtra("imgPath",mCurrentPhotoPath);
            startActivity(serverIntent);
            /*File imgFile = new  File(mCurrentPhotoPath);

            if(imgFile.exists()){
                Bitmap imageBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                mImageView.setImageBitmap(imageBitmap);
            }*/
        }
    }

    private String compressImage(String path) throws IOException {
        File dir= getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Bitmap b= BitmapFactory.decodeFile(path);
        Bitmap out = Bitmap.createScaledBitmap(b, 1080, 1080, false);

        String filename=path.substring(path.lastIndexOf("/")+1, path.lastIndexOf("."));
        File image = File.createTempFile(
                filename,  /* prefix */
                ".jpg",         /* suffix */
                dir      /* directory */
        );
        File file = new File(dir, "resize.png");
        FileOutputStream fOut;
        try {
            fOut = new FileOutputStream(file);
            out.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.flush();
            fOut.close();
            b.recycle();
            out.recycle();
        } catch (Exception e) {}
        return file.getAbsolutePath();
    }
}

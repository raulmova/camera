package com.example.raul.camaraex;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button bTomar, bGaleria;
    private ImageView ivFoto;
    private TextView tvUrl;
    private static final int SELECT_PHOTO = 100;
    private static  final int REQUEST_INTERNAL_STORAGE = 1;
    private static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_IMAGE_CAPTURE = 100;
    private static final int SELECT_PICTURE = 100;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bTomar = (Button) findViewById(R.id.bCamera);
        bGaleria = (Button) findViewById(R.id.bGallery);
        ivFoto = (ImageView) findViewById(R.id.imageView);
        tvUrl = (TextView) findViewById(R.id.tvUrl);

        bTomar.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checarPermisosCamara();
                if(isCameraPermissionGranted()) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }

            }
        });

        bGaleria.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checarPermisosStorage();
                if(isStoragePermissionGranted()) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_INTERNAL_STORAGE);
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ivFoto.setImageBitmap(imageBitmap);
        }

        if (requestCode == REQUEST_INTERNAL_STORAGE && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            if (null != selectedImageUri) {
                // Get the path from the Uri
                String path = getPathFromURI(selectedImageUri);
                tvUrl.setText(path);
                Log.i(TAG, "Image Path : " + path);
                // Set the image in ImageView
                ivFoto.setImageURI(selectedImageUri);
            }
        }
    }

    /* Get the real path from the URI */
    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    private void checarPermisosCamara() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {


            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void checarPermisosStorage() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {


            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_INTERNAL_STORAGE);
            }
        }
    }

    private boolean isCameraPermissionGranted() {
        int permission = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA);

        return permission == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isStoragePermissionGranted() {
        int permission = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        return permission == PackageManager.PERMISSION_GRANTED;
    }

}

package com.example.chatproject5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import java.io.InputStream;

public class PlusMenuActivity extends AppCompatActivity implements AutoPermissionsListener {

    Intent intent;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_plus_menu);

        imageView = findViewById(R.id.imageView_menu);
        Button camera_button = findViewById(R.id.camera_button);
        Button album_button = findViewById(R.id.album_button);

        camera_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivity(intent);

                startActivityForResult(intent, 101);
            }
        });

        album_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openAlbum();

//                intent = new Intent(getApplicationContext(), AlbumActivity.class);
//                startActivity(intent);
            }
        });
        //AutoPermissions : 외부라이브러리에 추가해서 이용함
        AutoPermissions.Companion.loadAllPermissions(this, 101);
    }

    public void close_menu(View v) {
        finish();
    }


    public void openAlbum() {
        Intent intent = new Intent();

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 101); //requestCode 로 구분해줌

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101) {
            if(resultCode == RESULT_OK) { //실행 성공시
                Uri fileUri = data.getData();

                ContentResolver resolver = getContentResolver();

                try {
                    InputStream inputStream = resolver.openInputStream(fileUri);
                    Bitmap imgBitmap = BitmapFactory.decodeStream(inputStream);
                    imageView.setImageBitmap(imgBitmap);

//                    imageView.setImageURI(data.getData());
                    inputStream.close(); //io 닫기

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
    }

    @Override
    public void onDenied(int i, String[] strings) {

    }

    @Override
    public void onGranted(int i, String[] strings) {

    }
}
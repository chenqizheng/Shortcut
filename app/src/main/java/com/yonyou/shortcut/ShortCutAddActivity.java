package com.yonyou.shortcut;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ShortCutAddActivity extends AppCompatActivity {

    private static final int PHOTO_REQUEST_GALLERY = 1;
    private static final int PHOTO_REQUEST_CAREMA = 2;
    private static final String PHOTO_FILE_NAME = "icon.jpg";
    private static final int PHOTO_REQUEST_CROP = 3;
    private static final int MY_PERMISSIONS_REQUEST_EX = 10;
    private EditText nameEt;
    private EditText valueEt;
    private Spinner actionSpinner;
    private ImageView mImageView;
    private ArrayList<Action> actions;
    private File tempFile;
    private Bitmap bmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nameEt = (EditText) findViewById(R.id.edit_name);
        valueEt = (EditText) findViewById(R.id.edit_action_value);
        actionSpinner = (Spinner) findViewById(R.id.spinner_action);
        mImageView = (ImageView) findViewById(R.id.imageView);

        findViewById(R.id.create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleCreateClick();
            }
        });
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSelectImage();
            }
        });

        actions = ShortCutUtils.getAllAction();
        ArrayAdapter<Action> actionAdapter = new ArrayAdapter<Action>(this, android.R.layout.simple_spinner_item, actions);
        actionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        actionSpinner.setAdapter(actionAdapter);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_EX);
        } else {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_EX) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                // Permission Denied
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                finish();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void handleSelectImage() {
        new AlertDialog.Builder(this).setTitle("列表框").setItems(
                new String[]{"相册", "拍照"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            gallery();
                        } else {
                            camera();
                        }
                    }
                }).show();
    }

    private void handleCreateClick() {
        if ("".equals(nameEt.getText().toString())) {
            Toast.makeText(this, "Please Input Name", Toast.LENGTH_SHORT).show();
            return;
        }

        Action action = (Action) actionSpinner.getSelectedItem();
        Action shortCutAction = null;
        if (action != null) {
            try {
                shortCutAction = (Action) action.clone();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        shortCutAction.setName(nameEt.getText().toString());
        shortCutAction.setValue(valueEt.getText().toString());
        ShortCutUtils.createShortCut(getApplicationContext(), shortCutAction, bmap);
        new ActionDao(this).add(shortCutAction);
        Toast.makeText(this, "Create Success", Toast.LENGTH_SHORT).show();
        finish();
    }


    /*
    *从相册获取
    */

    public void gallery() {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    /*
     * 从相机获取
     */
    public void camera() {
        // 激活相机
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        // 判断存储卡是否可以用，可用进行存储
        tempFile = new File(Environment.getExternalStorageDirectory(),
                PHOTO_FILE_NAME);
        // 从文件中创建uri
        Uri uri = Uri.fromFile(tempFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
        startActivityForResult(intent, PHOTO_REQUEST_CAREMA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PHOTO_REQUEST_GALLERY:

                try {
                    Uri selectedImage = data.getData();
                    String picturePath = null;
                    String fromFile = selectedImage.getPath().split("/")[1];

                    //当照片是从相册选择的就进入第一个判断，不是从相册选择的进入第二个判断
                    if (fromFile.equals("storage")) {
                        picturePath = selectedImage.getPath();
                    } else {
                        String[] filePathColumns = {MediaStore.Images.Media.DATA};
                        Cursor c = this.getContentResolver().query(selectedImage, filePathColumns, null, null, null);
                        c.moveToNext();
                        int columnIndex = c.getColumnIndex(filePathColumns[0]);
                        picturePath = c.getString(columnIndex);
                        c.close();
                    }

                    cropPhoto(new File(picturePath));


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case PHOTO_REQUEST_CAREMA:

                cropPhoto(tempFile);
                break;

            case PHOTO_REQUEST_CROP:
                // 显示剪切的图像
                Uri uri = data.getData();
                if (uri != null) {
                    try {
                        bmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    bmap = data.getParcelableExtra("data");
                }
                mImageView.setImageBitmap(bmap);
                break;
        }
        }


    public void cropPhoto(File file) {
        Intent intent = new Intent();

        Uri mUri = Uri.fromFile(file);
        intent.setAction("com.android.camera.action.CROP");
        intent.setDataAndType(mUri, "image/*");// mUri是已经选择的图片Uri
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);// 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 128);// 输出图片大小
        intent.putExtra("outputY", 128);
        intent.putExtra("return-data", true);
        intent.putExtra("output", Uri.parse("file://" + file.getAbsolutePath() + ".temp"));
        startActivityForResult(intent, PHOTO_REQUEST_CROP);

    }
}

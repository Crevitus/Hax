package com.crevitus.hax;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CoarseGrainedActivity extends AppCompatActivity {

    TextView _txtExecutionList;
    final String DIRECTORY_PATH = "/test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coarse_grained);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        _txtExecutionList = (TextView)findViewById(R.id.txtExecutionList);

        Button btnReadFile = (Button)findViewById(R.id.btnReadFile);
        btnReadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _txtExecutionList.setText(getString(R.string.txt_execution_list));
                Thread background = new Thread(new Runnable() {
                    public void run() {
                        if (ContextCompat.checkSelfPermission(CoarseGrainedActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            updateExecutionMessage("\n" + getString(R.string.request_read_permission));
                            ActivityCompat.requestPermissions(CoarseGrainedActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    Constants.PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        } else {
                            listFiles();
                            updateExecutionMessage("\n" + getString(R.string.read_permissions_granted));
                            loadImage();
                            if (ContextCompat.checkSelfPermission(CoarseGrainedActivity.this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                updateExecutionMessage("\n" + getString(R.string.request_write_permission));
                                ActivityCompat.requestPermissions(CoarseGrainedActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        Constants.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                            } else {
                                updateExecutionMessage("\n" + getString(R.string.write_permissions_granted));
                                deleteFiles();
                                createFile();
                            }
                        }
                    }
                });
                background.start();
            }
        });
    }

    private void updateExecutionMessage(final String message)
    {
        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                _txtExecutionList.append(message);
            }
        });
        try{ Thread.sleep(500); }catch(InterruptedException e){ }
    }

    private void listFiles()
    {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + DIRECTORY_PATH;
        File dir = new File(path);
        final File files[] = dir.listFiles();
        updateExecutionMessage("\n" + getString(R.string.list_file_message));

        runOnUiThread(new Runnable(){
            @Override
            public void run(){
                TextView txtFileList = (TextView) findViewById(R.id.txtFileList);
                String filelist = getString(R.string.txt_file_list) + "\n";
                txtFileList.setText(filelist);
                for(File file : files )
                {
                    txtFileList.append(file.getAbsolutePath() + "\n");
                }
            }
        });
    }

    private void loadImage(){
        updateExecutionMessage("\n" + getString(R.string.read_test_image));
        String imageInSD = Environment.getExternalStorageDirectory().getAbsolutePath() + DIRECTORY_PATH + "/test.jpg";
        final Bitmap bitmap = BitmapFactory.decodeFile(imageInSD);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageView myImageView = (ImageView) findViewById(R.id.imageView);
                myImageView.setImageBitmap(bitmap);
            }
        });
    }

    private void deleteFiles()
    {
        updateExecutionMessage("\n" + getString(R.string.delete_file_message));
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + DIRECTORY_PATH + "/fileToDelete.txt");
        file.delete();
        listFiles();
    }

    private void createFile(){
        updateExecutionMessage("\n" + getString(R.string.create_file_message));
        String test = "test";
        File filepath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + DIRECTORY_PATH + "/fileToDelete.txt");
        try {
            FileOutputStream fos = new FileOutputStream(filepath);
            fos.write(test.getBytes());
            fos.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Thread background = new Thread(new Runnable() {
                        public void run() {
                            updateExecutionMessage("\n" + getString(R.string.read_permissions_granted));
                            loadImage();
                            listFiles();
                            updateExecutionMessage("\n" + getString(R.string.write_permissions_granted));
                            deleteFiles();
                            createFile();
                        }
                    });
                    background.start();
                }
                break;
            case Constants.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    updateExecutionMessage("\n" + getString(R.string.write_permissions_granted));
                    deleteFiles();
                    createFile();
                }
                break;
            default:
                break;
        }
    }
}

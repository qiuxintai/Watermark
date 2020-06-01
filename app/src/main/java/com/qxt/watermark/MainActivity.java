/*
 * Copyright 2020 Tyler Qiu.
 * YUV420 to RGBA open source project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qxt.watermark;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.ByteBuffer;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * @author Tyler Qiu
 * @date: 2020/05/12
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    ImageView mImageView;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = findViewById(R.id.image);
        mHandler = new Handler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "requestPermissions");
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onRequestPermissionsResult(final int requestCode,
                                           @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "All permission granted.");
            } else {
                Toast.makeText(this, "Storage permission required!!!", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.watermark) {
            addWatermark();
        } else if (v.getId() == R.id.original) {
            resetOriginal();
        }
    }

    private void addWatermark() {
        TaskExecutor.run(new Runnable() {
            @Override
            public void run() {
                final Context context = MainActivity.this;

                //read watermark and convert to RGBA
                byte[] watermarkJPEG = FileUtils.readRaw(context, R.raw.debug_watermark);
                final Bitmap watermarkBitmap = BitmapFactory.decodeByteArray(watermarkJPEG, 0, watermarkJPEG.length);
                int watermarkWidth = watermarkBitmap.getWidth();
                int watermarkHeight = watermarkBitmap.getHeight();
                byte[] watermark = new byte[watermarkWidth * watermarkHeight * 4];
                ByteBuffer watermarkBuffer = ByteBuffer.wrap(watermark);
                watermarkBitmap.copyPixelsToBuffer(watermarkBuffer);
                watermarkBuffer.rewind();

                //read src and convert to RGBA
                byte[] srcJPEG = FileUtils.readRaw(context, R.raw.test);
                final Bitmap srcBitmap = BitmapFactory.decodeByteArray(srcJPEG, 0, srcJPEG.length);
                int width = srcBitmap.getWidth();
                int height = srcBitmap.getHeight();
                byte[] src = new byte[width * height * 4];
                ByteBuffer srcBuffer = ByteBuffer.wrap(src);
                srcBitmap.copyPixelsToBuffer(srcBuffer);
                srcBuffer.rewind();

                Log.d(TAG, "add watermark start");
                WatermarkUtils.add(src, width, height, watermark, watermarkWidth, watermarkHeight, (width - watermarkWidth)/2, (height - watermarkHeight)/2);
                Log.d(TAG, "add watermark end");
                srcBitmap.copyPixelsFromBuffer(srcBuffer);

                watermarkBuffer.clear();
                watermarkBitmap.recycle();
                srcBuffer.clear();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mImageView.setImageBitmap(srcBitmap);
                        Toast.makeText(context, "convert finished!", Toast.LENGTH_LONG).show();
                    }
                });
                ImageUtils.RGBAToJPEG(context, src, width, height, "", "watermark_center");
            }
        });
    }

    private void resetOriginal() {
        TaskExecutor.run(new Runnable() {
            @Override
            public void run() {
                final Context context = MainActivity.this;
                byte[] srcJPEG = FileUtils.readRaw(context, R.raw.test);
                //BitmapFactory.Options options = new BitmapFactory.Options();
                //options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                //final Bitmap bitmap = BitmapFactory.decodeByteArray(srcJPEG, 0, srcJPEG.length, options);
                final Bitmap bitmap = BitmapFactory.decodeByteArray(srcJPEG, 0, srcJPEG.length);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mImageView.setImageBitmap(bitmap);
                        Toast.makeText(context, "load finished!", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}

package com.cv4j.app.utils;

import android.Manifest;
import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;

import com.safframework.permission.RxPermissions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by Tony Shen on 2017/6/25.
 */

public class FileUtils {

    /**
     * 保存图片信息，方便进行算法的bug。
     * @param activity
     * @param path
     * @param bitmap
     */
    public static void saveDebugImage(Activity activity, final String path,final Bitmap bitmap) {

        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        if (aBoolean) {

                            File filedir = new File(path);

                            if (!filedir.exists()) {
                                filedir.mkdir();
                            }
                            String name = String.valueOf(System.currentTimeMillis()) + "_ocr.png";
                            File tempFile = new File(filedir.getAbsoluteFile()+File.separator, name);
                            FileOutputStream output = null;
                            try {
                                output = new FileOutputStream(tempFile);
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
                            }catch (IOException ioe) {
                                Log.e("DEBUG-ERR", ioe.getMessage());
                            } finally {
                                if (output!=null) {
                                    try {
                                        output.flush();
                                        output.close();
                                    } catch (IOException e) {
                                        Log.i("DEBUG-INFO", e.getMessage());
                                    }
                                }
                            }
                        }
                    }
                });

    }
}

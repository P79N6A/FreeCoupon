package com.ks.freecoupon.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by Administrator on 2018/3/25.
 */

public class BitmapSaveLocal {
    private static final String SD_PATH = Environment.getExternalStorageDirectory() + "/Pictures/";
    private static final String IN_PATH = "/Pictures/";

    /**
     * 随机生产文件名
     *
     * @return
     */
    private static String generateFileName() {
        return UUID.randomUUID().toString();
    }
    /**
     * 保存bitmap到本地
     *
     * @param context
     * @param mBitmap
     * @return
     */
    public static String saveBitmap(Context context, Bitmap mBitmap) {
        String savePath;
        File filePic;
        String fileName;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            savePath = SD_PATH;
        } else {
            savePath = context.getApplicationContext().getFilesDir()
                    .getAbsolutePath()
                    + IN_PATH;
        }
        fileName = savePath + System.currentTimeMillis() + ".jpg";
        try {
            filePic = new File(fileName);
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            boolean compress = mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            if (compress) {
                fos.flush();
                fos.close();
                // 插入图库
                MediaStore.Images.Media.insertImage(context.getContentResolver(), filePic.getAbsolutePath(), ".jpg", null);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        // 发送广播，通知刷新图库的显示
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));
        return filePic.getAbsolutePath();
    }
}

package utilities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import models.usersinCircleModel;

/**
 * Created by mike on 11 Dec 2017.
 */

public class utils {



    public utils(Context con){
        mContext = con;
    }

    public static void DownloadImageFromPath(String urlToDownload, String pathtodownload) {
        try {
            URL url = new URL(urlToDownload);
            URLConnection connection = url.openConnection();
            connection.connect();
            // this will be useful so that you can show a typical 0-100% progress bar
            int fileLength = connection.getContentLength();

            // download the file
            InputStream input = new BufferedInputStream(connection.getInputStream());
            OutputStream output = new FileOutputStream(pathtodownload);

            byte data[] = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {

                // publishing the progress....


                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String createKey() {
        char[] chars1 = "ABCDEF012GHIJKL345MNOPQR678STUVWXY9378265".toCharArray();
        StringBuilder sb1 = new StringBuilder();
        Random random1 = new Random();
        for (int i = 0; i < 6; i++) {
            char c1 = chars1[random1.nextInt(chars1.length)];
            sb1.append(c1);
        }
        String random_string = sb1.toString();
        return random_string;
    }

    public static final String FILE_EXTENSION = ".bin";

    public static boolean savegroup(Context context, usersinCircleModel group) {
        String filename = String.valueOf(group.getUsername()) + FILE_EXTENSION;
        try {
            FileOutputStream fos = context.openFileOutput(filename, 0);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(group);
            oos.close();
            fos.close();
            Log.d("utils", filename);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ArrayList<usersinCircleModel> getallsavedgroups(Context context) {
        Exception e;
        ArrayList<usersinCircleModel> items = new ArrayList();
        File filesdir = context.getFilesDir();
        ArrayList<String> itemsfiles = new ArrayList();
        for (String file : filesdir.list()) {
            if (file.endsWith(FILE_EXTENSION)) {
                itemsfiles.add(file);
            }
        }
        int i = 0;
        while (i < itemsfiles.size()) {
            try {
                FileInputStream fis = context.openFileInput((String) itemsfiles.get(i));
                ObjectInputStream ois = new ObjectInputStream(fis);
                items.add((usersinCircleModel) ois.readObject());
                fis.close();
                ois.close();
                i++;
            } catch (IOException e2) {
                e = e2;
            } catch (ClassNotFoundException e3) {
                e = e3;
            }
        }
        return items;
        //e.printStackTrace();
        // return null;
    }

    public static boolean isRTL() {
        return isRTL(Locale.getDefault());
    }

    public static boolean isRTL(Locale locale) {
        final int directionality = Character.getDirectionality(locale.getDisplayName().charAt(0));
        return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
                directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC;
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
// RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
        // RECREATE THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);
        return resizedBitmap;
    }

     public static int getRandomColor(){
         Random rnd = new Random();
         return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
     }
    public static int getBatteryPercentage(Context context) {

        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, iFilter);

        int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

        float batteryPct = level / (float) scale;

        return (int) (batteryPct * 100);
    }
    private Context mContext = null;

    /**
     * Public constructor that takes mContext for later use
     */


    /**
     * Encode user email to use it as a Firebase key (Firebase does not allow "." in the key name)
     * Encoded email is also used as "userEmail", list and item "owner" value
     */
    public static String encodeEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    //This is a method to Check if the device internet connection is currently on
    public boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager

                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

}

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_SMS;
import static android.support.v4.app.ActivityCompat.shouldShowRequestPermissionRationale;


/**
 * Created by Ashwin Nirmale on 22,January,2019
 *
 *
 *  TODO : This class is common class developed by user to ask permission in any where in application.
 *
 *      {@link #checkSinglePermission(int)}
 *          param #id: is used to pass particular request's request code
 *          This class is used to check any single runtime permission is granted or not.
 *      }
 *
 *      {@link #checkMultiplePermission
 *          This class is used to check multiple runtime permissions is granted or not.
 *      }
 *
 *      {@link #onMultipleRequestCall
 *          Force user to ask multiple permission at single time
 *      }
 *
 *      {@link #onSingleRequestCall(int, int[])
 *      Ask user single permission at one time
 *      }
 *
 */

public class PermissionHelper {

    private Activity activity;
    private PermissionListener permissionListener;

    public PermissionHelper(Activity activity) {
        this.activity = activity;
    }

    public interface PermissionListener{
        void onPermissionCheck();
    }

    public void setPermissionListener(PermissionListener permissionListener){
        this.permissionListener = permissionListener;
    }

    public boolean checkSinglePermission(int id){

        switch (id) {
            case Common.CAMERA_PERMISSION_CODE :
                int cameraPermission = ContextCompat.checkSelfPermission(activity, CAMERA);
                if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{CAMERA}, Common.CAMERA_PERMISSION_CODE);
                    return false;
                }
                permissionListener.onPermissionCheck();
                break;

            case Common.MESSAGE_PERMISSION_CODE :
                int messagePermission = ContextCompat.checkSelfPermission(activity, READ_SMS);
                if (messagePermission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{READ_SMS}, Common.MESSAGE_PERMISSION_CODE);
                    return false;
                }
                permissionListener.onPermissionCheck();
                break;
        }
        return true;
    }


    public boolean checkMultiplePermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            int messagePermission = ContextCompat.checkSelfPermission(activity,READ_SMS);
            int locationPermission = ContextCompat.checkSelfPermission(activity,ACCESS_COARSE_LOCATION);
            int fineLocationPermission = ContextCompat.checkSelfPermission(activity,ACCESS_FINE_LOCATION);
            int cameraPermission = ContextCompat.checkSelfPermission(activity,CAMERA);

            List<String> permissionNeeded = new ArrayList<>();
            if (messagePermission != PackageManager.PERMISSION_GRANTED){
                permissionNeeded.add(READ_SMS);
            }
            if (locationPermission != PackageManager.PERMISSION_GRANTED){
                permissionNeeded.add(ACCESS_COARSE_LOCATION);
            }
            if (fineLocationPermission != PackageManager.PERMISSION_GRANTED){
                permissionNeeded.add(ACCESS_FINE_LOCATION);
            }
            if (cameraPermission != PackageManager.PERMISSION_GRANTED){
                permissionNeeded.add(CAMERA);
            }
            if (!permissionNeeded.isEmpty()){
                ActivityCompat.requestPermissions(activity,permissionNeeded.toArray(new String[permissionNeeded.size()]), Common.MULTIPLE_REQUEST_CODE);
                return false;
            }

            permissionListener.onPermissionCheck();
        }
        return true;
    }


    public void onMultipleRequestCall(int requestCode,String[] permissions,int[] grantResults){

        switch (requestCode){
            case Common.MULTIPLE_REQUEST_CODE:
                Map<String,Integer> mapValue = new HashMap<>();
                mapValue.put(READ_SMS,PackageManager.PERMISSION_GRANTED);
                mapValue.put(ACCESS_COARSE_LOCATION,PackageManager.PERMISSION_GRANTED);
                mapValue.put(ACCESS_FINE_LOCATION,PackageManager.PERMISSION_GRANTED);
                mapValue.put(CAMERA,PackageManager.PERMISSION_GRANTED);

                if (grantResults.length > 0){
                    for (int i = 0; i < permissions.length; i++)
                        mapValue.put(permissions[i],grantResults[i]);

                    if (mapValue.get(READ_SMS) == PackageManager.PERMISSION_GRANTED
                            && mapValue.get(ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && mapValue.get(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && mapValue.get(CAMERA) == PackageManager.PERMISSION_GRANTED){
                        checkMultiplePermission();

                    }else {
                        if (shouldShowRequestPermissionRationale(activity,READ_SMS)
                                || shouldShowRequestPermissionRationale(activity,ACCESS_COARSE_LOCATION)
                                || shouldShowRequestPermissionRationale(activity,ACCESS_FINE_LOCATION)
                                || shouldShowRequestPermissionRationale(activity,CAMERA)){
                            showDialog(activity.getResources().getString(R.string.permission_denied), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case DialogInterface.BUTTON_POSITIVE:
//                                            checkPermission();
                                            Intent settingIntent = new Intent();
                                            settingIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts("package",activity.getPackageName(),null);
                                            settingIntent.setData(uri);
                                            activity.startActivity(settingIntent);
                                            break;

                                            case DialogInterface.BUTTON_NEGATIVE:
                                                dialog.dismiss();
                                             break;
                                    }
                                }
                            });
                        }else {
                            Intent settingIntent = new Intent();
                            settingIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package",activity.getPackageName(),null);
                            settingIntent.setData(uri);
                            activity.startActivity(settingIntent);
                        }
                    }
                }

        }
    }

    public void onSingleRequestCall(int requestCode,int[] grantResults){
        switch (requestCode){
            case  Common.CAMERA_PERMISSION_CODE :

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSinglePermission(requestCode);
                }else {
                    if (shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
                        // show an explanation to the user
                        // Good practise: don't block thread after the user sees the explanation, try again to request the permission.
                        showDialog(activity.getResources().getString(R.string.permission_denied), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        Intent settingIntent = new Intent();
                                        settingIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                                        settingIntent.setData(uri);
                                        activity.startActivity(settingIntent);
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        dialog.dismiss();
                                        break;
                                }
                            }
                        });
                    } else {
                        Intent settingIntent = new Intent();
                        settingIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                        settingIntent.setData(uri);
                        activity.startActivity(settingIntent);
                    }

                }

                break;

            case Common.MESSAGE_PERMISSION_CODE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSinglePermission(requestCode);
                }else {
                    if (shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {

                        showDialog(activity.getResources().getString(R.string.permission_denied), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        Intent settingIntent = new Intent();
                                        settingIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                                        settingIntent.setData(uri);
                                        activity.startActivity(settingIntent);
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        dialog.dismiss();
                                        break;
                                }
                            }
                        });
                    } else {
                        Intent settingIntent = new Intent();
                        settingIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                        settingIntent.setData(uri);
                        activity.startActivity(settingIntent);
                    }

                }

                break;

        }
    }

    private void showDialog(String msg,DialogInterface.OnClickListener listener){
        new AlertDialog.Builder(activity).setMessage(msg).setPositiveButton("OK",listener).setNegativeButton("Cancel",listener).create().show();
    }
}

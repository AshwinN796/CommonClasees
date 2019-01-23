package com.extractsoftpvt.myfence.securityapp.helper;

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

import com.extractsoftpvt.myfence.securityapp.R;
import com.extractsoftpvt.myfence.securityapp.common.Common;

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
 *  TODO : There are two different methods to use this class depending upon your use-case ie.activity or fragments.
 *
 *  TODO: To use this class in Activity;
 *  Below methods are used in activity to depending on use-case to request single or multiple request at one time.
 *  {@link #checkSinglePermission(int)}
 *  {@link #checkSinglePermission(int)}
 *  Below methods are used inside Activity's overrride method {@link  android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback}
 *  {@link #onMultipleRequestCall(int, String[], int[])}
 *  {@link #onSingleRequestCall(int, int[])}
 *
 *
 *  TODO: To use this class inside fragment
 *  Below method is used in fragment to ask single permission to user
 *  {@link #checkSinglePermissionOnFragment(int, Activity)}
 *  Below method is used inside activity's override method{@link android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback}
 *  TODO: user must use instaceOf fragment to seperate method for each fragment
 *  {@link #askSingleRequestOnFragment(int, int[], Activity)}
 *
 *  TODO : {@link #setPermissionListener(PermissionListener)}
 *  TODO : In case of Fragment ,we need to use empty method inside fragment to work properly and our logic need to be wriiten inside activity
 *
 *  TODO : {@link #setPermissionListener(PermissionListener)} In case of Activity, Use it as it as
 *
 *      {@link #checkSinglePermission(int)}
 *          param #id: is used to pass particular request's request code
 *          This class is used to check any single runtime permission is granted or not inside activity.
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
 *      {@link #onSingleRequestCall(int, int[])}
 *      Ask user single permission at one time
 *      }
 *
 *      {@link #askSingleRequestOnFragment(int, int[], Activity)
 *      Ask user single permission at one time inside fragment
 *      }
 *
 *      {@link #showDialogwithFragment(String, DialogInterface.OnClickListener, Activity)
 *      Show Dialog to user inside fragment after denying permission
 *      }
 *
 *      {@link #checkSinglePermissionOnFragment(int, Activity)
 *      Check or validate permission inside fragment}
 *
 *      {@link #showDialogwithFragment(String, DialogInterface.OnClickListener, Activity)}
 *      To show dialog box in case of fragment
 *
 */

public class PermissionHelper {

    private Activity activity;
	private Context context;
    private Fragment fragment;
    private PermissionListener permissionListener;

    public PermissionHelper(Activity activity) {
        this.activity = activity;
    }
	
	public PermissionHelper(Fragment fragment) {
        this.fragment = fragment;
    }

    public PermissionHelper(Context context) {
        this.context = context;
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
                int cameraPermission = ContextCompat.checkSelfPermission(context, CAMERA);
                if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{CAMERA}, Common.CAMERA_PERMISSION_CODE);
                    return false;
                }
                permissionListener.onPermissionCheck();
                break;

            case Common.MESSAGE_PERMISSION_CODE :
                int messagePermission = ContextCompat.checkSelfPermission(context, READ_SMS);
                if (messagePermission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{READ_SMS}, Common.MESSAGE_PERMISSION_CODE);
                    return false;
                }
                permissionListener.onPermissionCheck();
                break;

            case Common.LOCATION_PERMISSION_CODE :
                int locationPermission = ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION);
                if (locationPermission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{ACCESS_FINE_LOCATION}, Common.LOCATION_PERMISSION_CODE);
                    return false;
                }
                permissionListener.onPermissionCheck();
                break;
        }
        return true;
    }


    public boolean checkMultiplePermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            int messagePermission = ContextCompat.checkSelfPermission(context,READ_SMS);
            int locationPermission = ContextCompat.checkSelfPermission(context,ACCESS_COARSE_LOCATION);
            int fineLocationPermission = ContextCompat.checkSelfPermission(context,ACCESS_FINE_LOCATION);
            int cameraPermission = ContextCompat.checkSelfPermission(context,CAMERA);

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
                ActivityCompat.requestPermissions((Activity) context,permissionNeeded.toArray(new String[permissionNeeded.size()]), Common.MULTIPLE_REQUEST_CODE);
                return false;
            }

            permissionListener.onPermissionCheck();
        }
        return true;
    }

    public boolean checkSinglePermissionOnFragment(int id, Activity activity){
        switch (id) {
            case Common.CAMERA_PERMISSION_CODE :
                int cameraPermission = ContextCompat.checkSelfPermission(activity, CAMERA);
                if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        activity.requestPermissions( new String[]{CAMERA}, Common.CAMERA_PERMISSION_CODE);
                    }
                    return false;
                }
                permissionListener.onPermissionCheck();
                break;

            case Common.MESSAGE_PERMISSION_CODE :
                int messagePermission = ContextCompat.checkSelfPermission(activity,READ_SMS);
                if (messagePermission != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        activity.requestPermissions( new String[]{READ_SMS}, Common.MESSAGE_PERMISSION_CODE);
                    }
                    return false;
                }
                permissionListener.onPermissionCheck();
                break;

            case Common.LOCATION_PERMISSION_CODE :
                int locationPermission = ContextCompat.checkSelfPermission(activity, ACCESS_FINE_LOCATION);
                if (locationPermission != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        activity.requestPermissions (new String[]{ACCESS_FINE_LOCATION}, Common.LOCATION_PERMISSION_CODE);
                    }
                    return false;
                }
                permissionListener.onPermissionCheck();
                break;
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
                        if (shouldShowRequestPermissionRationale((Activity) context,READ_SMS)
                                || shouldShowRequestPermissionRationale((Activity) context,ACCESS_COARSE_LOCATION)
                                || shouldShowRequestPermissionRationale((Activity) context,ACCESS_FINE_LOCATION)
                                || shouldShowRequestPermissionRationale((Activity) context,CAMERA)){
                            showDialog(context.getResources().getString(R.string.permission_denied), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case DialogInterface.BUTTON_POSITIVE:
//                                            checkPermission();
                                            Intent settingIntent = new Intent();
                                            settingIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts("package",context.getPackageName(),null);
                                            settingIntent.setData(uri);
                                            context.startActivity(settingIntent);
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
                            Uri uri = Uri.fromParts("package",context.getPackageName(),null);
                            settingIntent.setData(uri);
                            context.startActivity(settingIntent);
                        }
                    }
                }

        }
    }

    public void onSingleRequestCall(int requestCode, int[] grantResults){
        switch (requestCode){
            case  Common.CAMERA_PERMISSION_CODE :

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSinglePermission(requestCode);
                }else {
                    if (shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.CAMERA)) {
                        // show an explanation to the user
                        // Good practise: don't block thread after the user sees the explanation, try again to request the permission.
                        showDialog(context.getResources().getString(R.string.permission_denied), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        Intent settingIntent = new Intent();
                                        settingIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                                        settingIntent.setData(uri);
                                        context.startActivity(settingIntent);
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
                        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                        settingIntent.setData(uri);
                        context.startActivity(settingIntent);
                    }

                }

                break;

            case Common.MESSAGE_PERMISSION_CODE:

                if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSinglePermission(requestCode);
                    } else {

                    if (shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_SMS)) {
                        // show an explanation to the user
                        // Good practise: don't block thread after the user sees the explanation, try again to request the permission.
                        showDialog(context.getResources().getString(R.string.permission_denied), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        Intent settingIntent = new Intent();
                                        settingIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                                        settingIntent.setData(uri);
                                        context.startActivity(settingIntent);
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
                        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                        settingIntent.setData(uri);
                        context.startActivity(settingIntent);
                    }
                }


                break;


            case Common.LOCATION_PERMISSION_CODE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSinglePermission(requestCode);
                }else {
                    if (shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.CAMERA)) {
                        // show an explanation to the user
                        // Good practise: don't block thread after the user sees the explanation, try again to request the permission.
                        showDialog(context.getResources().getString(R.string.permission_denied), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        Intent settingIntent = new Intent();
                                        settingIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                                        settingIntent.setData(uri);
                                        context.startActivity(settingIntent);
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
                        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                        settingIntent.setData(uri);
                        context.startActivity(settingIntent);
                    }
                }

                break;

        }
    }

    public void askSingleRequestOnFragment(int requestCode, int[] grantResults, final Activity activity){

        switch (requestCode){
            case  Common.CAMERA_PERMISSION_CODE :

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSinglePermissionOnFragment(requestCode,activity);
                }else {
                    if (shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
                        showDialogwithFragment(activity.getResources().getString(R.string.permission_denied), new DialogInterface.OnClickListener() {
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
                        },activity);
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

                if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSinglePermissionOnFragment(requestCode,activity);
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_SMS)) {
                        Toast.makeText(activity,"Request Denied",Toast.LENGTH_SHORT).show();

                        showDialogwithFragment(activity.getResources().getString(R.string.permission_denied), new DialogInterface.OnClickListener() {
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
                        },activity);
                    }
                    else {
                        Intent settingIntent = new Intent();
                        settingIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                        settingIntent.setData(uri);
                        activity.startActivity(settingIntent);
                    }

                }

                break;


            case Common.LOCATION_PERMISSION_CODE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkSinglePermissionOnFragment(requestCode,activity);
                }else {
                    if (shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {

                        showDialogwithFragment(activity.getResources().getString(R.string.permission_denied), new DialogInterface.OnClickListener() {
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
                        },activity);

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
        new AlertDialog.Builder(context).setMessage(msg).setPositiveButton("OK",listener).setNegativeButton("Cancel",listener).create().show();
    }

    private void showDialogwithFragment(String msg,DialogInterface.OnClickListener listener,Activity activity){
        new AlertDialog.Builder(activity).setMessage(msg).setPositiveButton("OK",listener).setNegativeButton("Cancel",listener).create().show();
    }
}

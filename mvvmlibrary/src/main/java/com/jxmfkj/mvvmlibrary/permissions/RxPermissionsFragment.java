package com.jxmfkj.mvvmlibrary.permissions;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.blankj.utilcode.util.ArrayUtils;
import com.jxmfkj.mvvmlibrary.Config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import io.reactivex.subjects.PublishSubject;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class RxPermissionsFragment extends Fragment implements EasyPermissions.PermissionCallbacks {

    private static final int PERMISSIONS_REQUEST_CODE = 42;

    // Contains all the current permission requests.
    // Once granted or denied, they are removed from it.
    private Map<String, PublishSubject<Permission>> mSubjects = new HashMap<>();

    private List<String> mPermissions;

    public RxPermissionsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @TargetApi(Build.VERSION_CODES.M)
    void requestPermissions(@NonNull String message, @NonNull String[] permissions) {
        mPermissions = Arrays.asList(ArrayUtils.copy(permissions));
        EasyPermissions.requestPermissions(
                this,
                message,
                PERMISSIONS_REQUEST_CODE,
                permissions);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    void onRequestPermissionsResult(List<String> permissions, boolean[] shouldShowRequestPermissionRationale) {
        for (int i = 0, size = permissions.size(); i < size; i++) {
            log("onRequestPermissionsResult  " + permissions.get(i));
            // Find the corresponding subject
            PublishSubject<Permission> subject = mSubjects.get(permissions.get(i));
            if (subject == null) {
                // No subject found
                Log.e(RxPermissions.TAG, "RxPermissions.onRequestPermissionsResult invoked but didn't find the corresponding permission request.");
                return;
            }
            mSubjects.remove(permissions.get(i));
            boolean granted = EasyPermissions.hasPermissions(getContext(), permissions.get(i));
            subject.onNext(new Permission(permissions.get(i), granted, shouldShowRequestPermissionRationale[i]));
            subject.onComplete();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    boolean isGranted(String permission) {
        final FragmentActivity fragmentActivity = getActivity();
        if (fragmentActivity == null) {
            throw new IllegalStateException("This fragment must be attached to an activity.");
        }
        return fragmentActivity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(Build.VERSION_CODES.M)
    boolean isRevoked(String permission) {
        final FragmentActivity fragmentActivity = getActivity();
        if (fragmentActivity == null) {
            throw new IllegalStateException("This fragment must be attached to an activity.");
        }
        return fragmentActivity.getPackageManager().isPermissionRevokedByPolicy(permission, getActivity().getPackageName());
    }


    public PublishSubject<Permission> getSubjectByPermission(@NonNull String permission) {
        return mSubjects.get(permission);
    }

    public boolean containsByPermission(@NonNull String permission) {
        return mSubjects.containsKey(permission);
    }

    public void setSubjectForPermission(@NonNull String permission, @NonNull PublishSubject<Permission> subject) {
        mSubjects.put(permission, subject);
    }

    void log(String message) {
        if (Config.isDebug()) {
            Log.d(RxPermissions.TAG, message);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

        if (requestCode != PERMISSIONS_REQUEST_CODE) return;

        boolean[] shouldShowRequestPermissionRationale = new boolean[perms.size()];

        for (int i = 0; i < perms.size(); i++) {
            shouldShowRequestPermissionRationale[i] = shouldShowRequestPermissionRationale(perms.get(i));
        }

        onRequestPermissionsResult(perms, shouldShowRequestPermissionRationale);
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (requestCode != PERMISSIONS_REQUEST_CODE) return;

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        } else {
            boolean[] shouldShowRequestPermissionRationale = new boolean[perms.size()];
            for (int i = 0; i < perms.size(); i++) {
                shouldShowRequestPermissionRationale[i] = shouldShowRequestPermissionRationale(perms.get(i));
            }

            onRequestPermissionsResult(perms, shouldShowRequestPermissionRationale);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            if (mPermissions != null) {
                boolean[] shouldShowRequestPermissionRationale = new boolean[mPermissions.size()];
                for (int i = 0; i < mPermissions.size(); i++) {
                    shouldShowRequestPermissionRationale[i] = shouldShowRequestPermissionRationale(mPermissions.get(i));
                }
                onRequestPermissionsResult(mPermissions, shouldShowRequestPermissionRationale);
            }

        }
    }
}

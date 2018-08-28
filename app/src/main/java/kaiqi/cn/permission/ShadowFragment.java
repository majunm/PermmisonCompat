package kaiqi.cn.permission;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

/**
 * FrameLayout contentView =  (FrameLayout)activity.findViewById(android.R.id.content);
 */
public class ShadowFragment extends Fragment {
    public PermissionCompat.PermissionCompatCallback mPermissionCompatCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 禁止横竖屏切换时的Fragment的重建
        setRetainInstance(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        System.out.println("###########权限回调执行啦#############");
        System.out.println("###########权限回调执行啦###########################");
        System.out.println("###########权限回调执行啦#############");
        if (mPermissionCompatCallback != null) {
            if (PermissionCompat.hasAllPermissionsGranted(grantResults)) {
                mPermissionCompatCallback.ok(PermissionCompat.PASS);
            } else {
                mPermissionCompatCallback.refuse(PermissionCompat.DEATH_REFUSAL);
            }
        }
    }
}

[简书地址](https://www.jianshu.com/p/7bdbba785a2a)

> 查看rxpermission源码可知,核心代码是创建一个无ui的fragment,fragment是可感知activity生命周期的,这个时候,解决方案可不是跃然纸上了吗,不熟rxjava怎么办,在下封装了个简易代码,仅供参考~

### activity中
```java
  PermissionCompat.tryReqs(this, new PermissionCompat.PerCompatCallbackAdpt() {
            @Override
            public void ok(int cmds) {
                System.out.println("==============权限通过了==============");
            }
            @Override
            public void refuse(int cmds) {
                System.out.println("==============权限被拒绝了==============");
            }

            // 跳转settings吧
            @Override
            public void goSettings(int cmds) {
                System.out.println("==============请去setting界面==============");
                PermissionSettingPage.start(MyApplication.CONTEXT, false);
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE);
```

### fragment中
```java
PermissionCompat.tryReqs(getActivity(), new PermissionCompat.PerCompatCallbackAdpt() {
            @Override
            public void ok(int cmds) {
                System.out.println("==============权限通过了==============");
            }
            @Override
            public void refuse(int cmds) {
                System.out.println("==============权限被拒绝了==============");
            }

            // 跳转settings吧
            @Override
            public void goSettings(int cmds) {
                System.out.println("==============请去setting界面==============");
                PermissionSettingPage.start(MyApplication.CONTEXT, false);
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE);
```



### 核心代码

```java
package kaiqi.cn.permission;

import android.content.pm.PackageManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
 * super.onRequestPermissionsResult(requestCode, permissions, grantResults);
 * }
 */
public class PermissionCompat {
    public static final int CODE = 200;
    /**
     * 拒绝,不在
     */
    public static final int DEATH_REFUSAL = 250;
    /**
     * 拒绝
     */
    public static final int NORMAL_REFUSAL = 251;
    /**
     * 通过
     */
    public static final int PASS = 201;

    public interface PermissionCompatCallback {
        /**
         * @param cmds PASS 通过|NORMAL_REFUSAL 拒绝 |DEATH_REFUSAL 拒绝(
         */
        void ok(int cmds);

        void refuse(int cmds);

        void goSettings(int cmds);
    }

    public static abstract class PerCompatCallbackAdpt implements PermissionCompatCallback {

        @Override
        public abstract void ok(int cmds);

        @Override
        public void refuse(int cmds) {

        }

        @Override
        public void goSettings(int cmds) {

        }
    }

    public static final String TAG = "fragment";

    /**
     */
    public static void tryReqs(FragmentActivity act, PerCompatCallbackAdpt mPermissionCompatCallback, String... cmds) {

        if (cmds != null && cmds.length > 0) {
            List<String> mCmdLists = new ArrayList<>();
            for (String cmdStr : cmds) {
                mCmdLists.add(cmdStr);
            }
            for (String cmd : mCmdLists) {
                if (ContextCompat.checkSelfPermission(act, cmd) != PackageManager.PERMISSION_GRANTED) {
                    String[] mPermissions = mCmdLists.toArray(new String[mCmdLists.size()]);
                    FragmentManager fm = act.getSupportFragmentManager();
                    ShadowFragment mShadowFragment = (ShadowFragment) fm.findFragmentByTag(TAG);
                    if (mShadowFragment == null) {
                        mShadowFragment = new ShadowFragment();
                        fm.beginTransaction().add(mShadowFragment, TAG).commitAllowingStateLoss();
                    }
                    fm.executePendingTransactions();
                    if (mShadowFragment != null) {
                        mShadowFragment.mPermissionCompatCallback = mPermissionCompatCallback;
                        mShadowFragment.requestPermissions(mPermissions, CODE);
                    }
                    return;
                }
            }
            if (mPermissionCompatCallback != null) {
                mPermissionCompatCallback.ok(PASS);
            }
        } else {
            if (mPermissionCompatCallback != null) {
                mPermissionCompatCallback.ok(PASS);
            }
        }
    }

    public static boolean hasAllPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }
}
```


```java
package kaiqi.cn.permission;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
                for (String permission : permissions) { //拒绝,并且不在询问
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
                        mPermissionCompatCallback.goSettings(PermissionCompat.DEATH_REFUSAL);
                        return;
                    }
                }
                mPermissionCompatCallback.refuse(PermissionCompat.NORMAL_REFUSAL);
            }
        }
    }
}


```

权限组
```
android.permission.READ_CALENDAR
android.permission.WRITE_CALENDAR

android.permission.CAMERA

android.permission.READ_CONTACTS
android.permission.WRITE_CONTACTS
android.permission.GET_ACCOUNTS

android.permission.ACCESS_FINE_LOCATION
android.permission.ACCESS_COARSE_LOCATION

android.permission.RECORD_AUDIO

android.permission.READ_PHONE_STATE
android.permission.CALL_PHONE
android.permission.READ_CALL_LOG
android.permission.WRITE_CALL_LOG
com.android.voicemail.permission.ADD_VOICEMAIL
android.permission.USE_SIP
android.permission.PROCESS_OUTGOING_CALLS

android.permission.BODY_SENSORS

android.permission.READ_EXTERNAL_STORAGE
android.permission.WRITE_EXTERNAL_STORAGE

android.permission.SEND_SMS
android.permission.RECEIVE_SMS
android.permission.READ_SMS
android.permission.RECEIVE_WAP_PUSH
android.permission.RECEIVE_MMS
android.permission.READ_CELL_BROADCASTS
```

> 这个跳转settings来至 https://github.com/getActivity/XXPermissions

```

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
//PermissionSettingPage.start(MyApplication.CONTEXT, false);
//PermissionSettingPage.start(MyApplication.CONTEXT, true);
public class PermissionSettingPage {
    private static final String MARK = Build.MANUFACTURER.toLowerCase();

    /**
     * 跳转到应用权限设置页面
     *
     * @param context 上下文对象
     * @param newTask 是否使用新的任务栈启动
     */
    static void start(Context context, boolean newTask) {
        if (context == null) {
            return;
        }
        Intent intent = null;
        if (MARK.contains("huawei")) {
            intent = huawei(context);
        } else if (MARK.contains("xiaomi")) {
            intent = xiaomi(context);
        } else if (MARK.contains("oppo")) {
            intent = oppo(context);
        } else if (MARK.contains("vivo")) {
            intent = vivo(context);
        } else if (MARK.contains("meizu")) {
            intent = meizu(context);
        }

        if (intent == null || !hasIntent(context, intent)) {
            intent = google(context);
        }

        if (newTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        try {
            context.startActivity(intent);
        } catch (Exception ignored) {
            intent = google(context);
            context.startActivity(intent);
        }
    }

    private static Intent google(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        return intent;
    }

    private static Intent huawei(Context context) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity"));
        if (hasIntent(context, intent)) return intent;
        intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity"));
        if (hasIntent(context, intent)) return intent;
        intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.notificationmanager.ui.NotificationManagmentActivity"));
        return intent;
    }

    private static Intent xiaomi(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.putExtra("extra_pkgname", context.getPackageName());
        if (hasIntent(context, intent)) return intent;

        intent.setPackage("com.miui.securitycenter");
        if (hasIntent(context, intent)) return intent;

        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        if (hasIntent(context, intent)) return intent;

        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
        return intent;
    }

    private static Intent oppo(Context context) {
        Intent intent = new Intent();
        intent.putExtra("packageName", context.getPackageName());
        intent.setClassName("com.color.safecenter", "com.color.safecenter.permission.floatwindow.FloatWindowListActivity");
        if (hasIntent(context, intent)) return intent;

        intent.setClassName("com.coloros.safecenter", "com.coloros.safecenter.sysfloatwindow.FloatWindowListActivity");
        if (hasIntent(context, intent)) return intent;

        intent.setClassName("com.oppo.safe", "com.oppo.safe.permission.PermissionAppListActivity");
        return intent;
    }

    private static Intent vivo(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.FloatWindowManager");
        intent.putExtra("packagename", context.getPackageName());
        if (hasIntent(context, intent)) return intent;

        intent.setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.safeguard.SoftPermissionDetailActivity"));
        return intent;
    }

    private static Intent meizu(Context context) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.putExtra("packageName", context.getPackageName());
        intent.setComponent(new ComponentName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity"));
        return intent;
    }

    private static boolean hasIntent(Context context, Intent intent) {
        return context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }
}

```

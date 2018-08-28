[简书地址](https://www.jianshu.com/p/7bdbba785a2a)

activity中
```
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
            }
        });
```

fragment中
```
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
            }
        });
```



### 核心代码

```
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

        void other(int cmds);
    }

    public static abstract class PerCompatCallbackAdpt implements PermissionCompatCallback {

        @Override
        public abstract void ok(int cmds);

        @Override
        public void refuse(int cmds) {

        }

        @Override
        public void other(int cmds) {

        }
    }

    public static final String TAG = "fragment";

    /**
     */
    public static void tryReqs(AppCompatActivity act, PerCompatCallbackAdpt mPermissionCompatCallback, String... cmds) {

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


```
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

```
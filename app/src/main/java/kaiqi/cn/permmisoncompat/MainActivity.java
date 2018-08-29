package kaiqi.cn.permmisoncompat;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import kaiqi.cn.httpx.R;
import kaiqi.cn.permission.PermissionCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PermissionCompat.tryReqs(this, new PermissionCompat.PerCompatCallbackAdpt() {
            @Override
            public void ok(int cmds) {
                System.out.println("==============权限通过了==============");
            }
            @Override
            public void refuse(int cmds) {
                System.out.println("==============权限被拒绝了==============");
            }

            // 还未添加功能
            @Override
            public void goSettings(int cmds) {
                System.out.println("==============请去setting界面==============");
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE,Manifest.permission.CAMERA);
    }
}

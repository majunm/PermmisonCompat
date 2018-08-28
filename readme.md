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

            // 还未添加功能
            @Override
            public void other(int cmds) {
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

            // 还未添加功能
            @Override
            public void other(int cmds) {
                System.out.println("==============请去setting界面==============");
            }
        });
```
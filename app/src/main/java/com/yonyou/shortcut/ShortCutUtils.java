package com.yonyou.shortcut;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Chen on 2017/1/6.
 */

public class ShortCutUtils {
    private static final String INSTALL_SHORECUT = "com.android.launcher.action.INSTALL_SHORTCUT";

    private static final String UNINSTALL_SHORECUT =
            "com.android.launcher.action.UNINSTALL_SHORTCUT";

    public static ArrayList<Action> getAllAction() {
        ArrayList<Action> actions = new ArrayList<Action>();
        Action callAction = new Action();
        callAction.setProtocol("tel");
        callAction.setAction(Intent.ACTION_CALL);
        callAction.setName("电话");
        actions.add(callAction);

        Action smsAction = new Action();
        smsAction.setProtocol("smsto");
        smsAction.setAction(Intent.ACTION_SENDTO);
        smsAction.setName("短信");
        actions.add(smsAction);

        return actions;

    }

    public static void createShortCut(Context context, Action action, Bitmap icon) {
        if (action == null) {
            return;
        }
        Intent intent = new Intent();

        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, getIntent(action));
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, action.getName());
        if (icon == null) {
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                    Intent.ShortcutIconResource.fromContext(context,
                            R.mipmap.ic_launcher));
        } else {
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);
        }

        intent.setAction(INSTALL_SHORECUT);
        context.sendBroadcast(intent);
    }

    private static Intent getIntent(Action action) {
        Intent launchIntent = new Intent();
        String uriStr = action.getProtocol() + ":" + action.getValue();
        launchIntent.setData(Uri.parse(uriStr));
        launchIntent.setAction(action.getAction());
        return launchIntent;
    }

    /**
     * 删除当前应用的桌面快捷方式
     *
     * @param cx
     */
    public static void delShortcut(Context cx, Action action) {
        Intent shortcut = new Intent();
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, action.getName());
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, getIntent(action));
        shortcut.setAction(UNINSTALL_SHORECUT);
        cx.sendBroadcast(shortcut);
    }

    /**
     * 检查快捷方式是否存在 <br/>
     * <font color=red>注意：</font> 有些手机无法判断是否已经创建过快捷方式<br/>
     * 因此，在创建快捷方式时，请添加<br/>
     * shortcutIntent.putExtra("duplicate", false);// 不允许重复创建<br/>
     * 最好使用{@link #isShortCutExist(Context, String, Intent)}
     * 进行判断，因为可能有些应用生成的快捷方式名称是一样的的<br/>
     */
    public static boolean isShortCutExist(Context context, String title) {
        boolean result = false;
        try {
            ContentResolver cr = context.getContentResolver();
            Uri uri = getUriFromLauncher(context);
            Cursor c = cr.query(uri, new String[]{"title"}, "title=? ", new String[]{title}, null);
            if (c != null && c.getCount() > 0) {
                result = true;
            }
            if (c != null && !c.isClosed()) {
                c.close();
            }
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 不一定所有的手机都有效，因为国内大部分手机的桌面不是系统原生的<br/>
     * 更多请参考{@link #isShortCutExist(Context, String)}<br/>
     * 桌面有两种，系统桌面(ROM自带)与第三方桌面，一般只考虑系统自带<br/>
     * 第三方桌面如果没有实现系统响应的方法是无法判断的，比如GO桌面<br/>
     */
    public static boolean isShortCutExist(Context context, String title, Intent intent) {
        boolean result = false;
        try {
            ContentResolver cr = context.getContentResolver();
            Uri uri = getUriFromLauncher(context);
            Cursor c = cr.query(uri, new String[]{"title", "intent"}, "title=?  and intent=?",
                    new String[]{title, intent.toUri(0)}, null);
            if (c != null && c.getCount() > 0) {
                result = true;
            }
            if (c != null && !c.isClosed()) {
                c.close();
            }
        } catch (Exception ex) {
            result = false;
            ex.printStackTrace();
        }
        return result;
    }

    private static Uri getUriFromLauncher(Context context) {
        StringBuilder uriStr = new StringBuilder();
        String authority = LauncherUtil.getAuthorityFromPermissionDefault(context);
        if (authority == null || authority.trim().equals("")) {
            authority = LauncherUtil.getAuthorityFromPermission(context, LauncherUtil.getCurrentLauncherPackageName(context) + ".permission.READ_SETTINGS");
        }
        uriStr.append("content://");
        if (TextUtils.isEmpty(authority)) {
            int sdkInt = android.os.Build.VERSION.SDK_INT;
            if (sdkInt < 8) { // Android 2.1.x(API 7)以及以下的
                uriStr.append("com.android.launcher.settings");
            } else if (sdkInt < 19) {// Android 4.4以下
                uriStr.append("com.android.launcher2.settings");
            } else {// 4.4以及以上
                uriStr.append("com.android.launcher3.settings");
            }
        } else {
            uriStr.append(authority);
        }
        uriStr.append("/favorites?notify=true");
        return Uri.parse(uriStr.toString());
    }

}

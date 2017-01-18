package com.yonyou.shortcut;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

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

    public static void createShortCut(Context context, Action action) {
        if (action == null) {
            return;
        }
        int iconResId = action.getIconResId() == 0 ? R.mipmap.ic_launcher : action.getIconResId();
//        Intent.ShortcutIconResource shortcutIconResource = Intent.ShortcutIconResource.fromContext(context, iconResId);
        Intent intent = new Intent();

        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, getIntent(action));
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, action.getName());
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

}

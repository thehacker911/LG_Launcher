package com.android.settings;

import android.app.LauncherActivity;
import android.app.LauncherActivity.ListItem;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.view.View;
import android.widget.ListView;
import java.util.List;

public class CreateShortcut extends LauncherActivity
{
  protected Intent getTargetIntent()
  {
    Intent localIntent = new Intent("android.intent.action.MAIN", null);
    localIntent.addCategory("com.android.settings.SHORTCUT");
    localIntent.addFlags(268435456);
    return localIntent;
  }

  protected boolean onEvaluateShowIcons()
  {
    return false;
  }

  protected void onListItemClick(ListView paramListView, View paramView, int paramInt, long paramLong)
  {
    Intent localIntent1 = intentForPosition(paramInt);
    localIntent1.setFlags(2097152);
    Intent localIntent2 = new Intent();
    localIntent2.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", Intent.ShortcutIconResource.fromContext(this, 2130903040));
    localIntent2.putExtra("android.intent.extra.shortcut.INTENT", localIntent1);
    localIntent2.putExtra("android.intent.extra.shortcut.NAME", itemForPosition(paramInt).label);
    setResult(-1, localIntent2);
    finish();
  }

  protected List<ResolveInfo> onQueryPackageManager(Intent paramIntent)
  {
    List localList = super.onQueryPackageManager(paramIntent);
    if (localList == null)
      return null;
    for (int i = -1 + localList.size(); i >= 0; i--)
      if ((((ResolveInfo)localList.get(i)).activityInfo.name.endsWith(Settings.TetherSettingsActivity.class.getSimpleName())) && (!TetherSettings.showInShortcuts(this)))
        localList.remove(i);
    return localList;
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.CreateShortcut
 * JD-Core Version:    0.6.2
 */
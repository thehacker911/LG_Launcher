package com.android.settings.deviceinfo;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.storage.StorageVolume;
import android.text.format.Formatter;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MiscFilesHandler extends ListActivity
{
  private MemoryMearurementAdapter mAdapter;
  private LayoutInflater mInflater;
  private String mNumBytesSelectedFormat;
  private String mNumSelectedFormat;

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    setFinishOnTouchOutside(true);
    setTitle(2131429015);
    this.mNumSelectedFormat = getString(2131429016);
    this.mNumBytesSelectedFormat = getString(2131429017);
    this.mAdapter = new MemoryMearurementAdapter(this);
    this.mInflater = ((LayoutInflater)getSystemService("layout_inflater"));
    setContentView(2130968708);
    ListView localListView = getListView();
    localListView.setItemsCanFocus(true);
    localListView.setChoiceMode(3);
    localListView.setMultiChoiceModeListener(new ModeCallback(this));
    setListAdapter(this.mAdapter);
  }

  class MemoryMearurementAdapter extends BaseAdapter
  {
    private Context mContext;
    private ArrayList<StorageMeasurement.FileInfo> mData = null;
    private long mDataSize = 0L;

    public MemoryMearurementAdapter(Activity arg2)
    {
      Context localContext;
      this.mContext = localContext;
      StorageMeasurement localStorageMeasurement = StorageMeasurement.getInstance(localContext, (StorageVolume)localContext.getIntent().getParcelableExtra("storage_volume"));
      if (localStorageMeasurement == null);
      while (true)
      {
        return;
        this.mData = ((ArrayList)localStorageMeasurement.mFileInfoForMisc);
        if (this.mData != null)
        {
          Iterator localIterator = this.mData.iterator();
          while (localIterator.hasNext())
          {
            StorageMeasurement.FileInfo localFileInfo = (StorageMeasurement.FileInfo)localIterator.next();
            this.mDataSize += localFileInfo.mSize;
          }
        }
      }
    }

    public int getCount()
    {
      if (this.mData == null)
        return 0;
      return this.mData.size();
    }

    public long getDataSize()
    {
      return this.mDataSize;
    }

    public StorageMeasurement.FileInfo getItem(int paramInt)
    {
      if ((this.mData == null) || (this.mData.size() <= paramInt))
        return null;
      return (StorageMeasurement.FileInfo)this.mData.get(paramInt);
    }

    public long getItemId(int paramInt)
    {
      if ((this.mData == null) || (this.mData.size() <= paramInt))
        return 0L;
      return ((StorageMeasurement.FileInfo)this.mData.get(paramInt)).mId;
    }

    public View getView(final int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      if (paramView == null);
      for (final FileItemInfoLayout localFileItemInfoLayout = (FileItemInfoLayout)MiscFilesHandler.this.mInflater.inflate(2130968707, paramViewGroup, false); ; localFileItemInfoLayout = (FileItemInfoLayout)paramView)
      {
        StorageMeasurement.FileInfo localFileInfo = getItem(paramInt);
        localFileItemInfoLayout.setFileName(localFileInfo.mFileName);
        localFileItemInfoLayout.setFileSize(Formatter.formatFileSize(this.mContext, localFileInfo.mSize));
        final ListView localListView = (ListView)paramViewGroup;
        localFileItemInfoLayout.getCheckBox().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
          public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean)
          {
            localListView.setItemChecked(paramInt, paramAnonymousBoolean);
          }
        });
        localFileItemInfoLayout.setOnLongClickListener(new View.OnLongClickListener()
        {
          public boolean onLongClick(View paramAnonymousView)
          {
            if (localListView.getCheckedItemCount() > 0)
              return false;
            ListView localListView = localListView;
            int i = paramInt;
            boolean bool1 = localFileItemInfoLayout.isChecked();
            boolean bool2 = false;
            if (!bool1)
              bool2 = true;
            localListView.setItemChecked(i, bool2);
            return true;
          }
        });
        localFileItemInfoLayout.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            ListView localListView;
            int i;
            if (localListView.getCheckedItemCount() > 0)
            {
              localListView = localListView;
              i = paramInt;
              if (localFileItemInfoLayout.isChecked())
                break label41;
            }
            label41: for (boolean bool = true; ; bool = false)
            {
              localListView.setItemChecked(i, bool);
              return;
            }
          }
        });
        return localFileItemInfoLayout;
      }
    }

    public void notifyDataSetChanged()
    {
      super.notifyDataSetChanged();
    }

    public void removeAll(List<Object> paramList)
    {
      if (this.mData == null);
      while (true)
      {
        return;
        Iterator localIterator = paramList.iterator();
        while (localIterator.hasNext())
        {
          Object localObject = localIterator.next();
          this.mData.remove(localObject);
          this.mDataSize -= ((StorageMeasurement.FileInfo)localObject).mSize;
        }
      }
    }
  }

  private class ModeCallback
    implements AbsListView.MultiChoiceModeListener
  {
    private final Context mContext;
    private int mDataCount;

    public ModeCallback(Context arg2)
    {
      Object localObject;
      this.mContext = localObject;
      this.mDataCount = MiscFilesHandler.this.mAdapter.getCount();
    }

    private boolean deleteDir(File paramFile)
    {
      String[] arrayOfString = paramFile.list();
      if (arrayOfString != null)
        for (int i = 0; i < arrayOfString.length; i++)
          if (!deleteDir(new File(paramFile, arrayOfString[i])))
            return false;
      return paramFile.delete();
    }

    public boolean onActionItemClicked(ActionMode paramActionMode, MenuItem paramMenuItem)
    {
      ListView localListView = MiscFilesHandler.this.getListView();
      switch (paramMenuItem.getItemId())
      {
      default:
        return true;
      case 2131231265:
        SparseBooleanArray localSparseBooleanArray = localListView.getCheckedItemPositions();
        int j = MiscFilesHandler.this.getListView().getCheckedItemCount();
        if (j > this.mDataCount)
          throw new IllegalStateException("checked item counts do not match. checkedCount: " + j + ", dataSize: " + this.mDataCount);
        if (this.mDataCount > 0)
        {
          ArrayList localArrayList = new ArrayList();
          int k = 0;
          while (k < this.mDataCount)
            if (!localSparseBooleanArray.get(k))
            {
              k++;
            }
            else
            {
              if (StorageMeasurement.LOGV)
                Log.i("MemorySettings", "deleting: " + MiscFilesHandler.this.mAdapter.getItem(k));
              File localFile = new File(MiscFilesHandler.this.mAdapter.getItem(k).mFileName);
              if (localFile.isDirectory())
                deleteDir(localFile);
              while (true)
              {
                localArrayList.add(MiscFilesHandler.this.mAdapter.getItem(k));
                break;
                localFile.delete();
              }
            }
          MiscFilesHandler.this.mAdapter.removeAll(localArrayList);
          MiscFilesHandler.this.mAdapter.notifyDataSetChanged();
          this.mDataCount = MiscFilesHandler.this.mAdapter.getCount();
        }
        paramActionMode.finish();
        return true;
      case 2131231266:
      }
      for (int i = 0; i < this.mDataCount; i++)
        localListView.setItemChecked(i, true);
      onItemCheckedStateChanged(paramActionMode, 1, 0L, true);
      return true;
    }

    public boolean onCreateActionMode(ActionMode paramActionMode, Menu paramMenu)
    {
      MiscFilesHandler.this.getMenuInflater().inflate(2131755009, paramMenu);
      return true;
    }

    public void onDestroyActionMode(ActionMode paramActionMode)
    {
    }

    public void onItemCheckedStateChanged(ActionMode paramActionMode, int paramInt, long paramLong, boolean paramBoolean)
    {
      ListView localListView = MiscFilesHandler.this.getListView();
      int i = localListView.getCheckedItemCount();
      String str1 = MiscFilesHandler.this.mNumSelectedFormat;
      Object[] arrayOfObject1 = new Object[2];
      arrayOfObject1[0] = Integer.valueOf(i);
      arrayOfObject1[1] = Integer.valueOf(MiscFilesHandler.this.mAdapter.getCount());
      paramActionMode.setTitle(String.format(str1, arrayOfObject1));
      SparseBooleanArray localSparseBooleanArray = localListView.getCheckedItemPositions();
      long l = 0L;
      if (i > 0)
        for (int j = 0; j < this.mDataCount; j++)
          if (localSparseBooleanArray.get(j))
            l += MiscFilesHandler.this.mAdapter.getItem(j).mSize;
      String str2 = MiscFilesHandler.this.mNumBytesSelectedFormat;
      Object[] arrayOfObject2 = new Object[2];
      arrayOfObject2[0] = Formatter.formatFileSize(this.mContext, l);
      arrayOfObject2[1] = Formatter.formatFileSize(this.mContext, MiscFilesHandler.this.mAdapter.getDataSize());
      paramActionMode.setSubtitle(String.format(str2, arrayOfObject2));
    }

    public boolean onPrepareActionMode(ActionMode paramActionMode, Menu paramMenu)
    {
      return true;
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.deviceinfo.MiscFilesHandler
 * JD-Core Version:    0.6.2
 */
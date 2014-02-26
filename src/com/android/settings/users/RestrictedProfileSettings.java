package com.android.settings.users;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;
import android.preference.PreferenceFragment;
import android.provider.ContactsContract.DisplayPhoto;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import com.android.settings.SettingsPreferenceFragment;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RestrictedProfileSettings extends AppRestrictionsFragment
{
  private Dialog mEditUserInfoDialog;
  private EditUserPhotoController mEditUserPhotoController;
  private View mHeaderView;
  private Bitmap mSavedPhoto;
  private ImageView mUserIconView;
  private TextView mUserNameView;
  private boolean mWaitingForActivityResult;

  private void clearEditUserInfoDialog()
  {
    this.mEditUserInfoDialog = null;
    this.mSavedPhoto = null;
  }

  private UserInfo getExistingUser(UserHandle paramUserHandle)
  {
    Iterator localIterator = this.mUserManager.getUsers(true).iterator();
    while (localIterator.hasNext())
    {
      UserInfo localUserInfo = (UserInfo)localIterator.next();
      if (localUserInfo.id == paramUserHandle.getIdentifier())
        return localUserInfo;
    }
    return null;
  }

  public void onActivityCreated(Bundle paramBundle)
  {
    if (this.mHeaderView == null)
    {
      this.mHeaderView = LayoutInflater.from(getActivity()).inflate(2130968726, null);
      ((ViewGroup)getListView().getParent()).addView(this.mHeaderView, 0);
      this.mHeaderView.setOnClickListener(this);
      this.mUserIconView = ((ImageView)this.mHeaderView.findViewById(16908294));
      this.mUserNameView = ((TextView)this.mHeaderView.findViewById(16908310));
      getListView().setFastScrollEnabled(true);
    }
    super.onActivityCreated(paramBundle);
  }

  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    this.mWaitingForActivityResult = false;
    if ((this.mEditUserInfoDialog != null) && (this.mEditUserInfoDialog.isShowing()) && (this.mEditUserPhotoController.onActivityResult(paramInt1, paramInt2, paramIntent)));
  }

  public void onClick(View paramView)
  {
    if (paramView == this.mHeaderView)
    {
      showDialog(1);
      return;
    }
    super.onClick(paramView);
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (paramBundle != null)
    {
      this.mSavedPhoto = ((Bitmap)paramBundle.getParcelable("pending_photo"));
      this.mWaitingForActivityResult = paramBundle.getBoolean("awaiting_result", false);
    }
    init(paramBundle);
  }

  public Dialog onCreateDialog(int paramInt)
  {
    Dialog localDialog = null;
    if (paramInt == 1)
    {
      if (this.mEditUserInfoDialog != null)
        localDialog = this.mEditUserInfoDialog;
    }
    else
      return localDialog;
    View localView = getActivity().getLayoutInflater().inflate(2130968629, null);
    UserInfo localUserInfo = this.mUserManager.getUserInfo(this.mUser.getIdentifier());
    final EditText localEditText = (EditText)localView.findViewById(2131230847);
    localEditText.setText(localUserInfo.name);
    ImageView localImageView = (ImageView)localView.findViewById(2131230846);
    Object localObject;
    if (this.mSavedPhoto != null)
      localObject = CircleFramedDrawable.getInstance(getActivity(), this.mSavedPhoto);
    while (true)
    {
      localImageView.setImageDrawable((Drawable)localObject);
      this.mEditUserPhotoController = new EditUserPhotoController(this, localImageView, this.mSavedPhoto, (Drawable)localObject, this.mWaitingForActivityResult);
      this.mEditUserInfoDialog = new AlertDialog.Builder(getActivity()).setTitle(2131427617).setIconAttribute(2130837616).setView(localView).setCancelable(true).setPositiveButton(17039370, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          if (paramAnonymousInt == -1)
          {
            Editable localEditable = localEditText.getText();
            if (!TextUtils.isEmpty(localEditable))
            {
              CharSequence localCharSequence = RestrictedProfileSettings.this.mUserNameView.getText();
              if ((localCharSequence == null) || (!localEditable.toString().equals(localCharSequence.toString())))
              {
                ((TextView)RestrictedProfileSettings.this.mHeaderView.findViewById(16908310)).setText(localEditable.toString());
                RestrictedProfileSettings.this.mUserManager.setUserName(RestrictedProfileSettings.this.mUser.getIdentifier(), localEditable.toString());
              }
            }
            Drawable localDrawable = RestrictedProfileSettings.this.mEditUserPhotoController.getNewUserPhotoDrawable();
            Bitmap localBitmap = RestrictedProfileSettings.this.mEditUserPhotoController.getNewUserPhotoBitmap();
            if ((localDrawable != null) && (localBitmap != null) && (!localDrawable.equals(RestrictedProfileSettings.this.mUserIconView.getDrawable())))
            {
              RestrictedProfileSettings.this.mUserIconView.setImageDrawable(localDrawable);
              new AsyncTask()
              {
                protected Void doInBackground(Void[] paramAnonymous2ArrayOfVoid)
                {
                  RestrictedProfileSettings.this.mUserManager.setUserIcon(RestrictedProfileSettings.this.mUser.getIdentifier(), RestrictedProfileSettings.this.mEditUserPhotoController.getNewUserPhotoBitmap());
                  return null;
                }
              }
              .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
            }
            RestrictedProfileSettings.this.removeDialog(1);
          }
          RestrictedProfileSettings.this.clearEditUserInfoDialog();
        }
      }).setNegativeButton(17039360, new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          RestrictedProfileSettings.this.clearEditUserInfoDialog();
        }
      }).create();
      this.mEditUserInfoDialog.getWindow().setSoftInputMode(4);
      return this.mEditUserInfoDialog;
      localObject = this.mUserIconView.getDrawable();
      if (localObject == null)
        localObject = getCircularUserIcon();
    }
  }

  public void onResume()
  {
    super.onResume();
    UserInfo localUserInfo = getExistingUser(this.mUser);
    if (localUserInfo == null)
    {
      finishFragment();
      return;
    }
    ((TextView)this.mHeaderView.findViewById(16908310)).setText(localUserInfo.name);
    ((ImageView)this.mHeaderView.findViewById(16908294)).setImageDrawable(getCircularUserIcon());
  }

  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    if ((this.mEditUserInfoDialog != null) && (this.mEditUserInfoDialog.isShowing()) && (this.mEditUserPhotoController != null))
      paramBundle.putParcelable("pending_photo", this.mEditUserPhotoController.getNewUserPhotoBitmap());
    if (this.mWaitingForActivityResult)
      paramBundle.putBoolean("awaiting_result", this.mWaitingForActivityResult);
  }

  public void startActivityForResult(Intent paramIntent, int paramInt)
  {
    this.mWaitingForActivityResult = true;
    super.startActivityForResult(paramIntent, paramInt);
  }

  private static class EditUserPhotoController
  {
    private final Context mContext;
    private final Uri mCropPictureUri;
    private final Fragment mFragment;
    private final ImageView mImageView;
    private Bitmap mNewUserPhotoBitmap;
    private Drawable mNewUserPhotoDrawable;
    private final int mPhotoSize;
    private final Uri mTakePictureUri;

    public EditUserPhotoController(Fragment paramFragment, ImageView paramImageView, Bitmap paramBitmap, Drawable paramDrawable, boolean paramBoolean)
    {
      this.mContext = paramImageView.getContext();
      this.mFragment = paramFragment;
      this.mImageView = paramImageView;
      Context localContext1 = this.mContext;
      boolean bool2;
      Context localContext2;
      if (!paramBoolean)
      {
        bool2 = bool1;
        this.mCropPictureUri = createTempImageUri(localContext1, "CropEditUserPhoto.jpg", bool2);
        localContext2 = this.mContext;
        if (paramBoolean)
          break label123;
      }
      while (true)
      {
        this.mTakePictureUri = createTempImageUri(localContext2, "TakeEditUserPhoto2.jpg", bool1);
        this.mPhotoSize = getPhotoSize(this.mContext);
        this.mImageView.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            RestrictedProfileSettings.EditUserPhotoController.this.showUpdatePhotoPopup();
          }
        });
        this.mNewUserPhotoBitmap = paramBitmap;
        this.mNewUserPhotoDrawable = paramDrawable;
        return;
        bool2 = false;
        break;
        label123: bool1 = false;
      }
    }

    private void appendCropExtras(Intent paramIntent)
    {
      paramIntent.putExtra("crop", "true");
      paramIntent.putExtra("scale", true);
      paramIntent.putExtra("scaleUpIfNeeded", true);
      paramIntent.putExtra("aspectX", 1);
      paramIntent.putExtra("aspectY", 1);
      paramIntent.putExtra("outputX", this.mPhotoSize);
      paramIntent.putExtra("outputY", this.mPhotoSize);
    }

    private void appendOutputExtra(Intent paramIntent, Uri paramUri)
    {
      paramIntent.putExtra("output", paramUri);
      paramIntent.addFlags(3);
      paramIntent.setClipData(ClipData.newRawUri("output", paramUri));
    }

    private boolean canChoosePhoto()
    {
      Intent localIntent = new Intent("android.intent.action.GET_CONTENT");
      localIntent.setType("image/*");
      int i = this.mImageView.getContext().getPackageManager().queryIntentActivities(localIntent, 0).size();
      boolean bool = false;
      if (i > 0)
        bool = true;
      return bool;
    }

    private boolean canTakePhoto()
    {
      return this.mImageView.getContext().getPackageManager().queryIntentActivities(new Intent("android.media.action.IMAGE_CAPTURE"), 65536).size() > 0;
    }

    private void choosePhoto()
    {
      Intent localIntent = new Intent("android.intent.action.GET_CONTENT", null);
      localIntent.setType("image/*");
      appendOutputExtra(localIntent, this.mTakePictureUri);
      this.mFragment.startActivityForResult(localIntent, 1);
    }

    private Uri createTempImageUri(Context paramContext, String paramString, boolean paramBoolean)
    {
      File localFile1 = paramContext.getCacheDir();
      localFile1.mkdirs();
      File localFile2 = new File(localFile1, paramString);
      if (paramBoolean)
        localFile2.delete();
      return FileProvider.getUriForFile(paramContext, "com.android.settings.files", localFile2);
    }

    private void cropPhoto(Uri paramUri)
    {
      Intent localIntent = new Intent("com.android.camera.action.CROP");
      localIntent.setDataAndType(paramUri, "image/*");
      appendOutputExtra(localIntent, this.mCropPictureUri);
      appendCropExtras(localIntent);
      if (localIntent.resolveActivity(this.mContext.getPackageManager()) != null)
      {
        this.mFragment.startActivityForResult(localIntent, 3);
        return;
      }
      onPhotoCropped(paramUri, false);
    }

    private static int getPhotoSize(Context paramContext)
    {
      Cursor localCursor = paramContext.getContentResolver().query(ContactsContract.DisplayPhoto.CONTENT_MAX_DIMENSIONS_URI, new String[] { "display_max_dim" }, null, null, null);
      try
      {
        localCursor.moveToFirst();
        int i = localCursor.getInt(0);
        return i;
      }
      finally
      {
        localCursor.close();
      }
    }

    private void onPhotoCropped(final Uri paramUri, final boolean paramBoolean)
    {
      new AsyncTask()
      {
        protected Bitmap doInBackground(Void[] paramAnonymousArrayOfVoid)
        {
          if (paramBoolean)
            try
            {
              Bitmap localBitmap3 = BitmapFactory.decodeStream(RestrictedProfileSettings.EditUserPhotoController.this.mContext.getContentResolver().openInputStream(paramUri));
              return localBitmap3;
            }
            catch (FileNotFoundException localFileNotFoundException2)
            {
              return null;
            }
          Bitmap localBitmap1 = Bitmap.createBitmap(RestrictedProfileSettings.EditUserPhotoController.this.mPhotoSize, RestrictedProfileSettings.EditUserPhotoController.this.mPhotoSize, Bitmap.Config.ARGB_8888);
          Canvas localCanvas = new Canvas(localBitmap1);
          try
          {
            Bitmap localBitmap2 = BitmapFactory.decodeStream(RestrictedProfileSettings.EditUserPhotoController.this.mContext.getContentResolver().openInputStream(paramUri));
            if (localBitmap2 != null)
            {
              int i = Math.min(localBitmap2.getWidth(), localBitmap2.getHeight());
              int j = (localBitmap2.getWidth() - i) / 2;
              int k = (localBitmap2.getHeight() - i) / 2;
              localCanvas.drawBitmap(localBitmap2, new Rect(j, k, j + i, k + i), new Rect(0, 0, RestrictedProfileSettings.EditUserPhotoController.this.mPhotoSize, RestrictedProfileSettings.EditUserPhotoController.this.mPhotoSize), new Paint());
              return localBitmap1;
            }
          }
          catch (FileNotFoundException localFileNotFoundException1)
          {
            return null;
          }
          return null;
        }

        protected void onPostExecute(Bitmap paramAnonymousBitmap)
        {
          if (paramAnonymousBitmap != null)
          {
            RestrictedProfileSettings.EditUserPhotoController.access$1102(RestrictedProfileSettings.EditUserPhotoController.this, paramAnonymousBitmap);
            RestrictedProfileSettings.EditUserPhotoController.access$1202(RestrictedProfileSettings.EditUserPhotoController.this, CircleFramedDrawable.getInstance(RestrictedProfileSettings.EditUserPhotoController.this.mImageView.getContext(), RestrictedProfileSettings.EditUserPhotoController.this.mNewUserPhotoBitmap));
            RestrictedProfileSettings.EditUserPhotoController.this.mImageView.setImageDrawable(RestrictedProfileSettings.EditUserPhotoController.this.mNewUserPhotoDrawable);
          }
          new File(RestrictedProfileSettings.EditUserPhotoController.this.mContext.getCacheDir(), "TakeEditUserPhoto2.jpg").delete();
          new File(RestrictedProfileSettings.EditUserPhotoController.this.mContext.getCacheDir(), "CropEditUserPhoto.jpg").delete();
        }
      }
      .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
    }

    private void showUpdatePhotoPopup()
    {
      boolean bool1 = canTakePhoto();
      boolean bool2 = canChoosePhoto();
      if ((!bool1) && (!bool2))
        return;
      Context localContext = this.mImageView.getContext();
      final ArrayList localArrayList = new ArrayList();
      if (canTakePhoto())
        localArrayList.add(new AdapterItem(this.mImageView.getContext().getString(2131429291), 2));
      if (bool2)
        localArrayList.add(new AdapterItem(localContext.getString(2131429292), 1));
      final ListPopupWindow localListPopupWindow = new ListPopupWindow(localContext);
      localListPopupWindow.setAnchorView(this.mImageView);
      localListPopupWindow.setModal(true);
      localListPopupWindow.setInputMethodMode(2);
      localListPopupWindow.setAdapter(new ArrayAdapter(localContext, 2130968630, localArrayList));
      localListPopupWindow.setWidth(Math.max(this.mImageView.getWidth(), localContext.getResources().getDimensionPixelSize(2131558438)));
      localListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener()
      {
        public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          switch (((RestrictedProfileSettings.EditUserPhotoController.AdapterItem)localArrayList.get(paramAnonymousInt)).id)
          {
          default:
            return;
          case 1:
            RestrictedProfileSettings.EditUserPhotoController.this.choosePhoto();
            localListPopupWindow.dismiss();
            return;
          case 2:
          }
          RestrictedProfileSettings.EditUserPhotoController.this.takePhoto();
          localListPopupWindow.dismiss();
        }
      });
      localListPopupWindow.show();
    }

    private void takePhoto()
    {
      Intent localIntent = new Intent("android.media.action.IMAGE_CAPTURE");
      appendOutputExtra(localIntent, this.mTakePictureUri);
      this.mFragment.startActivityForResult(localIntent, 2);
    }

    public Bitmap getNewUserPhotoBitmap()
    {
      return this.mNewUserPhotoBitmap;
    }

    public Drawable getNewUserPhotoDrawable()
    {
      return this.mNewUserPhotoDrawable;
    }

    public boolean onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
    {
      if (paramInt2 != -1)
        return false;
      if ((paramIntent != null) && (paramIntent.getData() != null));
      for (Uri localUri = paramIntent.getData(); ; localUri = this.mTakePictureUri)
        switch (paramInt1)
        {
        default:
          return false;
        case 1:
        case 2:
          cropPhoto(localUri);
          return true;
        case 3:
        }
      onPhotoCropped(localUri, true);
      return true;
    }

    private static final class AdapterItem
    {
      final int id;
      final String title;

      public AdapterItem(String paramString, int paramInt)
      {
        this.title = paramString;
        this.id = paramInt;
      }

      public String toString()
      {
        return this.title;
      }
    }
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.users.RestrictedProfileSettings
 * JD-Core Version:    0.6.2
 */
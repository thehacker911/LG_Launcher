package com.android.settings.inputmethod;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.Loader;
import android.hardware.input.InputManager;
import android.hardware.input.InputManager.InputDeviceListener;
import android.hardware.input.KeyboardLayout;
import android.os.Bundle;
import android.view.InputDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;

public class KeyboardLayoutDialogFragment extends DialogFragment
  implements InputManager.InputDeviceListener, LoaderManager.LoaderCallbacks<Keyboards>
{
  private KeyboardLayoutAdapter mAdapter;
  private InputManager mIm;
  private String mInputDeviceDescriptor;
  private int mInputDeviceId = -1;

  public KeyboardLayoutDialogFragment()
  {
  }

  public KeyboardLayoutDialogFragment(String paramString)
  {
    this.mInputDeviceDescriptor = paramString;
  }

  private void onKeyboardLayoutClicked(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < this.mAdapter.getCount()))
    {
      KeyboardLayout localKeyboardLayout = (KeyboardLayout)this.mAdapter.getItem(paramInt);
      if (localKeyboardLayout != null)
        this.mIm.setCurrentKeyboardLayoutForInputDevice(this.mInputDeviceDescriptor, localKeyboardLayout.getDescriptor());
      dismiss();
    }
  }

  private void onSetupLayoutsButtonClicked()
  {
    ((OnSetupKeyboardLayoutsListener)getTargetFragment()).onSetupKeyboardLayouts(this.mInputDeviceDescriptor);
  }

  private void updateSwitchHintVisibility()
  {
    AlertDialog localAlertDialog = (AlertDialog)getDialog();
    View localView;
    if (localAlertDialog != null)
    {
      localView = localAlertDialog.findViewById(16908919);
      if (this.mAdapter.getCount() <= 1)
        break label38;
    }
    label38: for (int i = 0; ; i = 8)
    {
      localView.setVisibility(i);
      return;
    }
  }

  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    show(getActivity().getFragmentManager(), "layout");
  }

  public void onAttach(Activity paramActivity)
  {
    super.onAttach(paramActivity);
    Context localContext = paramActivity.getBaseContext();
    this.mIm = ((InputManager)localContext.getSystemService("input"));
    this.mAdapter = new KeyboardLayoutAdapter(localContext);
  }

  public void onCancel(DialogInterface paramDialogInterface)
  {
    super.onCancel(paramDialogInterface);
    dismiss();
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    if (paramBundle != null)
      this.mInputDeviceDescriptor = paramBundle.getString("inputDeviceDescriptor");
    getLoaderManager().initLoader(0, null, this);
  }

  public Dialog onCreateDialog(Bundle paramBundle)
  {
    Activity localActivity = getActivity();
    LayoutInflater localLayoutInflater = LayoutInflater.from(localActivity);
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(localActivity).setTitle(2131428523).setPositiveButton(2131428524, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        KeyboardLayoutDialogFragment.this.onSetupLayoutsButtonClicked();
      }
    }).setSingleChoiceItems(this.mAdapter, -1, new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        KeyboardLayoutDialogFragment.this.onKeyboardLayoutClicked(paramAnonymousInt);
      }
    }).setView(localLayoutInflater.inflate(2130968637, null));
    updateSwitchHintVisibility();
    return localBuilder.create();
  }

  public Loader<Keyboards> onCreateLoader(int paramInt, Bundle paramBundle)
  {
    return new KeyboardLayoutLoader(getActivity().getBaseContext(), this.mInputDeviceDescriptor);
  }

  public void onInputDeviceAdded(int paramInt)
  {
  }

  public void onInputDeviceChanged(int paramInt)
  {
    if ((this.mInputDeviceId >= 0) && (paramInt == this.mInputDeviceId))
      getLoaderManager().restartLoader(0, null, this);
  }

  public void onInputDeviceRemoved(int paramInt)
  {
    if ((this.mInputDeviceId >= 0) && (paramInt == this.mInputDeviceId))
      dismiss();
  }

  public void onLoadFinished(Loader<Keyboards> paramLoader, Keyboards paramKeyboards)
  {
    this.mAdapter.clear();
    this.mAdapter.addAll(paramKeyboards.keyboardLayouts);
    this.mAdapter.setCheckedItem(paramKeyboards.current);
    AlertDialog localAlertDialog = (AlertDialog)getDialog();
    if (localAlertDialog != null)
      localAlertDialog.getListView().setItemChecked(paramKeyboards.current, true);
    updateSwitchHintVisibility();
  }

  public void onLoaderReset(Loader<Keyboards> paramLoader)
  {
    this.mAdapter.clear();
    updateSwitchHintVisibility();
  }

  public void onPause()
  {
    this.mIm.unregisterInputDeviceListener(this);
    this.mInputDeviceId = -1;
    super.onPause();
  }

  public void onResume()
  {
    super.onResume();
    this.mIm.registerInputDeviceListener(this, null);
    InputDevice localInputDevice = this.mIm.getInputDeviceByDescriptor(this.mInputDeviceDescriptor);
    if (localInputDevice == null)
    {
      dismiss();
      return;
    }
    this.mInputDeviceId = localInputDevice.getId();
  }

  public void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    paramBundle.putString("inputDeviceDescriptor", this.mInputDeviceDescriptor);
  }

  private static final class KeyboardLayoutAdapter extends ArrayAdapter<KeyboardLayout>
  {
    private int mCheckedItem = -1;
    private final LayoutInflater mInflater;

    public KeyboardLayoutAdapter(Context paramContext)
    {
      super(17367200);
      this.mInflater = ((LayoutInflater)paramContext.getSystemService("layout_inflater"));
    }

    private View inflateOneLine(View paramView, ViewGroup paramViewGroup, String paramString, boolean paramBoolean)
    {
      View localView = paramView;
      if ((localView == null) || (isTwoLine(localView)))
      {
        localView = this.mInflater.inflate(17367055, paramViewGroup, false);
        setTwoLine(localView, false);
      }
      CheckedTextView localCheckedTextView = (CheckedTextView)localView.findViewById(16908308);
      localCheckedTextView.setText(paramString);
      localCheckedTextView.setChecked(paramBoolean);
      return localView;
    }

    private View inflateTwoLine(View paramView, ViewGroup paramViewGroup, String paramString1, String paramString2, boolean paramBoolean)
    {
      View localView = paramView;
      if ((localView == null) || (!isTwoLine(localView)))
      {
        localView = this.mInflater.inflate(17367200, paramViewGroup, false);
        setTwoLine(localView, true);
      }
      TextView localTextView1 = (TextView)localView.findViewById(16908308);
      TextView localTextView2 = (TextView)localView.findViewById(16908309);
      RadioButton localRadioButton = (RadioButton)localView.findViewById(16908994);
      localTextView1.setText(paramString1);
      localTextView2.setText(paramString2);
      localRadioButton.setChecked(paramBoolean);
      return localView;
    }

    private static boolean isTwoLine(View paramView)
    {
      return paramView.getTag() == Boolean.TRUE;
    }

    private static void setTwoLine(View paramView, boolean paramBoolean)
    {
      paramView.setTag(Boolean.valueOf(paramBoolean));
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      KeyboardLayout localKeyboardLayout = (KeyboardLayout)getItem(paramInt);
      String str1;
      String str2;
      if (localKeyboardLayout != null)
      {
        str1 = localKeyboardLayout.getLabel();
        str2 = localKeyboardLayout.getCollection();
        if (paramInt != this.mCheckedItem)
          break label77;
      }
      label77: for (boolean bool = true; ; bool = false)
      {
        if (!str2.isEmpty())
          break label83;
        return inflateOneLine(paramView, paramViewGroup, str1, bool);
        str1 = getContext().getString(2131428526);
        str2 = "";
        break;
      }
      label83: return inflateTwoLine(paramView, paramViewGroup, str1, str2, bool);
    }

    public void setCheckedItem(int paramInt)
    {
      this.mCheckedItem = paramInt;
    }
  }

  private static final class KeyboardLayoutLoader extends AsyncTaskLoader<KeyboardLayoutDialogFragment.Keyboards>
  {
    private final String mInputDeviceDescriptor;

    public KeyboardLayoutLoader(Context paramContext, String paramString)
    {
      super();
      this.mInputDeviceDescriptor = paramString;
    }

    public KeyboardLayoutDialogFragment.Keyboards loadInBackground()
    {
      KeyboardLayoutDialogFragment.Keyboards localKeyboards = new KeyboardLayoutDialogFragment.Keyboards();
      InputManager localInputManager = (InputManager)getContext().getSystemService("input");
      String[] arrayOfString = localInputManager.getKeyboardLayoutsForInputDevice(this.mInputDeviceDescriptor);
      int i = arrayOfString.length;
      for (int j = 0; j < i; j++)
      {
        KeyboardLayout localKeyboardLayout = localInputManager.getKeyboardLayout(arrayOfString[j]);
        if (localKeyboardLayout != null)
          localKeyboards.keyboardLayouts.add(localKeyboardLayout);
      }
      Collections.sort(localKeyboards.keyboardLayouts);
      String str = localInputManager.getCurrentKeyboardLayoutForInputDevice(this.mInputDeviceDescriptor);
      int k;
      if (str != null)
        k = localKeyboards.keyboardLayouts.size();
      for (int m = 0; ; m++)
        if (m < k)
        {
          if (((KeyboardLayout)localKeyboards.keyboardLayouts.get(m)).getDescriptor().equals(str))
            localKeyboards.current = m;
        }
        else
        {
          if (localKeyboards.keyboardLayouts.isEmpty())
          {
            localKeyboards.keyboardLayouts.add(null);
            localKeyboards.current = 0;
          }
          return localKeyboards;
        }
    }

    protected void onStartLoading()
    {
      super.onStartLoading();
      forceLoad();
    }

    protected void onStopLoading()
    {
      super.onStopLoading();
      cancelLoad();
    }
  }

  public static final class Keyboards
  {
    public int current = -1;
    public final ArrayList<KeyboardLayout> keyboardLayouts = new ArrayList();
  }

  public static abstract interface OnSetupKeyboardLayoutsListener
  {
    public abstract void onSetupKeyboardLayouts(String paramString);
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.inputmethod.KeyboardLayoutDialogFragment
 * JD-Core Version:    0.6.2
 */
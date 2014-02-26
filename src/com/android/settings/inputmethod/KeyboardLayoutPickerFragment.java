package com.android.settings.inputmethod;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.hardware.input.InputManager.InputDeviceListener;
import android.hardware.input.KeyboardLayout;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.TwoStatePreference;
import android.view.InputDevice;
import com.android.settings.SettingsPreferenceFragment;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class KeyboardLayoutPickerFragment extends SettingsPreferenceFragment
  implements InputManager.InputDeviceListener
{
  private InputManager mIm;
  private String mInputDeviceDescriptor;
  private int mInputDeviceId = -1;
  private KeyboardLayout[] mKeyboardLayouts;
  private HashMap<CheckBoxPreference, KeyboardLayout> mPreferenceMap = new HashMap();

  private PreferenceScreen createPreferenceHierarchy()
  {
    PreferenceScreen localPreferenceScreen = getPreferenceManager().createPreferenceScreen(getActivity());
    Activity localActivity = getActivity();
    for (KeyboardLayout localKeyboardLayout : this.mKeyboardLayouts)
    {
      CheckBoxPreference localCheckBoxPreference = new CheckBoxPreference(localActivity);
      localCheckBoxPreference.setTitle(localKeyboardLayout.getLabel());
      localCheckBoxPreference.setSummary(localKeyboardLayout.getCollection());
      localPreferenceScreen.addPreference(localCheckBoxPreference);
      this.mPreferenceMap.put(localCheckBoxPreference, localKeyboardLayout);
    }
    return localPreferenceScreen;
  }

  private void updateCheckedState()
  {
    String[] arrayOfString = this.mIm.getKeyboardLayoutsForInputDevice(this.mInputDeviceDescriptor);
    Arrays.sort(arrayOfString);
    Iterator localIterator = this.mPreferenceMap.entrySet().iterator();
    if (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      CheckBoxPreference localCheckBoxPreference = (CheckBoxPreference)localEntry.getKey();
      if (Arrays.binarySearch(arrayOfString, ((KeyboardLayout)localEntry.getValue()).getDescriptor()) >= 0);
      for (boolean bool = true; ; bool = false)
      {
        localCheckBoxPreference.setChecked(bool);
        break;
      }
    }
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.mInputDeviceDescriptor = getActivity().getIntent().getStringExtra("input_device_descriptor");
    if (this.mInputDeviceDescriptor == null)
      getActivity().finish();
    this.mIm = ((InputManager)getSystemService("input"));
    this.mKeyboardLayouts = this.mIm.getKeyboardLayouts();
    Arrays.sort(this.mKeyboardLayouts);
    setPreferenceScreen(createPreferenceHierarchy());
  }

  public void onInputDeviceAdded(int paramInt)
  {
  }

  public void onInputDeviceChanged(int paramInt)
  {
    if ((this.mInputDeviceId >= 0) && (paramInt == this.mInputDeviceId))
      updateCheckedState();
  }

  public void onInputDeviceRemoved(int paramInt)
  {
    if ((this.mInputDeviceId >= 0) && (paramInt == this.mInputDeviceId))
      getActivity().finish();
  }

  public void onPause()
  {
    this.mIm.unregisterInputDeviceListener(this);
    this.mInputDeviceId = -1;
    super.onPause();
  }

  public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference)
  {
    if ((paramPreference instanceof CheckBoxPreference))
    {
      CheckBoxPreference localCheckBoxPreference = (CheckBoxPreference)paramPreference;
      KeyboardLayout localKeyboardLayout = (KeyboardLayout)this.mPreferenceMap.get(localCheckBoxPreference);
      if (localKeyboardLayout != null)
      {
        if (localCheckBoxPreference.isChecked())
          this.mIm.addKeyboardLayoutForInputDevice(this.mInputDeviceDescriptor, localKeyboardLayout.getDescriptor());
        while (true)
        {
          return true;
          this.mIm.removeKeyboardLayoutForInputDevice(this.mInputDeviceDescriptor, localKeyboardLayout.getDescriptor());
        }
      }
    }
    return super.onPreferenceTreeClick(paramPreferenceScreen, paramPreference);
  }

  public void onResume()
  {
    super.onResume();
    this.mIm.registerInputDeviceListener(this, null);
    InputDevice localInputDevice = this.mIm.getInputDeviceByDescriptor(this.mInputDeviceDescriptor);
    if (localInputDevice == null)
    {
      getActivity().finish();
      return;
    }
    this.mInputDeviceId = localInputDevice.getId();
    updateCheckedState();
  }
}

/* Location:           C:\dex 2 jar\classes-dex2jar.jar
 * Qualified Name:     com.android.settings.inputmethod.KeyboardLayoutPickerFragment
 * JD-Core Version:    0.6.2
 */
package eu.chainfire.gingerbreak;

import android.content.Context;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.widget.EditText;

public class Pref
{
  public static PreferenceCategory Category(Context paramContext, PreferenceScreen paramPreferenceScreen, String paramString)
  {
    PreferenceCategory localPreferenceCategory = new PreferenceCategory(paramContext);
    localPreferenceCategory.setTitle(paramString);
    paramPreferenceScreen.addPreference(localPreferenceCategory);
    return localPreferenceCategory;
  }

  public static CheckBoxPreference Check(Context paramContext, PreferenceCategory paramPreferenceCategory, String paramString1, String paramString2, String paramString3, Object paramObject)
  {
    return Check(paramContext, paramPreferenceCategory, paramString1, paramString2, paramString3, paramObject, true);
  }

  public static CheckBoxPreference Check(Context paramContext, PreferenceCategory paramPreferenceCategory, String paramString1, String paramString2, String paramString3, Object paramObject, boolean paramBoolean)
  {
    CheckBoxPreference localCheckBoxPreference = new CheckBoxPreference(paramContext);
    localCheckBoxPreference.setTitle(paramString1);
    localCheckBoxPreference.setSummary(paramString2);
    localCheckBoxPreference.setEnabled(paramBoolean);
    localCheckBoxPreference.setKey(paramString3);
    localCheckBoxPreference.setDefaultValue(paramObject);
    paramPreferenceCategory.addPreference(localCheckBoxPreference);
    return localCheckBoxPreference;
  }

  public static EditTextPreference Edit(Context paramContext, PreferenceCategory paramPreferenceCategory, String paramString1, String paramString2, String paramString3, String paramString4, Object paramObject)
  {
    return Edit(paramContext, paramPreferenceCategory, paramString1, paramString2, paramString3, paramString4, paramObject, true, null);
  }

  public static EditTextPreference Edit(Context paramContext, PreferenceCategory paramPreferenceCategory, String paramString1, String paramString2, String paramString3, String paramString4, Object paramObject, Integer paramInteger)
  {
    return Edit(paramContext, paramPreferenceCategory, paramString1, paramString2, paramString3, paramString4, paramObject, true, paramInteger);
  }

  public static EditTextPreference Edit(Context paramContext, PreferenceCategory paramPreferenceCategory, String paramString1, String paramString2, String paramString3, String paramString4, Object paramObject, boolean paramBoolean)
  {
    return Edit(paramContext, paramPreferenceCategory, paramString1, paramString2, paramString3, paramString4, paramObject, paramBoolean, null);
  }

  public static EditTextPreference Edit(Context paramContext, PreferenceCategory paramPreferenceCategory, String paramString1, String paramString2, String paramString3, String paramString4, Object paramObject, boolean paramBoolean, Integer paramInteger)
  {
    EditTextPreference localEditTextPreference = new EditTextPreference(paramContext);
    localEditTextPreference.setTitle(paramString1);
    localEditTextPreference.setSummary(paramString2);
    localEditTextPreference.setEnabled(paramBoolean);
    localEditTextPreference.setKey(paramString4);
    localEditTextPreference.setDefaultValue(paramObject);
    localEditTextPreference.setDialogTitle(paramString3);
    if (paramInteger != null)
      localEditTextPreference.getEditText().setInputType(paramInteger.intValue());
    paramPreferenceCategory.addPreference(localEditTextPreference);
    return localEditTextPreference;
  }

  public static ListPreference List(Context paramContext, PreferenceCategory paramPreferenceCategory, String paramString1, String paramString2, String paramString3, String paramString4, Object paramObject, CharSequence[] paramArrayOfCharSequence1, CharSequence[] paramArrayOfCharSequence2)
  {
    return List(paramContext, paramPreferenceCategory, paramString1, paramString2, paramString3, paramString4, paramObject, paramArrayOfCharSequence1, paramArrayOfCharSequence2, true);
  }

  public static ListPreference List(Context paramContext, PreferenceCategory paramPreferenceCategory, String paramString1, String paramString2, String paramString3, String paramString4, Object paramObject, CharSequence[] paramArrayOfCharSequence1, CharSequence[] paramArrayOfCharSequence2, boolean paramBoolean)
  {
    ListPreference localListPreference = new ListPreference(paramContext);
    localListPreference.setTitle(paramString1);
    localListPreference.setSummary(paramString2);
    localListPreference.setEnabled(paramBoolean);
    localListPreference.setKey(paramString4);
    localListPreference.setDefaultValue(paramObject);
    localListPreference.setDialogTitle(paramString3);
    localListPreference.setEntries(paramArrayOfCharSequence1);
    localListPreference.setEntryValues(paramArrayOfCharSequence2);
    paramPreferenceCategory.addPreference(localListPreference);
    return localListPreference;
  }

  public static Preference Preference(Context paramContext, PreferenceCategory paramPreferenceCategory, String paramString1, String paramString2, boolean paramBoolean, Preference.OnPreferenceClickListener paramOnPreferenceClickListener)
  {
    Preference localPreference = new Preference(paramContext);
    localPreference.setTitle(paramString1);
    localPreference.setSummary(paramString2);
    localPreference.setEnabled(paramBoolean);
    if (paramOnPreferenceClickListener != null)
      localPreference.setOnPreferenceClickListener(paramOnPreferenceClickListener);
    paramPreferenceCategory.addPreference(localPreference);
    return localPreference;
  }
}

/* Location:           E:\开发工具\android开发工具\反编译工具\apktool2.2\gingerbreak\gingerbreak_dex2jar.jar
 * Qualified Name:     eu.chainfire.gingerbreak.Pref
 * JD-Core Version:    0.6.0
 */
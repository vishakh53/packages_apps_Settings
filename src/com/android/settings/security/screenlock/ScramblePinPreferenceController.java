/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.security.screenlock;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.os.UserHandle;
import android.provider.Settings;
import android.provider.Settings.Secure;

import androidx.preference.Preference;
import androidx.preference.TwoStatePreference;

import com.android.internal.widget.LockPatternUtils;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;

public class ScramblePinPreferenceController extends AbstractPreferenceController
        implements PreferenceControllerMixin, Preference.OnPreferenceChangeListener {

    private static final String KEY_SCRAMBLE_PIN_LAYOUT = "scramble_pin_layout";

    private final int mUserId;
    private final LockPatternUtils mLockPatternUtils;

    public ScramblePinPreferenceController(Context context, int userId,
            LockPatternUtils lockPatternUtils) {
        super(context);
        mUserId = userId;
        mLockPatternUtils = lockPatternUtils;
    }

    @Override
    public boolean isAvailable() {
        if (!mLockPatternUtils.isSecure(mUserId)) {
            return false;
        }
        switch (mLockPatternUtils.getKeyguardStoredPasswordQuality(mUserId)) {
            case DevicePolicyManager.PASSWORD_QUALITY_NUMERIC:
            case DevicePolicyManager.PASSWORD_QUALITY_NUMERIC_COMPLEX:
                return true;
            default:
                return false;
        }
    }

    @Override
    public void updateState(Preference preference) {
        int enabled = Settings.Secure.getIntForUser(mContext.getContentResolver(),
                Settings.Secure.LOCKSCREEN_PIN_SCRAMBLE_LAYOUT, 0,
                UserHandle.USER_CURRENT);
        ((TwoStatePreference) preference).setChecked(enabled != 0);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Settings.Secure.putIntForUser(mContext.getContentResolver(),
                Settings.Secure.LOCKSCREEN_PIN_SCRAMBLE_LAYOUT,
                (Boolean) newValue ? 1 : 0, UserHandle.USER_CURRENT);
        return true;
    }

    @Override
    public String getPreferenceKey() {
        return KEY_SCRAMBLE_PIN_LAYOUT;
    }
}

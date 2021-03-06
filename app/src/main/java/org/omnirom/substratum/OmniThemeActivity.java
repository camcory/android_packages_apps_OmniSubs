/*
 * Copyright (c) 2016-2017 Projekt Substratum
 * This file is part of Substratum.
 *
 * Substratum is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Substratum is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Substratum.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.omnirom.substratum;

import android.app.Activity;
import android.os.Bundle;

import projekt.substratum.common.References;

public class OmniThemeActivity extends Activity {

    /**
     * Controlled activity for ROM cherry-picked Settings/QS tile shortcuts
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        References.launchTheme(this,
                "org.omnirom.daynight",
                References.overlaysFragment
        );
        this.finish();
    }
}
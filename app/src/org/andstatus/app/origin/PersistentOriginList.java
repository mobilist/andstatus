/**
 * Copyright (C) 2015 yvolk (Yuri Volkov), http://yurivolkov.com
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

package org.andstatus.app.origin;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import org.andstatus.app.ActivityRequestCode;
import org.andstatus.app.IntentExtra;
import org.andstatus.app.R;
import org.andstatus.app.context.MyContextHolder;
import org.andstatus.app.util.MyLog;

public class PersistentOriginList extends OriginList {

    protected Iterable<Origin> getOrigins() {
        return MyContextHolder.get().persistentOrigins().collection();
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.addOriginButton);
        item.setEnabled(addEnabled);
        item.setVisible(addEnabled);

        return super.onPrepareOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addOriginButton:
                onAddOriginSelected("");
                break;
            case R.id.discoverOpenInstances:
                Intent intent = new Intent(this, DiscoveredOriginList.class);
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent, ActivityRequestCode.SELECT_OPEN_INSTANCE.id);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onAddOriginSelected(String originName) {
        Intent intent = new Intent(this, OriginEditor.class);
        intent.setAction(Intent.ACTION_INSERT);
        intent.putExtra(IntentExtra.EXTRA_ORIGIN_NAME.key, originName);
        startActivityForResult(intent, ActivityRequestCode.EDIT_ORIGIN.id);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        MyLog.v(this, "onActivityResult " + ActivityRequestCode.fromId(requestCode) );
        switch (ActivityRequestCode.fromId(requestCode)) {
            case EDIT_ORIGIN:
                fillList();
                break;
            case SELECT_OPEN_INSTANCE:
                if (resultCode == Activity.RESULT_OK) {
                    String originName = data.getStringExtra(IntentExtra.EXTRA_ORIGIN_NAME.key);
                    if (!TextUtils.isEmpty(originName)) {
                        onAddOriginSelected(originName);
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.persistent_origin_list;
    }

    @Override
    protected int getMenuResourceId() {
        return R.menu.persistent_origin_list;
    }
}

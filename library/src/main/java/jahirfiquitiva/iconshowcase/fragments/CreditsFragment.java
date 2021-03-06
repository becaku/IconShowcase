/*
 * Copyright (c) 2016 Jahir Fiquitiva
 *
 * Licensed under the CreativeCommons Attribution-ShareAlike
 * 4.0 International License. You may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 *    http://creativecommons.org/licenses/by-sa/4.0/legalcode
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Special thanks to the project contributors and collaborators
 * 	https://github.com/jahirfiquitiva/IconShowcase#special-thanks
 */

package jahirfiquitiva.iconshowcase.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pitchedapps.capsule.library.fragments.CapsuleFragment;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.adapters.CreditsAdapter;
import jahirfiquitiva.iconshowcase.enums.DrawerItem;

public class CreditsFragment extends CapsuleFragment {

    private Context context;

    @Override
    public void onFabClick(View v) {

    }

    @Override
    public int getTitleId() {
        return DrawerItem.CREDITS.getTitleID();
    }

    @Override
    protected int getFabIcon() {
        return 0;
    }

    /**
     * Will hide the fab if false; the fab is still in the viewgroup and is used for various other tasks such as the snackbar
     *
     * @return
     */
    @Override
    protected boolean hasFab() {
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        context = getActivity();

        View layout = inflater.inflate(R.layout.credits_section, container, false);

        int columnsNumber = getResources().getInteger(R.integer.credits_grid_width);

        RecyclerView recyclerView = (RecyclerView) layout.findViewById(R.id.credits_rv);
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(columnsNumber, StaggeredGridLayoutManager.VERTICAL));

        RecyclerFastScroller fastScroller = (RecyclerFastScroller) layout.findViewById(R.id.rvFastScroller);

        CreditsAdapter adapter = new CreditsAdapter(context);

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        fastScroller.attachRecyclerView(recyclerView);

        return layout;
    }

}
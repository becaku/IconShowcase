/*
 * Copyright (c) 2016.  Jahir Fiquitiva
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
 * Big thanks to the project contributors. Check them in the repository.
 *
 */

/*
 *
 */

package jahirfiquitiva.iconshowcase.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.activities.ShowcaseActivity;
import jahirfiquitiva.iconshowcase.adapters.HomeListAdapter;
import jahirfiquitiva.iconshowcase.models.HomeCard;
import jahirfiquitiva.iconshowcase.utilities.LauncherIntents;
import jahirfiquitiva.iconshowcase.utilities.ThemeUtils;
import jahirfiquitiva.iconshowcase.utilities.Utils;

public class MainFragment extends Fragment {

    private Context context;

    private static final String MARKET_URL = "https://play.google.com/store/apps/details?id=";
    private String PlayStoreListing;
    private ViewGroup layout;
    private ImageView iconsIV, wallsIV, widgetsIV;

    private boolean themeMode, cm, cyngn, rro; //to store theme engine installation status

    private RecyclerView mRecyclerView;
    private ArrayList<HomeCard> homeCards = new ArrayList<>();
    private Drawable playStoreDrawable;
//    private static final String ARGS_HOME_CARDS = "args_home_cards";

//    public static MainFragment newInstance(ArrayList<HomeCard> homeCards2) {
//        Log.e("asdf homecards2", "" + homeCards2.size());
//        MainFragment mF = new MainFragment();
//        if (homeCards2.size() > 0) {
//            test = true;
//            Log.e("asdf", "add bundle");
//            Bundle args = new Bundle();
//            args.putParcelableArrayList(ARGS_HOME_CARDS, homeCards2);
//        }
//        return mF;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        context = getActivity();
//        if (test) {
//            Log.e("asdf", "read bundle");

//            homeCards = getArguments().getParcelableArrayList(ARGS_HOME_CARDS);
//        }
        if (layout != null) {
            ViewGroup parent = (ViewGroup) layout.getParent();
            if (parent != null) {
                parent.removeView(layout);
            }
        }
        try {
            layout = (ViewGroup) inflater.inflate(R.layout.main_section, container, false);
        } catch (InflateException e) {
            //Do nothing
        }

        themeMode = getResources().getBoolean(R.bool.theme_mode);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.home_rv);

        if(!themeMode) {
            ShowcaseActivity.setupIcons(ShowcaseActivity.icon1, ShowcaseActivity.icon2,
                    ShowcaseActivity.icon3, ShowcaseActivity.icon4, ShowcaseActivity.icon5,
                    ShowcaseActivity.icon6, ShowcaseActivity.icon7, ShowcaseActivity.icon8,
                    ShowcaseActivity.numOfIcons);
        }

        String themedIcons = String.valueOf(getActivity().getResources().getInteger(R.integer.icons_amount));
        String availableWallpapers = String.valueOf(getActivity().getResources().getInteger(R.integer.walls_amount));
        String includedWidgets = String.valueOf(getActivity().getResources().getInteger(R.integer.zooper_widgets));

        iconsIV = (ImageView) layout.findViewById(R.id.icon_themed_icons);
        wallsIV = (ImageView) layout.findViewById(R.id.icon_available_wallpapers);
        widgetsIV = (ImageView) layout.findViewById(R.id.icon_included_widgets);
        setupIcons(getActivity());

        TextView iconsT = (TextView) layout.findViewById(R.id.text_themed_icons);
        iconsT.setText(getActivity().getResources().getString(R.string.themed_icons, themedIcons));

        TextView wallsT = (TextView) layout.findViewById(R.id.text_available_wallpapers);
        wallsT.setText(getActivity().getResources().getString(R.string.available_wallpapers, availableWallpapers));

        TextView widgetsT = (TextView) layout.findViewById(R.id.text_included_widgets);
        widgetsT.setText(getActivity().getResources().getString(R.string.included_widgets, includedWidgets));

        PlayStoreListing = getActivity().getPackageName();

        LinearLayout packInfo = (LinearLayout) layout.findViewById(R.id.appDetails);
        View divider = layout.findViewById(R.id.divider);
        packInfo.setVisibility(getActivity().getResources().getBoolean(R.bool.hide_pack_info) ?
                View.GONE :
                View.VISIBLE);
        divider.setVisibility(getActivity().getResources().getBoolean(R.bool.hide_pack_info) ?
                View.GONE :
                View.VISIBLE);

        if (!ShowcaseActivity.WITH_ZOOPER_SECTION) {
            LinearLayout widgets = (LinearLayout) layout.findViewById(R.id.widgets);
            widgets.setVisibility(View.GONE);
        }

        AppCompatButton ratebtn = (AppCompatButton) layout.findViewById(R.id.rate_button);
        ratebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent rate = new Intent(Intent.ACTION_VIEW, Uri.parse(MARKET_URL + PlayStoreListing));
                startActivity(rate);
            }
        });

        AppCompatButton iconsbtn = (AppCompatButton) layout.findViewById(R.id.icons_button);
        iconsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowcaseActivity.drawerItemClick(ShowcaseActivity.iconsPickerIdentifier);
                ShowcaseActivity.drawer.setSelection(ShowcaseActivity.iconsPickerIdentifier);
            }
        });

        if(!themeMode) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ShowcaseActivity.animateIcons(ShowcaseActivity.icon1, ShowcaseActivity.icon2,
                            ShowcaseActivity.icon3, ShowcaseActivity.icon4, ShowcaseActivity.icon5,
                            ShowcaseActivity.icon6, ShowcaseActivity.icon7, ShowcaseActivity.icon8,
                            ShowcaseActivity.numOfIcons);
                }
            }, 500);
        }

        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

//        Bundle bundle = getActivity().getIntent().getExtras();
//        ArrayList<HomeCard> homeCards = bundle
//                .getParcelableArrayList("homeCards");


//        Log.e("asdf to string", getActivity().getIntent().getParcelableArrayListExtra("homeCards").toString());
//        ArrayList<HomeCard> homeCards = getActivity().getIntent().getParcelableArrayListExtra("homeCards");

        homeCards.add(new HomeCard.Builder()
                .title(getResources().getString(R.string.more_apps))
                .description(getResources().getString(R.string.more_apps_long))
                .icon(playStoreDrawable)
                .onClickLink(getResources().getString(R.string.iconpack_author_playstore))
                .build());

//        Log.e("asdf size 2", "" + homeCards.size());
//        ArrayList<HomeCard> homeCards = new ArrayList<>();

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        HomeListAdapter mAdapter = new HomeListAdapter(homeCards, context);
        mRecyclerView.setAdapter(mAdapter);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        showFAB();
        Utils.expandToolbar(getActivity());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ShowcaseActivity.fab.setVisibility(View.GONE);
        ShowcaseActivity.fab.hide();
    }

    private void showFAB() {
        if (themeMode) {
            modifyFABIcon();
        }
        ShowcaseActivity.fab.setVisibility(View.VISIBLE);
        ShowcaseActivity.fab.show();
        ShowcaseActivity.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (themeMode) {
                    if (cm || cyngn) {
                        new LauncherIntents(getActivity(), "Cmthemeengine");
                    } else if (rro) {
                        new LauncherIntents(getActivity(), "Layers");
                    } else {
                        new MaterialDialog.Builder(getActivity())
                                .title(R.string.NTED_title)
                                .content(R.string.NTED_message)
                                .show();
                    }
                } else {
                    ShowcaseActivity.drawerItemClick(ShowcaseActivity.applyIdentifier);
                    ShowcaseActivity.drawer.setSelection(ShowcaseActivity.applyIdentifier);
                }
            }
        });
    }

    private void setupIcons(Context context) {
        final int light = ContextCompat.getColor(context, R.color.drawable_tint_dark);
        final int dark = ContextCompat.getColor(context, R.color.drawable_tint_light);

        Drawable iconsDrawable = new IconicsDrawable(context)
                .icon(GoogleMaterial.Icon.gmd_android_alt)
                .color(ThemeUtils.darkTheme ? light : dark)
                .sizeDp(24);

        Drawable wallsDrawable = new IconicsDrawable(context)
                .icon(GoogleMaterial.Icon.gmd_collection_image)
                .color(ThemeUtils.darkTheme ? light : dark)
                .sizeDp(24);

        Drawable widgetsDrawable = new IconicsDrawable(context)
                .icon(GoogleMaterial.Icon.gmd_widgets)
                .color(ThemeUtils.darkTheme ? light : dark)
                .sizeDp(24);

        playStoreDrawable = new IconicsDrawable(context)
                .icon(GoogleMaterial.Icon.gmd_case_play)
                .color(ThemeUtils.darkTheme ? light : dark)
                .sizeDp(24);

        iconsIV.setImageDrawable(iconsDrawable);
        wallsIV.setImageDrawable(wallsDrawable);
        widgetsIV.setImageDrawable(widgetsDrawable);
    }

    private void modifyFABIcon() {
        cm = Utils.isAppInstalled(context, "org.cyanogenmod.theme.chooser");
        cyngn = Utils.isAppInstalled(context, "com.cyngn.theme.chooser");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            rro = Utils.isAppInstalled(context, "com.lovejoy777.rroandlayersmanager");
        } else {
            rro = false; //don't enable rro before lollipop, it didn't exist before that
        }

        if (cm || cyngn) {
            ShowcaseActivity.fab.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_apply_cm));
        } else if (rro) {
            ShowcaseActivity.fab.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_apply_layers));
        } else {
            ShowcaseActivity.fab.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_question));
        }
    }


}
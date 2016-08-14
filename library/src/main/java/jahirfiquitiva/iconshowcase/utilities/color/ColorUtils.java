/*
 * Copyright 2015 Google Inc.
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

package jahirfiquitiva.iconshowcase.utilities.color;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jahirfiquitiva.iconshowcase.R;
import jahirfiquitiva.iconshowcase.utilities.ThemeUtils;
import jahirfiquitiva.iconshowcase.utilities.Utils;


public class ColorUtils {

    @ColorInt
    private static int blendColors(@ColorInt int color1,
                                   @ColorInt int color2,
                                   @FloatRange(from = 0f, to = 1f) float ratio) {
        final float inverseRatio = 1f - ratio;
        float a = (Color.alpha(color1) * inverseRatio) + (Color.alpha(color2) * ratio);
        float r = (Color.red(color1) * inverseRatio) + (Color.red(color2) * ratio);
        float g = (Color.green(color1) * inverseRatio) + (Color.green(color2) * ratio);
        float b = (Color.blue(color1) * inverseRatio) + (Color.blue(color2) * ratio);
        return Color.argb((int) a, (int) r, (int) g, (int) b);
    }

    @SuppressWarnings("SameParameterValue")
    @ColorInt
    public static int adjustAlpha(@ColorInt int color, @FloatRange(from = 0.0, to = 1.0) float factor) {
        float a = Color.alpha(color) * factor;
        float r = Color.red(color);
        float g = Color.green(color);
        float b = Color.blue(color);
        return Color.argb((int) a, (int) r, (int) g, (int) b);
    }

    @ColorInt
    public static int darkenColor(Context context, @ColorInt int color) {
        return context.getResources().getBoolean(R.bool.darker_launcher_bg) ?
                darkenColor(color) : color;
    }

    @ColorInt
    private static int darkenColor(@ColorInt int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        color = Color.HSVToColor(hsv);

        return color;
    }

    @ColorInt
    private static int lightenColor(@ColorInt int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] /= 0.7f;
        color = Color.HSVToColor(hsv);

        return color;
    }

    public static Drawable getTintedIcon(@NonNull Context context, @DrawableRes int drawable, @ColorInt int color) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return getTintedIcon(ContextCompat.getDrawable(context, drawable), color);
            } else {
                Drawable icon = VectorDrawableCompat.create(context.getResources(), drawable, null);
                return getTintedIcon(icon, color);
            }
        } catch (Resources.NotFoundException ex) {
            return getTintedIcon(ContextCompat.getDrawable(context, R.drawable.iconshowcase_logo), color);
        }
    }

    @CheckResult
    @Nullable
    public static Drawable getTintedIcon(Drawable drawable, int color) {
        if (drawable != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (drawable instanceof VectorDrawable) {
                    drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
                }
                drawable = DrawableCompat.wrap(drawable.mutate());
            } else {
                drawable = DrawableCompat.wrap(drawable);
            }

            DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
            DrawableCompat.setTint(drawable, color);
            return drawable;
        } else {
            return null;
        }
    }

    public static boolean isLightColor(Bitmap bitmap) {
        Palette palette = Palette.from(bitmap).generate();
        if (palette.getSwatches().size() > 0) {
            return isLightColor(palette);
        }
        return isLightColor(palette);
    }

    private static boolean isLightColor(Palette palette) {
        return isLightColor(ColorUtils.getProminentSwatch(palette).getRgb());
    }

    private static boolean isLightColor(@ColorInt int color) {
        if (color == Color.BLACK) return false;
        else if (color == Color.WHITE || color == Color.TRANSPARENT) return true;
        final double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness < 0.45;
    }

    private static boolean checkDarknessOfColor(@ColorInt int color, float darkn) {
        if (color == Color.BLACK) return false;
        else if (color == Color.WHITE || color == Color.TRANSPARENT) return true;
        final double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkn >= 0.51 ? darkness > darkn : darkness < darkn;
    }

    public static void setupToolbarIconsAndTextsColors(final Context context, AppBarLayout appbar,
                                                       final Toolbar toolbar) {

        final int iconsColor = ThemeUtils.darkTheme ?
                ContextCompat.getColor(context, R.color.toolbar_text_dark) :
                ContextCompat.getColor(context, R.color.toolbar_text_light);

        final int defaultIconsColor = ContextCompat.getColor(context, android.R.color.white);

        if (appbar != null) {
            appbar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                @SuppressWarnings("ResourceAsColor")
                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    double ratio = Utils.round(((double) (verticalOffset * -1) / 255.0), 1);
                    if (ratio > 1) {
                        ratio = 1;
                    } else if (ratio < 0) {
                        ratio = 0;
                    }
                    int paletteColor = ColorUtils.blendColors(defaultIconsColor, iconsColor, (float) ratio);
                    if (toolbar != null) {
                        // Collapsed offset = -352
                        ToolbarColorizer.colorizeToolbar(toolbar, paletteColor);
                    }
                }
            });
        }
    }

    public static Palette.Swatch getProminentSwatch(Bitmap bitmap) {
        Palette palette = Palette.from(bitmap).generate();
        return getProminentSwatch(palette);
    }

    private static Palette.Swatch getProminentSwatch(Palette palette) {
        if (palette == null) return null;
        List<Palette.Swatch> swatches = getSwatchesList(palette);
        return Collections.max(swatches,
                new Comparator<Palette.Swatch>() {
                    @Override
                    public int compare(Palette.Swatch opt1, Palette.Swatch opt2) {
                        int a = opt1 == null ? 0 : opt1.getPopulation();
                        int b = opt2 == null ? 0 : opt2.getPopulation();
                        return a - b;
                    }
                });
    }

    private static List<Palette.Swatch> getSwatchesList(Palette palette) {
        List<Palette.Swatch> swatches = new ArrayList<>();

        Palette.Swatch vib = palette.getVibrantSwatch();
        Palette.Swatch vibLight = palette.getLightVibrantSwatch();
        Palette.Swatch vibDark = palette.getDarkVibrantSwatch();

        Palette.Swatch muted = palette.getMutedSwatch();
        Palette.Swatch mutedLight = palette.getLightMutedSwatch();
        Palette.Swatch mutedDark = palette.getDarkMutedSwatch();

        swatches.add(vib);
        swatches.add(vibLight);
        swatches.add(vibDark);
        swatches.add(muted);
        swatches.add(mutedLight);
        swatches.add(mutedDark);

        return swatches;
    }

    public static int getColorFromIcon(Drawable icon, final Context context) {
        Palette palette = Palette.from(Utils.drawableToBitmap(icon)).generate();
        int resultColor = getBetterColor(palette.getVibrantColor(0));
        if (resultColor == 0) {
            resultColor = getBetterColor(palette.getMutedColor(0));
        }
        if (resultColor == 0) {
            resultColor = ContextCompat.getColor(context, ThemeUtils.darkTheme ?
                    R.color.dark_theme_accent : R.color.light_theme_accent);
        }
        return resultColor;
    }

    private static int getBetterColor(@ColorInt int color) {
        if (ThemeUtils.darkTheme) {
            return checkDarknessOfColor(color, 0.8f) ? lightenColor(color) : color;
        } else {
            return checkDarknessOfColor(color, 0.2f) ? darkenColor(color) : color;
        }
    }

    public static int getBetterProgressBarColor(@ColorInt int color, Context context) {
        int betterColor = checkDarknessOfColor(color, 0.8f) ? lightenColor(color) : color;

        betterColor = checkDarknessOfColor(betterColor, 0.2f) ? darkenColor(betterColor) : betterColor;

        if (betterColor == 0) {
            betterColor = ContextCompat.getColor(context, ThemeUtils.darkTheme ?
                    R.color.dark_theme_accent : R.color.light_theme_accent);
        }

        return betterColor;
    }

}
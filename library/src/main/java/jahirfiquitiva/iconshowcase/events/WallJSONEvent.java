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

package jahirfiquitiva.iconshowcase.events;

import java.util.ArrayList;

import jahirfiquitiva.iconshowcase.models.WallpaperItem;

/**
 * Created by Allan Wang on 2016-09-06.
 */
public class WallJSONEvent {

    public final ArrayList<WallpaperItem> walls;

    public WallJSONEvent(ArrayList<WallpaperItem> list) {
        walls = list;
    }
}

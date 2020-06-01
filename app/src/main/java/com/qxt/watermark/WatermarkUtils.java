/*
 * Copyright 2020 Tyler Qiu.
 * YUV420 to RGBA open source project.
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

package com.qxt.watermark;

/**
 * @author Tyler Qiu
 * @date 2020/05/12
 */
public final class WatermarkUtils {
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("watermark");
    }


    /**
     *
     * @param image src image byte array
     * @param imageWidth src image width
     * @param imageHeight src image height
     * @param watermark watermark image byte array
     * @param watermarkWidth watermark image width
     * @param watermarkHeight watermark image height
     * @param x watermark position. x >= 0 && x <= imageWidth - watermarkWidth
     * @param y watermark position. y >= 0 && y <= imageHeight - watermarkHeight
     */
    public static native void add(byte[] image, int imageWidth, int imageHeight,
                                  byte[] watermark, int watermarkWidth, int watermarkHeight,
                                  int x, int y);
}

/*
 * Copyright (C) 2021 LineageOS
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

package org.lineageos.cutoutringservice

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.os.RemoteException
import android.util.Log
import android.view.*
import android.widget.ImageView
import java.lang.RuntimeException

import org.lineageos.cutoutringservice.R

class CutoutRingService : BroadcastReceiver() {
    private val mRingParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.TYPE_SECURE_SYSTEM_OVERLAY,
        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
        PixelFormat.RGBA_8888
    )
    private var mRingView: ImageView? = null
    private var mWindowManager: WindowManager? = null
    private var mVisibility = View.VISIBLE
    private var mCameraActive = false
    private var mFixedRotationInProgress = false
    override fun onReceive(context: Context, intent: Intent) {
        val mCameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        mWindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val mWindowManagerService = WindowManagerGlobal.getWindowManagerService()
        mRingParams.height = RING_SIZE
        mRingParams.width = RING_SIZE
        mRingParams.title = "CutoutRing"
        mRingParams.privateFlags =
            mRingParams.privateFlags or (WindowManager.LayoutParams.PRIVATE_FLAG_IS_ROUNDED_CORNERS_OVERLAY
                    or WindowManager.LayoutParams.PRIVATE_FLAG_NO_MOVE_ANIMATION)
        mRingParams.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
        mRingParams.fitInsetsTypes = 0
        mRingParams.fitInsetsSides = 0
        adjustParamsToRotation()
        mRingView = ImageView(context)
        mRingView!!.setImageResource(R.drawable.ring)
        mWindowManager!!.addView(mRingView, mRingParams)
        val mDisplayContainerListener: IDisplayWindowListener =
            object : IDisplayWindowListener.Stub() {
                override fun onDisplayAdded(displayId: Int) {}
                override fun onDisplayConfigurationChanged(
                    displayId: Int,
                    newConfig: Configuration
                ) {
                    onRotationChanged()
                }

                override fun onDisplayRemoved(displayId: Int) {}
                override fun onFixedRotationStarted(displayId: Int, newRotation: Int) {
                    mFixedRotationInProgress = true
                    Handler.getMain().postAtFrontOfQueue { setVisibility(HIDDEN) }
                }

                override fun onFixedRotationFinished(displayId: Int) {
                    onRotationChanged()
                    Handler.getMain()
                        .post { setVisibility(if (mCameraActive) SHOWN else SHOWN_SMALL) }
                    mFixedRotationInProgress = false
                }
            }
        try {
            mWindowManagerService.registerDisplayWindowListener(mDisplayContainerListener)
        } catch (e: RemoteException) {
            throw RuntimeException("Could not register the display listener")
        }
        val camCallback: CameraManager.AvailabilityCallback =
            object : CameraManager.AvailabilityCallback() {
                override fun onCameraAvailable(cameraId: String) {
                    if (cameraId == FRONT_CAMERA_ID) {
                        mCameraActive = false
                        if (!mFixedRotationInProgress) {
                            setVisibility(SHOWN_SMALL)
                        }
                    }
                }

                override fun onCameraUnavailable(cameraId: String) {
                    if (cameraId == FRONT_CAMERA_ID) {
                        mCameraActive = true
                        if (!mFixedRotationInProgress) {
                            setVisibility(SHOWN)
                        }
                    }
                }
            }
        mCameraManager.registerAvailabilityCallback(camCallback, Handler.getMain())
    }

    private fun adjustParamsToRotation() {
        val rotation = rotation
        var gravity = Gravity.CENTER
        var x = X_OFFSET
        var y = Y_OFFSET
        mVisibility = View.VISIBLE
        Log.i(TAG, String.format("Adjusting to rotation %d", rotation))
        when (rotation) {
            Surface.ROTATION_0 -> gravity = gravity or Gravity.TOP
            Surface.ROTATION_90 -> {
                gravity = Gravity.CENTER_VERTICAL or Gravity.START
                x = X_OFFSET_HORIZONTAL
                y = Y_OFFSET_HORIZONTAL
            }
            Surface.ROTATION_180 -> gravity = gravity or Gravity.BOTTOM
            Surface.ROTATION_270 -> {
                gravity = Gravity.CENTER_VERTICAL or Gravity.END
                x = X_OFFSET_HORIZONTAL
                y = Y_OFFSET_HORIZONTAL
            }
            else -> {
                Log.w(TAG, String.format("Unknown rotation %d, hiding the view", rotation))
                mVisibility = View.INVISIBLE
            }
        }
        mRingParams.x = x
        mRingParams.y = y
        mRingParams.gravity = gravity
    }

    private fun setVisibility(visibility: Int) {
        mRingView!!.visibility = mVisibility
        var scale = 0.0f
        when (visibility) {
            HIDDEN -> {
                scale = 0.0f
                mRingView!!.visibility = View.INVISIBLE
            }
            SHOWN_SMALL -> scale = SCALE_CAMERA_INACTIVE
            SHOWN -> scale = 1.0f
        }
        val sizeAnimation = AnimatorSet()
        val scaleX = ObjectAnimator.ofFloat(mRingView, "scaleX", scale)
        val scaleY = ObjectAnimator.ofFloat(mRingView, "scaleY", scale)
        scaleX.duration = ANIMATION_MS.toLong()
        scaleY.duration = ANIMATION_MS.toLong()
        sizeAnimation.play(scaleX).with(scaleY)
        sizeAnimation.start()
    }

    private val rotation: Int
        get() = mWindowManager!!.defaultDisplay.rotation

    private fun onRotationChanged() {
        adjustParamsToRotation()
        Handler.getMain()
            .postAtFrontOfQueue { mWindowManager!!.updateViewLayout(mRingView, mRingParams) }
    }

    companion object {
        private const val TAG = "CutoutRingService"
        private const val FRONT_CAMERA_ID = "1"
        private const val RING_SIZE = 78
        private const val X_OFFSET = 0
        private const val X_OFFSET_HORIZONTAL = 13
        private const val Y_OFFSET = 13
        private const val Y_OFFSET_HORIZONTAL = 0
        private const val ANIMATION_MS = 1000
        private const val HIDDEN = 0
        private const val SHOWN_SMALL = 1
        private const val SHOWN = 2
        private const val SCALE_CAMERA_INACTIVE = 0.6f
    }
}

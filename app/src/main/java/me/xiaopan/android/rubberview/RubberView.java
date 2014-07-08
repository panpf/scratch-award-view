/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.xiaopan.android.rubberview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class RubberView extends View {
    private Paint drawPaint;
    private Bitmap trackBitmap; // 轨迹图片，一会儿要在此图片上画滑动轨迹，然后通过Xfermode技术同遮罩层图片融合就能实现涂抹的效果
    private Canvas trackBitmapCanvas;
    private Xfermode xfermode;
    private Paint linePaint;    // 触摸轨迹线画笔

    private int defaultMaskColor = Color.LTGRAY;
    private Bitmap maskBitmap;  // 遮罩层图片
    private Rect dstRect;
    private Rect srcRect;

    private float downX;
    private float downY;
    private float moveX;
    private float moveY;

    private boolean moved;

    private View hintView;
    private Rect hintViewGlobalVisibleRect;
    private OnAcrossHintViewListener onAcrossHintViewListener;
    private boolean allowAcrossCallback;

    public RubberView(Context context) {
        this(context, null, 0);
    }

    public RubberView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RubberView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        drawPaint = new Paint();
        drawPaint.setColor(Color.BLACK);
        drawPaint.setFilterBitmap(true);

        linePaint = new Paint();
        linePaint.setColor(Color.BLACK);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeJoin(Paint.Join.ROUND); // 前圆角
        linePaint.setStrokeCap(Paint.Cap.ROUND); // 后圆角
        linePaint.setStrokeWidth(30); // 笔宽

        xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int layerCount = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG);
        canvas.drawBitmap(trackBitmap, getPaddingLeft(), getPaddingTop(), drawPaint);
        drawPaint.setXfermode(xfermode);
        canvas.drawBitmap(maskBitmap, srcRect, dstRect, drawPaint);
        drawPaint.setXfermode(null);
        if(!isInEditMode()){
            canvas.restoreToCount(layerCount);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        int width = right-left;
        int height = bottom-top;

        // 初始化轨迹图片，稍后将在此图上绘制触摸轨迹
        if(trackBitmap == null){
            trackBitmap = Bitmap.createBitmap(width-getPaddingRight()-getPaddingLeft(), height-getPaddingBottom()-getPaddingTop(), Bitmap.Config.ARGB_8888);
            trackBitmapCanvas = new Canvas(trackBitmap);
        // 如果已经创建了但是宽高有变化就重新创建
        }else if(trackBitmap.getWidth() != width-getPaddingRight()-getPaddingLeft() || trackBitmap.getHeight() != height-getPaddingBottom()-getPaddingTop()){
            trackBitmap.recycle();
            trackBitmap = Bitmap.createBitmap(width-getPaddingRight()-getPaddingLeft(), height-getPaddingBottom()-getPaddingTop(), Bitmap.Config.ARGB_8888);
            trackBitmapCanvas.setBitmap(trackBitmap);
        }

        // 初始化遮罩图片
        if(maskBitmap == null){
            maskBitmap = Bitmap.createBitmap(width-getPaddingRight()-getPaddingLeft(), height-getPaddingBottom()-getPaddingTop(), Bitmap.Config.ARGB_8888);
            new Canvas(maskBitmap).drawColor(defaultMaskColor);
        }

        // 初始化dst位置
        if(dstRect == null){
            dstRect = new Rect(getPaddingLeft(), getPaddingTop(), width-getPaddingRight(), height-getPaddingBottom());
        }else{
            dstRect.set(getPaddingLeft(), getPaddingTop(), width-getPaddingRight(), height-getPaddingBottom());
        }

        srcRect = computeSrcRect(new Point(maskBitmap.getWidth(), maskBitmap.getHeight()), new Point(dstRect.width(), dstRect.height()), ImageView.ScaleType.CENTER_CROP);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case  MotionEvent.ACTION_DOWN :
                downX = event.getX();
                downY = event.getY();
                moved = false;
                allowAcrossCallback = hintView != null && onAcrossHintViewListener != null;
                break;
            case MotionEvent.ACTION_MOVE:
                moved = true;
                moveX = event.getX();
                moveY = event.getY();
                trackBitmapCanvas.drawLine(downX, downY, moveX, moveY, linePaint);
                downX = moveX;
                downY = moveY;
                invalidate();

                if(allowAcrossCallback){
                    if(hintViewGlobalVisibleRect == null){
                        hintViewGlobalVisibleRect = new Rect();
                        hintView.getGlobalVisibleRect(hintViewGlobalVisibleRect);
                    }
                    if(hintViewGlobalVisibleRect.contains((int)event.getRawX(), (int)event.getRawY())){
                        onAcrossHintViewListener.onAcrossHintView(hintView);
                        allowAcrossCallback = false;
                    }
                }

                if(getParent() != null){
                    getParent().requestDisallowInterceptTouchEvent(true);
                }

                break;
        }
        if(!moved){
            super.onTouchEvent(event);
        }
        return true;
    }

    /**
     * 设置遮罩图片
     * @param maskBitmap 遮罩图片
     */
    public void setMaskImage(Bitmap maskBitmap) {
        this.maskBitmap = maskBitmap;
        requestLayout();
    }

    /**
     * 设置遮罩图片
     * @param color 遮罩图片的颜色，稍后将使用此颜色创建一张图片
     */
    public void setMaskImage(int color) {
        this.defaultMaskColor = color;
        if(maskBitmap != null){
            if(!maskBitmap.isRecycled()){
                maskBitmap.recycle();
            }
            maskBitmap = null;
        }
        requestLayout();
    }

    /**
     * 设置画笔的宽度
     * @param strokeWidth 画笔的宽度
     */
    public void setStrokeWidth(int strokeWidth) {
        this.linePaint.setStrokeWidth(strokeWidth);
    }

    /**
     * 激活监听划过提示视图的功能
     * @param hintView 提示视图，当用户划过此视图的时候就会回调监听器，一般来说此视图应该是隐藏在RubberView之下的中奖提示视图
     * @param onAcrossHintViewListener 当用户划过提示视图的时候就会回调此监听器
     */
    public void enableAcrossMonitor(View hintView, OnAcrossHintViewListener onAcrossHintViewListener){
        this.hintView = hintView;
        this.onAcrossHintViewListener = onAcrossHintViewListener;
    }

    public static Rect computeSrcRect(Point sourceSize, Point targetSize, ImageView.ScaleType scaleType){
        if(scaleType == ImageView.ScaleType.CENTER_INSIDE || scaleType == ImageView.ScaleType.MATRIX || scaleType == ImageView.ScaleType.FIT_XY){
            return new Rect(0, 0, sourceSize.x, sourceSize.y);
        }else{
            float scale;
            if(Math.abs(sourceSize.x - targetSize.x) < Math.abs(sourceSize.y - targetSize.y)){
                scale = (float) sourceSize.x/targetSize.x;
                if((int)(targetSize.y*scale) > sourceSize.y){
                    scale = (float) sourceSize.y/targetSize.y;
                }
            }else{
                scale = (float) sourceSize.y/targetSize.y;
                if((int)(targetSize.x*scale) > sourceSize.x){
                    scale = (float) sourceSize.x/targetSize.x;
                }
            }
            int srcLeft;
            int srcTop;
            int srcWidth = (int)(targetSize.x*scale);
            int srcHeight = (int)(targetSize.y*scale);
            if (scaleType == ImageView.ScaleType.FIT_START) {
                srcLeft = 0;
                srcTop = 0;
            } else if (scaleType == ImageView.ScaleType.FIT_END) {
                if(sourceSize.x > sourceSize.y){
                    srcLeft = sourceSize.x - srcWidth;
                    srcTop = 0;
                }else{
                    srcLeft = 0;
                    srcTop = sourceSize.y - srcHeight;
                }
            } else {
                if(sourceSize.x > sourceSize.y){
                    srcLeft = (sourceSize.x - srcWidth)/2;
                    srcTop = 0;
                }else{
                    srcLeft = 0;
                    srcTop = (sourceSize.y - srcHeight)/2;
                }
            }
            return new Rect(srcLeft, srcTop, srcLeft+srcWidth, srcTop+srcHeight);
        }
    }

    public interface OnAcrossHintViewListener{
        public void onAcrossHintView(View hintView);
    }
}

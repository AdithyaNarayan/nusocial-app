package com.teamnusocial.nusocial.ui.buddymatch

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.squareup.picasso.Transformation

class MaskTransformation(
    private val context: Context,
    @DrawableRes private val maskID: Int
) : Transformation {

    override fun key(): String {
        return "mask"
    }

    override fun transform(source: Bitmap): Bitmap {
        val width = source.width
        val height = source.height

        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        //Draw a masked, scaled down bitmap of the photo on top
        val maskingPaint = Paint()
        maskingPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        val maskDrawable = ContextCompat.getDrawable(context, maskID)!!
        maskDrawable.setBounds(0, 0, width , height)

        val overlayBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val overlayCanvas = Canvas(overlayBitmap)
        maskDrawable.draw(overlayCanvas)

        val pictureBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val pictureCanvas = Canvas(pictureBitmap)

        val sourceDrawable = BitmapDrawable(context.resources, source)
        sourceDrawable.setBounds(0, 0, width , height)
        pictureCanvas.drawBitmap(
            sourceDrawable.bitmap,
            null,
            Rect(0, 0, width , height),
            Paint()
        )

        overlayCanvas.drawBitmap(pictureBitmap, 0F, 0F, maskingPaint)

        canvas.drawBitmap(overlayBitmap, 0f, 0f, Paint())

        source.recycle()

        return output
    }
}
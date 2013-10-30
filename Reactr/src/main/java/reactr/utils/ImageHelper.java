package reactr.utils;

/**
 * Created by Kykmyrna on 20.09.13.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.util.TypedValue;
//вспомогательный класс для модификации изображени (округления)
public class ImageHelper {
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }


    //borderDips
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap_in, int color, Context context) {

        int h = bitmap_in.getHeight();
        int w = bitmap_in.getWidth();
        Bitmap bitmap = null;
        int dif = 0;
         if(h<w){
             dif=w-h;
             bitmap=getCroppedBitmap(bitmap_in,dif/2, 0, h, w-dif/2);
         }
        if(h>w){
            dif=h-w;
            bitmap=getCroppedBitmap(bitmap_in, 0, dif/2, h-dif/2, w);
        }
        if(w==h){
            bitmap=bitmap_in;
        }


        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int borderSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, bitmap.getWidth()/22,
                context.getResources().getDisplayMetrics());


        final int cornerSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) 1500,
                context.getResources().getDisplayMetrics());
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        // prepare canvas for transfer
        paint.setAntiAlias(true);
        paint.setColor(0xFFFFFFFF);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);

        // draw bitmap
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        // draw border
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((float) borderSizePx);
        canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);

        return output;
    }


    public static Bitmap getCroppedBitmap(Bitmap source, int left, int top, int bottom, int right) {

        if (source == null) {
            return null;
        }

        final int sourceWidth = source.getWidth();
        final int sourceHeight = source.getHeight();
        final int outputWidth =  right - left;
        final int outputHeight = bottom - top;

        if (sourceWidth < outputWidth || sourceHeight < outputHeight) {
            throw new IllegalArgumentException("Destination size larget than source size. Cant crop that way.");
        }

        final Bitmap output = Bitmap.createBitmap(outputWidth, outputHeight, Bitmap.Config.ARGB_8888);

        final Rect dest = new Rect(0, 0, outputWidth, outputHeight);
        final Rect src = new Rect(left, top, right, bottom);

        Canvas canvas = new Canvas(output);
        canvas.drawBitmap(source, src, dest, new Paint());

        return output;
    }
}
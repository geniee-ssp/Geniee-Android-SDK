package jp.co.geniee.samples.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ImageGetTask extends AsyncTask<Void, Void, Bitmap> {
    private String url;
    private final ThreadLocal<ImageView> imageView = new ThreadLocal<>();
    private String tag = "";

    public ImageGetTask(ImageView imageView, String url) {
        this.imageView.set(imageView);
        this.url = url;
        if (imageView.getTag() != null) {
            tag = imageView.getTag().toString();
        }
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        Bitmap bitmap;
        try {
            URL imageUrl = new URL(url);
            InputStream input;
            input = imageUrl.openStream();
            bitmap = BitmapFactory.decodeStream(input);
            input.close();
            return bitmap;
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (tag.equals(imageView.get().getTag())) {
            imageView.get().setImageBitmap(bitmap);
        }
    }
}

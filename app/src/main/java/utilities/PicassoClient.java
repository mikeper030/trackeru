package utilities;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.ultimatetoolsil.trackeru.R;

/**
 * Created by mike on 14 Dec 2017.
 */

public class PicassoClient {
    public  static  void DownloadGroupImage(Context c, String url, ImageView img, int degrees)
    {
        if (url!=null && url.length()>0)
        {
            Log.d("picasso","downloading...");
            Picasso.with(c).load(url).placeholder(R.drawable.group).rotate(degrees).fit().into(img);

        }else
        {
           Picasso.with(c).load(R.drawable.group).into(img);
        }
    }
    public  static  void DownloadSingleImage(Context c, String url, ImageView img, int degrees)
    {
        if (url!=null && url.length()>0)
        {
            Log.d("picasso","downloading...");
            Picasso.with(c).load(url).placeholder(R.drawable.profile).rotate(degrees).fit().into(img);

        }else
        {
            Picasso.with(c).load(R.drawable.profile).into(img);
        }
    }


}

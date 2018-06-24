package adapters;

/**
 * Created by mike on 17 Dec 2017.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.ultimatetoolsil.trackeru.R;

import java.util.ArrayList;

import models.User;
import utilities.PicassoClient;

/**
 * Created by mike on 14 Dec 2017.
 */

public class CustomAdapter2 extends BaseAdapter {
    Context c;
    ArrayList<User> circlesinfo;
    LayoutInflater inflater;


    public CustomAdapter2(Context c, ArrayList<User> circlesinfo) {
        this.c = c;
        this.circlesinfo = circlesinfo;
    }





    @Override
    public int getCount() {
        return circlesinfo.size();
    }

    @Override
    public Object getItem(int i) {
        return circlesinfo.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertview, ViewGroup viewGroup) {

        if (inflater== null)
        {
            inflater=(LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        } if(convertview==null)
        {
            convertview= inflater.inflate(R.layout.list_group,viewGroup,false);

        }




        MyHolder2 holder= new MyHolder2(convertview);
        holder.status.setText(R.string.status);
        holder.username.setText(circlesinfo.get(i).getUsername());
//        Log.d("pic",circlesinfo.get(i).getPicLink());

        PicassoClient.DownloadGroupImage(c, circlesinfo.get(i).getPicLink(), holder.img2,circlesinfo.get(i).getRotation());






        return convertview;
    }
}



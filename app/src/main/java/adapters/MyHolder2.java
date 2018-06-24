package adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.ultimatetoolsil.trackeru.R;

/**
 * Created by mike on 17 Dec 2017.
 */

public class MyHolder2 {


    TextView status,username;
    ImageView img2;

    public MyHolder2(View itemView) {

        status =(TextView) itemView.findViewById(R.id.online);
        username= (TextView) itemView.findViewById(R.id.usename);
        img2=(ImageView) itemView.findViewById(R.id.groupprofile);


    }
}


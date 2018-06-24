package adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.ultimatetoolsil.trackeru.R;

/**
 * Created by mike on 14 Dec 2017.
 */

public class MyHolder1 {


         TextView nameTxt;
         ImageView img,options;
         ImageView ex;
        public MyHolder1(View itemView) {


            nameTxt= (TextView) itemView.findViewById(R.id.nameTxt);
            img=(ImageView) itemView.findViewById(R.id.dogimage);
            ex=(ImageView)itemView.findViewById(R.id.imageView2);
            options=(ImageView) itemView.findViewById(R.id.menu3d);


    }
}

package adapters;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import org.ultimatetoolsil.trackeru.R;

/**
 * Created by mike on 24 Dec 2017.
 */

public class MyHolder3 {
    TextView groupname;
    CheckBox check;

    public MyHolder3(View itemView) {

        groupname =(TextView) itemView.findViewById(R.id.groupname);
        check= (CheckBox) itemView.findViewById(R.id.check);



    }
}

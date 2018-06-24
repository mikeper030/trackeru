package adapters;

/**
 * Created by mike on 14 Dec 2017.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.PopupMenu;
import android.widget.Toast;

import org.ultimatetoolsil.trackeru.Createcircle;
import org.ultimatetoolsil.trackeru.MainActivity;
import org.ultimatetoolsil.trackeru.R;

import java.util.ArrayList;

import models.usercircleModel;
import utilities.PicassoClient;
import utilities.utils;

public class CustomAdapter1 extends BaseAdapter {
    Context c;
    public static int position = -1;
    public static String key=null;
    ArrayList<usercircleModel> circles;
    LayoutInflater inflater;


    public CustomAdapter1(Context c, ArrayList<usercircleModel> circles) {
        this.c = c;
        this.circles = circles;
    }


    @Override
    public int getCount() {
        return circles.size();
    }

    @Override
    public Object getItem(int i) {
        return circles.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertview, ViewGroup viewGroup) {

        if (inflater == null) {
            inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertview == null) {
            convertview = inflater.inflate(R.layout.list_item, viewGroup, false);

        }

        Animation a = AnimationUtils.loadAnimation(c, R.anim.rotateltr);
        Animation b = AnimationUtils.loadAnimation(c, R.anim.rotatertl);

        MyHolder1 holder = new MyHolder1(convertview);
        holder.nameTxt.setText(circles.get(i).getName());

        PicassoClient.DownloadGroupImage(c, circles.get(i).getImgUrl(), holder.img, circles.get(i).getImgRotation());
        Log.d("d",String.valueOf(circles.get(i).getImgRotation()));



        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.getContext().startActivity(new Intent(c, Createcircle.class).putExtra("title", circles.get(i).getKey())
                        .putExtra("name", circles.get(i).getName().toString()).putExtra("pic", "start"));
            }
        });
        holder.options.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(final View view) {

                position = i;
                PopupMenu menu = new PopupMenu(view.getContext(), view);
                menu.inflate(R.menu.usercircleitem);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.addfriend:
                                // open friends fragment

                                view.getContext().startActivity(new Intent(c, Createcircle.class).putExtra("title",  circles.get(i).getKey())
                                        .putExtra("name", circles.get(i).getName().toString()));


                                return true;
                            case R.id.deletecircle:
                                final AlertDialog.Builder adb = new AlertDialog.Builder(
                                        c);

                                // Setting Dialog Title
                                adb.setTitle(R.string.warning);

                                // Setting Dialog Message
                                adb.setMessage(R.string.change);

                                // Setting Icon to Dialog
                                // alertDialog.setIcon(R.drawable.);

                                // Setting OK Button
                                adb.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        MainActivity.dbr.child("users").child(MainActivity.userid).child("circles").child("listdata").child(circles.get(i).getKey().toString()).removeValue();
                                        MainActivity.dbr.child("public").child("circles").child(circles.get(i).getKey()).removeValue();
                                        // Write your code here to execute after dialog closed
                                        Toast.makeText(c, R.string.deleted, Toast.LENGTH_SHORT).show();
                                    }
                                });
                                adb.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                // Showing Alert Message
                                adb.show();


                                return true;
                        }


                        return false;
                    }
                });
                menu.show();

            }
        });


        //   Log.d("url",circlesinfo.get(i).getImgUrl().toString());


        if (utils.isRTL()) {

            holder.ex.setRotation(90);
            holder.ex.startAnimation(b);
            Log.d("ff", "rtl");
            // The view has RTL layout
        } else {
            holder.ex.startAnimation(a);
            Log.d("ff", "ltr");
        }


        return convertview;

    }

}
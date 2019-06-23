package yaseerfarah22.com.follow_me3;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by DELL on 6/19/2018.
 */

public class slider_adapter extends PagerAdapter{

    Context context;
    LayoutInflater layoutInflater;


    public slider_adapter(Context context){
        this.context=context;
    }


    public int[] slider_image={
            R.mipmap.search,
            R.mipmap.location2,
            R.mipmap.road


    };
    public String[] slider_title={
            "Search",
            "Destination",
            "Route"

    };
    public String[]slider_text={

            "You can search for any place in Egypt with powerful search engine with auto complete address ",
            "We have a Multi Destinations and can reach to 10,000 Destinations that you can route between it  ",
            "you can choose the fastest way to arrive to destination or the shortest way"
    };



    @Override
    public int getCount() {
        return slider_image.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==(LinearLayout)object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.slide,container,false);

        ImageView imageView=(ImageView)view.findViewById(R.id.imageView);
        TextView title_v=(TextView)view.findViewById(R.id.textView2);
        TextView des_v=(TextView)view.findViewById(R.id.textView3);

        imageView.setImageResource(slider_image[position]);
        title_v.setText(slider_title[position]);
        des_v.setText(slider_text[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout)object);
    }
}

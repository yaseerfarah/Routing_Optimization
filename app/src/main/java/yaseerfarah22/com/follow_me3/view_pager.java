package yaseerfarah22.com.follow_me3;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class view_pager extends AppCompatActivity {

    ViewPager viewPager;
    LinearLayout linearLayout;
    slider_adapter slider_ad;
    TextView[] dots;
    Button finish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        viewPager=(ViewPager)findViewById(R.id.viewpager);
        linearLayout=(LinearLayout)findViewById(R.id.linearLayout);
        finish =(Button)findViewById(R.id.button);
        finish.setEnabled(false);
        finish.setVisibility(View.INVISIBLE);
        slider_ad=new slider_adapter(this);
        viewPager.setAdapter(slider_ad);
        adddots(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                adddots(position);
                if(position==dots.length-1){
                    finish.setEnabled(true);
                    finish.setVisibility(View.VISIBLE);
                }
                else {
                    finish.setEnabled(false);
                    finish.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view_pager.this,MainActivity.class);
                startActivity(intent);
            }
        });

    }


    public void adddots(int i){
        dots=new TextView[3];
        linearLayout.removeAllViews();
        for (int b=0;b<dots.length;b++){
            dots[b]=new TextView(this);
            dots[b].setText(Html.fromHtml("&#8226"));
            dots[b].setTextSize(35);
            dots[b].setTextColor(getResources().getColor(R.color.gray));
            linearLayout.addView(dots[b]);
        }

        dots[i].setTextColor(Color.WHITE);

    }
}

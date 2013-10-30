package com.eyepinch.reactr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class PhotoViewActivity extends FragmentActivity {

  static final String TAG = "myLogs";
  static final int PAGE_COUNT = 8;

  ViewPager pager;
  PagerAdapter pagerAdapter;
  ImageButton pageIndikator;
    private ImageButton skipBtn;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_flip_main);
    getActionBar().hide();
    pager = (ViewPager) findViewById(R.id.pager);
    pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
    pager.setAdapter(pagerAdapter);

      skipBtn = (ImageButton) findViewById(R.id.skipBtn);

      skipBtn.setOnClickListener(toStartClick);



    pager.setOnPageChangeListener(new OnPageChangeListener() {

      @Override
      public void onPageSelected(int position) {
        Log.d(TAG, "onPageSelected, position = " + position); 


                        pageIndikator=((ImageButton) findViewById(R.id.slide0));
                        if(position==0){
                            pageIndikator.setImageResource(R.drawable.dot_on);
                        }
                        else{
                            pageIndikator.setImageResource(R.drawable.dot_off);
                        }

                       pageIndikator=((ImageButton) findViewById(R.id.slide1));
                      if(position==1){
                          pageIndikator.setImageResource(R.drawable.dot_on);
                      }
                      else{
                          pageIndikator.setImageResource(R.drawable.dot_off);
                      }

        				pageIndikator=((ImageButton) findViewById(R.id.slide2));
        				if(position==2){
        				pageIndikator.setImageResource(R.drawable.dot_on);
        				}
        				else{
                            pageIndikator.setImageResource(R.drawable.dot_off);
        				}

        				pageIndikator=((ImageButton) findViewById(R.id.slide3));
        				if(position==3){
                            pageIndikator.setImageResource(R.drawable.dot_on);
        				}
        				else{
                            pageIndikator.setImageResource(R.drawable.dot_off);
        				}

        				pageIndikator=((ImageButton) findViewById(R.id.slide4));
        				if(position==4){
                            pageIndikator.setImageResource(R.drawable.dot_on);
        				}
        				else{
                            pageIndikator.setImageResource(R.drawable.dot_off);
        				}


        				pageIndikator=((ImageButton) findViewById(R.id.slide5));
        				if(position==5){
                            pageIndikator.setImageResource(R.drawable.dot_on);
        				}
        				else{
                            pageIndikator.setImageResource(R.drawable.dot_off);
        				}

                      pageIndikator=((ImageButton) findViewById(R.id.slide6));
                      if(position==6){
                          pageIndikator.setImageResource(R.drawable.dot_on);
                      }
                      else{
                          pageIndikator.setImageResource(R.drawable.dot_off);
                      }


                      pageIndikator=((ImageButton) findViewById(R.id.slide7));
                      if(position==7){
                          pageIndikator.setImageResource(R.drawable.dot_on);
                          pageIndikator=((ImageButton) findViewById(R.id.skipBtn));
                          pageIndikator.setImageResource(R.drawable.start_button);
                      }
                      else{
                          pageIndikator.setImageResource(R.drawable.dot_off);
                          pageIndikator=((ImageButton) findViewById(R.id.skipBtn));
                          pageIndikator.setImageResource(R.drawable.skip_button);
                      }


      }

      @Override
      public void onPageScrolled(int position, float positionOffset,
          int positionOffsetPixels) {
      }

      @Override
      public void onPageScrollStateChanged(int state) {
      }
    });
  }

  private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    public MyFragmentPagerAdapter(FragmentManager fm) {
      super(fm);
    }

    @Override
    public Fragment getItem(int position) {
      return PageFragment.newInstance(position);
    }

    @Override
    public int getCount() {
      return PAGE_COUNT;
    }
  }

    View.OnClickListener toStartClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(PhotoViewActivity.this, MainActivity.class));
        }
    };

}
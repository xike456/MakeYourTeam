package com.smile.makeyourteam.Activities;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.smile.makeyourteam.Fragments.ChatGroupFragment;
import com.smile.makeyourteam.Fragments.ChatManagerFragment;
import com.smile.makeyourteam.Fragments.HomeFragment;
import com.smile.makeyourteam.Fragments.SettingsFragment;
import com.smile.makeyourteam.Fragments.TaskManagerFragment;
import com.smile.makeyourteam.R;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        adapter = new MyAdapter(getSupportFragmentManager());

        viewPager.setAdapter(adapter);

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
                tabLayout.getTabAt(0).setIcon(R.drawable.ic_home_black_24dp);
                tabLayout.getTabAt(1).setIcon(R.drawable.ic_event_note_black_24dp);
                tabLayout.getTabAt(2).setIcon(R.drawable.ic_chat_black_24dp);
                tabLayout.getTabAt(3).setIcon(R.drawable.ic_settings_black_24dp);

            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getSupportActionBar().setTitle(adapter.getPageTitle(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    class MyAdapter extends FragmentPagerAdapter {

        Fragment fragments[] = {
                new HomeFragment(),
                new TaskManagerFragment(),
                new ChatManagerFragment(),
                new SettingsFragment()
        };

        String titles[] = { "Home", "Task", "Chat", "Settings" };


        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}

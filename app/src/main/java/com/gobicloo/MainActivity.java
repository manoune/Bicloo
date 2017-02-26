package com.gobicloo;

import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.gobicloo.object.Station;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private List<Station> stations;
    private MapsFragment mapsFragment;
    private StationListFragment stationListFragment;
    private BottomSheetBehavior mBottomSheetBehavior;
    private TextView nameTextView;
    private TextView availableBikesTextView;
    private TextView availableStandsTextView;
    private TextView statusTextView;
    private TextView bankingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        View bottomSheet = findViewById(R.id.bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        nameTextView = (TextView) findViewById(R.id.station);
        availableBikesTextView = (TextView) findViewById(R.id.availableStands);
        availableStandsTextView = (TextView) findViewById(R.id.availableBikes);
        statusTextView = (TextView) findViewById(R.id.status);
        bankingTextView = (TextView) findViewById(R.id.banking);
    }

    public BottomSheetBehavior getmBottomSheetBehavior() {
        return mBottomSheetBehavior;
    }

    public List<Station> getStations() {
        return stations;
    }

    public void setStations(List<Station> stations) {
        this.stations = stations;
    }

    public void setBottomSheetForStation(Station station) {
        nameTextView.setText(station.getNameForDisplay());
        availableBikesTextView.setText(String.valueOf(station.getAvailable_bikes()) + " Bicloo");
        availableStandsTextView.setText(String.valueOf(station.getAvailable_bike_stands()) + " places restantes");
        if(station.getStatus().equals("OPEN")) {
            statusTextView.setText("Ouvert");
        } else {
            statusTextView.setText("Fermé");
        }
        if(station.isBanking()) {
            bankingTextView.setText("Terminal de paiement");
        } else {
            bankingTextView.setText("Pas de terminal de paiement");
        }
    }
    private void setupViewPager(ViewPager viewPager) {
        mapsFragment = new MapsFragment();
        stationListFragment = new StationListFragment();

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(mapsFragment, "Carte");
        adapter.addFragment(stationListFragment, "Liste");
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);

        new DownloadTask(this, stationListFragment).execute();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        getmBottomSheetBehavior().setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public void onBackPressed() {
        /*String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
        if(tag.equals("LIST")) {
            viewPager.setCurrentItem(0);
        }*/
        // TODO à voir pour quand on fait back depuis le fragment 1 on revienne sur le 0

        super.onBackPressed();
    }

    public void locatePointOnMap(double lat, double longitude) {
        viewPager.setCurrentItem(0);
        mapsFragment.setLocation(lat, longitude);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String filter = "";
        switch (item.getItemId()) {
            case R.id.action_refresh:
                Toast.makeText(this, "TODO", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menuSort:
                return true;
            case R.id.menuSortAvailable:
                if(!item.isChecked()) {
                    filter = "BIKE_AVAILABLE";
                    item.setChecked(true);
                } else {
                    filter = "BIKE_ALL";
                    item.setChecked(false);
                }
                // TODO sur la carte
                // Filtre sur la vue liste
                stationListFragment.getAdapter().getFilter().filter(filter);
                return true;
            case R.id.menuSortOpen:
                if(!item.isChecked()) {
                    filter = "OPEN";
                    item.setChecked(true);
                } else {
                    filter = "BIKE_ALL";
                    item.setChecked(false);
                }
                // TODO sur la carte
                // Filtre sur la vue liste
                stationListFragment.getAdapter().getFilter().filter(filter);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}

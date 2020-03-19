package com.sop.cacapp.Fragments;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class MainFragmentTest {

    private MainFragment mMainFragment;

    @Before
    public void setUp() {
        mMainFragment = new MainFragment();
    }

    @Test
    public void mainFragmentNotNull() {
        assertNotNull(mMainFragment);
    }

    @Test
    public void getTimeDifference() {
        Date d1 = new Date();
        Date d2 = new Date();
        long timeBase = 8000000;
        d1.setTime(timeBase);
        long oneMinute = 1000*60;
        long oneDay = 24*60*60*1000;
        long twoDays = 2 * oneDay;
        d2.setTime(timeBase+twoDays);
        assertEquals("2 dia(s)", mMainFragment.GetTimeDifference(d1, d2));
        d2.setTime(timeBase+oneDay);
        assertEquals("1 dia(s)", mMainFragment.GetTimeDifference(d1, d2));
        d2.setTime(timeBase + (oneDay/2));
        assertEquals("12 hora(s)", mMainFragment.GetTimeDifference(d1, d2));
        d2.setTime(timeBase + (5*oneMinute));
        assertEquals("5 minutos", mMainFragment.GetTimeDifference(d1, d2));
    }
}
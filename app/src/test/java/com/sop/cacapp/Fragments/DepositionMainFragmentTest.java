package com.sop.cacapp.Fragments;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class DepositionMainFragmentTest {

    private DepositionMainFragment mDepositionMainFragment;

    @Before
    public void setUp() {
        mDepositionMainFragment = new DepositionMainFragment();
    }

    @Test
    public void mainFragmentNotNull() {
        assertNotNull(mDepositionMainFragment);
    }

}
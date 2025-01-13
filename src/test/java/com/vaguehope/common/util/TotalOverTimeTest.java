package com.vaguehope.common.util;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class TotalOverTimeTest {

	@Test
	public void itLoops() throws Exception {
		final FakeTicker ticker = new FakeTicker();
		final TotalOverTime t = new TotalOverTime(1, TimeUnit.HOURS, 60, ticker);

		for (int i = 1; i <= 100; i++) {
			t.increment(1);
			assertEquals(Math.min(i, 60), t.get());
			ticker.addTime(1, TimeUnit.MINUTES);
		}
	}

	@Test
	public void itDoesBigJumps() throws Exception {
		final FakeTicker ticker = new FakeTicker();
		final TotalOverTime t = new TotalOverTime(1, TimeUnit.HOURS, 60, ticker);

		t.increment(5);  // t=0m
		assertEquals(5, t.get());

		ticker.addTime(1, TimeUnit.MINUTES);  // t=1m
		t.increment(1);
		assertEquals(6, t.get());

		ticker.addTime(30, TimeUnit.MINUTES);  // t=31m
		t.increment(7);
		assertEquals(13, t.get());

		ticker.addTime(29, TimeUnit.MINUTES);  // t=60m / 0m
		t.increment(9);
		assertEquals(17, t.get());
	}

	@Test
	public void itDoesLongerBuckets() throws Exception {
		final FakeTicker ticker = new FakeTicker();
		final TotalOverTime t = new TotalOverTime(1, TimeUnit.HOURS, 12, ticker);

		t.increment(5);  // t=0m
		assertEquals(5, t.get());

		ticker.addTime(1, TimeUnit.MINUTES);  // t=1m
		t.increment(1);
		assertEquals(6, t.get());

		ticker.addTime(30, TimeUnit.MINUTES);  // t=31m
		t.increment(7);
		assertEquals(13, t.get());

		ticker.addTime(30, TimeUnit.MINUTES);  // t=61m / 1m
		t.increment(9);
		assertEquals(16, t.get());
	}

}

package com.vaguehope.common.util;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Ticker;

public class TotalOverTime {

	private final long totalDurationNanos;
	private final long bucketDurationNanos;
	private final Ticker ticker;

	private final long[] ringBuffer;
	private volatile long lastIncrementNanos;

	public TotalOverTime(final long duration, final TimeUnit timeUnit, final int bucketCount, final Ticker ticker) {
		this.totalDurationNanos = timeUnit.toNanos(duration);
		this.bucketDurationNanos = this.totalDurationNanos / bucketCount;
		this.ticker = ticker;
		this.ringBuffer = new long[bucketCount];
		this.lastIncrementNanos = ticker.read();
	}

	public void increment(final long delta) {
		final long now = this.ticker.read();

		final int expiredBucketCount = (int) ((now - this.lastIncrementNanos) / this.bucketDurationNanos);
		if (expiredBucketCount > 0) {
			final int lastIncrementedBucket = (int) ((this.lastIncrementNanos % this.totalDurationNanos) / this.bucketDurationNanos);
			for (int x = 1; x <= expiredBucketCount; x++) {
				int i = lastIncrementedBucket + x;
				if (i > this.ringBuffer.length - 1) i -= this.ringBuffer.length;
				this.ringBuffer[i] = 0;
			}
		}

		final int currentBucket = (int) ((now % this.totalDurationNanos) / this.bucketDurationNanos);
		this.ringBuffer[currentBucket] += delta;
		this.lastIncrementNanos = now;
	}

	public long get() {
		long total = 0L;
		for (int i = 0; i < this.ringBuffer.length; i++) {
			total += this.ringBuffer[i];
		}
		return total;
	}

}

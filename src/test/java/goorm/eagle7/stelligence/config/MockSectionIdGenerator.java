package goorm.eagle7.stelligence.config;

import java.util.concurrent.atomic.AtomicLong;

import goorm.eagle7.stelligence.common.sequence.SectionIdGenerator;

public class MockSectionIdGenerator implements SectionIdGenerator {

	private AtomicLong sequenceValue = new AtomicLong(1L);

	@Override
	public Long getAndIncrementSectionId() {
		return sequenceValue.getAndIncrement();
	}

	public void clear() {
		sequenceValue.set(1L);
	}
}

package goorm.eagle7.stelligence.domain.badge.event.model;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public abstract class Event {

	private final LocalDateTime createdAt;

	protected Event() {
		this.createdAt = LocalDateTime.now();
	}

}

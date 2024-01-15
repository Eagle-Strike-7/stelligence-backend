package goorm.eagle7.stelligence.common.sequence;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Profile("local") //테스트 환경과 분리하기 위해서 local profile에서만 실행합니다.
class SequenceTableInitiator implements ApplicationRunner {

	private final SequenceTableRepository sequenceTableRepository;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		// section_id_sequence 테이블이 존재하지 않으면 생성합니다.
		if (!sequenceTableRepository.existsById("section")) {
			sequenceTableRepository.save(new SequenceTable("section"));
		}
	}
}

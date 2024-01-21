package goorm.eagle7.stelligence.common.sequence;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SeuqenceTable의 section 레코드는 다음에 생성될 sectionId를 관리합니다.
 * 프로그램 동작 도중 SequenceTable의 sequence 레코드가 없으면 문제가 생깁니다.
 * 일반적으로는 DB에 존재하겠지만, 개발 환경에서는 잦은 초기화 등으로 레코드가 없을 가능성도 있습니다.
 * 따라서 이 클래스는 불필요한 에러를 막기 위해 section 레코드의 생성을 보장하는 역할을 합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Profile({"local", "dev"}) //테스트 환경과 분리하기 위해서 local profile에서만 실행합니다.
class SequenceTableInitiator implements ApplicationRunner {

	private final SequenceTableRepository sequenceTableRepository;

	/**
	 * SequenceTable 테이블의 초기화를 담당합니다.
	 * SequenceTable에 section값이 있는 경우 별도의 초기화를 하지 않으며,
	 * section값이 없는 경우 section값을 1로 초기화합니다.
	 */
	@Override
	public void run(ApplicationArguments args) throws Exception {
		// section_id_sequence 테이블이 존재하지 않으면 생성합니다.
		if (!sequenceTableRepository.existsById("section")) {
			log.info("SequenceTable의 section 레코드가 존재하지 않습니다. 새로 생성합니다.");
			sequenceTableRepository.save(new SequenceTable("section"));
		}
	}
}

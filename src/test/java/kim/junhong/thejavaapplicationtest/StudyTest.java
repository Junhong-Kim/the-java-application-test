package kim.junhong.thejavaapplicationtest;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StudyTest {

    @Test
    @DisplayName("스터디 만들기 fast")
    @Tag("fast")
    void create_new_study_fast() {

    }

    @Test
    @DisplayName("스터디 만들기 slow")
    @Tag("slow")
    void create_new_study_slow() {

    }

    @Test
    @DisplayName("조건에 따라 테스트 실행하기1")
    @EnabledOnOs({OS.MAC})
    @EnabledOnJre({JRE.JAVA_8, JRE.JAVA_11})
    @EnabledIfEnvironmentVariable(named = "TEST_ENV", matches = "LOCAL")
    void env_test_enable() {
        String test_env = System.getenv("TEST_ENV");

        // 예제1
        assumeTrue("LOCAL".equalsIgnoreCase(test_env));
        System.out.println("assumeTrue test_env = " + test_env);

        // 예제2
        assumingThat("LOCAL".equalsIgnoreCase(test_env), () -> {
            System.out.println("assumingThat test_env = " + test_env);
        });
    }

    @Test
    @DisplayName("조건에 따라 테스트 실행하기2")
    @DisabledOnOs({OS.MAC})
    void env_test_disable() {
        // nothing
    }

    @Test
    @DisplayName("스터디 만들기")
    void create_new_study() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new Study(-10));
        assertEquals("limit은 0보다 커야 한다.", exception.getMessage());

        Study study = new Study(10);

        // 모든 구문 확인
        assertAll(
                () -> assertNotNull(study),
                () -> assertEquals(StudyStatus.DRAFT, study.getStatus(), "스터디를 처음 만들면 상태값이 DRAFT 상태여야 한다."), // 성공 여부와 상관 없이 문자열 연산
                () -> assertEquals(StudyStatus.DRAFT, study.getStatus(), () -> "스터디를 처음 만들면 상태값이 " + StudyStatus.DRAFT + " 상태여야 한다."), // 따라서, 문자열 연산 비용이 많이들 경우 람다식을 활용
                () -> assertTrue(study.getLimit() > 0, "스터디 최대 참석 가능 인원은 0보다 커야 한다."),
                () -> assertTimeout(Duration.ofMillis(100), () -> new Study(10)), // 특정 시간 안에 실행이 완료되는지
                () -> assertTimeoutPreemptively(Duration.ofMillis(100), () -> new Study(10)) // 특정 시간만큼 기다리고 즉시 종료
        );
    }

    @Test
    @Disabled
    void create_new_study_again() {
        System.out.println("create1");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("before all");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("after all");
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("before each");
    }

    @AfterEach
    void afterEach() {
        System.out.println("after each");
    }
}

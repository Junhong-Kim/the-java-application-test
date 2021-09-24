package kim.junhong.thejavaapplicationtest;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StudyTest {

    @FastTest
    @DisplayName("스터디 만들기 fast")
    void create_new_study_fast() {

    }

    @SlowTest
    @DisplayName("스터디 만들기 slow")
    void create_new_study_slow() {

    }

    @DisplayName("RepeatedTest 스터디 만들기")
    @RepeatedTest(value = 10, name = "{displayName}, {currentRepetition}/{totalRepetitions}")
    void repeatTest(RepetitionInfo repetitionInfo) {
        System.out.println("test " + repetitionInfo.getCurrentRepetition() + "/" + repetitionInfo.getTotalRepetitions());
    }

    @DisplayName("ParameterizedTest 스터디 만들기")
    @ParameterizedTest(name = "{index} {displayName} message={0}")
    @ValueSource(strings = {"일", "이", "삼", "사"})
//    @EmptySource
//    @NullSource
    @NullAndEmptySource
    void parameterizedTest(String message) {
        System.out.println(message);
    }

    @DisplayName("valueSourceParameterizedTest 스터디 만들기 1")
    @ParameterizedTest(name = "{index} {displayName} message={0}")
    @ValueSource(ints = {10, 20, 40})
    void valueSourceParameterizedTest(Integer limit) {
        System.out.println(limit);
    }

    @DisplayName("valueSourceParameterizedTest 스터디 만들기 2")
    @ParameterizedTest(name = "{index} {displayName} message={0}")
    @ValueSource(ints = {10, 20, 40})
    void valueSourceParameterizedTest(@ConvertWith(StudyConverter.class) Study study) {
        System.out.println(study.getLimit());
    }

    static class StudyConverter extends SimpleArgumentConverter {

        @Override
        protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
            assertEquals(Study.class, targetType, "Can only convert to Study");
            return new Study(Integer.parseInt(source.toString()));
        }
    }

    @DisplayName("csvSourceParameterizedTest 스터디 만들기 1")
    @ParameterizedTest(name = "{index} {displayName} message={0}")
    @CsvSource({"10, 'java study'", "20, 'spring study'"})
    void csvSourceParameterizedTest(Integer limit, String name) {
        Study study = new Study(limit, name);
        System.out.println(study);
    }

    @DisplayName("csvSourceParameterizedTest 스터디 만들기 2")
    @ParameterizedTest(name = "{index} {displayName} message={0}")
    @CsvSource({"10, 'java study'", "20, 'spring study'"})
    void csvSourceParameterizedTest(ArgumentsAccessor argumentsAccessor) {
        Study study = new Study(argumentsAccessor.getInteger(0),argumentsAccessor.getString(1));
        System.out.println(study);
    }

    @DisplayName("csvSourceParameterizedTest 스터디 만들기 3")
    @ParameterizedTest(name = "{index} {displayName} message={0}")
    @CsvSource({"10, 'java study'", "20, 'spring study'"})
    void csvSourceParameterizedTest(@AggregateWith(StudyAggregator.class) Study study) {
        System.out.println(study);
    }

    static class StudyAggregator implements ArgumentsAggregator {

        @Override
        public Object aggregateArguments(ArgumentsAccessor argumentsAccessor, ParameterContext parameterContext) throws ArgumentsAggregationException {
            return new Study(argumentsAccessor.getInteger(0),argumentsAccessor.getString(1));
        }
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

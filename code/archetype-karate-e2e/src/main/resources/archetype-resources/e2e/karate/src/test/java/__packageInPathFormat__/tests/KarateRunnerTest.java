package ${package}.tests;

import com.intuit.karate.junit5.Karate;

public class KarateRunnerTest {

    @Karate.Test
    Karate runAll() {
        return Karate.run("classpath:tests/products").relativeTo(getClass());
    }
}

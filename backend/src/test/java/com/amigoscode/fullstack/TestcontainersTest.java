package com.amigoscode.fullstack;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;


public class TestcontainersTest extends AbstractTestcontainersUnitTest{

    @Test
    void itShouldStartPostgresDb() {
        assertThat(container.isRunning()).isTrue();
        assertThat(container.isCreated()).isTrue();
    }

}

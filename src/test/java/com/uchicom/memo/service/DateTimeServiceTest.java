// (C) 2023 uchicom
package com.uchicom.memo.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.uchicom.memo.AbstractTest;
import java.time.ZoneId;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Spy;

/**
 * {@link DateTimeService}のテストケース.
 *
 * @author uchicom
 */
@Tag("service")
public class DateTimeServiceTest extends AbstractTest {

  @Captor ArgumentCaptor<ZoneId> zoneIdCaptor;

  @Spy @InjectMocks DateTimeService service;

  @Test
  public void getLocalDate() {
    assertThat(service.getLocalDate()).isNotNull();
  }

  @Test
  public void getLocalDateTime() {
    assertThat(service.getLocalDateTime()).isNotNull();
  }
}

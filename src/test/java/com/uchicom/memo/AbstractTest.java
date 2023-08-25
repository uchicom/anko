// (C) 2023 uchicom
package com.uchicom.memo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

import com.uchicom.memo.api.AbstractApi;
import com.uchicom.memo.dto.response.ErrorDto;
import com.uchicom.util.ThrowingSupplier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;

public abstract class AbstractTest {

  AutoCloseable closeable;

  @Captor ArgumentCaptor<ThrowingSupplier<?, Throwable>> supplierCaptor;

  @BeforeEach
  public void openMocks() {
    closeable = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  public void releaseMocks() throws Exception {
    closeable.close();
  }

  protected void createApiMock(AbstractApi abstractApi) {
    createMockTrans(abstractApi);
    createMockRefer(abstractApi);
    var errorDto = new ErrorDto("エラーテスト");
    doReturn(errorDto).when(abstractApi).errorApi(any());
  }

  <T> void createMockTrans(AbstractApi abstractApi) {
    doAnswer(
            invocation -> {
              ThrowingSupplier<T, Throwable> supplier =
                  invocation.<ThrowingSupplier<T, Throwable>>getArgument(1);
              return abstractApi.handling(supplier);
            })
        .when(abstractApi)
        .trans(any(), any());
  }

  <T> void createMockRefer(AbstractApi abstractApi) {
    doAnswer(
            invocation -> {
              ThrowingSupplier<T, Throwable> supplier =
                  invocation.<ThrowingSupplier<T, Throwable>>getArgument(0);
              return abstractApi.handling(supplier);
            })
        .when(abstractApi)
        .refer(any());
  }
}

// (C) 2026 uchicom
package com.uchicom.pj.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import com.uchicom.pj.AbstractTest;
import com.uchicom.pj.Constants;
import com.uchicom.pj.dao.RefreshTokenDao;
import com.uchicom.pj.entity.RefreshToken;
import com.uchicom.pj.util.SecurityUtil;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

/** {@link RefreshTokenService}のテストケース. */
@Tag("service")
public class RefreshTokenServiceTest extends AbstractTest {

  @Mock DateTimeService dateTimeService;
  @Mock RefreshTokenDao refreshTokenDao;

  @Spy @InjectMocks RefreshTokenService service;

  @Captor ArgumentCaptor<byte[]> tokenHashCaptor;
  @Captor ArgumentCaptor<LocalDateTime> dateTimeCaptor;
  @Captor ArgumentCaptor<RefreshToken> refreshTokenCaptor;
  @Captor ArgumentCaptor<Long> accountIdCaptor;

  /** {@link RefreshTokenService#get(String)}で有効なトークンが存在する場合のテスト. */
  @Test
  public void get() throws Exception {
    // mock
    var now = LocalDateTime.of(2026, 3, 29, 12, 0, 0);
    doReturn(now).when(dateTimeService).getLocalDateTime();
    var refreshToken = new RefreshToken();
    var tokenHash = SecurityUtil.getHash("token", Constants.REFRESH_TOKEN_SALT);
    doReturn(refreshToken)
        .when(refreshTokenDao)
        .findRefreshable(tokenHashCaptor.capture(), dateTimeCaptor.capture());
    doReturn(true).when(refreshTokenDao).update(refreshTokenCaptor.capture());

    // test method
    var result = service.get("token");

    // assert
    assertThat(result).isEqualTo(refreshToken);
    assertThat(tokenHashCaptor.getValue()).isEqualTo(tokenHash);
    assertThat(dateTimeCaptor.getValue()).isEqualTo(now);
    assertThat(refreshTokenCaptor.getValue().expire_datetime)
        .isEqualTo(now.plusDays(Constants.REFRESH_TOKEN_MAX_AGE_DAYS));
    Mockito.verify(dateTimeService, Mockito.times(2)).getLocalDateTime();
    Mockito.verify(refreshTokenDao).update(refreshToken);
  }

  /** {@link RefreshTokenService#get(String)}で有効なトークンが存在しない場合のテスト. */
  @Test
  public void get_null() throws Exception {
    // mock
    var now = LocalDateTime.of(2026, 3, 29, 12, 0, 0);
    doReturn(now).when(dateTimeService).getLocalDateTime();
    doReturn(null)
        .when(refreshTokenDao)
        .findRefreshable(tokenHashCaptor.capture(), dateTimeCaptor.capture());

    // test method
    var result = service.get("token");

    // assert
    assertThat(result).isNull();
    Mockito.verify(dateTimeService, Mockito.times(1)).getLocalDateTime();
    Mockito.verify(refreshTokenDao, Mockito.never()).update(Mockito.any());
  }

  /** {@link RefreshTokenService#register(Long)}でレコードが存在しない場合は新規登録のテスト. */
  @Test
  public void register_insert() throws Exception {
    // mock
    var now = LocalDateTime.of(2026, 3, 30, 12, 0, 0);
    doReturn(now).when(dateTimeService).getLocalDateTime();
    doReturn(null).when(refreshTokenDao).findByAccountId(accountIdCaptor.capture());
    var token = "generated_token";
    doReturn(token).when(service).generateToken();
    doReturn(true).when(refreshTokenDao).insert(refreshTokenCaptor.capture());

    // test method
    var accountId = 1L;
    var result = service.register(accountId);

    // assert
    assertThat(result).isEqualTo(token);
    assertThat(accountIdCaptor.getValue()).isEqualTo(accountId);
    var captured = refreshTokenCaptor.getValue();
    assertThat(captured.account_id).isEqualTo(accountId);
    assertThat(captured.token_hash)
        .isEqualTo(SecurityUtil.getHash(token, Constants.REFRESH_TOKEN_SALT));
    assertThat(captured.expire_datetime)
        .isEqualTo(now.plusDays(Constants.REFRESH_TOKEN_MAX_AGE_DAYS));
    Mockito.verify(refreshTokenDao, Mockito.never()).update(Mockito.any());
  }

  /** {@link RefreshTokenService#register(Long)}でinactive_datetimeがnullの場合は更新のテスト. */
  @Test
  public void register_update() throws Exception {
    // mock
    var now = LocalDateTime.of(2026, 3, 30, 12, 0, 0);
    doReturn(now).when(dateTimeService).getLocalDateTime();
    var existing = new RefreshToken();
    existing.inactive_datetime = null;
    doReturn(existing).when(refreshTokenDao).findByAccountId(accountIdCaptor.capture());
    var token = "generated_token";
    doReturn(token).when(service).generateToken();
    doReturn(true).when(refreshTokenDao).update(refreshTokenCaptor.capture());

    // test method
    var result = service.register(1L);

    // assert
    assertThat(result).isEqualTo(token);
    var captured = refreshTokenCaptor.getValue();
    assertThat(captured.token_hash)
        .isEqualTo(SecurityUtil.getHash(token, Constants.REFRESH_TOKEN_SALT));
    assertThat(captured.expire_datetime)
        .isEqualTo(now.plusDays(Constants.REFRESH_TOKEN_MAX_AGE_DAYS));
    Mockito.verify(refreshTokenDao, Mockito.never()).insert(Mockito.any());
  }

  /** {@link RefreshTokenService#register(Long)}でinactive_datetimeがnot nullの場合はnull返却のテスト. */
  @Test
  public void register_inactive() throws Exception {
    // mock
    var existing = new RefreshToken();
    existing.inactive_datetime = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
    doReturn(existing).when(refreshTokenDao).findByAccountId(accountIdCaptor.capture());

    // test method
    var result = service.register(1L);

    // assert
    assertThat(result).isNull();
    Mockito.verify(refreshTokenDao, Mockito.never()).insert(Mockito.any());
    Mockito.verify(refreshTokenDao, Mockito.never()).update(Mockito.any());
  }

  /** {@link RefreshTokenService#delete(Long)}でレコードが存在しない場合のテスト. */
  @Test
  public void delete_null() throws Exception {
    // mock
    doReturn(null).when(refreshTokenDao).findByAccountId(accountIdCaptor.capture());

    // test method
    service.delete(1L);

    // assert
    assertThat(accountIdCaptor.getValue()).isEqualTo(1L);
    Mockito.verify(refreshTokenDao, Mockito.never()).delete(Mockito.any());
  }

  /** {@link RefreshTokenService#delete(Long)}でinactive_datetimeがnot nullの場合は削除しないテスト. */
  @Test
  public void delete_inactive() throws Exception {
    // mock
    var existing = new RefreshToken();
    existing.inactive_datetime = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
    doReturn(existing).when(refreshTokenDao).findByAccountId(accountIdCaptor.capture());

    // test method
    service.delete(1L);

    // assert
    assertThat(accountIdCaptor.getValue()).isEqualTo(1L);
    Mockito.verify(refreshTokenDao, Mockito.never()).delete(Mockito.any());
  }

  /** {@link RefreshTokenService#delete(Long)}で正常削除のテスト. */
  @Test
  public void delete() throws Exception {
    // mock
    var existing = new RefreshToken();
    existing.inactive_datetime = null;
    doReturn(existing).when(refreshTokenDao).findByAccountId(accountIdCaptor.capture());
    Mockito.doNothing().when(refreshTokenDao).delete(refreshTokenCaptor.capture());

    // test method
    service.delete(1L);

    // assert
    assertThat(accountIdCaptor.getValue()).isEqualTo(1L);
    assertThat(refreshTokenCaptor.getValue()).isEqualTo(existing);
  }

  /** {@link RefreshTokenService#generateToken()}でトークンが生成されるテスト. */
  @Test
  public void generateToken() throws Exception {
    // test method
    var result = service.generateToken();

    // assert
    assertThat(result).isNotNull();
    assertThat(result).isNotBlank();
  }
}

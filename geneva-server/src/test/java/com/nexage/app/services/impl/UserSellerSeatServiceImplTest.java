package com.nexage.app.services.impl;

import static com.nexage.app.web.support.TestObjectsFactory.createCompany;
import static com.nexage.app.web.support.TestObjectsFactory.createSellerSeat;
import static com.nexage.app.web.support.TestObjectsFactory.createSellerSeatUser;
import static com.nexage.app.web.support.TestObjectsFactory.createUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.nexage.admin.core.model.SellerSeat;
import com.nexage.admin.core.model.User;
import com.nexage.admin.core.repository.SellerSeatRepository;
import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.security.UserContext;
import com.ssp.geneva.common.error.exception.GenevaValidationException;
import com.ssp.geneva.common.model.inventory.CompanyType;
import com.ssp.geneva.common.security.error.GenevaSecurityException;
import com.ssp.geneva.common.security.error.SecurityErrorCodes;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserSellerSeatServiceImplTest {

  @Mock private SellerSeatRepository sellerSeatRepository;
  @Mock private UserContext userContext;
  @InjectMocks private UserSellerSeatServiceImpl sellerSeatService;

  @Test
  void updateUserWhereNotAuthorized() {
    // given
    User user = createSellerSeatUser();
    when(userContext.hasAccessToSellerSeatOrHasNexageAffiliation(anyLong())).thenReturn(false);
    var sellerSeatPid = user.getSellerSeat().getPid();
    // when & then
    var exception =
        assertThrows(
            GenevaSecurityException.class,
            () -> sellerSeatService.updateUserWithVerifiedSellerSeat(user, sellerSeatPid));
    assertEquals(SecurityErrorCodes.SECURITY_NOT_AUTHORIZED, exception.getErrorCode());
  }

  @Test
  void updateUserWhereSellerSeatNotFound() {
    // given
    SellerSeat sellerSeat = createSellerSeat();
    User user = createUser();
    user.setSellerSeat(sellerSeat);

    when(sellerSeatRepository.findById(sellerSeat.getPid())).thenReturn(Optional.empty());
    when(userContext.hasAccessToSellerSeatOrHasNexageAffiliation(sellerSeat.getPid()))
        .thenReturn(true);
    var sellerSeatPid = sellerSeat.getPid();
    // when & then
    var exception =
        assertThrows(
            GenevaValidationException.class,
            () -> sellerSeatService.updateUserWithVerifiedSellerSeat(user, sellerSeatPid));
    assertEquals(ServerErrorCodes.SERVER_SELLER_SEAT_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  void updateUserWhereSellerSeatWithoutSellers() {
    // given
    SellerSeat sellerSeat = createSellerSeat();
    User user = createUser();
    user.setSellerSeat(sellerSeat);

    when(sellerSeatRepository.findById(sellerSeat.getPid())).thenReturn(Optional.of(sellerSeat));
    when(userContext.hasAccessToSellerSeatOrHasNexageAffiliation(sellerSeat.getPid()))
        .thenReturn(true);
    var sellerSeatPid = sellerSeat.getPid();
    // when & then
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> sellerSeatService.updateUserWithVerifiedSellerSeat(user, sellerSeatPid));
    assertEquals(
        ServerErrorCodes.SERVER_SELLER_SEAT_WITHOUT_SELLERS_NOT_ALLOWED, exception.getErrorCode());
  }

  @Test
  void updateUserWhereSellerSeatIsDisabled() {
    // given
    SellerSeat sellerSeat = createSellerSeat();
    sellerSeat.setSellers(Set.of(createCompany(CompanyType.SELLER)));
    sellerSeat.disable();

    User user = createUser();
    user.setSellerSeat(sellerSeat);
    when(sellerSeatRepository.findById(sellerSeat.getPid())).thenReturn(Optional.of(sellerSeat));
    when(userContext.hasAccessToSellerSeatOrHasNexageAffiliation(sellerSeat.getPid()))
        .thenReturn(true);
    var sellerSeatPid = sellerSeat.getPid();

    // when & then
    GenevaValidationException exception =
        assertThrows(
            GenevaValidationException.class,
            () -> sellerSeatService.updateUserWithVerifiedSellerSeat(user, sellerSeatPid));
    assertEquals(ServerErrorCodes.SERVER_SELLER_SEAT_NOT_ENABLED, exception.getErrorCode());
  }

  @Test
  void updateUser() {
    // given
    SellerSeat sellerSeatIn = createSellerSeat();
    SellerSeat sellerSeatOut = createSellerSeat();
    sellerSeatOut.setSellers(Set.of(createCompany(CompanyType.SELLER)));

    User user = createUser();
    user.setSellerSeat(sellerSeatIn);
    when(sellerSeatRepository.findById(sellerSeatIn.getPid()))
        .thenReturn(Optional.of(sellerSeatOut));
    when(userContext.hasAccessToSellerSeatOrHasNexageAffiliation(sellerSeatIn.getPid()))
        .thenReturn(true);

    // when
    sellerSeatService.updateUserWithVerifiedSellerSeat(user, sellerSeatIn.getPid());

    // then
    assertSame(sellerSeatOut, user.getSellerSeat());
  }
}

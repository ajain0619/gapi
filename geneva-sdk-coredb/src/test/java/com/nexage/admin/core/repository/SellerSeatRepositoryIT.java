package com.nexage.admin.core.repository;

import static com.nexage.admin.core.model.SellerSeat.ENABLED;
import static com.nexage.admin.core.specification.SellerSeatSpecification.withNameLike;
import static com.nexage.admin.core.specification.SellerSeatSpecification.withNonEmptySellers;
import static com.nexage.admin.core.specification.SellerSeatSpecification.withPidNotIn;
import static com.nexage.admin.core.specification.SellerSeatSpecification.withStatus;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.data.jpa.domain.Specification.where;

import com.nexage.admin.core.CoreDbSdkIntegrationTestBase;
import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.SellerSeat;
import com.nexage.admin.core.model.SellerSeatMdmId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Sql(
    scripts = "/data/repository/seller-seat-repository.sql",
    config = @SqlConfig(encoding = "utf-8"))
class SellerSeatRepositoryIT extends CoreDbSdkIntegrationTestBase {

  private static final Long SELLER_SEAT_PID = 12345L;

  @Autowired private SellerSeatRepository sellerSeatRepository;

  @Test
  void shouldFetchSellerSeat() {
    SellerSeat seat = sellerSeatRepository.getOne(SELLER_SEAT_PID);

    assertNotNull(seat);
    assertEquals(SELLER_SEAT_PID, seat.getPid());
  }

  @Test
  void shouldFetchOnlyAssignableSellerSeats() {
    Page<SellerSeat> sellerSeats =
        sellerSeatRepository.findAll(
            where(withStatus(ENABLED)).and(withNonEmptySellers()), PageRequest.of(0, 10));

    assertNotNull(sellerSeats);
    assertEquals(1, sellerSeats.getTotalElements());
    assertEquals(SELLER_SEAT_PID, sellerSeats.getContent().get(0).getPid());
  }

  @Test
  void shouldCreateNewSellerSeat() {
    SellerSeat sellerSeat = createSellerSeat();

    sellerSeatRepository.save(sellerSeat);

    // then
    List<SellerSeat> sellerSeats = sellerSeatRepository.findAll();
    assertEquals(6, sellerSeats.size());
    //     get that second SellerSeat created (apart from the one in setup method)
    List<SellerSeat> seatsCreatedInTest =
        sellerSeatRepository.findAll(where(withPidNotIn(SELLER_SEAT_PID, 2L, 3L, 4L, 5L)));
    assertEquals(1, seatsCreatedInTest.size());
    SellerSeat newlyInserted = seatsCreatedInTest.get(0);
    assertEquals("ssTestName", newlyInserted.getName());
    assertEquals("ssTestDesc", newlyInserted.getDescription());
    assertNotNull(newlyInserted.getPid());
  }

  @Test
  void shouldReturnSellerSeatMdmIdIfSetOnAnActiveSellerSeat() {
    // given
    Company company = new Company();
    company.setName("Test 1");
    company.setId("8a858acb012c2c608ee1608ee8cb3017");

    SellerSeat sellerSeat = new SellerSeat();
    sellerSeat.setPid(12345l);
    sellerSeat.setName("Foo");
    sellerSeat.setDescription("Bar");
    sellerSeat.setStatus(Boolean.TRUE);
    sellerSeat.setVersion(0);
    sellerSeat.getSellers().add(company);

    // when
    Optional<SellerSeat> result = sellerSeatRepository.findById(12345l);

    // then
    SellerSeatMdmId sellerSeatMdmId = new SellerSeatMdmId();
    sellerSeatMdmId.setPid(1L);
    sellerSeatMdmId.setId("mdm2");
    sellerSeatMdmId.setLastUpdate(new Date(1625137871000L));
    sellerSeatMdmId.setSellerSeat(sellerSeat);

    List<SellerSeatMdmId> mdmIds = result.get().getMdmIds();
    assertEquals(1, mdmIds.size());
    SellerSeatMdmId sellerSeatMdmIdResult = mdmIds.get(0);

    assertEquals(sellerSeatMdmId, sellerSeatMdmIdResult);
  }

  @Test
  void shouldFetchLikeSellerSeatsWhenSearchingByName() {
    String queryTerm = "Foo";
    Page<SellerSeat> sellerSeats =
        sellerSeatRepository.findAll(
            where(withStatus(ENABLED))
                .and(withNonEmptySellers().and(withNonEmptySellers()).and(withNameLike(queryTerm))),
            PageRequest.of(0, 1000));

    assertNotNull(sellerSeats);
    assertEquals(1, sellerSeats.getTotalElements());
    assertEquals(SELLER_SEAT_PID, sellerSeats.getContent().get(0).getPid());
  }

  private SellerSeat createSellerSeat() {
    SellerSeat sellerSeat = new SellerSeat();
    sellerSeat.setStatus(Boolean.TRUE);
    sellerSeat.setName("ssTestName");
    sellerSeat.setDescription("ssTestDesc");
    return sellerSeat;
  }
}

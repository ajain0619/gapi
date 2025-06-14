package com.nexage.app.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.nexage.admin.core.model.Company;
import com.nexage.admin.core.model.User;
import com.nexage.app.dto.user.CompanyViewDTO;
import com.nexage.app.dto.user.UserDTO;
import com.nexage.app.security.UserContext;
import com.ssp.geneva.common.model.inventory.CompanyType;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UserDTOMapperTest {

  @Test
  void testMapDTO() {
    // given
    final long pid = new Random().nextLong();
    final String id = UUID.randomUUID().toString();
    final String userName = UUID.randomUUID().toString();
    final String email = UUID.randomUUID().toString();
    final String companyName = UUID.randomUUID().toString();
    final String contactNumber = UUID.randomUUID().toString();

    User source = new User();
    source.setPid(pid);
    source.setId(id);
    source.setUserName(userName);
    source.addCompany(new Company(companyName, CompanyType.BUYER));
    source.setEmail(email);
    source.setContactNumber(contactNumber);

    // when
    UserDTO result = UserDTOMapper.MAPPER.map(source);

    // then
    assertNotNull(result);
    assertEquals(result.getPid(), source.getPid());
    assertEquals(result.getId(), source.getId());
    assertEquals(result.getUserName(), source.getUserName());
    assertEquals(result.getCompanies().iterator().next().getName(), source.getCompanyName());
    assertEquals(result.getCompanies().iterator().next().getType(), source.getCompanyType());
    assertEquals(result.getEmail(), source.getEmail());
    assertEquals(result.getContactNumber(), source.getContactNumber());
  }

  @Test
  void testMapFromDTO() {
    // given
    final long pid = new Random().nextLong();
    final String id = UUID.randomUUID().toString();
    final String userName = UUID.randomUUID().toString();
    final String email = UUID.randomUUID().toString();

    final long companyPid = new Random().nextLong();
    final String companyName = UUID.randomUUID().toString();
    final String contactNumber = UUID.randomUUID().toString();

    UserDTO source = new UserDTO();
    source.setPid(pid);
    source.setId(id);
    source.setUserName(userName);
    source.setEmail(email);
    source.setContactNumber(contactNumber);

    Set<CompanyViewDTO> companies = new HashSet<>();
    companies.add(new CompanyViewDTO(companyPid, companyName, CompanyType.BUYER, false));
    source.setCompanies(companies);

    // when
    User result = UserDTOMapper.MAPPER.map(source);

    // then
    assertNotNull(result);
    assertEquals(result.getPid(), source.getPid());
    assertEquals(result.getId(), source.getId());
    assertEquals(result.getUserName(), source.getUserName());
    assertEquals(result.getCompanyPid(), source.getCompanies().iterator().next().getPid());
    assertEquals(result.getCompanyName(), source.getCompanies().iterator().next().getName());
    assertEquals(result.getCompanyType(), source.getCompanies().iterator().next().getType());
    assertEquals(result.getEmail(), source.getEmail());
    assertEquals(result.getContactNumber(), source.getContactNumber());
  }

  @Test
  void shouldSetPrimaryContactWhenUserIsNotSellerSeatUser() {
    // given
    final long pid = new Random().nextLong();
    final String id = UUID.randomUUID().toString();
    final String userName = UUID.randomUUID().toString();
    final String email = UUID.randomUUID().toString();

    final long companyPid = new Random().nextLong();
    final String companyName = UUID.randomUUID().toString();
    final String contactNumber = UUID.randomUUID().toString();

    UserDTO source = new UserDTO();
    source.setPid(pid);
    source.setId(id);
    source.setUserName(userName);
    source.setEmail(email);
    source.setContactNumber(contactNumber);
    source.setPrimaryContact(true);

    Set<CompanyViewDTO> companies = new HashSet<>();
    companies.add(new CompanyViewDTO(companyPid, companyName, CompanyType.BUYER, false));
    source.setCompanies(companies);

    User user = new User();
    user.setPrimaryContact(false);
    user.addCompany(new Company());

    UserContext userContext = mock(UserContext.class);
    given(userContext.isCurrentUser(source.getPid())).willReturn(false);
    given(userContext.writePrivilegeCheck(user)).willReturn(true);
    // when
    UserDTOMapper.MAPPER.setUserDetails(source, user, userContext);

    // then
    assertTrue(user.isPrimaryContact());
    assertEquals(user, user.getCompany().getContact());
  }
}

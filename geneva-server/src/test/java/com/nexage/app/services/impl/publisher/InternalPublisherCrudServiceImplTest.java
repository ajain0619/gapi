package com.nexage.app.services.impl.publisher;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;

import com.nexage.app.dto.publisher.PublisherDTO;
import com.nexage.app.error.EntityConstraintViolationException;
import com.nexage.app.services.BeanValidationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InternalPublisherCrudServiceImplTest {

  @Mock private BeanValidationService beanValidationService;

  @InjectMocks InternalPublisherCrudServiceImpl internalPublisherCrudService;

  @Test
  void shouldThrowWhenCreatingInternalPublisherWithInvalidInput() {
    // given
    PublisherDTO publisherDto = PublisherDTO.newBuilder().build();

    doThrow(EntityConstraintViolationException.class)
        .when(beanValidationService)
        .validate(publisherDto);

    // when/then
    assertThrows(
        EntityConstraintViolationException.class,
        () -> internalPublisherCrudService.create(publisherDto));
  }

  @Test
  void shouldThrowWhenUpdatingInternalPublisherWithInvalidInput() {
    // given
    PublisherDTO publisherDto = PublisherDTO.newBuilder().build();

    doThrow(EntityConstraintViolationException.class)
        .when(beanValidationService)
        .validate(publisherDto);

    // when/then
    assertThrows(
        EntityConstraintViolationException.class,
        () -> internalPublisherCrudService.update(publisherDto, 1L));
  }
}

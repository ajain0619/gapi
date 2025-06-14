package com.nexage.app.dto.filter;

import java.io.Serializable;
import java.util.Date;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterListDTO implements Serializable {

  private static final long serialVersionUID = -7001634166728666897L;

  private Integer pid;
  private Long buyerId;

  @Length(min = 1, max = 240)
  @NotEmpty
  private String name;

  private FilterListUploadStatusDTO uploadStatus;
  @NotNull private FilterListTypeDTO type;
  private Integer valid;
  private Integer invalid;
  private Integer duplicate;
  private Integer error;
  private Integer total;
  private Date created;
  private Date updated;
  private Integer version;
}

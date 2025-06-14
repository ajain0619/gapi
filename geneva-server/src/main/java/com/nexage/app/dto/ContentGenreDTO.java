package com.nexage.app.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContentGenreDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  @NotNull private Long pid;

  @NotNull private String genre;
}

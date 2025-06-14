package com.nexage.app.queue.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

public class MessageHeadersConst {

  public static final String MESSAGING_TRACE_ID_MDC = "messagingTraceId";
  public static final String USERNAME_MDC = "userName";
  public static final String SSP_SOURCE = "ssp";
  public static final String ENRICH_MESSAGE_COMMAND = "enrich_message_command";
  public static final String CREATE_COMPANY_EVENT = "create_company_event";

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public class Operation {
    public static final String CREATE = "CREATE";
  }
}

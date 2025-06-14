package com.nexage.geneva.util;

import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseTransformer;
import com.github.tomakehurst.wiremock.http.BasicResponseRenderer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.Response;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;

public class CaptureStateTransformer extends ResponseTransformer {

  @Override
  public Response transform(
      Request request, Response response, FileSource files, Parameters parameters) {
    String state = null;
    String redirect_uri = null;

    if (request.queryParameter("state") != null) {
      state = request.queryParameter("state").firstValue();
    }
    redirect_uri = request.queryParameter("redirect_uri").firstValue();
    BasicResponseRenderer basic = new BasicResponseRenderer();
    ResponseDefinition responseDefinition =
        ResponseDefinition.redirectTo(redirect_uri + "?code=authcode&state=" + state);
    ServeEvent serveEvent = ServeEvent.of(LoggedRequest.createFrom(request), responseDefinition);
    Response redirectResponse = basic.render(serveEvent);
    return redirectResponse;
  }

  @Override
  public String getName() {
    return "CaptureStateTransformer";
  }

  @Override
  public boolean applyGlobally() {
    return false;
  }
}

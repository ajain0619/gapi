package com.nexage.app.services;

public interface SessionService {

  /** Invalidate all Spring sessions this endpoint is intended only for admin/ops */
  void deleteSessions();
}

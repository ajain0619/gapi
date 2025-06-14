package com.ssp.geneva.common.security.config;

import com.ssp.geneva.common.security.handler.CustomMethodSecurityExpressionHandler;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.annotation.Jsr250Voter;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.ExpressionBasedPreInvocationAdvice;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.prepost.PreInvocationAuthorizationAdviceVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

/** {@inheritDoc} */
@Log4j2
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class GenevaGlobalMethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {

  @Bean("roleHierarchy")
  public RoleHierarchyImpl roleHierarchy() {
    var roleHierarchy = new RoleHierarchyImpl();
    roleHierarchy.setHierarchy(
        "ROLE_ADMIN_NEXAGE > ROLE_MANAGER_NEXAGE\n"
            + "ROLE_MANAGER_YIELD_NEXAGE > ROLE_MANAGER_SMARTEX_NEXAGE\n"
            + "ROLE_MANAGER_SMARTEX_NEXAGE > ROLE_MANAGER_NEXAGE\n"
            + "ROLE_MANAGER_NEXAGE > ROLE_USER_NEXAGE\n"
            + "ROLE_ADMIN_SELLER > ROLE_MANAGER_SELLER\n"
            + "ROLE_MANAGER_SELLER > ROLE_USER_SELLER\n"
            + "ROLE_ADMIN_BUYER > ROLE_MANAGER_BUYER\n"
            + "ROLE_MANAGER_BUYER > ROLE_USER_BUYER\n"
            + "ROLE_ADMIN_SEATHOLDER > ROLE_MANAGER_SEATHOLDER\n"
            + "ROLE_MANAGER_SEATHOLDER > ROLE_USER_SEATHOLDER\n"
            + "ROLE_ADMIN_SELLER_SEAT > ROLE_MANAGER_SELLER_SEAT\n"
            + "ROLE_MANAGER_SELLER_SEAT > ROLE_USER_SELLER_SEAT\n"
            + "ROLE_USER_SELLER > ROLE_AD_REVIEWER_NEXAGE\n"
            + "ROLE_AD_REVIEWER_NEXAGE > ROLE_AD_REVIEWER_SELLER");
    return roleHierarchy;
  }

  @Bean("roleVoter")
  public RoleHierarchyVoter roleVoter() {
    return new RoleHierarchyVoter(roleHierarchy());
  }

  @Bean("methodSecurityExpressionHandler")
  public DefaultMethodSecurityExpressionHandler methodSecurityExpressionHandler() {
    var handler = new CustomMethodSecurityExpressionHandler();
    handler.setRoleHierarchy(roleHierarchy());
    return handler;
  }

  @Bean("expressionBasedPreInvocationAdvice")
  public ExpressionBasedPreInvocationAdvice expressionBasedPreInvocationAdvice() {
    var advice = new ExpressionBasedPreInvocationAdvice();
    advice.setExpressionHandler(methodSecurityExpressionHandler());
    return advice;
  }

  @Bean("methodAccessDecisionManager")
  public AffirmativeBased methodAccessDecisionManager() {
    var voter = new PreInvocationAuthorizationAdviceVoter(expressionBasedPreInvocationAdvice());
    return new AffirmativeBased(
        List.of(roleVoter(), new Jsr250Voter(), new AuthenticatedVoter(), voter));
  }

  @Override
  protected AccessDecisionManager accessDecisionManager() {
    return methodAccessDecisionManager();
  }
}

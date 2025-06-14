package com.nexage.admin.core.validator;

/**
 * Validation Group to be used when updating a Native placement. The groups
 * NativePlacementCreateGroup and NativePlacementUpdateGroup are a temporary workaround, to avoid
 * triggering validations not currently used for native placements. Some of the validators need to
 * be updated to support Native V2. Created a ticket to cover this:
 * https://jira.vzbuilders.com/browse/MX-14662
 */
public interface NativePlacementUpdateGroup {}

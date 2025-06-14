package com.nexage.app.services;

import com.nexage.admin.core.model.Position;
import com.nexage.admin.core.model.RTBProfile;
import com.nexage.admin.core.model.Site;

/**
 * Methods used in reference to Seller's positions.
 *
 * <p>NOTE: Please do not add any new functionality to this class or its implementations. The
 * business concepts associated to the different operations handled by the following functions use
 * strategies, concepts, or frameworks considered deprecated. Be sure you reach to the core team
 * before considering designing or implementing new functionality under this class.
 */
public interface SellerPositionService {
  /**
   * Gets the position based on Pid.
   *
   * @param pid The {@link Long} that correlates to a {@link Position}'s pid.
   * @return The {@link Position} that matches the pid passed in.
   */
  Position getPosition(Long pid);

  /**
   * Creates a {@link Position} for the {@link Site} that matches the site Pid passed in.
   *
   * @param sitePid The Pid that correlates to the {@link Site} to create the {@link Position}
   *     under.
   * @param position The {@link Position} to be created under the {@link Site}.
   * @return The {@link Site} with the newly created {@link Position}.
   */
  Site createPosition(Long sitePid, Position position);

  /**
   * Updates the {@link Position} to the {@link Position} passed in.
   *
   * @param position The updated {@link Position}.
   * @return The {@link Site} with the updated {@link Position}.
   */
  Site updatePosition(Position position);

  /**
   * Deletes the {@link Position} from the {@link Site} that matches the provided sitePid.
   *
   * @param sitePid The Pid of the {@link Site} to delete the {@link Position} from.
   * @param positionPid The Pid of the {@link Position} that is being deleted.
   * @return The {@link Site} without the {@link Position} that was deleted.
   */
  Site deletePosition(Long sitePid, Long positionPid);

  /**
   * Archives the {@link Position} from the provided {@link Site}.
   *
   * @param site The {@link Site} that the {@link Position} is being archived from.
   * @param positionPid The pid that corresponds with the {@link Position} being archived.
   * @return The {@link Site} without the archived {@link Position}.
   */
  Site archivePosition(Site site, Long positionPid);

  /**
   * Assigns {@link RTBProfile} to {@link Position}.
   *
   * @param positionPid The Pid that correlates to the {@link Position} to add the {@link
   *     RTBProfile} to.
   * @param rtbProfilePid The Pid of the {@link RTBProfile} that's being added to the {@link
   *     Position}.
   * @param ownerRTBProfilePid The pid for the Owner of the {@link RTBProfile}.
   */
  void assignRTBProfileToPosition(Long positionPid, Long rtbProfilePid, Long ownerRTBProfilePid);
}

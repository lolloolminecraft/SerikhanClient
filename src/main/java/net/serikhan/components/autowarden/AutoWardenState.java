package net.serikhan.components.autowarden;

/**
 * State machine for Auto Warden controller.
 * Controls transitions between different operational phases.
 */
public enum AutoWardenState {
    /**
     * Idle state - awaiting activation or safe conditions.
     */
    IDLE,

    /**
     * Moving toward Warden spawn location.
     */
    GO_WARDEN,

    /**
     * Waiting for teleport command to execute.
     */
    WAIT_WARDEN_TELEPORT,

    /**
     * Active farming/working near Warden.
     */
    WORKING,

    /**
     * Returning home to resupply.
     */
    RETURN_HOME,

    /**
     * Waiting for home teleport to execute.
     */
    WAIT_HOME_TELEPORT,

    /**
     * Restocking potions from supply chest.
     */
    RESUPPLY,

    /**
     * Applying potion effects before returning to Warden.
     */
    APPLY_EFFECTS,

    /**
     * Temporarily suspended due to external threat (e.g., player nearby).
     */
    SUSPENDED,

    /**
     * Emergency escape due to Warden threat.
     */
    EMERGENCY,

    /**
     * Insufficient supplies available - cannot continue.
     */
    NO_SUPPLIES
}

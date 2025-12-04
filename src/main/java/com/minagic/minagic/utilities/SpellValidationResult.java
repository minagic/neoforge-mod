package com.minagic.minagic.utilities;

import javax.annotation.Nullable;

public record SpellValidationResult(
        boolean success,
        @Nullable String failureMessage,
        boolean showToPlayer
) {
    public static final SpellValidationResult OK = new SpellValidationResult(true, null, false);
    public static final SpellValidationResult INVALID_PHASE = new SpellValidationResult(false, "Invalid spell phase", false);

    public static SpellValidationResult fail(String message, boolean showToPlayer) {
        return new SpellValidationResult(false, message, showToPlayer);
    }

    public static SpellValidationResult internalFail(String reason) {
        return fail(reason, false); // for debug logs, not players
    }

    public static SpellValidationResult playerFail(String message) {
        return fail(message, true); // show in HUD
    }

    public SpellValidationResult and(SpellValidationResult other) {
        return this.success ? other : this;
    }

    public SpellValidationResult or(SpellValidationResult other) {
        return this.success ? this : other;
    }

    public SpellValidationResult negate() {
        return this.success
                ? SpellValidationResult.fail("Negated success", this.showToPlayer)
                : SpellValidationResult.OK;
    }
}
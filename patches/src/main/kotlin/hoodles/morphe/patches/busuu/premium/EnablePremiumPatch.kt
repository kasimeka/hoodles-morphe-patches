/**
 * Copyright 2026 Hoo-dles
 * https://github.com/hoo-dles/morphe-patches
 */

package hoodles.morphe.patches.busuu.premium

import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.patch.stringOption
import app.morphe.util.returnEarly
import hoodles.morphe.compatibility.Compat

// Wire values parsed by AccessTierDomainModel (iel.a): "standard" -> PREMIUM, "plus" -> PREMIUM_PLUS.
enum class PremiumTier(val wireValue: String) {
    PREMIUM("standard"),
    PREMIUM_PLUS("plus")
}

@Suppress("unused")
val enablePremiumPatch = bytecodePatch(
    name = "Enable Premium",
    description = "Enables app features locked behind the subscription paywall."
) {
    compatibleWith(Compat.BUSUU)

    val premiumTier by stringOption(
        key = "premiumTier",
        default = PremiumTier.PREMIUM_PLUS.name,
        values = mapOf(
            "Premium" to PremiumTier.PREMIUM.name,
            "Premium Plus" to PremiumTier.PREMIUM_PLUS.name
        ),
        title = "Tier",
        description = "Choose which subscription tier to report to the app.",
        required = true,
    )

    execute {
        val tier = enumValueOf<PremiumTier>(premiumTier!!)

        IsPremiumFingerprint.match(ApiUserToStringFingerprint.classDef)
            .method.returnEarly(true)

        GetTierFingerprint.match(ApiUserAccessToStringFingerprint.classDef)
            .method.returnEarly(tier.wireValue)

        GetHasActiveSubscriptionFingerprint.method.returnEarly(true)
        IsPremiumFingerprint.match(PremiumUserCtorFingerprint.classDef).method.returnEarly(true)
    }
}

/**
 * Copyright 2026 Hoo-dles
 * https://github.com/hoo-dles/morphe-patches
 */

package hoodles.morphe.patches.busuu.premium

import app.morphe.patcher.Fingerprint

object IsPremiumFingerprint : Fingerprint(
    name = "isPremium"
)

// ApiUser
object ApiUserToStringFingerprint : Fingerprint(
    strings = listOf("ApiUser(legacyUid=")
)

// ApiUserAccess
object ApiUserAccessToStringFingerprint : Fingerprint(
    strings = listOf("ApiUserAccess(tier=")
)
object GetTierFingerprint : Fingerprint(
    name = "getTier"
)

// PremiumUser
object PremiumUserCtorFingerprint : Fingerprint(
    strings = listOf("legacyId", "name", "avatar", "accessTier")
)
object GetHasActiveSubscriptionFingerprint : Fingerprint(
    name = "getHasActiveSubscription"
)

// UserEntity: the stored user record. Its accessTier is read when the user is loaded from the local database.
object UserEntityGetAccessTierFingerprint : Fingerprint(
    classFingerprint = Fingerprint(
        strings = listOf("UserEntity(legacyId=")
    ),
    name = "getAccessTier",
    parameters = listOf(),
    returnType = "Ljava/lang/String;"
)


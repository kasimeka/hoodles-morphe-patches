/**
 * Copyright 2026 Hoo-dles
 * https://github.com/hoo-dles/morphe-patches
 */

package hoodles.morphe.patches.busuu.premium

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.literal

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

// ho3.h(): maps a lesson to its state (nkj). Locked is nkj.c; nkj.f is unlocked.
object LessonStateFingerprint : Fingerprint(
    strings = listOf("learntLanguage", "courseProgress"),
    returnType = "Lnkj;"
)

// ho3.k(): course banner state (ihj). Available shows the "free lessons" banner; ihj.b hides it.
object CourseBannerStateFingerprint : Fingerprint(
    parameters = listOf("L", "Z", "I", "I"),
    returnType = "Lihj;"
)

// da0.isPremiumAccount(): reads the local access_data flag that gates locked lessons.
object IsPremiumAccountFingerprint : Fingerprint(
    classFingerprint = Fingerprint(
        strings = listOf("access_data")
    ),
    name = "isPremiumAccount",
    parameters = listOf(),
    returnType = "Z"
)

// Role ids: 6 = Premium Plus, 7 = Premium Standard.
object SubscriptionStateFingerprint : Fingerprint(
    parameters = listOf("L", "Z", "Z"),
    returnType = "Lcom/busuu/domain/entities/user/SubscriptionStateEnum;",
    filters = listOf(
        literal(6),
        literal(7)
    )
)

object UserEntityGetAccessTierFingerprint : Fingerprint(
    classFingerprint = Fingerprint(
        strings = listOf("UserEntity(legacyId=")
    ),
    name = "getAccessTier",
    parameters = listOf(),
    returnType = "Ljava/lang/String;"
)


/**
 * Copyright 2026 Hoo-dles
 * https://github.com/hoo-dles/morphe-patches
 */

package hoodles.morphe.patches.busuu.premium

import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.morphe.patcher.extensions.InstructionExtensions.getInstruction
import app.morphe.patcher.extensions.InstructionExtensions.instructions
import app.morphe.util.getFreeRegisterProvider
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import app.morphe.patcher.patch.bytecodePatch
import app.morphe.patcher.patch.stringOption
import app.morphe.util.returnEarly
import hoodles.morphe.compatibility.Compat

enum class PremiumTier(val wireValue: String) {
    PREMIUM("standard"),
    PREMIUM_PLUS("plus")
}

@Suppress("unused")
val enablePremiumPatch = bytecodePatch(
    name = "Enable Premium",
    description = "Enables app features locked behind the subscription paywall. The Premium Plus tier needs the exact recommended Busuu version and is more prone to failure."
) {
    compatibleWith(Compat.BUSUU)

    val premiumTier by stringOption(
        key = "premiumTier",
        default = PremiumTier.PREMIUM.name,
        values = mapOf(
            "Premium" to PremiumTier.PREMIUM.name,
            "Premium Plus" to PremiumTier.PREMIUM_PLUS.name
        ),
        title = "Tier",
        description = "Choose which subscription tier to report to the app. Premium Plus needs the exact recommended Busuu version and is more prone to failure.",
        required = true,
    )

    execute {
        val tier = enumValueOf<PremiumTier>(premiumTier!!)

        IsPremiumFingerprint.match(ApiUserToStringFingerprint.classDef)
            .method.returnEarly(true)

        GetTierFingerprint.match(ApiUserAccessToStringFingerprint.classDef)
            .method.returnEarly(tier.wireValue)
        UserEntityGetAccessTierFingerprint.method.returnEarly(tier.wireValue)

        if (tier == PremiumTier.PREMIUM_PLUS) {
            IsPremiumAccountFingerprint.method.returnEarly(true)
            CourseBannerStateFingerprint.method.addInstructions(
                0,
                """
                    sget-object p0, Lihj${'$'}b;->a:Lihj${'$'}b;
                    return-object p0
                """.trimIndent()
            )
            LessonStateFingerprint.method.let { method ->
                val returnIndexes = method.instructions.withIndex()
                    .filter { it.value.opcode == Opcode.RETURN_OBJECT }
                    .map { it.index }
                returnIndexes.asReversed().forEach { index ->
                    val valueRegister = method.getInstruction<OneRegisterInstruction>(index).registerA
                    val tempRegister = method.getFreeRegisterProvider(index, 1, emptyList()).getFreeRegister()
                    method.addInstructionsWithLabels(
                        index,
                        """
                            instance-of v$tempRegister, v$valueRegister, Lnkj${'$'}c;
                            if-eqz v$tempRegister, :locked_$index
                            sget-object v$valueRegister, Lnkj${'$'}f;->a:Lnkj${'$'}f;
                            :locked_$index
                            instance-of v$tempRegister, v$valueRegister, Lnkj${'$'}g;
                            if-eqz v$tempRegister, :keep_$index
                            sget-object v$valueRegister, Lnkj${'$'}f;->a:Lnkj${'$'}f;
                            :keep_$index
                            nop
                        """.trimIndent()
                    )
                }
            }
            SubscriptionStateFingerprint.method.addInstructions(
                0,
                """
                    sget-object p0, Lcom/busuu/domain/entities/user/SubscriptionStateEnum;->PREMIUM_PLUS:Lcom/busuu/domain/entities/user/SubscriptionStateEnum;
                    return-object p0
                """.trimIndent()
            )
        }

        GetHasActiveSubscriptionFingerprint.method.returnEarly(true)
        IsPremiumFingerprint.match(PremiumUserCtorFingerprint.classDef).method.returnEarly(true)
    }
}

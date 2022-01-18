package com.template.states

import com.template.contracts.TokenContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.ContractState
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party

@BelongsToContract(TokenContract::class)
data class TokenState(
    // ① TokenStateが正しい型のパラメータを持つこと
    // ③ senderとreceiverがParticipantsであること
    override val participants: List<AbstractParty> = listOf()
) : ContractState // ② ContractStateであること
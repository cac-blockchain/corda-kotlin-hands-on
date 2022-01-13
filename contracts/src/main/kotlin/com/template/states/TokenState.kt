package com.template.states

import com.template.contracts.TokenContract
import net.corda.core.contracts.BelongsToContract
import net.corda.core.contracts.ContractState
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.Party

@BelongsToContract(TokenContract::class)
data class TokenState(
    val sender: Party,
    val receiver: Party,
    val amount: Int,
    override val participants: List<AbstractParty> = listOf(sender, receiver)
) : ContractState
package com.template.contracts

import com.template.states.TokenState
import net.corda.core.contracts.*
import net.corda.core.contracts.Requirements.using
import net.corda.core.transactions.LedgerTransaction

class TokenContract : Contract {
    companion object {
        // Used to identify our contract when building a transaction.
        const val ID = "com.template.contracts.TokenContract"
    }

    // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
    // does not throw an exception.
    override fun verify(tx: LedgerTransaction) {
        // Verification logic goes here.
        val command = tx.commands.requireSingleCommand<Commands.Create>()
        val outputs = tx.outputsOfType<TokenState>()
        requireThat {
            "No inputs should be consumed.".using(tx.inputStates.isEmpty())
            "Output must be only 1.".using(outputs.size == 1)
            val output = outputs.single()
            "Has zero-amount TokenState.".using(output.amount != 0)
            "Has negative-amount TokenState.".using(output.amount >= 0)
            checkSigners(output, command)
        }
    }

    private fun checkSigners(state: TokenState, command: CommandWithParties<Commands>) {
        "Sender must be a signer." using command.signers.contains(state.sender.owningKey)
        "Receiver must be a signer." using command.signers.contains(state.receiver.owningKey)
    }

    // Used to indicate the transaction's intent.
    interface Commands : CommandData {
        class Create : Commands
    }
}
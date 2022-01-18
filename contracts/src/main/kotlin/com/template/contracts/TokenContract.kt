package com.template.contracts

import com.template.states.TokenState
import net.corda.core.contracts.*
import net.corda.core.contracts.Requirements.using
import net.corda.core.transactions.LedgerTransaction

// ① Contractであること
class TokenContract : Contract {
    companion object {
        // Used to identify our contract when building a transaction.
        const val ID = "com.template.contracts.TokenContract"
    }

    // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
    // does not throw an exception.
    override fun verify(tx: LedgerTransaction) {
        val command = null
        val outputs = tx.outputsOfType<TokenState>()
        requireThat {
            // ② TokenContractのinputが0であること

            // ③ TokenContractのoutputが1つであること

            // ④ outputのAmountが正の整数であること
        }
    }

    private fun checkSigners(state: TokenState, command: CommandWithParties<Commands>) {
        // ⑥ 必須の署名者が指定されていること
    }

    // Used to indicate the transaction's intent.
    interface Commands : CommandData {
        // ⑤ TokenContractのコマンドがCreateであること
        class Create : Commands
    }
}
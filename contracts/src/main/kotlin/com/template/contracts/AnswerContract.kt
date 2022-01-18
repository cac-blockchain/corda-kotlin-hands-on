package com.template.contracts

import com.template.states.TokenState
import net.corda.core.contracts.*
import net.corda.core.contracts.Requirements.using
import net.corda.core.transactions.LedgerTransaction

// ① Contractであること
//class TokenContract : Contract {
//    companion object {
//        // Used to identify our contract when building a transaction.
//        const val ID = "com.template.contracts.TokenContract"
//    }
//
//    // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
//    // does not throw an exception.
//    override fun verify(tx: LedgerTransaction) {
//        // Verification logic goes here.
//        // ⑤ TokenContractのコマンドがCreateであること
//        val command = tx.commands.requireSingleCommand<Commands.Create>()
//        val outputs = tx.outputsOfType<TokenState>()
//        requireThat {
//            "② TokenContractのinputが0であること".using(tx.inputStates.isEmpty())
//            "③ TokenContractのoutputが1つであること".using(outputs.size == 1)
//            val output = outputs.single()
//            "④ outputのAmountが正の整数であること".using(output.amount != 0 && output.amount >= 0)
//            checkSigners(output, command)
//        }
//    }
//
//    private fun checkSigners(state: TokenState, command: CommandWithParties<Commands>) {
//        // ⑦ 必須の署名者が指定されていること
//        "Sender must be a signer." using command.signers.contains(state.sender.owningKey)
//        "Receiver must be a signer." using command.signers.contains(state.receiver.owningKey)
//    }
//
//    // Used to indicate the transaction's intent.
//    interface Commands : CommandData {
//        class Create : Commands
//    }
//}
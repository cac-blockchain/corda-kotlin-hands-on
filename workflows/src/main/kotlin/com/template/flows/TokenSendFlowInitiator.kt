package com.template.flows

import co.paralleluniverse.fibers.Suspendable
import com.template.contracts.TokenContract
import com.template.states.TokenState
import net.corda.core.contracts.Command
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder

@StartableByRPC
@InitiatingFlow
class TokenSendFlowInitiator(
    private val receiver: Party,
    private val amount: Int
    ) : FlowLogic<SignedTransaction>() {

    @Suspendable
    override fun call(): SignedTransaction {
        // We choose our transaction's notary (the notary prevents double-spends).
        // ① 正しいノータリーを使用していること
        val notary = serviceHub.networkMapCache.notaryIdentities[0]
        // We get a reference to our own identity.
        val sender = ourIdentity

        // We create our new TokenState.
        // ② 正しいStateをoutputとしていること
        val tokenState = TokenState(sender, receiver, amount)

        // We build our transaction.
        // ③ 正しいコマンドを使用していること
        // ④ 正しい署名者を指定していること
        val txCommand = Command(
            TokenContract.Commands.Create(),
            listOf(sender.owningKey, receiver.owningKey)
        )
        // ⑤ 正しいContractを使用していること
        val txBuilder = TransactionBuilder(notary)
            .addOutputState(tokenState, TokenContract.ID)
            .addCommand(txCommand)

        // We check our transaction is valid based on its contracts.
        txBuilder.verify(serviceHub)

        val session = initiateFlow(receiver)

        // We sign the transaction with our private key, making it immutable.
        val signedTransaction = serviceHub.signInitialTransaction(txBuilder)

        // The counterparty signs the transaction
        val fullySignedTransaction = subFlow(CollectSignaturesFlow(signedTransaction, listOf(session)))

        // We get the transaction notarised and recorded automatically by the platform.
        return subFlow(FinalityFlow(fullySignedTransaction, listOf(session)))
    }
}

@InitiatedBy(TokenSendFlowInitiator::class)
class TokenSendFlowResponder(private val counterpartySession: FlowSession) : FlowLogic<SignedTransaction>() {
    @Suspendable
    override fun call(): SignedTransaction {
        val signTransactionFlow = object : SignTransactionFlow(counterpartySession) {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {
                //Addition checks
            }
        }
        val txId = subFlow(signTransactionFlow).id
        return subFlow(ReceiveFinalityFlow(counterpartySession, expectedTxId = txId))
    }
}
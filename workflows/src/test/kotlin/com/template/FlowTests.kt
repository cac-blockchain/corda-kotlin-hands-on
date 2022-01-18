package com.template

import com.template.contracts.TokenContract
import com.template.flows.TokenSendFlowInitiator
import com.template.states.TokenState
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.utilities.getOrThrow
import net.corda.testing.core.singleIdentity
import net.corda.testing.node.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.util.concurrent.Future


class FlowTests {
    private lateinit var network: MockNetwork

    private lateinit var nodeA: StartedMockNode
    private lateinit var nodeB: StartedMockNode
    private lateinit var notaryNode: StartedMockNode

    private lateinit var alice: Party
    private lateinit var bob: Party
    private lateinit var notary: Party

    @Before
    fun setup() {
        network = MockNetwork(
            MockNetworkParameters(
                cordappsForAllNodes = listOf(
                    TestCordapp.findCordapp("com.template.contracts"),
                    TestCordapp.findCordapp("com.template.flows")
                )
            )
        )
        notaryNode = network.notaryNodes.first()
        notary = notaryNode.info.singleIdentity()
        nodeA = network.createPartyNode()
        nodeB = network.createPartyNode()
        notary = notaryNode.info.singleIdentity()
        alice = nodeA.info.singleIdentity()
        bob = nodeB.info.singleIdentity()
        network.runNetwork()
    }

    @After
    fun tearDown() {
        network.stopNodes()
    }

    // TODO:
    //  ① 正しいノータリーを使用していること
    //  ② 正しいStateをoutputとしていること
    //  ③ 正しいコマンドを使用していること
    //  ④ 正しい署名者を指定していること
    //  ⑤ 正しいContractを使用していること
    //  ⑥ なんですかこれは

    @Test // ① 正しいノータリーを使用していること
    fun transactionConstructedByFlowUsesTheCorrectNotary() {
        val signedTransaction = runTokenSendFlowInitiator()

        assertEquals(1, signedTransaction.tx.outputStates.size)
        val output = signedTransaction.tx.outputs.single()

        assertEquals(notary, output.notary)
    }

    @Test // ② 正しいStateをoutputとしていること
    fun transactionConstructedByFlowHasOneTokenStateOutputWithTheCorrectAmountAndOwner() {
        val signedTransaction = runTokenSendFlowInitiator()

        assertEquals(1, signedTransaction.tx.outputStates.size)
        val output = signedTransaction.tx.outputsOfType(TokenState::class.java).single()

        assertEquals(bob, output.receiver)
        assertEquals(99, output.amount)
    }

    @Test // ③ 正しいコマンドを使用していること
    fun transactionConstructedByFlowHasOneIssueCommand() {
        val signedTransaction = runTokenSendFlowInitiator()
        assertEquals(1, signedTransaction.tx.commands.size)
        val command = signedTransaction.tx.commands.single()

        assert(command.value is TokenContract.Commands.Create)
    }

    @Test // ④ 正しい署名者を指定していること
    fun transactionConstructedByFlowHasOneCommandWithTheIssuerAndTheOwnerAsASigners() {
        val signedTransaction = runTokenSendFlowInitiator()
        assertEquals(1, signedTransaction.tx.commands.size)
        val command = signedTransaction.tx.commands.single()

        assertEquals(2, command.signers.size)
        assertTrue(command.signers.contains(alice.owningKey))
        assertTrue(command.signers.contains(bob.owningKey))
    }

    @Test // ⑤ 正しいContractを使用していること
    fun transactionConstructedByFlowHasOneOutputUsingTheCorrectContract() {
        val signedTransaction = runTokenSendFlowInitiator()
        assertEquals(1, signedTransaction.tx.outputStates.size)
        val output = signedTransaction.tx.outputs.single()

        assertEquals(TokenContract.ID, output.contract)
    }

    @Test // ⑥ なんですかこれは
    fun transactionConstructedByFlowHasNoInputsAttachmentsOrTimeWindows() {
        val signedTransaction = runTokenSendFlowInitiator()
        assertEquals(0, signedTransaction.tx.inputs.size)
        // The single attachment is the contract attachment.
        assertEquals(1, signedTransaction.tx.attachments.size)
        assertNull(signedTransaction.tx.timeWindow)
    }

    private fun runTokenSendFlowInitiator(): SignedTransaction {
        val flow = TokenSendFlowInitiator(bob, 99)
        val future = nodeA.startFlow(flow)
        network.runNetwork()
        return future.getOrThrow()
    }
}
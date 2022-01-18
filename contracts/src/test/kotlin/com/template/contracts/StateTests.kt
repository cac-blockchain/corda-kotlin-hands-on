package com.template.contracts

import com.template.states.TokenState
import net.corda.core.contracts.Contract
import net.corda.core.contracts.ContractState
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.testing.core.TestIdentity
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

class StateTests {
    private val alice: Party = TestIdentity(CordaX500Name("Alice", "Tokyo", "JP")).party
    private val bob: Party = TestIdentity(CordaX500Name("Bob", "NewYork", "US")).party

// TODO:
//  ① TokenStateが正しい型のパラメータを持つこと
//  ② ContractStateであること
//  ③ senderとreceiverがParticipantsであること

    @Test // ① TokenStateが正しい型のパラメータを持つこと
    fun tokenStateHasParamsOfCorrectTypeInConstructor() {
        val tokenState = TokenState(alice, bob, 100)
        assertEquals(alice, tokenState.sender)
        assertEquals(bob, tokenState.receiver)
        assertEquals(100, tokenState.amount)
    }

    @Test // ② ContractStateであること
    fun tokenContractImplementsContract() {
        assertTrue(TokenState(alice, bob, 100) is ContractState)
    }

    @Test // ③ senderとreceiverがParticipantsであること
    fun tokenStateHasTwoParticipantsTheSenderAndTheReceiver() {
        val tokenState = TokenState(alice, bob, 100)
        assertEquals(2, tokenState.participants.size)
        assertTrue(tokenState.participants.contains(alice))
        assertTrue(tokenState.participants.contains(bob))
    }
}
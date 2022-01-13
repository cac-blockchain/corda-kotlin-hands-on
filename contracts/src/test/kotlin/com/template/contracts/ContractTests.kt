import com.template.contracts.TokenContract
import com.template.states.TokenState
import net.corda.core.contracts.Contract
import net.corda.core.identity.CordaX500Name
import net.corda.testing.contracts.DummyState
import net.corda.testing.core.DummyCommandData
import net.corda.testing.core.TestIdentity
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test

class ContractTests {
    private val alice = TestIdentity(CordaX500Name("Alice", "Tokyo", "JP"))
    private val bob = TestIdentity(CordaX500Name("Bob", "NewYork", "US"))
    private val ledgerServices = MockServices(listOf("com.template.contracts"), alice, bob)
    private val tokenState = TokenState(alice.party, bob.party, 100)

    @Test // ①
    fun tokenContractImplementsContract() {
        assert(TokenContract() is Contract)
    }

    @Test // ②
    fun tokenContractRequiresZeroInputsInTheTransaction() {
        ledgerServices.ledger {
            transaction {
                // Has an input, will fail.
                input(TokenContract.ID, tokenState)
                output(TokenContract.ID, tokenState)
                command(
                    listOf(alice.publicKey, bob.publicKey),
                    TokenContract.Commands.Create()
                )
                fails()
            }

            transaction {
                // Has no input, will verify.
                output(TokenContract.ID, tokenState)
                command(
                    listOf(alice.publicKey, bob.publicKey),
                    TokenContract.Commands.Create()
                )
                verifies()

            }
        }
    }

    @Test // ③
    fun tokenContractRequiresOneOutputInTheTransaction() {
        ledgerServices.ledger {
            transaction {
                // Has two outputs, will fail.
                output(TokenContract.ID, tokenState)
                output(TokenContract.ID, tokenState)
                command(
                    listOf(alice.publicKey, bob.publicKey),
                    TokenContract.Commands.Create()
                )
                fails()
            }

            transaction {
                // Has one output, will verify.
                output(TokenContract.ID, tokenState)
                command(
                    listOf(alice.publicKey, bob.publicKey),
                    TokenContract.Commands.Create()
                )
                verifies()
            }
        }
    }

    @Test // ④
    fun tokenContractRequiresTheTransactionsOutputToBeATokenState() {
        ledgerServices.ledger {
            transaction {
                // Has wrong output type, will fail.
                output(TokenContract.ID, DummyState())
                command(
                    listOf(alice.publicKey , bob.publicKey),
                    TokenContract.Commands.Create()
                )
                fails()
            }

            transaction {
                // Has correct output type, will verify.
                output(TokenContract.ID, tokenState)
                command(
                    listOf(alice.publicKey , bob.publicKey),
                    TokenContract.Commands.Create()
                )
                verifies()
            }
            
        }
    }

    @Test // ⑤
    fun tokenContractRequiresTheTransactionsOutputToHaveAPositiveAmount() {
        val zeroTokenState = TokenState(alice.party, bob.party, 0)
        val negativeTokenState = TokenState(alice.party, bob.party, -1)
        val positiveTokenState = TokenState(alice.party, bob.party, 2)

        ledgerServices.ledger {
            transaction {
                // Has zero-amount TokenState, will fail.
                output(TokenContract.ID, zeroTokenState)
                command(
                    listOf(alice.publicKey , bob.publicKey),
                    TokenContract.Commands.Create()
                )
                failsWith("Has zero-amount TokenState.")
            }

            transaction {
                // Has negative-amount TokenState, will fail.
                output(TokenContract.ID, negativeTokenState)
                command(
                    listOf(alice.publicKey , bob.publicKey),
                    TokenContract.Commands.Create()
                )
                failsWith("Has negative-amount TokenState.")
            }

            transaction {
                // Has positive-amount TokenState, will verify.
                output(TokenContract.ID, tokenState)
                command(
                    listOf(alice.publicKey , bob.publicKey),
                    TokenContract.Commands.Create()
                )
                verifies()

            }

            transaction {
                // Also has positive-amount TokenState, will verify.
                output(TokenContract.ID, positiveTokenState)
                command(
                    listOf(alice.publicKey , bob.publicKey),
                    TokenContract.Commands.Create()
                )
                verifies()
            }
        }
    }

    @Test // ⑥
    fun tokenContractRequiresTheTransactionsCommandToBeAnIssueCommand() {
        ledgerServices.ledger {
            transaction {
                // Has wrong command type, will fail.
                output(TokenContract.ID, tokenState)
                command(listOf(alice.publicKey , bob.publicKey), DummyCommandData)
                fails()
            }

            transaction {
                // Has correct command type, will verify.
                output(TokenContract.ID, tokenState)
                command(
                    listOf(alice.publicKey , bob.publicKey),
                    TokenContract.Commands.Create()
                )
                verifies()
            }
        }
    }

    @Test // ⑦
    fun tokenContractRequiresTheIssuerToBeARequiredSignerInTheTransaction() {
        ledgerServices.ledger {
            transaction {
                // Sender is not a required signer, will fail.
                output(TokenContract.ID, tokenState)
                command(bob.publicKey, TokenContract.Commands.Create())
                failsWith("Sender must be a signer.")
            }

            transaction {
                // Receiver is also not a required signer, will fail.
                output(TokenContract.ID, tokenState)
                command(alice.publicKey, TokenContract.Commands.Create())
                failsWith("Receiver must be a signer.")
            }

            transaction {
                // Sender and Receiver are required signers, will verify.
                output(TokenContract.ID, tokenState)
                command(
                    listOf(alice.publicKey , bob.publicKey),
                    TokenContract.Commands.Create()
                )
                verifies()
            }
        }
    }

    @Test // ⑧
    fun tokenContractRequiresOneCommandInTheTransaction() {
        ledgerServices.ledger {
            transaction {
                output(TokenContract.ID, tokenState)
                // Has two commands, will fail.
                command(
                    listOf(alice.publicKey , bob.publicKey),
                    TokenContract.Commands.Create()
                )
                command(
                    listOf(alice.publicKey , bob.publicKey),
                    TokenContract.Commands.Create()
                )
                fails()
            }

            transaction {
                output(TokenContract.ID, tokenState)
                // Has one command, will verify.
                command(
                    listOf(alice.publicKey , bob.publicKey),
                    TokenContract.Commands.Create()
                )
                verifies()
            }
        }
    }
}
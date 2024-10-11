package dev.emmanuel.wallet.customers.infrastructure.graphql.signup

import com.ninjasquad.springmockk.MockkBean
import dev.emmanuel.wallet.common.domain.exception.DomainException
import dev.emmanuel.wallet.customers.application.usecase.SignUp
import dev.emmanuel.wallet.customers.domain.entity.Email
import dev.emmanuel.wallet.customers.domain.entity.FullName
import dev.emmanuel.wallet.customers.domain.entity.NewCustomer
import dev.emmanuel.wallet.customers.infrastructure.graphql.WithGraphQLTest
import dev.emmanuel.wallet.customers.mocks.Customers
import io.mockk.every
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.Import


@Import(SignUpMutation::class)
internal class SignUpMutationTest : WithGraphQLTest() {

    @MockkBean
    private lateinit var signUp: SignUp

    @Test
    fun `should signup a new customer when valid data was provided`() {
        val savedCustomer = Customers.givenCustomer()
        every { signUp.execute(any()) } returns savedCustomer

        val mutation = """
          mutation SignUp {
            signUp(input: {
              fullName: "Emmanuel Silva",
              email: "emmanuel@dev.com",
              password: "password"
            }) {
              ...on CustomerRegistered {
                id
              }
            }
          }
      """.trimIndent()

        val graphqlResponse =
            dgsQueryExecutor.executeAndExtractJsonPathAsObject(mutation, "data.signUp", CustomerRegistered::class.java)

        assertThat(graphqlResponse)
            .isEqualTo(CustomerRegistered(id = savedCustomer.id.value))

        verify {
            signUp.execute(
                NewCustomer(
                    fullName = FullName("Emmanuel Silva"),
                    email = Email("emmanuel@dev.com")
                )
            )
        }
    }

    @Test
    fun `should return a bad request error when invalid full name was provided`() {
        val mutation = """
          mutation SignUp {
            signUp(input: {
              fullName: "",
              email: "invalid",
              password: "password"
            }) {
              ...on CustomerRegistered {
                id
              }
            }
          }
      """.trimIndent()

        assertThatReturnBadRequestErrors(
            query = mutation,
            messages = arrayOf("Full name must not be blank", "Full name must contain at least first and last names")
        )

        verify(exactly = 0) { signUp.execute(any()) }
    }

    @Test
    fun `should return a bad request error when invalid email was provided`() {
        val mutation = """
          mutation SignUp {
            signUp(input: {
              fullName: "Full Name",
              email: "invalid",
              password: "password"
            }) {
              ...on CustomerRegistered {
                id
              }
            }
          }
      """.trimIndent()

        assertThatReturnBadRequestErrors(query = mutation,"Email must have a valid format")
        verify(exactly = 0) { signUp.execute(any()) }
    }

    @Test
    fun `should return a failed precondition error when an domain exception was thrown`() {
        every { signUp.execute(any()) } throws DomainException("Failed due to an domain error")

        val mutation = """
          mutation SignUp {
            signUp(input: {
              fullName: "Full Name",
              email: "mock@email.com",
              password: "password"
            }) {
              ...on CustomerRegistered {
                id
              }
            }
          }
      """.trimIndent()

        assertThatReturnPreConditionFailedErrors(query = mutation,"Failed due to an domain error")
    }
}
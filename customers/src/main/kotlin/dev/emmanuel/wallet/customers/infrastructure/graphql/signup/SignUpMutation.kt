package dev.emmanuel.wallet.customers.infrastructure.graphql.signup

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import dev.emmanuel.wallet.customers.application.usecase.SignUp
import dev.emmanuel.wallet.customers.application.usecase.SignUpRequest
import dev.emmanuel.wallet.customers.domain.entity.Email
import dev.emmanuel.wallet.customers.domain.entity.FullName
import dev.emmanuel.wallet.customers.domain.entity.NewCustomer
import dev.emmanuel.wallet.customers.domain.entity.Password

@DgsComponent
class SignUpMutation(
    private val signUp: SignUp
) {
    @DgsMutation(field = "signUp")
    fun signUp(@InputArgument("input") input: SignUpInput): CustomerRegistered {
        val useCaseRequest = buildUseCaseRequest(input)

        val customer = signUp.execute(useCaseRequest)
        return CustomerRegistered(id = customer.id.value)
    }

    private fun buildUseCaseRequest(input: SignUpInput): SignUpRequest {
        val newCustomer = NewCustomer(
            fullName = FullName(input.fullName),
            email = Email(input.email)
        )

        val password = Password(input.password)

        return SignUpRequest(
            newCustomer = newCustomer,
            password = password
        )
    }
}
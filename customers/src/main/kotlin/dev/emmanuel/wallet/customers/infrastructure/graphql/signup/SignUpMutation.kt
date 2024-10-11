package dev.emmanuel.wallet.customers.infrastructure.graphql.signup

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import dev.emmanuel.wallet.customers.application.usecase.SignUp
import dev.emmanuel.wallet.customers.domain.entity.Email
import dev.emmanuel.wallet.customers.domain.entity.FullName
import dev.emmanuel.wallet.customers.domain.entity.NewCustomer

@DgsComponent
class SignUpMutation(
    private val signUp: SignUp
) {
    @DgsMutation(field = "signUp")
    fun signUp(@InputArgument("input") input: SignUpInput): CustomerRegistered {
        val newCustomer = NewCustomer(
            fullName = FullName(input.fullName),
            email = Email(input.email)
        )

        val customer = signUp.execute(newCustomer)
        return CustomerRegistered(id = customer.id.value)
    }
}
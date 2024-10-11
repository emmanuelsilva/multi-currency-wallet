package dev.emmanuel.wallet.customers.infrastructure.graphql.signup

data class SignUpInput(
    val fullName: String,
    val email: String,
    val password: String
)
package dev.emmanuel.wallet.customers.infrastructure.graphql

import com.netflix.graphql.dgs.DgsQueryExecutor
import com.netflix.graphql.dgs.autoconfig.DgsAutoConfiguration
import com.netflix.graphql.dgs.autoconfig.DgsExtendedScalarsAutoConfiguration
import dev.emmanuel.wallet.common.infrastructure.graphql.core.GraphQLExceptionHandler
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    classes = [
        DgsAutoConfiguration::class,
        DgsExtendedScalarsAutoConfiguration::class,
        GraphQLExceptionHandler::class
    ]
)
class WithGraphQLTest {

    @Autowired
    protected lateinit var dgsQueryExecutor: DgsQueryExecutor

    fun assertThatReturnBadRequestErrors(query: String, vararg messages: String) {
        val graphqlResponse = dgsQueryExecutor.execute(query)
        val errors = graphqlResponse.errors

        assertThat(errors)
            .hasSize(messages.size)
            .allSatisfy { error ->
                assertThat(error.extensions).containsEntry("errorType", "BAD_REQUEST")
                assertThat(messages).contains(error.message)
            }
    }

    fun assertThatReturnPreConditionFailedErrors(query: String, vararg messages: String) {
        val graphqlResponse = dgsQueryExecutor.execute(query)
        val errors = graphqlResponse.errors

        assertThat(errors)
            .hasSize(messages.size)
            .allSatisfy { error ->
                assertThat(error.extensions).containsEntry("errorType", "FAILED_PRECONDITION")
                assertThat(messages).contains(error.message)
            }
    }

    fun assertReturnedAnUnauthorizedErrorResponse(query: String) {
        val graphqlResponse = dgsQueryExecutor.execute(query)
        val errors = graphqlResponse.errors

        assertThat(errors)
            .hasSize(1)
            .allSatisfy { error ->
                assertThat(error.message).isEqualTo("Unauthenticated")
                assertThat(error.extensions).containsEntry("errorType", "UNAUTHENTICATED")
            }
    }
}
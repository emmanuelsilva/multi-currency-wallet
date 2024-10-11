package dev.emmanuel.wallet.common.infrastructure.graphql.core

import com.netflix.graphql.types.errors.ErrorType
import com.netflix.graphql.types.errors.TypedGraphQLError
import dev.emmanuel.wallet.common.domain.exception.DomainException
import dev.emmanuel.wallet.common.domain.exception.ValidationException
import graphql.execution.DataFetcherExceptionHandler
import graphql.execution.DataFetcherExceptionHandlerParameters
import graphql.execution.DataFetcherExceptionHandlerResult
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture

@Component
class GraphQLExceptionHandler : DataFetcherExceptionHandler {

    private val logger = KotlinLogging.logger { }

    override fun handleException(handlerParameters: DataFetcherExceptionHandlerParameters): CompletableFuture<DataFetcherExceptionHandlerResult> {
        val exception = handlerParameters.exception
        return handleErrorBasedOnExceptionType(exception, handlerParameters)
    }

    private fun handleErrorBasedOnExceptionType(
        exception: Throwable,
        handlerParameters: DataFetcherExceptionHandlerParameters
    ) = when (exception) {
        is ValidationException -> handleBadRequestErrorResult(exception, handlerParameters)
        is DomainException -> handleFailedPreconditionErrorResult(exception, handlerParameters)
        else -> buildGenericInternalResult(handlerParameters)
    }

    private fun handleBadRequestErrorResult(
        exception: ValidationException,
        handlerParameters: DataFetcherExceptionHandlerParameters
    ): CompletableFuture<DataFetcherExceptionHandlerResult> {

        logger.warn(exception) { "Returning bad request due to validation exception" }

        val graphqlErrors = exception.messages.map { message ->
            TypedGraphQLError
                .newBadRequestBuilder()
                .message(message)
                .path(handlerParameters.path)
                .build()
        }

        val result = DataFetcherExceptionHandlerResult.Builder().errors(graphqlErrors).build()
        return CompletableFuture.completedFuture(result)
    }

    private fun handleFailedPreconditionErrorResult(
        exception: DomainException,
        handlerParameters: DataFetcherExceptionHandlerParameters
    ): CompletableFuture<DataFetcherExceptionHandlerResult> {

        logger.warn(exception) { "Returning failed precondition due to domain exception" }

        val graphqlError = TypedGraphQLError
            .newBuilder()
            .errorType(ErrorType.FAILED_PRECONDITION)
            .message(exception.message)
            .path(handlerParameters.path)
            .build()

        val result = DataFetcherExceptionHandlerResult.newResult(graphqlError).build()

        return CompletableFuture.completedFuture(result)
    }

    private fun buildGenericInternalResult(
        handlerParameters: DataFetcherExceptionHandlerParameters
    ): CompletableFuture<DataFetcherExceptionHandlerResult> {
        val exception = handlerParameters.exception
        logger.error(exception) { "Returning internal server due to an unamapped exception ${exception.message}" }

        val graphqlError = TypedGraphQLError
            .newInternalErrorBuilder()
            .message(exception.message)
            .path(handlerParameters.path)
            .build()

        val result = DataFetcherExceptionHandlerResult.newResult(graphqlError).build()
        return CompletableFuture.completedFuture(result)
    }
}
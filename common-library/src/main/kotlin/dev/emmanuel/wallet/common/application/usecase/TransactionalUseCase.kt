package dev.emmanuel.wallet.common.application.usecase

import org.springframework.core.annotation.AliasFor
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Transactional
@Service
annotation class TransactionalUseCase(
    @get: AliasFor(annotation = Transactional::class, attribute = "propagation")
    val transactionPropagation: Propagation = Propagation.REQUIRED,

    @get: AliasFor(annotation = Transactional::class, attribute = "readOnly")
    val readOnly: Boolean = false
)
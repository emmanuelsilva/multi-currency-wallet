package dev.emmanuel.wallet.common.infrastructure.repository

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions

@Configuration
class JdbcCustomConversionsConfiguration(
    val mapReaderConverter: MapReaderConverter,
    val mapWriterConverter: MapWriterConverter
) {

    @Bean
    fun jdbcCustomConversions() = JdbcCustomConversions(listOf(mapReaderConverter, mapWriterConverter))

}
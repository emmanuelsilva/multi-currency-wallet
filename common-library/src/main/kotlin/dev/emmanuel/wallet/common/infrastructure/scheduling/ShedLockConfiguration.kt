package dev.emmanuel.wallet.common.infrastructure.scheduling

import io.github.oshai.kotlinlogging.KotlinLogging
import net.javacrumbs.shedlock.core.LockProvider
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.scheduling.annotation.EnableScheduling
import javax.sql.DataSource

@Configuration
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "1m")
class ShedLockConfiguration {

    private val logger = KotlinLogging.logger { }

    @Bean
    fun lockProvider(dataSource: DataSource): LockProvider {
        logger.info { "Configuring shedlock with JDBC template" }

        val configuration = JdbcTemplateLockProvider.Configuration.builder()
            .withJdbcTemplate(JdbcTemplate(dataSource))
            .usingDbTime()
            .build()

        return JdbcTemplateLockProvider(configuration)
    }

}
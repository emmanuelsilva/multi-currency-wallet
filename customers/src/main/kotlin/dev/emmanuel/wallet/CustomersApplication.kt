package dev.emmanuel.wallet

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CustomersApplication

fun main(args: Array<String>) {
	runApplication<CustomersApplication>(*args)
}

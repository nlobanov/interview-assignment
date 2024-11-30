package dev.lobanov.raynetassignment

import dev.lobanov.raynetassignment.config.CsvUploadProperties
import dev.lobanov.raynetassignment.config.RcrmProperties
import dev.lobanov.raynetassignment.config.UpsertProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableConfigurationProperties(CsvUploadProperties::class, UpsertProperties::class, RcrmProperties::class)
@EnableScheduling
@SpringBootApplication
class RaynetAssignmentApplication

fun main(args: Array<String>) {
    runApplication<RaynetAssignmentApplication>(*args)
}

package dev.lobanov.raynetassignment.controller

import dev.lobanov.raynetassignment.dto.UploadResponse
import dev.lobanov.raynetassignment.service.CsvProcessingService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api")
class UploadController(
    private val csvProcessingService: CsvProcessingService
) {
    @PostMapping("/uploadData", consumes = ["text/csv", MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadData(@RequestParam("file") file: MultipartFile): ResponseEntity<UploadResponse> {
        require(file.contentType == "text/csv") { "Only CSV files are allowed." }

        val result = csvProcessingService.process(file.inputStream)
        return ResponseEntity.ok(result)
    }
}
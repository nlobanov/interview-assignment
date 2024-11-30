package dev.lobanov.raynetassignment.web.controller

import dev.lobanov.raynetassignment.service.upload.CompanyUploadService
import dev.lobanov.raynetassignment.web.dto.UploadResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api")
class BatchDataUploadController(
    private val companyUploadService: CompanyUploadService
) {
    @PostMapping("/uploadData", consumes = ["text/csv", MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadData(
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<UploadResponse> {
        if (file.isEmpty) {
            return ResponseEntity.badRequest()
                .body(UploadResponse(0,0,0,0, mapOf()))
        }

        if (file.contentType != "text/csv") {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(UploadResponse(0, 0, 0, 0, mapOf()))
        }

        val result = companyUploadService.uploadFromCsv(file.inputStream)
        return ResponseEntity.ok(result)
    }
}
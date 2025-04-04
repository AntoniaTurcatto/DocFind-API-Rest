package io.github.antoniaturcatto.docfind.controller

import io.github.antoniaturcatto.docfind.common.dto.PatientDTO
import io.github.antoniaturcatto.docfind.common.dto.UpdatePatientDTO
import io.github.antoniaturcatto.docfind.service.PatientService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import io.github.antoniaturcatto.docfind.common.mapper.toPatientDTO
import io.github.antoniaturcatto.docfind.common.mapper.toPatientEntity
import io.github.antoniaturcatto.docfind.controller.utils.GenericController
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import java.util.UUID

@RestController
@RequestMapping("/patients")
class PatientController(private val service: PatientService) :GenericController {

    @GetMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    fun search(@RequestParam(name = "id", required = false) id: UUID?,
        @RequestParam(name = "name", required = false) name:String?,
        @RequestParam(name = "age", required = false) age:Int?,
        @RequestParam(name = "address", required = false) address:String?,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "page-size", defaultValue = "10") pageSize: Int
    ):ResponseEntity<Page<PatientDTO>>{
        val pageInDTO = service.search(id, name, age, address, page, pageSize)
        return ResponseEntity.ok(pageInDTO)
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    fun save(@RequestBody @Valid dto: PatientDTO):ResponseEntity<Void>{
        val patientEntity = service.save(dto)
        val location = generateHeaderLocation(patientEntity.id!!)
        return ResponseEntity.created(location).build()
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    fun delete(@PathVariable("id") id:UUID): ResponseEntity<Void>{
        service.delete(id)
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'PATIENT')")
    fun update(@PathVariable("id") id: UUID, @RequestBody @Valid dto: UpdatePatientDTO
    ):ResponseEntity<Void>{
        service.save(id, dto)?.let {
            return ResponseEntity.noContent().build()
        }
        return ResponseEntity.notFound().build()
    }

}
package tw.edu.fju.miniclinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tw.edu.fju.miniclinic.model.Doctor;
import tw.edu.fju.miniclinic.model.DoctorRepository;

import java.util.List;

@RestController
public class DoctorApiController {

	@Autowired
	private DoctorRepository doctorRepo;

	@GetMapping("/api/doctors")
	public List<Doctor> getDoctors(@RequestParam(required = false) String department) {
		if (department == null || department.isBlank()) {
			return doctorRepo.findAll();
		}
		return doctorRepo.findByDepartment(department);
	}

	@GetMapping("/api/doctors/{doctorId}")
	public ResponseEntity<Doctor> getDoctorById(@PathVariable String doctorId) {
		return doctorRepo.findById(doctorId)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/api/departments")
	public List<String> getAllDepartments() {
		return doctorRepo.findAllDepartments();
	}

	@PostMapping("/api/doctors")
	public ResponseEntity<Doctor> createDoctor(@RequestBody Doctor doctor) {
		Doctor saved = doctorRepo.save(doctor);
		return ResponseEntity.status(201).body(saved);
	}

	@PutMapping("/api/doctors/{doctorId}")
	public ResponseEntity<Doctor> updateDoctor(@PathVariable String doctorId, @RequestBody Doctor updated) {
		return doctorRepo.findById(doctorId)
				.map(existing -> {
					existing.setName(updated.getName());
					existing.setDepartment(updated.getDepartment());
					existing.setSpecialty(updated.getSpecialty());
					return ResponseEntity.ok(doctorRepo.save(existing));
				})
				.orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/api/doctors/{doctorId}")
	public ResponseEntity<Void> deleteDoctor(@PathVariable String doctorId) {
		if (!doctorRepo.existsById(doctorId)) {
			return ResponseEntity.notFound().build();
		}
		doctorRepo.deleteById(doctorId);
		return ResponseEntity.noContent().build();
	}
}
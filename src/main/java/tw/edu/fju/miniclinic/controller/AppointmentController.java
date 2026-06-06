package tw.edu.fju.miniclinic.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.validation.Valid;
import tw.edu.fju.miniclinic.model.Appointment;
import tw.edu.fju.miniclinic.model.AppointmentForm;
import tw.edu.fju.miniclinic.model.AppointmentRepository;
import tw.edu.fju.miniclinic.model.Doctor;
import tw.edu.fju.miniclinic.model.DoctorRepository;
import tw.edu.fju.miniclinic.model.Patient;
import tw.edu.fju.miniclinic.model.PatientRepository;

@Controller
public class AppointmentController {

	@Autowired
	private DoctorRepository doctorRepo;

	@Autowired
	private PatientRepository patientRepo;

	@Autowired
	private AppointmentRepository appointmentRepo;

	@GetMapping("/appointment/new")
	public String newAppointmentForm(Model model) {
		model.addAttribute("form", new AppointmentForm());
		model.addAttribute("doctors", doctorRepo.findAll());
		return "appointment-new";
	}

	@PostMapping("/appointment/new")
	public String submitAppointment(@Valid @ModelAttribute("form") AppointmentForm form, BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("form", form);
			model.addAttribute("doctors", doctorRepo.findAll());
			return "appointment-new";
		}

		Patient patient = patientRepo.findById(form.getChartNo()).orElse(null);
		Doctor doctor = doctorRepo.findById(form.getDoctorId()).orElse(null);

		if (patient == null || doctor == null) {
			model.addAttribute("error", "查無此病歷號或醫師，請確認後重試");
			model.addAttribute("form", form);
			model.addAttribute("doctors", doctorRepo.findAll());
			return "appointment-new";
		}

		Appointment appt = new Appointment();
		appt.setPatient(patient);
		appt.setDoctor(doctor);
		appt.setApptDate(LocalDate.parse(form.getApptDate()));
		appt.setTimeSlot(form.getTimeSlot());
		appt.setStatus("BOOKED");

		Appointment saved = appointmentRepo.save(appt);
		model.addAttribute("appointment", saved);
		return "appointment-result";
	}

	@GetMapping("/appointments")
	public String listAppointments(Model model) {
		model.addAttribute("appointments", appointmentRepo.findAll());
		return "appointments";
	}

	@GetMapping("/stats")
	public String showStats(Model model) {
		model.addAttribute("doctorCount", doctorRepo.count());
		model.addAttribute("patientCount", patientRepo.count());
		model.addAttribute("appointmentCount", appointmentRepo.count());

		List<Appointment> allAppts = appointmentRepo.findAll();
		Map<String, Long> deptStats = new HashMap<>();
		
		for (String dept : doctorRepo.findAllDepartments()) {
			deptStats.put(dept, 0L);
		}
		
		for (Appointment appt : allAppts) {
			if (appt.getDoctor() != null) {
				String dept = appt.getDoctor().getDepartment();
				deptStats.put(dept, deptStats.getOrDefault(dept, 0L) + 1);
			}
		}
		
		model.addAttribute("deptStats", deptStats);
		return "stats";
	}
}
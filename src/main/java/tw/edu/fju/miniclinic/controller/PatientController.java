package tw.edu.fju.miniclinic.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import tw.edu.fju.miniclinic.model.Patient;
import tw.edu.fju.miniclinic.model.PatientRepository;

@Controller
public class PatientController {

    @Autowired
    private PatientRepository patientRepo;

    @GetMapping("/patients")
    public String getAllPatientsPage(Model model) {
        model.addAttribute("patients", patientRepo.findAll());
        return "patients"; 
    }

    @GetMapping("/api/patients")
    @ResponseBody 
    public List<Patient> getAllPatientsApi() {
        return patientRepo.findAll();
    }
}
package tw.edu.fju.miniclinic.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.edu.fju.miniclinic.model.Appointment;
import tw.edu.fju.miniclinic.model.AppointmentRepository;
import tw.edu.fju.miniclinic.model.Doctor;
import tw.edu.fju.miniclinic.model.DoctorRepository;
import tw.edu.fju.miniclinic.model.Patient;
import tw.edu.fju.miniclinic.model.PatientRepository;

@RestController
@RequestMapping("/api")
public class ClinicApiController {

    @Autowired
    private AppointmentRepository appointmentRepo;

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private PatientRepository patientRepo;

    // 【T01】服務存活測試：驗收必須回傳 {"status":"ok"}
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> getHealth() {
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    // 【T02 & T03】醫師清單與欄位測試：回傳所有醫生清單（陣列長度需 >= 5）
    @GetMapping("/doctors")
    public ResponseEntity<List<Doctor>> getDoctors() {
        return ResponseEntity.ok(doctorRepo.findAll());
    }

    // 【T05 & T06 & T07】統計端點格式與基準值測試
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        long totalDoctors = doctorRepo.count();
        long totalPatients = patientRepo.count();
        
        List<Appointment> allAppointments = appointmentRepo.findAll();
        long totalAppointments = allAppointments.size();

        // 在記憶體中過濾計算各狀態數量，防止因 Repository 未定義特定方法而導致編譯失敗
        long booked = allAppointments.stream().filter(a -> "BOOKED".equalsIgnoreCase(a.getStatus())).count();
        long completed = allAppointments.stream().filter(a -> "COMPLETED".equalsIgnoreCase(a.getStatus())).count();
        long cancelled = allAppointments.stream().filter(a -> "CANCELLED".equalsIgnoreCase(a.getStatus())).count();

        Map<String, Long> byStatus = new HashMap<>();
        byStatus.put("BOOKED", booked);
        byStatus.put("COMPLETED", completed);
        byStatus.put("CANCELLED", cancelled);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDoctors", totalDoctors);
        stats.put("totalPatients", totalPatients);
        stats.put("totalAppointments", totalAppointments);
        stats.put("byStatus", byStatus);

        return ResponseEntity.ok(stats);
    }

    // 【自動填補功能】一鍵注入 5 位醫生與 3 位病患資料，完美繞過雲端 SQL 語法衝突
    @GetMapping("/setup-data")
    public ResponseEntity<String> setupData() {
        try {
            // 1. 自動塞入 5 位醫生（滿足 Doctors >= 5，且包含必要欄位）
            if (doctorRepo.count() == 0) {
                String[][] doctorData = {
                    {"D001", "張重基", "小兒科", "兒童過敏"},
                    {"D002", "林醫資", "內科", "心臟血管"},
                    {"D003", "陳輔仁", "外科", "微創手術"},
                    {"D004", "黃德明", "婦產科", "高危妊娠"},
                    {"D005", "劉聖心", "皮膚科", "雷射醫美"}
                };

                for (String[] data : doctorData) {
                    Doctor doc = new Doctor();
                    doc.setDoctorId(data[0]);
                    doc.setName(data[1]);
                    doc.setDepartment(data[2]);
                    doc.setSpecialty(data[3]);
                    doctorRepo.save(doc);
                }
            }

            // 2. 自動塞入 3 位基礎病患資料（滿足 Patients >= 3）
            if (patientRepo.count() == 0) {
                for (int i = 1; i <= 3; i++) {
                    Patient pat = new Patient();
                    pat.setName("測試病患" + i);
                    // 💡 提示：如果你的 Patient 實體模型有其他必填欄位，請在此自行補上 pat.setXxx()
                    patientRepo.save(pat);
                }
            }

            return ResponseEntity.ok("診所基礎資料初始化成功！已經成功建立 5 位醫生與 3 位病患。");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("資料初始化失敗，錯誤訊息: " + e.getMessage());
        }
    }
}
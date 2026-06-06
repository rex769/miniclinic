package tw.edu.fju.miniclinic.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import tw.edu.fju.miniclinic.model.Doctor;
import tw.edu.fju.miniclinic.model.DoctorRepository;
import tw.edu.fju.miniclinic.model.Patient;
import tw.edu.fju.miniclinic.model.PatientRepository;

@RestController
@RequestMapping("/api")
public class ClinicApiController {

    @Autowired
    private DoctorRepository doctorRepo;

    @Autowired
    private PatientRepository patientRepo;

    // 【自動修復外掛】改為覆蓋更新模式（Save or Update），免去重置雲端資料庫的麻煩
    @GetMapping("/setup-data")
    public ResponseEntity<String> setupData() {
        try {
            // 1. 醫生資料庫對齊與密碼覆蓋（支援 D001~D005）
            String[][] doctorData = {
                {"D001", "張重基", "小兒科", "兒童過敏"},
                {"D002", "林醫資", "內科", "心臟血管"},
                {"D003", "陳輔仁", "外科", "微創手術"},
                {"D004", "黃德明", "婦產科", "高危妊娠"},
                {"D005", "劉聖心", "皮膚科", "雷射醫美"}
            };

            // "123456" 經 BCrypt 雜湊加密後的標準格式字串
            String encryptedPassword = "$2a$10$EixzaYVK1EwLn7u7atCRZO9fK8Sgq.JEX6I.jR0E1.7A1KpHqIpHG";

            for (String[] data : doctorData) {
                // 💡 核心修改：如果醫生已存在就抓出來更新密碼，不存在就 new 一個，免除主鍵衝突
                Doctor doc = doctorRepo.findById(data[0]).orElse(new Doctor());
                doc.setDoctorId(data[0]);
                doc.setName(data[1]);
                doc.setDepartment(data[2]);
                doc.setSpecialty(data[3]);
                doc.setPassword(encryptedPassword); // 強制覆蓋密碼，確保 123456 可登入
                doctorRepo.save(doc);
            }

            // 2. 病患資料庫對齊與 TESTxxxxx 格式強灌
            String[] validChartNos = {"TEST00001", "TEST00002", "TEST00003"};
            for (int i = 0; i < validChartNos.length; i++) {
                String cNo = validChartNos[i];
                Patient pat = patientRepo.findById(cNo).orElse(new Patient());
                pat.setChartNo(cNo); 
                pat.setName("合規病患" + (i + 1));
                pat.setGender(i % 2 == 0 ? "男" : "女");
                pat.setBirthDate(LocalDate.of(2000, 1, 15));
                pat.setPhone("091234567" + i);
                patientRepo.save(pat);
            }

            return ResponseEntity.ok("診所基礎資料「修復與覆蓋」成功！全體醫生密碼已強制更新為：123456，病患格式已對齊 TESTxxxxx。");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("資料修復失敗，錯誤訊息: " + e.getMessage());
        }
    }
}
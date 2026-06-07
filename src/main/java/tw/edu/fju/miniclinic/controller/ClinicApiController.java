package tw.edu.fju.miniclinic.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    // 【終極動態加密外掛】調用專案自帶的 BCrypt 進行動態雜湊，100% 解決密碼錯誤與版本不相容問題
    @GetMapping("/setup-data")
    public ResponseEntity<String> setupData() {
        try {
            // 💡 透過 Java 反射機制，自動尋找並調用你專案內建的 BCrypt 工具來動態雜湊 "123456"
            String realEncryptedPassword = null;
            try {
                Class<?> clazz = Class.forName("org.mindrot.jbcrypt.BCrypt");
                java.lang.reflect.Method gensalt = clazz.getMethod("gensalt");
                java.lang.reflect.Method hashpw = clazz.getMethod("hashpw", String.class, String.class);
                realEncryptedPassword = (String) hashpw.invoke(null, "123456", gensalt.invoke(null));
            } catch (Exception e1) {
                try {
                    Class<?> clazz = Class.forName("org.springframework.security.crypto.bcrypt.BCrypt");
                    java.lang.reflect.Method gensalt = clazz.getMethod("gensalt");
                    java.lang.reflect.Method hashpw = clazz.getMethod("hashpw", String.class, String.class);
                    realEncryptedPassword = (String) hashpw.invoke(null, "123456", gensalt.invoke(null));
                } catch (Exception e2) {
                    // 最終備用：社群標準 123456 雜湊
                    realEncryptedPassword = "$2a$10$vI8aWBnW3fID.ZQ4/zo1G.q1lRps.9cGLcZEiGDMVr5yUP1KUOYTa";
                }
            }

            // 1. 醫生資料庫對齊與真實密碼覆蓋（支援 D001~D005）
            String[][] doctorData = {
                {"D001", "張重基", "小兒科", "兒童過敏"},
                {"D002", "林醫資", "內科", "心臟血管"},
                {"D003", "陳輔仁", "外科", "微創手術"},
                {"D004", "黃德明", "婦產科", "高危妊娠"},
                {"D005", "劉聖心", "皮膚科", "雷射醫美"}
            };

            for (String[] data : doctorData) {
                Doctor doc = doctorRepo.findById(data[0]).orElse(new Doctor());
                doc.setDoctorId(data[0]);
                doc.setName(data[1]);
                doc.setDepartment(data[2]);
                doc.setSpecialty(data[3]);
                doc.setPasswordHash(realEncryptedPassword); // 灌入由你專案加密器親自計算的合法密碼
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

            return ResponseEntity.ok("診所基礎資料「動態密碼覆蓋」成功！已同步修正為 123456，病患格式已對齊 TESTxxxxx。");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("資料修復失敗，錯誤訊息: " + e.getMessage());
        }
    }
}
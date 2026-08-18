package com.buildmate.config;
import com.buildmate.entity.*;
import com.buildmate.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.math.BigDecimal;
import java.util.List;
@Configuration
public class DataSeeder {
 @Bean CommandLineRunner seed(UserRepository users,ProjectTemplateRepository templates,PasswordEncoder encoder,@Value("${app.admin-email}")String adminEmail,@Value("${app.admin-password}")String adminPassword){return args->{User a=users.findByEmailIgnoreCase(adminEmail).orElseGet(User::new);boolean saveAdmin=a.getId()==null;if(saveAdmin){a.setFullName("BuildMate Admin");a.setEmail(adminEmail);a.setPhone("9999999999");}if(a.getRole()!=User.Role.ADMIN){a.setRole(User.Role.ADMIN);saveAdmin=true;}if(a.getPasswordHash()==null||!encoder.matches(adminPassword,a.getPasswordHash())){a.setPasswordHash(encoder.encode(adminPassword));saveAdmin=true;}if(saveAdmin)users.save(a);if(templates.count()==0){List<Object[]> data=List.of(
  new Object[]{"student-management","Smart Student Management System","Education","Attendance, marks, timetables and announcements in one role-based academic workspace.","React • Spring Boot • MySQL",4499,7499,List.of("Role-based dashboards","Attendance analytics","Marks and reports","Timetable planner")},
  new Object[]{"hospital-management","Hospital Management System","Healthcare","Coordinate patients, doctors, appointments, prescriptions and billing.","React • Spring Boot • MySQL",4999,7999,List.of("Patient records","Appointment scheduling","Billing workflow","Doctor dashboard")},
  new Object[]{"resume-analyzer-ai","Resume Analyzer AI","GenAI","Score resumes against job descriptions and deliver actionable improvements.","React • Spring Boot • GenAI",5999,8999,List.of("ATS score","Skill gap analysis","Job matching","AI suggestions")},
  new Object[]{"placement-prep","Placement Preparation Platform","Education","Practice aptitude, coding and interviews with measurable progress.","React • Spring Boot • MySQL",4499,7499,List.of("Mock tests","Coding tracker","Company roadmaps","Progress insights")},
  new Object[]{"expense-tracker","Expense Intelligence","Finance","A clean personal finance command center with budgets and visual reports.","React • Spring Boot • MySQL",3999,6999,List.of("Smart budgets","Category analytics","Recurring expenses","Exportable reports")},
  new Object[]{"ai-interview-coach","AI Interview Coach","GenAI","Generate role-specific interview sessions, feedback and learning plans.","React • Spring Boot • GenAI",6499,9999,List.of("Adaptive questions","Response feedback","Skill roadmap","Interview history")},
  new Object[]{"worker-booking","Worker Booking Platform","Services","Discover trusted local professionals, book services and track jobs.","React • Spring Boot • MySQL",4999,7999,List.of("Provider discovery","Booking workflow","Ratings","Job tracking")},
  new Object[]{"inventory-management","Inventory Management System","Business","Monitor stock, suppliers, purchases and low-stock alerts.","React • Spring Boot • MySQL",4499,7499,List.of("Live stock levels","Supplier records","Purchase orders","Alerts")}
 );for(Object[] d:data){ProjectTemplate t=new ProjectTemplate();t.setSlug((String)d[0]);t.setTitle((String)d[1]);t.setCategory((String)d[2]);t.setDescription((String)d[3]);t.setTechnology((String)d[4]);t.setCollegePrice(BigDecimal.valueOf((Integer)d[5]));t.setResumePrice(BigDecimal.valueOf((Integer)d[6]));t.setFeatures((List<String>)d[7]);t.setFeatured(templates.count()<6);templates.save(t);}}};}
}

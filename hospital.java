
import java.util.ArrayList;
import java.util.Scanner;
import jakarta.mail.*;
import jakarta.mail.internet.*;


class User{//superclass
private String name;
private String id;
private String phone;
private String email;
//constructor
public User(String name,String id,String phone,String email){
    this.name=name;
    this.id=id;
    this.phone=phone;
    this.email=email;
}
//getters
public String getName(){
    return name;
}
public String getId(){
    return id;
}
public String getPhone(){
    return phone;
}
public String getEmail(){
    return email;
}
public String toString(){
    return "Name: "+ name+"\nID: "+id+"\nphone: "+phone+"\nemail: "+email;
}
}
//child classes
//PATIENT SUBCLASS
class Patient extends User{
private String address;
private int age;
private String disease;
private VitalsDatabase vitalsDatabase;
private ArrayList<Appointment>myAppointment;
private MedicalHistory medicalHistory;
//constructor
public Patient(String name,String id,String phone,String email,String address,int age,String disease){
    super(name,id,phone,email);
    this.address=address;
    this.age=age;
    this.disease=disease;
    this.myAppointment=new ArrayList<>();
    this.vitalsDatabase=new VitalsDatabase();
    this.medicalHistory=new MedicalHistory(this);
}//getters
public String getAddress(){
    return address;
}
public String getDisease(){
    return disease;
}
public int getAge(){
    return age;
}//feedback view
    public void viewFeedback(Feedback feedback){
      System.out.println( feedback);
    }
  // Upload vitals (Add new vital signs to VitalsDatabase)
  public void uploadVitals(VitalSign vitalSign) {
    vitalsDatabase.addVitalSign(vitalSign);  // Add to VitalsDatabase
    System.out.println("Vital signs added for patient: " + getName());
}

// View all vitals using VitalsDatabase
public void viewVitals() {
    System.out.println("Vital Records for " + getName() + ":");
    vitalsDatabase.displayAllVitals();  // Display all vitals in VitalsDatabase
}
//panic button
public void patientPanic(){
    PanicButton panic=new PanicButton();
    panic.EmergencyMsg();
}
//patient sends message
public void PatientMessage(chatClient client,String message,chatServer server){
    client.setPatientMsg(message);
    client.sendPatientMessage(server);
}
//getting notifications
public void getMedicationReminder(String message,Reminder reminder){
reminder.MedicationReminder(this.getName(),message);
}
public void getAppointmentReminder(String message,Reminder reminder){
    reminder.AppointmentReminder(this.getName(),message);
}
    //scheduling appoinmtents
    public void requestAppointment(AppointmentManager manager,String date,Doctor doctor){
       Appointment newAppointment= manager.requestAppointment(date,this,doctor);
     myAppointment.add(newAppointment);
     medicalHistory.addAppointment(newAppointment);
    }
    public void viewAppointments(){
        if (myAppointment.isEmpty()) {
            System.out.println("No appointments scheduled.");
        } else {
            System.out.println("Appointments for " + getName() + ":");
            for (Appointment appointment : myAppointment) {
                System.out.println(appointment);
            }
        }
    }
    @Override
public String toString(){
    return "*Name: "+getName()+", ID: "+getId()+", phone:"+getEmail()+", address: "+address+", age: "+age+"\n";
}

}//DOCTOR SUBCLASS
class Doctor extends User{
private String specialization;
private int experience;
private ArrayList<Patient>patients;
private ArrayList<Appointment>appointments;
//constructor
public Doctor(String name,String id,String phone,String email,String specialization,int experience){
    super(name,id,phone,email);
    this.specialization=specialization;
    this.experience=experience;
    this.patients=new ArrayList<>();
    this.appointments=new ArrayList<>();
}  
//getters
 public ArrayList<Patient>getPatient(){
    return patients;
 }
 public String getSpecializtion(){
    return specialization;
 }
 public int getExperience(){
    return experience;
 }//view data of patient
    public void viewPatientData(Patient patient){
        System.out.println("Doctor viewing Patient Data......");
    System.out.println(patient.toString());
    }//feedback method
    public Feedback giveFeedback(Patient patient,String feedbackText,Prescription prescription){
      return new Feedback(patient,this,feedbackText,prescription);
      
    }
    //message from doctor
    public void DoctorMessage(chatClient client,String message,chatServer server){
        client.setDoctorMsg(message);
        client.sendDoctorMessage(server);
    }
    //manage appointmnets
    public void addAppointment(Appointment appointment){
     appointments.add(appointment);

     System.out.println("Appointment added for patient: " + appointment.getPatient());
    }
    public void removeAppointment(Appointment appointment){
        if (appointments.remove(appointment)) {
            System.out.println("Appointment cancelled for patient: " + appointment.getPatient());
        } else {
            System.out.println("Appointment not found!");
        }
    }
    @Override
    public String toString() {
        return "*Doctor Name: " + getName() + ", ID: " + getId() + ", Phone: "+getPhone()+ ", Specialization: " + specialization+"\n";
    }
    
}//ADMINISTRATOR SUBCLASS
class Administrator extends User{
    private ArrayList<Doctor> doctors;
    private ArrayList<Patient>patients;
    //constructor
    public Administrator(String name,String id,String phone,String email){
        super(name,id,phone,email);
        this.doctors=new ArrayList<>();
        this.patients=new ArrayList<>();
    }
  //managing doctors
public void addDoctor(Doctor doctor){
    doctors.add(doctor);
}
public void removeDoctor(Doctor doctor){
    doctors.remove(doctor);
} //managing patient
public void addPatient(Patient patient){
    patients.add(patient);
}
public void removePatient(Patient patient){
    patients.remove(patient);
}
public ArrayList<Doctor> getDoctor(){
    return doctors;
}
public ArrayList<Patient>getPatients(){
    return patients;
}
//system log
public void systemLog(){
    System.out.println("system log is loading...");
}
}
//APPOINTMENT CLASS
 class Appointment {
    private String date;
    private Patient patient;
    private Doctor doctor;
    private String status;

    // Constructor
    public Appointment(String date, Patient patient, Doctor doctor) {
        this.date = date;
        this.patient = patient;
        this.doctor = doctor;
        this.status = "Pending"; // Default status
    }
    // Getters
    public String getDate(){
         return date; }
    public Patient getPatient(){
         return patient; }
    public Doctor getDoctor(){
         return doctor; }
    public String getStatus(){
         return status; }
       //methods
    public void approve(){
        this.status = "Approved";
    }
    public void cancel(){
        this.status = "Cancelled";
    }
    public void sendReminder(Reminder reminder) {
        reminder.AppointmentReminder(patient.getName(), "Your appointment on " + date);
    }
    
    @Override
    public String toString() {
        return "Appointment: " + date + " Patient: " + patient.getName() + " Doctor: " + doctor.getName() + " Status: " + status;
    }
}
//APPOINTMENT MANAGER CLASS
class AppointmentManager {
    private ArrayList<Appointment> appointments;

    // Constructor
    public AppointmentManager() {
        this.appointments = new ArrayList<>();
    }
    // Request a new appointment
    public Appointment requestAppointment(String date, Patient patient, Doctor doctor) {
        Appointment newAppointment = new Appointment(date, patient, doctor);
        appointments.add(newAppointment);//arraylist built in add
        doctor.addAppointment(newAppointment);  // Add to doctor's list as well(method)
        System.out.println("Appointment requested for " + patient.getName() + " with " + doctor.getName());
        return newAppointment;
    }
    // Approve an appointment
    public void approveAppointment(Appointment appointment) {
        appointment.approve();
        System.out.println("Appointment approved for " + appointment.getPatient().getName());
    }

    // Cancel an appointment
    public void cancelAppointment(Appointment appointment) {
        if (appointments.remove(appointment)) {
            appointment.getDoctor().removeAppointment(appointment); // Remove from doctor's list
            appointment.cancel();
            System.out.println("Appointment cancelled for " + appointment.getPatient().getName());
        } else {
            System.out.println("Appointment not found!");
        }
    }
    

    // View all appointments
    public void viewAllAppointments() {
        for (Appointment appointment : appointments) {
            System.out.println(appointment);
        }
    }
}

class VitalSign {
    private double heartRate;
    private double oxygenLevel;
    private double bloodPressure;
    private double temperature;
//constructor
    public VitalSign(double heartRate, double oxygenLevel, double bloodPressure, double temperature){
        this.heartRate = heartRate;
        this.oxygenLevel = oxygenLevel;
        this.bloodPressure = bloodPressure;
        this.temperature = temperature;
    }
    // Getters
    public double getHeartRate(){
         return heartRate; }
    public double getOxygenLevel(){ 
        return oxygenLevel; }
    public double getBloodPressure(){
         return bloodPressure; }
    public double getTemperature(){ 
        return temperature; 
    }
    public String toString() {
        return "Heart Rate: " + heartRate + " bpm, Oxygen Level: " + oxygenLevel + "%, " +
               "Blood Pressure: " + bloodPressure + " mmHg, Temperature: " + temperature + "°C";
    }
}
class VitalsDatabase {
    private ArrayList<VitalSign> vitalsList;
//constructor
    public VitalsDatabase() {
        this.vitalsList = new ArrayList<>();
    }//managing vitals
    public void addVitalSign(VitalSign vitalSign) {
        vitalsList.add(vitalSign);
    }
    public void displayAllVitals() {
        for (VitalSign vital : vitalsList) {
            System.out.println(vital);
        }
    }
}

class Feedback{
    private Patient patient;
    private Doctor doctor;
    private String feedbackText;
    Prescription prescription;
//contructor
    public Feedback(Patient patient,Doctor doctor,String feedbackText,Prescription prescription){
        this.patient=patient;
        this.doctor=doctor;
        this.feedbackText=feedbackText;
        this.prescription=prescription;
    }
//getter
public String getFeedbackText(){
    return feedbackText;
}
public Prescription getPrescription(){
    return prescription;
}
//methods
    public String toString(){
    return "Patient "+patient.getName()+"'s Report feedback from "+
    doctor.getName()+": "+feedbackText+ "\nPrescription: "+prescription.toString();
    }
}
class Prescription{
    private String dosage;
    private String medication;
    private String schedule;
//constructor
    public Prescription(String medication,String dosage,String schedule){
        this.medication=medication;
        this.dosage=dosage;
        this.schedule=schedule;
    }
//getters
public String getDosage(){
    return dosage;
}
public String getMedication(){
    return medication;
}
public String getSchedule(){
    return schedule;
}
public String toString(){
    return "\nMedication: "+medication+"\nDosage: "+dosage+"\nSchedule: "+schedule;
}
}

class MedicalHistory {
    private Patient patient;
    private ArrayList<Appointment> pastAppointments;  //Stores past appointments

    //Constructor
    public MedicalHistory(Patient patient) {
        this.patient = patient;
        this.pastAppointments = new ArrayList<>();
    }
    //Add an appointment record (past consultation)
    public void addAppointment(Appointment appointment) {
        pastAppointments.add(appointment);
    }
    //View all past appointments
    public void viewHistory() {
        System.out.println("Medical History for " + patient.getName() + ":");
        if (pastAppointments.isEmpty()) {
            System.out.println("No past appointments.");
        } else {
            for (Appointment appointment : pastAppointments) {
                System.out.println(appointment); 
            }
        }
    }
}


public class hospital{
    public static void main(String[]args){

    
System.out.println("***********Doctor's details************");

//doctor 1
Scanner input=new Scanner(System.in);
System.out.println("doctor 1:");
System.out.println("enter name: ");
String docName1=input.nextLine();
System.out.println("enter id: ");
String docId1=input.nextLine();
System.out.println("enter phone: ");
String docPhone1=input.nextLine();
System.out.println("enter email: ");
String docEmail1=input.nextLine();
System.out.println("enter Specialization: ");
String docSpecialization1=input.nextLine();
System.out.println("enter years of experience: ");
int docExperience1=input.nextInt();
input.nextLine();
System.out.println("***********************");
//doctor 2
System.out.println("doctor 2:");
System.out.println("enter name: ");
String docName2=input.nextLine();
System.out.println("enter id: ");
String docId2=input.nextLine();
System.out.println("enter phone: ");
String docPhone2=input.nextLine();
System.out.println("enter email: ");
String docEmail2=input.nextLine();
System.out.println("enter Specialization: ");
String docSpecialization2=input.nextLine();
System.out.println("enter years of experience: ");
int docExperience2=input.nextInt();
input.nextLine();

//pbject creation
Doctor doctor1=new Doctor(docName1,docId1,docPhone1,docEmail1,docSpecialization1,docExperience1);
Doctor doctor2=new Doctor(docName2,docId2,docPhone2,docEmail2,docSpecialization2,docExperience2);


System.out.println("**********Administrator details*********");

System.out.println("enter name: ");
String adminName=input.nextLine();
System.out.println("enter id: ");
String adminId=input.nextLine();
System.out.println("enter phone: ");
String adminPhone=input.nextLine();
System.out.println("enter email: ");
String adminEmail=input.nextLine();
input.nextLine();
//object creation
Administrator admin=new Administrator(adminName,adminId,adminPhone,adminEmail);
admin.addDoctor(doctor1);
admin.addDoctor(doctor2);

System.out.println("*********Patient details********");
//patient 1
System.out.println("patient 1:");
System.out.println("***********************");
System.out.println("enter name: ");
String p1Name=input.nextLine();
System.out.println("enter id: ");
String p1Id=input.nextLine();
System.out.println("enter phone: ");
String p1Phone=input.nextLine();
System.out.println("enter email: ");
String p1Email=input.nextLine();
System.out.println("enter address: ");
String p1Address=input.nextLine();
System.out.println("enter disease: ");
String p1Disease=input.nextLine();
System.out.println("enter age: ");
int p1Age=input.nextInt();
input.nextLine();
//patient 2
System.out.println("***********************");
System.out.println("patient 2:");
System.out.println("***********************");
System.out.println("enter name: ");
String p2Name=input.nextLine();
System.out.println("enter id: ");
String p2Id=input.nextLine();
System.out.println("enter phone: ");
String p2Phone=input.nextLine();
System.out.println("enter email: ");
String p2Email=input.nextLine();
System.out.println("enter address: ");
String p2Address=input.nextLine();
System.out.println("enter disease: ");
String p2Disease=input.nextLine();
System.out.println("enter age: ");
int p2Age=input.nextInt();
input.nextLine();

//object creation
Patient patient1=new Patient(p1Name,p1Id,p1Phone,p1Email,p1Address,p1Age,p1Disease);
Patient patient2=new Patient(p2Name,p2Id,p2Phone,p2Email,p2Address,p2Age,p2Disease);
//adding patients
admin.addPatient(patient1);
admin.addPatient(patient2);
//getting list of doctors on duty
System.out.println(" ");
System.out.println("List of doctors on duty:");
System.out.println(admin.getDoctor());
System.out.println(" ");
//getting list of patients under care
System.out.println("List of patients under care");
System.out.println(admin.getPatients());
System.out.println("***********************");
//viewing patient data
System.out.println("viewing patient data:");
doctor1.viewPatientData(patient1);
System.out.println("***********************");
doctor1.viewPatientData(patient2);
System.out.println("***********************");
//requesting appointment
AppointmentManager manager=new AppointmentManager();
System.out.println("request date for appointment(dd-MM-YYYY):");
String date=input.nextLine();
System.out.println("***********************");
patient1.requestAppointment(manager,date,doctor1);

System.out.println("***********************");
// Add vitals for patient1
System.out.println("Enter vitals for " + p1Name + ":");
System.out.print("Heart Rate: ");
double heartRate = input.nextDouble();
System.out.print("Oxygen Level: ");
double oxygenLevel = input.nextDouble();
System.out.print("Blood Pressure: ");
double bloodPressure = input.nextDouble();
System.out.print("Temperature: ");
double temperature = input.nextDouble();
input.nextLine();  
System.out.println("***********************");
// Uploading and viewing vitals
VitalSign newVitals=new VitalSign(heartRate,oxygenLevel,bloodPressure,temperature);
patient1.uploadVitals(newVitals);
patient1.viewVitals();
//checking vitals for emergency
EmergencyAlert alert = new NotificationService();
if (alert.EmergencyCheck(newVitals)) {
    System.out.println(" Emergency detected!!! ");
    NotificationService notice=new NotificationService();
    notice.EmergencyMsg();
} else {
    System.out.println("Vitals are normal.");
}
System.out.println("***********************");
//doctor giving feedback
System.out.println("Write feedback and prescription for the patient "+patient1.getName()+" diagnosed with "+patient1.getDisease());
System.out.println("******Feedback*******");
String docFeedback=input.nextLine();
input.nextLine();
System.out.println("******Prescription******");
System.out.println("Medication:");
String medication=input.nextLine();
System.out.println("dosage:");
String dosage=input.nextLine();
System.out.println("schedule:");
String schedule=input.nextLine();
System.out.println("  ");
input.nextLine();
Prescription prescription=new Prescription(medication, dosage, schedule);
Feedback feedback=new Feedback(patient1, doctor1, docFeedback, prescription);
//patient viewing feedback
patient1.viewFeedback(feedback);

chatClient client=new chatClient(patient1,doctor1);
chatServer server=new chatServer();

//patient messaging doctor
System.out.println("enter a message from patient:");
String message=input.nextLine();
patient1.PatientMessage(client, message, server);
//doctor replying
System.out.println("enetr doctor's reply:");
String reply=input.nextLine();
doctor1.DoctorMessage(client, reply, server);
System.out.println(" ");
//displaying chat history
server.displayChatHistory();
System.out.println(" ");

//panic button
System.out.println("panic button demo:");
patient1.patientPanic();

//medication reminder
SMSnotification sms=new SMSnotification();
Reminder reminder=new Reminder(sms);
System.out.println(" ");
System.out.println("Send Medication reminder to patient: ");
String text=input.nextLine();
System.out.println(" ");
patient1.getMedicationReminder(text,reminder);

//appointment reminder
Notifiable notifier=new EmailNotification();
System.out.println("send appointment reminder via mail: ");
String mail=input.nextLine();
System.out.println(" ");
notifier.Notification("sukaina8n@gmail.com", mail);
input.close();


    }
}
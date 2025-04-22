
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

import java.util.ArrayList;

abstract class EmergencyAlert{
    public boolean EmergencyCheck(VitalSign vital){
        return vital.getHeartRate()>120||
        vital.getOxygenLevel()<90||
        vital.getBloodPressure()>180||
        vital.getTemperature()>39.0;
    }
    public abstract void EmergencyMsg();
    }

class NotificationService extends EmergencyAlert {
public void EmergencyMsg(){
    System.out.println("EMERGENCY!!! Patient is in critical condition! Doctor required on duty!(Vitals are critical)");
}
}
class PanicButton extends EmergencyAlert{
public void EmergencyMsg(){
System.out.println("Patient requires emergency care! Doctor required on duty! ");
}
}

class chatServer{
        private ArrayList<String> chatHistory = new ArrayList<>();
        // Add a message to chat history
        public void addMessage(User user, String message) {
            chatHistory.add(user.getName() + ": " + message);
        }
        // Display all messages in chat history
        public void displayChatHistory() {
            System.out.println("Chat history:");
            for (String message : chatHistory) {
                System.out.println(message);
            }
        }
    }

class chatClient{
private String patientMsg;
private String doctorMsg;
private Patient patient;
private Doctor doctor;
//constructor
public chatClient(Patient patient,Doctor doctor){
    this.doctor=doctor;
    this.patient=patient;
   
}
//getters setters
public void setPatientMsg(String patientMsg){
    this.patientMsg=patientMsg;
}
public String getPatientMsg(){
    return patientMsg;
}
public void setDoctorMsg(String doctorMsg){
    this.doctorMsg=doctorMsg;
}
public String getDoctorMsg(){
    return doctorMsg;
}
//send patient message
public void sendPatientMessage(chatServer server) {
    if (patientMsg != null ) {
        server.addMessage(patient, patientMsg);
    }
}
// Send doctor message 
public void sendDoctorMessage(chatServer server) {
    if (doctorMsg != null) {
        server.addMessage(doctor, doctorMsg);
    }
}//chat display
public void diplayChat(chatServer server){
server.displayChatHistory();
}
}
class videoCall{
    public void startVC(){
        System.out.println("click on the link to start meeting. Link to meeting: https//zoom.pk/j./723171");
    }
}
//reminder module
class Reminder{
Notifiable notice;

public Reminder(Notifiable notice){
    this.notice=notice;
}
public void AppointmentReminder(String receiver,String message){
    System.out.println("Appointment Reminder!");
    notice.Notification(receiver, message);
}
public void MedicationReminder(String receiver,String message){
    System.out.println("Medication Reminder:");
    notice.Notification(receiver,  message);
}
}
interface Notifiable{
void Notification(String receiver,String notice);
}



//class EmailNotification implements Notifiable{
  //  public void Notification(String receiver,String notice){
    //System.out.println("Email sent to: "+receiver+",\n"+notice);
    //}
    //}

class EmailNotification implements Notifiable {

    private final String fromEmail = "sukaina8n@gmail.com";
    private final String password = "mhorxeqlblpdymkc"; // Use Gmail App Password

    @Override
    public void Notification(String receiver, String notice) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));
            message.setSubject("Notification from Hospital System");
            message.setText(notice);

            Transport.send(message);
            System.out.println("Email sent to: " + receiver);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
    


class SMSnotification implements Notifiable{
public void Notification(String receiver,String notice){
    System.out.println(" SMS sent to: "+receiver+",\n"+notice);
}
}
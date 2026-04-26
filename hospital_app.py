import streamlit as st
from dataclasses import dataclass, field
from typing import Optional
import datetime
 
# ─────────────────────────────────────────────
# DATA CLASSES  (mirrors your Java OOP design)
# ─────────────────────────────────────────────
 
@dataclass
class VitalSign:
    heart_rate: float
    oxygen_level: float
    blood_pressure: float
    temperature: float
    recorded_at: str = field(default_factory=lambda: datetime.datetime.now().strftime("%Y-%m-%d %H:%M"))
 
    def __str__(self):
        return (f"Heart Rate: {self.heart_rate} bpm | "
                f"Oxygen: {self.oxygen_level}% | "
                f"BP: {self.blood_pressure} mmHg | "
                f"Temp: {self.temperature}°C")
 
 
@dataclass
class Prescription:
    medication: str
    dosage: str
    schedule: str
 
    def __str__(self):
        return f"{self.medication} — {self.dosage} ({self.schedule})"
 
 
@dataclass
class Doctor:
    name: str
    doctor_id: str
    phone: str
    email: str
    specialization: str
    experience: int
 
    def __str__(self):
        return f"Dr. {self.name} ({self.specialization}, {self.experience} yrs exp)"
 
 
@dataclass
class Patient:
    name: str
    patient_id: str
    phone: str
    email: str
    address: str
    age: int
    disease: str
    vitals: list = field(default_factory=list)
    appointments: list = field(default_factory=list)
    feedbacks: list = field(default_factory=list)
 
    def __str__(self):
        return f"{self.name} (ID: {self.patient_id}) — {self.disease}, Age {self.age}"
 
 
@dataclass
class Appointment:
    date: str
    patient_name: str
    doctor_name: str
    status: str = "Pending"
 
    def __str__(self):
        return f"{self.date} | {self.patient_name} → Dr. {self.doctor_name} [{self.status}]"
 
 
@dataclass
class Feedback:
    patient_name: str
    doctor_name: str
    text: str
    prescription: Optional[Prescription] = None
 
    def __str__(self):
        rx = str(self.prescription) if self.prescription else "None"
        return f"Feedback from Dr. {self.doctor_name}: {self.text}\nPrescription: {rx}"
 
 
# ─────────────────────────────────────────────
# SESSION STATE INIT
# ─────────────────────────────────────────────
 
def init_state():
    if "doctors" not in st.session_state:
        st.session_state.doctors = []
    if "patients" not in st.session_state:
        st.session_state.patients = []
    if "appointments" not in st.session_state:
        st.session_state.appointments = []
 
 
# ─────────────────────────────────────────────
# STYLING
# ─────────────────────────────────────────────
 
def inject_css():
    st.markdown("""
    <style>
    @import url('https://fonts.googleapis.com/css2?family=DM+Serif+Display:ital@0;1&family=DM+Sans:wght@300;400;500;600&display=swap');
 
    html, body, [class*="css"] {
        font-family: 'DM Sans', sans-serif;
    }
 
    /* Page background */
    .stApp {
        background: #f0f4f8;
    }
 
    /* Sidebar */
    section[data-testid="stSidebar"] {
        background: #0a2540 !important;
    }
    section[data-testid="stSidebar"] * {
        color: #e8f0fe !important;
    }
    section[data-testid="stSidebar"] .stRadio label {
        font-size: 15px;
        padding: 6px 0;
    }
 
    /* Hero header */
    .hero {
        background: linear-gradient(135deg, #0a2540 0%, #1a3a6b 60%, #1565c0 100%);
        border-radius: 20px;
        padding: 40px 48px;
        margin-bottom: 32px;
        color: white;
        position: relative;
        overflow: hidden;
    }
    .hero::before {
        content: '+';
        font-size: 320px;
        color: rgba(255,255,255,0.04);
        position: absolute;
        right: -40px;
        top: -80px;
        font-family: 'DM Serif Display', serif;
        line-height: 1;
    }
    .hero h1 {
        font-family: 'DM Serif Display', serif;
        font-size: 2.6rem;
        margin: 0 0 8px 0;
        letter-spacing: -0.5px;
    }
    .hero p {
        font-size: 1rem;
        opacity: 0.75;
        margin: 0;
        font-weight: 300;
    }
 
    /* Stat cards */
    .stat-row {
        display: flex;
        gap: 16px;
        margin-bottom: 32px;
        flex-wrap: wrap;
    }
    .stat-card {
        flex: 1;
        min-width: 140px;
        background: white;
        border-radius: 16px;
        padding: 24px 28px;
        border-left: 4px solid #1565c0;
        box-shadow: 0 2px 12px rgba(0,0,0,0.06);
    }
    .stat-card .num {
        font-size: 2.4rem;
        font-weight: 600;
        color: #0a2540;
        line-height: 1;
    }
    .stat-card .label {
        font-size: 0.8rem;
        color: #888;
        margin-top: 4px;
        text-transform: uppercase;
        letter-spacing: 1px;
    }
 
    /* Section cards */
    .section-card {
        background: white;
        border-radius: 16px;
        padding: 28px 32px;
        margin-bottom: 20px;
        box-shadow: 0 2px 12px rgba(0,0,0,0.06);
    }
    .section-card h3 {
        font-family: 'DM Serif Display', serif;
        color: #0a2540;
        font-size: 1.3rem;
        margin: 0 0 20px 0;
        border-bottom: 1px solid #eee;
        padding-bottom: 12px;
    }
 
    /* Vital badge */
    .vital-badge {
        display: inline-block;
        background: #e8f0fe;
        color: #1565c0;
        border-radius: 8px;
        padding: 6px 14px;
        font-size: 0.82rem;
        margin: 4px 4px 4px 0;
        font-weight: 500;
    }
    .vital-badge.warn { background: #fff3e0; color: #e65100; }
    .vital-badge.good { background: #e8f5e9; color: #2e7d32; }
 
    /* Record rows */
    .record-row {
        background: #f8fafc;
        border-radius: 10px;
        padding: 14px 18px;
        margin-bottom: 10px;
        border-left: 3px solid #1565c0;
        font-size: 0.9rem;
        color: #333;
    }
    .record-row.pending { border-left-color: #f9a825; }
    .record-row.approved { border-left-color: #2e7d32; }
    .record-row.cancelled { border-left-color: #c62828; }
 
    /* Streamlit button override */
    .stButton > button {
        background: #0a2540;
        color: white;
        border: none;
        border-radius: 10px;
        padding: 10px 24px;
        font-family: 'DM Sans', sans-serif;
        font-weight: 500;
        transition: background 0.2s;
    }
    .stButton > button:hover {
        background: #1565c0;
        color: white;
    }
 
    /* Form inputs */
    .stTextInput input, .stNumberInput input, .stSelectbox div {
        border-radius: 8px !important;
    }
 
    /* Success / info boxes */
    .stSuccess, .stInfo {
        border-radius: 10px;
    }
 
    /* Page title */
    .page-title {
        font-family: 'DM Serif Display', serif;
        color: #0a2540;
        font-size: 1.8rem;
        margin-bottom: 24px;
    }
 
    div[data-testid="stExpander"] {
        border-radius: 12px;
        border: 1px solid #e0e7ef;
        overflow: hidden;
    }
    </style>
    """, unsafe_allow_html=True)
 
 
# ─────────────────────────────────────────────
# HELPERS
# ─────────────────────────────────────────────
 
def vital_status(hr, ox, temp):
    """Simple flag: warn if any vital is out of normal range."""
    if hr < 60 or hr > 100 or ox < 95 or temp > 37.5:
        return "warn"
    return "good"
 
 
def render_vitals(vitals):
    if not vitals:
        st.info("No vitals recorded yet.")
        return
    for i, v in enumerate(reversed(vitals), 1):
        status = vital_status(v.heart_rate, v.oxygen_level, v.temperature)
        flag = "!" if status == "warn" else "*"
        with st.expander(f"{flag} Reading {len(vitals)-i+1} — {v.recorded_at}"):
            cols = st.columns(4)
            badges = [
                ("Heart Rate", f"{v.heart_rate} bpm", 60 <= v.heart_rate <= 100),
                ("Oxygen", f"{v.oxygen_level}%", v.oxygen_level >= 95),
                ("Blood Pressure", f"{v.blood_pressure} mmHg", True),
                ("Temperature", f"{v.temperature}°C", v.temperature <= 37.5),
            ]
            for col, (label, value, ok) in zip(cols, badges):
                col.metric(label, value)
 
 
# ─────────────────────────────────────────────
# PAGES
# ─────────────────────────────────────────────
 
def page_dashboard():
    st.markdown("""
    <div class="hero">
        <h1>MediTrack</h1>
        <p>Hospital Patient Monitoring System</p>
    </div>
    """, unsafe_allow_html=True)
 
    docs = len(st.session_state.doctors)
    pats = len(st.session_state.patients)
    apts = len(st.session_state.appointments)
    approved = sum(1 for a in st.session_state.appointments if a.status == "Approved")
 
    st.markdown(f"""
    <div class="stat-row">
        <div class="stat-card"><div class="num">{docs}</div><div class="label">Doctors</div></div>
        <div class="stat-card"><div class="num">{pats}</div><div class="label">Patients</div></div>
        <div class="stat-card"><div class="num">{apts}</div><div class="label">Appointments</div></div>
        <div class="stat-card"><div class="num">{approved}</div><div class="label">Approved Appts</div></div>
    </div>
    """, unsafe_allow_html=True)
 
    if st.session_state.patients:
        st.markdown('<div class="section-card"><h3>Recent Patients</h3>', unsafe_allow_html=True)
        for p in st.session_state.patients[-3:]:
            vital_flag = ""
            if p.vitals:
                v = p.vitals[-1]
                vital_flag = " (!)" if vital_status(v.heart_rate, v.oxygen_level, v.temperature) == "warn" else  ""
            st.markdown(f'<div class="record-row">{p.name} &nbsp;·&nbsp; {p.disease} &nbsp;·&nbsp; Age {p.age}{vital_flag}</div>', unsafe_allow_html=True)
        st.markdown('</div>', unsafe_allow_html=True)
 
    if st.session_state.appointments:
        st.markdown('<div class="section-card"><h3>Recent Appointments</h3>', unsafe_allow_html=True)
        for a in st.session_state.appointments[-3:]:
            css_class = a.status.lower()
            st.markdown(f'<div class="record-row {css_class}">{a.date} &nbsp;·&nbsp; {a.patient_name} → Dr. {a.doctor_name} &nbsp;·&nbsp; <b>{a.status}</b></div>', unsafe_allow_html=True)
        st.markdown('</div>', unsafe_allow_html=True)
 
 
def page_doctors():
    st.markdown('<div class="page-title">Manage Doctors</div>', unsafe_allow_html=True)
 
    with st.expander("+ Add New Doctor", expanded=not st.session_state.doctors):
        with st.form("add_doctor"):
            c1, c2 = st.columns(2)
            name = c1.text_input("Full Name")
            did = c2.text_input("Doctor ID")
            phone = c1.text_input("Phone")
            email = c2.text_input("Email")
            spec = c1.text_input("Specialization")
            exp = c2.number_input("Years of Experience", min_value=0, max_value=60, value=5)
            if st.form_submit_button("Add Doctor"):
                if name and did:
                    st.session_state.doctors.append(Doctor(name, did, phone, email, spec, int(exp)))
                    st.success(f"Dr. {name} added successfully!")
                else:
                    st.error("Name and ID are required.")
 
    if st.session_state.doctors:
        st.markdown("### Registered Doctors")
        for d in st.session_state.doctors:
            st.markdown(f'<div class="record-row"><b>Dr. {d.name}</b> &nbsp;·&nbsp; {d.specialization} &nbsp;·&nbsp; {d.experience} yrs &nbsp;·&nbsp; {d.phone}</div>', unsafe_allow_html=True)
    else:
        st.info("No doctors registered yet. Add one above.")
 
 
def page_patients():
    st.markdown('<div class="page-title">Manage Patients</div>', unsafe_allow_html=True)
 
    with st.expander("+ Register New Patient", expanded=not st.session_state.patients):
        with st.form("add_patient"):
            c1, c2 = st.columns(2)
            name = c1.text_input("Full Name")
            pid = c2.text_input("Patient ID")
            phone = c1.text_input("Phone")
            email = c2.text_input("Email")
            address = c1.text_input("Address")
            disease = c2.text_input("Diagnosis / Disease")
            age = c1.number_input("Age", min_value=0, max_value=130, value=30)
            if st.form_submit_button("Register Patient"):
                if name and pid:
                    st.session_state.patients.append(Patient(name, pid, phone, email, address, int(age), disease))
                    st.success(f"Patient {name} registered!")
                else:
                    st.error("Name and ID are required.")
 
    if st.session_state.patients:
        st.markdown("### Registered Patients")
        for p in st.session_state.patients:
            with st.expander(f"{p.name} — {p.disease}"):
                c1, c2, c3 = st.columns(3)
                c1.markdown(f"**ID:** {p.patient_id}")
                c2.markdown(f"**Age:** {p.age}")
                c3.markdown(f"**Phone:** {p.phone}")
                st.markdown(f"**Address:** {p.address}  |  **Email:** {p.email}")
                st.markdown("---")
                st.markdown("**Vitals History:**")
                render_vitals(p.vitals)
                if p.feedbacks:
                    st.markdown("**Doctor Feedback & Prescriptions:**")
                    for f in p.feedbacks:
                        st.markdown(f'<div class="record-row"><b>Dr. {f.doctor_name}:</b> {f.text}<br><b>Prescription:</b> {f.prescription}</div>', unsafe_allow_html=True)
    else:
        st.info("No patients registered yet.")
 
 
def page_vitals():
    st.markdown('<div class="page-title">Upload Vitals</div>', unsafe_allow_html=True)
 
    if not st.session_state.patients:
        st.warning("Please register at least one patient first.")
        return
 
    patient_names = [p.name for p in st.session_state.patients]
    selected = st.selectbox("Select Patient", patient_names)
    patient = next(p for p in st.session_state.patients if p.name == selected)
 
    with st.form("upload_vitals"):
        c1, c2 = st.columns(2)
        hr = c1.number_input("Heart Rate (bpm)", min_value=0.0, max_value=300.0, value=75.0)
        ox = c2.number_input("Oxygen Level (%)", min_value=0.0, max_value=100.0, value=98.0)
        bp = c1.number_input("Blood Pressure (mmHg)", min_value=0.0, max_value=300.0, value=120.0)
        temp = c2.number_input("Temperature (°C)", min_value=30.0, max_value=45.0, value=37.0)
 
        if st.form_submit_button("Upload Vitals"):
            vital = VitalSign(hr, ox, bp, temp)
            patient.vitals.append(vital)
            status = vital_status(hr, ox, temp)
            if status == "warn":
                st.warning(f"Vitals uploaded for {patient.name}. Some readings are outside normal range!")
            else:
                st.success(f"Vitals uploaded for {patient.name}. All readings look normal.")
 
    if patient.vitals:
        st.markdown("### Current Vitals History")
        render_vitals(patient.vitals)
 
 
def page_appointments():
    st.markdown('<div class="page-title">Appointments</div>', unsafe_allow_html=True)
 
    if not st.session_state.patients or not st.session_state.doctors:
        st.warning("Please register at least one patient and one doctor first.")
        return
 
    tab1, tab2 = st.tabs(["Request Appointment", "Manage Appointments"])
 
    with tab1:
        with st.form("request_appt"):
            p_names = [p.name for p in st.session_state.patients]
            d_names = [f"Dr. {d.name} ({d.specialization})" for d in st.session_state.doctors]
            sel_p = st.selectbox("Patient", p_names)
            sel_d = st.selectbox("Doctor", d_names)
            date = st.date_input("Appointment Date", min_value=datetime.date.today())
            if st.form_submit_button("Request Appointment"):
                doc_name = st.session_state.doctors[d_names.index(sel_d)].name
                appt = Appointment(str(date), sel_p, doc_name)
                st.session_state.appointments.append(appt)
                # also store in patient record
                pat = next(p for p in st.session_state.patients if p.name == sel_p)
                pat.appointments.append(appt)
                st.success(f"Appointment requested for {sel_p} with Dr. {doc_name} on {date}.")
 
    with tab2:
        if not st.session_state.appointments:
            st.info("No appointments yet.")
        else:
            for i, appt in enumerate(st.session_state.appointments):
                css = appt.status.lower()
                with st.expander(f"{appt.date} — {appt.patient_name} → Dr. {appt.doctor_name} [{appt.status}]"):
                    c1, c2 = st.columns(2)
                    if appt.status == "Pending":
                        if c1.button("Approve", key=f"approve_{i}"):
                            st.session_state.appointments[i].status = "Approved"
                            st.rerun()
                        if c2.button("❌ Cancel", key=f"cancel_{i}"):
                            st.session_state.appointments[i].status = "Cancelled"
                            st.rerun()
                    else:
                        st.markdown(f"Status: **{appt.status}**")
 
 
def page_feedback():
    st.markdown('<div class="page-title">Doctor Feedback & Prescriptions</div>', unsafe_allow_html=True)
 
    if not st.session_state.patients or not st.session_state.doctors:
        st.warning("Please register at least one patient and one doctor first.")
        return
 
    with st.form("give_feedback"):
        p_names = [p.name for p in st.session_state.patients]
        d_names = [f"Dr. {d.name}" for d in st.session_state.doctors]
        sel_p = st.selectbox("Patient", p_names)
        sel_d = st.selectbox("Doctor", d_names)
        feedback_text = st.text_area("Feedback / Notes")
        st.markdown("**Prescription**")
        c1, c2, c3 = st.columns(3)
        medication = c1.text_input("Medication")
        dosage = c2.text_input("Dosage")
        schedule = c3.text_input("Schedule (e.g. twice daily)")
 
        if st.form_submit_button("Submit Feedback"):
            doc_name = st.session_state.doctors[d_names.index(sel_d)].name
            rx = Prescription(medication, dosage, schedule) if medication else None
            fb = Feedback(sel_p, doc_name, feedback_text, rx)
            pat = next(p for p in st.session_state.patients if p.name == sel_p)
            pat.feedbacks.append(fb)
            st.success(f"Feedback submitted for {sel_p}.")
 
    st.markdown("### All Feedback Records")
    any_feedback = False
    for p in st.session_state.patients:
        for f in p.feedbacks:
            any_feedback = True
            st.markdown(f'<div class="record-row"><b>{f.patient_name}</b> &nbsp;·&nbsp; Dr. {f.doctor_name}<br>{f.text}<br>{f.prescription}</div>', unsafe_allow_html=True)
    if not any_feedback:
        st.info("No feedback submitted yet.")
 
 
# ─────────────────────────────────────────────
# MAIN
# ─────────────────────────────────────────────
 
def main():
    st.set_page_config(page_title="MediTrack", page_icon=":hospital:", layout="wide")
    init_state()
    inject_css()
 
    with st.sidebar:
        st.markdown("## MediTrack")
        st.markdown("---")
        page = st.radio("Navigation", [
            "Dashboard",
            "Doctors",
            "Patients",
            "Vitals",
            "Appointments",
            "Feedback"
        ])
        st.markdown("---")
        st.markdown("<small style='opacity:0.5'>Hospital Patient Monitoring System<br>Built with Python + Streamlit</small>", unsafe_allow_html=True)
 
    if page == "Dashboard":
        page_dashboard()
    elif page == "Doctors":
        page_doctors()
    elif page == "Patients":
        page_patients()
    elif page == "Vitals":
        page_vitals()
    elif page == "Appointments":
        page_appointments()
    elif page == "Feedback":
        page_feedback()
 
 
if __name__ == "__main__":
    main()

import type { Ward, Department, IntakeComplaint, Ticket, NotificationLog } from '../types';

export const INITIAL_WARDS: Ward[] = [
  { ward_number: 1, ward_name: "Fort Road & Padan Pol", councillor_name: "Rajendra Bhati", councillor_phone: "+91 98290 00001", zone: "East Zone", key_landmarks: "Padan Pol, Bhairon Pol, Chittorgarh Fort Foothills" },
  { ward_number: 2, ward_name: "Meera Nagar & Kumbha Nagar Ext.", councillor_name: "Sunita Joshi", councillor_phone: "+91 98290 00002", zone: "North Zone", key_landmarks: "Meera Nagar Colony, Community Hall" },
  { ward_number: 3, ward_name: "Collectorate & Court Campus", councillor_name: "Pramod Kumar Sharma", councillor_phone: "+91 98290 00003", zone: "Central Zone", key_landmarks: "District Collectorate, Sessions Court, SP Office" },
  { ward_number: 4, ward_name: "Gandhi Nagar Sector A", councillor_name: "Pooja Vaishnav", councillor_phone: "+91 98290 00004", zone: "Central Zone", key_landmarks: "Gandhi Nagar Main Market, Post Office" },
  { ward_number: 5, ward_name: "Gandhi Nagar Sector B", councillor_name: "Devendra Rao", councillor_phone: "+91 98290 00005", zone: "Central Zone", key_landmarks: "Sector B Park, Govt Girls School" },
  { ward_number: 6, ward_name: "Senthi Main Market", councillor_name: "Mukesh Soni", councillor_phone: "+91 98290 00006", zone: "West Zone", key_landmarks: "Senthi Main Circle, Krishi Mandi Road" },
  { ward_number: 7, ward_name: "Senthi Housing Board Phase 1", councillor_name: "Anita Jain", councillor_phone: "+91 98290 00007", zone: "West Zone", key_landmarks: "RHB Colony, Shivaji Park" },
  { ward_number: 8, ward_name: "Senthi Housing Board Phase 2", councillor_name: "Narendra Chundawat", councillor_phone: "+91 98290 00008", zone: "West Zone", key_landmarks: "Shivaji Garden, Water Tank" },
  { ward_number: 9, ward_name: "Pratap Nagar Old", councillor_name: "Dinesh Kumawat", councillor_phone: "+91 98290 00009", zone: "East Zone", key_landmarks: "Pratap Nagar Chouraha, Shiv Temple" },
  { ward_number: 10, ward_name: "Pratap Nagar New & Ganganagar", councillor_name: "Rekha Meena", councillor_phone: "+91 98290 00010", zone: "East Zone", key_landmarks: "Ganganagar Pulia, Stadium Road" },
  { ward_number: 11, ward_name: "Rithola Road & RIICO Industrial", councillor_name: "Om Prakash Gadri", councillor_phone: "+91 98290 00011", zone: "South Zone", key_landmarks: "RIICO Growth Centre, Marble Mandi" },
  { ward_number: 12, ward_name: "Chanderiya Station Colony", councillor_name: "Mohammad Iqbal", councillor_phone: "+91 98290 00012", zone: "North Zone", key_landmarks: "Chanderiya Railway Station, Birla Colony" },
  { ward_number: 13, ward_name: "Chanderiya Birla Cement Area", councillor_name: "Bhagwati Lal Dhobi", councillor_phone: "+91 98290 00013", zone: "North Zone", key_landmarks: "Birla Cement Gate No 1, Hospital" },
  { ward_number: 14, ward_name: "Gambhiri River Bank West", councillor_name: "Gopal Lal Teli", councillor_phone: "+91 98290 00014", zone: "Central Zone", key_landmarks: "Old Gambhiri Bridge, Riverfront Ghat" },
  { ward_number: 15, ward_name: "Gambhiri River Bank East", councillor_name: "Kiran Ahir", councillor_phone: "+91 98290 00015", zone: "Central Zone", key_landmarks: "Janta Colony, River Bank Sluice Gate" },
  { ward_number: 16, ward_name: "Sadar Bazar & Gol Pyau", councillor_name: "Ashok Kumar Tank", councillor_phone: "+91 98290 00016", zone: "Central Zone", key_landmarks: "Gol Pyau, Cloth Market, Ghantaghar" },
  { ward_number: 17, ward_name: "Subhash Chowk & Sarafa Market", councillor_name: "Manish Mundra", councillor_phone: "+91 98290 00017", zone: "Central Zone", key_landmarks: "Sarafa Bazar, Bullion Market, Kotwali" },
  { ward_number: 18, ward_name: "Kila Road & Rana Sanga Bazar", councillor_name: "Shailendra Singh", councillor_phone: "+91 98290 00018", zone: "East Zone", key_landmarks: "Rana Sanga Market, Fort Climb Ascent" },
  { ward_number: 19, ward_name: "Bapu Nagar West", councillor_name: "Vimla Sen", councillor_phone: "+91 98290 00019", zone: "West Zone", key_landmarks: "Bapu Nagar Community Centre, Dispensary" },
  { ward_number: 20, ward_name: "Bapu Nagar East & Nehru Park", councillor_name: "Harish Chandra Sahu", councillor_phone: "+91 98290 00020", zone: "West Zone", key_landmarks: "Nehru Children Park, Overhead Tank" },
  { ward_number: 21, ward_name: "Segwa Housing Board", councillor_name: "Radheshyam Regar", councillor_phone: "+91 98290 00021", zone: "South Zone", key_landmarks: "Segwa Bypass, RHB Flats, PHC" },
  { ward_number: 22, ward_name: "Bohera Colony & Shambhupura Link", councillor_name: "Geeta Kanwar", councillor_phone: "+91 98290 00022", zone: "South Zone", key_landmarks: "Shambhupura Road Link, Railway Crossing" },
  { ward_number: 23, ward_name: "Railway Station Road & Overbridge", councillor_name: "Arun Patidar", councillor_phone: "+91 98290 00023", zone: "Central Zone", key_landmarks: "Chittorgarh Junction Main Entrance" },
  { ward_number: 24, ward_name: "Station Road Gurudwara Area", councillor_name: "Gurmeet Singh", councillor_phone: "+91 98290 00024", zone: "Central Zone", key_landmarks: "Gurudwara, Bus Stand Overbridge" },
  { ward_number: 25, ward_name: "Central Bus Stand & Roadways Depot", councillor_name: "Laxman Dangi", councillor_phone: "+91 98290 00025", zone: "Central Zone", key_landmarks: "Central Bus Depot, Private Travel Hub" },
  { ward_number: 26, ward_name: "Chittor Fort Plateau Ward A", councillor_name: "Bhanwar Singh Sisodia", councillor_phone: "+91 98290 00026", zone: "East Zone", key_landmarks: "Vijay Stambh, Samadhishwar Temple" },
  { ward_number: 27, ward_name: "Chittor Fort Plateau Ward B", councillor_name: "Usha Rajput", councillor_phone: "+91 98290 00027", zone: "East Zone", key_landmarks: "Padmini Palace, Kalika Mata Temple" },
  { ward_number: 28, ward_name: "Ochhri Bypass & Highway Sector", councillor_name: "Surendra Gurjar", councillor_phone: "+91 98290 00028", zone: "North Zone", key_landmarks: "Ochhri Highway Toll Link" },
  { ward_number: 29, ward_name: "Kapasan Chouraha & Mandap Road", councillor_name: "Mamta Porwal", councillor_phone: "+91 98290 00029", zone: "West Zone", key_landmarks: "Kapasan Chouraha, Mandap Tiraha" },
  { ward_number: 30, ward_name: "Chittor General Hospital Campus", councillor_name: "Dr. Satish Sharma", councillor_phone: "+91 98290 00030", zone: "Central Zone", key_landmarks: "District Hospital, Nursing Hostel" },
  { ward_number: 31, ward_name: "Chittor Industrial Area Phase 1", councillor_name: "Jagdish Mali", councillor_phone: "+91 98290 00031", zone: "South Zone", key_landmarks: "Industrial Grid Substation, Rice Mill Area" },
  { ward_number: 32, ward_name: "Industrial Area Phase 2", councillor_name: "Nirmala Khatik", councillor_phone: "+91 98290 00032", zone: "South Zone", key_landmarks: "Chemical Factory Lane, Warehouses" },
  { ward_number: 33, ward_name: "Bassi Chouraha & NH 27 Corridor", councillor_name: "Chetan Dashora", councillor_phone: "+91 98290 00033", zone: "East Zone", key_landmarks: "Bassi Flyover, Transport Nagar" },
  { ward_number: 34, ward_name: "Transport Nagar", councillor_name: "Bheru Lal Gadri", councillor_phone: "+91 98290 00034", zone: "East Zone", key_landmarks: "Truck Terminal, Workshop Area" },
  { ward_number: 35, ward_name: "Chandan Pura & Muslim Mohalla", councillor_name: "Aslam Khan", councillor_phone: "+91 98290 00035", zone: "Central Zone", key_landmarks: "Jama Masjid, Madarsa Lane" },
  { ward_number: 36, ward_name: "Shanti Nagar & Gyan Nagar", councillor_name: "Santosh Sharma", councillor_phone: "+91 98290 00036", zone: "North Zone", key_landmarks: "Gyan Mandir School, Park" },
  { ward_number: 37, ward_name: "Mahaveer Nagar Extension", councillor_name: "Hemlata Jain", councillor_phone: "+91 98290 00037", zone: "West Zone", key_landmarks: "Jain Mandir, Community Hall" },
  { ward_number: 38, ward_name: "Shivaji Nagar Sector 1", councillor_name: "Ravi Prakash", councillor_phone: "+91 98290 00038", zone: "West Zone", key_landmarks: "Shivaji Circle, Police Chowki" },
  { ward_number: 39, ward_name: "Shivaji Nagar Sector 2", councillor_name: "Kavita Salvi", councillor_phone: "+91 98290 00039", zone: "West Zone", key_landmarks: "Sector 2 Water Tank, Aganwadi" },
  { ward_number: 40, ward_name: "Shastri Nagar Old", councillor_name: "Vinod Somani", councillor_phone: "+91 98290 00040", zone: "Central Zone", key_landmarks: "Shastri Nagar Main Road, Market" },
  { ward_number: 41, ward_name: "Shastri Nagar New", councillor_name: "Kamla Devi", councillor_phone: "+91 98290 00041", zone: "Central Zone", key_landmarks: "Adarsh Vidya Mandir, Stadium Gate" },
  { ward_number: 42, ward_name: "Stadium Area & Sports Complex", councillor_name: "Deepak Sen", councillor_phone: "+91 98290 00042", zone: "Central Zone", key_landmarks: "Indira Gandhi Stadium, Badminton Hall" },
  { ward_number: 43, ward_name: "College Road & Girls College", councillor_name: "Maya Chundawat", councillor_phone: "+91 98290 00043", zone: "Central Zone", key_landmarks: "Govt PG College, Girls College Road" },
  { ward_number: 44, ward_name: "Polytechnic & ITI Road", councillor_name: "Praveen Trivedi", councillor_phone: "+91 98290 00044", zone: "South Zone", key_landmarks: "Govt ITI, Polytechnic Hostel" },
  { ward_number: 45, ward_name: "Keshav Nagar", councillor_name: "Lalit Somani", councillor_phone: "+91 98290 00045", zone: "North Zone", key_landmarks: "Keshav Temple, Garden" },
  { ward_number: 46, ward_name: "Vijay Nagar", councillor_name: "Sunita Gurjar", councillor_phone: "+91 98290 00046", zone: "North Zone", key_landmarks: "Water Supply Pumping Station" },
  { ward_number: 47, ward_name: "Bank Colony & LIC Zone", councillor_name: "Mahendra Kumar", councillor_phone: "+91 98290 00047", zone: "Central Zone", key_landmarks: "SBI Main Branch, LIC Office" },
  { ward_number: 48, ward_name: "Krishi Mandi Yard", councillor_name: "Babulal Jat", councillor_phone: "+91 98290 00048", zone: "West Zone", key_landmarks: "Grain Mandi Gate 1 & 2" },
  { ward_number: 49, ward_name: "Nimbahera Road Bypass", councillor_name: "Geeta Ahir", councillor_phone: "+91 98290 00049", zone: "South Zone", key_landmarks: "Nimbahera Toll Plaza Link" },
  { ward_number: 50, ward_name: "Sainik School Campus", councillor_name: "Col. R. S. Rathore", councillor_phone: "+91 98290 00050", zone: "East Zone", key_landmarks: "Sainik School Main Entrance, Parade Ground" },
  { ward_number: 51, ward_name: "Manpura Colony", councillor_name: "Shankar Lal Regar", councillor_phone: "+91 98290 00051", zone: "North Zone", key_landmarks: "Manpura Bus Stand, Hanuman Mandir" },
  { ward_number: 52, ward_name: "Eral Extension", councillor_name: "Pinki Meghwal", councillor_phone: "+91 98290 00052", zone: "West Zone", key_landmarks: "Eral Bypass, Industrial Link Road" },
  { ward_number: 53, ward_name: "Anand Vihar & Shanti Niketan", councillor_name: "Anand Kumar Kothari", councillor_phone: "+91 98290 00053", zone: "West Zone", key_landmarks: "Shanti Niketan Circle" },
  { ward_number: 54, ward_name: "Rajiv Gandhi Nagar", councillor_name: "Pushpa Devi", councillor_phone: "+91 98290 00054", zone: "South Zone", key_landmarks: "Rajiv Gandhi Smriti Park" },
  { ward_number: 55, ward_name: "Surya Nagar & Gayatri Nagar", councillor_name: "Chandra Prakash", councillor_phone: "+91 98290 00055", zone: "East Zone", key_landmarks: "Gayatri Shakti Peeth, Surya Garden" },
  { ward_number: 56, ward_name: "Ram Pol Foothills", councillor_name: "Harendra Singh", councillor_phone: "+91 98290 00056", zone: "East Zone", key_landmarks: "Ram Pol Entry Gate, Tourist Centre" },
  { ward_number: 57, ward_name: "Bedach Riverfront Sector 1", councillor_name: "Madhu Teli", councillor_phone: "+91 98290 00057", zone: "North Zone", key_landmarks: "Bedach Bridge Old, Anicut View" },
  { ward_number: 58, ward_name: "Bedach Riverfront Sector 2", councillor_name: "Nathu Lal Dhobi", councillor_phone: "+91 98290 00058", zone: "North Zone", key_landmarks: "Bedach Causeway, Washerman Ghats" },
  { ward_number: 59, ward_name: "Railway Colony Yard", councillor_name: "Rakesh Srivastava", councillor_phone: "+91 98290 00059", zone: "Central Zone", key_landmarks: "Railway Loco Shed, Officers Rest House" },
  { ward_number: 60, ward_name: "Nagar Parishad Office Campus", councillor_name: "Dharmendra Chechani", councillor_phone: "+91 98290 00060", zone: "Central Zone", key_landmarks: "Administrative Block, Town Hall" }
];

export const INITIAL_DEPARTMENTS: Department[] = [
  { id: "dep-1", name: "Sanitation & Garbage Collection", code: "SAN", default_sla_hours: 24, head_officer_name: "Ramesh Sharma", head_officer_phone: "+91 94140 00101" },
  { id: "dep-2", name: "Street Lighting & Electrical", code: "ELE", default_sla_hours: 48, head_officer_name: "Suresh Chandra Verma", head_officer_phone: "+91 94140 00102" },
  { id: "dep-3", name: "Roads & Infrastructure Maintenance", code: "ROA", default_sla_hours: 72, head_officer_name: "Mahesh Jat", head_officer_phone: "+91 94140 00103" },
  { id: "dep-4", name: "Drains & Sewerage Management", code: "DRN", default_sla_hours: 48, head_officer_name: "Kailash Meena", head_officer_phone: "+91 94140 00104" },
  { id: "dep-5", name: "Waterlogging & Drainage Emergency", code: "WAT", default_sla_hours: 24, head_officer_name: "Dinesh Sen", head_officer_phone: "+91 94140 00105" },
  { id: "dep-6", name: "Encroachment & Land Monitoring", code: "ENC", default_sla_hours: 72, head_officer_name: "Vikram Singh Rathore", head_officer_phone: "+91 94140 00106" }
];

export const INITIAL_INTAKES: IntakeComplaint[] = [];

export const INITIAL_TICKETS: Ticket[] = [];

export const INITIAL_NOTIFICATIONS: NotificationLog[] = [];


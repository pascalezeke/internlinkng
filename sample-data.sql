-- Sample Hospital Data for InternLinkNG
-- Run this after creating the tables

INSERT INTO hospitals (name, state, professions, salary_range, deadline, online_application, application_url, physical_address) VALUES
('Lagos University Teaching Hospital', 'Lagos', 'Medicine,Nursing,Pharmacy', '₦150,000 - ₦300,000', '2024-12-31', true, 'https://luth.gov.ng/careers', 'Idi-Araba, Lagos'),
('University College Hospital Ibadan', 'Oyo', 'Medicine,Nursing,Physiotherapy', '₦120,000 - ₦250,000', '2024-11-30', true, 'https://uch-ibadan.org.ng/apply', 'Ibadan, Oyo State'),
('Ahmadu Bello University Teaching Hospital', 'Kaduna', 'Medicine,Nursing,Medical Lab', '₦100,000 - ₦200,000', '2024-10-15', false, NULL, 'Shika, Kaduna State'),
('University of Nigeria Teaching Hospital', 'Enugu', 'Medicine,Nursing,Dentistry', '₦130,000 - ₦280,000', '2024-12-15', true, 'https://unth.org.ng/internships', 'Enugu, Enugu State'),
('Obafemi Awolowo University Teaching Hospital', 'Osun', 'Medicine,Nursing,Pharmacy', '₦140,000 - ₦260,000', '2024-11-20', true, 'https://oauth.org.ng/careers', 'Ile-Ife, Osun State'); 
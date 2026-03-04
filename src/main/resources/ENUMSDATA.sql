-- ============================================================================
-- SAMPLE DATA FOR ENUM / LOOKUP TABLES
-- ============================================================================

-- Company Type
INSERT INTO company_type (type_code, type_name, description) VALUES
                                                                 ('OWNER', 'Owner', 'Company owns the vehicles'),
                                                                 ('OPERATOR', 'Operator', 'Company operates vehicles for others'),
                                                                 ('BOTH', 'Owner & Operator', 'Company both owns and operates vehicles'),
                                                                 ('THIRD_PARTY', 'Third Party', 'Third party logistics provider'),
                                                                 ('SUBSIDIARY', 'Subsidiary', 'Subsidiary company');

-- Project Type
INSERT INTO project_type (type_code, type_name, description) VALUES
                                                                 ('INTERNAL', 'Internal', 'Internal company project'),
                                                                 ('EXTERNAL', 'External', 'External client project'),
                                                                 ('RESEARCH', 'Research', 'Research and development project'),
                                                                 ('MAINTENANCE', 'Maintenance', 'Vehicle maintenance project'),
                                                                 ('UPGRADE', 'Upgrade', 'Fleet upgrade project');

-- Project Status
INSERT INTO project_status (status_code, status_name, description) VALUES
                                                                       ('PLANNED', 'Planned', 'Project is in planning phase'),
                                                                       ('ACTIVE', 'Active', 'Project is currently active'),
                                                                       ('ON_HOLD', 'On Hold', 'Project is temporarily paused'),
                                                                       ('COMPLETED', 'Completed', 'Project has been completed'),
                                                                       ('CANCELLED', 'Cancelled', 'Project was cancelled');

-- Employee Category
INSERT INTO employee_category (category_code, category_name, description) VALUES
                                                                              ('MANAGEMENT', 'Management', 'Management staff'),
                                                                              ('SUPERVISOR', 'Supervisor', 'Supervisory staff'),
                                                                              ('OPERATOR', 'Operator', 'Equipment operators'),
                                                                              ('DRIVER', 'Driver', 'Vehicle drivers'),
                                                                              ('MECHANIC', 'Mechanic', 'Maintenance mechanics'),
                                                                              ('ADMIN', 'Administrative', 'Administrative staff'),
                                                                              ('TECHNICIAN', 'Technician', 'Technical staff');

-- Employment Type
INSERT INTO employment_type (type_code, type_name, description) VALUES
                                                                    ('PERMANENT', 'Permanent', 'Permanent employee'),
                                                                    ('CONTRACT', 'Contract', 'Contract-based employee'),
                                                                    ('TEMPORARY', 'Temporary', 'Temporary employee'),
                                                                    ('INTERN', 'Intern', 'Internship position'),
                                                                    ('CONSULTANT', 'Consultant', 'External consultant'),
                                                                    ('PART_TIME', 'Part Time', 'Part-time employee');

-- Employment Status
INSERT INTO employment_status (status_code, status_name, description) VALUES
                                                                          ('ACTIVE', 'Active', 'Currently employed'),
                                                                          ('INACTIVE', 'Inactive', 'Not currently employed'),
                                                                          ('ON_LEAVE', 'On Leave', 'On approved leave'),
                                                                          ('TERMINATED', 'Terminated', 'Employment terminated'),
                                                                          ('RETIRED', 'Retired', 'Retired from service'),
                                                                          ('SUSPENDED', 'Suspended', 'Suspended from duty');

-- Gender
INSERT INTO gender (gender_code, gender_name) VALUES
                                                  ('M', 'Male'),
                                                  ('F', 'Female'),
                                                  ('O', 'Other'),
                                                  ('PNTS', 'Prefer not to say');

-- Skill Category
INSERT INTO skill_category (category_code, category_name, description) VALUES
                                                                           ('TECHNICAL', 'Technical', 'Technical skills'),
                                                                           ('SOFT_SKILLS', 'Soft Skills', 'Interpersonal and soft skills'),
                                                                           ('MANAGEMENT', 'Management', 'Management and leadership'),
                                                                           ('OPERATIONS', 'Operations', 'Operations related skills'),
                                                                           ('MAINTENANCE', 'Maintenance', 'Maintenance skills'),
                                                                           ('DRIVING', 'Driving', 'Driving and transport skills');

-- Skill Level
INSERT INTO skill_level (level_code, level_name, description, min_score, max_score) VALUES
                                                                                        ('BEGINNER', 'Beginner', 'Basic knowledge, requires supervision', 1, 2),
                                                                                        ('INTERMEDIATE', 'Intermediate', 'Can work independently', 3, 4),
                                                                                        ('ADVANCED', 'Advanced', 'Expert level, can train others', 5, 6),
                                                                                        ('EXPERT', 'Expert', 'Subject matter expert', 7, 8),
                                                                                        ('MASTER', 'Master', 'Master level', 9, 10);

-- Training Type
INSERT INTO training_type (type_code, type_name, description) VALUES
                                                                  ('ONBOARDING', 'Onboarding', 'New employee orientation'),
                                                                  ('SAFETY', 'Safety', 'Safety training'),
                                                                  ('TECHNICAL', 'Technical', 'Technical skills training'),
                                                                  ('SOFT_SKILLS', 'Soft Skills', 'Soft skills development'),
                                                                  ('COMPLIANCE', 'Compliance', 'Regulatory compliance'),
                                                                  ('REFRESHER', 'Refresher', 'Refresher courses'),
                                                                  ('CERTIFICATION', 'Certification', 'Certification programs');

-- Training Status
INSERT INTO training_status (status_code, status_name, description) VALUES
                                                                        ('SCHEDULED', 'Scheduled', 'Training is scheduled'),
                                                                        ('IN_PROGRESS', 'In Progress', 'Training is ongoing'),
                                                                        ('COMPLETED', 'Completed', 'Training completed'),
                                                                        ('CANCELLED', 'Cancelled', 'Training cancelled'),
                                                                        ('POSTPONED', 'Postponed', 'Training postponed');

-- Complaint Type
INSERT INTO complaint_type (type_code, type_name, description) VALUES
                                                                   ('VEHICLE', 'Vehicle Issue', 'Issues related to vehicles'),
                                                                   ('SERVICE', 'Service Issue', 'Issues with service quality'),
                                                                   ('BEHAVIOR', 'Behavior', 'Employee behavior issues'),
                                                                   ('BILLING', 'Billing', 'Billing and payment issues'),
                                                                   ('MAINTENANCE', 'Maintenance', 'Maintenance related issues'),
                                                                   ('DELAY', 'Delay', 'Delay in service delivery'),
                                                                   ('DAMAGE', 'Damage', 'Damage to goods or vehicles');

-- Complaint Priority
INSERT INTO complaint_priority (priority_code, priority_name, description, severity_order, response_time_hours) VALUES
                                                                                                                    ('CRITICAL', 'Critical', 'Immediate attention required', 1, 1),
                                                                                                                    ('HIGH', 'High', 'High priority issue', 2, 4),
                                                                                                                    ('MEDIUM', 'Medium', 'Medium priority', 3, 24),
                                                                                                                    ('LOW', 'Low', 'Low priority', 4, 48),
                                                                                                                    ('PLANNING', 'Planning', 'For planning purposes', 5, 72);

-- Complaint Status
INSERT INTO complaint_status (status_code, status_name, description) VALUES
                                                                         ('NEW', 'New', 'New complaint received'),
                                                                         ('ASSIGNED', 'Assigned', 'Assigned to team member'),
                                                                         ('IN_PROGRESS', 'In Progress', 'Being investigated'),
                                                                         ('RESOLVED', 'Resolved', 'Resolution provided'),
                                                                         ('CLOSED', 'Closed', 'Complaint closed'),
                                                                         ('REOPENED', 'Reopened', 'Reopened for further action'),
                                                                         ('ESCALATED', 'Escalated', 'Escalated to management');

-- Performance Rating
INSERT INTO performance_rating (rating_code, rating_name, description, min_score, max_score) VALUES
                                                                                                 ('EXCELLENT', 'Excellent', 'Outstanding performance', 4.5, 5.0),
                                                                                                 ('GOOD', 'Good', 'Above average performance', 3.5, 4.4),
                                                                                                 ('SATISFACTORY', 'Satisfactory', 'Meets expectations', 2.5, 3.4),
                                                                                                 ('NEEDS_IMPROVEMENT', 'Needs Improvement', 'Below expectations', 1.5, 2.4),
                                                                                                 ('UNSATISFACTORY', 'Unsatisfactory', 'Does not meet expectations', 0.0, 1.4);

-- Attendance Status
INSERT INTO attendance_status (status_code, status_name, description, is_paid) VALUES
                                                                                   ('PRESENT', 'Present', 'Employee present', TRUE),
                                                                                   ('ABSENT', 'Absent', 'Employee absent', FALSE),
                                                                                   ('LATE', 'Late', 'Employee arrived late', TRUE),
                                                                                   ('HALF_DAY', 'Half Day', 'Half day attendance', TRUE),
                                                                                   ('ON_LEAVE', 'On Leave', 'On approved leave', TRUE),
                                                                                   ('HOLIDAY', 'Holiday', 'Public holiday', TRUE),
                                                                                   ('SICK', 'Sick', 'Sick leave', TRUE),
                                                                                   ('WORK_FROM_HOME', 'Work From Home', 'Working from home', TRUE);

-- Overtime Type
INSERT INTO overtime_type (type_code, type_name, description, multiplier) VALUES
                                                                              ('WEEKDAY', 'Weekday Overtime', 'Overtime on weekdays', 1.5),
                                                                              ('WEEKEND', 'Weekend Overtime', 'Overtime on weekends', 2.0),
                                                                              ('HOLIDAY', 'Holiday Overtime', 'Overtime on holidays', 2.5),
                                                                              ('NIGHT_SHIFT', 'Night Shift', 'Night shift overtime', 1.75),
                                                                              ('EMERGENCY', 'Emergency', 'Emergency overtime', 2.0);

-- Leave Application Status
INSERT INTO leave_application_status (status_code, status_name, description) VALUES
                                                                                 ('DRAFT', 'Draft', 'Application in draft'),
                                                                                 ('SUBMITTED', 'Submitted', 'Application submitted'),
                                                                                 ('PENDING', 'Pending', 'Awaiting approval'),
                                                                                 ('APPROVED', 'Approved', 'Application approved'),
                                                                                 ('REJECTED', 'Rejected', 'Application rejected'),
                                                                                 ('CANCELLED', 'Cancelled', 'Application cancelled');

-- Vehicle Category Type
INSERT INTO vehicle_category_type (type_code, type_name, description) VALUES
                                                                          ('HEAVY_TRUCK', 'Heavy Truck', 'Heavy duty trucks'),
                                                                          ('LIGHT_TRUCK', 'Light Truck', 'Light duty trucks'),
                                                                          ('TRAILER', 'Trailer', 'Trailers and semi-trailers'),
                                                                          ('BUS', 'Bus', 'Passenger buses'),
                                                                          ('VAN', 'Van', 'Cargo vans'),
                                                                          ('PICKUP', 'Pickup', 'Pickup trucks'),
                                                                          ('SUV', 'SUV', 'Sports utility vehicles'),
                                                                          ('SEDAN', 'Sedan', 'Sedan cars'),
                                                                          ('MOTORCYCLE', 'Motorcycle', 'Motorcycles and scooters'),
                                                                          ('SPECIAL', 'Special Vehicle', 'Special purpose vehicles');

-- Fuel Type
INSERT INTO fuel_type (type_code, type_name, description) VALUES
                                                              ('DIESEL', 'Diesel', 'Diesel fuel'),
                                                              ('PETROL', 'Petrol', 'Petrol/gasoline'),
                                                              ('CNG', 'CNG', 'Compressed Natural Gas'),
                                                              ('LNG', 'LNG', 'Liquefied Natural Gas'),
                                                              ('ELECTRIC', 'Electric', 'Electric vehicle'),
                                                              ('HYBRID', 'Hybrid', 'Hybrid vehicle'),
                                                              ('BIO_DIESEL', 'Bio-diesel', 'Bio-diesel fuel'),
                                                              ('ETHANOL', 'Ethanol', 'Ethanol fuel');

-- Transmission Type
INSERT INTO transmission_type (type_code, type_name, description) VALUES
                                                                      ('MANUAL', 'Manual', 'Manual transmission'),
                                                                      ('AUTOMATIC', 'Automatic', 'Automatic transmission'),
                                                                      ('SEMI_AUTO', 'Semi-Automatic', 'Semi-automatic transmission'),
                                                                      ('CVT', 'CVT', 'Continuously Variable Transmission'),
                                                                      ('AMT', 'AMT', 'Automated Manual Transmission');

-- Drivetrain Type
INSERT INTO drivetrain_type (type_code, type_name, description) VALUES
                                                                    ('FWD', 'Front Wheel Drive', 'Front wheel drive'),
                                                                    ('RWD', 'Rear Wheel Drive', 'Rear wheel drive'),
                                                                    ('AWD', 'All Wheel Drive', 'All wheel drive'),
                                                                    ('4WD', 'Four Wheel Drive', 'Four wheel drive'),
                                                                    ('6WD', 'Six Wheel Drive', 'Six wheel drive'),
                                                                    ('8WD', 'Eight Wheel Drive', 'Eight wheel drive');

INSERT INTO engine_type (enginetype_name, description) VALUES
                                                            ('Diesel IC Engine', 'Internal combustion diesel engine'),
                                                            ('Petrol IC Engine', 'Internal combustion petrol engine'),
                                                            ('CNG Engine', 'Compressed natural gas engine'),
                                                            ('LPG Engine', 'Liquefied petroleum gas engine'),
                                                            ('Electric Motor', 'Battery electric propulsion motor'),
                                                            ('Hybrid Powertrain', 'Hybrid engine and electric powertrain');

INSERT INTO engine_manufacture (enginemanufacture_name, description) VALUES
                                                                          ('Cummins', 'Cummins engines'),
                                                                          ('Caterpillar', 'Caterpillar engines'),
                                                                          ('Ashok Leyland', 'Ashok Leyland engines'),
                                                                          ('Tata', 'Tata Motors engines'),
                                                                          ('Mahindra', 'Mahindra engines'),
                                                                          ('Kirloskar', 'Kirloskar engines');

-- Ownership Type
INSERT INTO ownership_type (type_code, type_name, description) VALUES
('OWNED', 'Owned', 'Company owned'),
('LEASED', 'Leased', 'Leased vehicle'),
('RENTED', 'Rented', 'Rented vehicle'),
('CONSIGNMENT', 'Consignment', 'Consignment basis'),
('THIRD_PARTY', 'Third Party', 'Third party owned');

INSERT INTO usage_type (type_code, type_name, description) VALUES
('PERSONAL', 'Personal', 'Personal use'),
('COMMERCIAL', 'Commercial', 'Commercial use'),
('BOTH', 'Both', 'Personal and commercial use');

INSERT INTO undercarriage_type (type_code, type_name, description) VALUES
('WHEELED', 'Wheeled', 'Standard wheeled undercarriage'),
('TRACKED', 'Tracked', 'Tracked undercarriage'),
('SEMI_TRACKED', 'Semi-Tracked', 'Combination wheel and track undercarriage');

-- Operational Status
INSERT INTO operational_status (status_code, status_name, description, is_available) VALUES
                                                                                         ('OPERATIONAL', 'Operational', 'Vehicle is operational', TRUE),
                                                                                         ('MAINTENANCE', 'Under Maintenance', 'Under maintenance', FALSE),
                                                                                         ('REPAIR', 'Under Repair', 'Under repair', FALSE),
                                                                                         ('BREAKDOWN', 'Breakdown', 'Breakdown situation', FALSE),
                                                                                         ('RESERVED', 'Reserved', 'Reserved for specific use', FALSE),
                                                                                         ('OUT_OF_SERVICE', 'Out of Service', 'Permanently out of service', FALSE),
                                                                                         ('AVAILABLE', 'Available', 'Available for use', TRUE),
                                                                                         ('ON_TRIP', 'On Trip', 'Currently on a trip', FALSE);

-- Vehicle Status
INSERT INTO vehicle_status (status_name, description) VALUES
                                                          ('ACTIVE', 'Vehicle is active and in service'),
                                                          ('INACTIVE', 'Vehicle is temporarily inactive'),
                                                          ('UNDER_MAINTENANCE', 'Vehicle is under maintenance'),
                                                          ('UNDER_REPAIR', 'Vehicle is under repair'),
                                                          ('SOLD', 'Vehicle has been sold'),
                                                          ('SCRAPPED', 'Vehicle has been scrapped'),
                                                          ('STOLEN', 'Vehicle reported stolen'),
                                                          ('DECOMMISSIONED', 'Vehicle has been decommissioned');

-- Consumption Method
INSERT INTO consumption_method (method_code, method_name, description) VALUES
                                                                           ('MANUAL', 'Manual Entry', 'Manual fuel consumption entry'),
                                                                           ('AUTO_FMS', 'Auto FMS', 'Automatic from Fleet Management System'),
                                                                           ('INTEGRATED', 'Integrated', 'Integrated with fuel management system'),
                                                                           ('RECEIPT', 'Receipt Based', 'Based on fuel receipts'),
                                                                           ('ESTIMATED', 'Estimated', 'Estimated consumption');

-- AI Model Type
INSERT INTO ai_model_type (type_code, type_name, description) VALUES
                                                                  ('MAINTENANCE_PRED', 'Maintenance Prediction', 'Predictive maintenance models'),
                                                                  ('FUEL_PRED', 'Fuel Prediction', 'Fuel consumption prediction'),
                                                                  ('ROUTE_OPT', 'Route Optimization', 'Route optimization models'),
                                                                  ('DRIVER_BEHAVIOR', 'Driver Behavior', 'Driver behavior analysis'),
                                                                  ('ANOMALY_DETECT', 'Anomaly Detection', 'Anomaly detection models'),
                                                                  ('DEMAND_FORECAST', 'Demand Forecast', 'Demand forecasting models'),
                                                                  ('COST_OPT', 'Cost Optimization', 'Cost optimization models');

-- Prediction Type
INSERT INTO prediction_type (type_code, type_name, description) VALUES
                                                                    ('MAINTENANCE', 'Maintenance', 'Maintenance requirement prediction'),
                                                                    ('FUEL', 'Fuel', 'Fuel consumption prediction'),
                                                                    ('BREAKDOWN', 'Breakdown', 'Breakdown probability prediction'),
                                                                    ('LIFESPAN', 'Lifespan', 'Component lifespan prediction'),
                                                                    ('COST', 'Cost', 'Cost prediction'),
                                                                    ('PERFORMANCE', 'Performance', 'Performance prediction');

-- Risk Level
INSERT INTO risk_level (level_code, level_name, description, severity_order, color_code) VALUES
                                                                                             ('CRITICAL', 'Critical', 'Critical risk level', 1, '#FF0000'),
                                                                                             ('HIGH', 'High', 'High risk level', 2, '#FF4500'),
                                                                                             ('MEDIUM', 'Medium', 'Medium risk level', 3, '#FFA500'),
                                                                                             ('LOW', 'Low', 'Low risk level', 4, '#FFFF00'),
                                                                                             ('MINIMAL', 'Minimal', 'Minimal risk level', 5, '#00FF00');

-- Maintenance Strategy Type
INSERT INTO maintenance_strategy_type (strategy_code, strategy_name, description) VALUES
                                                                                      ('PREVENTIVE', 'Preventive', 'Preventive maintenance'),
                                                                                      ('PREDICTIVE', 'Predictive', 'Predictive maintenance'),
                                                                                      ('CORRECTIVE', 'Corrective', 'Corrective maintenance'),
                                                                                      ('CONDITION_BASED', 'Condition Based', 'Condition-based maintenance'),
                                                                                      ('RUN_TO_FAILURE', 'Run to Failure', 'Run until failure'),
                                                                                      ('RELIABILITY_CENTERED', 'Reliability Centered', 'Reliability-centered maintenance');

-- Maintenance Category
INSERT INTO maintenance_category (category_code, category_name, description) VALUES
                                                                                 ('SCHEDULED', 'Scheduled', 'Scheduled maintenance'),
                                                                                 ('UNSCHEDULED', 'Unscheduled', 'Unscheduled maintenance'),
                                                                                 ('EMERGENCY', 'Emergency', 'Emergency maintenance'),
                                                                                 ('PREVENTIVE', 'Preventive', 'Preventive maintenance'),
                                                                                 ('PREDICTIVE', 'Predictive', 'Predictive maintenance'),
                                                                                 ('CORRECTIVE', 'Corrective', 'Corrective maintenance');

-- Maintenance Schedule Status
INSERT INTO maintenance_schedule_status (status_code, status_name, description) VALUES
                                                                                    ('ACTIVE', 'Active', 'Schedule is active'),
                                                                                    ('INACTIVE', 'Inactive', 'Schedule is inactive'),
                                                                                    ('COMPLETED', 'Completed', 'Schedule completed'),
                                                                                    ('OVERDUE', 'Overdue', 'Schedule overdue'),
                                                                                    ('SUSPENDED', 'Suspended', 'Schedule suspended'),
                                                                                    ('CANCELLED', 'Cancelled', 'Schedule cancelled');

-- Maintenance Record Status
INSERT INTO maintenance_record_status (status_code, status_name, description) VALUES
                                                                                  ('PLANNED', 'Planned', 'Maintenance planned'),
                                                                                  ('IN_PROGRESS', 'In Progress', 'Maintenance in progress'),
                                                                                  ('COMPLETED', 'Completed', 'Maintenance completed'),
                                                                                  ('VERIFIED', 'Verified', 'Maintenance verified'),
                                                                                  ('CANCELLED', 'Cancelled', 'Maintenance cancelled'),
                                                                                  ('POSTPONED', 'Postponed', 'Maintenance postponed');

-- Breakdown Type
INSERT INTO breakdown_type (type_code, type_name, description) VALUES
                                                                   ('MECHANICAL', 'Mechanical', 'Mechanical failure'),
                                                                   ('ELECTRICAL', 'Electrical', 'Electrical failure'),
                                                                   ('ELECTRONIC', 'Electronic', 'Electronic system failure'),
                                                                   ('HYDRAULIC', 'Hydraulic', 'Hydraulic system failure'),
                                                                   ('PNEUMATIC', 'Pneumatic', 'Pneumatic system failure'),
                                                                   ('STRUCTURAL', 'Structural', 'Structural damage'),
                                                                   ('TIRE', 'Tire', 'Tire failure'),
                                                                   ('BRAKE', 'Brake', 'Brake system failure'),
                                                                   ('ENGINE', 'Engine', 'Engine failure'),
                                                                   ('TRANSMISSION', 'Transmission', 'Transmission failure');

-- Breakdown Severity
INSERT INTO breakdown_severity (severity_code, severity_name, description, response_time_hours) VALUES
                                                                                                    ('CATASTROPHIC', 'Catastrophic', 'Catastrophic failure requiring immediate attention', 1),
                                                                                                    ('SEVERE', 'Severe', 'Severe breakdown', 2),
                                                                                                    ('MAJOR', 'Major', 'Major breakdown', 4),
                                                                                                    ('MINOR', 'Minor', 'Minor breakdown', 8),
                                                                                                    ('COSMETIC', 'Cosmetic', 'Cosmetic issues only', 24);

-- Breakdown Status
INSERT INTO breakdown_status (status_code, status_name, description) VALUES
                                                                         ('REPORTED', 'Reported', 'Breakdown reported'),
                                                                         ('ASSESSING', 'Assessing', 'Assessing the situation'),
                                                                         ('AWAITING_PARTS', 'Awaiting Parts', 'Waiting for spare parts'),
                                                                         ('IN_REPAIR', 'In Repair', 'Under repair'),
                                                                         ('REPAIRED', 'Repaired', 'Repair completed'),
                                                                         ('BACK_IN_SERVICE', 'Back in Service', 'Vehicle back in service'),
                                                                         ('WRITTEN_OFF', 'Written Off', 'Vehicle written off');

-- Repair Category
INSERT INTO repair_category (category_code, category_name, description) VALUES
                                                                            ('MECHANICAL', 'Mechanical', 'Mechanical repairs'),
                                                                            ('ELECTRICAL', 'Electrical', 'Electrical repairs'),
                                                                            ('BODY', 'Body', 'Body and panel repairs'),
                                                                            ('PAINT', 'Paint', 'Painting works'),
                                                                            ('TIRE', 'Tire', 'Tire repairs'),
                                                                            ('BRAKE', 'Brake', 'Brake system repairs'),
                                                                            ('ENGINE', 'Engine', 'Engine repairs'),
                                                                            ('TRANSMISSION', 'Transmission', 'Transmission repairs'),
                                                                            ('AC', 'AC', 'Air conditioning repairs'),
                                                                            ('GENERAL', 'General', 'General maintenance');

-- Repair Location
INSERT INTO repair_location (location_code, location_name, description) VALUES
                                                                            ('IN_HOUSE', 'In-house', 'In-house repair facility'),
                                                                            ('WORKSHOP', 'External Workshop', 'External workshop'),
                                                                            ('ON_SITE', 'On-site', 'On-site repair'),
                                                                            ('MOBILE', 'Mobile Service', 'Mobile repair service'),
                                                                            ('DEALER', 'Dealer', 'Authorized dealer'),
                                                                            ('SPECIALIST', 'Specialist', 'Specialist repair center');

-- Repair Type
INSERT INTO repair_type (type_code, type_name, description) VALUES
                                                                ('MINOR', 'Minor', 'Minor repair'),
                                                                ('MAJOR', 'Major', 'Major repair'),
                                                                ('OVERHAUL', 'Overhaul', 'Complete overhaul'),
                                                                ('REPLACEMENT', 'Replacement', 'Part replacement'),
                                                                ('REFURBISHMENT', 'Refurbishment', 'Refurbishment work');

-- Repair Job Status
INSERT INTO repair_job_status (status_code, status_name, description) VALUES
                                                                          ('PENDING', 'Pending', 'Job pending'),
                                                                          ('ASSIGNED', 'Assigned', 'Assigned to mechanic'),
                                                                          ('IN_PROGRESS', 'In Progress', 'Work in progress'),
                                                                          ('ON_HOLD', 'On Hold', 'Job on hold'),
                                                                          ('COMPLETED', 'Completed', 'Job completed'),
                                                                          ('QUALITY_CHECK', 'Quality Check', 'Under quality check'),
                                                                          ('APPROVED', 'Approved', 'Job approved');

-- Maintenance Assignment Status
INSERT INTO maintenance_assignment_status (status_code, status_name, description) VALUES
                                                                                      ('PENDING', 'Pending', 'Assignment pending'),
                                                                                      ('ASSIGNED', 'Assigned', 'Assigned to staff'),
                                                                                      ('ACCEPTED', 'Accepted', 'Assignment accepted'),
                                                                                      ('DECLINED', 'Declined', 'Assignment declined'),
                                                                                      ('IN_PROGRESS', 'In Progress', 'Work in progress'),
                                                                                      ('COMPLETED', 'Completed', 'Assignment completed');

-- Inventory Category Type
INSERT INTO inventory_category_type (type_code, type_name, description) VALUES
                                                                            ('SPARE_PARTS', 'Spare Parts', 'Vehicle spare parts'),
                                                                            ('CONSUMABLES', 'Consumables', 'Consumable items'),
                                                                            ('TOOLS', 'Tools', 'Tools and equipment'),
                                                                            ('LUBRICANTS', 'Lubricants', 'Oils and lubricants'),
                                                                            ('TIRES', 'Tires', 'Tires and tubes'),
                                                                            ('BATTERIES', 'Batteries', 'Vehicle batteries'),
                                                                            ('FILTERS', 'Filters', 'Various filters'),
                                                                            ('ELECTRICAL', 'Electrical', 'Electrical components'),
                                                                            ('BODY_PARTS', 'Body Parts', 'Body and panel parts'),
                                                                            ('SAFETY', 'Safety Items', 'Safety equipment');

-- Inventory Transaction Type
INSERT INTO inventory_transaction_type (type_code, type_name, description, affects_stock, stock_direction) VALUES
                                                                                                               ('PURCHASE', 'Purchase', 'Stock purchase', TRUE, 1),
                                                                                                               ('SALE', 'Sale', 'Stock sale', TRUE, -1),
                                                                                                               ('ISSUE', 'Issue', 'Issue to vehicle/job', TRUE, -1),
                                                                                                               ('RETURN', 'Return', 'Return to stock', TRUE, 1),
                                                                                                               ('ADJUSTMENT_PLUS', 'Adjustment (+ve)', 'Positive stock adjustment', TRUE, 1),
                                                                                                               ('ADJUSTMENT_MINUS', 'Adjustment (-ve)', 'Negative stock adjustment', TRUE, -1),
                                                                                                               ('TRANSFER_IN', 'Transfer In', 'Transfer from another location', TRUE, 1),
                                                                                                               ('TRANSFER_OUT', 'Transfer Out', 'Transfer to another location', TRUE, -1),
                                                                                                               ('QUARANTINE', 'Quarantine', 'Move to quarantine', TRUE, 0),
                                                                                                               ('DISPOSE', 'Dispose', 'Stock disposal', TRUE, -1),
                                                                                                               ('QUOTATION', 'Quotation', 'Quoted to supplier', FALSE, 0),
                                                                                                               ('ORDER', 'Order', 'Order placed', FALSE, 0);

-- Inventory Alert Type
INSERT INTO inventory_alert_type (type_code, type_name, description) VALUES
                                                                         ('LOW_STOCK', 'Low Stock', 'Stock below minimum level'),
                                                                         ('REORDER', 'Reorder Point', 'Stock at reorder point'),
                                                                         ('OVER_STOCK', 'Over Stock', 'Stock above maximum level'),
                                                                         ('EXPIRY', 'Expiry', 'Item approaching expiry'),
                                                                         ('OBSOLETE', 'Obsolete', 'Item considered obsolete'),
                                                                         ('QUARANTINE', 'Quarantine', 'Item in quarantine'),
                                                                         ('QUALITY', 'Quality Issue', 'Quality concerns detected');

-- Alert Level
INSERT INTO alert_level (level_code, level_name, description, severity_order, color_code) VALUES
                                                                                              ('EMERGENCY', 'Emergency', 'Emergency alert', 1, '#FF0000'),
                                                                                              ('CRITICAL', 'Critical', 'Critical alert', 2, '#FF4500'),
                                                                                              ('WARNING', 'Warning', 'Warning alert', 3, '#FFA500'),
                                                                                              ('NOTICE', 'Notice', 'Notice alert', 4, '#FFFF00'),
                                                                                              ('INFO', 'Info', 'Informational', 5, '#0000FF');

-- Supplier Type
INSERT INTO supplier_type (type_code, type_name, description) VALUES
                                                                  ('MANUFACTURER', 'Manufacturer', 'Direct manufacturer'),
                                                                  ('DISTRIBUTOR', 'Distributor', 'Authorized distributor'),
                                                                  ('WHOLESALER', 'Wholesaler', 'Wholesale supplier'),
                                                                  ('RETAILER', 'Retailer', 'Retail supplier'),
                                                                  ('SERVICE_PROVIDER', 'Service Provider', 'Service provider'),
                                                                  ('OEM', 'OEM', 'Original Equipment Manufacturer'),
                                                                  ('AFTERMARKET', 'Aftermarket', 'Aftermarket parts supplier');

-- Payment Mode
INSERT INTO payment_mode (mode_code, mode_name, description) VALUES
                                                                 ('CASH', 'Cash', 'Cash payment'),
                                                                 ('CHEQUE', 'Cheque', 'Cheque payment'),
                                                                 ('BANK_TRANSFER', 'Bank Transfer', 'Bank transfer'),
                                                                 ('CREDIT_CARD', 'Credit Card', 'Credit card payment'),
                                                                 ('DEBIT_CARD', 'Debit Card', 'Debit card payment'),
                                                                 ('UPI', 'UPI', 'UPI payment'),
                                                                 ('NET_BANKING', 'Net Banking', 'Online net banking'),
                                                                 ('CREDIT', 'Credit', 'Credit terms'),
                                                                 ('FUEL_CARD', 'Fuel Card', 'Fuel card payment');

-- Expense Category
INSERT INTO expense_category (category_code, category_name, description) VALUES
                                                                             ('FUEL', 'Fuel', 'Fuel expenses'),
                                                                             ('MAINTENANCE', 'Maintenance', 'Maintenance expenses'),
                                                                             ('REPAIR', 'Repair', 'Repair expenses'),
                                                                             ('TIRE', 'Tire', 'Tire expenses'),
                                                                             ('INSURANCE', 'Insurance', 'Insurance premiums'),
                                                                             ('TAX', 'Tax', 'Tax payments'),
                                                                             ('PERMIT', 'Permit', 'Permit and license fees'),
                                                                             ('TOLL', 'Toll', 'Toll charges'),
                                                                             ('PARKING', 'Parking', 'Parking fees'),
                                                                             ('SALARY', 'Salary', 'Employee salaries'),
                                                                             ('ADMIN', 'Administrative', 'Administrative expenses'),
                                                                             ('UTILITY', 'Utility', 'Utility expenses'),
                                                                             ('RENT', 'Rent', 'Rent payments'),
                                                                             ('TRAINING', 'Training', 'Training expenses');

-- Quotation Status
INSERT INTO quotation_status (status_code, status_name, description) VALUES
                                                                         ('DRAFT', 'Draft', 'Quotation in draft'),
                                                                         ('SENT', 'Sent', 'Quotation sent to supplier'),
                                                                         ('UNDER_REVIEW', 'Under Review', 'Being reviewed'),
                                                                         ('ACCEPTED', 'Accepted', 'Quotation accepted'),
                                                                         ('REJECTED', 'Rejected', 'Quotation rejected'),
                                                                         ('EXPIRED', 'Expired', 'Quotation expired'),
                                                                         ('REVISED', 'Revised', 'Revised quotation');

-- Approval Status
INSERT INTO approval_status (status_code, status_name, description) VALUES
                                                                        ('PENDING', 'Pending', 'Awaiting approval'),
                                                                        ('APPROVED', 'Approved', 'Approved'),
                                                                        ('REJECTED', 'Rejected', 'Rejected'),
                                                                        ('NEEDS_REVISION', 'Needs Revision', 'Needs revision'),
                                                                        ('ESCALATED', 'Escalated', 'Escalated to higher authority'),
                                                                        ('DEFERRED', 'Deferred', 'Deferred for later decision'),
                                                                        ('CANCELLED', 'Cancelled', 'Cancelled by requester');

-- Purchase Request Status
INSERT INTO purchase_request_status (status_code, status_name, description) VALUES
                                                                                ('DRAFT', 'Draft', 'Request in draft'),
                                                                                ('SUBMITTED', 'Submitted', 'Submitted for approval'),
                                                                                ('PENDING_APPROVAL', 'Pending Approval', 'Awaiting approval'),
                                                                                ('APPROVED', 'Approved', 'Request approved'),
                                                                                ('REJECTED', 'Rejected', 'Request rejected'),
                                                                                ('QUOTATION_PHASE', 'Quotation Phase', 'Getting quotations'),
                                                                                ('ORDER_PLACED', 'Order Placed', 'Purchase order placed'),
                                                                                ('PARTIALLY_RECEIVED', 'Partially Received', 'Partially received'),
                                                                                ('COMPLETED', 'Completed', 'Request completed'),
                                                                                ('CANCELLED', 'Cancelled', 'Request cancelled');

-- Purchase Order Status
INSERT INTO purchase_order_status (status_code, status_name, description) VALUES
                                                                              ('DRAFT', 'Draft', 'Order in draft'),
                                                                              ('PENDING_APPROVAL', 'Pending Approval', 'Awaiting approval'),
                                                                              ('APPROVED', 'Approved', 'Order approved'),
                                                                              ('SENT', 'Sent', 'Order sent to supplier'),
                                                                              ('CONFIRMED', 'Confirmed', 'Order confirmed by supplier'),
                                                                              ('SHIPPED', 'Shipped', 'Order shipped'),
                                                                              ('PARTIALLY_RECEIVED', 'Partially Received', 'Partially received'),
                                                                              ('COMPLETED', 'Completed', 'Order completed'),
                                                                              ('CANCELLED', 'Cancelled', 'Order cancelled'),
                                                                              ('ON_HOLD', 'On Hold', 'Order on hold');

-- Goods Receipt Status
INSERT INTO goods_receipt_status (status_code, status_name, description) VALUES
                                                                             ('EXPECTED', 'Expected', 'Delivery expected'),
                                                                             ('PARTIALLY_RECEIVED', 'Partially Received', 'Partial receipt'),
                                                                             ('RECEIVED', 'Received', 'Goods received'),
                                                                             ('INSPECTED', 'Inspected', 'Quality inspection done'),
                                                                             ('ACCEPTED', 'Accepted', 'Goods accepted'),
                                                                             ('REJECTED', 'Rejected', 'Goods rejected'),
                                                                             ('QUARANTINED', 'Quarantined', 'Quarantined for inspection'),
                                                                             ('RETURNED', 'Returned', 'Returned to supplier');

-- Material Request Status
INSERT INTO material_request_status (status_code, status_name, description) VALUES
                                                                                ('DRAFT', 'Draft', 'Request in draft'),
                                                                                ('SUBMITTED', 'Submitted', 'Submitted'),
                                                                                ('PENDING_APPROVAL', 'Pending Approval', 'Awaiting approval'),
                                                                                ('APPROVED', 'Approved', 'Request approved'),
                                                                                ('REJECTED', 'Rejected', 'Request rejected'),
                                                                                ('ISSUED', 'Issued', 'Material issued'),
                                                                                ('PARTIALLY_ISSUED', 'Partially Issued', 'Partially issued'),
                                                                                ('CANCELLED', 'Cancelled', 'Request cancelled'),
                                                                                ('CLOSED', 'Closed', 'Request closed');

-- Customer Type
INSERT INTO customer_type (type_code, type_name, description) VALUES
                                                                  ('INDIVIDUAL', 'Individual', 'Individual customer'),
                                                                  ('CORPORATE', 'Corporate', 'Corporate customer'),
                                                                  ('GOVERNMENT', 'Government', 'Government entity'),
                                                                  ('NON_PROFIT', 'Non-Profit', 'Non-profit organization'),
                                                                  ('PARTNERSHIP', 'Partnership', 'Partnership firm'),
                                                                  ('SOLE_PROPRIETOR', 'Sole Proprietor', 'Sole proprietorship');

-- Customer Status
INSERT INTO customer_status (status_code, status_name, description) VALUES
                                                                        ('ACTIVE', 'Active', 'Active customer'),
                                                                        ('INACTIVE', 'Inactive', 'Inactive customer'),
                                                                        ('BLACKLISTED', 'Blacklisted', 'Blacklisted customer'),
                                                                        ('PROSPECT', 'Prospect', 'Potential customer'),
                                                                        ('FORMER', 'Former', 'Former customer'),
                                                                        ('VIP', 'VIP', 'VIP customer');

-- Rate Type
INSERT INTO rate_type (type_code, type_name, description) VALUES
                                                              ('HOURLY', 'Hourly', 'Hourly rate'),
                                                              ('DAILY', 'Daily', 'Daily rate'),
                                                              ('WEEKLY', 'Weekly', 'Weekly rate'),
                                                              ('MONTHLY', 'Monthly', 'Monthly rate'),
                                                              ('KM_BASED', 'KM Based', 'Rate per kilometer'),
                                                              ('TONNE_KM', 'Tonne-KM', 'Rate per tonne-kilometer'),
                                                              ('TRIP_BASED', 'Trip Based', 'Rate per trip'),
                                                              ('PROJECT_BASED', 'Project Based', 'Project-based rate');

-- Work Order Type
INSERT INTO work_order_type (type_code, type_name, description) VALUES
                                                                    ('MAINTENANCE', 'Maintenance', 'Maintenance work'),
                                                                    ('REPAIR', 'Repair', 'Repair work'),
                                                                    ('INSPECTION', 'Inspection', 'Inspection work'),
                                                                    ('INSTALLATION', 'Installation', 'Installation work'),
                                                                    ('FABRICATION', 'Fabrication', 'Fabrication work'),
                                                                    ('OVERHAUL', 'Overhaul', 'Overhaul work'),
                                                                    ('MODIFICATION', 'Modification', 'Modification work');

-- Work Order Status
INSERT INTO work_order_status (status_code, status_name, description) VALUES
                                                                          ('DRAFT', 'Draft', 'Work order in draft'),
                                                                          ('PENDING_APPROVAL', 'Pending Approval', 'Awaiting approval'),
                                                                          ('APPROVED', 'Approved', 'Work order approved'),
                                                                          ('ASSIGNED', 'Assigned', 'Assigned to team'),
                                                                          ('IN_PROGRESS', 'In Progress', 'Work in progress'),
                                                                          ('ON_HOLD', 'On Hold', 'Work on hold'),
                                                                          ('COMPLETED', 'Completed', 'Work completed'),
                                                                          ('VERIFIED', 'Verified', 'Work verified'),
                                                                          ('CANCELLED', 'Cancelled', 'Work order cancelled'),
                                                                          ('CLOSED', 'Closed', 'Work order closed');

-- Work Type
INSERT INTO work_type (type_code, type_name, description) VALUES
                                                              ('MECHANICAL', 'Mechanical', 'Mechanical work'),
                                                              ('ELECTRICAL', 'Electrical', 'Electrical work'),
                                                              ('BODY', 'Body', 'Body work'),
                                                              ('PAINT', 'Paint', 'Paint work'),
                                                              ('WELDING', 'Welding', 'Welding work'),
                                                              ('DIAGNOSTIC', 'Diagnostic', 'Diagnostic work'),
                                                              ('CLEANING', 'Cleaning', 'Cleaning work'),
                                                              ('LUBRICATION', 'Lubrication', 'Lubrication work');

-- Cost Type
INSERT INTO cost_type (type_code, type_name, description) VALUES
                                                              ('LABOR', 'Labor', 'Labor costs'),
                                                              ('MATERIAL', 'Material', 'Material costs'),
                                                              ('EQUIPMENT', 'Equipment', 'Equipment costs'),
                                                              ('SUBCONTRACT', 'Subcontract', 'Subcontractor costs'),
                                                              ('OVERHEAD', 'Overhead', 'Overhead costs'),
                                                              ('TRANSPORT', 'Transport', 'Transport costs'),
                                                              ('MISCELLANEOUS', 'Miscellaneous', 'Miscellaneous costs');

-- Fuel Payment Method
INSERT INTO fuel_payment_method (method_code, method_name, description) VALUES
                                                                            ('CASH', 'Cash', 'Cash payment'),
                                                                            ('FUEL_CARD', 'Fuel Card', 'Fuel card payment'),
                                                                            ('CREDIT_CARD', 'Credit Card', 'Credit card payment'),
                                                                            ('DEBIT_CARD', 'Debit Card', 'Debit card payment'),
                                                                            ('CREDIT_TERMS', 'Credit Terms', 'Credit terms with fuel station'),
                                                                            ('FLEET_CARD', 'Fleet Card', 'Fleet management card'),
                                                                            ('ACCOUNT', 'Account', 'Company account');

-- Fuel Approval Status
INSERT INTO fuel_approval_status (status_code, status_name, description) VALUES
                                                                             ('PENDING', 'Pending', 'Awaiting approval'),
                                                                             ('APPROVED', 'Approved', 'Fuel request approved'),
                                                                             ('REJECTED', 'Rejected', 'Fuel request rejected'),
                                                                             ('AUTO_APPROVED', 'Auto-Approved', 'Auto-approved based on rules'),
                                                                             ('FLAGGED', 'Flagged', 'Flagged for review'),
                                                                             ('ESCALATED', 'Escalated', 'Escalated to manager');

-- Hire Basis
INSERT INTO hire_basis (basis_code, basis_name, description) VALUES
                                                                 ('HR', 'Hourly', 'Hire by hour'),
                                                                 ('DAY', 'Daily', 'Hire by day'),
                                                                 ('WK', 'Weekly', 'Hire by week'),
                                                                 ('MON', 'Monthly', 'Hire by month'),
                                                                 ('KM', 'Kilometer', 'Hire by kilometer'),
                                                                 ('TRIP', 'Trip', 'Hire by trip'),
                                                                 ('PROJ', 'Project', 'Hire by project');

-- Billing Cycle
INSERT INTO billing_cycle (cycle_code, cycle_name, description) VALUES
                                                                    ('WEEKLY', 'Weekly', 'Weekly billing'),
                                                                    ('BI_WEEKLY', 'Bi-Weekly', 'Bi-weekly billing'),
                                                                    ('MONTHLY', 'Monthly', 'Monthly billing'),
                                                                    ('QUARTERLY', 'Quarterly', 'Quarterly billing'),
                                                                    ('HALF_YEARLY', 'Half-Yearly', 'Half-yearly billing'),
                                                                    ('YEARLY', 'Yearly', 'Yearly billing'),
                                                                    ('PROJECT_BASED', 'Project Based', 'Project-based billing'),
                                                                    ('TRIP_BASED', 'Trip Based', 'Trip-based billing');

-- Fuel Price Source
INSERT INTO fuel_price_source (source_code, source_name, description) VALUES
                                                                          ('MANUAL', 'Manual Entry', 'Manual price entry'),
                                                                          ('OIL_COMPANY', 'Oil Company', 'Oil company rates'),
                                                                          ('FUEL_CARD', 'Fuel Card', 'Fuel card provider rates'),
                                                                          ('GOVERNMENT', 'Government', 'Government published rates'),
                                                                          ('INVOICE', 'Invoice', 'Based on actual invoice'),
                                                                          ('MARKET', 'Market Rate', 'Market average rates');

-- Hire Bill Status
INSERT INTO hire_bill_status (status_code, status_name, description) VALUES
                                                                         ('DRAFT', 'Draft', 'Bill in draft'),
                                                                         ('GENERATED', 'Generated', 'Bill generated'),
                                                                         ('SENT', 'Sent', 'Sent to customer'),
                                                                         ('PARTIALLY_PAID', 'Partially Paid', 'Partially paid'),
                                                                         ('PAID', 'Paid', 'Fully paid'),
                                                                         ('OVERDUE', 'Overdue', 'Payment overdue'),
                                                                         ('DISPUTED', 'Disputed', 'Disputed by customer'),
                                                                         ('CANCELLED', 'Cancelled', 'Bill cancelled'),
                                                                         ('CREDIT_NOTE', 'Credit Note', 'Credit note issued');

-- Bill Component Type
INSERT INTO bill_component_type (type_code, type_name, description) VALUES
                                                                        ('HIRE_CHARGE', 'Hire Charge', 'Basic hire charge'),
                                                                        ('FUEL', 'Fuel', 'Fuel charges'),
                                                                        ('TOLL', 'Toll', 'Toll charges'),
                                                                        ('PARKING', 'Parking', 'Parking charges'),
                                                                        ('WAITING', 'Waiting', 'Waiting charges'),
                                                                        ('OVERTIME', 'Overtime', 'Overtime charges'),
                                                                        ('LOADING', 'Loading', 'Loading/unloading charges'),
                                                                        ('NIGHT_HALT', 'Night Halt', 'Night halt charges'),
                                                                        ('TAX', 'Tax', 'Taxes'),
                                                                        ('DISCOUNT', 'Discount', 'Discounts'),
                                                                        ('PENALTY', 'Penalty', 'Penalty charges');

-- Deduction Basis
INSERT INTO deduction_basis (basis_code, basis_name, description) VALUES
                                                                      ('PERCENTAGE', 'Percentage', 'Percentage based deduction'),
                                                                      ('FIXED', 'Fixed', 'Fixed amount deduction'),
                                                                      ('PER_UNIT', 'Per Unit', 'Per unit (km/hour) deduction'),
                                                                      ('FORMULA', 'Formula', 'Formula based deduction'),
                                                                      ('CONTRACTUAL', 'Contractual', 'As per contract terms');

-- Tyre Status
INSERT INTO tyre_status (status_code, status_name, description) VALUES
                                                                    ('NEW', 'New', 'New tyre'),
                                                                    ('IN_USE', 'In Use', 'Currently in use'),
                                                                    ('RESERVED', 'Reserved', 'Reserved for future use'),
                                                                    ('UNDER_REPAIR', 'Under Repair', 'Being repaired'),
                                                                    ('RETREADED', 'Retreaded', 'Retreaded tyre'),
                                                                    ('SCRAP', 'Scrap', 'Ready for scrapping'),
                                                                    ('SCRAPPED', 'Scrapped', 'Scrapped tyre'),
                                                                    ('IN_STOCK', 'In Stock', 'In inventory');

-- Wheel Position
INSERT INTO wheel_position (position_code, position_name, description) VALUES
                                                                           ('F', 'Front', 'Front axle'),
                                                                           ('R', 'Rear', 'Rear axle'),
                                                                           ('T1', 'Trailer 1', 'First trailer axle'),
                                                                           ('T2', 'Trailer 2', 'Second trailer axle'),
                                                                           ('D', 'Drive', 'Drive axle'),
                                                                           ('S', 'Steer', 'Steer axle'),
                                                                           ('L1', 'Lift 1', 'First lift axle'),
                                                                           ('L2', 'Lift 2', 'Second lift axle');

-- Wear Pattern
INSERT INTO wear_pattern (pattern_code, pattern_name, description) VALUES
                                                                       ('NORMAL', 'Normal', 'Normal wear pattern'),
                                                                       ('CENTER', 'Center Wear', 'Excessive center wear'),
                                                                       ('EDGE', 'Edge Wear', 'Excessive edge wear'),
                                                                       ('ONE_SIDE', 'One Side', 'One-sided wear'),
                                                                       ('CUPPING', 'Cupping', 'Cupping/Scalloping wear'),
                                                                       ('PATCHY', 'Patchy', 'Patchy wear'),
                                                                       ('HEEL_TOE', 'Heel-Toe', 'Heel-toe wear');

-- Tyre Action
INSERT INTO tyre_action (action_code, action_name, description) VALUES
                                                                    ('FITTED', 'Fitted', 'Tyre fitted to vehicle'),
                                                                    ('REMOVED', 'Removed', 'Tyre removed from vehicle'),
                                                                    ('ROTATED', 'Rotated', 'Tyre rotated'),
                                                                    ('REPAIRED', 'Repaired', 'Tyre repaired'),
                                                                    ('INFLATED', 'Inflated', 'Tyre inflated'),
                                                                    ('INSPECTED', 'Inspected', 'Tyre inspected'),
                                                                    ('BALANCED', 'Balanced', 'Tyre balanced'),
                                                                    ('ALIGNED', 'Aligned', 'Wheel alignment done');

-- Tyre Repair Type
INSERT INTO tyre_repair_type (type_code, type_name, description) VALUES
                                                                     ('PATCH', 'Patch Repair', 'External patch repair'),
                                                                     ('PLUG', 'Plug Repair', 'Plug repair'),
                                                                     ('VULCANIZING', 'Vulcanizing', 'Hot vulcanizing'),
                                                                     ('SECTION', 'Section Repair', 'Section repair'),
                                                                     ('RETREAD', 'Retread', 'Retreading'),
                                                                     ('TUBE_REPAIR', 'Tube Repair', 'Tube repair');

-- Rotation Scheme
INSERT INTO rotation_scheme (scheme_code, scheme_name, description) VALUES
                                                                        ('STANDARD', 'Standard', 'Standard rotation pattern'),
                                                                        ('CROSS', 'Cross', 'Cross rotation pattern'),
                                                                        ('FIVE_TIRE', 'Five-Tire', 'Five-tire rotation'),
                                                                        ('DRIVE_AXLE', 'Drive Axle', 'Drive axle only rotation'),
                                                                        ('FRONT_REAR', 'Front-Rear', 'Front to rear rotation');

-- Removal Reason
INSERT INTO removal_reason (reason_code, reason_name, description) VALUES
                                                                       ('WORN_OUT', 'Worn Out', 'Tyre worn beyond limits'),
                                                                       ('DAMAGED', 'Damaged', 'Tyre damaged irreparably'),
                                                                       ('PUNCTURE', 'Puncture', 'Multiple punctures'),
                                                                       ('SIDE_WALL', 'Sidewall Damage', 'Sidewall damage'),
                                                                       ('BLOWOUT', 'Blowout', 'Tyre blowout'),
                                                                       ('RETREADING', 'For Retreading', 'Removed for retreading'),
                                                                       ('SEASONAL', 'Seasonal Change', 'Seasonal tyre change'),
                                                                       ('UPGRADE', 'Upgrade', 'Upgrading to better tyre');

-- Battery Status
INSERT INTO battery_status (status_code, status_name, description) VALUES
                                                                       ('NEW', 'New', 'New battery'),
                                                                       ('IN_USE', 'In Use', 'Currently in use'),
                                                                       ('CHARGING', 'Charging', 'Under charging'),
                                                                       ('TESTING', 'Testing', 'Under testing'),
                                                                       ('GOOD', 'Good', 'Good condition'),
                                                                       ('WEAK', 'Weak', 'Weak condition'),
                                                                       ('FAILED', 'Failed', 'Failed battery'),
                                                                       ('RECYCLED', 'Recycled', 'Recycled battery');

-- Battery Test Result
INSERT INTO battery_test_result (result_code, result_name, description) VALUES
                                                                            ('PASS', 'Pass', 'Test passed'),
                                                                            ('FAIL', 'Fail', 'Test failed'),
                                                                            ('CHARGE_NEEDED', 'Charge Needed', 'Needs charging'),
                                                                            ('WEAK', 'Weak', 'Weak performance'),
                                                                            ('GOOD', 'Good', 'Good condition'),
                                                                            ('REPLACE', 'Replace', 'Needs replacement');

-- Warranty Claim Status
INSERT INTO warranty_claim_status (status_code, status_name, description) VALUES
                                                                              ('DRAFT', 'Draft', 'Claim in draft'),
                                                                              ('SUBMITTED', 'Submitted', 'Submitted to supplier'),
                                                                              ('UNDER_REVIEW', 'Under Review', 'Being reviewed'),
                                                                              ('APPROVED', 'Approved', 'Claim approved'),
                                                                              ('REJECTED', 'Rejected', 'Claim rejected'),
                                                                              ('PARTIALLY_APPROVED', 'Partially Approved', 'Partially approved'),
                                                                              ('PAID', 'Paid', 'Claim paid'),
                                                                              ('CREDITED', 'Credited', 'Amount credited'),
                                                                              ('CLOSED', 'Closed', 'Claim closed');

-- Service Order Status
INSERT INTO service_order_status (status_code, status_name, description) VALUES
                                                                             ('PENDING', 'Pending', 'Order pending'),
                                                                             ('ASSIGNED', 'Assigned', 'Assigned to service provider'),
                                                                             ('IN_PROGRESS', 'In Progress', 'Service in progress'),
                                                                             ('ON_HOLD', 'On Hold', 'Service on hold'),
                                                                             ('COMPLETED', 'Completed', 'Service completed'),
                                                                             ('VERIFIED', 'Verified', 'Service verified'),
                                                                             ('INVOICED', 'Invoiced', 'Invoiced'),
                                                                             ('PAID', 'Paid', 'Payment done'),
                                                                             ('CANCELLED', 'Cancelled', 'Order cancelled');

-- Revenue Type
INSERT INTO revenue_type (type_code, type_name, description) VALUES
                                                                 ('HIRE', 'Hire Charges', 'Vehicle hire revenue'),
                                                                 ('TRANSPORT', 'Transport', 'Transport service revenue'),
                                                                 ('MAINTENANCE', 'Maintenance', 'Maintenance service revenue'),
                                                                 ('RENTAL', 'Rental', 'Equipment rental revenue'),
                                                                 ('CONSULTING', 'Consulting', 'Consulting service revenue'),
                                                                 ('OTHER', 'Other', 'Other revenue sources');

-- Login Status
INSERT INTO login_status (status_code, status_name, description) VALUES
                                                                     ('ACTIVE', 'Active', 'Active login'),
                                                                     ('INACTIVE', 'Inactive', 'Inactive account'),
                                                                     ('LOCKED', 'Locked', 'Account locked'),
                                                                     ('SUSPENDED', 'Suspended', 'Account suspended'),
                                                                     ('EXPIRED', 'Expired', 'Password expired'),
                                                                     ('PENDING', 'Pending', 'Pending activation');

-- Issue To Type
INSERT INTO issue_to_type (type_code, type_name, description) VALUES
                                                                  ('VEHICLE', 'Vehicle', 'Issued to vehicle'),
                                                                  ('EMPLOYEE', 'Employee', 'Issued to employee'),
                                                                  ('PROJECT', 'Project', 'Issued to project'),
                                                                  ('WORK_ORDER', 'Work Order', 'Issued to work order'),
                                                                  ('DEPARTMENT', 'Department', 'Issued to department'),
                                                                  ('EXTERNAL', 'External', 'Issued externally');

-- Payroll Status
INSERT INTO payroll_status (status_code, status_name, description) VALUES
                                                                       ('DRAFT', 'Draft', 'Payroll in draft'),
                                                                       ('PROCESSING', 'Processing', 'Processing payroll'),
                                                                       ('CALCULATED', 'Calculated', 'Calculations done'),
                                                                       ('PENDING_APPROVAL', 'Pending Approval', 'Awaiting approval'),
                                                                       ('APPROVED', 'Approved', 'Payroll approved'),
                                                                       ('PAID', 'Paid', 'Payments made'),
                                                                       ('CANCELLED', 'Cancelled', 'Payroll cancelled'),
                                                                       ('REVERSED', 'Reversed', 'Payroll reversed');

-- Payroll Payment Method
INSERT INTO payroll_payment_method (method_code, method_name, description) VALUES
                                                                               ('BANK_TRANSFER', 'Bank Transfer', 'Direct bank transfer'),
                                                                               ('CHEQUE', 'Cheque', 'Cheque payment'),
                                                                               ('CASH', 'Cash', 'Cash payment'),
                                                                               ('WALLET', 'Wallet', 'Digital wallet payment'),
                                                                               ('CARD', 'Card', 'Card payment');

-- Payroll Deduction Type
INSERT INTO payroll_deduction_type (type_code, type_name, description) VALUES
                                                                           ('TAX', 'Tax', 'Tax deduction'),
                                                                           ('INSURANCE', 'Insurance', 'Insurance premium'),
                                                                           ('LOAN', 'Loan', 'Loan repayment'),
                                                                           ('ADVANCE', 'Advance', 'Advance recovery'),
                                                                           ('PENALTY', 'Penalty', 'Penalty deduction'),
                                                                           ('VOLUNTARY', 'Voluntary', 'Voluntary deduction'),
                                                                           ('OTHER', 'Other', 'Other deductions');

-- Voucher Status
INSERT INTO voucher_status (status_code, status_name, description) VALUES
                                                                       ('DRAFT', 'Draft', 'Voucher in draft'),
                                                                       ('PENDING_APPROVAL', 'Pending Approval', 'Awaiting approval'),
                                                                       ('APPROVED', 'Approved', 'Voucher approved'),
                                                                       ('PAID', 'Paid', 'Payment made'),
                                                                       ('CANCELLED', 'Cancelled', 'Voucher cancelled'),
                                                                       ('REJECTED', 'Rejected', 'Voucher rejected');

-- ML Job Status
INSERT INTO ml_job_status (status_code, status_name, description) VALUES
                                                                      ('PENDING', 'Pending', 'Job pending'),
                                                                      ('QUEUED', 'Queued', 'In queue'),
                                                                      ('RUNNING', 'Running', 'Job running'),
                                                                      ('COMPLETED', 'Completed', 'Job completed'),
                                                                      ('FAILED', 'Failed', 'Job failed'),
                                                                      ('CANCELLED', 'Cancelled', 'Job cancelled'),
                                                                      ('TIMEOUT', 'Timeout', 'Job timed out');

-- Component Type
INSERT INTO component_type (type_code, type_name, description) VALUES
                                                                   ('ENGINE', 'Engine', 'Engine components'),
                                                                   ('TRANSMISSION', 'Transmission', 'Transmission components'),
                                                                   ('BRAKE', 'Brake', 'Brake system'),
                                                                   ('SUSPENSION', 'Suspension', 'Suspension system'),
                                                                   ('ELECTRICAL', 'Electrical', 'Electrical system'),
                                                                   ('COOLING', 'Cooling', 'Cooling system'),
                                                                   ('EXHAUST', 'Exhaust', 'Exhaust system'),
                                                                   ('FUEL', 'Fuel', 'Fuel system'),
                                                                   ('STEERING', 'Steering', 'Steering system'),
                                                                   ('BODY', 'Body', 'Body components');

-- Filter Status
INSERT INTO filter_status (status_code, status_name, description) VALUES
                                                                      ('CLEAN', 'Clean', 'Filter is clean'),
                                                                      ('DIRTY', 'Dirty', 'Filter needs cleaning'),
                                                                      ('CLOGGED', 'Clogged', 'Filter is clogged'),
                                                                      ('DAMAGED', 'Damaged', 'Filter is damaged'),
                                                                      ('REPLACED', 'Replaced', 'Filter replaced'),
                                                                      ('INSPECTED', 'Inspected', 'Filter inspected');

-- Assignment Type
INSERT INTO assignment_type (type_code, type_name, description) VALUES
                                                                    ('VEHICLE', 'Vehicle', 'Vehicle assignment'),
                                                                    ('DRIVER', 'Driver', 'Driver assignment'),
                                                                    ('CREW', 'Crew', 'Crew assignment'),
                                                                    ('EQUIPMENT', 'Equipment', 'Equipment assignment'),
                                                                    ('TASK', 'Task', 'Task assignment'),
                                                                    ('PROJECT', 'Project', 'Project assignment');

-- Assignment Status
INSERT INTO assignment_status (status_code, status_name, description) VALUES
                                                                          ('PENDING', 'Pending', 'Assignment pending'),
                                                                          ('ACTIVE', 'Active', 'Assignment active'),
                                                                          ('COMPLETED', 'Completed', 'Assignment completed'),
                                                                          ('CANCELLED', 'Cancelled', 'Assignment cancelled'),
                                                                          ('SUSPENDED', 'Suspended', 'Assignment suspended'),
                                                                          ('EXPIRED', 'Expired', 'Assignment expired');

-- Maintenance Plan Type
INSERT INTO maintenance_plan_type (type_code, type_name, description) VALUES
                                                                          ('TIME_BASED', 'Time Based', 'Time-based maintenance'),
                                                                          ('USAGE_BASED', 'Usage Based', 'Usage-based maintenance'),
                                                                          ('CONDITION_BASED', 'Condition Based', 'Condition-based maintenance'),
                                                                          ('PREDICTIVE', 'Predictive', 'Predictive maintenance'),
                                                                          ('SEASONAL', 'Seasonal', 'Seasonal maintenance');

-- Maintenance Plan Status
INSERT INTO maintenance_plan_status (status_code, status_name, description) VALUES
                                                                                ('ACTIVE', 'Active', 'Plan is active'),
                                                                                ('INACTIVE', 'Inactive', 'Plan is inactive'),
                                                                                ('COMPLETED', 'Completed', 'Plan completed'),
                                                                                ('SUSPENDED', 'Suspended', 'Plan suspended'),
                                                                                ('ARCHIVED', 'Archived', 'Plan archived');

-- Maintenance Plan Item Status
INSERT INTO maintenance_plan_item_status (status_code, status_name, description) VALUES
                                                                                     ('PENDING', 'Pending', 'Item pending'),
                                                                                     ('SCHEDULED', 'Scheduled', 'Item scheduled'),
                                                                                     ('IN_PROGRESS', 'In Progress', 'Item in progress'),
                                                                                     ('COMPLETED', 'Completed', 'Item completed'),
                                                                                     ('SKIPPED', 'Skipped', 'Item skipped'),
                                                                                     ('OVERDUE', 'Overdue', 'Item overdue');

-- Maintenance Program Type
INSERT INTO maintenance_program_type (type_code, type_name, description) VALUES
                                                                             ('OEM', 'OEM', 'OEM recommended program'),
                                                                             ('CUSTOM', 'Custom', 'Custom maintenance program'),
                                                                             ('REGULATORY', 'Regulatory', 'Regulatory required program'),
                                                                             ('WARRANTY', 'Warranty', 'Warranty-based program'),
                                                                             ('FLEET_SPECIFIC', 'Fleet Specific', 'Fleet-specific program');

-- Outsourced Repair Status
INSERT INTO outsourced_repair_status (status_code, status_name, description) VALUES
                                                                                 ('REQUESTED', 'Requested', 'Repair requested'),
                                                                                 ('QUOTED', 'Quoted', 'Quote received'),
                                                                                 ('APPROVED', 'Approved', 'Quote approved'),
                                                                                 ('IN_PROGRESS', 'In Progress', 'Repair in progress'),
                                                                                 ('COMPLETED', 'Completed', 'Repair completed'),
                                                                                 ('QUALITY_CHECK', 'Quality Check', 'Quality check'),
                                                                                 ('ACCEPTED', 'Accepted', 'Repair accepted'),
                                                                                 ('INVOICED', 'Invoiced', 'Invoice received'),
                                                                                 ('PAID', 'Paid', 'Payment made'),
                                                                                 ('DISPUTED', 'Disputed', 'Repair disputed');

-- Advance Status
INSERT INTO advance_status (status_code, status_name, description) VALUES
                                                                       ('REQUESTED', 'Requested', 'Advance requested'),
                                                                       ('PENDING_APPROVAL', 'Pending Approval', 'Awaiting approval'),
                                                                       ('APPROVED', 'Approved', 'Advance approved'),
                                                                       ('REJECTED', 'Rejected', 'Advance rejected'),
                                                                       ('PAID', 'Paid', 'Advance paid'),
                                                                       ('PARTIALLY_SETTLED', 'Partially Settled', 'Partially settled'),
                                                                       ('SETTLED', 'Settled', 'Fully settled'),
                                                                       ('CANCELLED', 'Cancelled', 'Advance cancelled');

-- Education Level
INSERT INTO education_level (level_code, level_name, description) VALUES
                                                                      ('BELOW_10TH', 'Below 10th', 'Below 10th standard'),
                                                                      ('10TH', '10th Pass', '10th standard passed'),
                                                                      ('12TH', '12th Pass', '12th standard passed'),
                                                                      ('ITI', 'ITI', 'Industrial Training Institute'),
                                                                      ('DIPLOMA', 'Diploma', 'Technical diploma'),
                                                                      ('GRADUATE', 'Graduate', 'Bachelor degree'),
                                                                      ('POST_GRADUATE', 'Post Graduate', 'Master degree'),
                                                                      ('DOCTORATE', 'Doctorate', 'PhD or equivalent');

-- Document Type Enum
INSERT INTO document_type_enum (type_code, type_name, description) VALUES
                                                                       ('DRIVING_LICENSE', 'Driving License', 'Valid driving license'),
                                                                       ('AADHAR', 'Aadhar Card', 'Aadhar identification'),
                                                                       ('PAN', 'PAN Card', 'PAN card'),
                                                                       ('VOTER_ID', 'Voter ID', 'Voter identification'),
                                                                       ('PASSPORT', 'Passport', 'Passport'),
                                                                       ('INSURANCE', 'Insurance', 'Insurance certificate'),
                                                                       ('REGISTRATION', 'Registration', 'Vehicle registration'),
                                                                       ('PERMIT', 'Permit', 'Vehicle permit'),
                                                                       ('TAX_RECEIPT', 'Tax Receipt', 'Tax payment receipt'),
                                                                       ('POLLUTION', 'Pollution Certificate', 'Pollution control certificate'),
                                                                       ('FITNESS', 'Fitness Certificate', 'Vehicle fitness certificate');

-- Data Scope Type
INSERT INTO data_scope_type (scope_code, scope_name, description) VALUES
                                                                      ('ALL_COMPANIES', 'All Companies', 'Access to all companies data'),
                                                                      ('COMPANY', 'Company', 'Access to current company data'),
                                                                      ('DEPARTMENT', 'Department', 'Access to department data'),
                                                                      ('BRANCH', 'Branch', 'Access to branch data'),
                                                                      ('SELF', 'Self', 'Access to own data only'),
                                                                      ('CUSTOM', 'Custom', 'Custom access scope');

-- Project Member Role
INSERT INTO project_member_role (role_code, role_name, description) VALUES
                                                                        ('PROJECT_MANAGER', 'Project Manager', 'Overall project manager'),
                                                                        ('TEAM_LEAD', 'Team Lead', 'Team leader'),
                                                                        ('MEMBER', 'Member', 'Project team member'),
                                                                        ('SPONSOR', 'Sponsor', 'Project sponsor'),
                                                                        ('STAKEHOLDER', 'Stakeholder', 'Project stakeholder'),
                                                                        ('CONSULTANT', 'Consultant', 'Project consultant'),
                                                                        ('OBSERVER', 'Observer', 'Project observer');

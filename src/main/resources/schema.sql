-- ============================================================================
-- COMPLETE VEHICLE MANAGEMENT SYSTEM (VMS) SCHEMA - ENHANCED
-- Multi-Company Scoping | Full RBAC | Auth 2.0 | Data Scopes
-- AI Fuel Tracking | Maintenance Programs | Own/Hire Vehicles
-- ============================================================================
-- RULE: All transactional/entity tables carry company_id AND company_code
--       for multi-tenancy. Enum/lookup tables do NOT carry company_id or
--       company_code (shared across system).
-- ============================================================================

/* ============================ EXTENSIONS ================================ */
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE EXTENSION IF NOT EXISTS "pg_trgm";
CREATE EXTENSION IF NOT EXISTS "btree_gist";
CREATE EXTENSION IF NOT EXISTS plpgsql;

/* ====================== UTILITY FUNCTIONS ======================== */
CREATE OR REPLACE FUNCTION set_updated_at()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ============================================================================
-- ENUM / LOOKUP TABLES  (No company_id / company_code — shared across system)
-- ============================================================================

CREATE TABLE IF NOT EXISTS company_type (
                                            type_id SERIAL PRIMARY KEY, type_code VARCHAR(20) UNIQUE NOT NULL,
                                            type_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS project_type (
                                            type_id SERIAL PRIMARY KEY, type_code VARCHAR(20) UNIQUE NOT NULL,
                                            type_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS project_status (
                                              status_id SERIAL PRIMARY KEY, status_code VARCHAR(20) UNIQUE NOT NULL,
                                              status_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS employee_category (
                                                 category_id SERIAL PRIMARY KEY, category_code VARCHAR(20) UNIQUE NOT NULL,
                                                 category_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS employment_type (
                                               type_id SERIAL PRIMARY KEY, type_code VARCHAR(20) UNIQUE NOT NULL,
                                               type_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS employment_status (
                                                 status_id SERIAL PRIMARY KEY, status_code VARCHAR(20) UNIQUE NOT NULL,
                                                 status_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS gender (
                                      gender_id SERIAL PRIMARY KEY, gender_code VARCHAR(10) UNIQUE NOT NULL,
                                      gender_name VARCHAR(20) NOT NULL, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS skill_category (
                                              category_id SERIAL PRIMARY KEY, category_code VARCHAR(20) UNIQUE NOT NULL,
                                              category_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS skill_level (
                                           level_id SERIAL PRIMARY KEY, level_code VARCHAR(20) UNIQUE NOT NULL,
                                           level_name VARCHAR(50) NOT NULL, description TEXT,
                                           min_score INTEGER, max_score INTEGER, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS training_type (
                                             type_id SERIAL PRIMARY KEY, type_code VARCHAR(20) UNIQUE NOT NULL,
                                             type_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS training_status (
                                               status_id SERIAL PRIMARY KEY, status_code VARCHAR(20) UNIQUE NOT NULL,
                                               status_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS complaint_type (
                                              type_id SERIAL PRIMARY KEY, type_code VARCHAR(20) UNIQUE NOT NULL,
                                              type_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS complaint_priority (
                                                  priority_id SERIAL PRIMARY KEY, priority_code VARCHAR(20) UNIQUE NOT NULL,
                                                  priority_name VARCHAR(50) NOT NULL, description TEXT,
                                                  severity_order INTEGER, response_time_hours INTEGER, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS complaint_status (
                                                status_id SERIAL PRIMARY KEY, status_code VARCHAR(20) UNIQUE NOT NULL,
                                                status_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS performance_rating (
                                                  rating_id SERIAL PRIMARY KEY, rating_code VARCHAR(20) UNIQUE NOT NULL,
                                                  rating_name VARCHAR(50) NOT NULL, description TEXT,
                                                  min_score DECIMAL(5,2), max_score DECIMAL(5,2), is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS attendance_status (
                                                 status_id SERIAL PRIMARY KEY, status_code VARCHAR(20) UNIQUE NOT NULL,
                                                 status_name VARCHAR(50) NOT NULL, description TEXT,
                                                 is_paid BOOLEAN DEFAULT TRUE, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS overtime_type (
                                             type_id SERIAL PRIMARY KEY, type_code VARCHAR(20) UNIQUE NOT NULL,
                                             type_name VARCHAR(50) NOT NULL, description TEXT,
                                             multiplier DECIMAL(3,2) DEFAULT 1.5, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS leave_application_status (
                                                        status_id SERIAL PRIMARY KEY, status_code VARCHAR(20) UNIQUE NOT NULL,
                                                        status_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS vehicle_category_type (
                                                     type_id SERIAL PRIMARY KEY, type_code VARCHAR(20) UNIQUE NOT NULL,
                                                     type_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS fuel_type (
                                         type_id SERIAL PRIMARY KEY, type_code VARCHAR(20) UNIQUE NOT NULL,
                                         type_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS transmission_type (
                                                 type_id SERIAL PRIMARY KEY, type_code VARCHAR(20) UNIQUE NOT NULL,
                                                 type_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS drivetrain_type (
                                               type_id SERIAL PRIMARY KEY, type_code VARCHAR(20) UNIQUE NOT NULL,
                                               type_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS ownership_type (
                                              type_id SERIAL PRIMARY KEY, type_code VARCHAR(10) UNIQUE NOT NULL,
                                              type_name VARCHAR(20) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS operational_status (
                                                  status_id SERIAL PRIMARY KEY, status_code VARCHAR(20) UNIQUE NOT NULL,
                                                  status_name VARCHAR(50) NOT NULL, description TEXT,
                                                  is_available BOOLEAN DEFAULT FALSE, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS consumption_method (
                                                  method_id SERIAL PRIMARY KEY, method_code VARCHAR(20) UNIQUE NOT NULL,
                                                  method_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS ai_model_type (
                                             type_id SERIAL PRIMARY KEY, type_code VARCHAR(20) UNIQUE NOT NULL,
                                             type_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS prediction_type (
                                               type_id SERIAL PRIMARY KEY, type_code VARCHAR(20) UNIQUE NOT NULL,
                                               type_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS risk_level (
                                          level_id SERIAL PRIMARY KEY, level_code VARCHAR(20) UNIQUE NOT NULL,
                                          level_name VARCHAR(50) NOT NULL, description TEXT,
                                          severity_order INTEGER, color_code VARCHAR(7), is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS maintenance_strategy_type (
                                                         strategy_id SERIAL PRIMARY KEY, strategy_code VARCHAR(20) UNIQUE NOT NULL,
                                                         strategy_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS maintenance_category (
                                                    category_id SERIAL PRIMARY KEY, category_code VARCHAR(20) UNIQUE NOT NULL,
                                                    category_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS maintenance_schedule_status (
                                                           status_id SERIAL PRIMARY KEY, status_code VARCHAR(20) UNIQUE NOT NULL,
                                                           status_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS maintenance_record_status (
                                                         status_id SERIAL PRIMARY KEY, status_code VARCHAR(20) UNIQUE NOT NULL,
                                                         status_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS breakdown_type (
                                              type_id SERIAL PRIMARY KEY, type_code VARCHAR(20) UNIQUE NOT NULL,
                                              type_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS breakdown_severity (
                                                  severity_id SERIAL PRIMARY KEY, severity_code VARCHAR(20) UNIQUE NOT NULL,
                                                  severity_name VARCHAR(50) NOT NULL, description TEXT,
                                                  response_time_hours INTEGER, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS breakdown_status (
                                                status_id SERIAL PRIMARY KEY, status_code VARCHAR(20) UNIQUE NOT NULL,
                                                status_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS repair_category (
                                               category_id SERIAL PRIMARY KEY, category_code VARCHAR(20) UNIQUE NOT NULL,
                                               category_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS repair_location (
                                               location_id SERIAL PRIMARY KEY, location_code VARCHAR(20) UNIQUE NOT NULL,
                                               location_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS repair_type (
                                           type_id SERIAL PRIMARY KEY, type_code VARCHAR(20) UNIQUE NOT NULL,
                                           type_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS repair_job_status (
                                                 status_id SERIAL PRIMARY KEY, status_code VARCHAR(20) UNIQUE NOT NULL,
                                                 status_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS maintenance_assignment_status (
                                                             status_id SERIAL PRIMARY KEY, status_code VARCHAR(20) UNIQUE NOT NULL,
                                                             status_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS inventory_category_type (
                                                       type_id SERIAL PRIMARY KEY, type_code VARCHAR(20) UNIQUE NOT NULL,
                                                       type_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS inventory_transaction_type (
                                                          type_id SERIAL PRIMARY KEY, type_code VARCHAR(20) UNIQUE NOT NULL,
                                                          type_name VARCHAR(50) NOT NULL, description TEXT,
                                                          affects_stock BOOLEAN DEFAULT TRUE,
                                                          stock_direction INTEGER CHECK (stock_direction IN (-1, 0, 1)),
                                                          is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS inventory_alert_type (
                                                    type_id SERIAL PRIMARY KEY, type_code VARCHAR(20) UNIQUE NOT NULL,
                                                    type_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS alert_level (
                                           level_id SERIAL PRIMARY KEY, level_code VARCHAR(20) UNIQUE NOT NULL,
                                           level_name VARCHAR(50) NOT NULL, description TEXT,
                                           severity_order INTEGER, color_code VARCHAR(7), is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS supplier_type (
                                             type_id SERIAL PRIMARY KEY, type_code VARCHAR(20) UNIQUE NOT NULL,
                                             type_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS payment_mode (
                                            mode_id SERIAL PRIMARY KEY, mode_code VARCHAR(20) UNIQUE NOT NULL,
                                            mode_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS expense_category (
                                                category_id SERIAL PRIMARY KEY, category_code VARCHAR(20) UNIQUE NOT NULL,
                                                category_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS quotation_status (
                                                status_id SERIAL PRIMARY KEY, status_code VARCHAR(20) UNIQUE NOT NULL,
                                                status_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS approval_status (
                                               status_id SERIAL PRIMARY KEY, status_code VARCHAR(20) UNIQUE NOT NULL,
                                               status_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS purchase_request_status (
                                                       status_id SERIAL PRIMARY KEY, status_code VARCHAR(20) UNIQUE NOT NULL,
                                                       status_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS purchase_order_status (
                                                     status_id SERIAL PRIMARY KEY, status_code VARCHAR(20) UNIQUE NOT NULL,
                                                     status_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS goods_receipt_status (
                                                    status_id SERIAL PRIMARY KEY, status_code VARCHAR(20) UNIQUE NOT NULL,
                                                    status_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS material_request_status (
                                                       status_id SERIAL PRIMARY KEY, status_code VARCHAR(20) UNIQUE NOT NULL,
                                                       status_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS customer_type (
                                             type_id SERIAL PRIMARY KEY, type_code VARCHAR(20) UNIQUE NOT NULL,
                                             type_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS customer_status (
                                               status_id SERIAL PRIMARY KEY, status_code VARCHAR(20) UNIQUE NOT NULL,
                                               status_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS rate_type (
                                         type_id SERIAL PRIMARY KEY, type_code VARCHAR(20) UNIQUE NOT NULL,
                                         type_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS work_order_type (
                                               type_id SERIAL PRIMARY KEY, type_code VARCHAR(20) UNIQUE NOT NULL,
                                               type_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS work_order_status (
                                                 status_id SERIAL PRIMARY KEY, status_code VARCHAR(20) UNIQUE NOT NULL,
                                                 status_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS work_type (
                                         type_id SERIAL PRIMARY KEY, type_code VARCHAR(20) UNIQUE NOT NULL,
                                         type_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS cost_type (
                                         type_id SERIAL PRIMARY KEY, type_code VARCHAR(20) UNIQUE NOT NULL,
                                         type_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS fuel_payment_method (
                                                   method_id SERIAL PRIMARY KEY, method_code VARCHAR(20) UNIQUE NOT NULL,
                                                   method_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS fuel_approval_status (
                                                    status_id SERIAL PRIMARY KEY, status_code VARCHAR(20) UNIQUE NOT NULL,
                                                    status_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS hire_basis (
                                          basis_id SERIAL PRIMARY KEY, basis_code VARCHAR(10) UNIQUE NOT NULL,
                                          basis_name VARCHAR(20) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS billing_cycle (
                                             cycle_id SERIAL PRIMARY KEY, cycle_code VARCHAR(20) UNIQUE NOT NULL,
                                             cycle_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS fuel_price_source (
                                                 source_id SERIAL PRIMARY KEY, source_code VARCHAR(20) UNIQUE NOT NULL,
                                                 source_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS hire_bill_status (
                                                status_id SERIAL PRIMARY KEY, status_code VARCHAR(20) UNIQUE NOT NULL,
                                                status_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS bill_component_type (
                                                   type_id SERIAL PRIMARY KEY, type_code VARCHAR(20) UNIQUE NOT NULL,
                                                   type_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS deduction_basis (
                                               basis_id SERIAL PRIMARY KEY, basis_code VARCHAR(20) UNIQUE NOT NULL,
                                               basis_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS tyre_status (
                                           status_id SERIAL PRIMARY KEY, status_code VARCHAR(20) UNIQUE NOT NULL,
                                           status_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS wheel_position (
                                              position_id SERIAL PRIMARY KEY, position_code VARCHAR(1) UNIQUE NOT NULL,
                                              position_name VARCHAR(20) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS wear_pattern (
                                            pattern_id SERIAL PRIMARY KEY, pattern_code VARCHAR(20) UNIQUE NOT NULL,
                                            pattern_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS tyre_action (
                                           action_id SERIAL PRIMARY KEY, action_code VARCHAR(20) UNIQUE NOT NULL,
                                           action_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS tyre_repair_type (
                                                type_id SERIAL PRIMARY KEY, type_code VARCHAR(20) UNIQUE NOT NULL,
                                                type_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS rotation_scheme (
                                               scheme_id SERIAL PRIMARY KEY, scheme_code VARCHAR(20) UNIQUE NOT NULL,
                                               scheme_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS removal_reason (
                                              reason_id SERIAL PRIMARY KEY, reason_code VARCHAR(20) UNIQUE NOT NULL,
                                              reason_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS battery_status (
                                              status_id SERIAL PRIMARY KEY, status_code VARCHAR(20) UNIQUE NOT NULL,
                                              status_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS battery_test_result (
                                                   result_id SERIAL PRIMARY KEY, result_code VARCHAR(20) UNIQUE NOT NULL,
                                                   result_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS warranty_claim_status (
                                                     status_id SERIAL PRIMARY KEY, status_code VARCHAR(20) UNIQUE NOT NULL,
                                                     status_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS service_order_status (
                                                    status_id SERIAL PRIMARY KEY, status_code VARCHAR(20) UNIQUE NOT NULL,
                                                    status_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS revenue_type (
                                            type_id SERIAL PRIMARY KEY, type_code VARCHAR(20) UNIQUE NOT NULL,
                                            type_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS login_status (
                                            status_id SERIAL PRIMARY KEY, status_code VARCHAR(20) UNIQUE NOT NULL,
                                            status_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS issue_to_type (
                                             type_id SERIAL PRIMARY KEY, type_code VARCHAR(20) UNIQUE NOT NULL,
                                             type_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS payroll_status (
                                              status_id SERIAL PRIMARY KEY, status_code VARCHAR(20) UNIQUE NOT NULL,
                                              status_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS payroll_payment_method (
                                                      method_id SERIAL PRIMARY KEY, method_code VARCHAR(20) UNIQUE NOT NULL,
                                                      method_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS payroll_deduction_type (
                                                      type_id SERIAL PRIMARY KEY, type_code VARCHAR(20) UNIQUE NOT NULL,
                                                      type_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS voucher_status (
                                              status_id SERIAL PRIMARY KEY, status_code VARCHAR(20) UNIQUE NOT NULL,
                                              status_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS ml_job_status (
                                             status_id SERIAL PRIMARY KEY, status_code VARCHAR(20) UNIQUE NOT NULL,
                                             status_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS component_type (
                                              type_id SERIAL PRIMARY KEY, type_code VARCHAR(20) UNIQUE NOT NULL,
                                              type_name VARCHAR(50) NOT NULL, description TEXT, is_active BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS filter_status (
                                             status_id   SERIAL PRIMARY KEY,
                                             status_code VARCHAR(20) UNIQUE NOT NULL,
                                             status_name VARCHAR(50)        NOT NULL,
                                             description TEXT,
                                             is_active   BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS assignment_type (
                                               type_id     SERIAL PRIMARY KEY,
                                               type_code   VARCHAR(20) UNIQUE NOT NULL,
                                               type_name   VARCHAR(50)        NOT NULL,
                                               description TEXT,
                                               is_active   BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS assignment_status (
                                                 status_id   SERIAL PRIMARY KEY,
                                                 status_code VARCHAR(20) UNIQUE NOT NULL,
                                                 status_name VARCHAR(50)        NOT NULL,
                                                 description TEXT,
                                                 is_active   BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS maintenance_plan_type (
                                                     type_id     SERIAL PRIMARY KEY,
                                                     type_code   VARCHAR(20) UNIQUE NOT NULL,
                                                     type_name   VARCHAR(50)        NOT NULL,
                                                     description TEXT,
                                                     is_active   BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS maintenance_plan_status (
                                                       status_id   SERIAL PRIMARY KEY,
                                                       status_code VARCHAR(20) UNIQUE NOT NULL,
                                                       status_name VARCHAR(50)        NOT NULL,
                                                       description TEXT,
                                                       is_active   BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS maintenance_plan_item_status (
                                                            status_id   SERIAL PRIMARY KEY,
                                                            status_code VARCHAR(20) UNIQUE NOT NULL,
                                                            status_name VARCHAR(50)        NOT NULL,
                                                            description TEXT,
                                                            is_active   BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS maintenance_program_type (
                                                        type_id     SERIAL PRIMARY KEY,
                                                        type_code   VARCHAR(20) UNIQUE NOT NULL,
                                                        type_name   VARCHAR(50)        NOT NULL,
                                                        description TEXT,
                                                        is_active   BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS outsourced_repair_status (
                                                        status_id   SERIAL PRIMARY KEY,
                                                        status_code VARCHAR(20) UNIQUE NOT NULL,
                                                        status_name VARCHAR(50)        NOT NULL,
                                                        description TEXT,
                                                        is_active   BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS advance_status (
                                              status_id   SERIAL PRIMARY KEY,
                                              status_code VARCHAR(20) UNIQUE NOT NULL,
                                              status_name VARCHAR(50)        NOT NULL,
                                              description TEXT,
                                              is_active   BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS education_level (
                                               level_id    SERIAL PRIMARY KEY,
                                               level_code  VARCHAR(20) UNIQUE NOT NULL,
                                               level_name  VARCHAR(50)        NOT NULL,
                                               description TEXT,
                                               is_active   BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS document_type_enum (
                                                  type_id     SERIAL PRIMARY KEY,
                                                  type_code   VARCHAR(20) UNIQUE NOT NULL,
                                                  type_name   VARCHAR(50)        NOT NULL,
                                                  description TEXT,
                                                  is_active   BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS data_scope_type (
                                               scope_id    SERIAL PRIMARY KEY,
                                               scope_code  VARCHAR(20) UNIQUE NOT NULL,
                                               scope_name  VARCHAR(50)        NOT NULL,
                                               description TEXT,
                                               is_active   BOOLEAN DEFAULT TRUE
);
CREATE TABLE IF NOT EXISTS project_member_role (
                                                   role_id     SERIAL PRIMARY KEY,
                                                   role_code   VARCHAR(20) UNIQUE NOT NULL,
                                                   role_name   VARCHAR(50)        NOT NULL,
                                                   description TEXT,
                                                   is_active   BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS usage_type (
                                          type_id SERIAL PRIMARY KEY,
                                          type_code VARCHAR(20) UNIQUE NOT NULL,
                                          type_name VARCHAR(50) NOT NULL,
                                          description TEXT,
                                          is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS undercarriage_type (
                                                  type_id SERIAL PRIMARY KEY,
                                                  type_code VARCHAR(20) UNIQUE NOT NULL,
                                                  type_name VARCHAR(50) NOT NULL,
                                                  description TEXT,
                                                  is_active BOOLEAN DEFAULT TRUE
);

--------------------------------------------

CREATE TABLE IF NOT EXISTS number_plate_type (
                                                 plate_type_id SERIAL PRIMARY KEY,
                                                 plate_name    VARCHAR(50) NOT NULL UNIQUE, -- White (Private), Yellow (Commercial), Green (EV), Black (Rental), Red (Temporary)
                                                 description   TEXT
);

CREATE TABLE IF NOT EXISTS permit_type (
                                           permit_type_id SERIAL PRIMARY KEY,
                                           permit_name    VARCHAR(100) NOT NULL UNIQUE, -- Private, Commercial, Tourist, School, National
                                           description    TEXT
);

CREATE TABLE IF NOT EXISTS body_style (
                                          style_id    SERIAL PRIMARY KEY,
                                          style_name  VARCHAR(50) NOT NULL UNIQUE, -- Hatchback, Sedan, SUV, MUV, Coupe, Convertible, Pickup, Van, Bus, Truck
                                          description TEXT
);

CREATE TABLE IF NOT EXISTS insurance_type (
                                              ins_type_id SERIAL PRIMARY KEY,
                                              type_name   VARCHAR(100) NOT NULL UNIQUE, -- Third Party, Comprehensive, Zero Depreciation
                                              description TEXT
);

CREATE TABLE IF NOT EXISTS purchase_type (
                                             purchase_type_id SERIAL PRIMARY KEY,
                                             type_name        VARCHAR(50) NOT NULL UNIQUE, -- New, Used, Auction, Transfer
                                             description      TEXT
);

CREATE TABLE IF NOT EXISTS seller_type (
                                           seller_type_id SERIAL PRIMARY KEY,
                                           type_name      VARCHAR(50) NOT NULL UNIQUE, -- Authorized Dealer, Individual, Auction House, Inter-Company Transfer
                                           description    TEXT
);

CREATE TABLE IF NOT EXISTS finance_status (
                                              status_id   SERIAL PRIMARY KEY,
                                              status_name VARCHAR(50) NOT NULL UNIQUE, -- Active, Closed, Defaulted, Foreclosed
                                              description TEXT
);

CREATE TABLE IF NOT EXISTS inspection_result (
                                                 result_id   SERIAL PRIMARY KEY,
                                                 result_name VARCHAR(50) NOT NULL UNIQUE, -- Pass, Fail, Conditional Pass
                                                 description TEXT
);

CREATE TABLE IF NOT EXISTS vehicle_status (
                                              status_id   SERIAL PRIMARY KEY,
                                              status_name VARCHAR(50) NOT NULL UNIQUE, -- Active, Sold, Scrapped, Stolen, Under Repair, Decommissioned
                                              description TEXT
);

CREATE TABLE IF NOT EXISTS engine_type (
                                           engineType_id   SERIAL PRIMARY KEY,
                                           engineType_name VARCHAR(50) NOT NULL UNIQUE,
                                           description TEXT
);

CREATE TABLE IF NOT EXISTS engine_manufacture (
                                                  engineManufacture_id   SERIAL PRIMARY KEY,
                                                  engineManufacture_name VARCHAR(50) NOT NULL UNIQUE,
                                                  description TEXT
);

-- Seed data scope types
INSERT INTO data_scope_type (scope_code, scope_name, description)
VALUES ('GLOBAL',     'Global',     'Access all companies (Super Admin only)'),
       ('COMPANY',    'Company',    'Access entire company'),
       ('BRANCH',     'Branch',     'Access specific branch only'),
       ('DEPARTMENT', 'Department', 'Access specific department only'),
       ('SELF',       'Self',       'Access only own employee data')
ON CONFLICT (scope_code) DO NOTHING;
-- ============================================================================
-- SECTION 2: CORE ORGANIZATION (company_id + company_code scoped)
-- ============================================================================

CREATE TABLE IF NOT EXISTS company (
                                       company_id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                       company_code    VARCHAR(50) UNIQUE  NOT NULL,
                                       company_name    VARCHAR(200)        NOT NULL,
                                       company_type_id INTEGER REFERENCES company_type (type_id),
                                       registration_no VARCHAR(100) UNIQUE NOT NULL,
                                       tax_id          VARCHAR(100),
                                       email           VARCHAR(120)        NOT NULL,
                                       phone_primary   VARCHAR(20)         NOT NULL,
                                       address         TEXT,
                                       timezone        VARCHAR(50)      DEFAULT 'Asia/Colombo',
                                       currency        VARCHAR(10)      DEFAULT 'LKR',
                                       is_active       BOOLEAN          DEFAULT TRUE,
                                       created_at      TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP,
                                       updated_at      TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP
);
CREATE TRIGGER trg_company_upd
    BEFORE UPDATE ON company FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS company_branch (
                                              branch_id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                              company_id       UUID               NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                              company_code     VARCHAR(50)        NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                              branch_code      VARCHAR(50) UNIQUE NOT NULL,
                                              branch_name      VARCHAR(200)       NOT NULL,
                                              address          TEXT,
                                              city             VARCHAR(100),
                                              state_province   VARCHAR(100),
                                              country          VARCHAR(100),
                                              latitude         DECIMAL(10, 8),
                                              longitude        DECIMAL(11, 8),
                                              is_main_workshop BOOLEAN          DEFAULT FALSE,
                                              is_active        BOOLEAN          DEFAULT TRUE,
                                              created_at       TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP,
                                              updated_at       TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_branch_company ON company_branch (company_id);
CREATE TRIGGER trg_branch_upd
    BEFORE UPDATE ON company_branch FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS department (
                                          department_id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                          company_id           UUID               NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                          company_code         VARCHAR(50)        NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                          branch_id            UUID REFERENCES company_branch (branch_id),
                                          department_code      VARCHAR(50) UNIQUE NOT NULL,
                                          department_name      VARCHAR(120)       NOT NULL,
                                          parent_department_id UUID REFERENCES department (department_id),
                                          is_active            BOOLEAN          DEFAULT TRUE,
                                          created_at           TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP,
                                          updated_at           TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_dept_company ON department (company_id);
CREATE TRIGGER trg_dept_upd
    BEFORE UPDATE ON department FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS project (
                                       project_id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                       company_id       UUID               NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                       company_code     VARCHAR(50)        NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                       branch_id        UUID REFERENCES company_branch (branch_id),
                                       project_code     VARCHAR(80) UNIQUE NOT NULL,
                                       project_name     VARCHAR(255)       NOT NULL,
                                       project_type_id  INTEGER REFERENCES project_type (type_id),
                                       site_address     TEXT,
                                       site_latitude    DECIMAL(10, 8),
                                       site_longitude   DECIMAL(11, 8),
                                       start_date       DATE               NOT NULL,
                                       planned_end_date DATE,
                                       actual_end_date  DATE,
                                       budget_amount    DECIMAL(15, 2),
                                       actual_cost      DECIMAL(15, 2),
                                       project_manager  VARCHAR(100),
                                       status_id        INTEGER REFERENCES project_status (status_id),
                                       created_at       TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP,
                                       updated_at       TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_project_company ON project (company_id);
CREATE TRIGGER trg_project_upd
    BEFORE UPDATE ON project FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS job_position (
                                            position_id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                            company_id    UUID               NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                            company_code  VARCHAR(50)        NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                            position_code VARCHAR(30) UNIQUE NOT NULL,
                                            position_name VARCHAR(100)       NOT NULL,
                                            description   TEXT,
                                            is_active     BOOLEAN          DEFAULT TRUE,
                                            created_at    TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_job_position_company ON job_position (company_id);

-- ============================================================================
-- SECTION 3: EMPLOYEE & HR MANAGEMENT (company_id + company_code scoped)
-- ============================================================================

CREATE TABLE IF NOT EXISTS employee_grade (
                                              grade_id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                              company_id             UUID               NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                              company_code           VARCHAR(50)        NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                              grade_code             VARCHAR(30) UNIQUE NOT NULL,
                                              grade_name             VARCHAR(100)       NOT NULL,
                                              category_id            INTEGER REFERENCES employee_category (category_id),
                                              base_salary            DECIMAL(12, 2)     NOT NULL,
                                              base_allowance         DECIMAL(12, 2)   DEFAULT 0,
                                              daily_allowance        DECIMAL(10, 2)   DEFAULT 0,
                                              overtime_rate_per_hour DECIMAL(10, 2),
                                              notes                  TEXT,
                                              created_at             TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_grade_company ON employee_grade (company_id);

CREATE TABLE IF NOT EXISTS employee (
                                        employee_id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                        company_id           UUID               NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                        company_code         VARCHAR(50)        NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                        branch_id            UUID REFERENCES company_branch (branch_id),
                                        department_id        UUID REFERENCES department (department_id),
                                        grade_id             UUID REFERENCES employee_grade (grade_id),
                                        position_id          UUID REFERENCES job_position (position_id),
                                        manager_id           UUID REFERENCES employee (employee_id),
                                        employee_code        VARCHAR(50) UNIQUE NOT NULL,
                                        first_name           VARCHAR(100)       NOT NULL,
                                        last_name            VARCHAR(100)       NOT NULL,
                                        date_of_birth        DATE               NOT NULL,
                                        gender_id            INTEGER REFERENCES gender (gender_id),
                                        national_id          VARCHAR(50) UNIQUE NOT NULL,

                                        mobile_phone         VARCHAR(20)        NOT NULL,
                                        work_email           VARCHAR(120) UNIQUE,
                                        current_address      TEXT,
                                        hire_date            DATE               NOT NULL,
                                        employment_type_id   INTEGER REFERENCES employment_type (type_id),
                                        job_title            VARCHAR(100),
                                        is_driver            BOOLEAN          DEFAULT FALSE,
                                        is_operator          BOOLEAN          DEFAULT FALSE,
                                        is_technician        BOOLEAN          DEFAULT FALSE,
                                        employment_status_id INTEGER REFERENCES employment_status (status_id),
                                        created_at           TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP,
                                        updated_at           TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP,
                                        CONSTRAINT chk_employee_age CHECK (EXTRACT(YEAR FROM AGE(date_of_birth)) >= 18)
);
CREATE INDEX IF NOT EXISTS idx_employee_company ON employee (company_id);
CREATE INDEX IF NOT EXISTS idx_employee_branch ON employee (branch_id);
CREATE INDEX IF NOT EXISTS idx_employee_dept ON employee (department_id);
CREATE INDEX IF NOT EXISTS idx_employee_manager ON employee (manager_id);
CREATE TRIGGER trg_employee_upd
    BEFORE UPDATE ON employee FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS employee_skill (
                                              skill_id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                              company_id        UUID         NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                              company_code      VARCHAR(50)  NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                              skill_name        VARCHAR(100) NOT NULL,
                                              skill_category_id INTEGER REFERENCES skill_category (category_id),
                                              description       TEXT,
                                              UNIQUE (company_id, skill_name)
);

CREATE TABLE IF NOT EXISTS employee_skill_assessment (
                                                         assessment_id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                         company_id      UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                                         company_code    VARCHAR(50) NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                                         employee_id     UUID NOT NULL REFERENCES employee (employee_id),
                                                         skill_id        UUID NOT NULL REFERENCES employee_skill (skill_id),
                                                         assessment_date DATE NOT NULL,
                                                         skill_level_id  INTEGER REFERENCES skill_level (level_id),
                                                         proficiency     INTEGER CHECK (proficiency BETWEEN 1 AND 10),
                                                         assessed_by     UUID REFERENCES employee (employee_id),
                                                         notes           TEXT,
                                                         created_at      TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS employee_education (
                                                  education_id     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                  company_id       UUID         NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                                  company_code     VARCHAR(50)  NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                                  employee_id      UUID         NOT NULL REFERENCES employee (employee_id) ON DELETE CASCADE,
                                                  level_id         INTEGER REFERENCES education_level (level_id),
                                                  institution_name VARCHAR(200) NOT NULL,
                                                  field_of_study   VARCHAR(200),
                                                  degree_name      VARCHAR(200),
                                                  start_date       DATE,
                                                  end_date         DATE,
                                                  is_completed     BOOLEAN          DEFAULT FALSE,
                                                  grade_gpa        VARCHAR(20),
                                                  notes            TEXT,
                                                  created_at       TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_employee_education ON employee_education (employee_id);

CREATE TABLE IF NOT EXISTS employee_document (
                                                 document_id     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                 company_id      UUID         NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                                 company_code    VARCHAR(50)  NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                                 employee_id     UUID         NOT NULL REFERENCES employee (employee_id) ON DELETE CASCADE,
                                                 document_type   VARCHAR(50)  NOT NULL,
                                                 document_name   VARCHAR(200) NOT NULL,
                                                 document_number VARCHAR(100),
                                                 file_path       TEXT         NOT NULL,
                                                 file_size_bytes BIGINT,
                                                 mime_type       VARCHAR(100),
                                                 issue_date      DATE,
                                                 expiry_date     DATE,
                                                 is_verified     BOOLEAN          DEFAULT FALSE,
                                                 verified_by     UUID REFERENCES employee (employee_id),
                                                 notes           TEXT,
                                                 uploaded_at     TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_employee_document ON employee_document (employee_id);

    CREATE TABLE IF NOT EXISTS project_member (
                                              member_id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                              company_id  UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                              company_code VARCHAR(50) NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                              project_id  UUID NOT NULL REFERENCES project (project_id) ON DELETE CASCADE,
                                              employee_id UUID NOT NULL REFERENCES employee (employee_id) ON DELETE CASCADE,
                                              role_id     INTEGER REFERENCES project_member_role (role_id),
                                              joined_date DATE NOT NULL    DEFAULT CURRENT_DATE,
                                              left_date   DATE,
                                              is_active   BOOLEAN          DEFAULT TRUE,
                                              created_at  TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP,
                                              UNIQUE (project_id, employee_id)
);
CREATE INDEX IF NOT EXISTS idx_project_member_project ON project_member (project_id);
CREATE INDEX IF NOT EXISTS idx_project_member_employee ON project_member (employee_id);

CREATE TABLE IF NOT EXISTS employee_training (
                                                 training_id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                 company_id       UUID         NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                                 company_code     VARCHAR(50)  NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                                 training_name    VARCHAR(200) NOT NULL,
                                                 training_type_id INTEGER REFERENCES training_type (type_id),
                                                 description      TEXT,
                                                 duration_hours   INTEGER,
                                                 provider         VARCHAR(100),
                                                 created_at       TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS employee_training_record (
                                                        training_record_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                        company_id         UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                                        company_code       VARCHAR(50) NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                                        employee_id        UUID NOT NULL REFERENCES employee (employee_id),
                                                        training_id        UUID NOT NULL REFERENCES employee_training (training_id),
                                                        training_date      DATE NOT NULL,
                                                        completion_date    DATE,
                                                        status_id          INTEGER REFERENCES training_status (status_id),
                                                        score              DECIMAL(5, 2),
                                                        certificate_number VARCHAR(100),
                                                        notes              TEXT,
                                                        created_at         TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS employee_complaint (
                                                  complaint_id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                  company_id        UUID         NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                                  company_code      VARCHAR(50)  NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                                  employee_id       UUID         NOT NULL REFERENCES employee (employee_id),
                                                  complaint_date    DATE         NOT NULL,
                                                  complaint_type_id INTEGER REFERENCES complaint_type (type_id),
                                                  subject           VARCHAR(200) NOT NULL,
                                                  description       TEXT         NOT NULL,
                                                  priority_id       INTEGER REFERENCES complaint_priority (priority_id),
                                                  status_id         INTEGER REFERENCES complaint_status (status_id),
                                                  assigned_to       UUID REFERENCES employee (employee_id),
                                                  resolution        TEXT,
                                                  resolved_date     DATE,
                                                  created_at        TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS employee_performance_review (
                                                           review_id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                           company_id            UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                                           company_code          VARCHAR(50) NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                                           employee_id           UUID NOT NULL REFERENCES employee (employee_id),
                                                           review_date           DATE NOT NULL,
                                                           reviewer_id           UUID NOT NULL REFERENCES employee (employee_id),
                                                           performance_score     DECIMAL(5, 2) CHECK (performance_score BETWEEN 0 AND 100),
                                                           attendance_score      DECIMAL(5, 2),
                                                           productivity_score    DECIMAL(5, 2),
                                                           safety_score          DECIMAL(5, 2),
                                                           overall_rating_id     INTEGER REFERENCES performance_rating (rating_id),
                                                           strengths             TEXT,
                                                           areas_for_improvement TEXT,
                                                           goals                 TEXT,
                                                           next_review_date      DATE,
                                                           created_at            TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- SECTION 4: AUTHENTICATION (Auth 2.0)
-- ============================================================================

CREATE TABLE IF NOT EXISTS app_user (
                                        user_id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                        company_id            UUID REFERENCES company (company_id) ON DELETE CASCADE,
                                        company_code          VARCHAR(50) REFERENCES company (company_code) ON UPDATE CASCADE,
                                        employee_id           UUID UNIQUE REFERENCES employee (employee_id) ON DELETE CASCADE,
                                        username              VARCHAR(50) UNIQUE  NOT NULL,
                                        email                 VARCHAR(120) UNIQUE NOT NULL,
                                        password_hash         VARCHAR(255)        NOT NULL,
                                        is_super_admin        BOOLEAN          DEFAULT FALSE,
                                        is_company_admin      BOOLEAN          DEFAULT FALSE,
                                        is_active             BOOLEAN          DEFAULT TRUE,
                                        is_locked             BOOLEAN          DEFAULT FALSE,
                                        failed_login_attempts INTEGER          DEFAULT 0,
                                        last_login_at         TIMESTAMPTZ,
                                        password_changed_at   TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP,
                                        email_verified        BOOLEAN          DEFAULT FALSE,
                                        email_verified_at     TIMESTAMPTZ,
                                        created_at            TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP,
                                        updated_at            TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_user_username ON app_user (username);
CREATE INDEX IF NOT EXISTS idx_user_company ON app_user (company_id);
CREATE TRIGGER trg_user_upd
    BEFORE UPDATE ON app_user FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS user_session (
                                            session_id    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                            user_id       UUID                NOT NULL REFERENCES app_user (user_id) ON DELETE CASCADE,
                                            company_id    UUID REFERENCES company (company_id) ON DELETE CASCADE,
                                            company_code  VARCHAR(50) REFERENCES company (company_code) ON UPDATE CASCADE,
                                            session_token VARCHAR(255) UNIQUE NOT NULL,
                                            ip_address    INET,
                                            user_agent    TEXT,
                                            created_at    TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP,
                                            last_activity TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP,
                                            expires_at    TIMESTAMPTZ         NOT NULL,
                                            is_active     BOOLEAN          DEFAULT TRUE
);
CREATE INDEX IF NOT EXISTS idx_user_session_user ON user_session (user_id, expires_at);

CREATE TABLE IF NOT EXISTS login_history (
                                             history_id     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                             user_id        UUID NOT NULL REFERENCES app_user (user_id) ON DELETE CASCADE,
                                             company_id     UUID REFERENCES company (company_id) ON DELETE CASCADE,
                                             company_code   VARCHAR(50) REFERENCES company (company_code) ON UPDATE CASCADE,
                                             username       VARCHAR(50),
                                             login_time     TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP,
                                             logout_time    TIMESTAMPTZ,
                                             ip_address     INET,
                                             user_agent     TEXT,
                                             status_id      INTEGER REFERENCES login_status (status_id),
                                             failure_reason TEXT
);
CREATE INDEX IF NOT EXISTS idx_login_history_user ON login_history (user_id, login_time DESC);

CREATE TABLE IF NOT EXISTS user_history (
                                            history_id  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                            user_id     UUID        NOT NULL REFERENCES app_user (user_id) ON DELETE CASCADE,
                                            company_id  UUID REFERENCES company (company_id) ON DELETE CASCADE,
                                            company_code VARCHAR(50) REFERENCES company (company_code) ON UPDATE CASCADE,
                                            action      VARCHAR(50) NOT NULL,
                                            entity_type VARCHAR(100),
                                            entity_id   UUID,
                                            old_values  JSONB,
                                            new_values  JSONB,
                                            ip_address  INET,
                                            user_agent  TEXT,
                                            created_at  TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_user_history_user ON user_history (user_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_user_history_entity ON user_history (entity_type, entity_id);

CREATE TABLE IF NOT EXISTS password_reset_token (
                                                    token_id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                    user_id    UUID         NOT NULL REFERENCES app_user (user_id) ON DELETE CASCADE,
                                                    token_hash VARCHAR(255) NOT NULL,
                                                    expires_at TIMESTAMPTZ  NOT NULL,
                                                    used_at    TIMESTAMPTZ,
                                                    is_used    BOOLEAN          DEFAULT FALSE,
                                                    created_at TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_password_reset_user ON password_reset_token (user_id, is_used);

CREATE TABLE IF NOT EXISTS email_verification_token (
                                                        token_id    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                        user_id     UUID         NOT NULL REFERENCES app_user (user_id) ON DELETE CASCADE,
                                                        token_hash  VARCHAR(255) NOT NULL,
                                                        email       VARCHAR(120) NOT NULL,
                                                        expires_at  TIMESTAMPTZ  NOT NULL,
                                                        verified_at TIMESTAMPTZ,
                                                        is_used     BOOLEAN          DEFAULT FALSE,
                                                        created_at  TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_email_verify_user ON email_verification_token (user_id, is_used);

-- ============================================================================
-- SECTION 5: RBAC – ROLE & PERMISSION SYSTEM
-- ============================================================================

CREATE TABLE IF NOT EXISTS system_module (
                                             module_id     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                             module_code   VARCHAR(50) UNIQUE NOT NULL,
                                             module_name   VARCHAR(100)       NOT NULL,
                                             description   TEXT,
                                             display_order INTEGER          DEFAULT 0,
                                             is_active     BOOLEAN          DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS permission (
                                          permission_id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                          permission_code VARCHAR(120) UNIQUE NOT NULL,
                                          module_id       UUID REFERENCES system_module (module_id),
                                          description     TEXT,
                                          is_active       BOOLEAN          DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS role (
                                    role_id     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                    company_id  UUID REFERENCES company (company_id) ON DELETE CASCADE,
                                    company_code VARCHAR(50) REFERENCES company (company_code) ON UPDATE CASCADE,
                                    role_code   VARCHAR(80) UNIQUE NOT NULL,
                                    role_name   VARCHAR(80)        NOT NULL,
                                    description TEXT,
                                    is_system   BOOLEAN          DEFAULT FALSE,
                                    is_active   BOOLEAN          DEFAULT TRUE,
                                    created_at  TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_role_company ON role (company_id);

CREATE TABLE IF NOT EXISTS role_permission (
                                               role_id       UUID REFERENCES role (role_id) ON DELETE CASCADE,
                                               permission_id UUID REFERENCES permission (permission_id) ON DELETE CASCADE,
                                               grant_type    VARCHAR(10) NOT NULL DEFAULT 'GRANT' CHECK (grant_type IN ('GRANT', 'DENY')),
                                               PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE IF NOT EXISTS role_hierarchy (
                                              hierarchy_id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                              parent_role_id UUID NOT NULL REFERENCES role (role_id) ON DELETE CASCADE,
                                              child_role_id  UUID NOT NULL REFERENCES role (role_id) ON DELETE CASCADE,
                                              created_at     TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP,
                                              UNIQUE (parent_role_id, child_role_id),
                                              CONSTRAINT chk_no_self_hierarchy CHECK (parent_role_id != child_role_id)
);
CREATE INDEX IF NOT EXISTS idx_role_hierarchy_parent ON role_hierarchy (parent_role_id);
CREATE INDEX IF NOT EXISTS idx_role_hierarchy_child ON role_hierarchy (child_role_id);

CREATE TABLE IF NOT EXISTS user_role (
                                         user_role_id  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                         user_id       UUID NOT NULL REFERENCES app_user (user_id) ON DELETE CASCADE,
                                         role_id       UUID NOT NULL REFERENCES role (role_id) ON DELETE CASCADE,
                                         company_id    UUID REFERENCES company (company_id) ON DELETE CASCADE,
                                         company_code  VARCHAR(50) REFERENCES company (company_code) ON UPDATE CASCADE,
                                         branch_id     UUID REFERENCES company_branch (branch_id) ON DELETE CASCADE,
                                         department_id UUID REFERENCES department (department_id) ON DELETE CASCADE,
                                         assigned_at   TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP,
                                         assigned_by   UUID REFERENCES app_user (user_id),
                                         expires_at    TIMESTAMPTZ,
                                         is_active     BOOLEAN          DEFAULT TRUE,
                                         UNIQUE (user_id, role_id, company_id, branch_id, department_id)
);
CREATE INDEX IF NOT EXISTS idx_user_role_user ON user_role (user_id, is_active);
CREATE INDEX IF NOT EXISTS idx_user_role_scope ON user_role (company_id, branch_id, department_id);

CREATE TABLE IF NOT EXISTS user_permission (
                                               user_permission_id UUID PRIMARY KEY     DEFAULT gen_random_uuid(),
                                               user_id            UUID        NOT NULL REFERENCES app_user (user_id) ON DELETE CASCADE,
                                               permission_id      UUID        NOT NULL REFERENCES permission (permission_id) ON DELETE CASCADE,
                                               grant_type         VARCHAR(10) NOT NULL DEFAULT 'GRANT' CHECK (grant_type IN ('GRANT', 'DENY')),
                                               assigned_by        UUID REFERENCES app_user (user_id),
                                               assigned_at        TIMESTAMPTZ          DEFAULT CURRENT_TIMESTAMP,
                                               UNIQUE (user_id, permission_id)
);

CREATE TABLE IF NOT EXISTS user_data_scope (
                                               scope_id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                               user_id       UUID    NOT NULL REFERENCES app_user (user_id) ON DELETE CASCADE,
                                               scope_type_id INTEGER NOT NULL REFERENCES data_scope_type (scope_id),
                                               company_id    UUID REFERENCES company (company_id) ON DELETE CASCADE,
                                               company_code  VARCHAR(50) REFERENCES company (company_code) ON UPDATE CASCADE,
                                               branch_id     UUID REFERENCES company_branch (branch_id) ON DELETE CASCADE,
                                               department_id UUID REFERENCES department (department_id) ON DELETE CASCADE,
                                               created_at    TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP,
                                               is_active     BOOLEAN          DEFAULT TRUE,
                                               UNIQUE (user_id, scope_type_id, company_id, branch_id, department_id)
);
CREATE INDEX IF NOT EXISTS idx_user_data_scope ON user_data_scope (user_id, is_active);

-- ============================================================================
-- SECTION 6: SYSTEM SERVICES & DASHBOARD
-- ============================================================================

CREATE TABLE IF NOT EXISTS system_service (
                                              service_id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                              module_id           UUID REFERENCES system_module (module_id),
                                              service_code        VARCHAR(50) UNIQUE NOT NULL,
                                              service_name        VARCHAR(100)       NOT NULL,
                                              service_description TEXT,
                                              service_path        VARCHAR(200),
                                              icon_name           VARCHAR(50),
                                              parent_service_id   UUID REFERENCES system_service (service_id),
                                              display_order       INTEGER          DEFAULT 0,
                                              is_active           BOOLEAN          DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS role_service_permission (
                                                       role_id    UUID REFERENCES role (role_id) ON DELETE CASCADE,
                                                       service_id UUID REFERENCES system_service (service_id) ON DELETE CASCADE,
                                                       can_access BOOLEAN DEFAULT FALSE,
                                                       can_create BOOLEAN DEFAULT FALSE,
                                                       can_edit   BOOLEAN DEFAULT FALSE,
                                                       can_delete BOOLEAN DEFAULT FALSE,
                                                       can_export BOOLEAN DEFAULT FALSE,
                                                       PRIMARY KEY (role_id, service_id)
);

CREATE TABLE IF NOT EXISTS user_service_access (
                                                   user_id    UUID NOT NULL REFERENCES app_user (user_id) ON DELETE CASCADE,
                                                   service_id UUID NOT NULL REFERENCES system_service (service_id) ON DELETE CASCADE,
                                                   can_access BOOLEAN,
                                                   can_create BOOLEAN,
                                                   can_edit   BOOLEAN,
                                                   can_delete BOOLEAN,
                                                   can_export BOOLEAN,
                                                   updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                                   PRIMARY KEY (user_id, service_id)
);
CREATE INDEX IF NOT EXISTS idx_user_service_access_user ON user_service_access (user_id);

CREATE TABLE IF NOT EXISTS dashboard_config (
                                                config_id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                company_id       UUID REFERENCES company (company_id) ON DELETE CASCADE,
                                                company_code     VARCHAR(50) REFERENCES company (company_code) ON UPDATE CASCADE,
                                                user_id          UUID REFERENCES app_user (user_id) ON DELETE CASCADE,
                                                role_id          UUID REFERENCES role (role_id) ON DELETE CASCADE,
                                                widget_name      VARCHAR(100) NOT NULL,
                                                widget_type      VARCHAR(50)  NOT NULL,
                                                widget_title     VARCHAR(100) NOT NULL,
                                                position_x       INTEGER          DEFAULT 0,
                                                position_y       INTEGER          DEFAULT 0,
                                                width            INTEGER          DEFAULT 4,
                                                height           INTEGER          DEFAULT 4,
                                                is_visible       BOOLEAN          DEFAULT TRUE,
                                                refresh_interval INTEGER          DEFAULT 300,
                                                config_data      JSONB,
                                                created_at       TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP,
                                                updated_at       TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP
);
CREATE TRIGGER trg_dashboard_config_upd
    BEFORE UPDATE ON dashboard_config FOR EACH ROW EXECUTE FUNCTION set_updated_at();
CREATE UNIQUE INDEX IF NOT EXISTS ux_dashboard_user_widget ON dashboard_config (user_id, widget_name) WHERE user_id IS NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS ux_dashboard_role_widget ON dashboard_config (role_id, widget_name) WHERE role_id IS NOT NULL;

-- ============================================================================
-- SECTION 7: ATTENDANCE / OVERTIME / LEAVE
-- ============================================================================

CREATE TABLE IF NOT EXISTS attendance (
                                          attendance_id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                          company_id      UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                          company_code    VARCHAR(50) NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                          employee_id     UUID NOT NULL REFERENCES employee (employee_id) ON DELETE CASCADE,
                                          attendance_date DATE NOT NULL,
                                          check_in_time   TIMESTAMPTZ,
                                          check_out_time  TIMESTAMPTZ,
                                          project_id      UUID REFERENCES project (project_id),
                                          latitude_in     DECIMAL(10, 8),
                                          longitude_in    DECIMAL(11, 8),
                                          latitude_out    DECIMAL(10, 8),
                                          longitude_out   DECIMAL(11, 8),
                                          scheduled_hours DECIMAL(5, 2)    DEFAULT 8,
                                          actual_hours    DECIMAL(5, 2),
                                          overtime_hours  DECIMAL(5, 2)    DEFAULT 0,
                                          status_id       INTEGER REFERENCES attendance_status (status_id),
                                          UNIQUE (employee_id, attendance_date)
);
CREATE INDEX IF NOT EXISTS idx_attendance_emp_date ON attendance (employee_id, attendance_date DESC);
CREATE INDEX IF NOT EXISTS idx_attendance_company ON attendance (company_id);

CREATE TABLE IF NOT EXISTS overtime_request (
                                                overtime_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                company_id  UUID          NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                                company_code VARCHAR(50)  NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                                employee_id UUID          NOT NULL REFERENCES employee (employee_id) ON DELETE CASCADE,
                                                project_id  UUID REFERENCES project (project_id),
                                                ot_date     DATE          NOT NULL,
                                                hours       DECIMAL(5, 2) NOT NULL,
                                                ot_type_id  INTEGER REFERENCES overtime_type (type_id),
                                                approved    BOOLEAN          DEFAULT FALSE,
                                                approved_by UUID REFERENCES employee (employee_id),
                                                approved_at TIMESTAMPTZ,
                                                created_at  TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS leave_type (
                                          leave_type_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                          company_id    UUID               NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                          company_code  VARCHAR(50)        NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                          leave_code    VARCHAR(20) UNIQUE NOT NULL,
                                          leave_name    VARCHAR(100)       NOT NULL,
                                          days_per_year DECIMAL(5, 2)    DEFAULT 14
);

CREATE TABLE IF NOT EXISTS leave_application (
                                                 leave_id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                 company_id    UUID          NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                                 company_code  VARCHAR(50)   NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                                 employee_id   UUID          NOT NULL REFERENCES employee (employee_id) ON DELETE CASCADE,
                                                 leave_type_id UUID          NOT NULL REFERENCES leave_type (leave_type_id),
                                                 start_date    DATE          NOT NULL,
                                                 end_date      DATE          NOT NULL,
                                                 total_days    DECIMAL(5, 2) NOT NULL,
                                                 status_id     INTEGER REFERENCES leave_application_status (status_id),
                                                 applied_at    TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP,
                                                 approved_by   UUID REFERENCES employee (employee_id),
                                                 approved_at   TIMESTAMPTZ,
                                                 updated_at    TIMESTAMPTZ      DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================================
-- SECTION 8: RATIONS / ADVANCES / PAYROLL
-- ============================================================================

CREATE TABLE IF NOT EXISTS ration_policy (
                                             ration_policy_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                             company_id       UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                             company_code     VARCHAR(50) NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                             policy_name      VARCHAR(100) NOT NULL,
                                             per_day_amount   DECIMAL(10, 2) NOT NULL DEFAULT 0,
                                             notes            TEXT,
                                             is_active        BOOLEAN DEFAULT TRUE,
                                             created_at       TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ration_distribution (
                                                   ration_id    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                   company_id   UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                                   company_code VARCHAR(50) NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                                   employee_id  UUID NOT NULL REFERENCES employee (employee_id),
                                                   project_id   UUID REFERENCES project (project_id),
                                                   ration_date  DATE NOT NULL,
                                                   meals_count  INT DEFAULT 1,
                                                   amount       DECIMAL(10, 2) NOT NULL,
                                                   notes        TEXT,
                                                   created_at   TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                                   UNIQUE (employee_id, ration_date)
);

CREATE TABLE IF NOT EXISTS employee_advance (
                                                advance_id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                company_id   UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                                company_code VARCHAR(50) NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                                employee_id  UUID NOT NULL REFERENCES employee (employee_id),
                                                issued_date  DATE NOT NULL,
                                                amount       DECIMAL(12, 2) NOT NULL,
                                                balance      DECIMAL(12, 2) NOT NULL,
                                                purpose      TEXT,
                                                status_id    INTEGER REFERENCES advance_status (status_id),
                                                created_at   TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS payroll (
                                       payroll_id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                       company_id         UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                       company_code       VARCHAR(50) NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                       employee_id        UUID NOT NULL REFERENCES employee (employee_id) ON DELETE CASCADE,
                                       payroll_month      INTEGER NOT NULL CHECK (payroll_month BETWEEN 1 AND 12),
                                       payroll_year       INTEGER NOT NULL,
                                       basic_salary       DECIMAL(12, 2) NOT NULL,
                                       overtime_hours     DECIMAL(6, 2) DEFAULT 0,
                                       overtime_amount    DECIMAL(10, 2) DEFAULT 0,
                                       ration_total       DECIMAL(10, 2) DEFAULT 0,
                                       allowances_total   DECIMAL(10, 2) DEFAULT 0,
                                       advance_deductions DECIMAL(10, 2) DEFAULT 0,
                                       other_deductions   DECIMAL(10, 2) DEFAULT 0,
                                       net_salary         DECIMAL(12, 2) NOT NULL,
                                       payment_date       DATE,
                                       payment_method_id  INTEGER REFERENCES payroll_payment_method (method_id),
                                       status_id          INTEGER REFERENCES payroll_status (status_id),
                                       created_at         TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                       UNIQUE (employee_id, payroll_month, payroll_year)
);

CREATE TABLE IF NOT EXISTS payroll_deduction (
                                                 pay_ded_id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                 company_id        UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                                 company_code      VARCHAR(50) NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                                 payroll_id        UUID NOT NULL REFERENCES payroll (payroll_id) ON DELETE CASCADE,
                                                 deduction_type_id INTEGER REFERENCES payroll_deduction_type (type_id),
                                                 reference_id      UUID,
                                                 amount            DECIMAL(12, 2) NOT NULL,
                                                 note              TEXT
);

-- ============================================================================
-- SECTION 9: VEHICLE & EQUIPMENT MANAGEMENT
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. VEHICLE CATEGORY
-- ----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS vehicle_category (
                                                category_id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                category_name    VARCHAR(100) NOT NULL,
                                                category_code    VARCHAR(50) NOT NULL,
                                                category_type_id INTEGER REFERENCES vehicle_category_type (type_id),
                                                description      TEXT,
                                                icon_url         TEXT,
                                                is_active        BOOLEAN DEFAULT TRUE,
                                                created_at       TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                                updated_at       TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                                UNIQUE (category_name, category_code)
);
CREATE TRIGGER trg_vehicle_category_upd BEFORE UPDATE ON vehicle_category FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- ----------------------------------------------------------------------------
-- 2. VEHICLE TYPE
-- ----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS vehicle_type (
                                            type_id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                            category_id             UUID NOT NULL REFERENCES vehicle_category (category_id),
                                            type_name               VARCHAR(100) NOT NULL,
                                            body_style_id           INTEGER REFERENCES body_style (style_id),
                                            fuel_type_id            INTEGER REFERENCES fuel_type (type_id),
                                            undercarriage_type_id   INTEGER REFERENCES undercarriage_type (type_id),
                                            number_of_wheels        INTEGER,
                                            seating_capacity_min    INTEGER,
                                            seating_capacity_max    INTEGER,
                                            usage_type              VARCHAR(50),
                                            service_interval_km     INTEGER,
                                            service_interval_months INTEGER,
                                            service_interval_hours  INTEGER,
                                            oil_change_interval_km  INTEGER,
                                            description             TEXT,
                                            is_active               BOOLEAN DEFAULT TRUE,
                                            created_at              TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                            updated_at              TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                            UNIQUE (type_name)
);
CREATE TRIGGER trg_vehicle_type_upd BEFORE UPDATE ON vehicle_type FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS companyVehicleIdType (
                                                    idtype_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                    idtype_com UUID REFERENCES company(company_id) ON DELETE CASCADE,
                                                    company_code VARCHAR REFERENCES company(company_code),
                                                    idtype_typeid UUID REFERENCES vehicle_type(type_id)ON DELETE CASCADE,
                                                    idtype_code VARCHAR(10),
                                                    is_active BOOLEAN DEFAULT TRUE,
                                                    created_at timestamptz DEFAULT CURRENT_TIMESTAMP,
                                                    updated_at timestamptz  DEFAULT CURRENT_TIMESTAMP
);
-- ----------------------------------------------------------------------------
-- 3. VEHICLE MANUFACTURER (BRAND)
-- ----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS vehicle_manufacturer (
                                                    manufacturer_id   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                    manufacturer_name VARCHAR(100) NOT NULL,
                                                    manufacturer_code VARCHAR(20)  NOT NULL,
                                                    manufacturer_brand VARCHAR(100),
                                                    country           VARCHAR(100) NOT NULL,
                                                    logo_url          TEXT,
                                                    website           VARCHAR(200),
                                                    support_phone     VARCHAR(20),
                                                    support_email     VARCHAR(100),
                                                    description       TEXT,
                                                    is_active         BOOLEAN DEFAULT TRUE,
                                                    created_at        TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                                    updated_at        TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                                    UNIQUE(manufacturer_name, country)
);
CREATE TRIGGER trg_vehicle_manufacturer_upd BEFORE UPDATE ON vehicle_manufacturer FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- Manufacturer ↔ Category mapping (one brand can support multiple categories)
CREATE TABLE IF NOT EXISTS manufacturer_category (
                                                     id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                     manufacturer_id UUID NOT NULL REFERENCES vehicle_manufacturer (manufacturer_id) ON DELETE CASCADE,
                                                     category_id     UUID NOT NULL REFERENCES vehicle_category (category_id) ON DELETE CASCADE,
                                                     UNIQUE (manufacturer_id, category_id)
);

CREATE TABLE IF NOT EXISTS distributor (
                                           distributor_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                           manufacturer_id UUID REFERENCES vehicle_manufacturer (manufacturer_id),
                                           distributor_name VARCHAR(150) NOT NULL,
                                           distributor_country VARCHAR(100) NOT NULL,
                                           distributor_location VARCHAR(100),
                                           distributor_logo TEXT,
                                           distributor_phoneNumber VARCHAR(20),
                                           distributor_email VARCHAR(100),
                                           distributor_description TEXT,
                                           is_active BOOLEAN DEFAULT TRUE,
                                           create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                           update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- ----------------------------------------------------------------------------
-- 4. VEHICLE MODEL
-- ----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS vehicle_model (
                                             model_id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                             manufacturer_id      UUID NOT NULL REFERENCES vehicle_manufacturer (manufacturer_id),
                                             type_id              UUID NOT NULL REFERENCES vehicle_type (type_id),
                                             model_name           VARCHAR(100) NOT NULL,
                                             model_code           VARCHAR(50),
                                             model_year           INTEGER,
                                             body_style_id        INTEGER REFERENCES body_style (style_id),
                                             fuel_type_id         INTEGER REFERENCES fuel_type (type_id),
                                             engine_capacity_cc   INTEGER,
                                             power_hp             INTEGER,
                                             torque_nm            INTEGER,
                                             number_of_cylinders  INTEGER,
                                             transmission_type_id INTEGER REFERENCES transmission_type (type_id),
                                             drivetrain_type_id   INTEGER REFERENCES drivetrain_type (type_id),
                                             seating_capacity     INTEGER,
                                             number_of_doors      INTEGER,
                                             kerb_weight_kg       DECIMAL(8, 2),
                                             gvw_kg               DECIMAL(8, 2),
                                             wheelbase_mm         INTEGER,
                                             fuel_efficiency_kmpl DECIMAL(6, 2),
                                             launch_year          INTEGER,
                                             description          TEXT,
                                             image_url            TEXT,
                                             is_active            BOOLEAN DEFAULT TRUE,
                                             created_at           TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                             updated_at           TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                             UNIQUE (model_name)
);
CREATE TRIGGER trg_vehicle_model_upd BEFORE UPDATE ON vehicle_model FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- Model Variants (e.g., Base, Mid, Top, Legender)
CREATE TABLE IF NOT EXISTS vehicle_model_variant (
                                                     variant_id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                     model_id        UUID NOT NULL REFERENCES vehicle_model (model_id) ON DELETE CASCADE,
                                                     variant_name    VARCHAR(100) NOT NULL,
                                                     variant_code    VARCHAR(50),
                                                     additional_features TEXT,
                                                     price_ex_showroom   DECIMAL(15, 2),
                                                     is_active       BOOLEAN DEFAULT TRUE,
                                                     created_at      TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                                     UNIQUE (model_id, variant_name)
);

-- ----------------------------------------------------------------------------
-- 5. VEHICLE (MASTER RECORD)
-- ----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS vehicle (
                                       vehicle_id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    -- Model & Variant
                                       modelVariant_id         UUID NOT NULL REFERENCES vehicle_model_variant (variant_id),
                                       vehicle_model           UUID NOT NULL REFERENCES vehicle_model(model_id),
                                       vehicle_type            UUID NOT NULL REFERENCES vehicle_type(type_id),
                                       vehicle_category        UUID NOT NULL REFERENCES vehicle_category(category_id),

    -- Identification
                                       registration_number     VARCHAR(50) UNIQUE,          -- Quick-access copy from vehicle_registration
                                       chassis_number          VARCHAR(100) UNIQUE NOT NULL,
                                       engine_number           VARCHAR(100) NOT NULL,
                                       key_number              VARCHAR(50),
                                       vehicle_image           TEXT,

    -- Specs
                                       manufacture_year        INTEGER NOT NULL,
                                       color                   VARCHAR(50),
                                       fuel_type_id            INTEGER REFERENCES fuel_type (type_id),
                                       transmission_type_id    INTEGER REFERENCES transmission_type (type_id),
                                       number_plate_type_id    INTEGER REFERENCES number_plate_type (plate_type_id),
                                       body_style_id           INTEGER REFERENCES body_style (style_id),
                                       seating_capacity        INTEGER,
                                       undercarriage_type      INTEGER REFERENCES undercarriage_type(type_id),
                                       engine_type             INTEGER REFERENCES engine_type(engineType_id),
                                       engine_manufacture      INTEGER REFERENCES engine_manufacture(engineManufacture_id),
    -- Odometer & Engine Hours
                                       initial_odometer_km     DECIMAL(12, 2) DEFAULT 0,
                                       current_odometer_km     DECIMAL(12, 2) DEFAULT 0,
                                       total_engine_hours      DECIMAL(12, 2) DEFAULT 0,

    -- Fuel / Consumption
                                       consumption_method_id   INTEGER REFERENCES consumption_method (method_id),
                                       rated_efficiency_kmpl   DECIMAL(8, 3),
                                       rated_consumption_lph   DECIMAL(8, 3),

    -- Ownership
                                       ownership_type_id       INTEGER REFERENCES ownership_type (type_id) NOT NULL,
                                       current_ownership       VARCHAR(200),
                                       previous_owners_count   INTEGER DEFAULT 0,
                                       manufacture_id          UUID REFERENCES vehicle_manufacturer(manufacturer_id),
                                       distributor_id          UUID REFERENCES distributor(distributor_id),
                                       vehicle_condition       VARCHAR(20) CHECK (vehicle_condition IN ('New', 'Used', 'Damaged')) DEFAULT 'New',

    -- Operational
                                       operational_status_id   INTEGER REFERENCES operational_status (status_id),
                                       vehicle_status_id       INTEGER REFERENCES vehicle_status (status_id),

    -- Misc
                                       notes                   TEXT,
                                       is_active               BOOLEAN DEFAULT TRUE,
                                       created_by              UUID REFERENCES employee (employee_id),
                                       updated_by              UUID REFERENCES employee (employee_id),
                                       created_at              TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                       updated_at              TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_vehicle_status         ON vehicle (operational_status_id);
CREATE INDEX IF NOT EXISTS idx_vehicle_registration   ON vehicle (registration_number);
CREATE INDEX IF NOT EXISTS idx_vehicle_ownership      ON vehicle (ownership_type_id);
CREATE INDEX IF NOT EXISTS idx_vehicle_chassis        ON vehicle (chassis_number);
CREATE INDEX IF NOT EXISTS idx_vehicle_active_status  ON vehicle (operational_status_id, ownership_type_id) WHERE is_active = TRUE;

CREATE TRIGGER trg_vehicle_upd BEFORE UPDATE ON vehicle FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS company_vehicles (
                                                companyVehicle_id UUID PRIMARY KEY  DEFAULT gen_random_uuid(),
                                                company_id UUID NOT NULL REFERENCES company (company_id),
                                                company_code VARCHAR NOT NULL REFERENCES company(company_code) ON DELETE CASCADE,
                                                company_project UUID REFERENCES project(project_id),
                                                company_branch UUID REFERENCES company_branch(branch_id),
                                                company_department UUID REFERENCES department(department_id),

    -- Model & Variant
                                                companyVehicle_category UUID NOT NULL REFERENCES vehicle_category(category_id),
                                                companyVehicle_manufacture UUID NOT NULL REFERENCES vehicle_manufacturer(manufacturer_id),

    -- Identification
                                                registration_number     VARCHAR(50) UNIQUE,          -- Quick-access copy from vehicle_registration
                                                chassis_number          VARCHAR(100) UNIQUE NOT NULL,
                                                engine_number           VARCHAR(100) NOT NULL,
                                                key_number              VARCHAR(50),
                                                vehicle_image           TEXT,

    -- Specs
                                                manufacture_year        INTEGER NOT NULL,
                                                color                   VARCHAR(50),
                                                fuel_type_id            INTEGER REFERENCES fuel_type (type_id),
                                                transmission_type_id    INTEGER REFERENCES transmission_type (type_id),
                                                number_plate_type_id    INTEGER REFERENCES number_plate_type (plate_type_id),
                                                body_style_id           INTEGER REFERENCES body_style (style_id),
                                                seating_capacity        INTEGER,
                                                undercarriage_type      INTEGER REFERENCES undercarriage_type(type_id),
                                                engine_type             INTEGER REFERENCES engine_type(engineType_id),
                                                engine_manufacture      INTEGER REFERENCES engine_manufacture(engineManufacture_id),
    -- Odometer & Engine Hours
                                                initial_odometer_km     DECIMAL(12, 2) DEFAULT 0,
                                                current_odometer_km     DECIMAL(12, 2) DEFAULT 0,
                                                total_engine_hours      DECIMAL(12, 2) DEFAULT 0,

    -- Fuel / Consumption
                                                consumption_method_id   INTEGER REFERENCES consumption_method (method_id),
                                                rated_efficiency_kmpl   DECIMAL(8, 3),
                                                rated_consumption_lph   DECIMAL(8, 3),

    -- Ownership
                                                ownership_type_id       INTEGER REFERENCES ownership_type (type_id) NOT NULL,
                                                current_ownership       VARCHAR(200),
                                                previous_owners_count   INTEGER DEFAULT 0,
                                                manufacture_id          UUID REFERENCES vehicle_manufacturer(manufacturer_id),
                                                distributor_id          UUID REFERENCES distributor(distributor_id),
                                                vehicle_condition       VARCHAR(20) CHECK (vehicle_condition IN ('New', 'Used', 'Damaged')) DEFAULT 'New',

    -- Operational
                                                operational_status_id   INTEGER REFERENCES operational_status (status_id),
                                                vehicle_status_id       INTEGER REFERENCES vehicle_status (status_id),

    -- Misc
                                                notes                   TEXT,
                                                is_active               BOOLEAN DEFAULT TRUE,
                                                created_by              UUID REFERENCES employee (employee_id),
                                                updated_by              UUID REFERENCES employee (employee_id),
                                                created_at              TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                                updated_at              TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
-- ----------------------------------------------------------------------------
-- 6. VEHICLE REGISTRATION / LICENSE DETAILS - done
-- ----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS vehicle_registration (
                                                    registration_id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                    vehicle_id              UUID NOT NULL REFERENCES vehicle (vehicle_id) ON DELETE CASCADE,
                                                    company_id              UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                                    companyVehicle_id       UUID NOT NUll REFERENCES company_vehicles(companyVehicle_id)ON DELETE CASCADE,
                                                    registration_number     VARCHAR(50) NOT NULL,
                                                    registration_date       DATE NOT NULL,
                                                    registration_expiry     DATE NOT NULL,
                                                    registering_authority   VARCHAR(150),  -- RTO name
                                                    registration_state      VARCHAR(100),
                                                    registration_city       VARCHAR(100),
                                                    rc_book_number          VARCHAR(100),
                                                    rc_status               VARCHAR(20) CHECK (rc_status IN ('Valid', 'Expired', 'Suspended')) DEFAULT 'Valid',
                                                    number_plate_type_id    INTEGER REFERENCES number_plate_type (plate_type_id),
                                                    renewal_reminder_days   INTEGER DEFAULT 30,
                                                    notes                   TEXT,
                                                    is_current              BOOLEAN DEFAULT TRUE,
                                                    created_at              TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                                    updated_at              TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_vreg_vehicle    ON vehicle_registration (vehicle_id);
CREATE INDEX IF NOT EXISTS idx_vreg_expiry     ON vehicle_registration (registration_expiry);
CREATE TRIGGER trg_vehicle_registration_upd BEFORE UPDATE ON vehicle_registration FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- ----------------------------------------------------------------------------
-- 7. VEHICLE PURCHASE DETAILS
-- ----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS vehicle_purchase (
                                                purchase_id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                vehicle_id              UUID NOT NULL REFERENCES vehicle (vehicle_id) ON DELETE CASCADE,
                                                company_id              UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                                purchase_type_id        INTEGER REFERENCES purchase_type (purchase_type_id),
                                                purchase_date           DATE NOT NULL,
                                                seller_type_id          INTEGER REFERENCES seller_type (seller_type_id),
                                                seller_name             VARCHAR(200),
                                                seller_contact          VARCHAR(20),
                                                seller_email            VARCHAR(100),
                                                seller_address          TEXT,
                                                invoice_number          VARCHAR(100),
                                                invoice_date            DATE,
                                                ex_showroom_price       DECIMAL(15, 2),
                                                registration_charges    DECIMAL(15, 2),
                                                insurance_at_purchase   DECIMAL(15, 2),
                                                accessories_cost        DECIMAL(15, 2),
                                                other_charges           DECIMAL(15, 2),
                                                on_road_price           DECIMAL(15, 2),
                                                payment_mode            VARCHAR(50) CHECK (payment_mode IN ('Cash', 'Cheque', 'Online', 'Loan', 'Lease')),
                                                notes                   TEXT,
                                                created_at              TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                                updated_at              TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_vpurchase_vehicle ON vehicle_purchase (vehicle_id);
CREATE TRIGGER trg_vehicle_purchase_upd BEFORE UPDATE ON vehicle_purchase FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- ----------------------------------------------------------------------------
-- 8. VEHICLE FINANCE / LOAN DETAILS
-- ----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS vehicle_finance (
                                               finance_id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                               vehicle_id              UUID NOT NULL REFERENCES vehicle (vehicle_id) ON DELETE CASCADE,
                                               company_id              UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                               purchase_mode           VARCHAR(20) CHECK (purchase_mode IN ('Self-Financed', 'Loan', 'Lease')) NOT NULL,
                                               bank_name               VARCHAR(150),
                                               loan_account_number     VARCHAR(100),
                                               loan_amount             DECIMAL(15, 2),
                                               down_payment            DECIMAL(15, 2),
                                               interest_rate_percent   DECIMAL(5, 2),
                                               loan_tenure_months      INTEGER,
                                               emi_amount              DECIMAL(15, 2),
                                               loan_start_date         DATE,
                                               loan_end_date           DATE,
                                               emis_paid               INTEGER DEFAULT 0,
                                               emis_remaining          INTEGER,
                                               outstanding_amount      DECIMAL(15, 2),
                                               hypothecation_bank      VARCHAR(150),
                                               loan_closure_date       DATE,
                                               finance_status_id       INTEGER REFERENCES finance_status (status_id),
                                               emi_reminder_days       INTEGER DEFAULT 3,
                                               notes                   TEXT,
                                               created_at              TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                               updated_at              TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_vfinance_vehicle ON vehicle_finance (vehicle_id);
CREATE TRIGGER trg_vehicle_finance_upd BEFORE UPDATE ON vehicle_finance FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- ----------------------------------------------------------------------------
-- 9. VEHICLE INSURANCE DETAILS - done
-- ----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS vehicle_insurance (
                                                 insurance_id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                 vehicle_id              UUID NOT NULL REFERENCES vehicle (vehicle_id) ON DELETE CASCADE,
                                                 companyVehicle_id       UUID NOT NULL REFERENCES company_vehicles(companyVehicle_id)ON DELETE CASCADE,
                                                 company_id              UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                                 insurance_company       VARCHAR(150) NOT NULL,
                                                 policy_number           VARCHAR(100) UNIQUE NOT NULL,
                                                 insurance_type_id       INTEGER REFERENCES insurance_type (ins_type_id),
                                                 policy_start_date       DATE NOT NULL,
                                                 policy_expiry_date      DATE NOT NULL,
                                                 idv_amount              DECIMAL(15, 2),
                                                 premium_amount          DECIMAL(15, 2),
                                                 payment_mode            VARCHAR(50),
                                                 payment_date            DATE,
                                                 agent_name              VARCHAR(150),
                                                 agent_contact           VARCHAR(20),
                                                 agent_email             VARCHAR(100),
                                                 nominee_name            VARCHAR(150),
                                                 add_on_covers           TEXT,
                                                 ncb_percent             DECIMAL(5, 2) DEFAULT 0,
                                                 claim_count             INTEGER DEFAULT 0,
                                                 last_claim_date         DATE,
                                                 last_claim_amount       DECIMAL(15, 2),
                                                 renewal_reminder_days   INTEGER DEFAULT 30,
                                                 insurance_status        VARCHAR(20) CHECK (insurance_status IN ('Active', 'Expired', 'Cancelled')) DEFAULT 'Active',
                                                 notes                   TEXT,
                                                 is_current              BOOLEAN DEFAULT TRUE,
                                                 created_at              TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                                 updated_at              TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_vins_vehicle  ON vehicle_insurance (vehicle_id);
CREATE INDEX IF NOT EXISTS idx_vins_expiry   ON vehicle_insurance (policy_expiry_date);
CREATE INDEX IF NOT EXISTS idx_vins_status   ON vehicle_insurance (insurance_status);
CREATE TRIGGER trg_vehicle_insurance_upd BEFORE UPDATE ON vehicle_insurance FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- Insurance Claims Log
CREATE TABLE IF NOT EXISTS vehicle_insurance_claim (
                                                       claim_id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                       insurance_id    UUID NOT NULL REFERENCES vehicle_insurance (insurance_id) ON DELETE CASCADE,
                                                       vehicle_id      UUID NOT NULL REFERENCES vehicle (vehicle_id) ON DELETE CASCADE,
                                                       claim_date      DATE NOT NULL,
                                                       claim_amount    DECIMAL(15, 2),
                                                       settled_amount  DECIMAL(15, 2),
                                                       claim_reason    TEXT,
                                                       claim_status    VARCHAR(50) CHECK (claim_status IN ('Pending', 'Approved', 'Settled', 'Rejected')),
                                                       settled_date    DATE,
                                                       notes           TEXT,
                                                       created_at      TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------------------
-- 10. VEHICLE FITNESS CERTIFICATE -done
-- ----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS vehicle_fitness_certificate (
                                                           fitness_id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                           vehicle_id              UUID NOT NULL REFERENCES vehicle (vehicle_id) ON DELETE CASCADE,
                                                           companyVehicle_id       UUID NOT NULL REFERENCES company_vehicles(companyVehicle_id)ON DELETE CASCADE,
                                                           company_id              UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                                           certificate_number      VARCHAR(100) UNIQUE NOT NULL,
                                                           issuing_authority       VARCHAR(150),
                                                           inspection_center       VARCHAR(150),
                                                           inspector_id            VARCHAR(100),
                                                           inspector_name          VARCHAR(150),
                                                           issue_date              DATE NOT NULL,
                                                           expiry_date             DATE NOT NULL,
                                                           validity_duration_years INTEGER,
                                                           inspection_result_id    INTEGER REFERENCES inspection_result (result_id),
                                                           remarks                 TEXT,
                                                           renewal_reminder_days   INTEGER DEFAULT 30,
                                                           fitness_status          VARCHAR(20) CHECK (fitness_status IN ('Valid', 'Expired', 'Suspended')) DEFAULT 'Valid',
                                                           is_current              BOOLEAN DEFAULT TRUE,
                                                           created_at              TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                                           updated_at              TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_vfit_vehicle ON vehicle_fitness_certificate (vehicle_id);
CREATE INDEX IF NOT EXISTS idx_vfit_expiry  ON vehicle_fitness_certificate (expiry_date);
CREATE TRIGGER trg_vehicle_fitness_upd BEFORE UPDATE ON vehicle_fitness_certificate FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- ----------------------------------------------------------------------------
-- 11. VEHICLE PUC (POLLUTION UNDER CONTROL) -Done
-- ----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS vehicle_puc (
                                           puc_id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                           vehicle_id              UUID NOT NULL REFERENCES vehicle (vehicle_id) ON DELETE CASCADE,
                                           company_id              UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                           certificate_number      VARCHAR(100) NOT NULL,
                                           issuing_center          VARCHAR(150),
                                           issue_date              DATE NOT NULL,
                                           expiry_date             DATE NOT NULL,
                                           co_emission_percent     DECIMAL(6, 3),
                                           hc_emission_ppm         DECIMAL(8, 2),
                                           test_result             VARCHAR(20) CHECK (test_result IN ('Pass', 'Fail')) DEFAULT 'Pass',
                                           puc_status              VARCHAR(20) CHECK (puc_status IN ('Valid', 'Expired')) DEFAULT 'Valid',
                                           renewal_reminder_days   INTEGER DEFAULT 15,
                                           is_current              BOOLEAN DEFAULT TRUE,
                                           created_at              TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                           updated_at              TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_vpuc_vehicle ON vehicle_puc (vehicle_id);
CREATE INDEX IF NOT EXISTS idx_vpuc_expiry  ON vehicle_puc (expiry_date);
CREATE TRIGGER trg_vehicle_puc_upd BEFORE UPDATE ON vehicle_puc FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- ----------------------------------------------------------------------------
-- 12. VEHICLE LOCATION DETAILS
-- ----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS vehicle_location (
                                                location_id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                vehicle_id              UUID NOT NULL REFERENCES vehicle (vehicle_id) ON DELETE CASCADE,
                                                company_id              UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                                branch_id               UUID REFERENCES company_branch (branch_id),
                                                department_id           UUID REFERENCES department (department_id),
                                                location_type           VARCHAR(50) CHECK (location_type IN ('Home', 'Office', 'Warehouse', 'Field', 'On Route', 'Workshop', 'Other')),
                                                location_name           VARCHAR(150),
                                                address_line1           TEXT,
                                                address_line2           TEXT,
                                                city                    VARCHAR(100),
                                                state                   VARCHAR(100),
                                                country                 VARCHAR(100),
                                                pin_code                VARCHAR(20),
                                                gps_coordinates         POINT,
                                                assigned_zone           VARCHAR(100),
                                                assigned_region         VARCHAR(100),
                                                period_start_date       DATE,
                                                period_end_date         DATE,
                                                duration_days           INTEGER,
                                                recorded_at             TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                                recorded_by             UUID REFERENCES employee (employee_id),
                                                is_current              BOOLEAN DEFAULT TRUE,
                                                notes                   TEXT
);
CREATE INDEX IF NOT EXISTS idx_vloc_vehicle ON vehicle_location (vehicle_id);
CREATE INDEX IF NOT EXISTS idx_vloc_current ON vehicle_location (vehicle_id) WHERE is_current = TRUE;

-- ----------------------------------------------------------------------------
-- 13. VEHICLE OWNER / DRIVER ASSIGNMENT
-- ----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS vehicle_owner (
                                             owner_id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                             vehicle_id      UUID NOT NULL REFERENCES vehicle (vehicle_id) ON DELETE CASCADE,
                                             company_id      UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                             owner_name      VARCHAR(150) NOT NULL,
                                             owner_contact   VARCHAR(20),
                                             owner_email     VARCHAR(100),
                                             id_proof_type   VARCHAR(50),   -- Aadhaar, PAN, Passport, Driving License
                                             id_proof_number VARCHAR(100),
                                             owner_address   TEXT,
                                             ownership_from  DATE NOT NULL,
                                             ownership_to    DATE,
                                             is_current      BOOLEAN DEFAULT TRUE,
                                             notes           TEXT,
                                             created_at      TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_vowner_vehicle ON vehicle_owner (vehicle_id);

CREATE TABLE IF NOT EXISTS vehicle_assignment (
                                                  assignment_id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                  company_id               UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                                  company_code             VARCHAR(50) NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                                  vehicle_id               UUID NOT NULL REFERENCES vehicle (vehicle_id) ON DELETE CASCADE,
                                                  assignment_type_id       INTEGER REFERENCES assignment_type (type_id),
                                                  assigned_to_employee_id  UUID REFERENCES employee (employee_id),
                                                  assigned_to_project_id   UUID REFERENCES project (project_id),
                                                  assigned_to_department   UUID REFERENCES department (department_id),
                                                  driver_id                UUID REFERENCES employee (employee_id),
                                                  driver_license_number    VARCHAR(100),
                                                  driver_license_expiry    DATE,
                                                  assigned_at              TIMESTAMPTZ NOT NULL,
                                                  expected_return_at       TIMESTAMPTZ,
                                                  returned_at              TIMESTAMPTZ,
                                                  start_odometer_km        DECIMAL(12, 2),
                                                  end_odometer_km          DECIMAL(12, 2),
                                                  start_fuel_level_percent DECIMAL(5, 2),
                                                  end_fuel_level_percent   DECIMAL(5, 2),
                                                  start_location           TEXT,
                                                  end_location             TEXT,
                                                  purpose                  TEXT,
                                                  status_id                INTEGER REFERENCES assignment_status (status_id),
                                                  notes                    TEXT,
                                                  created_at               TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                                  updated_at               TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_vassign_vehicle   ON vehicle_assignment (vehicle_id);
CREATE INDEX IF NOT EXISTS idx_vassign_employee  ON vehicle_assignment (assigned_to_employee_id);
CREATE INDEX IF NOT EXISTS idx_vassign_project   ON vehicle_assignment (assigned_to_project_id);
CREATE TRIGGER trg_vehicle_assignment_upd BEFORE UPDATE ON vehicle_assignment FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- ----------------------------------------------------------------------------
-- 14. VEHICLE MAINTENANCE & SERVICE
-- ----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS vehicle_service (
                                               service_id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                               vehicle_id              UUID NOT NULL REFERENCES vehicle (vehicle_id) ON DELETE CASCADE,
                                               company_id              UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                               service_type            VARCHAR(50) CHECK (service_type IN ('General', 'Major', 'Oil Change', 'Tire Change', 'Battery', 'Accident Repair', 'Other')),
                                               service_date            DATE NOT NULL,
                                               odometer_at_service     DECIMAL(12, 2),
                                               next_service_date       DATE,
                                               next_service_km         DECIMAL(12, 2),
                                               service_center_name     VARCHAR(150),
                                               service_center_contact  VARCHAR(20),
                                               service_cost            DECIMAL(15, 2),
                                               parts_replaced          TEXT,
                                               work_done               TEXT,
                                               amc_covered             BOOLEAN DEFAULT FALSE,
                                               notes                   TEXT,
                                               created_at              TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_vservice_vehicle ON vehicle_service (vehicle_id);
CREATE INDEX IF NOT EXISTS idx_vservice_date    ON vehicle_service (service_date);

-- AMC (Annual Maintenance Contract)
CREATE TABLE IF NOT EXISTS vehicle_amc (
                                           amc_id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                           vehicle_id      UUID NOT NULL REFERENCES vehicle (vehicle_id) ON DELETE CASCADE,
                                           company_id      UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                           amc_provider    VARCHAR(150),
                                           amc_number      VARCHAR(100),
                                           start_date      DATE NOT NULL,
                                           end_date        DATE NOT NULL,
                                           amc_cost        DECIMAL(15, 2),
                                           coverage_details TEXT,
                                           amc_status      VARCHAR(20) CHECK (amc_status IN ('Active', 'Expired', 'Cancelled')) DEFAULT 'Active',
                                           renewal_reminder_days INTEGER DEFAULT 30,
                                           notes           TEXT,
                                           created_at      TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                           updated_at      TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_vamc_vehicle ON vehicle_amc (vehicle_id);
CREATE TRIGGER trg_vehicle_amc_upd BEFORE UPDATE ON vehicle_amc FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- ----------------------------------------------------------------------------
-- 15. VEHICLE DOCUMENTS
-- ----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS vehicle_document (
                                                document_id     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                vehicle_id      UUID NOT NULL REFERENCES vehicle (vehicle_id) ON DELETE CASCADE,
                                                company_id      UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                                doc_type_id     INTEGER NOT NULL REFERENCES document_type_enum (type_id),
                                                document_name   VARCHAR(200) NOT NULL,
                                                document_number VARCHAR(100),
                                                issue_date      DATE,
                                                expiry_date     DATE,
                                                file_url        TEXT,
                                                file_size_kb    INTEGER,
                                                uploaded_by     UUID REFERENCES employee (employee_id),
                                                notes           TEXT,
                                                is_active       BOOLEAN DEFAULT TRUE,
                                                created_at      TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                                updated_at      TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_vdoc_vehicle  ON vehicle_document (vehicle_id);
CREATE INDEX IF NOT EXISTS idx_vdoc_type     ON vehicle_document (doc_type_id);
CREATE INDEX IF NOT EXISTS idx_vdoc_expiry   ON vehicle_document (expiry_date);
CREATE TRIGGER trg_vehicle_document_upd BEFORE UPDATE ON vehicle_document FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- ----------------------------------------------------------------------------
-- 16. VEHICLE SALE / DISPOSAL
-- ----------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS vehicle_sale (
                                            sale_id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                            vehicle_id      UUID NOT NULL REFERENCES vehicle (vehicle_id) ON DELETE CASCADE,
                                            company_id      UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                            sale_date       DATE NOT NULL,
                                            sale_price      DECIMAL(15, 2),
                                            buyer_name      VARCHAR(150),
                                            buyer_contact   VARCHAR(20),
                                            buyer_email     VARCHAR(100),
                                            sale_type       VARCHAR(50) CHECK (sale_type IN ('Direct Sale', 'Auction', 'Scrap', 'Transfer')),
                                            odometer_at_sale DECIMAL(12, 2),
                                            invoice_number  VARCHAR(100),
                                            notes           TEXT,
                                            created_at      TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_vsale_vehicle ON vehicle_sale (vehicle_id);

-- ----------------------------------------------------------------------------
-- 17. VEHICLE ALERTS & REMINDERS (CONSOLIDATED VIEW HELPER)
-- ----------------------------------------------------------------------------

CREATE VIEW vehicle_expiry_alerts AS
SELECT
    v.vehicle_id,
    v.company_id,
    v.vehicle_code,
    vm.model_name,
    vmfr.manufacturer_name,
    v.insurance_expiry,
    v.registration_expiry,
    v.fitness_expiry,
    v.puc_expiry,
    v.permit_expiry,
    v.tax_expiry,
    CASE WHEN v.insurance_expiry    <= CURRENT_DATE + INTERVAL '30 days' THEN TRUE ELSE FALSE END AS insurance_alert,
    CASE WHEN v.registration_expiry <= CURRENT_DATE + INTERVAL '30 days' THEN TRUE ELSE FALSE END AS registration_alert,
    CASE WHEN v.fitness_expiry      <= CURRENT_DATE + INTERVAL '30 days' THEN TRUE ELSE FALSE END AS fitness_alert,
    CASE WHEN v.puc_expiry          <= CURRENT_DATE + INTERVAL '30 days' THEN TRUE ELSE FALSE END AS puc_alert,
    CASE WHEN v.permit_expiry       <= CURRENT_DATE + INTERVAL '30 days' THEN TRUE ELSE FALSE END AS permit_alert,
    CASE WHEN v.tax_expiry          <= CURRENT_DATE + INTERVAL '30 days' THEN TRUE ELSE FALSE END AS tax_alert
FROM vehicle v
         JOIN vehicle_model vm   ON vm.model_id = v.model_id
         JOIN vehicle_manufacturer vmfr ON vmfr.manufacturer_id = vm.manufacturer_id
WHERE v.is_active = TRUE;

-- ============================================================================
-- SECTION 15: SUPPLIER
-- ============================================================================

CREATE TABLE IF NOT EXISTS supplier (
                                        supplier_id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                                        supplier_code    VARCHAR(50) NOT NULL
                                                                          DEFAULT ('SUP' || nextval('supplier_code_seq')),

                                        supplier_name    VARCHAR(200) NOT NULL,
                                        contact_person   VARCHAR(100),
                                        phone            VARCHAR(20),
                                        email            VARCHAR(120),
                                        address          TEXT,
                                        tax_id           VARCHAR(60),
                                        supplier_type_id INTEGER REFERENCES supplier_type (type_id),
                                        is_active        BOOLEAN DEFAULT TRUE,
                                        created_at       TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,

                                        UNIQUE (supplier_code)
);
CREATE SEQUENCE IF NOT EXISTS supplier_code_seq START 1;
-- ============================================================================
-- SECTION 10: VEHICLE RUNNING CHARTS
-- ============================================================================

CREATE TABLE IF NOT EXISTS vehicle_daily_activity (
                                                      activity_id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                      company_id        UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                                      company_code      VARCHAR(50) NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                                      vehicle_id        UUID NOT NULL REFERENCES vehicle (vehicle_id) ON DELETE CASCADE,
                                                      driver_id         UUID REFERENCES employee (employee_id),
                                                      project_id        UUID REFERENCES project (project_id),
                                                      activity_date     DATE NOT NULL,
                                                      start_time        TIMESTAMPTZ,
                                                      end_time          TIMESTAMPTZ,
                                                      start_odometer_km DECIMAL(12, 2),
                                                      end_odometer_km   DECIMAL(12, 2),
                                                      engine_hours      DECIMAL(10, 2),
                                                      distance_km       DECIMAL(12, 2) GENERATED ALWAYS AS (
                                                          CASE WHEN start_odometer_km IS NOT NULL AND end_odometer_km IS NOT NULL
                                                                   THEN GREATEST(end_odometer_km - start_odometer_km, 0) END
                                                          ) STORED,
                                                      work_description  TEXT,
                                                      remarks           TEXT,
                                                      created_at        TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                                      UNIQUE (vehicle_id, activity_date)
);

CREATE TABLE IF NOT EXISTS vehicle_running_log (
                                                   log_id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                   company_id         UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                                   company_code       VARCHAR(50) NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                                   vehicle_id         UUID NOT NULL REFERENCES vehicle (vehicle_id),
                                                   driver_id          UUID REFERENCES employee (employee_id),
                                                   project_id         UUID REFERENCES project (project_id),
                                                   log_date           DATE NOT NULL,
                                                   start_time         TIMESTAMPTZ,
                                                   end_time           TIMESTAMPTZ,
                                                   start_odometer     DECIMAL(12, 2),
                                                   end_odometer       DECIMAL(12, 2),
                                                   total_distance     DECIMAL(12, 2) GENERATED ALWAYS AS (
                                                       CASE WHEN start_odometer IS NOT NULL AND end_odometer IS NOT NULL
                                                                THEN GREATEST(end_odometer - start_odometer, 0) END
                                                       ) STORED,
                                                   engine_hours       DECIMAL(10, 2),
                                                   fuel_consumed      DECIMAL(10, 2),
                                                   work_type_id       INTEGER REFERENCES work_type (type_id),
                                                   work_description   TEXT,
                                                   load_capacity_used DECIMAL(5, 2),
                                                   trips_count        INTEGER DEFAULT 1,
                                                   operator_signature TEXT,
                                                   supervisor_approval UUID REFERENCES employee (employee_id),
                                                   created_at         TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_vehicle_running_date ON vehicle_running_log (vehicle_id, log_date);

CREATE TABLE IF NOT EXISTS vehicle_daily_summary (
                                                     summary_id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                     company_id          UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                                     company_code        VARCHAR(50) NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                                     vehicle_id          UUID NOT NULL REFERENCES vehicle (vehicle_id),
                                                     summary_date        DATE NOT NULL,
                                                     total_distance      DECIMAL(12, 2) DEFAULT 0,
                                                     total_engine_hours  DECIMAL(10, 2) DEFAULT 0,
                                                     total_fuel_consumed DECIMAL(10, 2) DEFAULT 0,
                                                     avg_fuel_efficiency DECIMAL(8, 3),
                                                     total_trips         INTEGER DEFAULT 0,
                                                     operational_hours   DECIMAL(6, 2) DEFAULT 0,
                                                     idle_hours          DECIMAL(6, 2) DEFAULT 0,
                                                     maintenance_hours   DECIMAL(6, 2) DEFAULT 0,
                                                     created_at          TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                                     UNIQUE (vehicle_id, summary_date)
);

CREATE TABLE IF NOT EXISTS vehicle_operating_cost (
                                                      cost_id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                      company_id     UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                                      company_code   VARCHAR(50) NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                                      vehicle_id     UUID NOT NULL REFERENCES vehicle (vehicle_id),
                                                      cost_date      DATE NOT NULL,
                                                      cost_type_id   INTEGER REFERENCES cost_type (type_id),
                                                      description    TEXT,
                                                      amount         DECIMAL(12, 2) NOT NULL,
                                                      odometer_km    DECIMAL(12, 2),
                                                      reference_type VARCHAR(50),
                                                      reference_id   UUID,
                                                      created_at     TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_vehicle_operating_cost ON vehicle_operating_cost (vehicle_id, cost_date);

-- ============================================================================
-- SECTION 11: FUEL MANAGEMENT WITH AI
-- ============================================================================

-- ============================================================================
-- SECTION 12: AI & PREDICTIVE MAINTENANCE
-- ============================================================================

-- ============================================================================
-- SECTION 13: MAINTENANCE MANAGEMENT
-- ============================================================================

CREATE TABLE IF NOT EXISTS maintenance_strategy (
                                                    strategy_id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                    company_id       UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                                    company_code     VARCHAR(50) NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                                    strategy_name    VARCHAR(100) NOT NULL,
                                                    strategy_type_id INTEGER REFERENCES maintenance_strategy_type (strategy_id),
                                                    description      TEXT
);

CREATE TABLE IF NOT EXISTS maintenance_standard (
                                                    standard_id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                    company_id            UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                                    company_code          VARCHAR(50) NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                                    type_id               UUID NOT NULL REFERENCES vehicle_type (type_id),
                                                    strategy_id           UUID REFERENCES maintenance_strategy (strategy_id),
                                                    standard_code         VARCHAR(40) NOT NULL,
                                                    name                  VARCHAR(120) NOT NULL,
                                                    category_id           INTEGER REFERENCES maintenance_category (category_id),
                                                    interval_km           INTEGER,
                                                    interval_months       INTEGER,
                                                    interval_engine_hours INTEGER,
                                                    checklist             JSONB,
                                                    is_active             BOOLEAN DEFAULT TRUE,
                                                    UNIQUE (company_id, standard_code)
);

CREATE TABLE IF NOT EXISTS maintenance_program (
                                                   program_id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                   company_id      UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                                   company_code    VARCHAR(50) NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                                   program_name    VARCHAR(200) NOT NULL,
                                                   program_type_id INTEGER REFERENCES maintenance_program_type (type_id),
                                                   description     TEXT,
                                                   is_active       BOOLEAN DEFAULT TRUE,
                                                   created_at      TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS vehicle_maintenance_program (
                                                           vehicle_program_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                           company_id         UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                                           company_code       VARCHAR(50) NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                                           vehicle_id         UUID NOT NULL REFERENCES vehicle (vehicle_id),
                                                           program_id         UUID NOT NULL REFERENCES maintenance_program (program_id),
                                                           start_date         DATE NOT NULL,
                                                           end_date           DATE,
                                                           is_active          BOOLEAN DEFAULT TRUE,
                                                           created_at         TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                                           UNIQUE (vehicle_id, program_id)
);

CREATE TABLE IF NOT EXISTS maintenance_program_template (
                                                            template_id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                            company_id               UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                                            company_code             VARCHAR(50) NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                                            program_name             VARCHAR(200) NOT NULL,
                                                            vehicle_type_id          UUID REFERENCES vehicle_type (type_id),
                                                            program_type             VARCHAR(50) CHECK (program_type IN ('preventive', 'predictive', 'corrective')),
                                                            interval_type            VARCHAR(20) CHECK (interval_type IN ('days', 'weeks', 'months', 'km', 'hours')),
                                                            interval_value           INTEGER NOT NULL,
                                                            checklist_template       JSONB,
                                                            estimated_duration_hours DECIMAL(5, 2),
                                                            estimated_cost           DECIMAL(10, 2),
                                                            is_active                BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS vehicle_maintenance_program_assignment (
                                                                      assignment_id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                                      company_id            UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                                                      company_code          VARCHAR(50) NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                                                      vehicle_id            UUID NOT NULL REFERENCES vehicle (vehicle_id),
                                                                      template_id           UUID NOT NULL REFERENCES maintenance_program_template (template_id),
                                                                      start_date            DATE NOT NULL,
                                                                      end_date              DATE,
                                                                      current_odometer      DECIMAL(12, 2),
                                                                      current_engine_hours  DECIMAL(12, 2),
                                                                      next_service_date     DATE,
                                                                      next_service_odometer DECIMAL(12, 2),
                                                                      next_service_hours    DECIMAL(12, 2),
                                                                      is_active             BOOLEAN DEFAULT TRUE,
                                                                      created_at            TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS maintenance_plan (
                                                plan_id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                company_id           UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                                company_code         VARCHAR(50) NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                                vehicle_id           UUID NOT NULL REFERENCES vehicle (vehicle_id),
                                                plan_name            VARCHAR(200) NOT NULL,
                                                plan_type_id         INTEGER REFERENCES maintenance_plan_type (type_id),
                                                start_date           DATE NOT NULL,
                                                end_date             DATE,
                                                total_estimated_cost DECIMAL(12, 2),
                                                status_id            INTEGER REFERENCES maintenance_plan_status (status_id),
                                                created_by           UUID REFERENCES employee (employee_id),
                                                created_at           TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS maintenance_plan_item (
                                                     plan_item_id     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                     company_id       UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                                     company_code     VARCHAR(50) NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                                     plan_id          UUID NOT NULL REFERENCES maintenance_plan (plan_id),
                                                     standard_id      UUID REFERENCES maintenance_standard (standard_id),
                                                     item_description TEXT NOT NULL,
                                                     scheduled_date   DATE NOT NULL,
                                                     estimated_cost   DECIMAL(10, 2),
                                                     actual_cost      DECIMAL(10, 2),
                                                     status_id        INTEGER REFERENCES maintenance_plan_item_status (status_id),
                                                     completed_date   DATE,
                                                     notes            TEXT,
                                                     created_at       TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS maintenance_schedule (
                                                    schedule_id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                    company_id             UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                                    company_code           VARCHAR(50) NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                                    vehicle_id             UUID NOT NULL REFERENCES vehicle (vehicle_id) ON DELETE CASCADE,
                                                    standard_id            UUID NOT NULL REFERENCES maintenance_standard (standard_id),
                                                    scheduled_date         DATE NOT NULL,
                                                    scheduled_odometer_km  DECIMAL(12, 2),
                                                    scheduled_engine_hours DECIMAL(12, 2),
                                                    ai_predicted_date      DATE,
                                                    prediction_confidence  DECIMAL(5, 2),
                                                    status_id              INTEGER REFERENCES maintenance_schedule_status (status_id),
                                                    notification_sent      BOOLEAN DEFAULT FALSE,
                                                    notification_sent_date TIMESTAMPTZ,
                                                    reminder_count         INTEGER DEFAULT 0,
                                                    created_at             TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                                    created_by             UUID REFERENCES employee (employee_id),
                                                    UNIQUE (vehicle_id, standard_id, scheduled_date)
);
CREATE INDEX IF NOT EXISTS idx_maintenance_schedule_status ON maintenance_schedule (status_id);

CREATE TABLE IF NOT EXISTS breakdown_record (
                                                breakdown_id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                company_id         UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                                company_code       VARCHAR(50) NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                                vehicle_id         UUID NOT NULL REFERENCES vehicle (vehicle_id) ON DELETE CASCADE,
                                                driver_id          UUID REFERENCES employee (employee_id),
                                                project_id         UUID REFERENCES project (project_id),
                                                breakdown_at       TIMESTAMPTZ NOT NULL,
                                                location           POINT,
                                                odometer_km        DECIMAL(12, 2),
                                                breakdown_type_id  INTEGER REFERENCES breakdown_type (type_id),
                                                severity_id        INTEGER REFERENCES breakdown_severity (severity_id),
                                                description        TEXT,
                                                repair_category_id INTEGER REFERENCES repair_category (category_id),
                                                repair_location_id INTEGER REFERENCES repair_location (location_id),
                                                status_id          INTEGER REFERENCES breakdown_status (status_id),
                                                created_at         TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_breakdown_vehicle_time ON breakdown_record (vehicle_id, breakdown_at DESC);
CREATE INDEX IF NOT EXISTS idx_breakdown_company ON breakdown_record (company_id);

CREATE TABLE IF NOT EXISTS repair_job (
                                          repair_job_id    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                          company_id       UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                          company_code     VARCHAR(50) NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                          breakdown_id     UUID NOT NULL REFERENCES breakdown_record (breakdown_id) ON DELETE CASCADE,
                                          repair_type_id   INTEGER REFERENCES repair_type (type_id),
                                          diagnosis_notes  TEXT,
                                          decided_solution TEXT,
                                          estimated_cost   DECIMAL(12, 2),
                                          actual_cost      DECIMAL(12, 2),
                                          start_date       DATE,
                                          completion_date  DATE,
                                          status_id        INTEGER REFERENCES repair_job_status (status_id),
                                          created_by       UUID REFERENCES employee (employee_id),
                                          created_at       TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                          updated_at       TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
CREATE TRIGGER trg_repair_job_upd BEFORE UPDATE ON repair_job FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS maintenance_record (
                                                  maintenance_id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                  company_id               UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                                  company_code             VARCHAR(50) NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                                  vehicle_id               UUID NOT NULL REFERENCES vehicle (vehicle_id) ON DELETE CASCADE,
                                                  schedule_id              UUID REFERENCES maintenance_schedule (schedule_id),
                                                  breakdown_id             UUID REFERENCES breakdown_record (breakdown_id),
                                                  standard_id              UUID NOT NULL REFERENCES maintenance_standard (standard_id),
                                                  start_time               TIMESTAMPTZ NOT NULL,
                                                  end_time                 TIMESTAMPTZ,
                                                  odometer_km              DECIMAL(12, 2) NOT NULL,
                                                  engine_hours             DECIMAL(12, 2),
                                                  work_performed           TEXT NOT NULL,
                                                  parts_used               JSONB,
                                                  lubricants_used          JSONB,
                                                  labor_cost               DECIMAL(10, 2),
                                                  parts_cost               DECIMAL(10, 2),
                                                  lubricants_cost          DECIMAL(10, 2),
                                                  other_cost               DECIMAL(10, 2),
                                                  total_cost               DECIMAL(12, 2),
                                                  next_service_date        DATE,
                                                  next_service_odometer_km DECIMAL(12, 2),
                                                  status_id                INTEGER REFERENCES maintenance_record_status (status_id),
                                                  created_at               TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                                  created_by               UUID REFERENCES employee (employee_id),
                                                  approved_by              UUID REFERENCES employee (employee_id),
                                                  approved_at              TIMESTAMPTZ
);
CREATE INDEX IF NOT EXISTS idx_maint_vehicle_time ON maintenance_record (vehicle_id, start_time DESC);
CREATE INDEX IF NOT EXISTS idx_maintenance_company ON maintenance_record (company_id);

CREATE TABLE IF NOT EXISTS maintenance_assignment (
                                                      assignment_id  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                      company_id     UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                                      company_code   VARCHAR(50) NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                                      breakdown_id   UUID REFERENCES breakdown_record (breakdown_id) ON DELETE CASCADE,
                                                      maintenance_id UUID REFERENCES maintenance_record (maintenance_id) ON DELETE CASCADE,
                                                      technician_id  UUID NOT NULL REFERENCES employee (employee_id),
                                                      assigned_at    TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
                                                      started_at     TIMESTAMPTZ,
                                                      completed_at   TIMESTAMPTZ,
                                                      status_id      INTEGER REFERENCES maintenance_assignment_status (status_id),
                                                      notes          TEXT
);

-- ============================================================================
-- SECTION 14: VEHICLE FILTER MANAGEMENT
-- ============================================================================

CREATE TABLE IF NOT EXISTS vehicle_filter_type (
                                                   filter_type_id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                   company_id          UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                                   company_code        VARCHAR(50) NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                                   filter_name         VARCHAR(100) NOT NULL,
                                                   filter_code         VARCHAR(50) NOT NULL,
                                                   description         TEXT,
                                                   typical_life_km     INTEGER,
                                                   typical_life_hours  INTEGER,
                                                   typical_life_months INTEGER,
                                                   UNIQUE (company_id, filter_code)
);

CREATE TABLE IF NOT EXISTS vehicle_filter (
                                              filter_id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                              company_id                    UUID NOT NULL REFERENCES company (company_id) ON DELETE CASCADE,
                                              company_code                  VARCHAR(50) NOT NULL REFERENCES company (company_code) ON UPDATE CASCADE,
                                              vehicle_id                    UUID NOT NULL REFERENCES vehicle (vehicle_id),
                                              filter_type_id                UUID NOT NULL REFERENCES vehicle_filter_type (filter_type_id),
                                              serial_number                 VARCHAR(100),
                                              installed_date                DATE NOT NULL,
                                              installed_odometer_km         DECIMAL(12, 2),
                                              installed_engine_hours        DECIMAL(12, 2),
                                              recommended_replacement_km    DECIMAL(12, 2),
                                              recommended_replacement_hours DECIMAL(12, 2),
                                              actual_replacement_date       DATE,
                                              actual_replacement_km         DECIMAL(12, 2),
                                              actual_replacement_hours      DECIMAL(12, 2),
                                              replacement_reason            VARCHAR(100),
                                              status_id                     INTEGER REFERENCES filter_status (status_id),
                                              created_at                    TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);


-- ----------------------------------------------------------------------------
-- HIRED VEHICLE DETAILS (Supplier based)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS hired_vehicles (
    hiredvehicle_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    supplier_id UUID NOT NULL REFERENCES supplier(supplier_id),
    supplier_code VARCHAR,

    hiredvehicle_type UUID REFERENCES vehicle_type(type_id),
    hiredvehicle_model UUID REFERENCES vehicle_model(model_id),
    hiredvehicle_category UUID NOT NULL REFERENCES vehicle_category(category_id),
    hiredvehicle_manufacture UUID NOT NULL REFERENCES vehicle_manufacturer(manufacturer_id),

    registration_number VARCHAR(50) UNIQUE,
    chassis_number VARCHAR(100) UNIQUE NOT NULL,
    engine_number VARCHAR(100) NOT NULL,
    key_number VARCHAR(50),
    vehicle_image TEXT,

    manufacture_year INTEGER NOT NULL,
    color VARCHAR(50),
    fuel_type_id INTEGER REFERENCES fuel_type(type_id),
    transmission_type_id INTEGER REFERENCES transmission_type(type_id),
    number_plate_type_id INTEGER REFERENCES number_plate_type(plate_type_id),
    body_style_id INTEGER REFERENCES body_style(style_id),
    seating_capacity INTEGER,
    undercarriage_type INTEGER REFERENCES undercarriage_type(type_id),
    engine_type INTEGER REFERENCES engine_type(engineType_id),
    engine_manufacture INTEGER REFERENCES engine_manufacture(engineManufacture_id),

    initial_odometer_km DECIMAL(12, 2) DEFAULT 0,
    current_odometer_km DECIMAL(12, 2) DEFAULT 0,
    total_engine_hours DECIMAL(12, 2) DEFAULT 0,

    consumption_method_id INTEGER REFERENCES consumption_method(method_id),
    rated_efficiency_kmpl DECIMAL(8, 3),
    rated_consumption_lph DECIMAL(8, 3),

    ownership_type_id INTEGER REFERENCES ownership_type(type_id) NOT NULL,
    current_ownership VARCHAR(200),
    previous_owners_count INTEGER DEFAULT 0,
    manufacture_id UUID REFERENCES vehicle_manufacturer(manufacturer_id),
    distributor_id UUID REFERENCES distributor(distributor_id),
    vehicle_condition VARCHAR(20) CHECK (vehicle_condition IN ('New', 'Used', 'Damaged')) DEFAULT 'New',

    operational_status_id INTEGER REFERENCES operational_status(status_id),
    vehicle_status_id INTEGER REFERENCES vehicle_status(status_id),

    notes TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_by UUID REFERENCES employee(employee_id),
    updated_by UUID REFERENCES employee(employee_id),
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_hired_vehicle_supplier ON hired_vehicles(supplier_id);
CREATE INDEX IF NOT EXISTS idx_hired_vehicle_type ON hired_vehicles(hiredvehicle_type);
CREATE INDEX IF NOT EXISTS idx_hired_vehicle_registration ON hired_vehicles(registration_number);
CREATE INDEX IF NOT EXISTS idx_hired_vehicle_chassis ON hired_vehicles(chassis_number);
CREATE TRIGGER trg_hired_vehicles_upd BEFORE UPDATE ON hired_vehicles FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- ----------------------------------------------------------------------------
-- HIRED VEHICLE PROFILE TABLES
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS hired_vehicle_registration (
    registration_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vehicle_id UUID,
    supplier_id UUID NOT NULL REFERENCES supplier(supplier_id) ON DELETE CASCADE,
    hiredvehicle_id UUID NOT NULL REFERENCES hired_vehicles(hiredvehicle_id) ON DELETE CASCADE,
    registration_number VARCHAR(50) NOT NULL,
    registration_date DATE,
    registration_expiry DATE,
    registering_authority VARCHAR(150),
    registration_state VARCHAR(100),
    registration_city VARCHAR(100),
    rc_book_number VARCHAR(100),
    rc_status VARCHAR(20) CHECK (rc_status IN ('Valid', 'Expired', 'Suspended')) DEFAULT 'Valid',
    number_plate_type_id INTEGER REFERENCES number_plate_type(plate_type_id),
    renewal_reminder_days INTEGER DEFAULT 30,
    notes TEXT,
    is_current BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_hvreg_hiredvehicle ON hired_vehicle_registration(hiredvehicle_id);
CREATE INDEX IF NOT EXISTS idx_hvreg_expiry ON hired_vehicle_registration(registration_expiry);
CREATE TRIGGER trg_hired_vehicle_registration_upd BEFORE UPDATE ON hired_vehicle_registration FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS hired_vehicle_insurance (
    insurance_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vehicle_id UUID,
    supplier_id UUID NOT NULL REFERENCES supplier(supplier_id) ON DELETE CASCADE,
    hiredvehicle_id UUID NOT NULL REFERENCES hired_vehicles(hiredvehicle_id) ON DELETE CASCADE,
    insurance_company VARCHAR(150) NOT NULL,
    policy_number VARCHAR(100) UNIQUE NOT NULL,
    insurance_type_id INTEGER REFERENCES insurance_type(ins_type_id),
    policy_start_date DATE,
    policy_expiry_date DATE,
    idv_amount DECIMAL(15,2),
    premium_amount DECIMAL(15,2),
    payment_mode VARCHAR(50),
    payment_date DATE,
    agent_name VARCHAR(100),
    agent_contact VARCHAR(20),
    agent_email VARCHAR(100),
    nominee_name VARCHAR(100),
    add_on_covers TEXT,
    ncb_percent DECIMAL(5,2),
    claim_count INTEGER DEFAULT 0,
    last_claim_date DATE,
    last_claim_amount DECIMAL(15,2),
    renewal_reminder_days INTEGER DEFAULT 30,
    insurance_status VARCHAR(20) DEFAULT 'Active',
    notes TEXT,
    is_current BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_hvins_hiredvehicle ON hired_vehicle_insurance(hiredvehicle_id);
CREATE INDEX IF NOT EXISTS idx_hvins_expiry ON hired_vehicle_insurance(policy_expiry_date);
CREATE TRIGGER trg_hired_vehicle_insurance_upd BEFORE UPDATE ON hired_vehicle_insurance FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS hired_vehicle_fitness_certificate (
    fitness_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vehicle_id UUID,
    supplier_id UUID NOT NULL REFERENCES supplier(supplier_id) ON DELETE CASCADE,
    hiredvehicle_id UUID NOT NULL REFERENCES hired_vehicles(hiredvehicle_id) ON DELETE CASCADE,
    certificate_number VARCHAR(100) NOT NULL,
    issuing_authority VARCHAR(150),
    inspection_center VARCHAR(150),
    inspector_id VARCHAR(100),
    inspector_name VARCHAR(150),
    issue_date DATE,
    expiry_date DATE,
    validity_duration_years INTEGER,
    inspection_result_id INTEGER,
    remarks TEXT,
    renewal_reminder_days INTEGER DEFAULT 30,
    fitness_status VARCHAR(20) DEFAULT 'Valid',
    is_current BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_hvfit_hiredvehicle ON hired_vehicle_fitness_certificate(hiredvehicle_id);
CREATE INDEX IF NOT EXISTS idx_hvfit_expiry ON hired_vehicle_fitness_certificate(expiry_date);
CREATE TRIGGER trg_hired_vehicle_fitness_certificate_upd BEFORE UPDATE ON hired_vehicle_fitness_certificate FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS hired_vehicle_puc (
    puc_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vehicle_id UUID,
    supplier_id UUID NOT NULL REFERENCES supplier(supplier_id) ON DELETE CASCADE,
    hiredvehicle_id UUID NOT NULL REFERENCES hired_vehicles(hiredvehicle_id) ON DELETE CASCADE,
    certificate_number VARCHAR(100) NOT NULL,
    issuing_center VARCHAR(150),
    issue_date DATE,
    expiry_date DATE,
    co_emission_percent DECIMAL(6,2),
    hc_emission_ppm DECIMAL(8,2),
    test_result VARCHAR(20) DEFAULT 'Pass',
    puc_status VARCHAR(20) DEFAULT 'Valid',
    renewal_reminder_days INTEGER DEFAULT 15,
    is_current BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_hvpuc_hiredvehicle ON hired_vehicle_puc(hiredvehicle_id);
CREATE INDEX IF NOT EXISTS idx_hvpuc_expiry ON hired_vehicle_puc(expiry_date);
CREATE TRIGGER trg_hired_vehicle_puc_upd BEFORE UPDATE ON hired_vehicle_puc FOR EACH ROW EXECUTE FUNCTION set_updated_at();

-- ----------------------------------------------------------------------------
-- HIRED VEHICLE IDENTITY TYPE CODES
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS hiredvehicleidtype (
    idtype_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    idtype_com UUID NOT NULL REFERENCES company(company_id),
    company_code VARCHAR NOT NULL,
    idtype_typeid UUID NOT NULL REFERENCES vehicle_type(type_id),
    idtype_code VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_hiredvehicleidtype_company_code UNIQUE (idtype_com, idtype_code)
);
CREATE INDEX IF NOT EXISTS idx_hiredvehicleidtype_company ON hiredvehicleidtype(idtype_com);
CREATE INDEX IF NOT EXISTS idx_hiredvehicleidtype_type ON hiredvehicleidtype(idtype_typeid);
CREATE TRIGGER trg_hiredvehicleidtype_upd BEFORE UPDATE ON hiredvehicleidtype FOR EACH ROW EXECUTE FUNCTION set_updated_at();

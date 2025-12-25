package com.shanzhu.hospital.security;


public final class AccessControlDemo {

    private AccessControlDemo() {
      
    }

    public static final String DAC_SQL = ""
            
            + "-- 创建医生角色（仅能操作诊疗相关数据）\n"
            + "CREATE USER IF NOT EXISTS 'role_doctor'@'localhost' IDENTIFIED BY 'doc123456';\n"
            + "GRANT SELECT ON DB_Hospital.patient_user TO 'role_doctor'@'localhost';\n"
            + "GRANT SELECT, UPDATE ON DB_Hospital.orders TO 'role_doctor'@'localhost';\n"
            + "GRANT SELECT ON DB_Hospital.drug TO 'role_doctor'@'localhost';\n"
            + "GRANT SELECT ON DB_Hospital.checks TO 'role_doctor'@'localhost';\n"
            + "\n"
            // 创建患者角色（仅能查询自己的订单）
            + "-- 创建患者角色（仅能查询自己的订单）\n"
            + "CREATE USER IF NOT EXISTS 'role_patient'@'localhost' IDENTIFIED BY 'pat123456';\n"
            + "\n"
            + "-- 新增患者自查询视图（无需改后端，直接查询视图）\n"
            + "CREATE VIEW v_patient_order AS\n"
            + "SELECT o.o_id, d.d_name, o.o_start, o.o_record, o.o_total_price, o.o_price_state\n"
            + "FROM orders o\n"
            + "JOIN doctor_user d ON o.d_id = d.d_id\n"
            + "WHERE o.p_id = SUBSTRING_INDEX(CURRENT_USER(), '@', 1) + 0;\n"
            + "GRANT SELECT ON DB_Hospital.v_patient_order TO 'role_patient'@'localhost';\n"
            + "\n"
            // 创建管理员角色（全权限）
            + "-- 创建管理员角色（全权限）\n"
            + "CREATE USER IF NOT EXISTS 'role_admin'@'localhost' IDENTIFIED BY 'adm123456';\n"
            + "GRANT ALL PRIVILEGES ON DB_Hospital.* TO 'role_admin'@'localhost' WITH GRANT OPTION;\n";

   
    public static String getDemoSql() {
        return DAC_SQL;
    }
}



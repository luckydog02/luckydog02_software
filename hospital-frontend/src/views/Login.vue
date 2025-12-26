<!--
 * 登录页
 *
-->
<template>
    <div class="login-container">
        <div class="login-card">
            <!-- 登录标题区域 -->
            <div class="login-header">
                <div class="logo">
                    <img src="../assets/医院管理.png" alt="系统logo">
                </div>
                <h1>医院管理系统</h1>
                <p>请登录您的账号</p>
            </div>

            <!-- 登录表单 -->
            <el-form
                :model="loginForm"
                :rules="loginRules"
                ref="ruleForm"
                class="login-form"
            >
                <!-- 用户名 -->
                <el-form-item prop="id">
                    <el-input 
                        v-model="loginForm.id" 
                        placeholder="请输入账号"
                        prefix-icon="iconfont icon-login-user"
                    ></el-input>
                </el-form-item>

                <!-- 密码 -->
                <el-form-item prop="password">
                    <el-input 
                        v-model="loginForm.password" 
                        type="password" 
                        placeholder="请输入密码"
                        prefix-icon="iconfont icon-login-password"
                        show-password
                    ></el-input>
                </el-form-item>

                <!-- 角色登录选择框 - 改为下拉菜单 -->
                <el-form-item prop="role">
                    <div class="select-with-icon">
                        <i class="iconfont icon-login-user select-icon"></i>
                        <el-select 
                            v-model="loginForm.role" 
                            placeholder="请选择角色"
                            clearable
                            style="width: 100%"
                        >
                            <el-option label="患者" value="患者"></el-option>
                            <el-option label="医生" value="医生"></el-option>
                            <el-option label="管理员" value="管理员"></el-option>
                        </el-select>
                    </div>
                </el-form-item>

                <el-form-item class="login-actions">
                    <el-button
                        type="primary"
                        style="width: 100%"
                        @click="submitLoginForm('ruleForm')"
                    >
                        <i class="iconfont icon-login-button"></i>
                        登录
                    </el-button>
                </el-form-item>

                <div class="register-link">
                    <span>还没有账号？</span>
                    <el-button
                        type="text"
                        @click="registerFormVisible = true"
                    >
                        立即注册
                    </el-button>
                </div>
            </el-form>
        </div>

        <!-- 注册对话框 -->
        <el-dialog 
            title="用户注册" 
            :visible.sync="registerFormVisible"
            :close-on-click-modal="false"
            width="500px"
        >
            <el-form
                :model="registerForm"
                :rules="registerRules"
                ref="registerForm"
                label-width="100px"
            >
                <el-form-item label="账号" prop="pId">
                    <el-input v-model.number="registerForm.pId" placeholder="请设置登录账号"></el-input>
                </el-form-item>

                <el-form-item label="密码" prop="pPassword">
                    <el-input v-model="registerForm.pPassword" type="password" placeholder="请设置密码"></el-input>
                </el-form-item>

                <el-form-item label="姓名" prop="pName">
                    <el-input v-model="registerForm.pName" placeholder="请输入您的姓名"></el-input>
                </el-form-item>

                <el-form-item label="性别">
                    <el-radio-group v-model="registerForm.pGender">
                        <el-radio label="男">男</el-radio>
                        <el-radio label="女">女</el-radio>
                    </el-radio-group>
                </el-form-item>

                <el-form-item label="出生日期" prop="pBirthday">
                    <el-date-picker
                        v-model="registerForm.pBirthday"
                        type="date"
                        placeholder="选择日期"
                        value-format="yyyy-MM-dd"
                        style="width: 100%"
                    ></el-date-picker>
                </el-form-item>

                <el-form-item label="手机号" prop="pPhone">
                    <el-input v-model="registerForm.pPhone" placeholder="请输入手机号码"></el-input>
                </el-form-item>

                <el-form-item label="邮箱" prop="pEmail">
                    <el-input v-model="registerForm.pEmail" placeholder="请输入邮箱地址"></el-input>
                </el-form-item>

                <el-form-item label="身份证号" prop="pCard">
                    <el-input v-model="registerForm.pCard" placeholder="请输入身份证号码"></el-input>
                </el-form-item>

                <!-- 隐藏角色选择，默认患者 -->
                <input type="hidden" v-model="registerForm.role" value="患者">
            </el-form>

            <div slot="footer">
                <el-button @click="registerFormVisible = false">
                    <i class="iconfont icon-cancel-button"></i>
                    取消
                </el-button>
                <el-button type="primary" @click="register('registerForm')">
                    <i class="iconfont icon-sure-button"></i>
                    确定注册
                </el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import request from "@/utils/request.js";
import { setToken } from "@/utils/storage.js";
export default {
    name: "Login",
    data() {
        var validatePhone = (rule, value, callback) => {
            if (!value) {
                callback(new Error("请输入手机号"));
            } else {
                let reg = /^1(3[0-9]|4[5,7]|5[0,1,2,3,5,6,7,8,9]|6[2,5,6,7]|7[0,1,7,8]|8[0-9]|9[1,8,9])\d{8}$/;
                if (!reg.test(value)) {
                    callback(new Error("请输入合法的手机号"));
                }
                callback();
            }
        };
        var validateCard = (rule, value, callback) => {
            if (!value) {
                callback(new Error("请输入身份证号"));
            } else {
                let reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
                if (!reg.test(value)) {
                    callback(new Error("请输入合法的身份证号码"));
                }
                callback();
            }
        };
        return {
            loginForm: {
                id: "",
                password: "",
                role: "",
            },
            loginRules: {
                id: [
                    { required: true, message: "请输入账号编号", trigger: "blur" },
                    { min: 3, max: 50, message: "长度在 3到 50 个字符", trigger: "blur" }
                ],
                password: [
                    { required: true, message: "请输入密码", trigger: "blur" }
                ],
                role: [
                    { required: true, message: "请选择角色", trigger: "change" }
                ]
            },
            // 注册相关
            registerFormVisible: false,
            registerForm: {
                pGender: "男",
                role: "患者" // 固定注册角色为患者
            },
            registerRules: {
                pId: [
                    { required: true, message: "请输入账号", trigger: "blur" },
                    { type: "number", message: "账号必须为数字类型", trigger: "blur" }
                ],
                pPassword: [
                    { required: true, message: "请输入密码", trigger: "blur" },
                    { min: 4, max: 50, message: "长度在 4到 50 个字符", trigger: "blur" }
                ],
                pName: [
                    { required: true, message: "请输入姓名", trigger: "blur" },
                    { min: 2, max: 8, message: "长度在 2到 8 个字符", trigger: "blur" }
                ],
                pEmail: [
                    { required: true, message: "请输入邮箱", trigger: "blur" },
                    { type: "email", message: "请输入正确的邮箱地址", trigger: ["blur", "change"] }
                ],
                pPhone: [{ validator: validatePhone, trigger: "blur" }],
                pCard: [{ validator: validateCard, trigger: "blur" }],
                pBirthday: [
                    { required: true, message: "请选择出生日期", trigger: "blur" }
                ]
            }
        };
    },
    methods: {
        // 注册提交
        register(formName) {
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    request
                        .get("patient/addPatient", {
                            params: {
                                pId: this.registerForm.pId,
                                pName: this.registerForm.pName,
                                pPassword: this.registerForm.pPassword,
                                pGender: this.registerForm.pGender,
                                pEmail: this.registerForm.pEmail,
                                pPhone: this.registerForm.pPhone,
                                pCard: this.registerForm.pCard,
                                pBirthday: this.registerForm.pBirthday,
                            },
                        })
                        .then((res) => {
                            if (res.data.status !== 200)
                                return this.$message.error("账号或邮箱已被占用！");
                            this.registerFormVisible = false;
                            this.$message.success("注册成功！请登录");
                        });
                } else {
                    return false;
                }
            });
        },
        // 登录提交
        submitLoginForm(formName) {
            if (!/^\d+$/.test(this.loginForm.id)) {
                this.$message.error("用户名必须为数字");
                return;
            }
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    let params = new URLSearchParams();
                    let url = "";
                    
                    // 根据角色设置不同的请求参数和地址
                    switch(this.loginForm.role) {
                        case "管理员":
                            params.append("aId", this.loginForm.id);
                            params.append("aPassword", this.loginForm.password);
                            url = "admin/login";
                            break;
                        case "医生":
                            params.append("dId", this.loginForm.id);
                            params.append("dPassword", this.loginForm.password);
                            url = "doctor/login";
                            break;
                        case "患者":
                            params.append("pId", this.loginForm.id);
                            params.append("pPassword", this.loginForm.password);
                            url = "patient/login";
                            break;
                    }

                    request
                        .post(url, params)
                        .then((res) => {
                            if (res.data.status != 200)
                                return this.$message.error("用户名或密码错误");
                            setToken(res.data.data.token);
                            
                            // 根据角色跳转到不同的布局页面
                            switch(this.loginForm.role) {
                                case "管理员":
                                    this.$router.push("/adminLayout");
                                    break;
                                case "医生":
                                    this.$router.push("/doctorLayout");
                                    break;
                                case "患者":
                                    this.$router.push("/patientLayout");
                                    break;
                            }
                        })
                        .catch((error) => {
                            this.$message.error("服务器异常：" + (error.response?.data || error.message));
                        });
                } else {
                    return false;
                }
            });
        }
    }
};
</script>

<style lang="scss">
.login-container {
    min-height: 100vh;
    background-image: url("../assets/login-back.jpeg");
    background-repeat: no-repeat;
    background-size: cover;
    background-position: center;
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 20px;
}

.login-card {
    width: 420px;
    background: #ffffff;
    border-radius: 10px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
    overflow: hidden;
    transition: all 0.3s ease;

    &:hover {
        box-shadow: 0 15px 35px rgba(0, 0, 0, 0.15);
    }
}

.login-header {
    text-align: center;
    padding: 30px 20px 20px;
    border-bottom: 1px solid #f5f5f5;

    .logo {
        width: 80px;
        height: 80px;
        margin: 0 auto 15px;
        border-radius: 50%;
        overflow: hidden;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);

        img {
            width: 100%;
            height: 100%;
            object-fit: cover;
        }
    }

    h1 {
        font-size: 24px;
        color: #333;
        margin: 0 0 10px;
        font-weight: 500;
    }

    p {
        color: #666;
        margin: 0;
        font-size: 14px;
    }
}

.login-form {
    padding: 30px 25px;

    .el-form-item {
        margin-bottom: 20px;
    }

    .el-input,
    .el-select {
        height: 44px;

        .el-input__inner {
            height: 100%;
            border-radius: 6px;
            padding: 0 15px;
            padding-left: 45px;
        }

        .el-input__prefix {
            left: 15px;
        }
    }

    .el-select .el-input {
        height: 44px;

        .el-input__inner {
            height: 100%;
            border-radius: 6px;
            padding: 0 15px;
            padding-left: 45px;
        }

        .el-input__prefix {
            left: 15px;
        }
    }

    .select-with-icon {
        position: relative;

        .select-icon {
            position: absolute;
            left: 15px;
            top: 50%;
            transform: translateY(-50%);
            z-index: 10;
            color: #c0c4cc;
            font-size: 16px;
            pointer-events: none;
        }

        .el-select {
            .el-input__inner {
                padding-left: 45px;
            }
        }
    }
}

.login-actions {
    margin-top: 30px;
    margin-bottom: 15px;

    .el-button {
        height: 44px;
        font-size: 16px;
        border-radius: 6px;
    }
}

.register-link {
    text-align: center;
    font-size: 14px;
    color: #666;

    .el-button {
        color: #409eff;
        padding: 0;
        height: auto;
        vertical-align: baseline;
    }
}

// 注册对话框样式
.el-dialog__header {
    border-bottom: 1px solid #f5f5f5;
    padding: 18px 20px;

    .el-dialog__title {
        font-size: 18px;
        color: #333;
    }
}

.el-dialog__body {
    padding: 25px 20px;
}

.el-dialog__footer {
    padding: 15px 20px;
    border-top: 1px solid #f5f5f5;
}

.el-radio-group {
    display: flex;
    gap: 20px;
}
</style>
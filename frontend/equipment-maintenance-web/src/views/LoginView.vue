<template>
  <div class="login-page">
    <div class="login-bg login-bg-one"></div>
    <div class="login-bg login-bg-two"></div>

    <div class="login-shell">

      <!-- 左侧品牌区 -->
      <section class="brand-panel">
        <div class="brand-badge">EM</div>

        <div class="brand-copy">
          <div class="eyebrow">Equipment Maintenance</div>
          <h1>设备运维管理系统</h1>

          <p>
            面向设备、维修工单与故障辅助分析的一体化运维平台。
          </p>
        </div>

        <div class="feature-list">
          <div class="feature-item">
            <span class="feature-dot"></span>
            设备信息集中管理
          </div>

          <div class="feature-item">
            <span class="feature-dot"></span>
            维修工单闭环处理
          </div>

          <div class="feature-item">
            <span class="feature-dot"></span>
            故障辅助分析与知识检索
          </div>
        </div>

        <div class="brand-footer">
          Enterprise Equipment Maintenance Platform
        </div>
      </section>

      <!-- 右侧登录区 -->
      <section class="login-panel">
        <div class="login-card">
          <div class="login-header">
            <div class="login-title">欢迎回来</div>
            <div class="login-subtitle">
              登录后进入设备运维管理平台
            </div>
          </div>

          <el-form class="login-form">

            <div class="field-label">用户名</div>

            <el-form-item>
              <el-input
                v-model="username"
                placeholder="请输入用户名"
                size="large"
              />
            </el-form-item>

            <div class="field-label">密码</div>

            <el-form-item>
              <el-input
                v-model="password"
                type="password"
                placeholder="请输入密码"
                show-password
                size="large"
                @keyup.enter="handleLogin"
              />
            </el-form-item>

            <el-button
              class="login-button"
              type="primary"
              size="large"
              @click="handleLogin"
            >
              登录
            </el-button>

          </el-form>

          <div class="login-tip">
            使用系统分配的账号登录
          </div>
        </div>
      </section>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import request from '../utils/request'

const router = useRouter()

const username = ref('')
const password = ref('')

const handleLogin = async () => {
  try {
    const response = await request.post('/auth/login', {
      username: username.value,
      password: password.value,
    })

    if (response.data.code === 200) {
      const token = response.data.data.token
      const role = response.data.data.role

      localStorage.setItem('token', token)
      localStorage.setItem('role', role)

      ElMessage.success('登录成功')

      router.push('/')
    } else {
      ElMessage.error(response.data.message || '登录失败')
    }
  } catch (error) {
    ElMessage.error('登录失败')
  }
}
</script>

<style scoped>
.login-page {
  position: relative;

  min-height: 100vh;

  display: flex;
  align-items: center;
  justify-content: center;

  overflow: hidden;

  background:
    linear-gradient(
      135deg,
      #f7f9fc 0%,
      #eef3f8 45%,
      #fafbfc 100%
    );
}

.login-bg {
  position: absolute;

  border-radius: 50%;
  filter: blur(12px);

  pointer-events: none;
}

.login-bg-one {
  width: 520px;
  height: 520px;

  top: -220px;
  right: -140px;

  background: rgba(117, 157, 208, 0.18);
}

.login-bg-two {
  width: 460px;
  height: 460px;

  bottom: -260px;
  left: -120px;

  background: rgba(141, 177, 167, 0.14);
}

.login-shell {
  position: relative;
  z-index: 1;

  width: min(1040px, calc(100vw - 48px));
  min-height: 610px;

  display: grid;
  grid-template-columns: 1.05fr 0.95fr;

  border: 1px solid rgba(255, 255, 255, 0.86);
  border-radius: 30px;

  overflow: hidden;

  background: rgba(255, 255, 255, 0.58);

  box-shadow:
    0 30px 80px rgba(45, 61, 78, 0.10),
    inset 0 1px 0 rgba(255, 255, 255, 0.85);

  backdrop-filter: blur(26px);
  -webkit-backdrop-filter: blur(26px);
}

.brand-panel {
  padding: 58px 54px;

  display: flex;
  flex-direction: column;

  background:
    linear-gradient(
      145deg,
      rgba(31, 40, 50, 0.96),
      rgba(48, 61, 73, 0.93)
    );

  color: white;
}

.brand-badge {
  width: 46px;
  height: 46px;

  border-radius: 15px;

  display: flex;
  align-items: center;
  justify-content: center;

  background: rgba(255, 255, 255, 0.12);

  border: 1px solid rgba(255, 255, 255, 0.12);

  font-size: 14px;
  font-weight: 700;

  letter-spacing: 0.5px;
}

.brand-copy {
  margin-top: 82px;
}

.eyebrow {
  margin-bottom: 14px;

  color: rgba(255, 255, 255, 0.55);

  font-size: 11px;
  letter-spacing: 1.5px;

  text-transform: uppercase;
}

.brand-copy h1 {
  margin: 0;

  font-size: 34px;
  line-height: 1.25;

  font-weight: 700;
  letter-spacing: -0.5px;
}

.brand-copy p {
  margin-top: 20px;

  max-width: 390px;

  color: rgba(255, 255, 255, 0.66);

  font-size: 14px;
  line-height: 1.9;
}

.feature-list {
  margin-top: 46px;

  display: flex;
  flex-direction: column;
  gap: 16px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;

  color: rgba(255, 255, 255, 0.82);

  font-size: 13px;
}

.feature-dot {
  width: 7px;
  height: 7px;

  border-radius: 50%;

  background: rgba(255, 255, 255, 0.70);

  box-shadow:
    0 0 0 4px rgba(255, 255, 255, 0.06);
}

.brand-footer {
  margin-top: auto;

  color: rgba(255, 255, 255, 0.32);

  font-size: 10px;
  letter-spacing: 0.4px;
}

.login-panel {
  padding: 54px;

  display: flex;
  align-items: center;
  justify-content: center;
}

.login-card {
  width: 100%;
  max-width: 360px;
}

.login-header {
  margin-bottom: 38px;
}

.login-title {
  color: #202832;

  font-size: 28px;
  font-weight: 700;

  letter-spacing: -0.4px;
}

.login-subtitle {
  margin-top: 10px;

  color: #98a2ad;

  font-size: 13px;
}

.field-label {
  margin-bottom: 8px;

  color: #59636e;

  font-size: 12px;
  font-weight: 600;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 22px;
}

.login-form :deep(.el-input__wrapper) {
  height: 48px;

  border-radius: 13px;

  background: rgba(255, 255, 255, 0.72);

  box-shadow:
    0 0 0 1px rgba(54, 68, 82, 0.08) inset !important;

  transition: all 0.2s ease;
}

.login-form :deep(.el-input__wrapper:hover) {
  box-shadow:
    0 0 0 1px rgba(54, 68, 82, 0.14) inset !important;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow:
    0 0 0 1px rgba(54, 68, 82, 0.34) inset !important;
}

.login-button {
  width: 100%;
  height: 48px;

  margin-top: 4px;

  border: none;
  border-radius: 13px;

  background: #27313b;

  font-size: 14px;
  font-weight: 600;

  box-shadow:
    0 10px 22px rgba(39, 49, 59, 0.16);

  transition: all 0.2s ease;
}

.login-button:hover {
  background: #1f2831;

  transform: translateY(-1px);
}

.login-tip {
  margin-top: 22px;

  text-align: center;

  color: #a2abb5;

  font-size: 11px;
}

@media (max-width: 820px) {

  .login-shell {
    grid-template-columns: 1fr;

    max-width: 520px;
  }

  .brand-panel {
    display: none;
  }

  .login-panel {
    padding: 42px 28px;
  }

}
</style>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const activeMenu = computed(() => route.path)

const role = computed(() => {
  return localStorage.getItem('role') || 'WORKER'
})

const roleText = computed(() => {
  return role.value === 'ADMIN' ? '管理员' : '维修人员'
})

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('role')
  router.push('/login')
}
</script>

<template>
  <div class="app-shell">

    <!-- 背景装饰，只负责视觉，不影响操作 -->
    <div class="background-decoration background-decoration-one"></div>
    <div class="background-decoration background-decoration-two"></div>

    <!-- 左侧导航 -->
    <aside class="sidebar glass-panel">

      <div class="brand">
        <div class="brand-mark">EM</div>

        <div class="brand-text">
          <div class="brand-title">设备运维</div>
          <div class="brand-subtitle">Maintenance</div>
        </div>
      </div>

      <div class="menu-label">
        工作台
      </div>

      <el-menu
        :default-active="activeMenu"
        router
        class="sidebar-menu"
      >
        <el-menu-item index="/equipment">
          <span class="menu-dot"></span>
          <span>设备管理</span>
        </el-menu-item>

        <el-menu-item index="/work-orders">
          <span class="menu-dot"></span>
          <span>工单管理</span>
        </el-menu-item>

        <el-menu-item index="/ai-analysis">
          <span class="menu-dot"></span>
          <span>故障辅助分析</span>
        </el-menu-item>
      </el-menu>

      <div class="sidebar-footer">
        <div class="status-dot"></div>

        <div>
          <div class="status-title">系统运行正常</div>
          <div class="status-text">设备运维管理平台</div>
        </div>
      </div>

    </aside>

    <!-- 右边主区域 -->
    <div class="workspace">

      <!-- 顶部 -->
      <header class="topbar glass-panel">

        <div>
          <div class="page-caption">设备运维管理平台</div>
          <div class="page-description">
            设备、维修工单与故障辅助分析
          </div>
        </div>

        <div class="user-area">

          <div class="user-info">
            <div class="user-avatar">
              {{ role === 'ADMIN' ? 'A' : 'W' }}
            </div>

            <div>
              <div class="user-name">{{ roleText }}</div>
              <div class="user-role">{{ role }}</div>
            </div>
          </div>

          <button
            class="logout-button"
            @click="handleLogout"
          >
            退出
          </button>

        </div>

      </header>

      <!-- 页面内容 -->
      <main class="page-container">
        <router-view />
      </main>

    </div>

  </div>
</template>

<style scoped>
.app-shell {
  position: relative;
  min-height: 100vh;
  padding: 18px;
  display: flex;
  gap: 18px;
  overflow: hidden;

  background:
    linear-gradient(
      135deg,
      #f7f9fc 0%,
      #eef3f8 45%,
      #f9fafc 100%
    );
}

.background-decoration {
  position: fixed;
  border-radius: 50%;
  filter: blur(12px);
  pointer-events: none;
}

.background-decoration-one {
  width: 420px;
  height: 420px;
  top: -160px;
  right: -100px;
  background: rgba(116, 158, 210, 0.16);
}

.background-decoration-two {
  width: 360px;
  height: 360px;
  left: 28%;
  bottom: -210px;
  background: rgba(151, 181, 173, 0.12);
}

.glass-panel {
  background: rgba(255, 255, 255, 0.68);
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow:
    0 12px 40px rgba(44, 62, 80, 0.07),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);

  backdrop-filter: blur(22px);
  -webkit-backdrop-filter: blur(22px);
}

.sidebar {
  position: relative;
  z-index: 1;

  width: 220px;
  min-width: 220px;
  height: calc(100vh - 36px);

  border-radius: 24px;
  padding: 22px 14px;

  display: flex;
  flex-direction: column;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 4px 8px 24px;
}

.brand-mark {
  width: 42px;
  height: 42px;
  border-radius: 14px;

  display: flex;
  align-items: center;
  justify-content: center;

  background: #202832;
  color: white;

  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.5px;
}

.brand-title {
  font-size: 16px;
  font-weight: 700;
  color: #202832;
}

.brand-subtitle {
  margin-top: 2px;
  color: #98a2ad;
  font-size: 11px;
}

.menu-label {
  padding: 4px 12px 9px;
  font-size: 11px;
  color: #a2abb5;
}

.sidebar-menu {
  border-right: none !important;
  background: transparent !important;
}

.sidebar-menu :deep(.el-menu-item) {
  height: 46px;
  margin-bottom: 5px;

  border-radius: 13px;

  color: #626d78;
  font-size: 14px;

  transition: all 0.2s ease;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.72);
  color: #202832;

  transform: translateX(2px);
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background: rgba(31, 40, 50, 0.92);
  color: white;

  box-shadow: 0 8px 20px rgba(32, 40, 50, 0.13);
}

.menu-dot {
  width: 6px;
  height: 6px;

  margin-right: 12px;

  border-radius: 50%;
  background: currentColor;
  opacity: 0.7;
}

.sidebar-footer {
  margin-top: auto;

  display: flex;
  align-items: center;
  gap: 10px;

  padding: 14px;

  background: rgba(255, 255, 255, 0.55);
  border-radius: 16px;
}

.status-dot {
  width: 8px;
  height: 8px;

  border-radius: 50%;
  background: #73a58e;

  box-shadow: 0 0 0 4px rgba(115, 165, 142, 0.12);
}

.status-title {
  font-size: 12px;
  font-weight: 600;
  color: #4f5964;
}

.status-text {
  margin-top: 3px;
  font-size: 10px;
  color: #a0a8b0;
}

.workspace {
  position: relative;
  z-index: 1;

  flex: 1;
  min-width: 0;
}

.topbar {
  height: 72px;

  border-radius: 22px;

  padding: 0 24px;

  display: flex;
  align-items: center;
  justify-content: space-between;
}

.page-caption {
  color: #202832;
  font-size: 16px;
  font-weight: 700;
}

.page-description {
  margin-top: 4px;

  color: #929ca7;
  font-size: 11px;
}

.user-area {
  display: flex;
  align-items: center;
  gap: 18px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-avatar {
  width: 36px;
  height: 36px;

  border-radius: 12px;

  display: flex;
  align-items: center;
  justify-content: center;

  background: rgba(32, 40, 50, 0.08);

  color: #38424c;
  font-size: 13px;
  font-weight: 700;
}

.user-name {
  font-size: 12px;
  font-weight: 600;
  color: #48525c;
}

.user-role {
  margin-top: 2px;

  color: #a2aab3;
  font-size: 10px;
}

.logout-button {
  height: 34px;
  padding: 0 15px;

  border: 1px solid rgba(32, 40, 50, 0.08);
  border-radius: 11px;

  background: rgba(255, 255, 255, 0.55);
  color: #626c76;

  cursor: pointer;

  transition: all 0.2s ease;
}

.logout-button:hover {
  background: rgba(255, 255, 255, 0.9);
  color: #202832;
}

.page-container {
  height: calc(100vh - 108px);
  margin-top: 18px;

  overflow: auto;

  border-radius: 22px;

  padding: 4px 2px 22px;
}
</style>

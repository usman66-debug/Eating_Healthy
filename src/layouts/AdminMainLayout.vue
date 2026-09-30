<template>
  <div class="layout-container">
    <aside class="layout-sidebar">
      <div class="sidebar-logo">
        <h1 class="font-display">Healthy<span>Diet</span></h1>
      </div>
      <div class="sidebar-user">
        <el-avatar src="@/assets/heros.png" :size="32" />
        <div class="sidebar-user-info">
          <span class="sidebar-user-name">系统管理员</span>
          <span class="sidebar-user-role">管理员</span>
        </div>
      </div>
      <el-menu default-active="2" class="el-menu-vertical-demo">
        <template v-for="menu in filterMenus" :key="menu.id">
          <el-menu-item :index="menu.path" v-if="!menu.children || menu.children.length === 0">
            <el-icon><component :is="menu.icon" /></el-icon>
            <sapm>{{ menu.menuName }}</sapm>
          </el-menu-item>
        </template>
      </el-menu>
    </aside>
  </div>
</template>

<script setup>
import { ref ,computed} from 'vue'
import defaultMenus from "@/data/menuData.js"

const filterMenus = computed(() => {
  return defaultMenus
})
</script>

<style scoped>
  .sidebar-user {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 18px;
  margin: 4px 10px 8px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: var(--r-md);
  border: 1px solid rgba(255, 255, 255, 0.04);
  position: relative;
  z-index: 1;
}

.sidebar-user-info {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.sidebar-user-name {
  font-size: 13px;
  font-weight: 600;
  color: rgba(250, 249, 246, 0.8);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.sidebar-user-role {
  font-size: 11px;
  color: rgba(250, 249, 246, 0.3);
  letter-spacing: 0.04em;
}

/* Sidebar footer */
.sidebar-footer {
  padding: 12px 10px;
  border-top: 1px solid rgba(255, 255, 255, 0.04);
  position: relative;
  z-index: 1;
  flex-shrink: 0;
}

.sidebar-collapse-btn {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  border-radius: var(--r-md);
  cursor: pointer;
  font-size: 13px;
  color: rgba(250, 249, 246, 0.35);
  transition: all var(--dur-fast) ease;
}

.sidebar-collapse-btn:hover {
  background: rgba(255, 255, 255, 0.04);
  color: rgba(250, 249, 246, 0.6);
}

.sidebar-collapse-btn .el-icon {
  font-size: 16px;
}

/* Header action buttons */
.header-action-btn {
  font-size: 18px;
  color: var(--text-muted);
  cursor: pointer;
  padding: 6px;
  border-radius: var(--r-sm);
  transition: all var(--dur-fast) ease;
}

.header-action-btn:hover {
  color: var(--primary);
  background: var(--c-forest-50);
}

/* User trigger */
.user-trigger {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 4px 10px;
  border-radius: var(--r-md);
  transition: background var(--dur-fast) ease;
}

.user-trigger:hover {
  background: var(--c-forest-50);
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}
</style>
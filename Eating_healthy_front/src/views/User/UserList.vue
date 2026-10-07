<template>
    <div class="user-list-page">
        <div class="page-card">
            <!-- 页面头部 -->
            <div class="page-card-header">
                <div class="header-title-area">
                    <h2 class="font-display section-title">用户管理</h2>
                    <p class="section-subtitle">管理系统用户账号与权限分配</p>
                </div>
            </div>
            <!-- 搜索栏 -->
            <div class="search-bar">
                <el-input v-model="queryParams.keyword" placeholder="搜索用户名/昵称/手机号" clearable prefix-icon="Search"
                    style="width: 280px" @keyup.enter="loadData" />
                <el-select v-model="queryParams.status" placeholder="用户状态" clearable style="width: 140px">
                    <el-option label="正常" :value="0" />
                    <el-option label="禁用" :value="1" />
                </el-select>
                <el-button type="primary" @click="loadData"><el-icon>
                        <Search />
                    </el-icon> 查询</el-button>
                <el-button @click="resetQuery">重置</el-button>
                <el-button type="primary" @click="handleAdd">
                    <el-icon>
                        <Plus />
                    </el-icon> 新增用户
                </el-button>
            </div>
            <!-- 表格 -->
            <el-table :data="tableData" stripe v-loading="loading" style="width: 100%">
                <el-table-column prop="id" label="ID" width="70" align="center" />
                <el-table-column prop="username" label="用户名" width="120">
                    <template #default="{ row }">
                        <div class="user-cell">
                            <el-avatar :size="30" :src="row.avatar || ''"
                                style="background: var(--c-forest-100); color: var(--c-forest-700); font-weight: 700; font-size: 12px;">
                                {{ row.nickname?.charAt(0) || 'U' }}
                            </el-avatar>
                            <span>{{ row.username }}</span>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="nickname" label="昵称" min-width="120" />
                <el-table-column prop="phone" label="手机号" min-width="130" />
                <el-table-column label="角色" width="100" align="center">
                    <template #default="{ row }">
                        <el-tag :type="row.roles?.includes('ADMIN') ? 'warning' : 'success'" size="small"
                            effect="plain">
                            {{ row.roles?.includes('ADMIN') ? '管理员' : '用户' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="状态" width="80" align="center">
                    <template #default="{ row }">
                        <div class="status-badge" :class="row.status === 0 ? 'is-active' : 'is-disabled'">
                            <span class="status-dot"></span>
                            {{ row.status === 0 ? '正常' : '禁用' }}
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" label="创建时间" min-width="170" />
                <el-table-column label="操作" width="220" align="center">
                    <template #default="{ row }">
                        <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
                        <el-button link type="warning" @click="handleResetPwd(row)">重置密码</el-button>
                        <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <!-- 分页 -->
            <div class="pagination-wrapper">
                <el-pagination v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize"
                    :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper"
                    @change="loadData" />
            </div>
        </div>
    </div>
</template>
<script setup>
import { ref, reactive,onMounted } from 'vue'
import { getUserListApi } from '@/apis/user'

const queryParams = reactive({
    keyword: '',
    status: ''
})

const loadData =async () => {
    loading.value = true
    const res = await getUserListApi(queryParams)
    tableData.value = res.data.data.records
    total.value = res.data.data.total
    loading.value = false
}
// 重置查询参数
const resetQuery = () => {
    queryParams.keyword = ''
    queryParams.status = ''
    queryParams.pageNum = 1
    loadData()
}
// 新增用户
const handleAdd = () => {
    console.log('新增用户')
}

// 列表
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
// 编辑
const handleEdit = () => { }
// 重置密码
const handleResetPwd = () => { }
// 删除
const handleDelete = () => { }

onMounted(() => {
    loadData()
})
</script>
<style scoped>
.page-card-header {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    margin-bottom: 24px;
}

.header-title-area .section-title {
    margin-bottom: 0;
}

/* User cell with avatar */
.user-cell {
    display: flex;
    align-items: center;
    gap: 10px;
}

.user-cell span {
    font-weight: 600;
    font-size: 13px;
}

/* Custom status badge */
.status-badge {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    font-size: 12px;
    font-weight: 600;
    padding: 3px 10px;
    border-radius: var(--r-pill);
}

.status-badge.is-active {
    background: rgba(61, 139, 71, 0.08);
    color: #3d8b47;
}

.status-badge.is-disabled {
    background: rgba(196, 69, 58, 0.08);
    color: #c4453a;
}

.status-dot {
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: currentColor;
}

.status-badge.is-active .status-dot {
    animation: pulseGlow 2s ease-in-out infinite;
}
</style>

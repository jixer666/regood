<template>
  <div class="category-container">
    <div class="btn-div">
      <el-button plain size="mini" type="primary" @click="getList">刷新</el-button>
      <el-button plain size="mini" type="primary" @click="handleAdd(null)">新增分类</el-button>
    </div>
    <el-table
      v-loading="loading"
      :data="tableList"
      row-key="categoryId"
      border
      :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
    >
      <el-table-column label="分类名称" align="center" key="name" prop="name" :show-overflow-tooltip="true" />
      <el-table-column label="分类图标" align="center" key="icon" prop="icon" width="100">
        <template slot-scope="scope">
          <span v-if="scope.row.icon">{{ scope.row.icon }}</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="排序" align="center" key="sort" prop="sort" width="80" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" width="200" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="handleAdd(scope.row)">新增子分类</el-button>
          <el-button size="mini" type="text" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="mini" type="text" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="上级分类" v-if="form.parentId && form.parentId !== 0">
          <el-input :value="parentName" disabled></el-input>
        </el-form-item>
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分类名称"></el-input>
        </el-form-item>
        <el-form-item label="分类图标" prop="icon">
          <el-input v-model="form.icon" placeholder="请输入分类图标"></el-input>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="9999"></el-input-number>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getCategoryTree, saveCategory, updateCategory, deleteCategory } from '@/api/business/category'

export default {
  name: 'Category',
  data() {
    return {
      loading: false,
      tableList: [],
      dialogVisible: false,
      dialogTitle: '',
      parentName: '',
      form: {
        categoryId: null,
        name: '',
        icon: '',
        sort: 0,
        parentId: 0
      },
      rules: {
        name: [
          { required: true, message: '请输入分类名称', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      getCategoryTree().then(response => {
        this.tableList = response.data || []
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handleAdd(row) {
      this.form = {
        categoryId: null,
        name: '',
        icon: '',
        sort: 0,
        parentId: row ? row.categoryId : 0
      }
      this.parentName = row ? row.name : ''
      this.dialogTitle = row ? '新增子分类' : '新增分类'
      this.dialogVisible = true
    },
    handleEdit(row) {
      this.form = {
        categoryId: row.categoryId,
        name: row.name,
        icon: row.icon || '',
        sort: row.sort || 0,
        parentId: row.parentId || 0
      }
      this.parentName = ''
      this.dialogTitle = '编辑分类'
      this.dialogVisible = true
    },
    handleDelete(row) {
      this.$confirm('确定删除该分类吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        deleteCategory(row.categoryId).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          if (this.form.categoryId) {
            updateCategory(this.form).then(() => {
              this.$message.success('修改成功')
              this.dialogVisible = false
              this.getList()
            })
          } else {
            saveCategory(this.form).then(() => {
              this.$message.success('保存成功')
              this.dialogVisible = false
              this.getList()
            })
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.category-container {
  padding: 20px;
}
.btn-div {
  margin-bottom: 15px;
}
</style>

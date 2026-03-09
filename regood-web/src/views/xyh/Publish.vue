<template>
  <div class="xyh-publish">
    <xyh-header />
    
    <div class="publish-container">
      <div class="publish-wrapper">
        <div class="publish-header">
          <h2 class="publish-title">发布商品</h2>
          <p class="publish-subtitle">让闲置流动起来，帮助他人，也为自己创造价值</p>
        </div>

        <div class="publish-form">
          <!-- 媒体上传区 -->
          <div class="upload-section">
            <div class="upload-title">
              <span>商品图片</span>
              <span class="upload-tips">（最多上传 9 张，首张作为封面）</span>
            </div>
            <div class="upload-area">
              <el-upload
                v-model:file-list="fileList"
                :auto-upload="false"
                :show-file-list="false"
                :drag="true"
                :limit="9"
                action="/api/upload"
                multiple
                list-type="picture-card"
                :on-exceed="handleExceed"
                class="upload-container"
              >
                <i class="el-icon-plus"></i>
                <div slot="file" slot-scope="{ file }">
                  <img
                    class="el-upload-list__item-thumbnail"
                    :src="file.url"
                    alt=""
                  >
                  <span class="el-upload-list__item-actions">
                    <span
                      class="el-upload-list__item-preview"
                      @click="handlePictureCardPreview(file)"
                    >
                      <i class="el-icon-zoom-in"></i>
                    </span>
                    <span
                      v-if="!disabled"
                      class="el-upload-list__item-delete"
                      @click="handleRemove(file)"
                    >
                      <i class="el-icon-delete"></i>
                    </span>
                  </span>
                </div>
              </el-upload>
              <div class="upload-preview" v-if="previewVisible">
                <img :src="previewImage" alt="" class="preview-image">
                <el-button
                  class="preview-close"
                  icon="el-icon-close"
                  circle
                  @click="previewVisible = false"
                ></el-button>
              </div>
            </div>
          </div>

          <!-- 基础信息 -->
          <div class="form-section">
            <div class="form-title">基础信息</div>
            <el-form
              ref="form"
              :model="form"
              :rules="rules"
              label-width="100px"
              class="form-content"
            >
              <el-form-item label="商品名称" prop="title">
                <el-input
                  v-model="form.title"
                  placeholder="请输入商品名称（限 30 字）"
                  maxlength="30"
                  show-word-limit
                />
              </el-form-item>

              <el-form-item label="商品分类" prop="category">
                <el-cascader
                  v-model="form.category"
                  :options="categoryOptions"
                  :props="categoryProps"
                  placeholder="请选择商品分类"
                  clearable
                />
              </el-form-item>

              <el-form-item label="商品描述" prop="description">
                <el-input
                  v-model="form.description"
                  type="textarea"
                  :rows="5"
                  placeholder="请详细描述商品情况，描述越详细，卖出越快哦！"
                  maxlength="500"
                  show-word-limit
                />
              </el-form-item>
            </el-form>
          </div>

          <!-- 属性选择 -->
          <div class="form-section">
            <div class="form-title">商品属性</div>
            <el-form
              ref="form"
              :model="form"
              :rules="rules"
              label-width="100px"
              class="form-content"
            >
              <el-form-item label="商品成色" prop="condition">
                <el-radio-group v-model="form.condition">
                  <el-radio label="全新">全新</el-radio>
                  <el-radio label="99新">99 新</el-radio>
                  <el-radio label="9成新">9 成新</el-radio>
                  <el-radio label="8成新">8 成新</el-radio>
                  <el-radio label="8成新以下">8 成新以下</el-radio>
                </el-radio-group>
              </el-form-item>

              <el-form-item label="购入价格" prop="originalPrice">
                <el-input-number
                  v-model="form.originalPrice"
                  :min="0"
                  :precision="2"
                  placeholder="请输入购入原价"
                  class="price-input"
                />
                <span class="price-unit">元</span>
              </el-form-item>

              <el-form-item label="出手价格" prop="price">
                <el-input-number
                  v-model="form.price"
                  :min="0"
                  :precision="2"
                  placeholder="请输入出售价格"
                  class="price-input"
                />
                <span class="price-unit">元</span>
              </el-form-item>
            </el-form>
          </div>

          <!-- 交易方式 -->
          <div class="form-section">
            <div class="form-title">交易方式</div>
            <el-form
              ref="form"
              :model="form"
              :rules="rules"
              label-width="100px"
              class="form-content"
            >
              <el-form-item label="交易方式" prop="tradeMethods">
                <el-checkbox-group v-model="form.tradeMethods">
                  <el-checkbox label="faceToFace">校园面交</el-checkbox>
                  <el-checkbox label="express">快递寄送</el-checkbox>
                  <el-checkbox label="pickup">自提</el-checkbox>
                </el-checkbox-group>
              </el-form-item>

              <el-form-item
                v-if="form.tradeMethods.includes('faceToFace')"
                label="面交地点"
                prop="meetingPlace"
              >
                <el-select
                  v-model="form.meetingPlace"
                  placeholder="请选择面交地点"
                  clearable
                >
                  <el-option label="一食堂门口" value="one_canteen"></el-option>
                  <el-option label="二食堂门口" value="two_canteen"></el-option>
                  <el-option label="图书馆门口" value="library"></el-option>
                  <el-option label="教学楼楼下" value="classroom"></el-option>
                  <el-option label="宿舍楼下" value="dormitory"></el-option>
                  <el-option label="其他地点" value="other"></el-option>
                </el-select>
              </el-form-item>
            </el-form>
          </div>

          <!-- 提交按钮 -->
          <div class="form-actions">
            <el-button
              type="primary"
              size="large"
              class="submit-btn"
              :loading="submitLoading"
              @click="handleSubmit"
            >
              一键发布
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <xyh-footer />
  </div>
</template>

<script>
import XyhHeader from '@/components/XyhHeader'
import XyhFooter from '@/components/XyhFooter'

export default {
  name: 'XyhPublish',
  components: {
    XyhHeader,
    XyhFooter
  },
  data() {
    return {
      fileList: [],
      previewVisible: false,
      previewImage: '',
      disabled: false,
      submitLoading: false,
      form: {
        title: '',
        category: [],
        description: '',
        condition: '9成新',
        originalPrice: 0,
        price: 0,
        tradeMethods: [],
        meetingPlace: ''
      },
      categoryOptions: [
        {
          value: 'digital',
          label: '数码电子',
          children: [
            { value: 'digital/phone', label: '手机' },
            { value: 'digital/computer', label: '电脑' },
            { value: 'digital/tablet', label: '平板' },
            { value: 'digital/accessories', label: '配件' }
          ]
        },
        {
          value: 'books',
          label: '书籍教材',
          children: [
            { value: 'books/textbook', label: '教材' },
            { value: 'books/novel', label: '小说' },
            { value: 'books/reference', label: '参考书' }
          ]
        },
        {
          value: 'transport',
          label: '生活代步',
          children: [
            { value: 'transport/bike', label: '自行车' },
            { value: 'transport/ebike', label: '电动车' }
          ]
        },
        {
          value: 'clothing',
          label: '衣物鞋帽',
          children: [
            { value: 'clothing/top', label: '上衣' },
            { value: 'clothing/bottom', label: '下装' },
            { value: 'clothing/shoes', label: '鞋子' }
          ]
        },
        {
          value: 'beauty',
          label: '美妆日用',
          children: [
            { value: 'beauty/makeup', label: '化妆品' },
            { value: 'beauty/skincare', label: '护肤品' }
          ]
        }
      ],
      categoryProps: {
        value: 'value',
        label: 'label',
        children: 'children'
      },
      rules: {
        title: [
          { required: true, message: '请输入商品名称', trigger: 'blur' },
          { min: 1, max: 30, message: '长度在 1 到 30 个字符', trigger: 'blur' }
        ],
        category: [
          { required: true, message: '请选择商品分类', trigger: 'change' }
        ],
        description: [
          { required: true, message: '请输入商品描述', trigger: 'blur' }
        ],
        condition: [
          { required: true, message: '请选择商品成色', trigger: 'change' }
        ],
        price: [
          { required: true, message: '请输入出售价格', trigger: 'change' },
          { type: 'number', message: '价格必须为数字', trigger: 'change' }
        ]
      }
    }
  },
  methods: {
    handleExceed(files, fileList) {
      this.$message.warning('最多只能上传 9 张图片')
    },
    handleRemove(file) {
      const index = this.fileList.indexOf(file)
      this.fileList.splice(index, 1)
    },
    handlePictureCardPreview(file) {
      this.previewImage = file.url
      this.previewVisible = true
    },
    handleSubmit() {
      this.$refs.form.validate(valid => {
        if (!valid) {
          return
        }
        
        this.submitLoading = true
        
        // 模拟提交
        setTimeout(() => {
          this.$message.success('商品发布成功！')
          this.submitLoading = false
          this.$router.push('/home')
        }, 1500)
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.xyh-publish {
  min-height: 100vh;
  background: #f5f7fa;
}

.publish-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.publish-wrapper {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  padding: 32px;
}

.publish-header {
  text-align: center;
  margin-bottom: 32px;
  
  .publish-title {
    font-size: 28px;
    font-weight: 600;
    color: #333;
    margin-bottom: 8px;
  }
  
  .publish-subtitle {
    font-size: 14px;
    color: #999;
  }
}

.publish-form {
  .upload-section,
  .form-section {
    margin-bottom: 32px;
  }
  
  .upload-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    font-weight: 600;
    color: #333;
    margin-bottom: 16px;
    
    .upload-tips {
      font-size: 13px;
      color: #999;
      font-weight: 400;
    }
  }
  
  .upload-area {
    .upload-container {
      ::v-deep .el-upload--picture-card {
        width: 100px;
        height: 100px;
      }
    }
    
    .upload-preview {
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.6);
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 1000;
      
      .preview-image {
        max-width: 80%;
        max-height: 80%;
        border-radius: 8px;
      }
      
      .preview-close {
        position: absolute;
        top: 30px;
        right: 30px;
        background: #fff;
        color: #333;
      }
    }
  }
  
  .form-title {
    font-size: 16px;
    font-weight: 600;
    color: #333;
    margin-bottom: 20px;
    padding-bottom: 12px;
    border-bottom: 2px solid #1890ff;
  }
  
  .form-content {
    .price-input {
      width: 200px;
    }
    
    .price-unit {
      margin-left: 8px;
      color: #666;
    }
  }
  
  .form-actions {
    margin-top: 40px;
    padding-top: 32px;
    border-top: 1px solid #e8e8e8;
    
    .submit-btn {
      width: 100%;
      height: 52px;
      font-size: 18px;
      font-weight: 600;
      background: linear-gradient(135deg, #1890ff 0%, #00b4b8 100%);
      border: none;
    }
  }
}

@media (max-width: 768px) {
  .publish-wrapper {
    padding: 20px;
  }
  
  .publish-header .publish-title {
    font-size: 24px;
  }
  
  .form-content .price-input {
    width: 100%;
  }
}
</style>

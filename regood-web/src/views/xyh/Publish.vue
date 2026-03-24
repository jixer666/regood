<template>
  <div class="publish-page">
    <xyh-header />

    <div class="publish-container">
      <div class="publish-header">
        <h1 class="publish-title">{{ isEdit ? '编辑商品' : '发布闲置' }}</h1>
        <p class="publish-subtitle">{{ isEdit ? '修改商品信息后点击保存' : '让闲置流动起来，帮助他人，也为自己创造价值' }}</p>
      </div>

      <div class="publish-main">
        <div class="main-left">
          <div class="upload-card">
            <div class="card-header">
              <h3 class="card-title">商品图片</h3>
              <span class="card-tip">最多上传9张，首张为封面</span>
            </div>
            <div class="upload-content">
              <FileUpload
                v-model="form.images"
                :limit="9"
                :file-size="5"
                :file-type="['png', 'jpg', 'jpeg']"
                :drag="true"
              />
            </div>
          </div>

          <div class="preview-card">
            <div class="card-header">
              <h3 class="card-title">发布预览</h3>
            </div>
            <div class="preview-content">
              <div class="preview-item">
                <el-image
                  :src="firstImage"
                  fit="cover"
                  class="preview-image"
                >
                  <div slot="error" class="image-placeholder">
                    <i class="el-icon-picture-outline"></i>
                  </div>
                </el-image>
                <div class="preview-info">
                  <div class="preview-title">{{ form.title || '商品标题' }}</div>
                  <div class="preview-price">
                    <span class="price-symbol">¥</span>
                    <span class="price-value">{{ form.price || '0.00' }}</span>
                  </div>
                  <div class="preview-tags">
                    <span class="tag condition" v-if="form.condition">{{ form.condition }}</span>
                    <span class="tag" v-if="form.freeShipping">包邮</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="main-right">
          <div class="form-card">
            <div class="card-header">
              <h3 class="card-title">基本信息</h3>
            </div>
            <el-form
              ref="form"
              :model="form"
              :rules="rules"
              label-width="100px"
              class="form-content"
            >
              <el-form-item label="商品标题" prop="title">
                <el-input
                  v-model="form.title"
                  placeholder="请输入商品名称，限30字"
                  maxlength="30"
                  show-word-limit
                />
              </el-form-item>

              <el-form-item label="商品分类" prop="category">
                <el-cascader
                  v-model="form.category"
                  :options="categoryOptions"
                  placeholder="请选择商品分类"
                  clearable
                  class="full-width"
                />
              </el-form-item>

              <el-form-item label="商品描述" prop="description">
                <el-input
                  v-model="form.description"
                  type="textarea"
                  :rows="5"
                  placeholder="品牌型号、入手渠道、转手原因等，描述越详细越容易卖出哦~"
                  maxlength="500"
                  show-word-limit
                />
              </el-form-item>
            </el-form>
          </div>

          <div class="form-card">
            <div class="card-header">
              <h3 class="card-title">商品属性</h3>
            </div>
            <el-form
              :model="form"
              label-width="100px"
              class="form-content"
            >
              <el-form-item label="商品成色">
                <div class="condition-options">
                  <div
                    v-for="item in conditionOptions"
                    :key="item.value"
                    :class="['condition-item', { active: form.condition === item.value }]"
                    @click="form.condition = item.value"
                  >
                    {{ item.label }}
                  </div>
                </div>
              </el-form-item>

              <el-form-item label="购入价格">
                <el-input-number
                  v-model="form.originalPrice"
                  :min="0"
                  :precision="2"
                  placeholder="请输入购入原价"
                  class="price-input"
                />
                <span class="price-unit">元</span>
              </el-form-item>

              <el-form-item label="出售价格" class="sell-price">
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

          <div class="form-card">
            <div class="card-header">
              <h3 class="card-title">交易方式</h3>
            </div>
            <el-form
              :model="form"
              label-width="100px"
              class="form-content"
            >
              <el-form-item label="交易方式">
                <div class="trade-options">
                  <div
                    v-for="item in tradeOptions"
                    :key="item.value"
                    :class="['trade-item', { active: form.tradeMethods.includes(item.value) }]"
                    @click="toggleTrade(item.value)"
                  >
                    <div class="trade-icon">
                      <i :class="item.icon"></i>
                    </div>
                    <div class="trade-info">
                      <div class="trade-name">{{ item.label }}</div>
                    </div>
                    <div class="trade-check" v-if="form.tradeMethods.includes(item.value)">
                      <i class="el-icon-check"></i>
                    </div>
                  </div>
                </div>
              </el-form-item>

              <el-form-item label="面交地点" v-if="form.tradeMethods.length > 0">
                <el-input v-model="form.meetingPlace" placeholder="请输入面交地点" class="full-width" />
              </el-form-item>
            </el-form>
          </div>

          <div class="form-card">
            <div class="card-header">
              <h3 class="card-title">发货设置</h3>
            </div>
            <el-form
              :model="form"
              label-width="100px"
              class="form-content"
            >
              <el-form-item label="是否包邮">
                <el-switch v-model="form.freeShipping" />
                <span class="switch-tip">{{ form.freeShipping ? '卖家承担运费' : '买家承担运费' }}</span>
              </el-form-item>

              <el-form-item label="运费" v-if="!form.freeShipping">
                <el-input-number
                  v-model="form.shippingFee"
                  :min="0"
                  :precision="2"
                  placeholder="请输入运费"
                  class="price-input"
                />
                <span class="price-unit">元</span>
              </el-form-item>
            </el-form>
          </div>

          <div class="form-card tips-card">
            <i class="el-icon-warning"></i>
            <span>发布即表示同意《平台公约》，请勿发布违规商品，违规商品将被下架处理</span>
          </div>

          <div class="form-actions">
            <el-button class="draft-btn" @click="saveDraft" v-if="!isEdit">
              <i class="el-icon-document"></i>
              存草稿
            </el-button>
            <el-button type="primary" class="publish-btn" @click="handlePublish">
              <i class="el-icon-check"></i>
              {{ isEdit ? '保存修改' : '立即发布' }}
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
import FileUpload from '@/components/FileUpload'
import { saveProduct, getProductDetail, updateProduct } from '@/api/business/product'
import { getCategoryTree } from '@/api/business/category'

const TRADE_METHODS_MAP = {
  face: '当面交易',
  express: '快递邮寄',
  pickup: '同城自提'
}

export default {
  name: 'XyhPublish',
  components: {
    XyhHeader,
    XyhFooter,
    FileUpload
  },
  data() {
    return {
      isEdit: false,
      productId: null,
      uploadingImages: [],
      form: {
        images: '',
        title: '',
        description: '',
        category: [],
        condition: '',
        originalPrice: 0,
        price: 0,
        tradeMethods: [],
        meetingPlace: '',
        freeShipping: true,
        shippingFee: 0
      },
      rules: {
        title: [
          { required: true, message: '请输入商品标题', trigger: 'blur' },
          { min: 1, max: 30, message: '长度在 1 到 30 个字符', trigger: 'blur' }
        ],
        category: [
          { required: true, message: '请选择商品分类', trigger: 'change' }
        ],
        description: [
          { required: true, message: '请输入商品描述', trigger: 'blur' }
        ]
      },
      conditionOptions: [
        { label: '全新', value: '全新' },
        { label: '99新', value: '99新' },
        { label: '9成新', value: '9成新' },
        { label: '8成新', value: '8成新' },
        { label: '7成新', value: '7成新' }
      ],
      categoryOptions: [],
      tradeOptions: [
        { label: '当面交易', value: 'face', icon: 'el-icon-user', desc: '校园内面交，安全便捷' },
        { label: '快递邮寄', value: 'express', icon: 'el-icon-truck', desc: '全国配送，运费到付' },
        { label: '同城自提', value: 'pickup', icon: 'el-icon-location', desc: '买家上门自提' }
      ],
      meetingPlaces: [
        { label: '一食堂门口', value: 'one_canteen' },
        { label: '二食堂门口', value: 'two_canteen' },
        { label: '图书馆门口', value: 'library' },
        { label: '教学楼楼下', value: 'classroom' },
        { label: '宿舍楼下', value: 'dormitory' },
        { label: '其他地点', value: 'other' }
      ]
    }
  },
  computed: {
    firstImage() {
      if (this.form.images) {
        const images = this.form.images.split(',')
        return images.length > 0 ? images[0] : ''
      }
      return ''
    }
  },
  async created() {
    await this.loadCategoryTree()
    this.checkEditMode()
  },
  methods: {
    checkEditMode() {
      const id = this.$route.query.id
      if (id) {
        this.isEdit = true
        this.productId = id
        this.loadProductDetail()
      }
    },
    findCategoryPath(categoryId) {
      for (const parent of this.categoryOptions) {
        if (parent.value === categoryId) {
          return [categoryId]
        }
        if (parent.children) {
          for (const child of parent.children) {
            if (child.value === categoryId) {
              return [parent.value, categoryId]
            }
          }
        }
      }
      return []
    },
    async loadProductDetail() {
      try {
        const res = await getProductDetail(this.productId)
        if (res.code === 200 && res.data) {
          const data = res.data
          const tradeMethods = data.tradeMethods
            ? (typeof data.tradeMethods === 'string' ? data.tradeMethods.split(',') : data.tradeMethods)
            : ['face']
          
          const categoryPath = this.findCategoryPath(data.categoryId)
          
          this.form = {
            images: Array.isArray(data.images) ? data.images.join(',') : (data.images || ''),
            title: data.title || '',
            description: data.description || '',
            category: categoryPath,
            condition: data.condition || '9成新',
            originalPrice: data.originalPrice || 0,
            price: data.price || 0,
            tradeMethods: tradeMethods,
            meetingPlace: data.meetingPlace || '',
            freeShipping: data.freeShipping !== false,
            shippingFee: data.shippingFee || 0
          }
        }
      } catch (error) {
        console.error('加载商品详情失败', error)
        this.$message.error('加载商品详情失败')
      }
    },
    async loadCategoryTree() {
      try {
        const res = await getCategoryTree()
        if (res.code === 200 && res.data) {
          this.categoryOptions = res.data.map(item => ({
            value: item.categoryId,
            label: item.name,
            children: (item.children || []).map(child => ({
              value: child.categoryId,
              label: child.name
            }))
          }))
        }
      } catch (error) {
        console.error('加载分类失败', error)
      }
    },
    toggleTrade(value) {
      const index = this.form.tradeMethods.indexOf(value)
      if (index > -1) {
        if (this.form.tradeMethods.length <= 1) {
          this.$message.warning('至少选择一种交易方式')
          return
        }
        this.form.tradeMethods.splice(index, 1)
      } else {
        this.form.tradeMethods.push(value)
      }
    },
    saveDraft() {
      this.$message.success('草稿已保存')
    },
    async handlePublish() {
      try {
        const valid = await this.$refs.form.validate().catch(() => false)
        if (!valid) return

        if (!this.form.images) {
          this.$message.warning('请上传商品图片')
          return
        }

        if (this.form.price <= 0) {
          this.$message.warning('请输入出售价格')
          return
        }

        const categoryId = Array.isArray(this.form.category)
          ? this.form.category[this.form.category.length - 1]
          : this.form.category

        const imagesArray = typeof this.form.images === 'string'
          ? this.form.images.split(',').filter(Boolean)
          : this.form.images

        const tradeMethodsStr = Array.isArray(this.form.tradeMethods)
          ? this.form.tradeMethods.join(',')
          : this.form.tradeMethods

        const productData = {
          title: this.form.title,
          description: this.form.description,
          categoryId: categoryId,
          condition: this.form.condition,
          originalPrice: this.form.originalPrice,
          price: this.form.price,
          images: imagesArray,
          tradeMethods: tradeMethodsStr,
          meetingPlace: this.form.meetingPlace,
          freeShipping: this.form.freeShipping,
          shippingFee: this.form.shippingFee
        }

        if (this.isEdit) {
          productData.productId = this.productId
          await updateProduct(productData)
          this.$message.success('更新成功！')
        } else {
          await saveProduct(productData)
          this.$message.success('发布成功！')
        }
        
        this.$router.push('/profile?tab=publish')
      } catch (error) {
        console.error(this.isEdit ? '更新失败' : '发布失败', error)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.publish-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.publish-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.publish-header {
  margin-bottom: 24px;

  .publish-title {
    font-size: 28px;
    font-weight: 600;
    color: #1a1a1a;
    margin-bottom: 8px;
  }

  .publish-subtitle {
    font-size: 14px;
    color: #86909c;
  }
}

.publish-main {
  display: flex;
  gap: 24px;
}

.main-left {
  width: 400px;
  flex-shrink: 0;
}

.main-right {
  flex: 1;
  min-width: 0;
}

.form-card,
.upload-card,
.preview-card {
  background: #fff;
  border-radius: 8px;
  margin-bottom: 16px;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 20px;
    border-bottom: 1px solid #f0f0f0;

    .card-title {
      font-size: 16px;
      font-weight: 600;
      color: #1a1a1a;
    }

    .card-tip {
      font-size: 13px;
      color: #86909c;
    }
  }
}

.upload-content {
  padding: 20px;

  .image-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 12px;
  }

  .image-item {
    aspect-ratio: 1;
    border-radius: 8px;
    overflow: hidden;
    position: relative;
    cursor: pointer;

    &:hover .image-actions {
      opacity: 1;
    }

    .el-image {
      width: 100%;
      height: 100%;
    }

    .image-cover {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      background: linear-gradient(135deg, #ff6b00 0%, #ff8533 100%);
      color: #fff;
      font-size: 12px;
      padding: 4px 0;
      text-align: center;
    }

    .image-actions {
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      background: rgba(0, 0, 0, 0.6);
      display: flex;
      opacity: 0;
      transition: opacity 0.2s;

      .action-btn {
        flex: 1;
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 8px 0;
        color: #fff;
        font-size: 12px;
        cursor: pointer;

        &:hover {
          background: rgba(255, 255, 255, 0.1);
        }

        &.delete:hover {
          background: rgba(255, 77, 79, 0.3);
        }

        i {
          font-size: 16px;
          margin-bottom: 2px;
        }
      }
    }
  }

  .upload-box {
    aspect-ratio: 1;
    border: 2px dashed #e5e6eb;
    border-radius: 8px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      border-color: #ff6b00;
      background: #fff9f5;

      i, span {
        color: #ff6b00;
      }
    }

    i {
      font-size: 32px;
      color: #c9cdd4;
      margin-bottom: 8px;
    }

    span {
      font-size: 14px;
      color: #86909c;
    }
  }
}

.preview-content {
  padding: 20px;

  .preview-item {
    display: flex;
    gap: 12px;

    .preview-image {
      width: 100px;
      height: 100px;
      border-radius: 8px;
      flex-shrink: 0;

      .image-placeholder {
        width: 100%;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        background: #f5f7fa;

        i {
          font-size: 32px;
          color: #c9cdd4;
        }
      }
    }

    .preview-info {
      flex: 1;
      min-width: 0;

      .preview-title {
        font-size: 14px;
        color: #1a1a1a;
        margin-bottom: 8px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .preview-price {
        margin-bottom: 8px;

        .price-symbol {
          font-size: 14px;
          color: #ff4d4f;
        }

        .price-value {
          font-size: 20px;
          font-weight: 600;
          color: #ff4d4f;
        }
      }

      .preview-tags {
        display: flex;
        gap: 6px;

        .tag {
          font-size: 12px;
          padding: 2px 6px;
          border-radius: 4px;
          background: #f5f7fa;
          color: #86909c;

          &.condition {
            background: #fff5e6;
            color: #ff6b00;
          }
        }
      }
    }
  }
}

.form-content {
  padding: 20px;

  .full-width {
    width: 100%;
  }

  .price-input {
    width: 200px;
  }

  .price-unit {
    margin-left: 8px;
    color: #86909c;
  }

  .switch-tip {
    margin-left: 12px;
    font-size: 13px;
    color: #86909c;
  }
}

.condition-options {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;

  .condition-item {
    padding: 8px 20px;
    border: 1px solid #e5e6eb;
    border-radius: 4px;
    font-size: 14px;
    color: #4e5969;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      border-color: #ff6b00;
      color: #ff6b00;
    }

    &.active {
      background: #fff5e6;
      border-color: #ff6b00;
      color: #ff6b00;
    }
  }
}

.sell-price {
  ::v-deep .el-form-item__label {
    color: #ff4d4f;
    font-weight: 600;
  }
}

.trade-options {
  display: flex;
  gap: 16px;

  .trade-item {
    flex: 1;
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 16px;
    border: 1px solid #e5e6eb;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      border-color: #ff6b00;
    }

    &.active {
      border-color: #ff6b00;
      background: #fff9f5;
    }

    .trade-icon {
      width: 48px;
      height: 48px;
      background: #f5f7fa;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;

      i {
        font-size: 24px;
        color: #ff6b00;
      }
    }

    .trade-info {
      flex: 1;

      .trade-name {
        font-size: 14px;
        font-weight: 500;
        color: #1a1a1a;
        margin-bottom: 4px;
      }

      .trade-desc {
        font-size: 12px;
        color: #86909c;
      }
    }

    .trade-check {
      width: 24px;
      height: 24px;
      background: #ff6b00;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;

      i {
        color: #fff;
        font-size: 14px;
      }
    }
  }
}

.tips-card {
  padding: 16px 20px;
  display: flex;
  align-items: center;
  gap: 8px;
  background: #fffbe6;
  border: 1px solid #ffe58f;

  i {
    font-size: 16px;
    color: #faad14;
  }

  span {
    font-size: 13px;
    color: #d48806;
  }
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 16px;
  padding: 20px;
  background: #fff;
  border-radius: 8px;

  .draft-btn {
    padding: 12px 32px;
    font-size: 14px;

    i {
      margin-right: 6px;
    }
  }

  .publish-btn {
    padding: 12px 48px;
    font-size: 14px;
    background: linear-gradient(135deg, #ff6b00 0%, #ff8533 100%);
    border: none;

    i {
      margin-right: 6px;
    }

    &:hover {
      background: linear-gradient(135deg, #ff8533 0%, #ff9d55 100%);
    }
  }
}

@media (max-width: 1024px) {
  .publish-main {
    flex-direction: column;
  }

  .main-left {
    width: 100%;
  }

  .upload-content .image-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 768px) {
  .publish-container {
    padding: 16px;
  }

  .upload-content .image-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .trade-options {
    flex-direction: column;
  }

  .form-actions {
    flex-direction: column;

    .draft-btn,
    .publish-btn {
      width: 100%;
    }
  }
}
</style>

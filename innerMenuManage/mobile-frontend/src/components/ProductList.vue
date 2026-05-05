<template>
  <div class="product-list">
    <van-card
      v-for="(item, index) in products"
      :key="item.id || index"
      :thumb="item.image"
      :title="item.title"
      :desc="item.desc"
      :price="item.price"
      :origin-price="item.originPrice"
      :tag="item.tag"
      lazy-load
      @click="handleClick(item)"
    >
      <template #footer v-if="showFooter">
        <van-button size="mini">加入购物车</van-button>
        <van-button size="mini" type="primary">立即购买</van-button>
      </template>
    </van-card>
    
    <van-empty v-if="products.length === 0" description="暂无商品" />
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  config: {
    type: Object,
    default: () => ({})
  },
  componentData: {
    type: Object,
    default: () => ({})
  }
})

const router = useRouter()
const products = ref([])
const showFooter = computed(() => props.config?.showFooter !== false)

const loadProducts = () => {
  if (props.config?.products) {
    products.value = props.config.products
  } else if (props.componentData?.default_config?.products) {
    products.value = props.componentData.default_config.products
  } else {
    products.value = getMockProducts()
  }
}

const getMockProducts = () => {
  return [
    {
      id: 1,
      image: 'https://img.yzcdn.cn/vant/ipad.jpeg',
      title: '示例商品1',
      desc: '这是商品描述信息',
      price: 100.00,
      originPrice: 200.00,
      tag: '热卖'
    },
    {
      id: 2,
      image: 'https://img.yzcdn.cn/vant/ipad.jpeg',
      title: '示例商品2',
      desc: '这是商品描述信息',
      price: 150.00,
      originPrice: 300.00,
      tag: '新品'
    }
  ]
}

const handleClick = (item) => {
  if (item.url) {
    router.push(item.url)
  }
}

onMounted(() => {
  loadProducts()
})
</script>

<style lang="scss" scoped>
.product-list {
  background-color: $bg-color-light;
  padding: $spacing-sm;
  
  :deep(.van-card) {
    margin-bottom: $spacing-sm;
  }
}
</style>

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { Button, Tabbar, TabbarItem, Swiper, SwiperItem, Grid, GridItem, Card, List, PullRefresh, NavBar, Icon, Cell, CellGroup, Tag, Image as VanImage } from 'vant'
import 'vant/lib/index.css'
import './styles/index.scss'

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.use(Button)
app.use(Tabbar)
app.use(TabbarItem)
app.use(Swiper)
app.use(SwiperItem)
app.use(Grid)
app.use(GridItem)
app.use(Card)
app.use(List)
app.use(PullRefresh)
app.use(NavBar)
app.use(Icon)
app.use(Cell)
app.use(CellGroup)
app.use(Tag)
app.use(VanImage)

app.mount('#app')

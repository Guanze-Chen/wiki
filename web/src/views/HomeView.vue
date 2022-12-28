<template>
  <a-layout>
    <a-layout-sider width="200" style="background: #fff">
      <a-menu
          mode="inline"
          :style="{ height: '100%', borderRight: 0 }"
          @click="handleClick"
      >
        <a-menu-item key="welcome">
            <book-filled />
            <span>欢迎</span>
        </a-menu-item>
        <a-sub-menu v-for="item in level1" :key="item.id">
          <template #title>
            <link-outlined />
            <span>{{item.name}}</span>
          </template>

          <a-menu-item v-for="child in item.children" :key="child.id">
            <paper-clip-outlined />
            <span>{{child.name}}</span>
          </a-menu-item>

        </a-sub-menu>

      </a-menu>
    </a-layout-sider>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <div class="welcome" v-show="isShowWelcome">欢迎来到知识库</div>
      <a-list v-show="!isShowWelcome" item-layout="vertical" size="large" :pagination="pagination" :grid="{gutter: 20, column: 3}" :data-source="ebooks">
        <template #renderItem="{ item }">
          <a-list-item key="item.name">
            <template #actions>
          <span v-for="{ type, text } in actions" :key="type">
            <component :is="type" style="margin-right: 8px" />
            {{ text }}
          </span>
            </template>
            <a-list-item-meta :description="item.description">
              <template #title>
                <router-link :to="'/docdetail?ebookId=' + item.id">
                  {{item.name}}
                </router-link>
              </template>
              <template #avatar><a-avatar :src="item.cover" /></template>
            </a-list-item-meta>
          </a-list-item>
        </template>
      </a-list>
    </a-layout-content>

  </a-layout>
</template>

<script lang="ts">
import { defineComponent, onMounted, ref, reactive, toRef } from 'vue';
import { BookFilled, LinkOutlined, PaperClipOutlined } from '@ant-design/icons-vue';
import axios from 'axios';
import {message} from "ant-design-vue";
import {Tool} from "@/utils/tool";


export default defineComponent({
  name: 'HomeView',
  components: {
    BookFilled,
    LinkOutlined,
    PaperClipOutlined
  },
  setup() {

    const actions: Record<string, string>[] = [
      { type: 'StarOutlined', text: '156' },
      { type: 'LikeOutlined', text: '156' },
      { type: 'MessageOutlined', text: '2' },
    ];
    const ebooks = ref("")
    const pagination = ref({
      current:1,
      pageSize:5,
      total:0
    });

    const level1 = ref();
    let categorys: any;

    const handleQueryCategory = () => {
      axios.get('/category/all', {
      })
          .then((res) => {
            const data = res.data;
            if (data.success) {
              categorys = data.content;
              level1.value = [];
              level1.value = Tool.array2Tree(categorys, 0);

              // 加载目录完成后再去加载电子书
              axios.get("/ebook/list", {
                params: {
                  page: 1,
                  size: 1000
                }
              })
                  .then((res) => {
                    const data = res.data;
                    ebooks.value = data.content.list;
                  })
            } else {
              message.error(data.message);
            }
          })
    };

    const handleQueryEbook = () => {
      axios.get("/ebook/list", {
        params: {
          page: 1,
          size: 1000,
          categoryId2: categoryId2,
        }
      })
          .then((res) => {
            const data = res.data;
            ebooks.value = data.content.list;
          })
    }

    const isShowWelcome = ref(true);
    let categoryId2 = 0;

    const handleClick = (value: any) => {
      console.log("Menu Click")
      if (value.key === 'welcome') {
        isShowWelcome.value = true;
      } else {
        categoryId2 = value.key;
        isShowWelcome.value = false;
        handleQueryEbook();
      }
    }


    onMounted(() => {
      handleQueryCategory();

    })

    return {
      ebooks,
      actions,
      pagination,

      handleClick,
      level1,

      isShowWelcome,
    }

  }
});
</script>

<style scoped>
  .ant-avatar {
    width: 50px;
    height: 50px;
    line-height: 50px;
    border-radius: 8%;
    margin: 5px 0;
  }

</style>

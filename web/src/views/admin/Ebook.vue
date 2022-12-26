<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <div>
        电子书管理
      </div>
      <a-table
      :columns="columns"
      :data-source="ebooks"
      :pagination="pagination"
      @change="handleTableChange"
      >
        <template #bodyCell="{column, record}">
          <template v-if="column.key === 'cover'">
            <img :src=record.cover alt="cover">
          </template>

          <template v-else-if="column.key === 'Action'">
            <a-space size="small">
              <a-button type="primary" @click="edit">
                编辑
              </a-button>
              &nbsp;
              <a-button type="danger">
                删除
              </a-button>
            </a-space>
          </template>

        </template>





      </a-table>
    </a-layout-content>
  </a-layout>
  <a-modal
      v-model:visible="modalVisible"
      title="编辑电子书"
      :confirm-loading="modalLoading"
      @ok="handleOk"
  >
    <p>test</p>
  </a-modal>
</template>

<script lang="ts">
import { defineComponent, onMounted, ref } from 'vue';
import { StarOutlined, LikeOutlined, MessageOutlined } from '@ant-design/icons-vue';
import axios from 'axios';





export default defineComponent({
  name: 'Ebook',
  components: {
    StarOutlined,
    LikeOutlined,
    MessageOutlined,
  },
  setup() {
    const ebooks = ref();
    const params = ref();
    params.value = {};
    const pagination = ref({
      current:1,
      pageSize:2,
      total:0
    });
    const loading = ref(false);


    const columns = [
      {
        title:'封面',
        dataIndex:'cover',
        key: 'cover'
      },
      {
        title:'名称',
        dataIndex:'name',
        key: 'name'
      },
      {
        title:'分类1',
        dataIndex:'category1Id',
        key: 'category1'
      },
      {
        title:'分类2',
        dataIndex:'category2Id',
        key: 'category2'
      },
      {
        title:'文档数',
        dataIndex:'docCount',
        key: 'docCount'
      },
      {
        title:'阅读数',
        dataIndex:'viewCount',
        key: 'viewCount'
      },
      {
        title:'点赞数',
        dataIndex:'voteCount',
        key: 'voteCount'
      },
      {
        title:'操作',
        dataIndex:'Action',
        key: 'Action'
      },

    ]

    const handleQuery = (params: any) => {
      loading.value = true;
      axios.get('/ebook/list', {
          params: params
          })
          .then((res) => {
            loading.value = false;
            const data = res.data;
            ebooks.value = data.content.list;
            pagination.value.current = params.page;
            pagination.value.total = data.content.total;
          })
    };

    const handleTableChange = (pagination: any) => {
      handleQuery({
        page: pagination.current,
        size: pagination.pageSize
      })
    };

    const modalVisible = ref(false);
    const modalLoading = ref(false);

    const showModal = () => {
      modalVisible.value = true;
    }

    const handleOk = () => {
      modalLoading.value = true;
      setTimeout(() => {
        modalVisible.value = false;
        modalLoading.value = false;
      }, 2000)
    }

    const edit = () => {
      modalVisible.value = true;
    }



    onMounted(() => {
      handleQuery({
        page: pagination.value.current,
        size: pagination.value.pageSize
      });
    })

    return {
      ebooks,
      pagination,
      columns,
      loading,
      handleTableChange,
      handleOk,
      modalVisible,
      modalLoading,
      edit,

    }




  }
});
</script>

<style scoped>
  img {
    width: 50px;
    height: 50px;
  }

</style>

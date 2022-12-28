<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <div>
        <a-form
            layout="inline"
        >
          <a-form-item
          >
            <a-input v-model:value="param.name" placeholder="输入名称">
              <template #prefix>
                <search-outlined class="site-form-item-icon" />
              </template>
            </a-input>
          </a-form-item>

          <a-form-item>
            <a-button type="primary" @click="handleQuery({
          page:1,
          size:pagination.pageSize
          })">
              查询
            </a-button>
          </a-form-item>

          <a-form-item>
            <a-button type="primary" @click="add">
              新增
            </a-button>
          </a-form-item>

        </a-form>
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
              <router-link :to="'/doc?ebookId='+record.id">
              <a-button type="primary" @click="edit(record)">
                文档管理
              </a-button>
              </router-link>

              <a-button type="primary" @click="edit(record)">
                编辑
              </a-button>

              <a-popconfirm
                  title="Are you sure delete this record?"
                  ok-text="Yes"
                  cancel-text="No"
                  @confirm="handleDelete(record.id)"
              >
                <a-button type="danger" >
                  删除
                </a-button>
              </a-popconfirm>

            </a-space>
          </template>

          <template v-else-if="column.key === 'category'" >
              <span>{{getCategoryName(record.category1Id)}} / {{getCategoryName(record.category2Id)}}</span>
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
    <a-form
        :model="ebook"
        :label-col="{ span: 8 }"
        :wrapper-col="{ span: 16 }"
    >
      <a-form-item
          label="封面"
      >
        <a-input v-model:value="ebook.cover" />
      </a-form-item>

      <a-form-item
          label="名称"
      >
        <a-input v-model:value="ebook.name" />
      </a-form-item>

     <a-form-item label="分类">
       <a-cascader
           v-model:value="categoryIds"
           :options="level1"
           :field-names = "{label: 'name', value: 'id', children: 'children'}"
           placeholder="Please select" />

     </a-form-item>

      <a-form-item
          label="描述"
      >
        <a-textarea v-model:value="ebook.description"/>
      </a-form-item>


    </a-form>
  </a-modal>
</template>

<script lang="ts">
import { defineComponent, onMounted, ref } from 'vue';
import { SearchOutlined } from '@ant-design/icons-vue';
import { message } from "ant-design-vue";
import axios from 'axios';
import {Tool} from "@/utils/tool";





export default defineComponent({
  name: 'Ebook',
  components: {
    SearchOutlined,
  },
  setup() {
    const ebooks = ref();
    const param = ref();
    param.value = {};
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
        title:'分类',
        key: 'category'
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
      // 如果不清空现有数据 则编辑保存后重新加载数据后 再点编辑 则列表显示还是编辑前的数据
      ebook.value = [];
      axios.get('/ebook/list', {
          params: {
            page: params.page,
            size: params.size,
            name: param.value.name,
          }
          })
          .then((res) => {
            loading.value = false;
            const data = res.data;
            if (data.success) {
              ebooks.value = data.content.list;
              pagination.value.current = params.page;
              pagination.value.total = data.content.total;
            } else {
              message.error(data.message);
            }
          })
    };

    const handleTableChange = (pagination: any) => {
      handleQuery({
        page: pagination.current,
        size: pagination.pageSize
      })
    };

     //  -----表单-----
    const categoryIds = ref();
    const modalVisible = ref(false);
    const modalLoading = ref(false);
    const level1 = ref();
    let categorys:any;
    const ebook = ref();

    const edit = (record: any) => {
      modalVisible.value = true;
      ebook.value = Tool.copy(record);
      categoryIds.value = [ebook.value.category1Id, ebook.value.category2Id];
    }

    const add = () => {
      modalVisible.value = true;
      ebook.value = {};
    }

    const handleDelete = (id: number) => {
      axios.delete("/ebook/delete/" + id)
          .then((res) => {
            const data = res.data;
            if (data.success) {
              // 重新加载列表
              handleQuery({
                page: pagination.value.current,
                size: pagination.value.pageSize
              })
            }
          })
    }

    const handleOk = () => {
      modalLoading.value = true;
      ebook.value.category1Id = categoryIds.value[0];
      ebook.value.category2Id = categoryIds.value[1];
      axios.post("/ebook/save", ebook.value)
          .then((res) => {
            const data = res.data;
            if (data.success) {
              modalVisible.value = false;
              modalLoading.value = false;
              // 重新加载列表
              handleQuery({
                page: pagination.value.current,
                size: pagination.value.pageSize
              })
            } else {
              message.error(data.message);
            }


          })
    }

    const handleQueryCategory = () => {
      loading.value = true;
      axios.get('/category/all', {
      })
          .then((res) => {
            loading.value = false;
            const data = res.data;
            if (data.success) {
              categorys = data.content;
              level1.value = [];
              level1.value = Tool.array2Tree(categorys, 0);
            } else {
              message.error(data.message);
            }
          })
    };

    const getCategoryName = (cid:number) => {
      let result = "";
      categorys.forEach((item:any) => {
        if (item.id === cid) {
          result = item.name;
        }
      })
      return result;
    }



    onMounted(() => {
      handleQueryCategory();
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
      add,
      handleDelete,
      ebook,
      handleQuery,
      param,

      categoryIds,
      level1,
      getCategoryName

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

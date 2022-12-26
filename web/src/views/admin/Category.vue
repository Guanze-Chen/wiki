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
            <a-button type="primary" @click="handleQuery()">
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
          :data-source="level1"
          :pagination=false
      >
        <template #bodyCell="{column, record}">
          <template v-if="column.key === 'cover'">
            <img :src=record.cover alt="cover">
          </template>

          <template v-else-if="column.key === 'Action'">
            <a-space size="small">
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

        </template>

      </a-table>
    </a-layout-content>
  </a-layout>
  <a-modal
      v-model:visible="modalVisible"
      title="编辑分类"
      :confirm-loading="modalLoading"
      @ok="handleOk"
  >
    <a-form
        :model="category"
        :label-col="{ span: 8 }"
        :wrapper-col="{ span: 16 }"
    >
      <a-form-item
          label="一级分类"
      >
        <a-input v-model:value="category.parent" />
        <a-select
            ref="select"
            v-model:value="category.parent"
        >
          <a-select-option value="0">无</a-select-option>
          <a-select-option v-for="c in level1" :key="c.id" :value="c.id" :disabled="category.id === c.id">
              {{c.name}}
          </a-select-option>

        </a-select>

      </a-form-item>

      <a-form-item
          label="名称"
      >
        <a-input v-model:value="category.name" />
      </a-form-item>

      <a-form-item
          label="序号"
      >
        <a-input v-model:value="category.sort" />
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
  name: 'Category',
  components: {
    SearchOutlined,
  },
  setup() {
    const categorys = ref();
    const param = ref();
    param.value = {};
    const loading = ref(false);


    const columns = [
      {
        title:'一级分类',
        dataIndex:'parent',
        key: 'parent'
      },
      {
        title:'名称',
        dataIndex:'name',
        key: 'name'
      },
      {
        title:'序号',
        dataIndex:'sort',
        key: 'sort'
      },
      {
        title:'操作',
        dataIndex:'Action',
        key: 'Action'
      },

    ]

    /**
     * 一级分类树，children属性就是二级分类
     * [{
     *   id: "",
     *   name: "",
     *   children: [{
     *     id: "",
     *     name: "",
     *   }]
     * }]
     */
    const level1 = ref(); // 一级分类树，children属性就是二级分类

    const handleQuery = () => {
      loading.value = true;
      axios.get('/category/all', {
      })
          .then((res) => {
            loading.value = false;
            const data = res.data;
            if (data.success) {
              categorys.value = data.content;

              level1.value = [];
              level1.value = Tool.array2Tree(categorys.value, 0);
            } else {
              message.error(data.message);
            }
          })
    };


    const modalVisible = ref(false);
    const modalLoading = ref(false);



    const category = ref({});

    const edit = (record: any) => {
      modalVisible.value = true;
      category.value = Tool.copy(record);
    }

    const add = () => {
      modalVisible.value = true;
      category.value = {};
    }

    const handleDelete = (id: number) => {
      axios.delete("/category/delete/" + id)
          .then((res) => {
            const data = res.data;
            if (data.success) {
              // 重新加载列表
              handleQuery();
            }
          })
    }

    const handleOk = () => {
      modalLoading.value = true;
      axios.post("/category/save", category.value)
          .then((res) => {
            const data = res.data;
            if (data.success) {
              modalVisible.value = false;
              modalLoading.value = false;
              // 重新加载列表
              handleQuery()
            } else {
              message.error(data.message);
            }


          })
    }



    onMounted(() => {
      handleQuery();
    })

    return {
      //categorys,
      level1,
      columns,
      loading,
      handleOk,
      modalVisible,
      modalLoading,
      edit,
      add,
      handleDelete,
      category,
      handleQuery,
      param,

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

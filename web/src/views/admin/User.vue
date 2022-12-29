<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <div>
        <a-form
            layout="inline"
            :model="param"
        >
          <a-form-item
          >
            <a-input v-model:value="param.loginName" placeholder="登录名">
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
      :data-source="users"
      :pagination="pagination"
      @change="handleTableChange"
      :loading="loading"
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

              <a-button type="primary" @click="resetPwd(record)">
                重置密码
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
      title="编辑用户"
      :confirm-loading="modalLoading"
      @ok="handleOk"
  >
    <a-form
        :model="user"
        :label-col="{ span: 8 }"
        :wrapper-col="{ span: 16 }"
    >
      <a-form-item
          label="登录名"
      >
        <a-input v-model:value="user.loginName" :disabled="!!user.id" />
      </a-form-item>

      <a-form-item
          label="昵称"
      >
        <a-input v-model:value="user.name" />
      </a-form-item>

     <a-form-item label="密码" v-show="!user.id">
       <a-input-password v-model:value="user.password"/>

     </a-form-item>


    </a-form>
  </a-modal>

  <a-modal
      v-model:visible="resetModalVisible"
      title="重置密码"
      :confirm-loading="resetModalLoading"
      @ok="handleResetModalOk"
  >
    <a-form
        :model="user"
        :label-col="{ span: 8 }"
        :wrapper-col="{ span: 16 }"
    >

      <a-form-item label="新密码">
        <a-input-password v-model:value="user.password"/>

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

declare let hexMd5: any;
declare let KEY: any



export default defineComponent({
  name: 'User',
  components: {
    SearchOutlined,
  },
  setup() {
    const users = ref();
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
        title:'登录名',
        dataIndex:'loginName',
        key: 'loginName'
      },
      {
        title:'昵称',
        dataIndex:'name',
        key: 'name'
      },
      {
        title:'密码',
        dataIndex: 'password',
        key: 'password'
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
      user.value = [];
      axios.get('/user/list', {
          params: {
            page: params.page,
            size: params.size,
            loginName: param.value.loginName,
          }
          })
          .then((res) => {
            loading.value = false;
            const data = res.data;
            if (data.success) {
              users.value = data.content.list;
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
    const user = ref();
    const modalVisible = ref(false);
    const modalLoading = ref(false);

    const edit = (record: any) => {
      modalVisible.value = true;
      user.value = Tool.copy(record);
    }

    const add = () => {
      modalVisible.value = true;
      user.value = {};
    }

    const handleDelete = (id: number) => {
      axios.delete("/user/delete/" + id)
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
      user.value.password = hexMd5(user.value.password + KEY);
      axios.post("/user/save", user.value)
          .then((res) => {
            modalLoading.value = false;
            const data = res.data;
            if (data.success) {
              modalVisible.value = false;
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

    // 重置密码表单

    //  -----表单-----
    const resetModalVisible = ref(false);
    const resetModalLoading = ref(false);

    const resetPwd = (record: any) => {
      resetModalVisible.value = true;
      user.value = Tool.copy(record);
      user.value.password = null;
    }



    const handleResetModalOk = () => {
      resetModalVisible.value = true;
      user.value.password = hexMd5(user.value.password + KEY);
      axios.post("/user/resetpwd", user.value)
          .then((res) => {
            resetModalLoading.value = false;
            const data = res.data;
            if (data.success) {
              resetModalVisible.value = false;
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



    onMounted(() => {
      handleQuery({
        page: pagination.value.current,
        size: pagination.value.pageSize
      });
    })

    return {
      users,
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
      user,
      handleQuery,
      param,

      resetModalVisible,
      resetModalLoading,
      handleResetModalOk,
      resetPwd,



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

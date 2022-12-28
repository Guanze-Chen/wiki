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
      title="编辑文档"
      :confirm-loading="modalLoading"
      @ok="handleOk"
  >
    <a-form
        :model="doc"
        :label-col="{ span: 8 }"
        :wrapper-col="{ span: 16 }"
    >
      <a-form-item
          label="父文档"
      >
        <a-tree-select
            v-model:value="doc.parent"
            show-search
            style="width: 100%"
            :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
            placeholder="Please select"
            allow-clear
            tree-default-expand-all
            :tree-data="treeSelectData"
            :field-names="{
              title: 'name',
              label: 'name',
              value: 'id',
            }"
        >

        </a-tree-select>

      </a-form-item>

      <a-form-item
          label="名称"
      >
        <a-input v-model:value="doc.name" />
      </a-form-item>

      <a-form-item
          label="序号"
      >
        <a-input v-model:value="doc.sort" />
      </a-form-item>

    </a-form>
  </a-modal>
</template>

<script lang="ts">
import {createVNode, defineComponent, onMounted, ref} from 'vue';
import { SearchOutlined, ExclamationCircleOutlined } from '@ant-design/icons-vue';
import { message, Modal } from "ant-design-vue";
import axios from 'axios';
import {Tool} from "@/utils/tool";
import {useRoute} from "vue-router";





export default defineComponent({
  name: 'Doc',
  components: {
    SearchOutlined,
    ExclamationCircleOutlined,
  },
  setup() {
    const route = useRoute();
    console.log('路由', route);

    const docs = ref();
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
    let treeSelectData = ref();
    treeSelectData.value = [];

    const deleteIds: Array<string> = [];
    const deleteNames: Array<string> = [];




    const setDisable = (treeSelectData: any, id: any) => {
      // console.log(treeSelectData, id);
      // 遍历数组，即遍历某一层节点
      for (let i = 0; i < treeSelectData.length; i++) {
        const node = treeSelectData[i];
        if (node.id === id) {
          // 如果当前节点就是目标节点
          console.log("disabled", node);
          // 将目标节点设置为disabled
          node.disabled = true;

          // 遍历所有子节点，将所有子节点全部都加上disabled
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            for (let j = 0; j < children.length; j++) {
              setDisable(children, children[j].id)
            }
          }
        } else {
          // 如果当前节点不是目标节点，则到其子节点再找找看。
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            setDisable(children, id);
          }
        }
      }
    };

    // 查找整课数的id
    const getDeleteIds = (treeSelectData: any, id: any) => {
      // console.log(treeSelectData, id);
      // 遍历数组，即遍历某一层节点
      for (let i = 0; i < treeSelectData.length; i++) {
        const node = treeSelectData[i];
        if (node.id === id) {
          // 如果当前节点就是目标节点
          // 将目标节点添加进入ids
          deleteIds.push(id);
          deleteNames.push(node.name);

          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            for (let j = 0; j < children.length; j++) {
              getDeleteIds(children, children[j].id)
            }
          }
        } else {
          // 如果当前节点不是目标节点，则到其子节点再找找看。
          const children = node.children;
          if (Tool.isNotEmpty(children)) {
            getDeleteIds(children, id);
          }
        }
      }
    };

    const handleQuery = () => {
      loading.value = true;
      // 清空现有数据
      level1.value = [];
      axios.get('/doc/all', {
      })
          .then((res) => {
            loading.value = false;
            const data = res.data;
            if (data.success) {
              docs.value = data.content;

              level1.value = [];
              level1.value = Tool.array2Tree(docs.value, 0);
            } else {
              message.error(data.message);
            }
          })
    };


    const modalVisible = ref(false);
    const modalLoading = ref(false);



    const doc = ref({});

    const edit = (record: any) => {
      modalVisible.value = true;
      doc.value = Tool.copy(record);

      //不能选择当前节点及其所有子孙节点作为父节点
      treeSelectData.value = Tool.copy(level1.value);
      setDisable(treeSelectData.value, record.id);

      // 为选择数添加一个无
      treeSelectData.value.unshift({id:0, name: '无'});
    }

    const add = () => {
      modalVisible.value = true;
      doc.value = {
        ebookId: route.query.ebookId
      };

      treeSelectData.value = Tool.copy(level1.value);
      treeSelectData.value.unshift({id:0, name: '无'});
    }

    const handleDelete = (id: number) => {
      getDeleteIds(level1.value, id);

      Modal.confirm({
        title: '重要提醒',
        icon: createVNode(ExclamationCircleOutlined),
        content: '将删除：【' + deleteNames.join("，") + "】删除后不可恢复，确认删除？",
        onOk() {
          axios.delete("/doc/delete" + deleteIds.join(","))
              .then((res) => {
                const data = res.data;
                if (data.success) {
                  message.success("删除成功");
                  handleQuery();
                }
              })
        }

      })

    }

    const handleOk = () => {
      modalLoading.value = true;
      axios.post("/doc/save", doc.value)
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
      //docs,
      level1,
      columns,
      loading,
      handleOk,
      modalVisible,
      modalLoading,
      edit,
      add,
      handleDelete,
      doc,
      handleQuery,
      param,

      treeSelectData
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
